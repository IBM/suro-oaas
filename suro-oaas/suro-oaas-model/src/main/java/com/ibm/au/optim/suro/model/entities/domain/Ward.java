package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A ward is a part of a hospital model. It has a name and a number of beds.
 *
 * @author Peter Ilfrich
 */
public class Ward extends HospitalSubElement {

    /**
     * The name of the ward
     */
    @JsonProperty("name")
    private String name;

    /**
     * The number of beds in this ward
     */
    @JsonProperty("bedsCount")
    private int bedsCount = 0;

    /**
     * Creates a new ward object with the specified name and the number of beds.
     *
     * @param name      - the name of the ward
     * @param bedsCount - the number of beds
     */
    public Ward(String name, int bedsCount) {
        this.name = name;
        this.bedsCount = bedsCount;
    }

    /**
     * Default constructor
     */
    public Ward() {
    }

    /**
     * Getter for the name
     *
     * @return - the name of the ward
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     *
     * @param name - the new name of the ward.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the beds cound
     *
     * @return - the number of beds in this ward
     */
    public int getBedsCount() {
        return bedsCount;
    }

    /**
     * Setter for the beds count
     *
     * @param bedsCount - the new number of beds in the ward.
     */
    public void setBedsCount(int bedsCount) {
        this.bedsCount = bedsCount;
    }

}
