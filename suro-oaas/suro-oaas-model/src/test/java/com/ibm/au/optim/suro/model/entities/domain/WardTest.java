package com.ibm.au.optim.suro.model.entities.domain;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.Ward;

/**
 * @author Peter Ilfrich
 */
public class WardTest {

	@Test
    public void testCreateNew() {
        
    	Ward w = new Ward();
    	Assert.assertNull(w.getName());
        Assert.assertEquals(0, w.getBedsCount());
        Assert.assertNull(w.getId());
    }
    
    @Test
    public void testCreateComplex() {
        
    	Ward w = new Ward("name", 15);
    	Assert.assertEquals("name", w.getName());
    	Assert.assertEquals(15, w.getBedsCount());
    	Assert.assertNull(w.getId());
    }
    
    @Test
    public void testSetters() {
    	
        Ward w = new Ward("name", 15);
        Assert.assertEquals("name", w.getName());
        Assert.assertEquals(15, w.getBedsCount());

        w.setName("new-name");
        w.setBedsCount(4);

        Assert.assertEquals("new-name", w.getName());
        Assert.assertEquals(4, w.getBedsCount());

        w.setId("foobar");
        Assert.assertEquals("foobar", w.getId());
        w.setId(null);
        Assert.assertNull(w.getId());
    }
}
