package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Entity;

import java.util.List;

/**
 * A region can be a country or a state in a country. The region is used to specify government
 * targets and urgency categories for selective surgeries. These categories and KPI targets
 * can vary from region to region. Due to the fact that a country could have different categories
 * in different state, the region doesn't need to be a country.
 *
 * @author Peter Ilfrich
 */
public class Region extends Entity {

    // TODO: extract to enum?
    /**
     * If an interval is a week
     */
    public static final String INTERVAL_WEEKLY = "weekly";
    /**
     * If an interval is 2 weeks
     */
    public static final String INTERVAL_BIWEEKLY = "bi-weekly";
    /**
     * If an interval is a month
     */
    public static final String INTERVAL_MONTHLY = "monthly";
    /**
     * If an interval is a quarter
     */
    public static final String INTERVAL_QUARTERLY = "quarterly";
    /**
     * If an interval is half a year
     */
    public static final String INTERVAL_HALFYEARLY = "half-yearly";
    /**
     * If an interval is a year
     */
    public static final String INTERVAL_YEARLY = "yearly";


    /**
     * The name of the region
     */
    @JsonProperty("name")
    private String name;

    /**
     * A list of urgency categories defined for this region
     */
    @JsonProperty("urgencyCategories")
    private List<UrgencyCategory> urgencyCategories;

    /**
     * The interval type (which can be weekly, monthly, quarterly, yearly)
     */
    @JsonProperty("intervalType")
    private String intervalType;

    /**
     * The timestamp of when the first interval starts
     */
    @JsonProperty("firstIntervalStart")
    private long firstIntervalStart;

    /**
     * Default constructor returning a transient region without any values.
     */
    public Region() {
    }

    /**
     * Creates a new region with the specified name
     * @param name - the name of the region
     */
    public Region(String name) {
        this.name = name;
    }

    /**
     * Complete constructor for the transient region, which will initialise every field of the Region.
     * @param name - the name of the region
     * @param urgencyCategories - a list of urgency categories
     * @param intervalType - the interval type
     * @param firstIntervalStart - the timestamp when the first interval starts.
     */
    public Region(String name, List<UrgencyCategory> urgencyCategories, String intervalType, long firstIntervalStart) {
        this.name = name;
        this.urgencyCategories = urgencyCategories;
        this.intervalType = intervalType;
        this.firstIntervalStart = firstIntervalStart;
    }




    /**
     * Getter for the name of the region
     * @return - the name of the region
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the interval type (see INTERVAL_? constants)
     * @return - a String representing the interval type
     */
    public String getIntervalType() {
        return intervalType;
    }

    /**
     * Getter for the start of the first in interval
     * @return - the timestamp of the start of the first interval
     */
    public long getFirstIntervalStart() {
        return firstIntervalStart;
    }

    /**
     * Getter for the list of urgency categories in this region
     * @return - a list of urgency categories
     */
    public List<UrgencyCategory> getUrgencyCategories() {
        return urgencyCategories;
    }


    /**
     * Setter for the name field
     * @param name - the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the interval type.
     * @param type - any of the INTERVAL_? constants
     */
    public void setIntervalType(String type) {
        this.intervalType = type;
    }

    /**
     * Setter for the start of the first interval
     * @param start - a timestamp representing the start of the first interval
     */
    public void setFirstIntervalStart(long start) {
        this.firstIntervalStart = start;
    }

    /**
     * Setter for the urgency categories. Will overwrite any existing list.
     * @param categories - a list of urgency categories
     */
    public void setUrgencyCategories(List<UrgencyCategory> categories) {
        this.urgencyCategories = categories;
    }
}
