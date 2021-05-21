package com.ibm.au.optim.suro.model.store.domain.impl;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientRegionRepository;



/**
 * @author Peter Ilfrich
 */
public class TransientRegionRepositoryTest {

	@Test
    public void testCreateItem() {
    	
        TransientRegionRepository repo = new TransientRegionRepository();
        Region r = new Region("some-region");

        Assert.assertNull(r.getUrgencyCategories());
        Assert.assertNull(r.getId());
        Assert.assertNull(r.getIntervalType());
        Assert.assertEquals(0, r.getFirstIntervalStart());

        Assert.assertEquals(0, repo.getAll().size());
    }

	@Test
    public void testFindByName() {
    	
        TransientRegionRepository repo = new TransientRegionRepository();
        Region r = new Region("some-region");
        r.setIntervalType(Region.INTERVAL_QUARTERLY);
        repo.addItem(r);

        Region retrieved = repo.findByName("some-region");
        Assert.assertNotNull(retrieved);
        Assert.assertEquals(Region.INTERVAL_QUARTERLY, retrieved.getIntervalType());
    }

	@Test
    public void testFindByNameDoesntExist() {
        TransientRegionRepository repo = new TransientRegionRepository();
        Region r = new Region("some-region");
        repo.addItem(r);

        Assert.assertNull(repo.findByName("not-existing"));
        Assert.assertNull(repo.findByName(null));
    }


}
