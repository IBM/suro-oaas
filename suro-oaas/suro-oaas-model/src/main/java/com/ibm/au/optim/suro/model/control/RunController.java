package com.ibm.au.optim.suro.model.control;

import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.JobStatus;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.store.RunRepository;

import java.util.List;

/**
 * Controller to access the RunRepository. The controller provides methods to easily create and retrieve runs from
 * the database and update the status.
 *
 * The RunController also maintains the run queue.
 *
 * @author Peter Ilfrich
 */
public interface RunController {

    /**
     * The environment attribute name for this controller.
     */
    String RUN_CONTROLLER_INSTANCE = "controller:run:instance";

    /**
     * The type used to initialize the singleton for this controller.
     */
    String RUN_CONTROLLER_TYPE = RunController.class.getName();



    /**
     * Creates a new run for the given model, dataset and strategy, filled with the provided parameters.
     *
     * @param model - the optimisation model used for this run
     * @param dataSet - the data set used for this run
     * @param templateId - the strategy executed for this run
     * @param params - the parameters of this run in a map
     * @return The new run object
     */
    Run createRun(Run run);

    /**
     * Retrieves a run from the database specifies by the ID
     *
     * @param runId - the ID of the run document to retrieve
     * @return - the Run specified by the ID
     */
    Run getRun(String runId);

    /**
     * Retrieves the next run from the queue. This will return the first item from the queue or null, if no run is
     * currently available in the queue.
     *
     * @return - the first run of the run queue or null if not run is in the queue
     */
    Run getNext();

    /**
     * Adds a run to the queue.
     *
     * @return true or false depending on the success of the operation.
     */
    boolean addRun(Run run);

    /**
     * Remove a run from the queue if possible and update the state of the run.
     *
     * @param runId - the run to abort
     * @return - true if the operation was successful / false if the run is already in progress (and executing in DOCloud)
     */
    boolean abortRun(String runId);


    /**
     * Removes a run from the run repository.
     *
     * @param runId - the run to delete
     * @return - true or false depending on the success of the deletion
     */
    boolean deleteRun(String runId);

    /**
     * Resets the run (state, job state, job ID, ...) to CREATED
     * @param runId
     */
    void resetRun(String runId);

    /**
     * Returns the run repository associated with the controller.
     *
     * @return the run repository instance
     */
     RunRepository getRepository();


    /**
     * Returns a list representing the current run queue. This method does NOT return the actual queue, as access to
     * the queue is multi-theaded and should only be handled by the run controller.
     *
     * @return - a list with all the runs currently in the queue.
     */
    List<Run> getQueue();

    /**
     * Sets a run into the completed state and takes care of cleaning up the run.
     *
     * @param runId - the run to complete
     */
    void completeRun(String runId);

    /**
     * Updates a run object in the database with the provided information (the run parameter)
     *
     * @param run - the new updated object (the entire doc)
     */
    void updateRun(Run run);

    /**
     * Updates the state of a run in the database.
     *
     * @param run - the run to update
     * @param runStatus - the new run status
     */
    void setRunStatus(Run run, RunStatus runStatus);

    /**
     * Updates the job status of a run in the database.
     *
     * @param run - the run to update
     * @param status - the new job status
     */
    void setJobStatus(Run run, JobStatus status);

    /**
     * Returns the run associated with the provided job ID if it exists (or null if there's no run with the given job ID)
     *
     * @param jobId - the ID of the job in the executing environment
     * @return - the run associated with the job id (or null if the job id is not associated with any runs in the database)
     */
    Run getRunByJobId(String jobId);

    /**
     * Sets the run repository used for this controller. The controller will use this repository to store and read runs.
     * @param repository - the new run repository.
     */
    void setRepository(RunRepository repository);
}
