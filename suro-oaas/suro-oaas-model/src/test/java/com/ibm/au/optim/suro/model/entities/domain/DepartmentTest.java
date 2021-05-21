package com.ibm.au.optim.suro.model.entities.domain;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.Department;


/**
 * @author Peter Ilfrich
 */
public class DepartmentTest {

	@Test
    public void testCreateNew() {
    	
        Department dep = new Department();
        Assert.assertNull(dep.getName());
        Assert.assertEquals(0, dep.getMaxSimultaneousSessions());
        Assert.assertNull(dep.getId());
    }

	@Test
    public void testFullConstructor() {
    	
        Department dep = new Department("some-name", 15);
        Assert.assertEquals("some-name", dep.getName());
        Assert.assertEquals(15, dep.getMaxSimultaneousSessions());
        Assert.assertNull(dep.getId());
    }

	@Test
    public void testSetters() {
    	
        Department dep = new Department();
        dep.setMaxSimultaneousSessions(15);
        dep.setName("another name");

        Assert.assertEquals(15, dep.getMaxSimultaneousSessions());
        Assert.assertEquals("another name", dep.getName());

        dep.setName(null);
        Assert.assertNull(dep.getName());

        dep.setId("foobar");
        Assert.assertEquals("foobar", dep.getId());
        dep.setId(null);
        Assert.assertNull(dep.getId());
    }
}
