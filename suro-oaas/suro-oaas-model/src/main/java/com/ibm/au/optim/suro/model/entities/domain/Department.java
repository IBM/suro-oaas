package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A hospital is divided into departments. Departments focus on specific surgery types or
 * specific parts of the body. Examples for departments are Cardiology or Dental.
 *
 * @author Peter Ilfrich
 */
public class Department extends HospitalSubElement {



    @JsonProperty("name")
    private String name;

    @JsonProperty("maxSimultaneousSessions")
    private int maxSimultaneousSessions = 0;

    /**
     * Default constructor
     */
    public Department() {

    }

    /**
     * Creates a new department instance with the provided data
     * @param name - the name of the department
     * @param maxSimultaneousSessions - the number of maximum simultaneous sessions in this department
     */
    public Department(String name, int maxSimultaneousSessions) {
        this.name = name;
        this.maxSimultaneousSessions = maxSimultaneousSessions;
    }



    /**
     * Retrieves the name of this department instance
     * @return - the name of this department instance
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the maximum number of simultaneous sessions
     * @return - the maximum number of simultaneous sessions
     */
    public int getMaxSimultaneousSessions() {
        return maxSimultaneousSessions;
    }


    /**
     * Defines the new value for the maximum amount of simultaneous sessions.
     * @param newMax - the new maximum
     */
    public void setMaxSimultaneousSessions(int newMax) {
        this.maxSimultaneousSessions = newMax;
    }

    /**
     * Defines a new name for this department
     * @param newName - the new name for the department
     */
    public void setName(String newName) {
        this.name = newName;
    }


}
