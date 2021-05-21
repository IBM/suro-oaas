package com.ibm.au.optim.suro.model.entities.domain;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.Department;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.SpecialistType;
import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;
import com.ibm.au.optim.suro.model.entities.domain.Ward;

/**
 * @author Peter Ilfrich
 */
public class HospitalTest {

	@Test
    public void testSimpleConstructor() {
    	
        Hospital h = new Hospital();
        Assert.assertNull(h.getName());
        Assert.assertNull(h.getUrgencyCategories());
        Assert.assertNull(h.getSpecialistTypes());
        Assert.assertNull(h.getDepartments());
        Assert.assertNull(h.getRegionId());
        Assert.assertNull(h.getId());
        Assert.assertNull(h.getWards());
        Assert.assertEquals(0, h.getIcuBedCount());
        Assert.assertEquals(0, h.getTheatreCount());
        Assert.assertEquals(0, h.getTheatreSessionsPerDay());
        Assert.assertEquals(0, h.getSessionDuration());

        String regionId = "Australia/Victoria";
        String name = "General Hospital";
        h = new Hospital(regionId, name);

        Assert.assertEquals(name, h.getName());
        Assert.assertEquals(regionId, h.getRegionId());

        Assert.assertNull(h.getUrgencyCategories());
        Assert.assertNull(h.getSpecialistTypes());
        Assert.assertNull(h.getDepartments());
        Assert.assertNull(h.getId());
        Assert.assertNull(h.getWards());
        Assert.assertEquals(0, h.getIcuBedCount());
        Assert.assertEquals(0, h.getTheatreCount());
        Assert.assertEquals(0, h.getTheatreSessionsPerDay());
        Assert.assertEquals(0, h.getSessionDuration());
    }

	@Test
    public void testFullConstructor() {
    	
        String name = "name";
        String regionId = "region-id";
        List<Ward> wards = new ArrayList<>();
        wards.add(new Ward("ward-name", 16));
        List<Department> departments = new ArrayList<>();
        departments.add(new Department("department-name", 4));
        List<UrgencyCategory> categories = new ArrayList<>();
        categories.add(new UrgencyCategory(null, "Cat1", 1, 2, 3));
        List<SpecialistType> types = new ArrayList<>();
        types.add(new SpecialistType("surgeon-type", "department-name"));
        int icuCount = 1;
        int sessionsPerDay = 2;
        int sessionDuration = 3;
        int theatreCount = 4;
        Hospital h = new Hospital(regionId, name, wards, departments, types, categories, icuCount, sessionsPerDay, sessionDuration, theatreCount);

        Assert.assertNull(h.getId());
        Assert.assertEquals(regionId, h.getRegionId());
        Assert.assertEquals(name, h.getName());
        Assert.assertEquals(icuCount, h.getIcuBedCount());
        Assert.assertEquals(sessionDuration, h.getSessionDuration());
        Assert.assertEquals(sessionsPerDay, h.getTheatreSessionsPerDay());
        Assert.assertEquals(theatreCount, h.getTheatreCount());
        Assert.assertEquals(1, h.getWards().size());
        Assert.assertEquals(1, h.getDepartments().size());
        Assert.assertEquals(1, h.getUrgencyCategories().size());
        Assert.assertEquals(1, h.getSpecialistTypes().size());
    }

	@Test
    public void testSetters() {
    	
        String name = "name";
        String regionId = "region-id";
        List<Ward> wards = new ArrayList<>();
        wards.add(new Ward("ward-name", 16));
        List<Department> departments = new ArrayList<>();
        departments.add(new Department("department-name", 4));
        List<UrgencyCategory> categories = new ArrayList<>();
        categories.add(new UrgencyCategory(null, "Cat1", 1, 2, 3));
        List<SpecialistType> types = new ArrayList<>();
        types.add(new SpecialistType("surgeon-type", "department-name"));
        int icuCount = 1;
        int sessionsPerDay = 2;
        int sessionDuration = 3;
        int theatreCount = 4;

        Hospital h = new Hospital();
        h.setName(name);
        h.setRegionId(regionId);
        h.setWards(wards);
        h.setDepartments(departments);
        h.setUrgencyCategories(categories);
        h.setSpecialistTypes(types);
        h.setIcuBedCount(icuCount);
        h.setTheatreSessionsPerDay(sessionsPerDay);
        h.setSessionDuration(sessionDuration);
        h.setTheatreCount(theatreCount);
        h.setId("id");

        Assert.assertEquals("id", h.getId());
        Assert.assertEquals(regionId, h.getRegionId());
        Assert.assertEquals(name, h.getName());
        Assert.assertEquals(icuCount, h.getIcuBedCount());
        Assert.assertEquals(sessionDuration, h.getSessionDuration());
        Assert.assertEquals(sessionsPerDay, h.getTheatreSessionsPerDay());
        Assert.assertEquals(theatreCount, h.getTheatreCount());
        Assert.assertEquals(1, h.getWards().size());
        Assert.assertEquals(1, h.getDepartments().size());
        Assert.assertEquals(1, h.getUrgencyCategories().size());
        Assert.assertEquals(1, h.getSpecialistTypes().size());
    }
}
