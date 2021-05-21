/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document containing processed data from a CPLEX log file entry as created by
 * the CplexLogFileParser. These values are used to construct a
 * chronological trace of solver events toward an optimal solution.
 *
 */
public class RunLogEntry implements Cloneable {
	
	/**
	 * System timestamp of when the log event was generated
	 */
	@JsonProperty("time")
	private long time = 0;
	
	/**
	 * Whether the event represents a valid solution
	 */
	@JsonProperty("isSolution")
	private boolean solution = false;
	
	/**
	 * Node number event was found for
	 */
	@JsonProperty("node")
	private Long node;
	
	/**
	 * 
	 */
	@JsonProperty("otherCheck")
	private boolean otherCheck;
	
	/**
	 * Remaining nodes to check
	 */
	@JsonProperty("nodesLeft")
	private Long nodesLeft;
	
	/**
	 * Current objective
	 */
	@JsonProperty("objective")
	private Double objective;
	
	@JsonProperty("iinf")
	private Long iinf;
	
	/**
	 * Current best integer
	 */
	@JsonProperty("bestInteger")
	private Double bestInteger;
	
	/**
	 * Current best bound
	 */
	@JsonProperty("bestBound")
	private Double bestBound;
	
	/**
	 * Total iterations
	 */
	@JsonProperty("totalIterations")
	private Long totalIterations;
	
	/**
	 * Optimality gap
	 */
	@JsonProperty("gap")
	private Double gap;
	
	/**
	 * Raw log file entry
	 */
	@JsonProperty("rawLine")
	private String rawLine;

	/**
	 * Getter for the rawLine attribute.
	 * @return - the rawLine value
	 */
	public String getRawLine() {
		
		return this.rawLine;
	}

	/**
	 * Setter for the rawLine attribute
	 * @param rawLine - the new rawLine value
	 */
	public void setRawLine(String rawLine) {
		
		this.rawLine = rawLine;
	}

	/**
	 * Getter for the time attribute
	 * @return - the time value
	 */
	public long getTime() {
		
		return this.time;
	}

	/**
	 * Setter for the time attribute
	 * @param time - the new time value
	 */
	public void setTime(long time) {
		
		this.time = time;
	}

	/**
	 * Getter for the solution flag.
	 * @return - whether the entry is a solution or not
	 */
	public boolean isSolution() {
		
		return this.solution;
	}

	/**
	 * Setter for the solution flag.
	 * @param solution - whether the entry is a solution or not.
	 */
	public void setSolution(boolean solution) {
		this.solution = solution;
	}

	/**
	 * Getter for the node attribute
	 * @return the node value
	 */
	public Long getNode() {
		
		return this.node;
	}

	/**
	 * Setter for the node attribute
	 * @param node - the new node valu7e
	 */
	public void setNode(Long node) {
		
		this.node = node;
	}

	/**
	 * Getter for the otherCheck flag
	 * @return - whether the otherCheck flag is set or not
	 */
	public boolean getOtherCheck() {
		
		return this.otherCheck;
	}

	/**
	 * Setter for the otherCheck flag
	 * @param otherCheck - whether the otherCheck flag should be set or not
	 */
	public void setOtherCheck(boolean otherCheck) {
		
		this.otherCheck = otherCheck;
	}

	/**
	 * Getter for the number of nodes to the left
	 * @return - the number of nodes to the left
	 */
	public Long getNodesLeft() {
		
		return this.nodesLeft;
	}

	/**
	 * Setter for the number of nodes to the left
	 * @param nodesLeft - the new number of nodes to the left
	 */
	public void setNodesLeft(Long nodesLeft) {
		
		this.nodesLeft = nodesLeft;
	}

	/**
	 * Getter for the objective attribute
	 * @return - the value of the objective attribute
	 */
	public Double getObjective() {
		
		return this.objective;
	}

	/**
	 * Setter for the objective attribute
	 * @param objective - the new objective attribute value
	 */
	public void setObjective(Double objective) {
		
		this.objective = objective;
	}

	/**
	 * Getter for the iinf attribute
	 * @return - the iinf attribute value
	 */
	public Long getIinf() {
		
		return this.iinf;
	}

	/**
	 * Setter for the iinf attribute
	 * @param iinf - the new iinf attribute value
	 */
	public void setIinf(Long iinf) {
		this.iinf = iinf;
	}

	/**
	 * Getter for the best integer (as outcome of the optimisation)
	 * @return - the value of the best integer
	 */
	public Double getBestInteger() {
		return bestInteger;
	}

	/**
	 * Setter for the best integer (as outcome of the optimisation)
	 * @param bestInteger - the current best integer value
	 */
	public void setBestInteger(Double bestInteger) {
		
		this.bestInteger = bestInteger;
	}

	/**
	 * Getter for the best bound.
	 * @return - the best bound (see bestInteger)
	 */
	public Double getBestBound() {
		return bestBound;
	}

	/**
	 * Setter for the best bound
	 * @param bestBound - the new best bound (see bestInteger)
	 */
	public void setBestBound(Double bestBound) {
		this.bestBound = bestBound;
	}

	/**
	 * Getter for the total number of iterations.
	 * @return - The number of total iterations of the optimisation
	 */
	public Long getTotalIterations() {
		
		return this.totalIterations;
	}

	/**
	 * Setter for the total number of iterations
	 * @param totalIterations - the total number of iterations of the optimisation
	 */
	public void setTotalIterations(Long totalIterations) {
		
		this.totalIterations = totalIterations;
	}

	/**
	 * Getter for the gap attribute
	 * @return - the current gap of the solution
	 */
	public Double getGap() {
		
		return this.gap;
	}

	/**
	 * Setter of the gap attribute
	 * @param gap - the new gap of the solution
	 */
	public void setGap(Double gap) {
		
		this.gap = gap;
	}

	/**
	 * Gets a {@link String} representation for a {@link RunLogEntry}.
	 * 
	 * @return a {@link String} containing the dump of the property values.
	 */
	@Override
	public String toString() {
		return "RunLogEntry [time=" + time + ", solution="
				+ solution + ", node=" + node + ", otherCheck=" + otherCheck
				+ ", nodesLeft=" + nodesLeft + ", objective=" + objective
				+ ", iinf=" + iinf + ", bestInteger=" + bestInteger
				+ ", bestBound=" + bestBound + ", totalIterations="
				+ totalIterations + ", gap=" + gap + "]\n" + "[" + rawLine
				+ "]";
	}

	/**
	 * This method clones a {@link RunDetails}. The {@link String} properties
	 * of a {@link RunLogEntry} are not cloned because the underlying type 
	 * is immutable. 
	 * 
	 * @return  a {@link RunDetails} instance that represents the clone of the
	 * 			the current instance.
	 */
	public RunLogEntry clone() {
		
		RunLogEntry zombie = new RunLogEntry();
		
		// here we need to clone the specific properties that
		// are defined in this class.
		zombie.setTime(this.getTime());
		
		Number tmp = this.getBestBound();
		zombie.setBestBound(tmp == null ? null : new Double(tmp.doubleValue()));
		
		tmp = this.getBestInteger();
		zombie.setBestInteger(tmp == null ? null : new Double(tmp.doubleValue()));
		
		tmp = this.getGap();
		zombie.setGap(tmp == null ? null : new Double(tmp.doubleValue()));
		
		tmp = this.getObjective();
		zombie.setObjective(tmp == null ? null : new Double(tmp.doubleValue()));
		
		tmp = this.getIinf();
		zombie.setIinf(tmp == null ? null : new Long(tmp.longValue()));
		
		tmp = this.getNode();
		zombie.setNode(tmp == null ? null : new Long(tmp.longValue()));
		
		tmp = this.getNodesLeft();
		zombie.setNodesLeft(tmp == null ? null : new Long(tmp.longValue()));
		
		tmp = this.getTotalIterations();
		zombie.setTotalIterations(tmp == null ? null : new Long(tmp.longValue()));

		zombie.setTime(this.getTime());
		zombie.setOtherCheck(this.getOtherCheck());
		zombie.setSolution(this.isSolution());
		zombie.setRawLine(this.getRawLine());
		
		return zombie;
	}
	
	/**
	 * This method implements the equality test for a {@link RunEntryLog} instances. Two instances
	 * of {@link RunEntryLog} are the same, if all the properties are the same.
	 * 
	 * @return {@literal true} if the test is successful or {@literal false} otherwise.
	 */
	public boolean equals(Object other) {
		
		boolean areTheSame = (this == other);
		
		if ((areTheSame == false) && (other != null) && (other instanceof RunLogEntry)) {
			
			RunLogEntry otherEntry = (RunLogEntry) other;
			
			// first the simple types, so that we can pipe them together.
			//
			areTheSame = (this.getOtherCheck() == otherEntry.getOtherCheck()) &&
						 (this.isSolution() == otherEntry.isSolution()) &&
						 (this.getTime() == otherEntry.getTime());
				
			
			// bestBound
			//
			if (areTheSame == true) {
				
				Double d1 = this.getBestBound();
				Double d2 = otherEntry.getBestBound();
				
				areTheSame = (d1 == d2) || ((d1 != null) && (d2 != null) && (d1.equals(d2))) || (d1 == null && d2 == null);
			}

			// bestInteger
			//
			if (areTheSame == true) {

				Double d1 = this.getBestInteger();
				Double d2 = otherEntry.getBestInteger();
				
				areTheSame = (d1 == d2) || ((d1 != null) && (d2 != null) && (d1.equals(d2))) || (d1 == null && d2 == null);
				
			}	

			// gap
			//
			if (areTheSame == true) {
				

				Double d1 = this.getGap();
				Double d2 = otherEntry.getGap();
				
				areTheSame = (d1 == d2) || ((d1 != null) && (d2 != null) && (d1.equals(d2))) || (d1 == null && d2 == null);
			}

			// iinf
			//
			if (areTheSame == true) {
				
				Long l1 = this.getIinf();
				Long l2 = otherEntry.getIinf();
				
				areTheSame = (l1 == l2) || ((l1 != null) && (l2 != null) && (l1.equals(l2))) || (l1 == null && l2 == null);
				
			}
			
			// node
			//
			if (areTheSame == true) {

				Long l1 = this.getNode();
				Long l2 = otherEntry.getNode();
				
				areTheSame = (l1 == l2) || ((l1 != null) && (l2 != null) && (l1.equals(l2))) || (l1 == null && l2 == null);
				
			}
			
			// nodesLeft
			//
			if (areTheSame == true) {

				Long l1 = this.getNodesLeft();
				Long l2 = otherEntry.getNodesLeft();
				
				areTheSame = (l1 == l2) || ((l1 != null) && (l2 != null) && (l1.equals(l2))) || (l1 == null && l2 == null);
				
			}

			// objective
			//
			if (areTheSame == true) {
				

				Double d1 = this.getObjective();
				Double d2 = otherEntry.getObjective();
				
				areTheSame = (d1 == d2) || ((d1 != null) && (d2 != null) && (d1.equals(d2))) || (d1 == null && d2 == null);
				
			}
			
			// rawLine
			//
			if (areTheSame == true) {
				
				String s1 = this.getRawLine();
				String s2 = otherEntry.getRawLine();

				areTheSame = (s1 == s2) || ((s1 != null) && (s2 != null) && (s1.equals(s2))) || (s1 == null && s2 == null);
				
			}
			
			// totalIterations
			//
			if (areTheSame == true) {
				

				Long l1 = this.getTotalIterations();
				Long l2 = otherEntry.getTotalIterations();
				
				areTheSame = (l1 == l2) || ((l1 != null) && (l2 != null) && (l1.equals(l2))) || (l1 == null && l2 == null);
			}
			
		}
		return areTheSame;
	}
	
	
	
}
