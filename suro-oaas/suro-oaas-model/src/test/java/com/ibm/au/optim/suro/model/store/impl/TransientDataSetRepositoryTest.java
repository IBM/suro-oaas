package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.store.DataSetRepository;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class TransientDataSetRepositoryTest  {

	@Test
    public void testConstructor() {
		
        DataSetRepository repo = new TransientDataSetRepository();

        DataSet ds = new DataSet("set-name", "model-id", null);
        Assert.assertEquals(0, repo.getAll().size());

        Assert.assertEquals("set-name", ds.getLabel());
        Assert.assertEquals("model-id", ds.getModelId());
        Assert.assertNull(ds.getId());
        Assert.assertNull(ds.getDatFile());
    }

	@Test
    public void testAddItem() {
		
        DataSetRepository repo = new TransientDataSetRepository();

        DataSet ds = new DataSet("set-name", "model-id", null);
        repo.addItem(ds);

        Assert.assertNotNull(ds.getId());
        Assert.assertEquals(1, repo.getAll().size());
        Assert.assertNotNull(repo.getItem(ds.getId()));

        Assert.assertEquals("set-name", repo.getItem(ds.getId()).getLabel());
    }
	
	@Test
    public void testUpdateItem() {
		
        DataSetRepository repo = new TransientDataSetRepository();

        DataSet ds = new DataSet("set-name", "model-id", null);
        repo.addItem(ds);

        ds.setLabel("new-label");
        repo.updateItem(ds);

        Assert.assertEquals("new-label", repo.getItem(ds.getId()).getLabel());
    }
	
	@Test
    public void testFindByModelId() {
		
        DataSetRepository repo = new TransientDataSetRepository();
        DataSet ds1 = new DataSet("anotherSet", "another-model-id", null);
        DataSet ds2 = new DataSet("defaultSet", "model-id", null);

        repo.addItem(ds1);
        repo.addItem(ds2);

        Assert.assertEquals(1, repo.findByModelId("model-id").size());
        Assert.assertEquals(0, repo.findByModelId("not-existing").size());
        Assert.assertEquals(0, repo.findByModelId(null).size());

        List<DataSet> ds = repo.findByModelId("model-id");
        DataSet compare = ds.get(0);

        Assert.assertEquals(ds2.getId(), compare.getId());
        Assert.assertEquals(ds2.getModelId(), compare.getModelId());
    }

}
