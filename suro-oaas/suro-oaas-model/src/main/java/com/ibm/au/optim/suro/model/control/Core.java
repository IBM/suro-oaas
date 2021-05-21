package com.ibm.au.optim.suro.model.control;

import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.entities.Run;

/**
 * Singleton providing core functionality for the scheduling and execution of runs. The Core provides access to models,
 * data sets, runs and the job controller. The Core can also be used by other components of the system as it provides
 * access to general system properties.
 *
 * @author Peter Ilfrich
 */
public interface Core {

    /**
     * Instance name in the environment used to access the Core singleton
     */
    String CORE_INSTANCE = "core:instance";

    /**
     * Type helper used to retrieve the class name of the implementing class.
     */
    String CORE_TYPE = Core.class.getName();


    /**
     * System preference name specifying the current database content version.
     */
    String PREFERENCE_DB_VERSION_NAME = "database.content.version";

    /**
     * Creates a new run in the database and attaches it to the queue.
     * @param run	the {@link Run} instance that contains all the informatio for the submission.
     * @return - the run submitted to the system
     */
    Run submitRun(Run run);

    /**
     * Attempts to abort a run that has already been added to the queue. At first this method checks the queue if the
     * run is still in there. If this is the case it just gets removed and the status updated to aborted. If the run is
     * no longer in the queue, it is either already aborted or currently running.
     * @param runId - the ID of the run to abort
     * @return - true if the run could be aborted, false if the run could not be aborted.
     */
    boolean abortRun(String runId);

    /**
     * This method will first call Core#abortRun and then also make sure to delete the run object from the repository.
     * Should any of those 2 operation fail, the method will return false;
     * @param runId - the ID of the run to delete
     * @return - true if the run is successfully deleted / false if the run could not be deleted.
     */
    boolean deleteRun(String runId);

    /**
     * Loads the next run from the RunController.
     * @return - the next run or null, if no run is available in the queue.
     */
    Run getNextRun();

    /**
     * Updates the run object to completion. Setting the final state of the job.
     * @param runId - the run to update
     */
    void completeRun(String runId);

    /**
     * Makes sure no job executor is still executing this run, cleans up any jobs still
     * associated with this run, updates the run data (job ID), resets the state and adds
     * the run to the queue.
     *
     * @param runId - the run to restart
     */
    void restartRun(String runId);


    /**
     * Returns true if the system is ready for processing. When the system is not ready either the data migration is
     * still running or the system is in maintenance mode (manually set by an admin)
     * @return
     */
    boolean isReady();

    /**
     * Sets the systems ready state. If the system is not ready, no requests will be processed. If the system is ready,
     * job processing is permitted.
     * @param rdy - the new ready state
     * @return
     */
    void setReady(boolean rdy);

    /**
     * Retrieves the current version of the database. This is used by the data migration to determine the migration
     * operations.
     * @return - the version of the current database
     */
    String getCurrentDatabaseVersion();

    /**
     * Stores the new database version in the system preferences
     * @param newVersion
     */
    void setDatabaseVersion(String newVersion);

    /**
     * Returns the model controller currently configured for the system providing the model script and operation
     * parameters.
     * @return
     */
    ModelController getModelController();

    /**
     * Returns the run controller currently configured for the system controlling the run queue and allows easy access
     * to runs.
     * @return
     */
    RunController getRunController();

    /**
     * Returns the data set controller currently configued for the system providing access to the compiled dat file
     * either by retrieval from the file system, database or via computation utilising the prediction engine.
     * @return
     */
    DataSetController getDataSetController();

    /**
     * Returns an instance of the job controller associated with this core, handling the executors.
     * @return
     */
    JobController getJobController();

    /**
     * Returns the preference manager for the system, which is a basic key-value store.
     * @return
     */
    PreferenceManager getPreferenceManager();

}
