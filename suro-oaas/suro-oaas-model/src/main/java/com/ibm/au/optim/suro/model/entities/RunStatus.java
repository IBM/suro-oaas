package com.ibm.au.optim.suro.model.entities;

/**
 * Enum <b>RunStatus</b>. This enumeration defines the value of all the statuses that a {@link 
 * Run} instance undergoes. A {@link Run} initially is queued, then a job for the optimisation
 * back-end is created and the execution of the optimisation problem starts. Once the execution
 * is completed the results are collected and the run is terminated thus moving to the completed
 * status. At any point in time the {@link Run} can be aborted. In this case if there is a
 * corresponding optimisation job in the back-end the API will attempt to the collect partial
 * results, and then the run will be aborted. 
 *
 * @author Peter Ilfrich & Christian Vecchiola
 */
public enum RunStatus {

	//
    // Happy path statuses
    // 
	
	/**
	 * The {@link Run} is just created. This is when a {@link Run}
	 * instance is initialised.
	 */
	NEW ("NEW", "Newly created"),

	/**
	 * The {@link Run} is queued. It has not been submitted yet to
	 * the optimisation back-end and it is waiting to be picked up
	 * and being submitted.
	 */
    QUEUED ("QUEUED", "Queuing For Executing"),
    
    /**
     * The {@link Run} has been collected from the queue and an a
     * job has been created in the optimisation back-end to solve
     * the associated optimisation problem. At this point a value
     * for the job identifier has been created and associated to
     * the {@link Run} instance that spawned the job. Moreovoer,
     * an instance of {@link OptimizationResult} to collect the
     * real time data and the final results of the optimisation 
     * has been created and the value of {@link Run#resultId} is
     * being set.
     */
    CREATE_JOB ("CREATE_JOB", "Created Job"),
    
    /**
     * The {@link Job} instance associated to the {@link Run} is
     * executing in the optimisation back-end. At this point in 
     * time the APIs are receiving updates about the progress of
     * the optimisation and the corresponding instance of {@link 
     * OptimizationResult} is collecting the real time data.
     */
    PROCESSING ("PROCESSING", "Executing Job"),
    
    /**
     * The job on the optimisation back-end has been terminated
     * and the APIs are collecting the results produced to create
     * all the required views on the data and update the {@link 
     * Run} and {@link OptimizationResult} instance with the 
     * required information.
     */
    COLLECTING_RESULTS ("COLLECTING_RESULTS", "Collecting Results"),
    
    /**
     * The {@link Run} instance has completed successfully. This is
     * most of the cases because the the maximum run time configured
     * with the {@link Run} has been reached. At this point the
     * {@link Run} instance contains all the summary information
     * required, such as the value of the final gap, the solve status,
     * the nature of the solution and the execution time.
     */
    COMPLETED ("COMPLETED", "Complete"),

    //
    // Error statuses
    //
    
    /**
     * A request to terminate the {@link Run} execution has beeen issued.
     * This status can also be triggered by the optimisation back-end if
     * for any reason the corresponding job has being aborted.
     */
    ABORTING ("ABORTING", "Run Terminated"),
    
    /**
     * The {@link Run} instance has been forcefully terminated either by
     * the back-end or the user. Before transiting into this status, the
     * {@link Run} instance moves to the {@link RunStatus#COLLECTING_RESULTS}
     * to collect any partial result created so far.
     */
    ABORTED ("ABORTED", "Aborted Run"),
    
    /**
     * An error originated during the execution or abortion of the {@link Run}.
     * The {@link Run} is terminated.
     */
    FAILED ("FAILED", "Failed"),
    
    /**
     * This status is a possible next status from {@link RunStatus#QUEUED},
     * {@link RunStatus#CREATE_JOB}, and {@link RunStatus#PROCESSING}. This
     * status is assigned when it is not possible to further continue with
     * the processing of the {@link Run} because its settings and / or data
     * are not valid.
     */
    INVALID ("INVALID", "Invalid Run"),

    /*
     * Exception processing statuses
     */
    
    /**
     * This is the status in which all the {@link Run} that were not terminated
     * transit into when the API are rebooted. In this status the API will try
     * to synchronize their view of the runs with the view and the statuses in
     * the optimization back-end.
     */
    RESUME ("RESUME", "Resuming Run");


    /**
     * The identifier of the status.
     */
    private final String identifier;
    
    /**
     * The human-readable label for this status.
     */
    private final String label;



    /**
     * Initialises an instance of the {@link RunStatus} enumeration with the given identifier
     * and human friendly label.
	 *
     * @param id 			the technical identifier for this run state (TODO: might be removed)
     * @param statusLabel	the human readable status description
     *
     * TODO: need to check if we can rework the constructor and remove the id (which is the same as the name)
     */
    RunStatus(String id, String statusLabel) {
        this.identifier = id;
        this.label = statusLabel;
    }

    /**
     * Returns the identifier of the RunStatus (the name of the enumeration).
     * 
     * @return 	a {@link String} representing the unique identifier of the
     * 			value of the enumeration.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the label of this run status in a human readable form.
     * 
     * @return 	a {@link String} representing the human friendly text of the
     * 			enumeration value.
     */
    public String getLabel() {
        return this.label;
    }
    /**
     * Gets a {@link String} representation of the enumeration value.
     * 
     * @return the value of the <i>identifier</i> property.
     */
    @Override
    public String toString() {
    	
    	return this.identifier;
    }
}
