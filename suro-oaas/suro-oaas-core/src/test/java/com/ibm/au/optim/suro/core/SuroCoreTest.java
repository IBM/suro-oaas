package com.ibm.au.optim.suro.core;

import com.ibm.au.jaws.web.core.runtime.impl.InMemoryRuntimeContext;
import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.core.admin.preference.BasicPreferenceManager;
import com.ibm.au.optim.suro.core.controller.BasicDataSetController;
import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.core.controller.SuroRunController;
import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.control.*;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.control.job.JobExecutor;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Parameter;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.admin.preference.impl.TransientSystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientDataSetRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientRunRepository;
import com.ibm.au.jaws.web.core.runtime.RuntimeContext;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentFacade;
import com.ibm.au.jaws.web.core.runtime.impl.PropertiesParameterSource;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Peter Ilfrich
 */
public class SuroCoreTest extends TestCase {

	/**
	 * 
	 */
    private SuroCore core;


    /**
     * 
     */
    @Before
    public void setUp() throws Exception {

        this.resetRepositories();
    }



    /**
     * 
     */
    @Test
    public void testDefaultDatabaseVersion() {
        this.resetRepositories();
        this.core.setReady(true);

        Assert.assertEquals(SuroCore.DATABASE_MIN_VERSION, this.core.getCurrentDatabaseVersion());

        this.core.getPreferenceManager().setSystemPreference(Core.PREFERENCE_DB_VERSION_NAME, "0.0.8");
        Assert.assertEquals("0.0.8", this.core.getCurrentDatabaseVersion());

        this.core.getPreferenceManager().removeSystemPreference(Core.PREFERENCE_DB_VERSION_NAME);
        Assert.assertEquals(SuroCore.DATABASE_MIN_VERSION, this.core.getCurrentDatabaseVersion());
    }

    /**
     * 
     */
    @Test
    public void testBasicCoreReadiness() {
    	Assert.assertEquals(false, this.core.isReady());

        this.core.setReady(true);
        Assert.assertEquals(true, this.core.isReady());

        this.core.setReady(false);
        Assert.assertEquals(false, this.core.isReady());
    }

    /**
     * 
     */
    @Test
    public void testCompletedRunReadiness() {
    	
        this.resetRepositories();
        this.core.setReady(true);
        
        Run first = this.createRun(false);
        Run second = this.createRun(false);

        // create and submit 2 new runs
        first = this.core.submitRun(first);
        second = this.core.submitRun(second);

        // run status should be queued for both runs
        Assert.assertEquals(RunStatus.QUEUED, first.getStatus());
        Assert.assertEquals(RunStatus.QUEUED, second.getStatus());

        // disable the core and attempt to complete the runs (should add them to completed-queue)
        this.core.setReady(false);
        this.core.completeRun(first.getId());
        this.core.completeRun(second.getId());

        // run status should still be queued since the core is not ready
        Assert.assertEquals(RunStatus.QUEUED, first.getStatus());
        Assert.assertEquals(RunStatus.QUEUED, second.getStatus());

        // core gets ready, will complete queued runs
        this.core.setReady(true);

        // now the runs should be completed
        Assert.assertEquals(RunStatus.COMPLETED, first.getStatus());
        Assert.assertEquals(RunStatus.COMPLETED, second.getStatus());
    }

    /**
     * 
     */
    @Test
    public void testSubmitRun() {
    	
        this.resetRepositories();
        this.core.setReady(false);

        Run run = this.createRun(false);

        Run submitted = this.core.submitRun(run);
        Assert.assertNull(submitted);

        this.core.setReady(true);
        submitted = this.core.submitRun(run);
        Assert.assertNotNull(submitted);
        Assert.assertNotNull(submitted.getId());
        Assert.assertEquals(RunStatus.QUEUED, submitted.getStatus());
    }


    /**
     * 
     */
    @Test
    public void testCompleteRun() {
    	
        this.resetRepositories();
        this.core.setReady(false);

        this.core.getRunController().getRepository().addItem(new Run());
        Run run = this.core.getRunController().getRepository().getLast();

        // add the run to the completed queue
        this.core.completeRun(run.getId());

        this.core.setReady(true);
        Assert.assertEquals(RunStatus.COMPLETED, run.getStatus());
    }

    /**
     * 
     */
    @Test
    public void testGetNextRun() {
    	
        this.resetRepositories();

        // by default
        this.core.setReady(true);
        assertNull(this.core.getNextRun());

        // create a new run
        Run run = this.createRun(false);
        Run newRun = this.core.submitRun(run);

        // set system to not ready and attempt to retrieve next run
        this.core.setReady(false);
        assertNull(this.core.getNextRun());

        // set the system to ready and retrieve a run
        this.core.setReady(true);
        Run nextRun = this.core.getNextRun();
        Assert.assertNotNull(nextRun);
        Assert.assertEquals(newRun.getId(), nextRun.getId());
    }

    /**
     * 
     */
    @Test
    public void testAbortProcessingRun() {
    	
        this.resetRepositories();
        this.core.setReady(true);

        // create a new run


        Run run = this.createRun(false);
        Run submitted = this.core.getRunController().createRun(run);
        submitted.setStatus(RunStatus.PROCESSING);

        this.core.abortRun(submitted.getId());
        Assert.assertEquals(submitted.getStatus(), RunStatus.ABORTED);
    }

    /**
     * 
     */
    @Test
    public void testAbortNotExistingRun() {
    	
        this.resetRepositories();
        this.core.setReady(true);

        Assert.assertFalse(this.core.abortRun(null));
        Assert.assertFalse(this.core.abortRun("doesn't-exist"));
    }

    /**
     * 
     */
    @Test
    public void testAbortCompletedRun() {
    	
        this.resetRepositories();
        this.core.setReady(true);

        // create a new run

        Run run = this.createRun(false);
        Run submitted = this.core.getRunController().createRun(run);

        submitted.setStatus(RunStatus.FAILED);
        Assert.assertTrue(this.core.abortRun(submitted.getId()));

        submitted.setStatus(RunStatus.COMPLETED);
        Assert.assertTrue(this.core.abortRun(submitted.getId()));

        submitted.setStatus(RunStatus.ABORTED);
        Assert.assertTrue(this.core.abortRun(submitted.getId()));
    }

    /**
     * 
     */
    @Test
    public void testAbortQueuedRun() {
        
    	this.resetRepositories();
        this.core.setReady(true);

        // create a new run
        Run run = this.createRun(false);
        Run submitted = this.core.getRunController().createRun(run);
        
        this.core.getRunController().addRun(submitted);
        Assert.assertEquals(1, this.core.getRunController().getQueue().size());

        submitted.setStatus(RunStatus.QUEUED);
        Assert.assertTrue(this.core.abortRun(submitted.getId()));

        Assert.assertEquals(0, this.core.getRunController().getQueue().size());
    }

    /**
     * 
     */
    @Test
    public void testAbortRunWithCoreNotReady() {
    	
    	this.resetRepositories();
        this.core.setReady(false);

        // create a new run
        Run run = this.createRun(false);
        Run submitted = this.core.getRunController().createRun(run);
        
        submitted.setStatus(RunStatus.CREATE_JOB);
        Assert.assertFalse(this.core.abortRun(submitted.getId()));
    }

    /**
     * 
     */
    @Test
    public void testAbortRunWithJobControllerFailing() {
    	
        this.resetRepositories(false);
        this.core.setReady(true);

        // create a new run
        Run newRun = this.createRun(true);
        
        newRun.setStatus(RunStatus.PROCESSING);

        Assert. assertTrue(this.core.abortRun(newRun.getId()));
    }

    /**
     * 
     */
    @Test
    public void testResetRunFailure() {
        this.resetRepositories(false);
        this.core.setReady(true);

        Run newRun = this.createRun(true);
        newRun.setStatus(RunStatus.PROCESSING);

        this.core.restartRun(newRun.getId());
        Assert.assertEquals(RunStatus.PROCESSING, newRun.getStatus());
    }

    /**
     * 
     */
    @Test
    public void testResetRunCoreNotReady() {
        this.resetRepositories(false);
        this.core.setReady(true);

        Run newRun = this.createRun(true);
        newRun.setStatus(RunStatus.PROCESSING);

        this.core.setReady(false);

        this.core.restartRun(newRun.getId());
        Assert.assertEquals(RunStatus.PROCESSING, newRun.getStatus());
    }

    /**
     * 
     */
    @Test
    public void testResetRun() {
    	this.resetRepositories();
        this. core.setReady(true);

        Run newRun = this.createRun(true);
        newRun.setStatus(RunStatus.PROCESSING);

        // reset the run, which will put it back into the queue
        Assert.assertEquals(0, this.core.getRunController().getQueue().size());
        this.core.restartRun(newRun.getId());
        Assert.assertEquals(1, this.core.getRunController().getQueue().size());
        Assert.assertEquals(RunStatus.QUEUED, newRun.getStatus());
    }


    /**
     * 
     */
    @Test
    public void testDeleteProcessingRun() {
    	this.resetRepositories();
    	this.core.setReady(true);

        Run newRun = this.createRun(true);
        newRun.setStatus(RunStatus.PROCESSING);

        Assert.assertEquals(1, this.core.getRunController().getRepository().getAll().size());
        Assert.assertTrue(this.core.deleteRun(newRun.getId()));
        Assert.assertEquals(0, this.core.getRunController().getRepository().getAll().size());
    }

    /**
     * 
     */
    @Test
    public void testDeleteRunCoreNotReady() {
    	this.resetRepositories();
    	this.core.setReady(true);

        Run newRun = this.createRun(true);

        this.core.setReady(false);
        Assert.assertFalse(this.core.deleteRun(newRun.getId()));
    }

    /**
     * 
     */
    @Test
    public void testDeleteNotExistingRun() {
    	this.resetRepositories();
        this.core.setReady(true);

        Assert.assertFalse(this.core.deleteRun(null));
        Assert.assertFalse(this.core.deleteRun("doesn't-exist"));
    }

    /**
     * 
     */
    @Test
    public void testDeleteQueuedRun() {
        this.resetRepositories();
        this.core.setReady(true);

        Run newRun = this.createRun(true);
        newRun.setStatus(RunStatus.QUEUED);
        this.core.getRunController().addRun(newRun);

        Assert.assertEquals(1, this.core.getRunController().getQueue().size());
        Assert.assertTrue(this.core.deleteRun(newRun.getId()));
        Assert.assertEquals(0, this.core.getRunController().getQueue().size());
    }

    /**
     * 
     */
    @Test
    public void testDeleteFinishedRun() {
        this.resetRepositories();
        this.core.setReady(true);

        Run newRun = this.createRun(true);
        newRun.setStatus(RunStatus.COMPLETED);

        Run newRun2 = this.createRun(true);
        newRun2.setStatus(RunStatus.ABORTED);

        Run newRun3 = this.createRun(true);
        newRun3.setStatus(RunStatus.FAILED);

        Assert.assertEquals(3, this.core.getRunController().getRepository().getAll().size());
        Assert.assertTrue(this.core.deleteRun(newRun.getId()));
        Assert.assertTrue(this.core.deleteRun(newRun2.getId()));
        Assert.assertTrue(this.core.deleteRun(newRun3.getId()));
        Assert.assertEquals(0, this.core.getRunController().getRepository().getAll().size());
    }









    /*
     SUPPORT METHODS
     */

    /**
     * 
     * @return
     */
    private Model createOptimizationModel() {
    	
    	List<Attachment> attachments = new ArrayList<Attachment>();
    	
    	Attachment att1 = new Attachment("script.mod", 0, "text/plain");
    	att1.store(new ByteArrayInputStream("mod-file-content".getBytes(StandardCharsets.UTF_8)));
    	attachments.add(att1);
    	
    	Attachment att2 = new Attachment("model.ops", 0, "text/plain");
    	att2.store(new ByteArrayInputStream("ops-file-content".getBytes(StandardCharsets.UTF_8)));
    	attachments.add(att2);
    	
    	
        Model model = this.core.getModelController().createModel("model-label", attachments);
        
        return model;
    }

    /**
     * 
     * @param model
     * @return
     */
    private DataSet createDataSet(Model model) {
    	
    	
        return this.core.getDataSetController().createDataSet(model, "set-label", new ByteArrayInputStream("dat-file-content".getBytes(StandardCharsets.UTF_8)));
    }
    
    /**
     * 
     * @return
     */
    private Run createRun(boolean submit) {
    	

        String templateId = "template-id";
        List<Parameter> parameters = new ArrayList<Parameter>();
        Model model = this.createOptimizationModel();
        DataSet dataSet = this.createDataSet(model);
        
        Run submitted = new Run();
        submitted.setModelId(model.getId());
        submitted.setDataSetId(dataSet.getId());
        submitted.setTemplateId(templateId);
        submitted.setParameters(parameters);
        
        if (submit == true) {
        
        	submitted = this.core.getRunController().createRun(submitted);
        	
        }
        
        return submitted;
    	
    }

    /**
     * 
     */
    private void resetRepositories() {
    	
        this.resetRepositories(true);
    }

    /**
     * 
     * @param jobControllerAbortBehaviour
     */
    private void resetRepositories(final boolean jobControllerAbortBehaviour) {
    	
        this.core = new SuroCore();
        // create the environment
        Properties properties = new Properties();
        PropertiesParameterSource source = new PropertiesParameterSource(properties);
        RuntimeContext runtime = new InMemoryRuntimeContext();
        EnvironmentFacade<PropertiesParameterSource, RuntimeContext> facade = new EnvironmentFacade<>(source, runtime);
        facade.bind();

        PreferenceManager prefManager = new BasicPreferenceManager();
        SystemPreferenceRepository prefRepo = new TransientSystemPreferenceRepository();
        prefManager.setRepository(prefRepo);
        facade.setAttribute(SystemPreferenceRepository.PREFERENCE_REPO_INSTANCE, prefRepo);
        facade.setAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE, prefManager);

        ModelController modelController = new BasicModelController();
        modelController.setRepository(new TransientModelRepository());
        facade.setAttribute(ModelController.MODEL_CONTROLLER_INSTANCE, modelController);

        DataSetController setController = new BasicDataSetController();
        setController.setRepository(new TransientDataSetRepository());
        facade.setAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE, setController);

        RunController runController = new SuroRunController();
        runController.setRepository(new TransientRunRepository());
        facade.setAttribute(RunController.RUN_CONTROLLER_INSTANCE, runController);

        JobController jobController = new JobController() {
            @Override
            public void completeJob(String runId) {}

            @Override
            public boolean abortJob(String runId) {
                return jobControllerAbortBehaviour;
            }

            @Override
            public void cleanupJobs() {
            }

            @Override
            public void createJobExecutor(int index) {
            }

            @Override
            public JobExecutor getJobExecutor(String runId) {
                return null;
            }

            @Override
            public void deleteJob(String jobId) {
            }

            @Override
            public List<JobExecutor> getExecutors() {
                return null;
            }

            @Override
            public ResultManager getResultManager() {
                return null;
            }
        };
        facade.setAttribute(JobController.JOB_CONTROLLER_INSTANCE, jobController);

        this.core.bind(facade);
    }
}
