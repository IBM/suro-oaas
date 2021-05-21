package com.ibm.au.optim.suro.core.controller;


import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class BasicModelControllerTest  {

	/**
	 * 
	 */
    private ModelController controller;
    /**
     * 
     */
    private InputStream modStream = new ByteArrayInputStream("mod-file-content".getBytes(StandardCharsets.UTF_8));
    /**
     * 
     */
    private InputStream opsStream = new ByteArrayInputStream("ops-file-content".getBytes(StandardCharsets.UTF_8));


    /**
     * 
     */
    @Before
    public void setUp() throws Exception {
        this.controller = new BasicModelController();
        this.controller.setRepository(new TransientModelRepository());
    }

    /**
     * 
     */
    @After
    public void tearDown() throws Exception {

        this.controller = null;
    }
	/**
	 * 
	 * @throws Exception
	 */
	@Test
    public void testModelCreation() {
        // reset the repo
        this.controller.getRepository().removeAll();

        Model model = this.createModel("new-model");

        Assert.assertNotNull(model);
        Assert.assertNotNull(model.getId());
        Assert.assertEquals("new-model", model.getLabel());
        Assert.assertEquals(1, this.controller.getRepository().getAll().size());

    }
	
	/**
	 * 
	 */
	@Test
    public void testModelRetrievalWithoutAttachments() {
        // reset the repo
        this.controller.getRepository().removeAll();
        Model expected = this.createModel("new-model");

        // when we add the model to the repository, the attachments
        // are stored somewhere else and therefore are not accessible
        // directly from the model instance.
        //
        Model actual = controller.getModel(expected.getId(), false);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getId(), actual.getId());
        
        List<Attachment> ea = expected.getAttachments();
        List<Attachment> aa = expected.getAttachments();
        
        Assert.assertNotNull(aa);
        Assert.assertEquals(ea.size(), aa.size());
        
        
        for(Attachment attachment : aa) {
        	Assert.assertNull(attachment.getStream());
        }
    }

	
	/**
	 * 
	 */
	@Test
    public void testModelRetrievalErrors() {
		
        this.controller.getRepository().removeAll();
        this.createModel("new-model");

        Model noModel = this.controller.getModel(null, false);
        Assert.assertNull(noModel);
        noModel = this.controller.getModel(null, true);
        Assert.assertNull(noModel);

        noModel = this.controller.getModel("not-existing", false);
        Assert.assertNull(noModel);
        noModel = this.controller.getModel("not-existing", true);
        Assert.assertNull(noModel);

    }

	
	/**
	 * 
	 */
	@Test
    public void testModelRetrievalWithAttachments() {
        // reset the repo
        this.controller.getRepository().removeAll();
        Model expected = this.createModel("new-model");

        Model actual = this.controller.getModel(expected.getId(), true);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getAttachments().size(), actual.getAttachments().size());
    }

	
	/**
	 * 
	 */
	@Test
    public void testDefaultModel() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create 2 models
        Model model1 = this.createModel("model1");
        Model model2 = this.createModel("model2");

        // currently there's no default model
        Assert.assertNull(this.controller.getDefaultModel());

        // set model1 as default
        this.controller.setDefaultModel(model1);

        // verify model1 is the default
        Model currentDefault = this.controller.getDefaultModel();
        Assert.assertNotNull(currentDefault);
        Assert.assertEquals(model1.getId(), currentDefault.getId());
        Assert.assertTrue(currentDefault.isDefaultModel());

        // set model2 as default
        this.controller.setDefaultModel(model2);
        // verify model2 is the default
        currentDefault = this.controller.getDefaultModel();
        Assert.assertNotNull(currentDefault);
        Assert.assertEquals(model2.getId(), currentDefault.getId());
        Assert.assertTrue(currentDefault.isDefaultModel());
        // verify model1 is no longer default
        Model oldDefault = this.controller.getModel(model1.getId(), false);
        Assert.assertNotNull(oldDefault);
        Assert.assertFalse(oldDefault.isDefaultModel());

        // attempt to set null as default model
        this.controller.setDefaultModel(null);
        // verify the default hasn't changed
        currentDefault = this.controller.getDefaultModel();
        Assert.assertNotNull(currentDefault);
        Assert.assertEquals(model2.getId(), currentDefault.getId());
        Assert.assertTrue(currentDefault.isDefaultModel());

    }
    
    private Model createModel(String label) {
    	
    	List<Attachment> attachments = new ArrayList<Attachment>();
    	Attachment a1 = new Attachment("script.mod", 0, "text/plain");
    	a1.store(this.modStream);
    	attachments.add(a1);
    	
    	Attachment a2 = new Attachment("model.ops", 0, "text/plain");
    	a2.store(this.opsStream);
    	attachments.add(a2);
    	Model model = this.controller.createModel(label, attachments);
    	
    	return model;
    }
}
