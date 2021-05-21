package com.ibm.au.optim.suro.core.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.control.RunController;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.notify.NotificationBus;
import com.ibm.au.optim.suro.model.entities.JobStatus;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Implementation of a run controller for the SURO application. It is using an in-memory queue to maintain the list of
 * waiting runs and has the ability to add and get runs from the configured repository.
 *
 * @author Peter Ilfrich
 */
public class SuroRunController extends AbstractSuroService implements RunController {

    /**
     * The repository to access the runs.
     */
    private RunRepository repository = null;

    /**
     * The queue of runs waiting to be executed.
     */
    private LinkedBlockingDeque<Run> runQueue = new LinkedBlockingDeque<>();

    /**
     * The logger for this run controller instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SuroRunController.class);

    /*
     * ##########################################################################################################
     *                                          Interface Methods
     * ##########################################################################################################
     */

    @Override
    public Run createRun(Run run) {
    	
        run.setStartTime(new Date().getTime());
        this.repository.addItem(run);

        return run;
    }

    @Override
    public Run getRun(String runId) {
        return this.repository.getItem(runId);
    }

    @Override
    public boolean addRun(Run run) {
        synchronized (this.runQueue) {
            if (this.runQueue.contains(run)) {
                return false;
            } else {
                this.runQueue.add(run);
                return true;
            }
        }
    }

    @Override
    public Run getNext() {
        synchronized (this.runQueue) {
            if (this.runQueue.isEmpty()) {
                // return null if there are no runs in the queue
                return null;
            }
            return this.runQueue.pollFirst();
        }
    }

    @Override
    public boolean abortRun(String runId) {
        if (runId == null) {
            // nothing to abort
            return false;
        }
        // check if the run is already processed and in a final state
        synchronized (this.repository) {
            Run run = getRun(runId);
            if (run == null) {
                return false;
            }
            else if ((run.getStatus() == RunStatus.COMPLETED) ||
                     (run.getStatus() == RunStatus.ABORTED) ||
                     (run.getStatus() == RunStatus.FAILED)) {
                return true;
            }
        }

        // find the run in the queue and remove it
        synchronized (this) {
            Run r = getRunFromQueue(runId);
            if (r != null) {
                // remove the run from the queue
                this.runQueue.remove(r);

                // update the run if necessary
                synchronized (this.repository) {
                    setRunStatus(getRun(runId), RunStatus.ABORTED);
                }
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean deleteRun(String runId) {
        synchronized (this.repository) {
            this.repository.removeItem(runId);
        }

        return true;
    }

    @Override
    public void resetRun(String runId) {
        synchronized (this.repository) {
            Run run = getRun(runId);
            run.setJobId(null);
            run.setJobStatus(null);
            updateRun(run);
            setRunStatus(run, RunStatus.QUEUED);
        }
    }


    @Override
    public void completeRun(String runId) {
        synchronized (this.repository) {
            Run run = this.getRun(runId);

            
            RunStatus status = run.getStatus();
            if ((status != RunStatus.FAILED) && (status != RunStatus.ABORTED) && (status != RunStatus.ABORTING)) {
            	
            	this.setRunStatus(run, RunStatus.COMPLETED);
            }
        }
    }

    @Override
    public void updateRun(Run run) {
        this.repository.updateItem(run);
    }

    @Override
    public void setRunStatus(Run run, RunStatus runStatus) {
        run.setStatus(runStatus);

        // [CV] NOTE: we do not need this, because it can be a computed property.
        //
        // set finished if one of the final states has been reached
        // if (runStatus.equals(RunStatus.ABORTED) || runStatus.equals(RunStatus.COMPLETED) || runStatus.equals(RunStatus.FAILED)) {
        //    run.setFinished(true);
        // }

        LOGGER.debug("[RunStatus] Setting run " + run.getId() + " to " + runStatus.toString());
        this.updateRun(run);

        try {
            String json = new ObjectMapper().writeValueAsString(run);
            NotificationBus.getInstance().broadcast("job:" + run.getId(), json);
        } catch (JsonProcessingException jpe) {
            LOGGER.error("Error serialising run", jpe);
        }
    }

    @Override
    public void setJobStatus(Run run, com.ibm.au.optim.suro.model.entities.JobStatus status) {
        run.setJobStatus(status);
        LOGGER.debug("[JobStatus] Setting run " + run.getId() + " to " + status.toString());
        this.updateRun(run);
    }

    @Override
    public List<Run> getQueue() {
        List<Run> result = new ArrayList<>();
        synchronized (this.runQueue) {
            for (Run run : this.runQueue) {
                result.add(run);
            }
        }

        return result;
    }

    @Override
    public Run getRunByJobId(String jobId) {
        if (jobId == null) {
            return null;
        }
        return repository.findByJobId(jobId);
    }

    @Override
    public void setRepository(RunRepository repository) {
        this.repository = repository;
    }


    /*
     * ##########################################################################################################
     *                                          Internal Methods
     * ##########################################################################################################
     */


    protected Run getRunFromQueue(String runId) {
        if (runId == null) {
            return null;
        }

        for (Run r : this.runQueue) {
            if (runId.equals(r.getId())) {
                return r;
            }
        }

        return null;
    }

    /**
     * Only executed once at system startup to load previously created runs. Runs that are just created can be
     * directly added to the queue. Runs that have already been started (handled by a job executor) should have a
     * job ID. They should be added to the queue as well, which requires the job executor to be able to resume an
     * already started run.
     */
    protected void resumeRuns() {
        // read runs from the repo
        synchronized (this.repository) {
            List<Run> runs = this.repository.getAll();
            for (Run run : runs) {
                // only add the ones to the queue that are just created
            	
            	RunStatus status = run.getStatus();
            	
            	if ((status != RunStatus.COMPLETED) && (status != RunStatus.FAILED) && 
            	    (status != RunStatus.INVALID) && (status != RunStatus.ABORTED)) {
            		
            		boolean added = this.addRun(run);
            		if (added == true) {
            			
            			this.setRunStatus(run, RunStatus.RESUME);
            		}
            		
            	} else if (status == RunStatus.ABORTING) {
            		
            		this.setRunStatus(run, RunStatus.ABORTED);
            		
            		if (run.getJobStatus() != null) {
            		
            			this.setJobStatus(run, JobStatus.INTERRUPT);
            		}
            	}
            }
        }
    }



    /*
     * ##########################################################################################################
     *                                          Runtime Service Methods
     * ##########################################################################################################
     */

    @Override
    protected void doRelease() {
        this.repository = null;
        this.runQueue = null;
    }

    @Override
    protected void doBind(Environment environment) {
        this.repository = (RunRepository) environment.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
        if (this.runQueue == null) {
            this.runQueue = new LinkedBlockingDeque<>();
        }

        // delay adding already created runs to the queue (on startup)
        SuroRunControllerResumeRunReminder reminder = new SuroRunControllerResumeRunReminder(environment, this);
        new Thread(reminder).start();
    }

    @Override
    public RunRepository getRepository() {
        return repository;
    }


    /*
     * ##########################################################################################################
     *                                                 Reminders
     * ##########################################################################################################
     */

    /**
     * Reminder used to delay adding already created - but not yet processed - runs to the queue on system startup.
     * The reminder will run in the background checking every couple of seconds, if the Core is ready. Once it is ready,
     * the runs will be resumed.
     */
    private static class SuroRunControllerResumeRunReminder implements Runnable {

        /**
         * The run controller which triggered the reminder.
         */
        private SuroRunController controller;
        /**
         * The environment used to locate the suro core
         */
        private Environment environment;
        /**
         * The boolean ot be used to determine whether the reminder is executed.
         */
        private boolean executed = false;

        /**
         * Creates a new reminder, which implements a method run(), which checks if the system is ready in a regular
         * interval. Once the Core is ready, the reminder triggers the resume method of the controller.
         *
         * @param env         - the environment used to retrieve the Core instance.
         * @param aController - the controller which triggered the reminder
         */
        public SuroRunControllerResumeRunReminder(Environment env, SuroRunController aController) {
            this.environment = env;
            this.controller = aController;
        }

        @Override
        public void run() {
            // as long as the reminder is not executed, repeat
            while (!executed) {
                try {
                    Thread.sleep(2000);
                    // read core implementation
                    Core core = (Core) this.environment.getAttribute(Core.CORE_INSTANCE);
                    if (core != null && core.isReady()) {
                        LOGGER.debug("Core is ready. Resuming runs.");
                        // resume runs
                        this.executed = true;
                        this.controller.resumeRuns();
                    }
                } catch (Exception e) {
                    LOGGER.warn("Exception thrown in the reminder thread for resuming jobs.", e);
                }
            }
        }
    }
}
