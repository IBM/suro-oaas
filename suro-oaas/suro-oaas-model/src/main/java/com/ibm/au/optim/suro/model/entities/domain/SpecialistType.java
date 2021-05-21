package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A specialist type is a surgeon who is a member of a department and can perform certain operations.
 *
 * @author Peter Ilfrich
 */
public class SpecialistType extends HospitalSubElement {


    /**
     * The name of the department the specialist belongs to
     */
    @JsonProperty("departmentName")
    private String departmentName;

    /**
     * The label of the specialist type
     */
    @JsonProperty("label")
    private String label;

    /**
     * Default constructor
     */
    public SpecialistType() {}

    /**
     * Creates a new specialist type
     * @param label - the name of the specialist type
     * @param department - the department name the specialist belongs to
     */
    public SpecialistType(String label, String department) {
        this.label = label;
        this.departmentName = department;
    }
    
    /**
     * Getter for the label
     * @return - the name of the specialist type
     */
    public String getLabel() {
        return label;
    }

    /**
     * Getter for the department name
     * @return - the name of the department the specialist type belongs to
     */
    public String getDepartment() {
        return departmentName;
    }

    /**
     * Setter for the label
     * @param label - the new label of the specialist type
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Setter for the department name
     * @param dep - the name of the new department the specialist type belongs to.
     */
    public void setDepartment(String dep) {
        this.departmentName = dep;
    }

}
