package com.ibm.au.optim.suro.model.store.impl;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.store.RunDetailsRepository;


/**
 * @author Peter Ilfrich
 */
public class TransientRunLogRepositoryTest {

	@Test
    public void testCreateItem() {
        RunDetailsRepository repo = new TransientOptimizationResultRepository();
        Assert.assertEquals(0, repo.getAll().size());
        
        RunDetails r = new RunDetails();
        
        repo.addItem(r);
        Assert.assertEquals(1, repo.getAll().size());
    }

    @Test
    public void testFindByRunId() {
        RunDetailsRepository repo = new TransientOptimizationResultRepository();
        RunDetails r = new RunDetails();
        r.setRunId("run-id");
        r.setGap(1.337);

        RunDetails r2 = new RunDetails();
        r2.setRunId("another-id");
        r2.setGap(0.0);

        repo.addItem(r);
        repo.addItem(r2);

        Assert.assertEquals(2, repo.getAll().size());

        Assert.assertNull(repo.findByRunId(null));
        Assert.assertNull(repo.findByRunId("not-existing"));

        Assert.assertEquals(1.337, repo.findByRunId("run-id").getGap(), 0.000000000001);
    }
}
