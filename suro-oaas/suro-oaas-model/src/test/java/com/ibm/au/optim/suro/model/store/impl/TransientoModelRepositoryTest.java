package com.ibm.au.optim.suro.model.store.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.Model;

/**
 * @author Peter Ilfrich
 */
public class TransientoModelRepositoryTest {

    private String TEST_MODEL 		= 	"test-Model";
    private String TEST_MODEL_FILE	=	"model.mod";

    @Test
    public void testGetAll() {

        TransientModelRepository repo = new TransientModelRepository();
        Assert.assertEquals(0, repo.getAll().size());

        repo.addItem(new Model("model1"));
        repo.addItem(new Model("model2"));

        Assert.assertEquals(2, repo.getAll().size());
    }
    
    @Test
    public void testGetDefault() {
        TransientModelRepository repo = new TransientModelRepository();
        Assert.assertNull(repo.findByDefaultFlag());

        Model model = new Model("model");
        repo.addItem(model);

        Assert.assertNull(repo.findByDefaultFlag());

        model.setDefaultModel(true);
        repo.updateItem(model);

        Assert.assertNotNull(repo.findByDefaultFlag());
        Assert.assertEquals(model.getId(), repo.findByDefaultFlag().getId());
    }

    @Test
    public void testCreateItem() {
    	
        TransientModelRepository repo = new TransientModelRepository();
        Model model = new Model(TEST_MODEL, false);

        Assert.assertEquals(0, repo.getAll().size());
        Assert.assertEquals(TEST_MODEL, model.getLabel());
    }

    @Test
    public void testAttachWithoutDocument() {
        TransientModelRepository repo = new TransientModelRepository();
        Model model = new Model(TEST_MODEL, false);

        InputStream modelFile = new ByteArrayInputStream("mod-file-content".getBytes(StandardCharsets.UTF_8));
        repo.attach(model, TEST_MODEL_FILE, "text/plain", modelFile);

        Assert.assertNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE));

        repo.addItem(model);
        Assert.assertNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE));

    }

    @Test
    public void testAttachments() {
        TransientModelRepository repo = new TransientModelRepository();
        Model model = new Model(TEST_MODEL, false);
        

        InputStream modFile = new ByteArrayInputStream("mod-file-content".getBytes(StandardCharsets.UTF_8));
        repo.addItem(model);
        repo.attach(model, TEST_MODEL_FILE, "text/plain", modFile);

        Assert.assertNotNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE));
    }

    @Test
    public void testMultipleAttachments() {
    	
        TransientModelRepository repo = new TransientModelRepository();
        Model model = new Model(TEST_MODEL, false);
        model.setId("model-1");
        repo.addItem(model);

        Model model2 = new Model(TEST_MODEL, false);
        model2.setId("model-2");
        repo.addItem(model2);

        InputStream modFile = new ByteArrayInputStream("mod-file-content".getBytes(StandardCharsets.UTF_8));
        InputStream modFile2 = new ByteArrayInputStream("mod2-file-content".getBytes(StandardCharsets.UTF_8));

        repo.attach(model, TEST_MODEL_FILE + "_1", "text/plain", modFile);
        repo.attach(model, TEST_MODEL_FILE + "_2", "text/plain", modFile2);

        Assert.assertEquals(2, repo.getAll().size());
        Assert.assertNotNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE + "_1"));
        Assert.assertNotNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE + "_2"));

        Assert.assertNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE));
        Assert.assertNull(repo.getAttachment(model2.getId(), TEST_MODEL_FILE));

        repo.attach(model, TEST_MODEL_FILE, "text/plain", modFile2);
        Assert.assertNotNull(repo.getAttachment(model.getId(), TEST_MODEL_FILE));
    }


}
