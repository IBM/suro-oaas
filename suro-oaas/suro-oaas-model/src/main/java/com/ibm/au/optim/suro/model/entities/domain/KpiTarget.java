package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An instance of this object represents a specific KPI target. Multiple KPI target instances
 * specify the requirements for an urgency category.
 *
 * @author Peter Ilfrich
 */
public class KpiTarget {

    /**
     * The interval
     */
    @JsonProperty("interval")
    int interval;

    /**
     * The number of points for this target
     */
    @JsonProperty("numberOfPoints")
    int numberOfPoints;

    /**
     * The on time performance required to achieve the points
     */
    @JsonProperty("requiredOnTimePerformance")
    double requiredOnTimePerformance;

    /**
     * Default constructor
     */
    public KpiTarget() {

    }

    /**
     * Creates a new kpi target
     * @param interval - the interval for which the target is valid
     * @param points - the number of points to achieve
     * @param requiredPerformance - the required performance to achieve the points
     */
    public KpiTarget(int interval, int points, double requiredPerformance) {
        this.interval = interval;
        this.numberOfPoints = points;
        this.requiredOnTimePerformance = requiredPerformance;
    }

    /**
     * Getter for the interval
     * @return - usually this is e.g. a quarter (1, 2, 3, 4)
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Setter for the interval.
     * @param interval - the new interval for this target
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * The number of points this target represents
     * @return - the number of points for this target
     */
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    /**
     * Setter for the points this targets represents
     * @param numberOfPoints - the new number of points defined by this target.
     */
    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    /**
     * Getter for the on time performance
     * @return - the percentage of on time performance required to reach this target
     */
    public double getRequiredOnTimePerformance() {
        return requiredOnTimePerformance;
    }

    /**
     * Setter for the on time performance
     * @param requiredOnTimePerformance - the percentage of on time performance required to reach this target.
     */
    public void setRequiredOnTimePerformance(double requiredOnTimePerformance) {
        this.requiredOnTimePerformance = requiredOnTimePerformance;
    }
}
