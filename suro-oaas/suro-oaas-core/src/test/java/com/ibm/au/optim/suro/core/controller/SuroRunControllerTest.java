package com.ibm.au.optim.suro.core.controller;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ibm.au.optim.suro.core.controller.SuroRunController;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Parameter;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.JobStatus;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientDataSetRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientRunRepository;

/**
 * @author Peter Ilfrich
 */
public class SuroRunControllerTest {

	/**
	 * 
	 */
    private String defaultTemplateId = "templateId";
    /**
     * 
     */
    private Model defaultModel;
    /**
     * 
     */
    private DataSet defaultSet;

    /**
     * 
     */
    private SuroRunController controller;



    /**
     * 
     */
    @After
    public void tearDown() throws Exception {

        this.defaultModel = null;
        this.defaultSet = null;
        this.controller = null;
    }
    /**
     * 
     */
    @Before
    public void setUp() throws Exception {


        ModelRepository modelRepository = new TransientModelRepository();
        this.defaultModel = new Model("label", true);
        modelRepository.addItem(this.defaultModel);

        DataSetRepository dataSetRepostiory = new TransientDataSetRepository();
        this.defaultSet = new DataSet();
        dataSetRepostiory.addItem(this.defaultSet);

        this.controller = new SuroRunController();
        this.controller.setRepository(new TransientRunRepository());
    }

    /**
     * 
     */
    @Test
    public void testRunQueue() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // check if the new queue is empty
        Assert.assertEquals(this.controller.getQueue().size(), 0);
        
        Run run = new Run();
        run.setDataSetId(this.defaultSet.getId());
        run.setModelId(this.defaultModel.getId());
        run.setTemplateId(this.defaultTemplateId);
        run.setParameters(new ArrayList<Parameter>());

        // add a run to the queue and check the size afterwards
        Run created = this.controller.createRun(run);
        String runId = run.getId();
        boolean add1 = this.controller.addRun(created);
        Assert.assertTrue(add1);
        boolean add2 = this.controller.addRun(created);
        Assert.assertFalse(add2);
        Assert.assertEquals(this.controller.getQueue().size(), 1);

        // retrieve the next run from the queue and verify it being removed from the queue
        Run nextRun = this.controller.getNext();
        Assert.assertNotNull(nextRun);
        Assert.assertEquals(nextRun.getId(), runId);
        Assert.assertEquals(this.controller.getQueue().size(), 0);

        nextRun = this.controller.getNext();
        Assert.assertNull(nextRun);
    }

    /**
     * 
     */
    @Test
    public void testCreateRun() {
        // reset the repo
        this.controller.getRepository().removeAll();
        
        
        Run run = new Run();
        run.setDataSetId(this.defaultSet.getId());
        run.setModelId(this.defaultModel.getId());
        run.setTemplateId(this.defaultTemplateId);
        run.setParameters(new ArrayList<Parameter>());

        // create the run
        Run created = this.controller.createRun(run);

        // check run data
        Assert.assertNotNull(created);
        Assert.assertNotNull(created.getId());
        Assert.assertEquals(created.getModelId(), this.defaultModel.getId());
        Assert.assertEquals(created.getDataSetId(), this.defaultSet.getId());
        Assert.assertEquals(created.getTemplateId(), this.defaultTemplateId);

        // fetch the run from the repo and compare
        String runId = created.getId();
        Run sameRun = this.controller.getRepository().getItem(runId);


        Assert.assertNotNull(sameRun);
        Assert.assertEquals(runId, sameRun.getId());
    }



    /**
     * 
     */
    @Test
    public void testRetrieveRun() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // add a new run directly to the repo
        Run run = new Run();
        this.controller.getRepository().addItem(run);

        // make sure it was added
        Assert.assertNotNull(run.getId());

        // retrieve the added run and compare with original
        Run fetchedRun = this.controller.getRun(run.getId());
        Assert.assertNotNull(fetchedRun);
        Assert.assertEquals(fetchedRun.getId(), run.getId());
    }



    /**
     * 
     */
    @Test
    public void testRunByJobId() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create run and add to repository
        Run run = new Run();
        run.setJobId("some-job-id");
        Run run2 = new Run();
        this.controller.getRepository().addItem(run);
        this.controller.getRepository().addItem(run2);

        // retrieve null
        Run nullRun = this.controller.getRunByJobId(null);
        Assert.assertNull(nullRun);

        Run job = this.controller.getRunByJobId("some-job-id");
        Assert.assertNotNull(job);
        Assert.assertEquals(job.getId(), run.getId());
        Assert.assertEquals(job.getJobId(), "some-job-id");
    }



    /**
     * 
     */
    @Test
    public void testSetJobState() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create run and add to repository
        Run run = new Run();
        this.controller.getRepository().addItem(run);
        String runId = run.getId();

        this.controller.setJobStatus(run, JobStatus.SUBMITTED);

        Run modifiedRun = this.controller.getRun(runId);
        Assert.assertNotNull(modifiedRun.getJobStatus());
        Assert.assertEquals(modifiedRun.getJobStatus(), JobStatus.SUBMITTED);

        this.controller.setJobStatus(run, JobStatus.PROCESSED);
        modifiedRun = this.controller.getRun(runId);
        Assert.assertNotNull(modifiedRun.getJobStatus());
        Assert.assertEquals(modifiedRun.getJobStatus(), JobStatus.PROCESSED);
    }


    /**
     * 
     */
    @Test
    public void testSetRunState() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create run and add to repository
        Run run = new Run();
        this.controller.getRepository().addItem(run);
        String runId = run.getId();

        this.controller.setRunStatus(run, RunStatus.COLLECTING_RESULTS);
        Run updated = this.controller.getRun(runId);
        Assert.assertNotNull(updated);
        Assert.assertNotNull(updated.getStatus());
        Assert.assertEquals(updated.getStatus(), RunStatus.COLLECTING_RESULTS);
    }


    /**
     * 
     */
    @Test
    public void testFinishedRunStates() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create runs and add to repository
        Run run1 = new Run();
        this.controller.getRepository().addItem(run1);
        String runId1 = run1.getId();

        Run run2 = new Run();
        this.controller.getRepository().addItem(run2);
        String runId2 = run2.getId();

        Run run3 = new Run();
        this.controller.getRepository().addItem(run3);
        String runId3 = run3.getId();

        this.controller.setRunStatus(run1, RunStatus.FAILED);
        this.controller.setRunStatus(run2, RunStatus.COMPLETED);
        this.controller.setRunStatus(run3, RunStatus.ABORTED);

        Run compare1 = this.controller.getRun(runId1);
        Assert.assertNotNull(compare1);
        Assert.assertEquals(compare1.getStatus(), RunStatus.FAILED);
        
        Run compare2 = this.controller.getRun(runId2);
        Assert.assertNotNull(compare2);
        Assert.assertEquals(compare2.getStatus(), RunStatus.COMPLETED);

        Run compare3 = this.controller.getRun(runId3);
        Assert.assertNotNull(compare3);
        Assert.assertEquals(compare3.getStatus(), RunStatus.ABORTED);
    }



    /**
     * 
     */
    @Test
    public void testResetRun() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create runs and set values
        Run run = new Run();
        run.setJobId("some-job-id");
        run.setJobStatus(JobStatus.CREATED);

        // add item to repo
        this.controller.getRepository().addItem(run);
        String runId = run.getId();

        // verify that the object has been added correctly.
        Run compare1 = this.controller.getRepository().getItem(runId);
        Assert.assertNotNull(compare1);
        Assert.assertNotNull(compare1.getJobId());
        Assert.assertNotNull(compare1.getJobStatus());

        // reset the run
        this.controller.resetRun(runId);

        // retrieve the updated run and check the reset
        Run compare2 = this.controller.getRepository().getItem(runId);
        Assert.assertNotNull(compare2);
        Assert.assertNull(compare2.getJobId());
        Assert.assertNull(compare2.getJobStatus());
    }


    /**
     * 
     */
    @Test
    public void testDeleteRun() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create runs and add to the repository
        Run run = new Run();
        this.controller.getRepository().addItem(run);
        String runId = run.getId();

        // check that the item has been added
        Assert.assertNotNull(this.controller.getRepository().getItem(runId));

        // remove the item
        boolean result = this.controller.deleteRun(runId);

        // verify the result and the repository content
        Assert.assertTrue(result);
        Assert.assertNull(this.controller.getRepository().getItem(runId));
    }



    /**
     * 
     */
    @Test
    public void testAbortFinishedRun() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create runs and add to the repository
        Run run = new Run();
        this.controller.getRepository().addItem(run);
        this.controller.addRun(run);

        // attempt to abort a run that doesn't exist
        boolean result = this.controller.abortRun("not-existing");
        Assert.assertFalse(result);
        boolean result2 = this.controller.abortRun(null);
        Assert.assertFalse(result2);

        // test different final states
        run.setStatus(RunStatus.COMPLETED);
        boolean result3 = this.controller.abortRun(run.getId());
        Assert.assertTrue(result3);
        Assert.assertEquals(run.getStatus(), RunStatus.COMPLETED);
        Assert.assertEquals(this.controller.getQueue().size(), 1);

        run.setStatus(RunStatus.ABORTED);
        boolean result4 = this.controller.abortRun(run.getId());
        Assert.assertTrue(result4);
        Assert.assertEquals(run.getStatus(), RunStatus.ABORTED);
        Assert.assertEquals(this.controller.getQueue().size(), 1);

        run.setStatus(RunStatus.FAILED);
        boolean result5 = this.controller.abortRun(run.getId());
        Assert.assertTrue(result5);
        Assert.assertEquals(run.getStatus(), RunStatus.FAILED);
        Assert.assertEquals(this.controller.getQueue().size(), 1);
    }



    /**
     * 
     */
    @Test
    public void testAbortQueuedRun() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create runs and add to the repository
        Run run = new Run();
        this.controller.getRepository().addItem(run);

        // try to abort run in repo, which is not in the queue
        boolean result = this.controller.abortRun(run.getId());
        Assert.assertFalse(result);

        // add run to the queue
        this.controller.addRun(run);
        Assert.assertEquals(1, this.controller.getQueue().size());

        // abort the run and check the result + final state
        boolean result2 = this.controller.abortRun(run.getId());
        Assert.assertTrue(result2);
        Assert.assertEquals(0, this.controller.getQueue().size());
        Assert.assertEquals(run.getStatus(), RunStatus.ABORTED);
    }



    /**
     * 
     */
    @Test
    public void testMultipleRunsInQueue() {
        // reset the repo
        this.controller.getRepository().removeAll();

        // create runs and add to the repository
        Run run1 = new Run();
        this.controller.getRepository().addItem(run1);
        Run run2 = new Run();
        this.controller.getRepository().addItem(run2);

        this.controller.addRun(run1);
        this.controller.addRun(run2);
        Assert.assertEquals(2, this.controller.getQueue().size());

        boolean result = this.controller.abortRun(run2.getId());
        Assert.assertTrue(result);
        Assert.assertEquals(1, this.controller.getQueue().size());
    }



    /**
     * 
     */
    @Test
    public void testCompleteRun() {
        // reset the repo
        this.controller.getRepository().removeAll();

        Run run1 = new Run();
        this.controller.getRepository().addItem(run1);
        this.controller.setRunStatus(run1, RunStatus.COLLECTING_RESULTS);
        this.controller.completeRun(run1.getId());
        Assert.assertEquals(RunStatus.COMPLETED, run1.getStatus());

        Run run2 = new Run();
        this.controller.getRepository().addItem(run2);
        this.controller.setRunStatus(run2, RunStatus.FAILED);
        this.controller.completeRun(run2.getId());
        Assert.assertEquals(RunStatus.FAILED, run2.getStatus());

        Run run3 = new Run();
        this.controller.getRepository().addItem(run3);
        this.controller.setRunStatus(run3, RunStatus.ABORTED);
        this.controller.completeRun(run3.getId());
        Assert.assertEquals(RunStatus.ABORTED, run3.getStatus());

        Run run4 = new Run();
        this.controller.getRepository().addItem(run4);
        this.controller.setRunStatus(run4, RunStatus.ABORTING);
        this.controller.completeRun(run4.getId());
        Assert.assertEquals(RunStatus.ABORTING, run4.getStatus());
    }

}
