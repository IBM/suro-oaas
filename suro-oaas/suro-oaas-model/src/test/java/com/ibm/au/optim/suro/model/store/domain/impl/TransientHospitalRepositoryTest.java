package com.ibm.au.optim.suro.model.store.domain.impl;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientHospitalRepository;

/**
 * @author Peter Ilfrich
 */
public class TransientHospitalRepositoryTest {

	@Test
    public void testCreateItem() {
        TransientHospitalRepository repo = new TransientHospitalRepository();
        Hospital hospital = new Hospital("region", "label", null, null, null, null, 0, 0, 0, 0);

        Assert.assertEquals("region", hospital.getRegionId());
        Assert.assertEquals("label", hospital.getName());

        Assert.assertNull(hospital.getId());
        Assert.assertNull(hospital.getDepartments());
        Assert.assertNull(hospital.getSpecialistTypes());
        Assert.assertNull(hospital.getUrgencyCategories());
        Assert.assertNull(hospital.getWards());

        Assert.assertEquals(0, hospital.getIcuBedCount());
        Assert.assertEquals(0, hospital.getSessionDuration());
        Assert.assertEquals(0, hospital.getTheatreCount());
        Assert.assertEquals(0, hospital.getTheatreSessionsPerDay());

        Assert.assertEquals(0, repo.getAll().size());
    }

	@Test
    public void testFindByRegion() {
        TransientHospitalRepository repo = new TransientHospitalRepository();
        Hospital hospital = new Hospital("region", "label", null, null, null, null, 0, 0, 0, 0);
        repo.addItem(hospital);

        List<Hospital> fetched = repo.findByRegion("region");
        Assert.assertEquals(1, fetched.size());
        Assert.assertEquals("label", fetched.get(0).getName());

        Hospital hospital2 = new Hospital("region", "label-2", null, null, null, null, 0, 0, 0, 0);
        repo.addItem(hospital2);
        Hospital hospital3 = new Hospital("another-region", "some-name", null, null, null, null, 0, 0, 0, 0);
        repo.addItem(hospital3);
        Assert.assertEquals(3, repo.getAll().size());

        fetched = repo.findByRegion("region");
        Assert.assertEquals(2, fetched.size());
    }

	@Test
    public void testFindByRegionDoesntExist() {
        TransientHospitalRepository repo = new TransientHospitalRepository();
        Hospital hospital = new Hospital("region", "label", null, null, null, null, 0, 0, 0, 0);
        repo.addItem(hospital);

        Assert.assertEquals(1, repo.getAll().size());
        Assert.assertEquals(0, repo.findByRegion("not-existing").size());
        Assert.assertEquals(0, repo.findByRegion(null).size());
    }
}
