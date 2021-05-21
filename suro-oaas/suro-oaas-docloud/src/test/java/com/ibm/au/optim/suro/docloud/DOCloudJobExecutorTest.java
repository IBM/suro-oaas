package com.ibm.au.optim.suro.docloud;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.oaas.test.MockController;
import com.ibm.au.optim.oaas.test.MockServer;
import com.ibm.au.optim.oaas.test.payload.*;
import com.ibm.au.optim.suro.docloud.job.impl.DOCloudJobMonitor;
import com.ibm.au.optim.suro.docloud.job.impl.DOCloudResumeJobMonitor;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Parameter;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;
import com.ibm.au.optim.suro.model.entities.JobStatus;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.optim.oaas.client.job.JobRequest;
import com.ibm.optim.oaas.client.job.model.Job;
import com.ibm.optim.oaas.client.job.model.JobExecutionStatus;
import com.ibm.optim.oaas.client.job.model.JobSolveStatus;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.*;


/**
 * @author Peter Ilfrich
 */
public class DOCloudJobExecutorTest extends DOCloudTest {

    MockController mc = new MockController(MockServer.DEFAULT_PORT);
    ObjectMapper mapper = new ObjectMapper();

    /**
     * 
     */
    @Test
    public void testBusyFlag() {
    	
        DOCloudJobExecutor exec = this.createJobExecutor(this.createEnvironment(1, false));
        Assert.assertFalse(exec.isBusy());

        exec.setBusy(true);
        Assert.assertTrue(exec.isBusy());

        exec.setBusy(false);
        Assert.assertFalse(exec.isBusy());
    }

    /**
     * 
     */
    @Test
    public void testCurrentRunNotExisting() {
    	
        DOCloudJobExecutor exec = this.createJobExecutor(this.createEnvironment(1, false));
        Assert.assertNull(exec.getCurrentRunId());
        Assert.assertNull(exec.getCurrentRun());
    }

    /**
     * 
     */
    @Test
    public void testCurrentJobId() {
    	
        DOCloudJobExecutor exec = this.createJobExecutor(this.createEnvironment(1, false));
        Assert.assertNull(exec.getCurrentJobId());

        exec.setCurrentJobId("new-job-id");
        Assert.assertEquals("new-job-id", exec.getCurrentJobId());

        exec.setCurrentJobId("something-else");
        Assert.assertEquals("something-else", exec.getCurrentJobId());

        exec.setCurrentJobId(null);
        Assert.assertNull(exec.getCurrentJobId());
    }

    /**
     * 
     */
    @Test
    public void testCurrentRun() {
    	
        Environment env = this.createEnvironment(0, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);
        RunRepository repo = this.getRunRepository(env);

        Run r = new Run();
        r.setStatus(RunStatus.PROCESSING);
        repo.addItem(r);

        exec.setCurrentRunId(r.getId());
        Assert.assertEquals(r.getId(), exec.getCurrentRunId());
        Assert.assertEquals(RunStatus.PROCESSING, exec.getCurrentRun().getStatus());
    }

    /**
     * 
     */
    @Test
    public void testFinish() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        this.getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setCurrentJobId("abc");
        exec.setBusy(true);
        exec.currentMonitor = new DOCloudResumeJobMonitor(env, exec, r.getId(), this.getJobController(env).getJobClient());

        exec.finish();

        Assert.assertNull(exec.getCurrentJobId());
        Assert.assertNull(exec.getCurrentRun());
        Assert.assertNull(exec.getCurrentRunId());
        Assert.assertNull(exec.currentMonitor);
        Assert.assertFalse(exec.isBusy());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testQuit() throws Exception {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        String jobId = mc.createJob();
        MockJob job = mc.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.INTERRUPTING.toString());
        mc.updateJob(job);

        Run r = new Run();
        r.setJobId(jobId);
        getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setCurrentJobId(jobId);
        exec.setBusy(true);
        exec.currentMonitor = new DOCloudResumeJobMonitor(env, exec, r.getId(), this.getJobController(env).getJobClient());
        // make sure the monitor is initiated and continued the first check
        Thread.sleep(200);
        // cancel the monitor
        exec.quit();
        // wait for the monitor to finish
        Thread.sleep(500);

        Assert.assertFalse(exec.active);
        // wait to make sure the future (monitor) is canceled.

        Assert.assertTrue(((DOCloudJobMonitor) exec.currentMonitor).getMonitor().isCancelled());
    }

    /**
     * 
     */
    @Test
    public void testQuitWithoutMonitor() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        this.getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setCurrentJobId("abc");
        exec.setBusy(true);

        exec.quit();

        Assert.assertFalse(exec.active);
    }

    /**
     * 
     */
    @Test
    public void testAbortNoJobId() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        this.getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setCurrentJobId("abc");
        exec.setBusy(true);
        exec.currentMonitor = new DOCloudResumeJobMonitor(env, exec, r.getId(), this.getJobController(env).getJobClient());

        exec.setCurrentJobId(null);

        exec.abort();

        Assert.assertNull(exec.currentMonitor);
        Assert.assertNull(exec.getCurrentJobId());
        Assert.assertFalse(exec.isBusy());
    }

    /**
     * 
     */
    @Test
    public void testAbortNoJobNoMonitor() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        this.getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);
        exec.setCurrentJobId(null);

        Assert.assertTrue(exec.abort());

        Assert.assertNull(exec.currentMonitor);
        Assert.assertNull(exec.getCurrentJobId());
        Assert.assertFalse(exec.isBusy());
    }

    /**
     * 
     */
    @Test
    public void testAbortJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        mc.reset();
        String jobId = mc.createJob();
        MockJob job = mc.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.RUNNING.toString());
        mc.updateJob(job);

        Run r = new Run();
        this.getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);
        exec.setCurrentJobId(jobId);

        Assert.assertTrue(exec.abort());
        Assert.assertTrue(exec.isBusy());
        Assert.assertNotNull(exec.getCurrentJobId());
        Assert.assertNotNull(exec.getCurrentRunId());

        job = mc.getJob(jobId);
        Assert.assertEquals(JobExecutionStatus.INTERRUPTED.toString(), job.getExecutionStatus());
    }


    /**
     * 
     */
    @Test
    public void testAbortInvalidJobId() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        mc.reset();
        String jobId = mc.createJob();
        MockJob job = mc.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.RUNNING.toString());
        mc.updateJob(job);

        Run r = new Run();
        r.setJobId("not-existing");
        this.getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);
        exec.setCurrentJobId("not-existing");

        Assert.assertTrue(exec.abort());
        Assert.assertEquals(JobExecutionStatus.RUNNING.toString(), mc.getJob(jobId).getExecutionStatus());
    }


    /**
     * 
     */
    @Test
    public void testAbortInvalidPort() {
    	
        Properties properties = this.createProperties(1, null);
        properties.setProperty(DOCloudJobController.CFG_DOCLOUD_API_URL, "http://localhost:13337");
        Environment env = this.createEnvironment(1, true, properties);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        mc.reset();
        String jobId = mc.createJob();
        MockJob job = mc.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.RUNNING.toString());
        mc.updateJob(job);

        Run r = new Run();
        getRunRepository(env).addItem(r);

        exec.setCurrentRunId(r.getId());
        exec.setBusy(true);
        exec.setCurrentJobId(jobId);

        Assert.assertFalse(exec.abort());
        Assert.assertEquals(JobExecutionStatus.RUNNING.toString(), mc.getJob(jobId).getExecutionStatus());
    }

    /**
     * 
     */
    @Test
    public void testRunCompletedWithProcessingJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        r.setStatus(RunStatus.PROCESSING);
        r.setJobStatus(JobStatus.RUNNING);
        this.getRunRepository(env).addItem(r);

        exec.runCompleted(r.getId());

        Assert.assertEquals(RunStatus.PROCESSING, r.getStatus());
        Assert.assertEquals(JobStatus.RUNNING, r.getJobStatus());
    }

    /**
     * 
     */
    @Test
    public void testRunCompleted() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        r.setStatus(RunStatus.COLLECTING_RESULTS);
        r.setJobStatus(JobStatus.RUNNING);
        getRunRepository(env).addItem(r);

        exec.runCompleted(r.getId());

        Assert.assertEquals(RunStatus.COMPLETED, r.getStatus());
        Assert.assertEquals(JobStatus.COMPLETED, r.getJobStatus());
    }

    /**
     * 
     */
    @Test
    public void testRunAbortedProcessingJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        r.setStatus(RunStatus.PROCESSING);
        r.setJobStatus(JobStatus.RUNNING);
        getRunRepository(env).addItem(r);

        exec.runAborted(r.getId());

        Assert.assertEquals(RunStatus.PROCESSING, r.getStatus());
        Assert.assertEquals(JobStatus.RUNNING, r.getJobStatus());
    }

    /**
     * 
     */
    @Test
    public void testRunAborted() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        r.setStatus(RunStatus.ABORTING);
        r.setJobStatus(JobStatus.RUNNING);
        getRunRepository(env).addItem(r);

        exec.runAborted(r.getId());

        Assert.assertEquals(RunStatus.ABORTED, r.getStatus());
        Assert.assertEquals(JobStatus.INTERRUPT, r.getJobStatus());
    }

    /**
     * 
     */
    @Test
    public void testRunFailedInvalidState() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        r.setStatus(RunStatus.ABORTED);
        r.setJobStatus(JobStatus.INTERRUPT);
        this.getRunRepository(env).addItem(r);

        exec.runAborted(r.getId());

        Assert.assertEquals(RunStatus.ABORTED, r.getStatus());
        Assert.assertEquals(JobStatus.INTERRUPT, r.getJobStatus());

        r.setStatus(RunStatus.COMPLETED);
        this.getRunRepository(env).updateItem(r);

        exec.runFailed(r.getId());

        Assert.assertEquals(RunStatus.COMPLETED, r.getStatus());
        Assert.assertEquals(JobStatus.INTERRUPT, r.getJobStatus());
    }

    /**
     * 
     */
    @Test
    public void testRunFailed() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        Run r = new Run();
        r.setStatus(RunStatus.PROCESSING);
        r.setJobStatus(JobStatus.RUNNING);
        this.getRunRepository(env).addItem(r);

        exec.runFailed(r.getId());

        Assert.assertEquals(RunStatus.FAILED, r.getStatus());
        Assert.assertEquals(JobStatus.FAILED, r.getJobStatus());
    }


    /**
     * 
     * @throws Exception
     */
    @Test
    public void testGetJobClient() throws Exception {
    	
        mc.reset();
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);
        String jobId = mc.createJob();

        Job job = exec.getJobClient().getJob(jobId);
        Assert.assertEquals(jobId, job.getId());
    }

    /**
     * 
     */
    @Test
    public void testGetJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        mc.reset();
        String jobId = mc.createJob();

        Assert.assertNull(exec.getJob(null));
        Assert.assertNull(exec.getJob("not-existing"));

        Job job = exec.getJob(jobId);
        Assert.assertEquals(jobId, job.getId());


        Properties props = new Properties();
        props.setProperty(DOCloudJobController.CFG_DOCLOUD_API_URL, "http://localhost:1337/not-existing/");
        env = this.createEnvironment(1, true, props);
        exec = this.createJobExecutor(env);

        mc.reset();
        jobId = mc.createJob();
        Assert.assertNull(exec.getJob(jobId));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testGetSolutionResult() throws Exception {
    	
        // downloads the solution.json and stores it in the repository
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);

        String jobId = mc.createJob();
        String content = "{\"key\":\"value\"}";
        mc.createAttachment(jobId, new MockAttachment("solution.json", true, content.getBytes().length, IOUtils.toInputStream(content)));

        Run run = this.getRunRepository(env).getLast();
        run.setJobId(jobId);

        exec.setCurrentJobId(jobId);
        exec.setCurrentRunId(run.getId());

        JsonNode node = exec.getSolutionResult();
        Assert.assertEquals("value", node.get("key").textValue());

        mc.deleteAttachment(jobId, "solution.json");
        Assert.assertNull(exec.getSolutionResult());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCollectionSolutionLog() throws Exception {
    	
        // downloads the solution.json and stores it in the repository
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);

        String jobId = mc.createJob();
        String content = "some-content";
        List<MockLogRecord> records = new ArrayList<>();
        Date date = new Date();
        records.add(new MockLogRecord(content, date.getTime(), MockLogRecord.LEVEL_INFO));
        mc.createLogItem(jobId, new MockLogItem("0", jobId, records));

        Run run = this.getRunRepository(env).getLast();
        run.setJobId(jobId);

        exec.setCurrentJobId(jobId);
        exec.setCurrentRunId(run.getId());

        exec.collectSolutionLog();
        InputStream stream = this.getRunRepository(env).getAttachment(run.getId(), "log.txt");
        List<MockLogItem> logItems = mapper.readValue(IOUtils.toString(stream), new TypeReference<List<MockLogItem>>() { });
        Assert.assertEquals(content, logItems.get(0).getRecords().get(0).getMessage());

        this.getRunRepository(env).removeAll();
        run.setId(null);
        this.getRunRepository(env).addItem(run);
        mc.deleteAttachment(jobId, "log.txt");
        exec.collectSolutionLog();

        Assert.assertNull(this.getRunRepository(env).getAttachment(run.getId(), "log.txt"));
    }

    /**
     * 
     */
    @Test
    public void testJobExistsInDOCloud() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        mc.reset();
        String jobId = mc.createJob();

        Assert.assertFalse(exec.jobExistsInDOCloud());

        exec.setCurrentJobId("not-existing");
        Assert.assertFalse(exec.jobExistsInDOCloud());
        
        exec.setCurrentJobId(jobId);
        Assert.assertTrue(exec.jobExistsInDOCloud());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCreateNewJob() throws Exception {
    	
        // prepare environment
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);
        Run run = this.getRunRepository(env).getLast();

        // THIS IS WHERE THE ACTION HAPPENS
        exec.setCurrentRunId(run.getId());
        exec.createNewJob();

//        List<MockJob> jobList = mc.getAllJobs();
//        Assert.assertEquals(1, jobList.size());
//        Assert.assertEquals(exec.getCurrentJobId(), jobList.get(0).getId());
//        Assert.assertEquals(3, mc.getAllAttachments(exec.getCurrentJobId()).size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCreateJobWithLimit() throws Exception {
    	
        // prepare environment
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);
        Run run = this.getRunRepository(env).getLast();

        mc.updateConfiguration(new MockServerConfiguration(true, false, false, false, false, false, false));

        // THIS IS WHERE THE ACTION HAPPENS
        exec.setCurrentRunId(run.getId());
        exec.createNewJob();

        Assert.assertEquals(0, mc.getAllJobs().size());
        Assert.assertEquals(0, mc.getAllAttachments(exec.getCurrentJobId()).size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCreateJobRequest() throws Exception {
    	
        // prepare environment
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);
        
        Run run = this.getRunRepository(env).getLast();
        List<Parameter> parameters = run.getParameters();
        
        Map<String, String[]> params = new HashMap<>();
        for (Parameter p: parameters) {
        	Object value = p.getValue();
            params.put(p.getName(), new String[] { value == null ? null : value.toString() });
        }
        
        PipedInputStream jobLiveLogInStream = new PipedInputStream(2000);
        PipedOutputStream outputStream = new PipedOutputStream(jobLiveLogInStream);
        DataSet dataset = ((DataSetController) env.getAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE)).getDataSet(run.getDataSetId(), true);
        Model model = ((ModelController) env.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE)).getModel(run.getModelId(), true);

        // ACTION
        JobRequest req = exec.createJobRequest(run, model, dataset, outputStream);
        Assert.assertNotNull(req);

        Assert.assertEquals(0, mc.getAllJobs().size());
        exec.getJobClient().createJob(req.getData());

        Assert.assertEquals(JobExecutionStatus.CREATED.toString(), mc.getAllJobs().get(0).getExecutionStatus());
        Assert.assertEquals(1, mc.getAllJobs().size());
    }


    /**
     * 
     */
    @Test
    public void testResumeJob() {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);

        Run run = this.getRunRepository(env).getLast();
        String jobId = mc.createJob();
        run.setJobId(jobId);
        run.setStatus(RunStatus.RESUME);
        MockJob job = mc.getJob(jobId);
        job.setExecutionStatus(JobExecutionStatus.RUNNING.toString());
        mc.updateJob(job);

        exec.setCurrentRunId(run.getId());
        exec.setCurrentJobId(jobId);

        exec.resumePreviousJob();

        Assert.assertNotNull(exec.currentMonitor);
    }


    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCollectResults() throws Exception {
    	
        Environment env = this.createEnvironment(1, true);
        DOCloudJobExecutor exec = this.setupFullEnvironment(env);

        // retrieve run from repo
        Run run = this.getRunRepository(env).getLast();

        // create job and connect them
        String jobId = mc.createJob();
        run.setJobId(jobId);
        this.getRunRepository(env).updateItem(run);

        // add solution.json
        mc.createAttachment(jobId, new MockAttachment("solution.json", true, 0, this.getClass().getResourceAsStream("/solution.json")));

        MockJob job = mc.getJob(jobId);
        // add gap to job details
        Map<String, String> detailMap = new HashMap<>();
        detailMap.put("PROGRESS_GAP", "0.5436231803936693");
        job.setDetails(detailMap);

        // set solve status
        job.setSolveStatus(JobSolveStatus.OPTIMAL_SOLUTION.toString());
        // update the job in DOCloud
        mc.updateJob(job);

        // update executor
        exec.setCurrentRunId(run.getId());
        exec.setCurrentJobId(jobId);

        // prepare model
        Model model = this.getModelRepository(env).findByDefaultFlag();
        
        @SuppressWarnings("unchecked")
		List<OutputMapping> mappings = (List<OutputMapping>) mapper.readValue(this.getClass().getResourceAsStream("/optimization-output.json"), 
        																	  new TypeReference<List<OutputMapping>>() { });
        
        model.setOutputMappings(mappings);

        // ACTION
        exec.collectResults(run);

        
        RunRepository runRepo = this.getRunRepository(env);
        
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "beds.csv"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "icu.csv"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "patients.csv"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "sessions.csv"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "surgeries.csv"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "targets.csv"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "waitlist.csv"));
        // TODO: check why this is still null, updated the output mapping, but it is still not generated.
        //assertNotNull(getRunRepository(env).getAttachment(run.getId(), "schedule.json"));
        Assert.assertNotNull(runRepo.getAttachment(run.getId(), "schedule-surgery-types.json"));
        Assert.assertNull(runRepo.getAttachment(run.getId(), "log.txt"));
    }



    /* UTILITY METHODS */

    /**
     * 
     * @param env
     * @return
     */
    protected DOCloudJobExecutor setupFullEnvironment(Environment env) {
    	
        mc.reset();

        // prepare data
        Run run = new Run();
        List<Parameter> parameters = new ArrayList<Parameter>();
        
        parameters.add(new Parameter("inputExtraNormalBeds", ""));
        parameters.add(new Parameter("maxRuntime", 1.5));
        parameters.add(new Parameter("inputWeightOntime", ""));
        parameters.add(new Parameter("inputExtraIcuBeds", ""));
        parameters.add(new Parameter("optimalityBoundary", 95.11));
        parameters.add(new Parameter("inputWeightOverdue", ""));
        

        run.setParameters(parameters);
        run.setStatus(RunStatus.QUEUED);
        
        

        // prepare environment
        DOCloudJobExecutor exec = this.createJobExecutor(env);

        // prepare repositories
        this.getRunRepository(env).addItem(run);
        Model model = new Model("default", true);
        
        
        ModelRepository modelRepo = this.getModelRepository(env);
        modelRepo.addItem(model);
        modelRepo.attach(model, "script.mod", "text/plain", this.getClass().getResourceAsStream("/default-model.mod"));
        run.setModelId(model.getId());
        DataSet dataset = new DataSet("default", model.getId(), null);
        
        DataSetRepository dataSetRepo = this.getDataSetRepository(env);
        dataSetRepo.addItem(dataset);
        dataSetRepo.attach(dataset, "data.dat", "text/plain", this.getClass().getResourceAsStream("/default-dataset.dat"));
        run.setDataSetId(dataset.getId());

        Template strategy = new Template();
        this.getTemplateRepository(env).addItem(strategy);
        run.setTemplateId(strategy.getId());

        return exec;
    }
    /**
     * 
     * @param env
     * @return
     */
    private DOCloudJobController getJobController(Environment env) {
    	
        return (DOCloudJobController) env.getAttribute(JobController.JOB_CONTROLLER_INSTANCE);
    }
    /**
     * 
     * @param env
     * @return
     */
    private RunRepository getRunRepository(Environment env) {
    	
        return (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
    }
    /**
     * 
     * @param env
     * @return
     */
    private DOCloudJobExecutor createJobExecutor(Environment env) {
    	
        return new DOCloudJobExecutor(1, env, (JobController) env.getAttribute(JobController.JOB_CONTROLLER_INSTANCE), 1000);
    }
    /**
     * 
     * @param env
     * @return
     */
    private DataSetRepository getDataSetRepository(Environment env) {
    	
        return (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
    }
    /**
     * 
     * @param env
     * @return
     */
    private ModelRepository getModelRepository(Environment env) {
    	
        return (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }
    /**
     * 
     * @param env
     * @return
     */
    private TemplateRepository getTemplateRepository(Environment env) {
    	
        return (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
    }
}
