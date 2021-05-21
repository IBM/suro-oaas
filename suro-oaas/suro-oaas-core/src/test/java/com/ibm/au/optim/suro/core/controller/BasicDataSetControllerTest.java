package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.optim.suro.core.controller.BasicDataSetController;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.impl.TransientDataSetRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class BasicDataSetControllerTest {

	/**
	 * 
	 */
    private DataSetController controller;
    /**
     * 
     */
    private Model defaultModel;
    /**
     * 
     */
    private InputStream datStream = new ByteArrayInputStream("dat-file-content".getBytes(StandardCharsets.UTF_8));

    /**
     * 
     */
    @Before
    public void setUp() throws Exception {

        this.controller = new BasicDataSetController();
        this.controller.setRepository(new TransientDataSetRepository());
        String label = "new-label";
        this.defaultModel = new Model(label, false);
    }

    /**
     * 
     */
    @After
    public void tearDown() throws Exception {

    	this.defaultModel = null;
    	this.controller = null;
    }

    /**
     * 
     */
    @Test
    public void testCreateDataSet() {
    	
    	this.controller.getRepository().removeAll();

        // add a new data set
        DataSet set = this.controller.createDataSet(defaultModel, "new-dataset", datStream);
        // verify insert and dat file
        Assert.assertNotNull(set);
        Assert.assertEquals(1, this.controller.getRepository().getAll().size());
        Assert.assertEquals("new-dataset", set.getLabel());
        Assert.assertNull(set.getDatFile());

        // add another data set without dat file
        DataSet noData = this.controller.createDataSet(defaultModel, "new-dataset", null);
        // verify insert
        Assert.assertNotNull(noData);
        Assert.assertEquals(2, this.controller.getRepository().getAll().size());
        Assert.assertNull(noData.getDatFile());
    }
    
    /**
     * 
     */
    @Test
    public void testGetDataSetWithoutAttachment() {
    	this.controller.getRepository().removeAll();

        DataSet set = this.controller.createDataSet(defaultModel, "new-dataset", datStream);
        Assert.assertNotNull(set);
        Assert.assertNull(set.getDatFile());

        // retrieve the dataset with dat file and verify information
        DataSet retrieved = this.controller.getDataSet(set.getId(), false);
        Assert.assertNotNull(retrieved);
        Assert.assertNull(retrieved.getDatFile());
        Assert.assertEquals("new-dataset", retrieved.getLabel());
    }

    /**
     * 
     */
    @Test
    public void testGetDataSetWithAttachment() {
    	this.controller.getRepository().removeAll();

        // create dataset with dat file
        DataSet set = this.controller.createDataSet(defaultModel, "new-dataset", datStream);
        Assert.assertNotNull(set);
        Assert.assertNull(set.getDatFile());

        // retrieve the dataset with dat file and verify information
        DataSet retrieved = this.controller.getDataSet(set.getId(), true);
        Assert.assertNotNull(retrieved);
        Assert.assertNotNull(retrieved.getDatFile());
        Assert.assertEquals("new-dataset", retrieved.getLabel());

        // create another set without dat file
        DataSet set2 = this.controller.createDataSet(defaultModel, "another-dataset", null);
        Assert.assertNotNull(set2);
        Assert.assertNull(set2.getDatFile());

        // retrieve the dataset without dat file and verify the data
        DataSet retrieved2 = this.controller.getDataSet(set2.getId(), true);
        Assert.assertNotNull(retrieved2);
        Assert.assertNull(retrieved2.getDatFile());
        Assert.assertEquals("another-dataset", retrieved2.getLabel());
    }
}
