package com.ibm.au.optim.suro.model.entities.domain;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.KpiTarget;
import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;

/**
 * @author Peter Ilfrich
 */
public class UrgencyCategoryTest {

	@Test
    public void testCreateNew() {
    	
        UrgencyCategory cat = new UrgencyCategory();
        Assert.assertNull(cat.getLabel());
        Assert.assertNull(cat.getKpiTargets());
        Assert.assertEquals(0, cat.getMaxWaitListStay());
        Assert.assertEquals(0, cat.getMinPointsRequired());
        Assert.assertEquals(0, cat.getPossiblePoints());
        Assert.assertNull(cat.getId());
    }

	@Test
    public void testCreateComplex() {

        List<KpiTarget> targets = new ArrayList<>();
        targets.add(new KpiTarget(1, 1, 1));
        targets.add(new KpiTarget(2, 2, 2));

        UrgencyCategory cat = new UrgencyCategory(targets, "some-label", 7, 1, 3);
        Assert.assertEquals(2, cat.getKpiTargets().size());
        Assert.assertEquals("some-label", cat.getLabel());
        Assert.assertEquals(7, cat.getMaxWaitListStay());
        Assert.assertEquals(1, cat.getMinPointsRequired());
        Assert.assertEquals(3, cat.getPossiblePoints());
        Assert.assertNull(cat.getId());
    }

	@Test
    public void testSetters() {
    	
        List<KpiTarget> targets = new ArrayList<>();
        targets.add(new KpiTarget(1, 1, 1));
        targets.add(new KpiTarget(2, 2, 2));

        UrgencyCategory cat = new UrgencyCategory();

        cat.setKpiTargets(targets);
        cat.setLabel("new-label");
        cat.setMinPointsRequired(2);
        cat.setPossiblePoints(3);
        cat.setMaxWaitListStay(14);

        Assert.assertEquals(2, cat.getKpiTargets().size());
        Assert.assertEquals("new-label", cat.getLabel());
        Assert.assertEquals(2, cat.getMinPointsRequired());
        Assert.assertEquals(3, cat.getPossiblePoints());
        Assert.assertEquals(14, cat.getMaxWaitListStay());

        cat.setId("foobar");
        Assert.assertEquals("foobar", cat.getId());
        cat.setId(null);
        Assert.assertNull(cat.getId());
    }
}
