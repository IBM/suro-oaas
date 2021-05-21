package com.ibm.au.optim.suro.model.entities.domain;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.KpiTarget;

/**
 * @author Peter Ilfrich
 */
public class KpiTargetTest {

	@Test
    public void testCreateNew() {
    	
        KpiTarget target = new KpiTarget();
        Assert.assertEquals(0, target.getInterval());
        Assert.assertEquals(0, target.getNumberOfPoints());
        Assert.assertEquals(0.0, target.getRequiredOnTimePerformance(), 0.0000000001);
    }

	@Test
    public void testCreateComplex() {
    	
        KpiTarget target = new KpiTarget(1, 2, 3.0);
        Assert.assertEquals(1, target.getInterval());
        Assert.assertEquals(2, target.getNumberOfPoints());
        Assert.assertEquals(3.0, target.getRequiredOnTimePerformance(), 0.000000001);
    }

	@Test
    public void testSetters() {
    	
        KpiTarget target = new KpiTarget();
        target.setInterval(5);
        target.setNumberOfPoints(15);
        target.setRequiredOnTimePerformance(35);

        Assert.assertEquals(5, target.getInterval());
        Assert.assertEquals(15, target.getNumberOfPoints());
        Assert.assertEquals(35.0, target.getRequiredOnTimePerformance(), 0.0000000001);
    }
}
