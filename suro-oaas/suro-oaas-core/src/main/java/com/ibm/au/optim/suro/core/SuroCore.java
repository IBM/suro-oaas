package com.ibm.au.optim.suro.core;

import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.control.*;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;


/**
 * Class <b>SuroCore</b>. This class implements logic of the {@link Core} interface.
 * The implementation uses 3 job executes to pull items from a queue managed by the 
 * {@link RunController} implementation.
 *
 * @author Peter Ilfrich
 */
public class SuroCore extends AbstractSuroService implements Core {


    /**
     * The current environment (config)
     */
    private Environment environment = null;


    /**
     * Flag that determines if the application is ready to operate.
     */
    private boolean ready = false;

    /**
     * The logger used for this core instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SuroCore.class);

    /**
     * For SURO the minimum database version is 0.0.3, when data migration was introduced.
     */
    public static final String DATABASE_MIN_VERSION = "0.0.3";

    /**
     * Completed run queue, used when runs are set to completed while the core is not ready.
     */
    private LinkedBlockingDeque<Run> completedRunQueue = new LinkedBlockingDeque<>();

    /**
     * This method provides the information about the current version of the database.
     * This information is for instance useful to distinguish between the different
     * instances of the data model. The information is stored within the preference
     * managed under the name {@link Core#PREFERENCE_DB_VERSION_NAME}.
     * 
     * @return  a {@link String} containing the version, if no version is found, then the
     * 			value of {@link SuroCore#DATABASE_MIN_VERSION} will be returned.
     */
    @Override
    public String getCurrentDatabaseVersion() {
        try {
            String version = this.getPreferenceManager().getSystemPreference(PREFERENCE_DB_VERSION_NAME);
            return version != null ? version : DATABASE_MIN_VERSION;
        }
        catch (Exception e) {
            LOGGER.error("Error when retrieving the database version from the preference manager.", e);
            return DATABASE_MIN_VERSION;
        }

    }

    /**
     * <p>
     * Sets the current database version. This information is used to discriminate the
     * different data model that is currently running and it is of primary user for the
     * {@link DatabasePreparer} implementation when migrating from a version to another.
     * </p>
     * <p>
     * This method stores the information by leveraging the services of the preference 
     * manager and persists the version information under the property named {@link 
     * Core#PREFERENCE_DB_VERSION_NAME}.
     * </p>
     * 
     * 
     * @param newVersion	a {@link String} that contains the new version of the database
     * 						to set. It can be {@literal null}.
     */
    @Override
    public void setDatabaseVersion(String newVersion) {
        this.getPreferenceManager().setSystemPreference(PREFERENCE_DB_VERSION_NAME, newVersion);
    }

    /**
     * This method submits a request for {@link Run} execution. 
     */
    @Override
    public Run submitRun(Run run) {
    	
    	
    	Run submitted = null;
    	
    	String modelId = run.getModelId();
    	String templateId = run.getTemplateId();
    	String dataSetId = run.getDataSetId();
    	
        this.debug("Submitting a new run: modelId=" + modelId + " | dataSetId=" + dataSetId + " | templateId=" + templateId);
       
        if (this.isReady()) {
           

            // create run object and add it to the queue
            RunController controller = this.getRunController();
            
            synchronized (controller.getRepository()) {
            	
                submitted = controller.createRun(run);

                // add to queue
                if (controller.addRun(submitted)) {
                	
                    this.debug("Run " + submitted.getId() + " added to the queue");
                    controller.setRunStatus(submitted, RunStatus.QUEUED);
                
                } else {
                	
                    // highly unlikely, the run was already added.
                    LOGGER.error("Run " + run.getId() + " is already in the queue and was NOT added again.");

                }
            }
            
        } else {
        	
            debug("submitRun: System not ready");
        }
        
        return submitted;
    }

    /**
     * <p>
     * This method is invoked to notify the core that the execution of a {@link Run}
     * instance has completed on the optimisation back-end and the results have come
     * back.
     * </p>
     * <p>
     * If the instance is active and ready it will invoke the configured implementation
     * of {@link JobController} to finalise the corresponding {@link Job} and then the 
     * configured {@link RunController} implementation to persist the new status of the 
     * {@link Run} instance mapped by <i>runId</i>. If the core is not ready, it will 
     * then add the run instance identified by the given id to the queue of completed 
     * runs so that it can be picked up later once the core is set to ready.
     * </p>
     *  
     * @param runId		a {@link String} representing the unique identifier of the {@link 
     * 					Run} that has been completed.
     */
    @Override
    public void completeRun(String runId) {
    	
        if (this.isReady()) {
        	
            this.debug("Setting run " + runId + " to completed");
            this.getJobController().completeJob(runId);
            this.getRunController().completeRun(runId);
        
        } else {
            // add the run to the queue, which will be processed when the system is set to ready
            synchronized (this.completedRunQueue) {
            	
                this.completedRunQueue.add(this.getRunController().getRun(runId));
                this.debug("completeRun: System not ready, adding item to the completed queue. Size: " + this.completedRunQueue.size());
            }

        }
    }
    
    /**
     * <p>
     * This method instructs the core to terminate the {@link Run} instance that is
     * identifier by the given <i>runId</i>. 
     * </p>
     * <p>
     * The method will perform an action if and only if the core is ready. In that case 
     * it will retrieve the configured implementation of {@link RunController} and check 
     * the status of the {@link Run} in the repository. If the status of the run is not 
     * terminal, it will be set to {@link RunStatus#ABORTING} and the {@link RunController} 
     * implementation will be instructed to abort the run. This process is successful if
     * and only if the execution has not started yet, otherwise the core will try to abort
     * the corresponding {@link Job} associated to the run, via the {@link JobController}
     * and then finalise the status of the run to {@link RunStatus#ABORTED}.
     * </p>
     * 
     * @param runId		a {@link String} containing the unique identifier of the {@link Run}
     * 					instance to abort. 
     * 
     * @return	{@link false} if <i>runId</i> is {@literal null} or the core is not ready.
     * 			Other cases when the method retuns {@literal false} is when there is no 
     * 			corresponding instance identified by the given <i>runId</i>. The value of
     * 			{@literal true} is returned in the other cases and identifies that the run
     * 			has been aborted.
     * 
     */
    @Override
    public boolean abortRun(String runId) {
    	
        if (runId == null) {
            return false;
        }

        if (this.isReady()) {
        	
            this.debug("Aborting run " + runId);
            // retrieve run controller
            RunController controller = this.getRunController();

            // check run
            synchronized (controller.getRepository()) {
            	
                Run run = controller.getRun(runId);
                if (run != null) {
                	
                	RunStatus status = run.getStatus();
                	if ((status != RunStatus.FAILED) &&
                		(status != RunStatus.COMPLETED) &&
                		(status != RunStatus.ABORTED)) {
                    	
                        controller.setRunStatus(run, RunStatus.ABORTING);
                        
                    } else {
                    	
                        return true;
                    }
                } else {
                	
                    return false;
                }
            }
            
            boolean runAborted = controller.abortRun(runId);

            // try to remove run from the queue
            if (runAborted == false) {
            	
                this.debug("Run " + runId + " has already been started. Attempting to abort job.");
                // access job controller and abort the job
                boolean result = this.getJobController().abortJob(runId);
                
                if (result == false) {
                	
                    this.getJobController().cleanupJobs();
                    result = true;
                }

                synchronized (controller.getRepository()) {
                
                	controller.setRunStatus(controller.getRun(runId), RunStatus.ABORTED);
                }
                
                return result;
                
            } else {
            	
                // run is aborted
                synchronized (controller.getRepository()) {
                    
                	controller.setRunStatus(controller.getRun(runId), RunStatus.ABORTED);
                }
                
                return true;
            }
            
        } else {
        	
            this.debug("abortRun: System not ready");
            return false;
        }
    }

    /**
     * <p>
     * This method retrieves the {@link Run} instance that needs to be submitted for execution
     * on the optimisation back-end.
     * </p>
     * <p>
     * The method first checks that the system is ready, and if so the {@link RunController}
     * implementation is invoked to retrieved the next {@link Run} instance and its details
     * are synchronised with the repository to avoid conflicts and operate on the most up to
     * date version of the instance.
     * </p>
     * 
     * @return 	a {@link Run} instance representing the next run request that needs to be
     * 			processed, or {@literal null} if no run is available for submission. The
     * 			method also returns {@literal null} if the core is not ready.
     * 
     */
    @Override
    public Run getNextRun() {
    	
        /* It is not recommended to add debug output here, as this method is executed too often */
    	
        if (this.isReady()) {
        	
            RunController controller = this.getRunController();

            // returns either the first run in the queue or null
            Run nextRun = controller.getNext();
            if (nextRun != null) {
            	
                // sync the run repository to avoid conflicts
                synchronized (controller.getRepository()) {
                	
                   this.debug("getNextRun: providing run " + nextRun.getId() + " to the job executor.");
                   
                    // reload the run from the database (to avoid conflicts) and update
                    nextRun = controller.getRun(nextRun.getId());
                }
            }

            return nextRun;
            
        } else {
        	
            this.debug("getNextRun: System not ready");
            return null;
        }
    }
    
    /**
     * This method deletes a {@link Run} instance from the system. This operations entails
     * different tasks according to the status of the run mapped by <i>runId</i>. If the
     * run is terminated, it will be only removed from the repository, if it is still active
     * the core will try to abort its execution.
     * 
     * @param runId		a {@link String} representing the unique identifier of the {@link Run}
     * 					instance to terminate.
     * 
     * @return 	a {@literal boolean} value indicating whether the operation has been successful.
     * 			In particular the value {@literal true} will be returned if the core is ready
     * 			and the operation completes successfully. If the run does not exist or the core
     * 			is not ready {@literal false} will be returned.
     */
    @Override
    public boolean deleteRun(String runId) {
    	
        if (this.isReady()) {
        	
            this.debug("Removing run " + runId + " from queue.");

            boolean aborted = this.abortRun(runId);
            if (aborted) {
            	
                this.debug("Deleting run " + runId + " from repository");
                this.getRunController().getRepository().removeItem(runId);
            }

            return aborted;
            
        } else {
        	
            this.debug("deleteRun: System not ready");
            return false;
        }
    }

    /**
     * <p>
     * This method restart the execution of the {@link Run} instance that is mapped
     * by the given <i>runId</i>. If ready, the core will retrieve the configured
     * implementation of {@link JobController} and terminate the current execution 
     * of the {@link Job} mapped to the run (if any) and resubmit the request for 
     * executing the optimisation.
     * </p>
     * <p>
     * This method is of use in an edge case where the request has already been submitted
     * to the optimisation back-end but the connection suddenly terminates for unexpected
     * reasons (such as the server on the client side fails). When the connection is active
     * again, the status of the job on the optimisation back-end will be "created" but it
     * might not be "running". To address this issue we essentially restart the execution
     * by removing that stale request and creating a new one.
     * </p>
     * 
     * @param runId		a {@link String} containing the unique identifier of the {@link Run}
     * 					instance to restart.
     */
    @Override
    public void restartRun(String runId) {
    	
        if (this.isReady()) {
        	
        	RunController controller = this.getRunController();
        	
        	boolean aborted = this.getJobController().abortJob(runId);
        	
            if (aborted == false) {
            	
                Run run = controller.getRun(runId);
                LOGGER.error("Failed to abort job for run " + runId + " / job: " + run.getJobId());
                return;
            }

            controller.resetRun(runId);
            controller.addRun(controller.getRun(runId));
            
        } else {
            debug("resta"
            		+ "rtRun: Sysem not ready");
            return;
        }
    }

    /**
     * Indicates whether the core is ready to accept and manage {@link Run}
     * requests. Before the core is ready it is not possible to perform any
     * action, such as submitting or stopping a run.
     * 
     * @return {@literal true} if the core is ready, {@literal false} otherwise.
     */
    @Override
    public boolean isReady() {
    	
        return this.ready;
    }

    /**
     * <p>
     * This method is used to enable/disable the services of the core. The {@link Core}
     * will only process requests and respond to events if it the flag passed as the
     * argument is set to {@literal true}. 
     * </p>
     * <p>
     * This method has side effects. As soon as the core is set to ready, it will start
     * polling the internal queues to check whether there are completed runs to process.
     * </p>
     * 
     * @param rdy	a {@literal boolean} indicating whether the {@link Core} is ready
     * 				({@literal true}) or not ({@literal false}).
     */
    @Override
    public void setReady(boolean rdy) {
        this.ready = rdy;

        if (rdy) {
        	
            this.debug("System ready to receive and process requests.");
            
            // system is set to ready, process queued items
            synchronized (this.completedRunQueue) {
            	
                while (!this.completedRunQueue.isEmpty()) {
                	
                    // set runs to complete
                    Run run = this.completedRunQueue.pollFirst();
                    this.debug("Processing queued runs that are completed. Resuming process to complete the run...");
                    this.completeRun(run.getId());
                }
            }
        }
    }

    /**
     * This method disengages the core from the {@link Environment} implementation
     * it was connected to and disables the services that are offered by the core
     * to the other components.
     */
    @Override
    protected void doRelease() {
    	
        // release the environment
        this.setReady(false);
        this.environment = null;
    }
    
    /**
     * This method connects to the {@link Environment}. The core initialises and
     * retrieves all the components that are required for its functions from the
     * given instance of {@link Environment}.
     * 
     * @param environment	an {@link Environment} implementation that is used
     * 						to access configuration parameters for the application
     * 						and shared objects.
     */
    @Override
    protected void doBind(Environment environment) {
        this.environment = environment;
    }


    /**
     * Utility method to retrieve the {@link ModelController} implementation configured
     * within the application. The current implementation looks up the {@link Environment}
     * implementation configured with the core and retrieves the instance that is mapped
     * to the property {@link ModelController#MODEL_CONTROLLER_INSTANCE}.
     * 
     * @return 	a {@link ModelController} implementation, or {@literal null} if there is no
     * 			component configured for this interface.
     */
    @Override
    public ModelController getModelController() {
        return (ModelController) this.environment.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
    }

    /**
     * Utility method to retrieve the {@link RunController} implementation configured
     * within the application. The current implementation looks up the {@link Environment}
     * implementation configured with the core and retrieves the instance that is mapped
     * to the property {@link RunController#RUN_CONTROLLER_INSTANCE}.
     * 
     * @return 	a {@link RunController} implementation, or {@literal null} if there is no
     * 			component configured for this interface.
     */
    @Override
    public RunController getRunController() {
        return (RunController) this.environment.getAttribute(RunController.RUN_CONTROLLER_INSTANCE);
    }

    /**
     * Utility method to retrieve the {@link DataSetController} implementation configured
     * within the application. The current implementation looks up the {@link Environment}
     * implementation configured with the core and retrieves the instance that is mapped
     * to the property {@link DataSetController#DATA_SET_CONTROLLER_INSTANCE}.
     * 
     * @return 	a {@link DataSetController} implementation, or {@literal null} if there is no
     * 			component configured for this interface.
     */
    @Override
    public DataSetController getDataSetController() {
        return (DataSetController) this.environment.getAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE);
    }
    
    /**
     * Utility method to retrieve the {@link JobController} implementation configured
     * within the application. The current implementation looks up the {@link Environment}
     * implementation configured with the core and retrieves the instance that is mapped
     * to the property {@link JobController#JOB_CONTROLLER_INSTANCE}.
     * 
     * @return 	a {@link JobController} implementation, or {@literal null} if there is no
     * 			component configured for this interface.
     */
    @Override
    public JobController getJobController() {
        return (JobController) this.environment.getAttribute(JobController.JOB_CONTROLLER_INSTANCE);
    }

    /**
     * Utility method to retrieve the {@link PreferenceManager} implementation configured
     * within the application. The current implementation looks up the {@link Environment}
     * implementation configured with the core and retrieves the instance that is mapped
     * to the property {@link PreferenceManager#PREFERENCE_MANAGER_INSTANCE}.
     * 
     * @return 	a {@link PreferenceManager} implementation, or {@literal null} if there is no
     * 			component configured for this interface.
     */
    @Override
    public PreferenceManager getPreferenceManager() {
        return (PreferenceManager) this.environment.getAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE);
    }

    /**
     * Log a preformatted message into the debug log, with the prefix [CORE] to quickly identify the component, which
     * logged the message.
     * @param message - the message to log
     */
    private void debug(String message) {
        SuroCore.LOGGER.debug("[CORE] " + message);
    }
}
