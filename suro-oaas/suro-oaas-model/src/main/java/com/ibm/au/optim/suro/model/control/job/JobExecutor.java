package com.ibm.au.optim.suro.model.control.job;

import com.ibm.au.optim.suro.model.entities.RunLogEntry;
import com.ibm.au.optim.suro.model.entities.Run;

/**
 * Interface for any job executor. The executor needs to implement Runnable as well and the run() method determines
 * the behaviour of the executor. The finish method will be executed once the optimisation terminates (regardless of
 * the outcome or state).
 *
 * @author Peter Ilfrich
 */
public interface JobExecutor {

    /**
     * Getter for the currently active run
     * @return
     */
    Run getCurrentRun();

    /**
     * Retrieves the ID of the current run
     * @return
     */
    String getCurrentRunId();

    /**
     * Setter for the current run ID
     * @param runId - the ID of the run that is executed by the executor
     */
    void setCurrentRunId(String runId);

    /**
     * Getter for the current job ID
     * @return
     */
    String getCurrentJobId();


    /**
     * Setter for the current job ID
     * @param jobId - the job currently processed by this executor instance.
     */
    void setCurrentJobId(String jobId);

    /**
     * Setter for the busy flag
     * @param busy
     */
    void setBusy(boolean busy);

    /**
     * Getter for the busy flag
     * @return
     */
    boolean isBusy();


    /**
     * Aborts the current job execution.
     * @return
     */
    boolean abort();


    /**
     * Finish the current job, resetting the run and job data and removing the busy flag.
     */
    void finish();


    /**
     * Stops the job executor. This will terminate the executor thread.
     */
    void quit();


    /**
     * Attempts to retrieve the result of the optimisation job.
     * @param run
     */
    void collectResults(Run run);


    /**
     * A job has been interrupted. This will set the corresponding flags and process the run to a final state.
     * @param runId
     */
    void runAborted(String runId);

    /**
     * A negative outcome of te job execution. This will set the corresponding flags and process the run to a final state.
     * @param runId - the run that failed
     */
    void runFailed(String runId);

    /**
     * A positive outcome of the job execution. This will set the corresponding flags and process the run to a final state.
     * @param runId - the run that is successfully completed.
     */
    void runCompleted(String runId);


    /**
     * This function pushes to the notification bus the serialized information
     * into <i>JSON</i> format for the given <i>entry</i>. Its serialized form
     * is preceded by the prefix 'optim:log:', which identifies the nature of
     * the rest of the content.
     *
     * @param entry	an instance of {@link RunLogEntry} containing
     * 				the information parsed from one line of the live log stream
     * 				enriched with additional information about the log monitor.
     */
    void notifyOptimEvent(String runId, RunLogEntry entry);

    /**
     * This function pushes to the notification bus the serialized information into
     * <i>JSON</i> format for the run instance that is configured with this instance
     * of the JobMonitor. The serialized data is preceded by the 'job:'
     * prefix, which identifies the nature of the remaining content.
     */
    void notifyRunEvent(String runId);

}
