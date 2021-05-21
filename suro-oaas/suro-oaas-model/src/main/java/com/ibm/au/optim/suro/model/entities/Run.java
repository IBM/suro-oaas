/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * Class <b>Run</b>. This class extends {@link Entity} and represents the
 * submission of an optimisation. This abstraction does not directly represents
 * the underlying job that is submitted to the optimisation back-end nor contains
 * all the details of the results. It simply contains the summary information that
 * is required and of most interest for the end user. Other entities contain more
 * specialised information. 
 * </p>
 * <p>
 * A {@link Run} instance is created out of a given {@link Template} and it is
 * designed to tune such template to trigger an optimization job for executing the
 * underlying optimization model the template belongs to. Therefore, a {@link Run}
 * instance is mostly characterised by the free parameters defined in the template
 * it belongs to and additional specification about the desired gap threshold and
 * run time limit.
 * </p>
 * <p>
 * During execution the {@link Run} instance captures the information about the
 * execution status and changes about the status of the associated optimisation 
 * job. When the run terminates additional information about the completion status
 * the information about the solver status as well as the final gap and execution
 * time.
 * </p>
 * 
 * @see RunDetails
 * @see Job
 * 
 * @author Christian Vecchiola
 *
 */
public class Run extends Entity {
	
	/**
	 * A {@literal double} value indicating the default value for
	 * the gap property.
	 */
	public static final double DEFAULT_GAP = 1.0;

	/**
	 * A {@link String} representing the unique identifier of the
	 * {@link Template} instance that represents the template
	 * from which this {@link Run} instance has been created.
	 */
	@JsonProperty("templateId")
	protected String templateId;

	/**
	 * A {@link String} representing the unique identifier of the
	 * {@link Model} instance for which this {@link Run}
	 * instance is prepared.
	 */
	@JsonProperty("modelId")
	protected String modelId;
	
	/**
	 * A {@link String} representing the unique identifier of the
	 * partial dataset file that is used to run the optimisation
	 * job.
	 */
	@JsonProperty("datasetId")
	protected String dataSetId;
	
	/**
	 * A {@link String} representing the unique identifier of the 
	 * associated underlying optimisation job that is executed to
	 * solve the optimisation problem.
	 */
	@JsonProperty("jobId")
	protected String jobId;
	
	
	/**
	 * A {@link String} representing the current status of the {@link Run}. 
	 * This set of statuses is different from the statuses of the underlying 
	 * optimisation job, because the {@link Run} instance exists before and 
	 * after the corresponding job.
	 */
	@JsonProperty("status")
	protected RunStatus status = RunStatus.NEW;
	
	/**
	 * A {@link String} representing the current status of the {@link Job}
	 * that models the execution of the model.
	 */
	@JsonProperty("jobStatus")
	protected JobStatus jobStatus;
	
	/**
	 * A {@link String} representing the final status of the solver that solved
	 * the optimisation problem defined by the model associated to the run and
	 * specialised with the parameters coming from this {@link Run} and its
	 * associated {@link Template}.
	 */
	@JsonProperty("solveStatus")
	protected String solveStatus;

	/**
	 * A {@link String} representing the friendly name associated to the {@link Run}
	 * by the user. This is used primarily in the user interface for the purpose of
	 * providing a more helpful interface to the user.
	 */
	@JsonProperty("label")
	protected String label;

	/**
	 * A {@link String} representing the textual description of the run. This text
	 * can be helpful for the user to remember the parameter settings as well as the
	 * scenario explored with this specific setting.
	 */
	@JsonProperty("description")
	protected String description;
	
	/**
	 * A {@literal double} value indicating the minimum acceptable gap that is 
	 * defined in order to consider the solution feasible. The gap is expressed
	 * as a percentage and computed as a difference from the best non known
	 * analytical solution to the problem. Therefore, a gap of 0.05 indicates that
	 * a solution needs to be at least 99.95% of the best known analytical solution.
	 */
	@JsonProperty("minGap")
	protected double minGap = Run.DEFAULT_GAP;
	
	/**
	 * A {@literal int} value indicating the maximum number of minutes that the
	 * optimisation model is allowed to run to find a solution. This parameter is
	 * the one that eventually terminates the execution, if the execution is not
	 * aborted by other means.
	 */
	@JsonProperty("maxRunTime")
	protected long maxRunTime;
	
	/**
	 * A {@link List} of {@link Parameter} instances representing the free parameters
	 * that the {@link Template} from which this {@link Run} instance was created
	 * made available to the user for tuning. These do not represent all the parameters
	 * required by the model to execute, but this list will be eventually completed by
	 * adding first the default settings of the corresponding {@link Template} and
	 * then for the remaining parameters the default values set in the underlying model.
	 */
	@JsonProperty("parameters")
	protected List<Parameter> parameters;
	
	/**
	 * A {@literal long} value indicating the time in milliseconds of execution
	 * of the {@link Run}. The default value is 0.
	 */ 
	@JsonProperty("runTime")
	protected long runTime = 0;
	
	/**
	 * A {@literal long} value indicating the Unix epoch time of the instant in
	 * time when the {@link Run} was started.
	 */
	@JsonProperty("startTime")
	protected long startTime = 0;

	/**
	 * A {@link double} value indicating the final gap achieved by the solver
	 * for the specific instance of the optimisation problem associated to this
	 * {@link Run}. This value is expressed in percentage and ranges from 0.0 
	 * to 1.0.
	 */
	@JsonProperty("finalGap")
	protected double finalGap = Run.DEFAULT_GAP;
	
	
	/**
	 * Initialises an instance of the {@link Run}.
	 */
	public Run() {
		
	}
	
	/**
	 * Sets the unique identifier of the optimization model for this this {@link Run}
	 * has been prepared.
	 * 
	 * @param modelId	a {@link String} representing the unique identifier of the
	 * 					model.
	 */
	public void setModelId(String modelId) {
		
		this.modelId = modelId;
	}

	
	/**
	 * Gets the unique identifier of the optimization model for this this {@link Run}
	 * has been prepared.
	 * 
	 * @return	a {@link String} representing the unique identifier of the model.
	 */
	public String getModelId() {
		
		return this.modelId;
	}
	

	/**
	 * Sets the unique identifier of the {@link Template} instance that 
	 * represents the template from which this {@link Run} instance has been 
	 * created.
	 * 
	 * @param templateId	a {@link String} representing the unique identifier 
	 * 						of the template.
	 */
	public void setTemplateId(String templateId) {
		
		this.templateId = templateId;
	}
	
	/**
	 * Gets the unique identifier of the {@link Template} instance that 
	 * represents the template from which this {@link Run} instance has been 
	 * created.
	 * 
	 * @return	a {@link String} representing the unique identifier of the
	 * 			template.
	 */
	public String getTemplateId() {
		
		return this.templateId;
	}

	/**
	 * Gets the unique identifier of the {@link DataSet} instance that will be
	 * used to compose the final optinmisation problem subimtted to the solver.
	 * 
	 * @return 	a {@link String} representing the unique identifier of the data
	 * 			set.
	 */
	public String getDataSetId() {
		
		return this.dataSetId;
	}

	/**
	 * Sets the unique identifier of the {@link DataSet} instance that will be
	 * used to compose the final optinmisation problem subimtted to the solver.
	 * 
	 * @param dataSetId 	a {@link String} representing the unique identifier 
	 * 						of the data set.
	 */
    public void setDataSetId(String dataSetId) {
    	
		this.dataSetId = dataSetId;
	}
    
    /**
	 * Gets the unique identifier of the job that has been associated (if any)
	 * to this instance of the {@link Run} to solve the optimisation problem.
     * 
     * @return	a {@link String} representing the unique identifier of the job.
     * 			This value is {@literal null} until a request for optimisation
     * 			is submitted to the optimisation backend.
     */
	public String getJobId() {
		
		return this.jobId;
	}
    /**
	 * Gets the unique identifier of the job that has been associated (if any)
	 * to this instance of the {@link Run} to solve the optimisation problem.
     * 
     * @param jobId		a {@link String} representing the unique identifier of 
     * 					the job. This value is {@literal null} until a request 
     * 					for optimisation is submitted to the optimisation backend.
     */
	public void setJobId(String jobId) {
		
		this.jobId = jobId;
	}


	/**
	 * Gets the status of the {@link Run}. This set of statuses is different from the 
	 * statuses of the underlying optimisation job, because the {@link Run} instance 
	 * exists before and after the corresponding job.
	 * 
	 * @return	a {@link RunStatus} value that indicates the current status of
	 * 			the {@link Run}.
	 */
	public RunStatus getStatus() {
		
		return this.status;
	}
	/**
	 * Sets the status of the {@link Run}. This set of statuses is different from the 
	 * statuses of the underlying optimisation job, because the {@link Run} instance 
	 * exists before and after the corresponding job.
	 * 
	 * @param status	a {@link RunStatus} value that indicates the current status of
	 * 					the {@link Run}.
	 * 
	 * @throws IllegalArgumentException	if <i>status</i> is {@literal null}.
	 */
	public void setStatus(RunStatus status) {
		
		if (status == null) {
			
			throw new IllegalArgumentException("Parameter 'status' cannot be null.");
		}
		this.status = status;
	}

	/**
	 * Gets the status of the underlying job that has been spwaned in the 
	 * optimisation back-end to solve the optimisation problem.
	 * 
	 * @return	a {@link JobStatus} value representing the status of the
	 * 			job associated to the run.
	 */
	public JobStatus getJobStatus() {
		
		return this.jobStatus;
	}


	/**
	 * Sets the status of the underlying job that has been spwaned in the 
	 * optimisation back-end to solve the optimisation problem.
	 * 
	 * @param jobStatus	a {@link JobStatus} value representing the status of 
	 * 					the job associated to the run.
	 */
	public void setJobStatus(JobStatus jobStatus) {
		
		this.jobStatus = jobStatus;
	}

	/**
	 * Sets the final status of the reached by the solver that solved the
	 * optimisation problem spawned from this {@link Run} instance.
	 * 
	 * @return	a {@link String} containing the value of the solve status.
	 */
	public String getSolveStatus() {
		
		return this.solveStatus;
	}

	/**
	 * Sets the final status of the reached by the solver that solved the
	 * optimisation problem spawned from this {@link Run} instance.
	 * 
	 * @param solveStatus	a {@link String} containing the value of the 
	 * 						solve status.
	 */
	public void setSolveStatus(String solveStatus) {
		
		this.solveStatus = solveStatus;
	}
	
	/**
	 * Gets the friendly name for the run. The friendly name can be used in the
	 * user interface to provide a more helpful identifier for the purpose of
	 * run management.
	 * 
	 * @return	a {@link String} representing the friendly name of the run.
	 */
	public String getLabel() {
		
		return this.label;
	}
	
	/**
	 * Sets the friendly name for the run. The friendly name can be used in the
	 * user interface to provide a more helpful identifier for the purpose of
	 * run management.
	 * 
	 * @param label		a {@link String} representing the friendly name of the run.
	 */
	public void setLabel(String label) {
		
		this.label = label;
	}

	/**
	 * Gets an informative description about the run. This can be used to provide
	 * context about the run in relation to the parameter settings and the scenario
	 * being explored.
	 * 
	 * @return	a {@link String} representing the description of the run.
	 */
	public String getDescription() {
		
		return this.description;
	}


	/**
	 * Sets an informative description about the run. This can be used to provide
	 * context about the run in relation to the parameter settings and the scenario
	 * being explored.
	 * 
	 * @param description	a {@link String} representing the description of the run.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}
	
	/**
	 * Gets the list of parameters that the user can control in order to tune the
	 * template from which this {@link Run} instance was created. Parameters can
	 * either be weights for the objectives of the model or control other aspects
	 * of the optimisation process.
	 * 
	 * @return	a {@link List} of {@link Parameter} instances representing the 
	 * 			parameters of the run.
	 */
	public List<Parameter> getParameters() {
		
		return this.parameters;
	}
	

	
	/**
	 * Sets the list of parameters that the user can control in order to tune the
	 * template from which this {@link Run} instance was created. Parameters can
	 * either be weights for the objectives of the model or control other aspects
	 * of the optimisation process.
	 * 
	 * @param parameters	a {@link List} of {@link Parameter} instances representing 
	 * 						the parameters of the run.
	 */
	public void setParameters(List<Parameter> parameters) {
		
		this.parameters = parameters;
	}
	
	/**
	 * This method gets the parameter identified by <i>name</i> if present. 
	 * 
	 * @param name	a {@link String} representing the name of the parameter to look for.
	 * 				This argument cannot be {@literal null} or an empty string.
	 * 
	 * @return	a {@link Parameter} instance whose name is <i>name</i> or {@literal null}
	 * 			if not found.
	 * 
	 * @throws 	IllegalArgumentException	if <i>name</i> is {@literal null} or an empty
	 * 										string.
	 */
	public Parameter getParameter(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		Parameter found = null;
		
		if (this.parameters != null) {
			
			for(Parameter rp : this.parameters) {
				
				if (name.equals(rp.getName()) == true) {
					
					found = rp;
					break;
				}
			}
		}
		
		return found;
	}
	
	/**
	 * Sets the minimum gap for the solution. The minimum gap indicates the minimum 
	 * acceptable gap that is defined in order to consider the solution feasible. 
	 * 
	 * @param minGap	a {@literal double} representing the gap. The gap is expressed
	 * 					as a percentage and computed as a difference from the best known
	 * 					analytical solution to the problem. Therefore, a gap of 0.05 
	 * 					indicates that a solution needs to be at least 99.95% of the best 
	 * 					known analytical solution.
	 */
	public void setMinGap(double minGap) {
		
		this.minGap = minGap;
	}

	
	/**
	 * Gets the minimum gap for the solution. The minimum gap indicates the minimum 
	 * acceptable gap that is defined in order to consider the solution feasible. 
	 * 
	 * @return	a {@literal double} representing the gap. The gap is expressed as a 
	 * 			percentage and computed as a difference from the best known analytical 
	 * 			solution to the problem. Therefore, a gap of 0.05 indicates that a 
	 * 			solution needs to be at least 99.95% of the best known analytical solution.
	 */
	public double getMinGap() {
		
		return this.minGap;
	}
	
	/**
	 * Gets the run time limit set for the execution of this {@link Run}. This value
	 * indicates the maximum number of milliseconds that the optimisation model is allowed 
	 * to run to find a solution. This parameter is he one that eventually terminates 
	 * the execution, if the execution is not aborted by other means.
	 * 
	 * @param maxRunTime	a {@literal long} representing the number of milliseconds.
	 */
	public void setMaxRunTime(long maxRunTime) {
		
		this.maxRunTime = maxRunTime;
	}
	
	/**
	 * Gets the run time limit set for the execution of this {@link Run}. This value
	 * indicates the maximum number of milliseconds that the optimisation model is allowed 
	 * to run to find a solution. This parameter is he one that eventually terminates 
	 * the execution, if the execution is not aborted by other means.
	 * 
	 * @return	a {@literal long} representing the number of milliseconds.
	 */
	public long getMaxRunTime() {
		
		return this.maxRunTime;
	}
	
	/**
	 * Gets the execution time in milliseconds of the {@link Run}.
	 * 
	 * @return	a {@literal long} value indicating the execution time in milliseconds.
	 */
	public long getRunTime() {
		
		return this.runTime;
	}
	
	/**
	 * Sets the execution time in milliseconds of the {@link Run}.
	 * 
	 * @param runTime	a {@literal long} value indicating the execution time in milliseconds.
	 */
	public void setRunTime(long runTime) {
		
		this.runTime = runTime;
	}

	/**
	 * Gets the final gap for the run. A final gap indicates the best gap achieved
	 * by the solver for this specific instance of the optimisation problem spawn
	 * by the {@link Run}.
	 * 
	 * @return	a {@literal double} value indicating the percentage value of the gap 
	 * 			with respect to the best known analytical solution. This value ranges 
	 * 			from 0.0 to 1.0.
	 */
	public double getFinalGap() {
		
		return this.finalGap;
	}

	/**
	 * Sets the final gap for the run. A final gap indicates the best gap achieved
	 * by the solver for this specific instance of the optimisation problem spawn
	 * by the {@link Run}.
	 * 
	 * @param finalGap	a {@literal double} value indicating the percentage value of
	 * 					the gap with respect to the best known analytical solution.
	 * 					This value ranges from 0.0 to 1.0.
	 */
	public void setFinalGap(double finalGap) {
		
		this.finalGap = finalGap;
	}

	/**
	 * Gets the start time for the {@link Run}.
	 * 
	 * @return	a {@literal long} value indicating the Unix epoch time of the
	 * 			start time
	 */
	public long getStartTime() {
		
		return this.startTime;
	}

	/**
	 * Sets the start time for the {@link Run}.
	 * 
	 * @param startTime	a {@literal long} value indicating the Unix epoch time
	 * 					of the start time.
	 */
	public void setStartTime(long startTime) {
		
		this.startTime = startTime;
	}
	
	/**
	 * Gets {@link String} representation of the {@link Run}.
	 * 
	 * @return 	a {@link String} in the following form:
	 * 			<pre>
	 * 			[id: ..., label: ...., status: ...., jobId: ...., gap: ...]
	 * 			</pre>
	 */
	@Override
	public String toString() {
		
		return "[id: " + this.getId() + 
				", label: " + this.getLabel() + 
				", status: " + this.getStatus() + 
				", jobId: " + this.getJobId() + 
				", gap: " + this.getFinalGap() + "]";
				
	}
	
	/**
	 * This method clones a {@link Template}. The {@link String} properties
	 * of a {@link Template} are not cloned because the underlying type is
	 * immutable. The method first, invokes the super-class version of the
	 * method and then clones or copies the properties directly defined by
	 * the {@link Template} class.
	 * 
	 * @return  a {@link Template} instance that represents the clone of the
	 * 			the current instance.
	 */
	@Override
	public Entity clone() {
		
		// this call to the super-class version of the method
		// does take care of the super-class defined properties.
		//
		Run zombie = (Run) super.clone();
		
		// here we need to clone the specific properties that
		// are defined in this class.
		
		zombie.setLabel(this.getLabel());
		zombie.setDescription(this.getDescription());
		
		zombie.setModelId(this.getModelId());
		zombie.setTemplateId(this.getTemplateId());
		zombie.setDataSetId(this.getDataSetId());
		zombie.setJobId(this.getJobId());
		
		zombie.setMinGap(this.getMinGap());
		zombie.setFinalGap(this.getFinalGap());
		
		zombie.setMaxRunTime(this.getMaxRunTime());
		zombie.setStartTime(this.getStartTime());
		zombie.setRunTime(this.getRunTime());
		
		zombie.setSolveStatus(this.getSolveStatus());
		zombie.setStatus(this.getStatus());
		zombie.setJobStatus(this.getJobStatus());
		
		// now the list of template parameters.
		//
		List<Parameter> newParams = null;
		List<Parameter> params = this.getParameters();
		if (params != null) {
			
			newParams = new ArrayList<Parameter>();
			for(Parameter p : params) {
				newParams.add(p.clone());
			}
		}
		
		zombie.setParameters(newParams);
		
		return zombie;
	}
	
	/**
	 * This method creates an instance of {@link Run}. The method overrides the
	 * base class implementation to change the type of the instance returned by
	 * the method to {@link Run}.
	 * 
	 * @return an instance of {@link Run}.
	 */
	@Override
	protected Entity newInstance() {
		
		return new Run();
	}
}
