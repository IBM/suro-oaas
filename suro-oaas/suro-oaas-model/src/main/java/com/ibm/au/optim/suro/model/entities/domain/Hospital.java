package com.ibm.au.optim.suro.model.entities.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Entity;

import java.util.List;

/**
 * Transient implementation of the Hospital interface. This implementation just provides plain getters and setters and
 * some basic constructors. It should not be used outside of tests.
 *
 * @author Peter Ilfrich
 */
public class Hospital extends Entity {

    /**
     * The Id of the region the hospital belongs to.
     */
    @JsonProperty("regionId")
    private String regionId;
    /**
     * The name of the hospital
     */
    @JsonProperty("name")
    private String name;
    /**
     * A list of ward objects belonging to the hospital
     */
    @JsonProperty("wards")
    private List<Ward> wards;
    /**
     * A list of departments belonging to the hospital
     */
    @JsonProperty("departments")
    private List<Department> departments;
    /**
     * A list of specialist types available for this hospital.
     */
    @JsonProperty("specialistTypes")
    private List<SpecialistType> specialistTypes;
    /**
     * A list of urgency categories applicable for this hospital. These are defined on a region level and can be
     * overwritten on hospital level
     */
    @JsonProperty("urgencyCategories")
    private List<UrgencyCategory> urgencyCategories;
    /**
     * The number of ICU beds in the hospital
     */
    @JsonProperty("icuBedCount")
    private int icuBedCount;
    /**
     * The number of theatre sessions the hospital performs per day
     */
    @JsonProperty("theatreSessionsPerDay")
    private int theatreSessionsPerDay;
    /**
     * The duration of a single session as specified by the hospital
     */
    @JsonProperty("sessionDuration")
    private int sessionDuration;
    /**
     * The number of operation theatres in the hospital
     */
    @JsonProperty("theatreCount")
    private int theatreCount;

    /**
     * The default constructor
     */
    public Hospital() {
    }

    /**
     * Basic constructor creating a default hospital with a name within a specified region
     * @param regionId - the name of the region of this hospital (this is not the technical _id)
     * @param name - the neew name of this hospital
     */
    public Hospital(String regionId, String name) {
        this.regionId = regionId;
        this.name = name;
    }

    /**
     * A complex constructor, which allows to construct an object with all fields prepopulated.
     *
     * @param regionId              - the id of the region the hospital should be long to
     * @param name                  - the name of the hospital
     * @param wards                 - the list of wards available in this hospital
     * @param departments           - the list of departments available in this hospital
     * @param specialistTypes       - the list of different specialists available in the hospital
     * @param urgencyCategories     - a list of urgency categories defined (overwrite) for this hospital
     * @param icuBedCount           - the number of ICU beds in the hospital
     * @param theatreSessionsPerDay - the number of sessions the hospital performs per day
     * @param sessionDuration       - the duration of a single session
     * @param theatreCount          - the number of operation theatres
     */
    public Hospital(String regionId, String name, List<Ward> wards, List<Department> departments, List<SpecialistType> specialistTypes,
                    List<UrgencyCategory> urgencyCategories, int icuBedCount, int theatreSessionsPerDay, int sessionDuration, int theatreCount) {
        this.regionId = regionId;
        this.name = name;
        this.wards = wards;
        this.departments = departments;
        this.specialistTypes = specialistTypes;
        this.urgencyCategories = urgencyCategories;
        this.icuBedCount = icuBedCount;
        this.theatreSessionsPerDay = theatreSessionsPerDay;
        this.sessionDuration = sessionDuration;
        this.theatreCount = theatreCount;
    }


    /**
     * Getter for the region ID
     * @return - the name of the region the hospital is located in (determines base KPIs)
     */
    public String getRegionId() {
        return regionId;
    }

    /**
     * Setter for the region ID
     * @param regionId - the new name of the region the hospital should be long to
     */
    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    /**
     * Getter for the name of the hospital
     * @return - the name of the hospital
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the hospital
     * @param name - the new name of the hospital
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the wards of the hospital
     * @return - the list of wards of this hospital
     */
    public List<Ward> getWards() {
        return wards;
    }

    /**
     * Setter for the list of wards of this hospital
     * @param wards - the new wards of this hospital
     */
    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

    /**
     * Getter for the list of departments
     * @return - the departments of this hospital
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * Setter for the departments list
     * @param departments - the new list of departments of this hospital
     */
    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    /**
     * Getter for the specialist types.
     * @return - the list of specialist types in this hospital.
     */
    public List<SpecialistType> getSpecialistTypes() {
        return specialistTypes;
    }

    /**
     * Setter for the specialist types.
     * @param specialistTypes - the new list of specialist types in this hospital
     */
    public void setSpecialistTypes(List<SpecialistType> specialistTypes) {
        this.specialistTypes = specialistTypes;
    }

    /**
     * Getter for the urgency categories
     * @return - a list of urgency categories (might be complemented by {@link Region#getUrgencyCategories()})
     */
    public List<UrgencyCategory> getUrgencyCategories() {
        return urgencyCategories;
    }

    /**
     * Setter for the urgency categories
     * @param urgencyCategories - the new list of urgency catgories of this hospital (see also
     * {@link Region#setUrgencyCategories(List)})
     */
    public void setUrgencyCategories(List<UrgencyCategory> urgencyCategories) {
        this.urgencyCategories = urgencyCategories;
    }

    /**
     * Getter for icu bed count
     * @return - the number of ICU beds in this hospital
     */
    public int getIcuBedCount() {
        return icuBedCount;
    }

    /**
     * Setter for the icu bed count
     * @param icuBedCount - the new number of ICU beds in this hospital
     */
    public void setIcuBedCount(int icuBedCount) {
        this.icuBedCount = icuBedCount;
    }

    /**
     * Getter for the theatre sessions per day
     * @return - the number of theatre sessions per day (base configuration of the hospital)
     */
    public int getTheatreSessionsPerDay() {
        return theatreSessionsPerDay;
    }

    /**
     * The setter for the theatre sessions per day
     * @param theatreSessionsPerDay - the new number of theatre sessions per day in this hospital
     */
    public void setTheatreSessionsPerDay(int theatreSessionsPerDay) {
        this.theatreSessionsPerDay = theatreSessionsPerDay;
    }

    /**
     * Getter for the session duration
     * @return - the duration of a standard session in minutes
     */
    public int getSessionDuration() {
        return sessionDuration;
    }

    /**
     * Setter for the session duration
     * @param sessionDuration - the new number of minutes for the duration of the standard session in this hospital
     */
    public void setSessionDuration(int sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    /**
     * Getter for the theatre count
     * @return - the total number of theatres in this hospital
     */
    public int getTheatreCount() {
        return theatreCount;
    }

    /**
     * Setter for the theatre count
     * @param theatreCount - the new number of available theatres in this hospital
     */
    public void setTheatreCount(int theatreCount) {
        this.theatreCount = theatreCount;
    }
}
