package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract super class for {@link Department}, {@link Ward}, {@link UrgencyCategory} and {@link SpecialistType} to
 * provide the sub-element ID attribute and also allow handling of these sub-elements in the same method, if only the id
 * is required / important.
 *
 * @author Peter Ilfrich
 */
public abstract class HospitalSubElement {


    @JsonProperty("id")
    private String id;

    /**
     * Retrieves a unique ID identifying this sub element
     * @return - a unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets a new unique ID for this sub element
     * @param id - the new ID
     */
    public void setId(String id) {
        this.id = id;
    }
}
