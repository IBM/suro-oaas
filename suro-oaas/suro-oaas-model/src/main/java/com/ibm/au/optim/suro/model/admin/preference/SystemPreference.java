package com.ibm.au.optim.suro.model.admin.preference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Entity;

/**
 * Store object storing system wide preferences, persisting them in the repository to survive server restarts and
 * re-deployments.
 *
 * @author Peter Ilfrich
 */
public class SystemPreference extends Entity {

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;


    /**
     * Default constructor used for serialisation and deserialisation
     */
    public SystemPreference() {
    }

    /**
     * Creates a new system preference with the specified name and value
     * @param name - the name of the preference
     * @param value - the value of the preference
     */
    public SystemPreference(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Getter for the name of the preference
     * @return - the preference name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the preference
     * @param name - the new preference name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the value of the preference
     * @return - the preference value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for the value of the preference
     * @param value - the new preference value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
