package com.ibm.au.optim.suro.model.control.domain;

import java.util.List;

import com.ibm.au.optim.suro.model.entities.domain.Department;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.entities.domain.SpecialistType;
import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;
import com.ibm.au.optim.suro.model.entities.domain.Ward;

/**
 * The HospitalController provides CRUD access to hospitals and regions as well as helper methods to handle sub-elements
 * of hospitals and regions (such as departments, wards, urgency categories and specialist types).
 *
 * @author Peter Ilfrich
 */
public interface HospitalController {

    /**
     * The environment attribute name under which the hospital controller will be accessible.
     */
    String HOSPITAL_CONTROLLER_INSTANCE = "controller:hospital:instance";

    /**
     * The implementing class name of the hospital controller
     */
    String HOSPITAL_CONTROLLER_TYPE = HospitalController.class.getName();

    /* Hospital Methods */

    /**
     * Creates a new hospital in the repository.
     * @param hospital - the hospital data
     * @return - the new hospital object including the id and revision
     */
    Hospital createHospital(Hospital hospital);

    /**
     * Updates a hospital in the repository
     * @param hospital - the new hospital data
     * @return - the updated hospital object
     */
    Hospital updateHospital(Hospital hospital);

    /**
     * Updates the hospital meta data. This doesn't update the hospital in the repository.
     * @param hospitalToUpdate - the old hospital object
     * @param newData - the new hospital meta data
     * @return - the new hospital object with the new meta data and the old functional data (wards, departments,
     *          specialist types and urgency categories)
     */
    Hospital updateHospitalMetaData(Hospital hospitalToUpdate, Hospital newData);

    /**
     * Deletes a hospital from the repository.
     * @param hospital - the hospital to delete
     * @return - true if the hospital was provided as parameter and therefore deleted from the repository
     */
    boolean deleteHospital(Hospital hospital);

    /**
     * Deletes a hospital from the repository
     * @param hospitalId - the id of the hospital to delete
     * @return - true if the hospital was deleted
     */
    boolean deleteHospital(String hospitalId);

    /**
     * Retrieves the hospital with the given id from the repository.
     * @param hospitalId - the id of the hospital to retrieve
     * @return - the hospital object if it exists or null
     */
    Hospital getHospital(String hospitalId);

    /**
     * Retrieves a list of all hospitals in the repository
     * @return - a list of all hospitals
     */
    List<Hospital> getHospitals();

    /**
     * Retrieves a list of all hospitals in the repository within the specified region
     * @param region - the region to filer the hospital list by
     * @return - a list of hospitals within the specified region
     */
    List<Hospital> getHospitals(Region region);

    /**
     * Retrieves a list of hospitals from the repository within a specified region
     * @param regionName - the name of the region (not the ID)
     * @return - a list of hospitals within the specified region
     */
    List<Hospital> getHospitals(String regionName);



    /* Hospital Sub Elements */

    /**
     * Retrieves the list of effective urgency categories for a specific hospital within a given region.
     *
     * @param hospital - the hospital to consider, which can overwrite KPI targets
     * @return - the list of effective urgency categories containing the KPI targets from the region with overwrites
     * from the hospital.
     */
    List<UrgencyCategory> compileUrgencyCategories(Hospital hospital);

    /**
     * Removes a specific urgency category from the provided hospital. The category is removed by comparing the name of
     * the catgory.
     * @param hospital - the hospital from which to remove an urgency category
     * @param category - the urgency category to remove
     * @return - the hospital object after the removal of the category.
     */
    Hospital removeUrgencyCategory(Hospital hospital, UrgencyCategory category);

    /**
     * Removes a specific ward from a list of existing wards
     *
     * @param hospital - the hospital with the original list from which to remove the item
     * @param ward - the item to remove
     * @return - the original list without the ward that was removed
     */
    Hospital removeWard(Hospital hospital, Ward ward);

    /**
     * Removes a specific department from a list of existing departments
     *
     * @param hospital - the hospital with the original list from which to remove the item
     * @param department - the item to remove
     * @return - the original list without the department that was removed
     */
    Hospital removeDepartment(Hospital hospital, Department department);

    /**
     * Removes a specific specialist type from a list of existing specialist types
     *
     * @param hospital - the hospital with the original list from which to remove the item
     * @param type - the item to remove
     * @return - the original list without the type that was removed
     */
    Hospital removeSpecialistType(Hospital hospital, SpecialistType type);

    /**
     * Creates a new department within the given hospital
     * @param hospital - the hospital for which to add the department
     * @param department - the department to add
     * @return - the hospital after the department was added
     */
    Hospital createDepartment(Hospital hospital, Department department);

    /**
     * Creates a new ward within the given hospital
     * @param hospital - the hospital for which to add the ward
     * @param ward - the ward to add
     * @return - the hospital after the ward was added
     */
    Hospital createWard(Hospital hospital, Ward ward);

    /**
     * Creates a new specialist type within the given hospital
     * @param hospital - the hospital for which to add the specialist type
     * @param type - the specialist type to add
     * @return - the hospital after the department was added
     */
    Hospital createSpecialistType(Hospital hospital, SpecialistType type);

    /**
     * Updates the departments of a hospital.
     * @param hospital - the hospital to update
     * @param departments - the new department list
     * @return - the hospital after the update
     */
    Hospital updateDepartments(Hospital hospital, List<Department> departments);

    /**
     * Updates the wards of a hospital.
     * @param hospital - the hospital to update
     * @param wards - the new ward list
     * @return - the hospital after the update
     */
    Hospital updateWards(Hospital hospital, List<Ward> wards);

    /**
     * Updates the specialist types of a hospital.
     * @param hospital - the hospital to update
     * @param types - the new list of specialist types
     * @return - the hospital after the update
     */
    Hospital updateSpecialistTypes(Hospital hospital, List<SpecialistType> types);

    /**
     * Updates the urgency categories of a hospital.
     * @param hospital - the hospital to update
     * @param categories - the new urgency categories
     * @return - the hospital after the update
     */
    Hospital updateUrgencyCategories(Hospital hospital, List<UrgencyCategory> categories);



    /* Region Methods */

    /**
     * Retrieves a region by ID from the repository
     * @param regionId - the id of the region to retrieve
     * @return - the region if it exists or null
     */
    Region getRegion(String regionId);

    /**
     * Retrieve a list of all regions from the repository
     * @return - a list of all regions
     */
    List<Region> getRegions();

    /**
     * Creates a new region in the repository
     * @param region - the new region
     * @return - the region object after adding it to the repository
     */
    Region createRegion(Region region);

    /**
     * Updates a region in the repository
     * @param region - the new region object
     * @return - the region object after update
     */
    Region updateRegion(Region region);

    /**
     * Deletes a region from the repository
     * @param region - the region object, the removal will use the id of the region
     * @return - true, if the region was deleted
     */
    boolean deleteRegion(Region region);

    /**
     * Deletes a region from the repository
     * @param regionId - the id of the region to delete
     * @return - true, if the region was deleted
     */
    boolean deleteRegion(String regionId);

    /**
     * Updates the urgency categories of a region.
     * @param region - the region to update
     * @param categories - the new urgency categories
     * @return - the region after the update
     */
    Region updateUrgencyCategories(Region region, List<UrgencyCategory> categories);

    /**
     * Removes the provided urgency category from the region.
     * @param region - the region to update
     * @param category - the category to remove from the region
     * @return - the region after the removal of the category
     */
    Region removeUrgencyCategory(Region region, UrgencyCategory category);


}
