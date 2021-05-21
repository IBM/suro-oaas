package com.ibm.au.optim.suro.model.entities;

/**
 * Enum <b>JobStatus</b>. This enumeration encapsulates the set of statuses that identify 
 * the progress or evolution of a {@link Job} implementation in the optimisation back-end.
 * 
 * NOTE: At present time this set of statuses has been modelled by taking inspiration 
 * from the set os statuses that a job goes in DOCloud. 
 *
 * @author Peter Ilfrich & Christian Vecchiola
 */
public enum JobStatus {

    //
    // Happy path statuses
    //
	/**
	 * The {@link Job} is created or being created.
	 */
    CREATED ("CREATED", "Creating Job"),
    
    /**
     * The {@link Job} is being submitted.
     */
    SUBMITTED ("SUBMITTED", "Submitting Job"),
    
    /**
     * The {@link Job} is running.
     */
    RUNNING ("RUNNING", "Job Running"),
    
    /**
     * The {@link Job} has completed the execution of the optimisation problem
     * and the results are being collected.
     */
    PROCESSED ("PROCESSED", "Get Results"),
    
    /**
     * The {@link Job} has completed fullt and all the results have been collected.
     */
    COMPLETED ("COMPLETED", "Job Completed"),

    //
    // Exception statuses
    //
    /**
     * The {@link Job} has been interrupted. For instance, from a request to abort the job.
     */
    INTERRUPT ("INTERRUPT", "Job Interrupted"),
    
    /**
     * The {@link Job} failed with an exception on the optimisation back-end.
     */
    EXCEPTION ("EXCEPTION", "Job Failed With Exception"),
    
    /**
     * The {@link Job} has failed.
     */
    FAILED ("FAILED", "Job Failed");


    /**
     * A {@link String} representing the text of the enumeration value.
     */
    private String identifier;
    
    /**
     * A {@link String} representing the corresponding human friendly text
     * that is associated to the enumeration value.
     */
    private String label;

    /**
     * Initialises an instance of {@link JobStatus} with the given value
     * and corresponding friendly label.
     * 
     * @param id 			a {@link String} representing the identifier
     * 						of the status (i.e. upper case name).
     * 
     * @param statusLabel	a {@link String} representing the corresponding
     * 						friendly text for the job status value.
     */
    JobStatus(String id, String statusLabel) {
    	
        this.identifier = id;
        this.label = statusLabel;
    }

    /**
     * Gets the friendly name of the job status. This can be used in user
     * interface to provide a more helpful and friendly piece of information.
     * 
     * @return	a {@link String} representing the label of the status.
     */
    public String getLabel() {
    	
        return this.label;
    }

    /**
     * Gets the value of the status.
     * 
     * @return a {@link String} representing the value of the status.
     */
    public String getIdentifier() {
    	
        return this.identifier;
    }

    /**
     * Gets a {@link String} representation of the {@link JobStatus}
     * instance. This method returns the value of the <i>identifier</i>
     * property.
     * 
     * @return a {@link String} representing the value of the status.
     */
    @Override
    public String toString() {
    	
        return this.identifier;
    }

}
