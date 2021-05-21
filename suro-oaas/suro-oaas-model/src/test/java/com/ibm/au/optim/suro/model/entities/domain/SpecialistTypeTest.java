package com.ibm.au.optim.suro.model.entities.domain;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.SpecialistType;


/**
 * @author Peter Ilfrich
 */
public class SpecialistTypeTest {
	
	@Test
    public void testCreateNew() {
    	
        SpecialistType t = new SpecialistType();
        Assert.assertNull(t.getLabel());
        Assert.assertNull(t.getDepartment());
        Assert.assertNull(t.getId());
    }
    
    @Test
    public void testCreateComplex() {
        
    	SpecialistType t = new SpecialistType("Cardiologist", "some-department");
        Assert.assertEquals("Cardiologist", t.getLabel());
        Assert.assertEquals("some-department", t.getDepartment());
        Assert.assertNull(t.getId());
    }

    @Test
    public void testSetters() {
    	
        SpecialistType t = new SpecialistType();

        t.setDepartment("new-department");
        t.setLabel("new-label");

        Assert.assertEquals("new-department", t.getDepartment());
        Assert.assertEquals("new-label", t.getLabel());

        t.setId("foobar");
        Assert.assertEquals("foobar", t.getId());
        t.setId(null);
        Assert.assertNull(t.getId());
    }
}
