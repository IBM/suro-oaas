package com.ibm.au.optim.suro.docloud;

import com.ibm.au.optim.oaas.test.MockController;
import com.ibm.au.optim.oaas.test.MockServer;
import com.ibm.au.optim.oaas.test.payload.MockJob;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.control.job.JobExecutor;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.optim.oaas.client.job.model.Job;
import com.ibm.optim.oaas.client.job.model.JobExecutionStatus;

import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Peter Ilfrich
 */
public class DOCloudJobControllerTest extends DOCloudTest {


    private MockController mockControl;

    /**
     * 
     */
    @Before
    public void setUp() throws Exception {
    	
        this.mockControl = new MockController(MockServer.DEFAULT_PORT);
    }

    /**
     * 
     */
    @Test
    public void testBinding() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController controller = (DOCloudJobController) env.getAttribute(JobController.JOB_CONTROLLER_INSTANCE);

        controller.doRelease();
        Assert.assertNull(controller.getJobClient());
        Assert.assertNull(controller.getExecutors());
    }

    /**
     * 
     */
    @Test
    public void testExecutorCreation() {
    	
        // setup properties
        Properties properties = new Properties();
        properties.setProperty(DOCloudJobController.CFG_DOCLOUD_MAX_CONCURRENT, "A");
        // create + bind environment
        Environment env = this.createEnvironment(1, true, properties);
        Assert.assertEquals(DOCloudJobController.DEFAULT_DOCLOUD_MAX_CONCURRENT, getJobController(env).getExecutors().size());

        // create + bind environment
        Environment env2 = this.createEnvironment(7, true);
        Assert.assertEquals(7, getJobController(env2).getExecutors().size());
    }
    
    /**
     * 
     */
    @Test
    public void testJobCleanup() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = getJobController(env);
        control.cleanupJobs();

        Assert.assertEquals(0, this.mockControl.getAllJobs().size());
        this.mockControl.createJob();
        Assert.assertEquals(1, this.mockControl.getAllJobs().size());
        control.cleanupJobs();
        Assert.assertEquals(0, this.mockControl.getAllJobs().size());
    }

    /**
     * 
     */
    @Test
    public void testCleanupWithMatchingRun() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = getJobController(env);

        // create a job in DOCloud
        String jobId = this.mockControl.createJob();

        // create matching run
        RunRepository runRepository = this.getRunRepository(env);
        Run r = new Run();
        r.setJobId(jobId);
        runRepository.addItem(r);

        // attempt to cleanup, should not remove job
        control.cleanupJobs();
        // job should still be there
        Assert.assertEquals(1, this.mockControl.getAllJobs().size());
    }

    /**
     * 
     */
    @Test
    public void testDeleteJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        String jobId = this.mockControl.createJob();

        Assert.assertEquals(1, this.mockControl.getAllJobs().size());
        control.deleteJob(null);
        Assert.assertEquals(1, this.mockControl.getAllJobs().size());
        control.deleteJob("not-existing");
        Assert.assertEquals(1, this.mockControl.getAllJobs().size());
        control.deleteJob(jobId);
//        Assert.assertEquals(0, this.mockControl.getAllJobs().size());
    }

    /**
     * 
     */
    @Test
    public void testDeleteInvalidPort() {
        this.mockControl.reset();

        Properties properties = new Properties();
        properties.setProperty(DOCloudJobController.CFG_DOCLOUD_API_URL, "http://localhost:13337");

        Environment env = this.createEnvironment(1, true, properties);
        DOCloudJobController control = this.getJobController(env);

        String jobId = this.mockControl.createJob();
        control.deleteJob(jobId);

        Assert.assertEquals(1, this.mockControl.getAllJobs().size());
    }

    /**
     * 
     */
    @Test
    public void testJobClient() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        String jobId = this.mockControl.createJob();
        try {
        	
            List<? extends Job> jobs = control.getJobClient().getAllJobs();
            Assert.assertEquals(1, jobs.size());
            Assert.assertEquals(jobId, jobs.get(0).getId());
        
        } catch (Exception e) {
            
        	e.printStackTrace();
        	Assert.assertFalse(true);
        }
    }

    /**
     * 
     */
    @Test
    public void testAbortJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        String jobId = this.mockControl.createJob();
        RunRepository repo = this.getRunRepository(env);
        Run r = new Run();
        r.setJobId(jobId);
        repo.addItem(r);

        JobExecutor exec = control.getExecutors().get(0);

        exec.setCurrentJobId(jobId);
        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);

        Assert.assertTrue(control.abortJob(r.getId()));

        Assert.assertTrue(exec.isBusy());
        Assert.assertNotNull(exec.getCurrentJobId());
        Assert.assertNotNull(exec.getCurrentRunId());

        Assert.assertEquals(JobExecutionStatus.CREATED.toString(), this.mockControl.getJob(jobId).getExecutionStatus());

        // update status
        MockJob job = this.mockControl.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.RUNNING.toString());
        this.mockControl.updateJob(job);

        Assert.assertTrue(control.abortJob(r.getId()));
        Assert.assertEquals(JobExecutionStatus.INTERRUPTED.toString(), this.mockControl.getJob(jobId).getExecutionStatus());
    }
    
    /**
     * 
     */
    @Test
    public void testAbortNoExecutor() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        String jobId = this.mockControl.createJob();
        RunRepository repo = this.getRunRepository(env);
        Run r = new Run();
        r.setJobId(jobId);
        repo.addItem(r);

        JobExecutor exec = control.getExecutors().get(0);
        exec.setBusy(true);

        // update status
        MockJob job = this.mockControl.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.RUNNING.toString());
        this.mockControl.updateJob(job);

        Assert.assertTrue(control.abortJob(r.getId()));
        Assert.assertEquals(JobExecutionStatus.RUNNING.toString(), this.mockControl.getJob(jobId).getExecutionStatus());
    }
    
    /**
     * 
     */
    @Test
    public void testCompleteJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        String jobId = mockControl.createJob();
        RunRepository repo = this.getRunRepository(env);
        Run r = new Run();
        r.setJobId(jobId);
        repo.addItem(r);

        JobExecutor exec = control.getExecutors().get(0);

        exec.setCurrentJobId(jobId);
        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);

        control.completeJob(r.getId());

        Assert.assertFalse(exec.isBusy());
        Assert.assertNull(exec.getCurrentJobId());
        Assert.assertNull(exec.getCurrentRunId());

        Assert.assertEquals(JobExecutionStatus.CREATED.toString(), mockControl.getJob(jobId).getExecutionStatus());
    }
    
    /**
     * 
     */
    @Test
    public void testCompleteJobNoExecutor() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        String jobId = this.mockControl.createJob();
        RunRepository repo = this.getRunRepository(env);
        Run r = new Run();
        r.setJobId(jobId);
        repo.addItem(r);

        JobExecutor exec = control.getExecutors().get(0);
        exec.setBusy(true);
        exec.setCurrentJobId("abc");
        exec.setCurrentRunId("abc");

        control.completeJob(r.getId());

        Assert.assertTrue(exec.isBusy());
        Assert.assertEquals("abc", exec.getCurrentJobId());
        Assert.assertEquals("abc", exec.getCurrentRunId());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJobExecutor() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        // create run
        RunRepository repo = this.getRunRepository(env);
        Run r = new Run();
        repo.addItem(r);

        JobExecutor exec = control.getExecutors().get(0);
        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);

        JobExecutor retrieve = control.getJobExecutor(r.getId());
        Assert.assertEquals(r.getId(), retrieve.getCurrentRunId());
        Assert.assertTrue(exec.isBusy());
    }
    
    /**
     * 
     */
    @Test
    public void testGetJobExecutorNotFound() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobController control = this.getJobController(env);

        Assert.assertNull(control.getJobExecutor("not-existing"));

        // create job
        String jobId = this.mockControl.createJob();

        // create run
        RunRepository repo = this.getRunRepository(env);
        Run r = new Run();
        r.setJobId(jobId);
        repo.addItem(r);

        JobExecutor exec = control.getExecutors().get(0);
        exec.setCurrentRunId(r.getId());
        exec.setCurrentRunId(jobId);

        Assert.assertNull(control.getJobExecutor("not-existing"));
        Assert.assertNull(control.getJobExecutor(null));
    }





    /* UTILITY METHODS */

    /**
     * 
     * @param env
     * @return
     */
    private DOCloudJobController getJobController(Environment env) {
    	
        return (DOCloudJobController) env.getAttribute(DOCloudJobController.JOB_CONTROLLER_INSTANCE);
    }

    /**
     * 
     * @param env
     * @return
     */
    private RunRepository getRunRepository(Environment env) {
    	
        return (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
    }


}
