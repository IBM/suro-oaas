package com.ibm.au.optim.suro.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.core.controller.BasicDataSetController;
import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.core.controller.SuroRunController;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.control.ResultManager;
import com.ibm.au.optim.suro.model.control.RunController;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.control.job.JobExecutor;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunDetailsRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientDataSetRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientOptimizationResultRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientRunRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientTemplateRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.data.DataServiceException;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.optim.oaas.client.job.model.JobSolveStatus;

public class RunApiTest {

	@Test
	public void testGetLast() {

		Environment environment = this.getEnvironment();
		RunApi api = this.getApi(null, environment);

		Response res = api.getLastRun();
		assertEquals(404, res.getStatus());

		RunRepository repo = (RunRepository) environment.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);

		// create demo runs
		Run run1 = new Run();
		run1.setStartTime(new Date().getTime());
		repo.addItem(run1);

		Run run2 = new Run();
		run2.setStartTime(new Date().getTime());
		repo.addItem(run2);

		Run run3 = new Run();
		run3.setStartTime(new Date().getTime());
		repo.addItem(run3);

		// create the last run to retrieve
		Run run = new Run();
		run.setSolveStatus(JobSolveStatus.OPTIMAL_SOLUTION.toString());
		run.setStartTime(new Date().getTime() + 5000);
		repo.addItem(run);

		res = api.getLastRun();
		assertEquals(200, res.getStatus());
		Run lastRun = (Run) res.getEntity();
		assertNotNull(lastRun);

		// assertEquals(run.getId(), lastRun.getId());
		// assertEquals(JobSolveStatus.OPTIMAL_SOLUTION.toString(),
		// lastRun.getSolveStatus());
	}

	@Test
	public void testGetRepositories() {

		Environment env = EnvironmentHelper.mockEnvironment((Properties) null);

		RunApi api = this.getApi(null, env);
		try {
			api.getRunRepository();
			assertTrue(false);
		} catch (DataServiceException dse) {
			assertTrue(true);
		}

		try {
			api.getOptimizationResultRepository();
			assertTrue(false);
		} catch (DataServiceException dse) {
			assertTrue(true);
		}

		env.setAttribute(RunRepository.RUN_REPOSITORY_INSTANCE, new TransientRunRepository());
		env.setAttribute(RunDetailsRepository.DETAILS_REPOSITORY_INSTANCE, new TransientOptimizationResultRepository());

		assertNotNull(api.getRunRepository());
		assertNotNull(api.getOptimizationResultRepository());
	}

	/**
	 * This method tests that the API method {@link RunApi#getMediaType(String)}
	 * returns the expected media types for the different types of files that
	 * are retrieved as results. It also checks that the method defaults to
	 * {@link MediaType#TEXT_PLAIN_TYPE} when not known media type identifier is
	 * passed as argument.
	 */
	@Test
	public void testMediaType() {

		RunApi api = this.getApi(null, null);

		assertEquals(MediaType.APPLICATION_JSON_TYPE, api.getMediaType("test.json"));
		assertEquals(MediaType.TEXT_PLAIN_TYPE, api.getMediaType("something.log"));
		assertEquals(MediaType.TEXT_PLAIN_TYPE, api.getMediaType("something.txt"));
		assertEquals(MediaType.TEXT_PLAIN_TYPE, api.getMediaType("something.md"));
		assertEquals(new MediaType("text", "csv"), api.getMediaType("something.csv"));
		assertNull(api.getMediaType(null));
		assertNull(api.getMediaType(""));
		assertEquals(MediaType.TEXT_PLAIN_TYPE, api.getMediaType("placeholder"));
		assertEquals(MediaType.TEXT_PLAIN_TYPE, api.getMediaType(".placeholder"));
		assertEquals(MediaType.TEXT_PLAIN_TYPE, api.getMediaType(".gitignore"));
		assertEquals(MediaType.APPLICATION_JSON_TYPE, api.getMediaType("something.csv.json"));
		assertEquals(new MediaType("text", "csv"), api.getMediaType("something.json.csv"));
	}

	/**
	 * This helper method creates an instance of the {@link Environment}
	 * implementation for the purpose of testing.
	 * 
	 * @return a {@link Environment} implementation to use for injection in the
	 *         API.
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
			public ResultManager getResultManager() {
				return null;
			}
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
	 * This is an helper method that is used to create/retrieve a {@link RunApi}
	 * instance for the purpose of test.
	 * 
	 * @param strategy
	 *            a {@link Strategy} reference, that if not {@literal null}
	 *            represents the specific strategy instance that is used to
	 *            filter the set of {@link Run} instances served and accessible
	 *            by the API.
	 * 
	 * @param environment
	 *            an {@link Environment} implementation that will be injected
	 *            into the created {@link RunApi} instance. It can be
	 *            {@literal null}.
	 * 
	 * @return a {@link RunApi} instance.
	 */
	private RunApi getApi(Template template, Environment environment) {

		RunApi api = new RunApi(template, environment, null);

		return api;

	}

}
