package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class TransientTemplateRepositoryTest {

	@Test
    public void testCreateItem() {
		
        TransientTemplateRepository repo = new TransientTemplateRepository();
        repo.environment = EnvironmentHelper.mockEnvironment(new Properties());

        String label = "label";
        String description = "description";
        String modelId = "model-id";

        Template template = new Template();
        template.setLabel(label);
        template.setDescription(description);
        template.setModelId(modelId);
        
        Assert.assertEquals(0, repo.getAll().size());
        repo.addItem(template);

        Assert.assertEquals(1, repo.getAll().size());
        Assert.assertNotNull(template.getId());
        Assert.assertEquals(modelId, template.getModelId());
        Assert.assertEquals(label, template.getLabel());
        Assert.assertEquals(description, template.getDescription());
    }


    @Test
    public void testFindByModelId() {
    	
    	TransientTemplateRepository repo = new TransientTemplateRepository();
        repo.environment = EnvironmentHelper.mockEnvironment(new Properties());

        String label = "label";
        String description = "description";
        String modelId = "model-id";

        Template template = new Template();
        template.setModelId(modelId);
        template.setLabel(label);
        template.setDescription(description);

        repo.addItem(template);
        
        
        Assert.assertEquals(0, repo.findByModelId("not-existing").size());
        Assert.assertEquals(0, repo.findByModelId(null).size());
        
        List<Template> models = repo.findByModelId(modelId);
        Assert.assertEquals(1, models.size());
        Assert.assertEquals(template.getId(), models.get(0).getId());
    }


}
