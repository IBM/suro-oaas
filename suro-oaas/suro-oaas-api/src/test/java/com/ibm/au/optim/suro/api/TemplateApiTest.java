package com.ibm.au.optim.suro.api;


import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.core.controller.BasicDataSetController;
import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.core.controller.SuroRunController;
import com.ibm.au.optim.suro.model.control.*;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.control.job.JobExecutor;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.JobStatus;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.entities.RunLogEntry;
import com.ibm.au.optim.suro.model.entities.RunStatus;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.*;
import com.ibm.au.optim.suro.model.store.impl.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.jaws.data.DataServiceException;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.optim.oaas.client.job.model.JobSolveStatus;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.junit.Assert;

import java.io.*;
import java.util.*;

/**
 * @author Peter Ilfrich
 */
public class TemplateApiTest {
    
    private static double DELTA = 0.0000001;
    private String label = "some-label";
    
    private static final String MODEL_RESOURCE 		= 	"test-model.json";
    private static final String TEMPLATE_RESOURCE	=	"test-template.json";
    private static final String RUN_RESOURCE		=	"test-run.json";
    
    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testGetRunOptimData() {
    	
    	Environment env = this.getEnvironment();
    	TemplateApi api = this.getApi(env);
    	

    	
    	TemplateRepository sRepo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        RunRepository rnRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
        RunDetailsRepository orRepo = (RunDetailsRepository) env.getAttribute(RunDetailsRepository.DETAILS_REPOSITORY_INSTANCE);
        
        
        
        Template template = new Template();
        sRepo.addItem(template);

        Run run = new Run();
        run.setTemplateId(template.getId());
        rnRepo.addItem(run);

        // no results yet
        Response res = api.getRunOptimizationData(null, null);
        Assert.assertEquals(409, res.getStatus());

        res = api.getRunOptimizationData(null, "");
        Assert.assertEquals(409, res.getStatus());

        res = api.getRunOptimizationData(null, "foobar");
        Assert.assertEquals(409, res.getStatus());

        res = api.getRunOptimizationData(null, run.getId());
        Assert.assertEquals(409, res.getStatus());
            	
        RunDetails details = new RunDetails(run.getId(), new ArrayList<RunLogEntry>(), 13.37, 133.7, 1.337, null);

        orRepo.addItem(details);

        res = api.getRunOptimizationData(null, run.getId());
        Assert.assertEquals(409, res.getStatus());
        
        res = api.getRunOptimizationData(template.getId(), run.getId());
        Assert.assertEquals(200, res.getStatus());
        
        
        Assert.assertEquals(13.37, ((RunDetails) res.getEntity()).getBestBound(), TemplateApiTest.DELTA);
    }

    @Test
    public void testDownloadBlob() throws IOException {
    	
        Environment env = this.getEnvironment();
        TemplateApi api = this.getApi(env);

        Response res = api.getRunResultDocument(null, null, null, true);
        Assert.assertEquals(409, res.getStatus());
        res = api.getRunResultDocument(label, label, label, true);
        Assert.assertEquals(409, res.getStatus());

        RunRepository runRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
        TemplateRepository sRepo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        
        Run run = new Run();
        runRepo.addItem(run);
        runRepo.attach(run, "test.json", "application/json", IOUtils.toInputStream(label));

        res = api.getRunResultDocument(null, run.getId(), "test.json", true);
        Assert.assertEquals(409, res.getStatus());
        
        
        Template template = new Template();
        sRepo.addItem(template);
        run.setTemplateId(template.getId());
        
        res = api.getRunResultDocument(template.getId(), run.getId(), "test.json", true);
        Assert.assertEquals(200, res.getStatus());
        
        
        // read the blob and convert/check
        StreamingOutput output = (StreamingOutput) res.getEntity();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        output.write(os);
        Assert.assertEquals(label, new String(os.toByteArray()));

    }

    @Test
    public void testSubmitRun() throws IOException {
    	
        // test with not initialised api
        TemplateApi api = this.getApi(null);
        Response res = api.submitNewRun(null, null);
        Assert.assertEquals(500, res.getStatus());

        // init api and repositories
        Environment env = this.getEnvironment();
        api = this.getApi(env);
        
        
        RunRepository rRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
        TemplateRepository sRepo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        DataSetRepository dRepo = (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
        ModelRepository oRepo = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
        Core core = (Core) env.getAttribute(Core.CORE_INSTANCE);

        // setup demo data
        // model
        Model model = this.getResource(TemplateApiTest.MODEL_RESOURCE, Model.class);
        oRepo.addItem(model);
        
        // dataset
        DataSet dataset = new DataSet(label, model.getId(), null);
        dRepo.addItem(dataset);
        
        // template
        Template template = this.getResource(TemplateApiTest.TEMPLATE_RESOURCE, Template.class);
        template.setModelId(model.getId());
        sRepo.addItem(template);
        
        Run submission = this.getResource(TemplateApiTest.RUN_RESOURCE, Run.class);
        submission.setTemplateId(template.getId());
        submission.setModelId(model.getId());
        submission.setMaxRunTime(3 * 60 * 1000);

        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = mapper.writeValueAsBytes(submission);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        

        // core not ready
        res = api.submitNewRun(template.getId(), bis);
        Assert.assertEquals(503, res.getStatus());
        Assert.assertTrue(rRepo.getAll().isEmpty());

        // set core ready
        core.setReady(true);

        bis = new ByteArrayInputStream(bytes);
        
        // try again
        res = api.submitNewRun(template.getId(), bis);
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(1, rRepo.getAll().size());
    }
    
    @Test
    public void testStopRun() {
    	
    	Environment env = this.getEnvironment();
    	
        TemplateApi api = this.getApi(env);

        Response res = api.stopRun(null, null);
        Assert.assertEquals(409, res.getStatus());
        
        
        Template template = new Template();
        template.setLabel("Dummy");
        
        TemplateRepository repo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        repo.addItem(template);
        

        Run complete = new Run();
        complete.setTemplateId(template.getId());
        complete.setStatus(RunStatus.COMPLETED);
        complete.setSolveStatus(JobSolveStatus.OPTIMAL_SOLUTION.toString());
        

        Run executing = new Run();
        executing.setTemplateId(template.getId());
        executing.setStatus(RunStatus.PROCESSING);
        executing.setJobStatus(JobStatus.RUNNING);
        executing.setJobId(label);
        
        
        RunRepository runRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);

        runRepo.addItem(complete);
        runRepo.addItem(executing);

        res = api.stopRun(null, null);
        Assert.assertEquals(409, res.getStatus());

        res = api.stopRun(null, complete.getId());
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(RunStatus.COMPLETED, complete.getStatus());

        res = api.stopRun(null, executing.getId());
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(RunStatus.PROCESSING, executing.getStatus());

        Core core = (Core) env.getAttribute(Core.CORE_INSTANCE);
        core.setReady(true);

        res = api.stopRun(null, null);
        Assert.assertEquals(409, res.getStatus());


        res = api.stopRun(template.getId(), complete.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(RunStatus.COMPLETED, complete.getStatus());

        res = api.stopRun(template.getId(), executing.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(RunStatus.ABORTED, executing.getStatus());
    }

    @Test
    public void testDeleteRun() {
    	
    	Environment env = this.getEnvironment();
    	
        TemplateApi api = this.getApi(env);
        RunRepository runRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
        TemplateRepository repo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);

        Response res = api.deleteRun(null, null);
        Assert.assertEquals(409, res.getStatus());
        
        
        Template template = new Template();
        repo.addItem(template);
        

        Run run = new Run();
        run.setStatus(RunStatus.QUEUED);
        run.setTemplateId(template.getId());
        runRepo.addItem(run);

        // core not ready, always 406 - WHY ???
        res = api.deleteRun(null, null);
        Assert.assertEquals(409, res.getStatus());

        res = api.deleteRun(null, "");
        Assert.assertEquals(409, res.getStatus());

        res = api.deleteRun(null, "foobar");
        Assert.assertEquals(409, res.getStatus());

        res = api.deleteRun(null, run.getId());
        Assert.assertEquals(409, res.getStatus());
        
        res = api.deleteRun(template.getId(), run.getId());
        Assert.assertEquals(503, res.getStatus());

        // set core ready
        Core core = (Core) env.getAttribute(Core.CORE_INSTANCE);
        core.setReady(true);

        // test again
        res = api.deleteRun(null, null);
        Assert.assertEquals(409, res.getStatus());

        res = api.deleteRun(null, "");
        Assert.assertEquals(409, res.getStatus());

        res = api.deleteRun(null, "foobar");
        Assert.assertEquals(409, res.getStatus());

        Assert.assertFalse(runRepo.getAll().isEmpty());

        res = api.deleteRun(null, run.getId());
        Assert.assertEquals(409, res.getStatus());
        
        res = api.deleteRun(template.getId(), run.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertTrue(runRepo.getAll().isEmpty());
    }


    @Test
    public void testGetStrategies() {
    	
    	Environment env = this.getEnvironment();
    	TemplateApi api = this.getApi(env);

        Response res = api.getStrategies();
        Assert.assertEquals(404, res.getStatus());

        Model model = new Model(label, true);
        ModelRepository modelRepo = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
        modelRepo.addItem(model);

        res = api.getStrategies();
        Assert.assertEquals(200, res.getStatus());
        Assert.assertTrue(((List<?>) res.getEntity()).isEmpty());

        Template template1 = new Template();
        template1.setModelId(model.getId());

        Template template2 = new Template();
        template2.setModelId(model.getId());

        Template template3 = new Template();
        template3.setModelId("foobar");

        TemplateRepository repo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        
        repo.addItem(template1);
        repo.addItem(template2);
        repo.addItem(template3);

        res = api.getStrategies();
        Assert.assertEquals(2, ((List<?>) res.getEntity()).size());

        api = new TemplateApi();
        res = api.getStrategies();
        Assert.assertEquals(500, res.getStatus());
    }


    
    @Test
    public void testGetRepositories() {
    	
    	Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        TemplateApi api = this.getApi(env);

        try {
            api.getTemplateRepository();
            Assert.assertTrue(false);
        }
        catch (DataServiceException dse) {
        	Assert.assertTrue(true);
        }

        env.setAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE, new TransientTemplateRepository());

        Assert.assertNotNull(api.getTemplateRepository());
    }

    @Test
    public void testGetRunsForTemplate() {
    	
    	Environment env = this.getEnvironment();
    	TemplateApi api = this.getApi(env);

    	TemplateRepository repo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
    	
        Template template1 = new Template();
        Template template2 = new Template();
        repo.addItem(template1);
        repo.addItem(template2);

        Run run1 = new Run();
        run1.setTemplateId(template1.getId());
        Run run2 = new Run();
        run2.setTemplateId(template1.getId());
        Run run3 = new Run();
        run3.setTemplateId("foobar");
        
        
        RunRepository runRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);

        runRepo.addItem(run1);
        runRepo.addItem(run2);
        runRepo.addItem(run3);

        Response res = api.getRunsForTemplate(null);
        Assert.assertEquals(409, res.getStatus());

        res = api.getRunsForTemplate("not-existing");
        Assert.assertEquals(409, res.getStatus());

        res = api.getRunsForTemplate(template2.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertTrue(((List<?>) res.getEntity()).isEmpty());

        res = api.getRunsForTemplate(template1.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, ((List<?>) res.getEntity()).size());
    }
    
    @Test
    public void testGetTemplate() {
    	
        Environment env = this.getEnvironment();
        TemplateApi api = this.getApi(env);
        
        Response res = api.getTemplate(null);
        Assert.assertEquals(404, res.getStatus());

        res = api.getTemplate("not-existing");
        Assert.assertEquals(404, res.getStatus());

        Template template = new Template();
        template.setLabel(label);
        template.setDescription(label);
        template.setModelId(label);
        
        TemplateRepository repo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        repo.addItem(template);

        res = api.getTemplate("not-existing");
        Assert.assertEquals(404, res.getStatus());

        res = api.getTemplate(template.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(label, ((Template) res.getEntity()).getLabel());
    }

    @Test
    public void testGetTemplateRun() {
    	
        Environment env = this.getEnvironment();
        TemplateApi api = this.getApi(env);

        Response res = api.getTemplateRun(null, null);
        Assert.assertEquals(409, res.getStatus());


        res = api.getTemplateRun(null, "not-existing");
        Assert.assertEquals(409, res.getStatus());

        res = api.getTemplateRun("not-existing", null);
        Assert.assertEquals(409, res.getStatus());

        res = api.getTemplateRun("not-existing", "not-existing");
        Assert.assertEquals(409, res.getStatus());

        Run r = new Run();
        r.setFinalGap(13.37);
        r.setTemplateId("foobar");
        RunRepository rRepo = (RunRepository) env.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
        rRepo.addItem(r);

        Template s = new Template();
        s.setLabel(label);
        
        TemplateRepository sRepo = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        
        sRepo.addItem(s);

        res = api.getTemplateRun(null, r.getId());
        Assert.assertEquals(409, res.getStatus());

        // [CV] NOTE: at this point the repository does not have the template
        //			  set for the run and it will still return 409
        //
        res = api.getTemplateRun(s.getId(), r.getId());
        Assert.assertEquals(409, res.getStatus());
        
        // [CV] NOTE: we now set the proper template identifier and check again.
        //
        r.setTemplateId(s.getId());
        rRepo.updateItem(r);

        res = api.getTemplateRun(s.getId(), r.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(13.37, ((Run) res.getEntity()).getFinalGap(), TemplateApiTest.DELTA);

        res = api.getTemplateRun("foobar", r.getId());
        Assert.assertEquals(409, res.getStatus());

    }
    


    /**
     * This helper method creates an instance of the {@link Environment}
     * implementation for the purpose of testing.
     * 
     * @return	a {@link Environment} implementation to use for injection
     * 			in the API.
     */
    private Environment getEnvironment() {
    	
        // repositories
        TransientRunRepository runRepo = new TransientRunRepository();
        TransientTemplateRepository templateRepo = new TransientTemplateRepository();
        TransientModelRepository modelRepo = new TransientModelRepository();
        TransientDataSetRepository dataSetRepo = new TransientDataSetRepository();

        // run controller
        SuroRunController runController = new SuroRunController();
        runController.setRepository(runRepo);

        // run controller
        BasicModelController modelController = new BasicModelController();
        modelController.setRepository(modelRepo);

        // run controller
        BasicDataSetController dataSetController = new BasicDataSetController();
        dataSetController.setRepository(dataSetRepo);

        // core
        SuroCore core = new SuroCore();

        // mock job controller
        JobController jobController = new JobController() {
            @Override
            public void completeJob(String runId) {
                return;
            }

            @Override
            public boolean abortJob(String runId) {
                return true;
            }

            @Override
            public void cleanupJobs() {
                return;
            }

            @Override
            public void createJobExecutor(int index) {
                return;
            }

            @Override
            public JobExecutor getJobExecutor(String runId) {
                return null;
            }

            @Override
            public List<JobExecutor> getExecutors() {
                return null;
            }

            @Override
            public void deleteJob(String jobId) {
                return;
            }

            @Override
            public ResultManager getResultManager() { return null; }
        };

        // store environment variables
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(RunRepository.RUN_REPOSITORY_INSTANCE, runRepo);
        env.setAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE, dataSetRepo);
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, modelRepo);
        env.setAttribute(RunDetailsRepository.DETAILS_REPOSITORY_INSTANCE, new TransientOptimizationResultRepository());
        env.setAttribute(RunController.RUN_CONTROLLER_INSTANCE, runController);
        env.setAttribute(Core.CORE_INSTANCE, core);
        env.setAttribute(JobController.JOB_CONTROLLER_INSTANCE, jobController);
        env.setAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE, dataSetController);
        env.setAttribute(ModelController.MODEL_CONTROLLER_INSTANCE, modelController);
        env.setAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE, templateRepo);

        // bind services that require binding
        templateRepo.bind(env);
        core.bind(env);
        
        // set environment for the API
        return env;
    }
    
    /**
     * This helper method creates and configures an instance of the
     * {@link TemplateApi} class with the given {@link Environment}
     * implementation.
     *
     * @param env	an {@link Environment} implementation, it can be
     * 				{@literal null}.
     * 
     * @return	a {@link TemplateApi} instance used for testing.
     */
    private TemplateApi getApi(Environment env) {
    	
    	TemplateApi api = new TemplateApi();
    	api.environment = env;
    	
    	return api;
    }
    
    /**
     * This method reads the resource indicated by <i>resourcePath</i> and creates a corresponding
     * instance of the generic type that specialises this method. The method internally uses the
     * {@link ObjectMapper} to deserialise the <i>JSON</i> representation of the instance into the
     * corresponding bean.
     * 
     * @param resourcePath			a {@link String} containing the path to the resource in the
     * 								test resources folder that contains the <i>JSON</i> representation
     * 								of the bean.
     * @param clazz					a {@link Class} that contains the type information of the instances
     * 								to be created and casted.
     * 
     * @return	a instance of the specified type if the deserialisation is successful.
     * 
     * @throws JsonParseException		if there is any error in parsing the JSON content of the resource file
     * @throws JsonMappingException		if there is any error in mapping the valid JSON content to the bean
     * 									properties.
     * @throws IOException				if there is any other type of I/O error.
     */
    private <T> T getResource(String resourcePath, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
    	
    	T zombie = null;
    	InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
    	if (stream != null) {
    	
    		zombie = (T) mapper.readValue(stream, clazz);
    	
    	} else {
    		
    		Assert.fail("Could not find resource: " + resourcePath);
    	}
    	
    	return zombie;

    }

}
