package com.ibm.au.optim.suro.model.entities.domain;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;

/**
 * @author Peter Ilfrich
 */
public class RegionTest {

	@Test
    public void testSimpleConstructor() {
    	
        Region r = new Region();
        Assert.assertNull(r.getId());
        Assert.assertNull(r.getUrgencyCategories());
        Assert.assertNull(r.getName());
        Assert.assertEquals(0, r.getFirstIntervalStart());
        Assert.assertNull(r.getIntervalType());
    }

	@Test
    public void testComplexConstructor() {
    	
        long intervalStart = System.currentTimeMillis();
        String intervalType = Region.INTERVAL_MONTHLY;
        String name = "Victoria/Australia";
        List<UrgencyCategory> categories = new ArrayList<>();
        categories.add(new UrgencyCategory(null, "cat1", 1, 2, 3));

        Region r = new Region(name, categories, intervalType, intervalStart);

        Assert.assertNull(r.getId());
        Assert.assertEquals(name, r.getName());
        Assert.assertEquals(intervalType, r.getIntervalType());
        Assert.assertEquals(intervalStart, r.getFirstIntervalStart());
        Assert.assertEquals(1, r.getUrgencyCategories().size());
    }

	@Test
    public void testSetters() {
    	
        Region r = new Region();

        long intervalStart = System.currentTimeMillis();
        String intervalType = Region.INTERVAL_MONTHLY;
        String name = "Victoria/Australia";
        List<UrgencyCategory> categories = new ArrayList<>();
        categories.add(new UrgencyCategory(null, "cat1", 1, 2, 3));

        r.setName(name);
        r.setIntervalType(intervalType);
        r.setFirstIntervalStart(intervalStart);
        r.setUrgencyCategories(categories);
        r.setId("id");

        Assert.assertEquals("id", r.getId());
        Assert.assertEquals(name, r.getName());
        Assert.assertEquals(intervalType, r.getIntervalType());
        Assert.assertEquals(intervalStart, r.getFirstIntervalStart());
        Assert.assertEquals(1, r.getUrgencyCategories().size());
    }
}
