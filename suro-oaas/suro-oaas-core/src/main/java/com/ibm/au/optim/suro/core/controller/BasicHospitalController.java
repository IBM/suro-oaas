package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.util.SessionIdentifierGenerator;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of a hospital controller, implementing all methods of the interface and being available as
 * runtime service via the environment.
 *
 * The hospital controller allows CRUD operations on regions and hospitals. Furthermore hospital sub-elements such as
 * departments, wards and specialist types can be managed as well (CRUD). For regions and hospitals urgency categories
 * can also be managed (CRUD) as well as consolidated (region provides basic values, hospital overwrites values)
 *
 * @author Peter Ilfrich
 */
public class BasicHospitalController extends AbstractSuroService implements HospitalController {

    /**
     * The hospital repository storing hospital objects
     */
    private HospitalRepository hospitalRepository;

    /**
     * The region repository storing region objects
     */
    private RegionRepository regionRepository;

    /**
     * A generator to create unique string identifiers.
     */
    private SessionIdentifierGenerator uuidGenerator = new SessionIdentifierGenerator();

    /**
     * Constant for the helper methods to determine whether we are in a create or update scenario.
     */
    protected static final String OPERATION_CREATE = "CREATE";
    /**
     * Constant for the helper methods to determine whether we are in a create or update scenario.
     */
    protected static final String OPERATION_UPDATE = "UPDATE";

    @Override
    public Hospital createHospital(Hospital hospital) {
        if (hospital == null || hospital.getId() != null) {
            return null;
        }

        // make sure all departments have ids
        if (hospital.getDepartments() != null) {
            for (Department dep : hospital.getDepartments()) {
                if (dep.getId() == null) {
                    dep.setId(uuidGenerator.next());
                }
            }
        }

        // make sure all wards have ids
        if (hospital.getWards() != null) {
            for (Ward ward : hospital.getWards()) {
                if (ward.getId() == null) {
                    ward.setId(uuidGenerator.next());
                }
            }
        }

        // make sure all specialist types have ids
        if (hospital.getSpecialistTypes() != null) {
            for (SpecialistType type : hospital.getSpecialistTypes()) {
                if (type.getId() == null) {
                    type.setId(uuidGenerator.next());
                }
            }
        }

        // make sure all urgency categories have ids
        if (hospital.getUrgencyCategories() != null) {
            for (UrgencyCategory cat : hospital.getUrgencyCategories()) {
                if (cat.getId() == null) {
                    cat.setId(uuidGenerator.next());
                }
            }
        }

        hospitalRepository.addItem(hospital);
        return hospital;
    }

    @Override
    public Hospital updateHospital(Hospital hospital) {
        if (hospital == null || hospital.getId() == null) {
            return null;
        }

        hospitalRepository.updateItem(hospital);
        return hospital;
    }

    @Override
    public Hospital updateHospitalMetaData(Hospital hospitalToUpdate, Hospital newData) {
        if (hospitalToUpdate == null || newData == null) {
            return null;
        }

        hospitalToUpdate.setIcuBedCount(newData.getIcuBedCount());
        hospitalToUpdate.setTheatreCount(newData.getTheatreCount());
        hospitalToUpdate.setTheatreSessionsPerDay(newData.getTheatreSessionsPerDay());
        hospitalToUpdate.setSessionDuration(newData.getSessionDuration());
        hospitalToUpdate.setName(newData.getName());
        hospitalToUpdate.setRegionId(newData.getRegionId());

        updateHospital(hospitalToUpdate);
        return hospitalToUpdate;
    }

    @Override
    public boolean deleteHospital(Hospital hospital) {
        if (hospital == null) {
            return false;
        }
        return deleteHospital(hospital.getId());
    }

    @Override
    public boolean deleteHospital(String hospitalId) {
        if (hospitalId == null) {
            return false;
        }

        hospitalRepository.removeItem(hospitalId);
        return true;
    }

    @Override
    public Hospital getHospital(String hospitalId) {
        return hospitalRepository.getItem(hospitalId);
    }

    @Override
    public List<Hospital> getHospitals() {
        return hospitalRepository.getAll();
    }

    @Override
    public List<Hospital> getHospitals(Region region) {
        if (region == null) {
            return new ArrayList<>();
        }
        return getHospitals(region.getName());
    }

    @Override
    public List<Hospital> getHospitals(String regionName) {
        if (regionName == null) {
            return new ArrayList<>();
        }
        return hospitalRepository.findByRegion(regionName);
    }

    @Override
    public List<UrgencyCategory> compileUrgencyCategories(Hospital hospital) {
        if (hospital == null) {
            return new ArrayList<>();
        }

        Region region = regionRepository.findByName(hospital.getRegionId());
        Map<String, UrgencyCategory> categories = new HashMap<>();

        if (region != null) {
            // first use the region categories
            for (UrgencyCategory c : region.getUrgencyCategories()) {
                categories.put(c.getLabel(), c);
            }
        }

        // check which hospital categories override region categories
        if (hospital.getUrgencyCategories() != null) {
            for (UrgencyCategory cat : hospital.getUrgencyCategories()) {
                UrgencyCategory regionCat = categories.get(cat.getLabel());
                if (regionCat == null) {
                    categories.put(cat.getLabel(), cat);
                    continue;
                }
                // sync category parameters between region and hospital catgory
                if (regionCat.getMaxWaitListStay() != cat.getMaxWaitListStay()) {
                    regionCat.setMaxWaitListStay(cat.getMaxWaitListStay());
                }
                if (regionCat.getMinPointsRequired() != cat.getMinPointsRequired()) {
                    regionCat.setMinPointsRequired(cat.getMinPointsRequired());
                }
                if (regionCat.getPossiblePoints() != cat.getPossiblePoints()) {
                    regionCat.setPossiblePoints(cat.getPossiblePoints());
                }

                regionCat.setKpiTargets(getKpiTargets(regionCat, cat));
            }
        }

        List<UrgencyCategory> result = new ArrayList<>();
        for (UrgencyCategory cat : categories.values()) {
            result.add(cat);
        }

        return result;
    }

    @Override
    public Hospital removeUrgencyCategory(Hospital hospital, UrgencyCategory category) {
        if (category == null || hospital == null || hospital.getUrgencyCategories() == null || hospital.getUrgencyCategories().isEmpty()) {
            return hospital;
        }

        hospital.setUrgencyCategories(removeUrgencyCategory(hospital.getUrgencyCategories(), category));
        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital removeWard(Hospital hospital, Ward ward) {
        if (ward == null || hospital == null || hospital.getWards() == null || hospital.getWards().isEmpty()) {
            return hospital;
        }

        List<Ward> result = new ArrayList<>();
        for (Ward w : hospital.getWards()) {
            if (!w.getName().equals(ward.getName())) {
                result.add(w);
            }
        }
        hospital.setWards(result);

        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital removeDepartment(Hospital hospital, Department department) {
        if (department == null || hospital == null || hospital.getDepartments() == null || hospital.getDepartments().isEmpty()) {
            return hospital;
        }

        List<Department> result = new ArrayList<>();
        for (Department d : hospital.getDepartments()) {
            if (!d.getName().equals(department.getName())) {
                result.add(d);
            }
        }

        hospital.setDepartments(result);

        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital removeSpecialistType(Hospital hospital, SpecialistType type) {
        if (type == null || hospital == null || hospital.getSpecialistTypes() == null || hospital.getSpecialistTypes().isEmpty()) {
            return hospital;
        }

        List<SpecialistType> result = new ArrayList<>();
        for (SpecialistType t : hospital.getSpecialistTypes()) {
            if (!(t.getDepartment().equals(type.getDepartment()) && t.getLabel().equals(type.getLabel()))) {
                result.add(t);
            }
        }

        hospital.setSpecialistTypes(result);

        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Region getRegion(String regionId) {
        return regionRepository.getItem(regionId);
    }

    @Override
    public List<Region> getRegions() {
        return regionRepository.getAll();
    }

    @Override
    public Region createRegion(Region region) {
        if (region == null || region.getId() != null) {
            return null;
        }

        if (region.getUrgencyCategories() != null) {
            for (UrgencyCategory cat : region.getUrgencyCategories()) {
                if (cat.getId() != null) {
                    cat.setId(uuidGenerator.next());
                }
            }
        }

        regionRepository.addItem(region);
        return region;
    }

    @Override
    public Region updateRegion(Region region) {
        if (region == null || region.getId() == null) {
            return null;
        }
        regionRepository.updateItem(region);
        return region;
    }

    @Override
    public boolean deleteRegion(Region region) {
        if (region == null || region.getId() == null) {
            return false;
        }
        regionRepository.removeItem(region.getId());
        return true;
    }

    @Override
    public boolean deleteRegion(String regionId) {
        if (regionId == null) {
            return false;
        }
        regionRepository.removeItem(regionId);
        return true;
    }

    @Override
    public Region removeUrgencyCategory(Region region, UrgencyCategory category) {
        if (category == null || region == null || region.getUrgencyCategories() == null || region.getUrgencyCategories().isEmpty()) {
            return region;
        }

        region.setUrgencyCategories(removeUrgencyCategory(region.getUrgencyCategories(), category));

        updateRegion(region);
        return region;
    }

    @Override
    public Hospital createDepartment(Hospital hospital, Department department) {
        if (hospital == null || department == null) {
            return null;
        }

        if (hospital.getDepartments() == null) {
            hospital.setDepartments(new ArrayList<Department>());
        }

        if (!checkDepartmentName(OPERATION_CREATE, hospital, department)) {
            return null;
        }

        if (department.getId() == null) {
            department.setId(uuidGenerator.next());
        }

        hospital.getDepartments().add(department);

        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital createWard(Hospital hospital, Ward ward) {

        if (hospital == null || ward == null) {
            return null;
        }

        if (hospital.getWards() == null) {
            hospital.setWards(new ArrayList<Ward>());
        }

        if (!checkWardName(OPERATION_CREATE, hospital, ward)) {
            return null;
        }

        if (ward.getId() == null) {
            ward.setId(uuidGenerator.next());
        }

        hospital.getWards().add(ward);

        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital createSpecialistType(Hospital hospital, SpecialistType type) {
        if (hospital == null || type == null) {
            return null;
        }

        if (hospital.getSpecialistTypes() == null) {
            hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        }

        if (!checkSpecialistTypeName(OPERATION_CREATE, hospital, type)) {
            return null;
        }

        if (type.getId() == null) {
            type.setId(uuidGenerator.next());
        }

        hospital.getSpecialistTypes().add(type);

        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital updateDepartments(Hospital hospital, List<Department> departments) {
        if (hospital == null) {
            return null;
        }

        // avoid duplicate department names
        if (departments != null) {
            for (Department dep : departments) {
                if (!checkDepartmentName(OPERATION_UPDATE, departments, dep)) {
                    return null;
                }

                if (dep.getId() == null) {
                    dep.setId(uuidGenerator.next());
                }
            }
        }

        hospital.setDepartments(departments);
        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital updateWards(Hospital hospital, List<Ward> wards) {
        if (hospital == null) {
            return null;
        }

        // avoid duplicates
        if (wards != null) {
            for (Ward ward : wards) {
                if (!checkWardName(OPERATION_UPDATE, wards, ward)) {
                    return null;
                }

                if (ward.getId() == null) {
                    ward.setId(uuidGenerator.next());
                }
            }
        }

        hospital.setWards(wards);
        updateHospital(hospital);
        return hospital;

    }

    @Override
    public Hospital updateSpecialistTypes(Hospital hospital, List<SpecialistType> types) {
        if (hospital == null) {
            return null;
        }

        // avoid duplicates
        if (types != null) {
            for (SpecialistType type : types) {
                if (!checkSpecialistTypeName(OPERATION_UPDATE, types, type)) {
                    return null;
                }

                if (type.getId() == null) {
                    type.setId(uuidGenerator.next());
                }
            }
        }

        hospital.setSpecialistTypes(types);
        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Hospital updateUrgencyCategories(Hospital hospital, List<UrgencyCategory> categories) {
        if (hospital == null) {
            return null;
        }

        if (categories != null) {
            for (UrgencyCategory cat : categories) {
                // fix id of categories
                if (cat.getId() != null) {
                    cat.setId(uuidGenerator.next());
                }

                // fix JS rounding issues
                if (cat.getKpiTargets() != null) {
                    for (KpiTarget target : cat.getKpiTargets()) {
                        // round to 3 decimal places, e.g. 0.875 for 87.5%
                        target.setRequiredOnTimePerformance(Math.round(target.getRequiredOnTimePerformance() * 1000) / 1000.0);
                    }
                }
            }
        }

        hospital.setUrgencyCategories(categories);
        updateHospital(hospital);
        return hospital;
    }

    @Override
    public Region updateUrgencyCategories(Region region, List<UrgencyCategory> categories) {
        if (region == null) {
            return null;
        }

        if (categories != null) {
            for (UrgencyCategory cat : categories) {
                if (cat.getId() != null) {
                    cat.setId(uuidGenerator.next());
                }
            }
        }

        region.setUrgencyCategories(categories);
        updateRegion(region);
        return region;
    }

    /*
     * HELPER METHODS
     */


    /**
     * Removes a specific urgency category from a list of existing urgency categories
     *
     * @param list - the original list from which to remove the item
     * @param categoryToRemove  - the item to remove
     * @return - the original list without the category that was removed
     */
    public List<UrgencyCategory> removeUrgencyCategory(List<UrgencyCategory> list, UrgencyCategory categoryToRemove) {
        if (list == null) {
            return null;
        } else if (categoryToRemove == null) {
            return list;
        }

        List<UrgencyCategory> result = new ArrayList<>();
        for (UrgencyCategory c : list) {
            if (!c.getLabel().equals(categoryToRemove.getLabel())) {
                result.add(c);
            }
        }

        return result;
    }

    /**
     * Retrieves the effective KPI targets for a given urgency category. This will take the regions KPIs and apply any
     * hospital-speicifc overwrites to the KPI targets.
     *
     * @param regionCategory   - the urgency category within the region
     * @param hospitalCategory - the hospital override of the urgency category, if it exists.
     * @return - the effective list of KPIs for this given urgency category.
     */
    protected List<KpiTarget> getKpiTargets(UrgencyCategory regionCategory, UrgencyCategory hospitalCategory) {
        Map<String, KpiTarget> kpiMap = new HashMap<>();
        for (KpiTarget t : regionCategory.getKpiTargets()) {
            kpiMap.put(t.getInterval() + "," + t.getNumberOfPoints(), t);
        }

        if (hospitalCategory.getKpiTargets() != null) {
            for (KpiTarget hospitalTarget : hospitalCategory.getKpiTargets()) {
                KpiTarget regionTarget = kpiMap.get(hospitalTarget.getInterval() + "," + hospitalTarget.getNumberOfPoints());
                if (regionTarget == null) {
                    kpiMap.put(hospitalTarget.getInterval() + "," + hospitalTarget.getNumberOfPoints(), hospitalTarget);
                    continue;
                }

                if (regionTarget.getRequiredOnTimePerformance() != hospitalTarget.getRequiredOnTimePerformance()) {
                    regionTarget.setRequiredOnTimePerformance(hospitalTarget.getRequiredOnTimePerformance());
                }
            }
        }

        List<KpiTarget> result = new ArrayList<>();
        for (KpiTarget t : kpiMap.values()) {
            result.add(t);
        }
        return result;
    }

    /**
     * Checks the department name of a specific department against existing department names of a hospital.
     *
     * @param mode - CREATE or UPDATE
     * @param hospital    - the hospital holding the departments
     * @param department    - the new department entry
     * @return - in CREATE mode, this returns true, if no other department is named like the provided. In UPDATE mode,
     * there is one department that can have the same name (the department to update)
     */
    protected boolean checkDepartmentName(String mode, Hospital hospital, Department department) {
        return checkDepartmentName(mode, hospital.getDepartments(), department);
    }


    /**
     * Checks the department name of a specific department against existing department names of a hospital.
     *
     * @param mode - CREATE or UPDATE
     * @param currentList    - the current list of departments
     * @param department    - the new department entry
     * @return - in CREATE mode, this returns true, if no other department is named like the provided. In UPDATE mode,
     * there is one department that can have the same name (the department to update)
     */
    protected boolean checkDepartmentName(String mode, List<Department> currentList, Department department) {
        int match = 0;
        for (Department hd : currentList) {
            if (hd.getName().equals(department.getName())) {
                match++;
            }
        }

        return (OPERATION_CREATE.equals(mode) && match == 0) || (OPERATION_UPDATE.equals(mode) && match <= 1);
    }

    /**
     * Check the ward name of a specific ward against existing ward names of a hospital
     *
     * @param mode - CREATE or UPDATE
     * @param hospital    - the hospital holding ward info
     * @param ward    - the new ward to be checked for duplicates
     * @return - true or false, depending on the existence of another ward like the provided.
     */
    protected boolean checkWardName(String mode, Hospital hospital, Ward ward) {
        return checkWardName(mode, hospital.getWards(), ward);
    }


    /**
     * Check the ward name of a specific ward against existing ward names of a hospital
     *
     * @param mode - CREATE or UPDATE
     * @param currentList    - the current list of wards
     * @param ward    - the new ward to be checked for duplicates
     * @return - true or false, depending on the existence of another ward like the provided.
     */
    protected boolean checkWardName(String mode, List<Ward> currentList, Ward ward) {
        int match = 0;
        for (Ward hw : currentList) {
            if (hw.getName().equals(ward.getName())) {
                match++;
            }
        }

        return (OPERATION_CREATE.equals(mode) && match == 0) || (OPERATION_UPDATE.equals(mode) && match <= 1);
    }

    /**
     * Checks a specialist types label and department name against existing specialist types of a hospital.
     *
     * @param mode - CREATE or UPDATE
     * @param hospital    - the hospital holding specialist types
     * @param type - the new specialist type to check
     * @return - true or false, depending on the existence of another specialist type like the provided.
     */
    protected boolean checkSpecialistTypeName(String mode, Hospital hospital, SpecialistType type) {
        return checkSpecialistTypeName(mode, hospital.getSpecialistTypes(), type);
    }

    /**
     * Checks a specialist types label and department name against existing specialist types of a hospital.
     *
     * @param mode - CREATE or UPDATE
     * @param currentList    - the current list of specialist types
     * @param type - the new specialist type to check
     * @return - true or false, depending on the existence of another specialist type like the provided.
     */
    protected boolean checkSpecialistTypeName(String mode, List<SpecialistType> currentList, SpecialistType type) {
        int match = 0;
        for (SpecialistType st : currentList) {
            if (st.getLabel().equals(type.getLabel()) && st.getDepartment().equals(type.getDepartment())) {
                match++;
            }
        }

        return (OPERATION_CREATE.equals(mode) && match == 0) || (OPERATION_UPDATE.equals(mode) && match <= 1);
    }

    /*
     * Service Methods
     */

    @Override
    protected void doBind(Environment environment) throws Exception {
        this.regionRepository = (RegionRepository) environment.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        this.hospitalRepository = (HospitalRepository) environment.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
    }

    @Override
    protected void doRelease() throws Exception {
        this.regionRepository = null;
        this.hospitalRepository = null;
    }

    /*
     * GETTER / SETTER
     */

    /**
     * Getter method for the hospital repository
     * @return - the hospital repository or null
     */
    public HospitalRepository getHospitalRepository() {
        return hospitalRepository;
    }

    /**
     * Setter method for the hospital repository
     * @param hospitalRepository - the new hospital repository
     */
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * Getter method for the region repository
     * @return - the region repository or null
     */
    public RegionRepository getRegionRepository() {
        return regionRepository;
    }

    /**
     * Setter method for the region repository
     * @param regionRepository - the new region repository
     */
    public void setRegionRepository(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }


}
