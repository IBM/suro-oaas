/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * Class <b>RunDetails</b>. This class collects the details of the execution of
 * the run in the optimisation back-end. Besides details about the final result
 * of the optimisation process and (if occurred) information about errors, the
 * class also contains the log information that has been generated during the
 * remote execution of the optimisation process. This log is organised as a list
 * of entries that track the incremental progression of the solver towards the
 * optimal solution.
 * </p>
 */
public class RunDetails extends Entity {

	/**
	 * A {@link String} containing the unique identifier of the {@link Run} instance to which
	 * the information contained in this {@link RunDetails} instance refers to.
	 */
	@JsonProperty("runId")
	private String runId;

	/**
	 * A {@link List} of {@link RunLogEntry} records representing the collection of events that
	 * track the progress of the solver towards the optimal solution.
	 */
	@JsonProperty("entries")
	private List<RunLogEntry> entries;

	/**
	 * A {@literal double} value representing the value of the best bound found by the solver.
	 */
	@JsonProperty("bestBound")
	private double bestBound;

	/**
	 * A {@literal double} value representing the value of the best integer solution found
	 * to approximate the optimal solution. 
	 */
	@JsonProperty("bestInteger")
	private double bestInteger;

	/**
	 * A {@literal double} value representing the value of the gap achieved by the solver
	 * with respect to the optimal solution.
	 */
	@JsonProperty("gap")
	private double gap;
	
	/**
	 * A {@link Map} implementation containing the collection of <i>key-value</i> pairs
	 * that are used to store additional information, which are specific to the implementation
	 * of the optimisation back-end.
	 */
	@JsonProperty("attributes")
	private Map<String, Object> attributes;


	/**
	 * Initialise an instance of {@link RunDetails}.
	 */
	public RunDetails() {

	}
	/**
	 * Initialises an instnce of {@link RunDetails} with the given parameters.
	 * 
	 * @param runId			a {@link String} representing the unique identifier of the {@link Run} instance this
	 * 						instance of {@link RunDetails} relates to.
	 * @param entries		a {@link List} implementation containing the list of {@link RunLogEntry} items that
	 * 						represent the collection of events tracking the evolution of the optimisation process.
	 * @param bestBound		a {@literal double} value representing the best bound found by the solver.
	 * @param bestInt		a {@literal double} value representing the best integer found by the solver.
	 * @param gap			a {@literal double} value representing the gap achieved by the solver.
	 * @param attributes	a {@link Map} implementation containing additional information about optimisation 
	 * 						process that is specific to the optimisation engine used.
	 */
	public RunDetails(String runId, List<RunLogEntry> entries, double bestBound, double bestInt, double gap, Map<String,Object> attributes) {
		this.runId = runId;
		this.entries = entries;
		this.bestBound = bestBound;
		this.bestInteger = bestInt;
		this.gap = gap;

		this.attributes = attributes;
	}

	
	/**
	 * Gets the {@link Run} ID this result is associated with
	 *
	 * @return a {@link String} representing the unique identifier of the 
	 * 		   {@link Run} instance these details refer to.
	 */
	public String getRunId() {
		
		return this.runId;
	}

	/**
	 * Sets the {@link Run} ID for this result
	 *
	 * @param runId	a {@link String} representing the unique identifier of the
	 * 				{@link Run} instance these details refer to.
	 */
	public void setRunId(String runId) {
		
		this.runId = runId;
	}

	/**
	 * Gets the list of chronological CPLEX solver events that track the
	 * progression toward an optimal solution. These results are generated from
	 * the CplexLogFileParser.
	 *
	 * @return 	a {@link List} containing the {@link RunLogEntry} items that
	 * 			track the evolution of the solver over time.
	 */
	public List<RunLogEntry> getEntries() {
		
		return this.entries;
	}

	/**
	 * Gets the list of log entries that have been generated during the execution
	 * of the {@link Run} in the optimisation back-end.
	 *
	 * @param entries		a {@link List} containing the {@link RunLogEntry} items 
	 * 						that track the evolution of the solver over time.
	 */
	public void setEntries(List<RunLogEntry> entries) {
		
		this.entries = entries;
	}

	/**
	 * Gets the best bound computed so far by the solver. This value is updated 
	 * over time as new log entries are added and the optimisation process evolves.
	 *
	 * @return	a {@literal double} value indicating the best bound.
	 */
	public double getBestBound() {
		
		return this.bestBound;
	}

	/**
	 * Sets the best bound computed so far by the solver. This value is updated 
	 * over time as new log entries are added and the optimisation process evolves.
	 *
	 * @param bestBound		a {@literal double} value indicating the best bound.
	 */
	public void setBestBound(double bestBound) {
		
		this.bestBound = bestBound;
	}

	/**
	 * Gets the best integer value approximating the optimal solution 
	 * found by the solver. This value is updated over time as more log 
	 * entries are added and the optimisation process.
	 *
	 * @return a {@literal double} value representing the best integer.
	 */
	public double getBestInteger() {
		
		return this.bestInteger;
	}

	/**
	 * Sets the best integer value approximating the optimal solution 
	 * found by the solver. This value is updated over time as more log 
	 * entries are added and the optimisation process evolves.
	 *
	 * @param bestInteger	a {@literal double} value representing the 
	 * 						best integer.
	 */
	public void setBestInteger(double bestInteger) {
		
		this.bestInteger = bestInteger;
	}

	/**
	 * Gets the best gap value achieved so far by the solver to approximate
	 * the best solution. This value is updated over time as more log entries
	 * are added and the optimisation process evolves.
	 *
	 * @return	a {@literal double} value representing the best gap.
	 */
	public double getGap() {
		
		return this.gap;
	}

	/**
	 * Sets the best gap value achieved so far by the solver to approximate
	 * the best solution. This value is updated over time as more log entries
	 * are added and the optimisation process evolves.
	 *
	 * @param gap	a {@literal double} value representing the best gap.
	 */
	public void setGap(double gap) {
		
		this.gap = gap;
	}

	/**
	 * Append a new optimization log entry to the list of optimization results
	 * These entries should be generated from a CPLEX solver log using a
	 * log file parser
	 *
	 * @param entry	a {@link RunLogEntry} representing an optimization result entry.
     *
     * @return {@literal true} if the entry was added, {@literal false} if not.
     *
	 */
	public boolean addEntry(RunLogEntry entry) {
		if (entries == null) {
			entries = new ArrayList<>();
		}

		// check if the entry already exists to avoid overlapping entries when resuming runs
		for(RunLogEntry e : entries) {
			if (e.getTime() == entry.getTime()) {
				// abort adding the entry or evaluating its information (gap, bounds, ...)
				return false;
			}
		}

		entries.add(entry);
		if (entry.getGap() != null) {
			setGap(entry.getGap());
		}
		if (entry.getBestInteger() != null) {
			setBestInteger(entry.getBestInteger());
		}
		if (entry.getBestBound() != null) {
			setBestBound(entry.getBestBound());
		}

		return true;
	}
	
	/**
	 * Sets the collection of additional attributes that contain additional
	 * information specific to the back-end implementation of the optimisation
	 * engine used.
	 * 
	 * 
	 * @param attributes	a {@link Map} implementation containing a collection
	 * 						of <i>key-value</i> pair that define the collection
	 * 						of attributes to set. It can be {@literal null}.
	 */
	public void setAttributes(Map<String, Object> attributes) {
		
		this.attributes = attributes;
		
	}
	
	/**
	 * Gets the collection of additional attributes that contain specific
	 * information to the optimisation engine back-end used to solve the
	 * optimisation process. By default it is {@literal null}.
	 * 
	 * @return	a {@link Map} implementation containing a collection of the
	 * 			<i>key-value</i> pairs representing the attributes. 
	 */
	public Map<String,Object> getAttributes() {
		
		return this.attributes;
	}
	
	/**
	 * Sets the value for a given attribute. An attribute is an additional
	 * property that is specific to either the implementation of the back
	 * end used for the optimisation and provides additional information
	 * beyond the fields defined in this instance, which are common to all
	 * (or most) of the optimisation processes.
	 * 
	 * @param name	a {@link String} representin the name of the attribute.
	 * 				It cannot be {@literal null} or an empty string.
	 * 
	 * @param value	a {@link Object} reference representing the value of 
	 * 				the attribute.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null}
	 * 									or an empty string.
	 */
	public void setAttribute(String name, Object value) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		if (this.attributes == null) {
			
			this.attributes = new HashMap<String, Object>();
		}
		
		this.attributes.put(name, value);
	}
	
	/**
	 * Gets the value for a given attribute. An attribute is an additional
	 * property that is specific to either the implementation of the back
	 * end used for the optimisation and provides additional information
	 * beyond the fields defined in this instance, which are common to all
	 * (or most) of the optimisation processes.
	 * 
	 * @param name	a {@link String} representing the name of the attribute
	 * 				we are looking for. It cannot be {@literal null} or an
	 * 				empty string.
	 * 
	 * @return	a {@link Object} reference representing the corresponding
	 * 			value of the attribute, or {@literal null} if not present.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null}
	 * 									or an empty string.
	 */
	public Object getAttribute(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		Object value = null;
		
		if (this.attributes != null) {
			
			value = this.attributes.get(name);
		}
		
		return value;
	}
	
	/**
	 * <p>
	 * This method clones a {@link RunDetails}. The {@link String} properties
	 * of a {@link RunDetails} are not cloned because the underlying type is
	 * immutable. The method first, invokes the super-class version of the
	 * method and then clones or copies the properties directly defined by
	 * the {@link RunDetails} class. 
	 * </p>
	 * <p>
	 * <b>NOTE:</b> the <i>attributes</i> field is not deeply copied but the
	 * values are simply passed from one set of attributes (the current one)
	 * to the cloned one. The method will only create a new {@link Map} 
	 * implementation to hold these values.
	 * </p>
	 * 
	 * @return  a {@link RunDetails} instance that represents the clone of the
	 * 			the current instance.
	 */
	@Override
	public Entity clone() {
		
		// this call to the super-class version of the method
		// does take care of the super-class defined properties.
		//
		RunDetails zombie = (RunDetails) super.clone();
		
		// here we need to clone the specific properties that
		// are defined in this class.
		
		zombie.setBestBound(this.getBestBound());
		zombie.setBestInteger(this.getBestInteger());
		zombie.setGap(this.getGap());
		zombie.setRunId(this.getRunId());
		
		// now the list of template parameters.
		//
		List<RunLogEntry> newEntries = null;
		List<RunLogEntry> entries = this.getEntries();
		if (entries != null) {
			
			newEntries = new ArrayList<RunLogEntry>();
			for(RunLogEntry e : entries) {
				newEntries.add(e.clone());
			}
		}
		
		zombie.setEntries(newEntries);
		

		
		Map<String,Object> attributes = this.getAttributes();
		if (attributes != null) {
			
			Map<String,Object> cloned = new HashMap<String,Object>();
			for(Entry<String,Object> pair : attributes.entrySet()) {
		
				cloned.put(pair.getKey(), pair.getValue());
			}
			
			zombie.setAttributes(cloned);
		}
		
		return zombie;
	}
	
	
	/**
	 * This method creates an instance of {@link RunDetails}. The method overrides
	 * the base class implementation to change the type of the instance returned
	 * by the method to {@link RunDetails}.
	 * 
	 * @return an instance of {@link RunDetails}.
	 */
	@Override
	protected Entity newInstance() {
		
		return new RunDetails();
	}

}
