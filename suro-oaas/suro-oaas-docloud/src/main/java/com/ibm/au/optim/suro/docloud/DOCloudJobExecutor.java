package com.ibm.au.optim.suro.docloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.control.RunController;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.control.job.JobMonitor;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.JobStatus;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Parameter;
import com.ibm.au.optim.suro.model.entities.RunLogEntry;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.notify.NotificationBus;
import com.ibm.au.optim.suro.docloud.job.impl.DOCloudJobMonitor;
import com.ibm.au.optim.suro.docloud.job.impl.DOCloudResumeJobMonitor;
import com.ibm.au.optim.suro.docloud.util.DatParamGenerator;
import com.ibm.au.optim.suro.docloud.util.OpsGenerator;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.util.StringUtils;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.optim.oaas.client.OperationException;
import com.ibm.optim.oaas.client.job.*;
import com.ibm.optim.oaas.client.job.JobExecutor;
import com.ibm.optim.oaas.client.job.model.Job;
import com.ibm.optim.oaas.client.job.model.JobSolveStatus;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread representing the job executor which is responsible to process the task. Each executor runs in a loop and
 * checks for new runs in the queue. If a new run is available, the run is pulled and processed, resulting in a job
 * request object and a job monitor.
 *
 * @author Peter Ilfrich
 */
public class DOCloudJobExecutor implements Runnable, com.ibm.au.optim.suro.model.control.job.JobExecutor {

    // environment
    /**
     * The environment of the servlet
     */
    private Environment env = null;

    /**
     * The Core logic instance
     */
    private Core core = null;
    /**
     * The job executor represented by this executor instance
     */
    private JobExecutor executor = null;

    /**
     * A {@link NotificationBus} implementation that is used to push events on the
     * bus while reading the live log stream that is attached to the {@link Run}
     * instance configured with this {@link DOCloudJobMonitor}. The bus is also used
     * to push events related to the state change of the remote job associated to
     * the configured {@link Run} instance. These events are received through the
     * methods exposed by the {@link JobCallback} interface that this type implements.
     */
    protected static final NotificationBus NOTIFIER = NotificationBus.getInstance();

    /**
     * This is a {@link ObjectMapper} instance shared across all the instances of
     * the {@link DOCloudJobMonitor} type that is used to serialized into <i>JSON</i>
     * {@link Run} and {@link RunLogEntry} instances before sending
     * them along with the corresponding events on the notification bus.
     */
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * The job controller configured for the system
     */
    private JobController controller;

    // configuration value
    /**
     * The check interval for this executor (check every x milliseconds for new runs to process)
     */
    private long checkInterval;

    // operational parameters
    /**
     * Flag exposing the status of the executor (busy / not busy)
     */
    private boolean busy = false;
    /**
     * The current job request which is executed by this executor.
     */
    private JobRequest currentRequest;
    /**
     * The current monitor for this job execution
     */
    protected JobMonitor currentMonitor;
    /**
     * The ID of the currently executed job
     */
    private String currentJobId;
    /**
     * The ID of the currently active run
     */
    private String currentRunId;

    /**
     * The index of this executor.
     */
    private int index = 0;

    /**
     * Flag used to prevent execution of a job if it is aborted before it has been submitted to DOCloud.
     */
    private boolean preventExecution = false;

    /**
     * Flag that is used to stop the thread (by aborting the endless loop)
     */
    protected boolean active = true;

    /**
     * The Logger instance for this executor.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DOCloudJobExecutor.class);

    /**
     * Instantiates a new job executor.
     * @param environment - the Environment of the current servlet context
     * @param controller - the job controller from which this executor is instantiated and controlled
     * @param checkInterval - the check interval specified by the controller
     */
    public DOCloudJobExecutor(int executorIndex, Environment environment, JobController controller, long checkInterval) {
        
    	this.index = executorIndex;
        this.env = environment;
        this.core = (Core) this.env.getAttribute(Core.CORE_INSTANCE);
        this.controller = controller;
        this.checkInterval = checkInterval;
        this.executor = JobExecutorFactory.createDefault();
    }


    @Override
    public void run() {
        
    	this.debug("Creating new job executor (" + this.index + ") with check interval " + checkInterval);
        
    	try {
            // wait for job / run cleanup to finish
            Thread.sleep(10000);
            
        } catch (InterruptedException ie) {
        	
            this.error("Ramp up delay was interrupted for executor " + this.index + ".", ie);
        }
    	
        while (this.active == true) {
        	
            try {
            	
                if (!this.busy && this.core != null) {
                    
                	Run currentRun = core.getNextRun();
                    
                	// check if a new run exists
                    if (currentRun != null) {
                       
                    	// store current run/job ID
                        this.currentRunId = currentRun.getId();
                        
                        if (currentRun.getJobId() != null) {
                            this.currentJobId = currentRun.getJobId();
                        }

                        // set executor busy
                        this.debug("Job executor " + this.index + " found a new run. Switching to busy mode");
                        this.setBusy(true);

                        // figure out if we need to resume the run or create a new one
                        if ((RunStatus.RESUME == currentRun.getStatus()) && this.jobExistsInDOCloud()) {
                        	
                            this.resumePreviousJob();
                        
                        } else {
                        
                        	this.createNewJob();
                        }
                    }
                }

            } catch (Exception e) {
            	
               this.error("[Executor-" + this.index + "] Uncaught error occurred", e);
            }
            try {
                // wait for a few seconds to check again
                Thread.sleep(this.checkInterval);
                
            } catch (InterruptedException ie) {
            	
                this.error("[Executor-" + this.index + "] Interrupted Exception while waiting for the check interval.", ie);
            }
        }
        
        this.debug("Executor" + this.index + " terminated.");
    }

    @Override
    public boolean isBusy() {
        return this.busy;
    }

    @Override
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    @Override
    public Run getCurrentRun() {
    	
    	Run run = null;
    	
    	if (this.currentRunId != null) {
            
        	run = this.core.getRunController().getRun(this.currentRunId);
        }
    	
    	return run;
    }



    @Override
    public String getCurrentRunId() {
    	
        return this.currentRunId;
    }

    @Override
    public void setCurrentRunId(String runId) {
    	
        this.currentRunId = runId;
    }

    @Override
    public String getCurrentJobId() {
    	
        return this.currentJobId;
    }

    @Override
    public void setCurrentJobId(String currentJobId) {
    	
        this.currentJobId = currentJobId;
    }

    @Override
    public void finish() {
    	
        this.currentJobId = null;
        
        if (currentMonitor != null) {
        	
            this.currentMonitor.cancel();
        }
        this.currentMonitor = null;
        this.currentRequest = null;
        this.currentRunId = null;
        this.preventExecution = false;
        
        this.setBusy(false);
    }

    @Override
    public void quit() {

        this.active = false;
        
        if (this.currentMonitor != null) {
        	
            this.currentMonitor.cancel();
        }
    }

    @Override
    public boolean abort() {
    	
    	boolean aborted = true;
    	
        this.debug("Aborting currently executing job (" + this.currentJobId + ")");
        
        try {
            if (this.getCurrentJobId() == null) {
        
            	this.debug("The selected job hasn't been started yet. Preventing execution and stopping monitor.");
                this.preventExecution = true;

                if (this.currentMonitor != null) {
                    this.currentMonitor.cancel();
                }

                // cleanup current objects and set executor to not busy
                this.finish();
                
                // here it was: return true;
            
            } else {
            	
                this.debug("Job is currently running with job ID " + this.currentJobId + " - send abort signal.");
                
                // trigger abortion (may cause exceptions)
                DOCloudJobController docControl = (DOCloudJobController) this.controller;
                docControl.getJobClient().abortJob(this.currentJobId);
                
                // here it was: return true;
            }
            
        } catch (JobNotFoundException noJob) {
            
        	this.error("Trying to abort job " + this.currentJobId + " failed. The job could not be found in DOCloud", noJob);
        
        	// here we did: return true;
        	
        } catch (OperationException oe) {
            
        	this.error("The job " + this.currentJobId + " could not be aborted.", oe);
            aborted = false;
            
            // here it was: return false;
        }

        this.debug("Job abort successful");
   
        return aborted;
    }



    @Override
    public void runCompleted(String runId) {
    	
        RunController ctrl = this.getRunController();
        synchronized (ctrl.getRepository()) {
        	
            Run run = ctrl.getRun(runId);
            if (run.getStatus() == RunStatus.COLLECTING_RESULTS) {
            	
                ctrl.setRunStatus(run, RunStatus.COMPLETED);
                ctrl.setJobStatus(run, JobStatus.COMPLETED);
            }
        }
        this.notifyRunEvent(runId);
    }

    @Override
    public void runAborted(String runId) {
    	
        RunController ctrl = this.getRunController();
        
        synchronized (ctrl.getRepository()) {
        	
            Run run = ctrl.getRun(runId);
            if (run.getStatus() == RunStatus.ABORTING) {
            	
                ctrl.setRunStatus(run, RunStatus.ABORTED);
                ctrl.setJobStatus(run, JobStatus.INTERRUPT);
            }
        }
        this.notifyRunEvent(runId);
    }

    @Override
    public void runFailed(String runId) {
    	
        RunController ctrl = this.getRunController();
        
        synchronized (ctrl.getRepository()) {
        	
            Run run = ctrl.getRun(runId);
            
            RunStatus status = run.getStatus();
            
            if ((status != RunStatus.ABORTED) && (status != RunStatus.COMPLETED)) {
            	
                ctrl.setRunStatus(run, RunStatus.FAILED);
                ctrl.setJobStatus(run, JobStatus.FAILED);
            }
            
        }
        this.notifyRunEvent(runId);

    }

    @Override
    public void collectResults(Run run) {
    	
        Job job = this.getJob(run.getJobId());

        if (job != null)  {
            
        	this.collectSolutionLog();
            RunRepository repo = this.getRunController().getRepository();
            JsonNode result = this.getSolutionResult();

            if (result != null) {
                try {
                
                	this.controller.getResultManager().storeResults(run, result);
                
                } catch (IOException ioe) {
                    LOGGER.error("Error during result transformation or storage.", ioe);
                }


                Double bestGap = null;
                String systemProgressGap = job.getDetails().get("PROGRESS_GAP");
                if (!StringUtils.isNullOrEmpty(systemProgressGap)) {
                    try {
                        bestGap = Double.valueOf(systemProgressGap);
                   
                    } catch (NumberFormatException nfe) {
                    	
                        this.error("Gap could not be parsed into a double", nfe);
                    }
                }
                if (bestGap == null) {
                	
                    bestGap = result.get("finalGap").get("value").doubleValue();
                }

                synchronized (repo) {
                	
                    Run dbRun = this.getRunController().getRun(currentRunId);
                    
                    // [CV] NOTE: We do not need this anymore, we simply need to
                    //       	  add the information about the completion of the
                    //            run.
                    //
                    // dbRun.setHasSolution(true);
                    dbRun.setFinalGap(bestGap);
                    dbRun.setSolveStatus(job.getSolveStatus().toString());
                    repo.updateItem(dbRun);
                }
            } else {
                synchronized (repo) {
                    Run dbRun = repo.getItem(currentRunId);
                    dbRun.setSolveStatus(JobSolveStatus.UNKNOWN.toString());
                    repo.updateItem(dbRun);
                }
            }

        }
    }

    @Override
    public void notifyOptimEvent(String runId, RunLogEntry entry) {
    	
        try {
        	
            String json = MAPPER.writeValueAsString(entry);
            NOTIFIER.broadcast("optim:log:" + runId, json);

        } catch (JsonProcessingException je) {

            this.error("Error serializing optimization data for notification", je);
        }
    }

    @Override
    public void notifyRunEvent(String runId) {
    	
        try {
        
        	String json = MAPPER.writeValueAsString(getRunController().getRun(runId));
            NOTIFIER.broadcast("job:" + runId, json);
        
        } catch (JsonProcessingException je) {
            this.error("Error serializing run for notification", je);
        }
    }

    /*
     * ==================================================================================================
     *                                          PROTECTED METHODS
     * ==================================================================================================
     */

    /**
     * Attach the solution log file to the
     */
    protected void collectSolutionLog() {


        InputStream is = null;
        File tempFile = null;
        try {
            tempFile = File.createTempFile("log_" + currentJobId, "txt");

            this.getJobClient().downloadLog(currentJobId, tempFile);

            is = new FileInputStream(tempFile);
            RunController runController = getRunController();
            
            synchronized (runController.getRepository()) {
            
            	Run run = runController.getRun(currentRunId);
                runController.getRepository().attach(run, Constants.DOCLOUD_LOG_FILE, Constants.DOCLOUD_LOG_MIME, is);
            }


        } catch (JobNotFoundException jnf) {
        	
            this.error("Error getting log file from DOCloud - job does not exist", jnf);

        } catch (OperationException oe) {
        	
            this.error("Error getting log file from DOCloud", oe);

        } catch (IOException ioe) {
        	
            this.error("Error reading solution log", ioe);
        
        } finally {
        	
            IOUtils.closeQuietly(is);

            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }


    /**
     * Downloads the solution data from DOCloud, attaches to the run document,
     * and return deserialized JSON node for post-processing.
     *
     *
     * @return 	a {@link JsonNode} instance that contains the <i>JSON</i> object
     * 			tree defining the document containing the solution results.
     */
    protected JsonNode getSolutionResult() {

    	
    	JsonNode node = null;
    	
    	String name = Constants.DOCLOUD_SOLUTION_FILE;

        File tempFile = null;
        InputStream is1 = null, is2 = null;
        try {
        	
            tempFile = File.createTempFile("solution_" + currentJobId, "json");
            this.getJobClient().downloadJobAttachment(currentJobId, name, tempFile);
            RunController runController = getRunController();

            // Attach solution file to Run DB entry
            is1 = new FileInputStream(tempFile);
            synchronized (runController.getRepository()) {
            	
                runController.getRepository().attach(runController.getRun(currentRunId), name, Constants.DOCLOUD_SOLUTION_MIME, is1);
            }

            is2 = new FileInputStream(tempFile);

            node = new ObjectMapper().readTree(is2);

        } catch (AttachmentNotFoundException anf) {
        
        	this.debug("Attachment could not be fetched from DOCloud - attachment " + anf.getAttachmentId() + " does not exist for job " + currentJobId, anf);
        
        } catch (JobNotFoundException jnfe) {
            
        	this.error("Error getting attachment from DOCloud - job does not exist", jnfe);
        
        } catch (OperationException oe) {
        
        	this.error("Error getting attachment from DOCloud", oe);
        
        } catch (IOException ioe) {
        
        	this.error("Error reading solution attachment", ioe);
        
        } finally {
        	
            IOUtils.closeQuietly(is1);
            IOUtils.closeQuietly(is2);
            if (tempFile != null) {
                tempFile.delete();
            }
        }

        return node;
    }


    /**
     * Returns the instance of the job client connected with the current DOCloud account
     * @return
     */
    protected JobClient getJobClient() {
    	
        DOCloudJobController docControl = (DOCloudJobController) this.controller;
        return docControl.getJobClient();
    }

    /**
     * Retrieves the currently executed job from DOCloud
     * @param jobId - the ID of the remote job in DOCloud
     * @return - a job object or null.
     */
    protected Job getJob(String jobId) {
        
    	Job job = null;
    	
    	if (jobId != null) {
           

	        try {
	        	
	            job =  this.getJobClient().getJob(jobId);
	       
	        } catch (JobNotFoundException jnfe) {
	        	
	            LOGGER.info("Requested job could not be found.", jnfe);
	            
	        } catch (OperationException oe) {
	        	
	            this.error("Error retrieving job from DOCloud", oe);
	        }
    	}
    	
    	return job;
    }

    /**
     * Checks if the job ID held by the current job executor can be found in DOCloud.
     * @return
     */
    protected boolean jobExistsInDOCloud() {
    	
    	boolean doesExist = this.currentJobId != null;
    	
        // no job id set -> false
        if (doesExist) {
       
	        Job job = this.getJob(currentJobId);
	        doesExist = (job != null);
        }
        return doesExist;
    }

    /**
     * Creates a new job in DOCloud with the currently provided run data.
     */
    protected void createNewJob() {
        // create live log stream (used for monitor)
        try {
        	
            PipedInputStream jobLiveLogInStream = new PipedInputStream(2000);
            PipedOutputStream jobLiveLogOutStream = new PipedOutputStream(jobLiveLogInStream);
            Reader jobLiveLogReader = new InputStreamReader(jobLiveLogInStream, StandardCharsets.UTF_8);

            Run currentRun = this.getCurrentRun();


            // create job request
            currentRequest = this.createJobRequest(currentRun,
                    							   this.getModelController().getModel(currentRun.getModelId(), true),
                                                   this.getDataSetController().getDataSet(currentRun.getDataSetId(), true),
                                                   jobLiveLogOutStream);


            // create job monitor
            this.debug("Creating new job monitor for run: " + currentRunId);
            DOCloudJobController jobController = (DOCloudJobController) this.controller;
            currentMonitor = new DOCloudJobMonitor(this, jobController.getJobClient(), currentRunId, jobLiveLogReader, this.env);

            // trigger the execution
            this.triggerExecution(currentRun);

        } catch (IOException ioe) {
        	
            this.error("IO Exception with piped streams used for the Job Monitor", ioe);
        }
    }

    /**
     * Triggers the execution of the specified run. The method assumes that currentRequest and currentMonitor are
     * prepared.
     * @param currentRun - the current run for which to trigger the executor.
     */
    protected void triggerExecution(Run currentRun) {
    	
        try {
            // execute the job request
            this.debug("Executing job request for run: " + currentRunId);
            
            if (this.preventExecution == false) {
                // execute the job
                this.executor.execute(currentRequest, (JobCallback) currentMonitor);
            }
        } catch (JobLimitException je) {
            
        	this.error("Too many concurrent jobs for the current API user.", je);
            this.core.abortRun(currentRun.getId());
        
        } catch (IOException ioe) {
        
        	this.error("IO Exception while trying to start new job", ioe);
        
        } catch (OperationException oe) {
            
        	this.error("Operation Exception while trying to start new job", oe);
        
        } catch (JobException je) {
            
        	this.error("Job Exception while trying to start new job", je);
        
        } catch (InterruptedException ire) {
            
        	this.error("Interrupted exception from job executor", ire);
        }
    }


    /**
     * Creates a new resume monitor to resume a job.
     */
    protected void resumePreviousJob() {
    	
        this.debug("Resuming run " + currentRunId + " with Job ID " + currentJobId);

        // get job controller
        DOCloudJobController jobController = (DOCloudJobController) this.controller;

        // create job monitor for resume job
        this.debug("Creating (RESUME) job monitor for run: " + currentRunId + " / job: " + currentJobId);
        this.currentMonitor = new DOCloudResumeJobMonitor(this.env, this, currentRunId, jobController.getJobClient());
    }

    /**
     * Creates a new job request for DOCloud.
     *
     * @param run - the run which is associated to this job
     * @param model - the model used for this job
     * @param dataSet - the data set used for this job
     * @param jobLiveLogOutStream - the stream used to send updates from DOCloud back to the server
     * @return
     */
    protected JobRequest createJobRequest(Run run, Model model, DataSet dataSet, PipedOutputStream jobLiveLogOutStream) {
        
    	JobRequest request = null;
    	
    	try {
            // cast controller
            DOCloudJobController jobController = (DOCloudJobController) this.controller;
            
            // Create DOCloud job request
            //
            //
            InputStream modelData = this.getStream(model);
            
            if (modelData != null) {


                // [CV] NOTE: this is the refactored code for the new model. We simply add
                //            the parameters by iterating from the list of parameters that
                //			  have been added to the Run instance. At this point validation
                //			  and parameter completion has been executed already therefore
                //			  we have all the set of parameters we need.
                //
                List<Parameter> parameters = run.getParameters();
                Map<String, String[]> params = new HashMap<>();
                if (parameters != null) {
                    for(Parameter p : parameters) {
                        Object value = p.getValue();
                        params.put(p.getName(), new String[] { value == null ? null : value.toString() });
                    }
                }

                // prepare input stream for dat file
                //
                InputStream datInputStream = new SequenceInputStream(dataSet.getDatFile(), new DatParamGenerator(params).generate());
                
            	
            
	            request = jobController.getJobClient().newRequest()
	                    			   .parameter("suro.template", run.getTemplateId())
	                    			   .input("model.mod", modelData)
	                    			   .input("model.ops", new OpsGenerator(run).generate())
	                    			   .input("model.dat", datInputStream)
	                    			   .livelog(jobLiveLogOutStream, Constants.DOCLOUD_DATE_FORMATTER)
	                    			   .deleteOnCompletion(true).build();
            
            } else {
            	
            	this.error("Could not find model file: " + Constants.DOCLOUD_MODEL_FILE + " for model (id: " + model.getId() +").", null);
            }
            
        } catch (Exception e) {
        	
            this.error("Error creating job request.", e);
        }
    	
    	return request;
    }

    /**
     * This method retrieves an input stream that can be used to read the content of the
     * optimisation model to execute. The method first check whether the model has an
     * attachment associated whose name is {@link Constants#DOCLOUD_MODEL_FILE}. It then
     * retrieves the corresponding {@link Attachment} instance and accesses its stream. 
     * If the stream is {@literal null} it queries the repository to retrieve the attachment.
     * 
     * @param model	a {@link Model} instance representing the abstraction that encapsulates
     * 				the optimisation model to execute and provides access to the optimisation
     * 				model script to execute.
     * 
     * @return	an {@link InputStream} implementation that can be used to access the binary
     * 			content of the optimisation model to execute, or {@literal null} if not found.
     */
    private InputStream getStream(Model model) {

		InputStream modelStream  = null;
		
    	Attachment script = model.getAttachment(Constants.DOCLOUD_MODEL_FILE);
    	
    	if (script != null) {
    		
    		modelStream = script.getStream();
    		
    		if (modelStream != null) {
    			
    	    	ModelRepository modelRepo = this.getModelController().getRepository();
    	    	modelStream = modelRepo.getAttachment(model.getId(), Constants.DOCLOUD_MODEL_FILE);
    		}
    	}
        
    	return modelStream;
	}


	/**
     * Returns the controller for the optimisation models
     * @return
     */
    protected ModelController getModelController() {
    	
    	return (ModelController) this.env.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
    }

    /**
     * Returns the controller for the data sets
     * @return
     */
    protected DataSetController getDataSetController() {
    	
    	return (DataSetController) this.env.getAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE);
    }

    protected RunController getRunController() {
    	
        return (RunController) this.env.getAttribute(RunController.RUN_CONTROLLER_INSTANCE);
    }
    


    private void debug(String message) {
    	
        LOGGER.debug("[Executor " + this.index + "]: " + message);
    }

    private void debug(String message, Throwable t) {
    	
        LOGGER.debug("[Executor " + this.index + "]: " + message, t);
    }

    private void error(String message, Throwable t) {
    	
        LOGGER.error("[Executor " + this.index + "]: " + message, t);
    }
}
