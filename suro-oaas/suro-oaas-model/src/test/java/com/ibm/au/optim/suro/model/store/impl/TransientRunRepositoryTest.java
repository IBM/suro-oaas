package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.store.RunRepository;


import org.junit.Test;
import org.junit.Assert;

/**
 * @author Peter Ilfrich
 */
public class TransientRunRepositoryTest {

	@Test
    public void testCreateItem() {
		
		
        RunRepository repo = new TransientRunRepository();
        Run run = new Run();

    	
		String expected = "model-id";
        run.setModelId(expected);
        Assert.assertEquals(expected, run.getModelId());
        
        expected = "dataset-id";
        run.setDataSetId(expected);
        Assert.assertEquals(expected, run.getDataSetId());
        
        expected = "template-id";
        run.setTemplateId(expected);
        Assert.assertEquals(expected, run.getTemplateId());

        Assert.assertEquals(0, repo.getAll().size());
        Assert.assertNull(run.getId());
    }

    @Test
    public void testFindByJobId() {
    	
        RunRepository repo = new TransientRunRepository();
        Run run = new Run();
        run.setModelId("model-id");
        run.setDataSetId("dataset-id");
        run.setTemplateId("template-id");
        run.setJobId("job-id");

        repo.addItem(run);

        Assert.assertNull(repo.findByJobId("not-existing"));
        Assert.assertNull(repo.findByJobId(null));

        Assert.assertNotNull(repo.findByJobId("job-id"));
        Assert.assertEquals(run.getId(), repo.findByJobId("job-id").getId());
        Assert.assertEquals("dataset-id", repo.findByJobId("job-id").getDataSetId());
    }

    @Test
    public void testGetLast() {
    	
        RunRepository repo = new TransientRunRepository();

        Run run = new Run();
        run.setModelId("model-id");
        run.setDataSetId("dataset-id");
        run.setTemplateId("template-id");
        
        Run run2 = new Run();
        run2.setModelId("model2-id");
        run2.setDataSetId("dataset2-id");
        run2.setTemplateId("template2-id");
        
        Assert.assertNull(repo.getLast());
        repo.addItem(run);
        Assert.assertNotNull(repo.getLast());
        Assert.assertEquals(run.getId(), repo.getLast().getId());

        repo.addItem(run2);
        // ensure the timestamp is guaranteed larger
        run2.setStartTime(run.getStartTime() + 60000);
        repo.updateItem(run2);

        Assert.assertEquals(run2.getId(), repo.getLast().getId());

        // move the first run up again
        run.setStartTime(run2.getStartTime() + 60000);
        repo.updateItem(run);

        Assert.assertEquals(run.getId(), repo.getLast().getId());
    }

    @Test
    public void testFindByTemplateId() {
    	
        RunRepository repo = new TransientRunRepository();
        String templateId = "template";
        String foobar = "foobar";
        Assert.assertEquals(0, repo.findByTemplateId(null).size());
        Assert.assertEquals(0, repo.findByTemplateId(templateId).size());

        Run r1 = new Run();
        Run r2 = new Run();
        r1.setTemplateId(templateId);
        r2.setTemplateId(templateId);
        
        Run r3 = new Run();
        r3.setTemplateId(foobar);

        repo.addItem(r1);
        repo.addItem(r2);
        repo.addItem(r3);
        
       
        Assert.assertEquals(0, repo.findByTemplateId(null).size());
        Assert.assertEquals(2, repo.findByTemplateId(templateId).size());
        Assert.assertEquals(1, repo.findByTemplateId(foobar).size());
        Assert.assertEquals(0, repo.findByTemplateId("bar").size());
    }
}
