package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientHospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientRegionRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Peter Ilfrich
 */
public class HospitalSubItemIdPreparerTest extends TestCase {

    public void testCheck() throws Exception {
        HospitalSubItemIdPreparer preparer = new HospitalSubItemIdPreparer();
        Environment env = createEnvironment();

        HospitalRepository hospitalRepo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        Hospital hospital = new Hospital();
        hospitalRepo.addItem(hospital);

        assertFalse(preparer.check(env));

        hospital.setWards(new ArrayList<Ward>());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.getWards().add(new Ward());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.check(env));

        hospital.getWards().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.setDepartments(new ArrayList<Department>());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.getDepartments().add(new Department());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.check(env));

        hospital.getDepartments().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.getSpecialistTypes().add(new SpecialistType());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.check(env));

        hospital.getSpecialistTypes().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        hospital.getUrgencyCategories().add(new UrgencyCategory());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.check(env));

        hospital.getUrgencyCategories().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.check(env));

        Region region = new Region();
        RegionRepository regionRepo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        regionRepo.addItem(region);
        assertFalse(preparer.check(env));

        region.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        regionRepo.updateItem(region);
        assertFalse(preparer.check(env));

        region.getUrgencyCategories().add(new UrgencyCategory());
        regionRepo.updateItem(region);
        assertTrue(preparer.check(env));

        region.getUrgencyCategories().get(0).setId("foo");
        regionRepo.updateItem(region);
        assertFalse(preparer.check(env));

        region.getUrgencyCategories().add(new UrgencyCategory());
        regionRepo.updateItem(region);
        assertTrue(preparer.check(env));

        region.getUrgencyCategories().get(1).setId("foo");
        regionRepo.updateItem(region);
        assertFalse(preparer.check(env));
    }


    public void testExecute() throws Exception {
        // prepare basics
        HospitalSubItemIdPreparer preparer = new HospitalSubItemIdPreparer();
        Environment env = createEnvironment();
        // prepare hospital
        Hospital hospital = new Hospital();
        hospital.setDepartments(new ArrayList<Department>());
        hospital.getDepartments().add(new Department());
        hospital.setWards(new ArrayList<Ward>());
        hospital.getWards().add(new Ward());
        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        hospital.getSpecialistTypes().add(new SpecialistType());
        hospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        hospital.getUrgencyCategories().add(new UrgencyCategory());
        // add hospital
        HospitalRepository hospitalRepo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        hospitalRepo.addItem(hospital);
        // prepare region
        Region region = new Region();
        region.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        region.getUrgencyCategories().add(new UrgencyCategory());
        // add region
        RegionRepository regionRepo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        regionRepo.addItem(region);


        // execute the preparer
        preparer.execute(env);

        // assert repo content
        assertEquals(1, hospitalRepo.getAll().size());
        hospital = hospitalRepo.getAll().get(0);
        assertEquals(1, regionRepo.getAll().size());
        region = regionRepo.getAll().get(0);


        // make assertions about the number of elements
        assertEquals(1, hospital.getUrgencyCategories().size());
        assertEquals(1, hospital.getWards().size());
        assertEquals(1, hospital.getDepartments().size());
        assertEquals(1, hospital.getSpecialistTypes().size());
        assertEquals(1, region.getUrgencyCategories().size());

        // assert the ids exist
        assertNotNull(hospital.getUrgencyCategories().get(0).getId());
        assertNotNull(hospital.getWards().get(0).getId());
        assertNotNull(hospital.getDepartments().get(0).getId());
        assertNotNull(hospital.getSpecialistTypes().get(0).getId());
        assertNotNull(region.getUrgencyCategories().get(0).getId());

        assertFalse(preparer.check(env));
        preparer.execute(env);

        assertEquals(1, hospitalRepo.getAll().size());
        hospital = hospitalRepo.getAll().get(0);
        assertEquals(1, regionRepo.getAll().size());
        region = regionRepo.getAll().get(0);


        // make assertions about the number of elements
        assertEquals(1, hospital.getUrgencyCategories().size());
        assertEquals(1, hospital.getWards().size());
        assertEquals(1, hospital.getDepartments().size());
        assertEquals(1, hospital.getSpecialistTypes().size());
        assertEquals(1, region.getUrgencyCategories().size());

        // assert the ids exist
        assertNotNull(hospital.getUrgencyCategories().get(0).getId());
        assertNotNull(hospital.getWards().get(0).getId());
        assertNotNull(hospital.getDepartments().get(0).getId());
        assertNotNull(hospital.getSpecialistTypes().get(0).getId());
        assertNotNull(region.getUrgencyCategories().get(0).getId());

        // reset region
        region.setUrgencyCategories(null);
        regionRepo.updateItem(region);
        // reset hospital
        hospital.setUrgencyCategories(null);
        hospital.setWards(null);
        hospital.setDepartments(null);
        hospital.setSpecialistTypes(null);
        hospitalRepo.updateItem(hospital);
        // execute -> will not modify any lists
        preparer.execute(env);
        // assert size of repo
        assertEquals(1, hospitalRepo.getAll().size());
        hospital = hospitalRepo.getAll().get(0);
        assertEquals(1, regionRepo.getAll().size());
        region = regionRepo.getAll().get(0);

        assertNull(hospital.getUrgencyCategories());
        assertNull(hospital.getWards());
        assertNull(hospital.getSpecialistTypes());
        assertNull(hospital.getDepartments());
        assertNull(region.getUrgencyCategories());

    }

    public void testValidation() throws Exception {
        HospitalSubItemIdPreparer preparer = new HospitalSubItemIdPreparer();
        Environment env = createEnvironment();

        HospitalRepository hospitalRepo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        Hospital hospital = new Hospital();
        hospitalRepo.addItem(hospital);

        assertTrue(preparer.validate(env));

        hospital.setWards(new ArrayList<Ward>());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.getWards().add(new Ward());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.validate(env));

        hospital.getWards().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.setDepartments(new ArrayList<Department>());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.getDepartments().add(new Department());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.validate(env));

        hospital.getDepartments().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.getSpecialistTypes().add(new SpecialistType());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.validate(env));

        hospital.getSpecialistTypes().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        hospital.getUrgencyCategories().add(new UrgencyCategory());
        hospitalRepo.updateItem(hospital);
        assertFalse(preparer.validate(env));

        hospital.getUrgencyCategories().get(0).setId("foo");
        hospitalRepo.updateItem(hospital);
        assertTrue(preparer.validate(env));

        Region region = new Region();
        RegionRepository regionRepo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        regionRepo.addItem(region);
        assertTrue(preparer.validate(env));

        region.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        regionRepo.updateItem(region);
        assertTrue(preparer.validate(env));

        region.getUrgencyCategories().add(new UrgencyCategory());
        regionRepo.updateItem(region);
        assertFalse(preparer.validate(env));

        region.getUrgencyCategories().get(0).setId("foo");
        regionRepo.updateItem(region);
        assertTrue(preparer.validate(env));

        region.getUrgencyCategories().add(new UrgencyCategory());
        regionRepo.updateItem(region);
        assertFalse(preparer.validate(env));

        region.getUrgencyCategories().get(1).setId("foo");
        regionRepo.updateItem(region);
        assertTrue(preparer.validate(env));
    }


    public void testFixHospitalSubElement() {
        HospitalSubItemIdPreparer preparer = new HospitalSubItemIdPreparer();
        preparer.fixHospitalSubElement(null);
        List<HospitalSubElement> list = new ArrayList<>();
        preparer.fixHospitalSubElement(list);
        assertEquals(0, list.size());
        list.add(new UrgencyCategory());

        preparer.fixHospitalSubElement(list);
        assertEquals(1, list.size());
        assertNotNull(list.get(0).getId());

        UrgencyCategory cat2 = new UrgencyCategory();
        cat2.setId("foobar");
        list.add(cat2);

        preparer.fixHospitalSubElement(list);
        assertEquals(2, list.size());
        assertEquals("foobar", list.get(1).getId());
        assertNotNull(list.get(0).getId());
    }

    public void testFixHospital() {
        HospitalSubItemIdPreparer preparer = new HospitalSubItemIdPreparer();
        Environment env = createEnvironment();

        Hospital hospital = new Hospital();
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        repo.addItem(hospital);

        preparer.fixHospital(hospital, env);
        hospital = repo.getItem(hospital.getId());
        assertNull(hospital.getUrgencyCategories());
        assertNull(hospital.getSpecialistTypes());
        assertNull(hospital.getDepartments());
        assertNull(hospital.getWards());

        hospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        hospital.setWards(new ArrayList<Ward>());
        hospital.setDepartments(new ArrayList<Department>());
        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        repo.updateItem(hospital);


        preparer.fixHospital(hospital, env);
        hospital = repo.getItem(hospital.getId());
        assertEquals(0, hospital.getUrgencyCategories().size());
        assertEquals(0, hospital.getSpecialistTypes().size());
        assertEquals(0, hospital.getDepartments().size());
        assertEquals(0, hospital.getWards().size());

    }

    /**
     * Sets up the environment with a region and hospital repository.
     * @return - the environment
     */
    private Environment createEnvironment() {
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);

        env.setAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE, new TransientHospitalRepository());
        env.setAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE, new TransientRegionRepository());

        return env;
    }
}
