package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.util.SessionIdentifierGenerator;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * Database preparer to ensure that sub-items of a hospital (departments, wards, specialist types, urgency categories)
 * or region (urgency categories) each have their own unique ID. If they don't have an ID, the item needs to be updated.
 *
 * This is required due to #1603 as part of #1463.
 *
 * @author Peter Ilfrich
 */
public class HospitalSubItemIdPreparer implements DatabasePreparer {

    /**
     * An instance of a generator used to generate unique ids.
     */
    private SessionIdentifierGenerator uuidGenerator = new SessionIdentifierGenerator();


    @Override
    public boolean check(Environment env) throws Exception {
        // check regions
        for (Region region : getRegionRepo(env).getAll()) {
            if (checkHospitalSubElement(region.getUrgencyCategories())) {
                return true;
            }
        }


        // check hospitals
        for (Hospital hospital : getHospitalRepo(env).getAll()) {
            if (checkHospital(hospital)) {
                return true;
            }
        }

        return false;
    }



    @Override
    public void execute(Environment env) throws Exception {
        // handle hospitals
        for (Hospital hospital : getHospitalRepo(env).getAll()) {

            // if a violation occurs, fix it (includes repo update)
            if (checkHospital(hospital)) {
                fixHospital(hospital, env);
            }

        }

        // handle regions
        for (Region region : getRegionRepo(env).getAll()) {

            // check the urgency categories
            if (checkHospitalSubElement(region.getUrgencyCategories())) {

                // if violated fix the list and update the item
                fixHospitalSubElement(region.getUrgencyCategories());
                getRegionRepo(env).updateItem(region);
            }
        }
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }


    /*
     * HELPER METHODS
     */

    /**
     * Checks a specific list of hospital sub elements (departments, wards, specialist types, urgency categories) for
     * completeness - in this case an existing unique id.
     * @param elements - the list of elements to check (or null)
     * @return - true if any violation occurs (missing id of any element)
     */
    protected boolean checkHospitalSubElement(List<? extends HospitalSubElement> elements) {
        // no list provided - no violation
        if (elements == null) {
            return false;
        }

        // check each element for existing id
        for (HospitalSubElement el : elements) {
            if (el.getId() == null) {
                return true;
            }
        }

        // no violations
        return false;

    }


    /**
     * Checks the sub elements (wards, departments, urgency categories, specialist types) of a hospital for completeness
     * (existing id).
     * @param hospital - the hospital to check
     * @return true if any violation occurs; false otherwise.
     */
    protected boolean checkHospital(Hospital hospital) {

        if (checkHospitalSubElement(hospital.getUrgencyCategories())) {
            // check urgency categories
            return true;

        } else if (checkHospitalSubElement(hospital.getDepartments())) {
            // check departments
            return true;

        } else if (checkHospitalSubElement(hospital.getWards())) {
            // check wards
            return true;

        } else if (checkHospitalSubElement(hospital.getSpecialistTypes())) {
            // check specialist types
            return true;

        } else {
            // no violations
            return false;

        }
    }


    /**
     * Fixes the given hospitals sub items and adds IDs where missing. This method assumes that there are violations and
     * will always write (update) the hospital in the repository (regardless if something has changed or not).
     * @param hospital - the hospital to fix
     * @param env - the environment used to retrieve the hospital repository
     */
    protected void fixHospital(Hospital hospital, Environment env) {
        // check and fix urgency categories
        if (checkHospitalSubElement(hospital.getUrgencyCategories())) {
            fixHospitalSubElement(hospital.getUrgencyCategories());

        }

        // check and fix specialist types
        if (checkHospitalSubElement(hospital.getSpecialistTypes())) {
            fixHospitalSubElement(hospital.getSpecialistTypes());

        }

        // check and fix specialist types
        if (checkHospitalSubElement(hospital.getWards())) {
            fixHospitalSubElement(hospital.getWards());

        }

        // check and fix specialist types
        if (checkHospitalSubElement(hospital.getDepartments())) {
            fixHospitalSubElement(hospital.getDepartments());

        }

        // write the changes to the repository
        getHospitalRepo(env).updateItem(hospital);
    }

    /**
     * Fixes a list of hospital sub elements. This will modify the list passed into this method (without returning it).
     * The method will check each element in the list. If it doesn't have an ID yet, one will be generated.
     *
     * @param elements - the elements to fix.
     */
    protected void fixHospitalSubElement(List<? extends HospitalSubElement> elements) {
        if (elements == null) {
            return;
        }

        for (HospitalSubElement el : elements) {
            if (el.getId() == null) {
                el.setId(uuidGenerator.next());
            }
        }

        // all done, update of the store object is done by the calling method
        return;
    }


    /*
     * REPOSITORY ACCESSORS
     */

    /**
     * Retrieves the hospital repository from the environment.
     * @param env - the current environment
     * @return - the hospital repository
     */
    protected HospitalRepository getHospitalRepo(Environment env) {
        return (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
    }

    /**
     * Retrieves the region repository from the environment.
     * @param env - the current environment
     * @return - the region repository.
     */
    protected RegionRepository getRegionRepo(Environment env) {
        return (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
    }
}
