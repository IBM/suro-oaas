package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Domain object representing an urgency category. An urgency category is defined for a
 * region and holds KPI targets specified by the government of the region. The KPI targets
 * specify what performance is required to score points and fulfill government requirements.
 *  *
 * @author Peter Ilfrich
 */
public class UrgencyCategory extends HospitalSubElement {



    /**
     * A list of kpi targets for this category
     */
    @JsonProperty("kpiTargets")
    private List<KpiTarget> kpiTargets;

    /**
     * A label for the category
     */
    @JsonProperty("label")
    private String label;

    /**
     * The maximum number of days on the waitlist for this category (before the category is violated)
     */
    @JsonProperty("maxWaitListStay")
    private int maxWaitListStay;

    /**
     * The minimum points required for this category
     */
    @JsonProperty("minPointsRequired")
    private int minPointsRequired;

    /**
     * The number of possible points for this category
     */
    @JsonProperty("possiblePoints")
    private int possiblePoints;


    /**
     * Creates a new urgency category
     * @param kpiTargets - the kpi targets for this category
     * @param label - the label of the category
     * @param maxWaitlistStay - the maximum number of days patients are allowed to stay on the waitlist
     * @param minPointsRequired - the minimum points required for this category
     * @param possiblePoints - the number of posssible points for this category
     */
    public UrgencyCategory(List<KpiTarget> kpiTargets, String label, int maxWaitlistStay, int minPointsRequired, int possiblePoints) {
        this.kpiTargets = kpiTargets;
        this.label = label;
        this.maxWaitListStay = maxWaitlistStay;
        this.minPointsRequired = minPointsRequired;
        this.possiblePoints = possiblePoints;
    }

    /**
     * Default constructor
     */
    public UrgencyCategory() {
    }



    /**
     * Returns a list of KPI targets for this urgency category instance.
     * @return - a list of KPI targets
     */
    public List<KpiTarget> getKpiTargets() {
        return kpiTargets;
    }

    /**
     * Setter for the kpi targets
     * @param kpiTargets - a new list of kpi targets
     */
    public void setKpiTargets(List<KpiTarget> kpiTargets) {
        this.kpiTargets = kpiTargets;
    }

    /**
     * Getter for the label
     * @return - the label of this category
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter for the label
     * @param label - the new label of this category
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for the max wait list stay
     * @return - the maximum number of days patients in this category are allowed to stay on the wait list
     */
    public int getMaxWaitListStay() {
        return maxWaitListStay;
    }

    /**
     * Setter for the max wait list stay
     * @param maxWaitListStay - the maximum number of days patients in this category are allowed to stay on the wait
     *                        list
     */
    public void setMaxWaitListStay(int maxWaitListStay) {
        this.maxWaitListStay = maxWaitListStay;
    }

    /**
     * Getter for the min points required.
     * @return - the minimum number of points required in this urgency category (see {@link KpiTarget})
     */
    public int getMinPointsRequired() {
        return minPointsRequired;
    }

    /**
     * Setter for the min points required.
     * @param minPointsRequired - the minimum number of points required to not violate this category.
     */
    public void setMinPointsRequired(int minPointsRequired) {
        this.minPointsRequired = minPointsRequired;
    }

    /**
     * Getter for the possible points
     * @return - the number of possible points to achieve in this category
     */
    public int getPossiblePoints() {
        return possiblePoints;
    }

    /**
     * Setter for the possible points
     * @param possiblePoints - the number of possible points to achieve in this category
     */
    public void setPossiblePoints(int possiblePoints) {
        this.possiblePoints = possiblePoints;
    }



}
