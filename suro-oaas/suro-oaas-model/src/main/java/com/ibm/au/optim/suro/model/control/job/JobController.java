package com.ibm.au.optim.suro.model.control.job;

import java.util.List;

import com.ibm.au.optim.suro.model.control.ResultManager;

/**
 * Interface for the job controller. The job controller is responsible for managing the executors and providing them
 * with functionality related to jobs and their associated runs.
 *
 * @author Peter Ilfrich
 */
public interface JobController {

    /**
     * The namespace used to store the job controller instance.
     */
	String JOB_CONTROLLER_INSTANCE = "controller:job:instance";

    /**
     * The class name implementing the controller.
     */
	String JOB_CONTROLLER_TYPE = JobController.class.getName();


    /**
     * Set the provided run to completed and clean up any job related stuff.
     *
     * @param runId - the run to be completed
     */
    void completeJob(String runId);

    /**
     * Aborts the provided run. Sets the run into the new state and aborts any running job.
     *
     * @param runId - the run to abort
     * @return true if the run and job was successfully aborted
     */
	boolean abortJob(String runId);

    /**
     * Cleanup any jobs associated with executors or the executing agent, which cannot be connected to any run in a
     * suitable state.
     */
	void cleanupJobs();

    /**
     * Creates a new job executor with the provided index. Any implementation should add the executor to a local list
     * of executors, so they can be accessed after creation.
     * @param index - the index of the executor (can be used to identify the executor)
     */
	void createJobExecutor(int index);


    /**
     * Tries to find a job executor executing the provided run
     * @param runId
     * @return
     */
    JobExecutor getJobExecutor(String runId);


    /**
     * Retrieves the list of executors managed by this controller.
     * @return - a list of JobExecutors
     */
    List<JobExecutor> getExecutors();


    /**
     * Deletes the job with the given ID in the execution environment. The deletion of the job doesn't include handling
     * of any job executors or monitors.
     * @param jobId
     */
    void deleteJob(String jobId);

    /**
     * Retrieves the result manager, which has the purpose to convert the fetched result node into the desired output
     * structure and store the output.
     * @return
     */
    ResultManager getResultManager();


}
