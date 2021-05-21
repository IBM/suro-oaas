package com.ibm.au.optim.suro.api;

import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;

/**
 * Class <b>ModelApiTest</b>. This class is used to test the API for accessing the configured
 * {@link Model} instances.
 * 
 * @author Christian Vecchiola
 *
 */
public class ModelApiTest {
	
	/**
	 * {@link Environment} instance that will be injected into the {@link HospitalApi}
	 * and that constitutes the shared environment between the test class and the
	 * instance being tested.
	 */
	private Environment env = null;
	
	/**
	 * {@link ModelController} implementation that is used by the {@link OptimizationModelApi}
	 * to operate on {@link Model} instances.
	 */
	private ModelController controller = new BasicModelController();
	
	/**
	 * {@link OptimizationModelApi} instance under test. This instance is continuously recreated
	 * before the execution of every test.
	 */
    private OptimizationModelApi api = new OptimizationModelApi();

    /**
     * {@link ModelRepository} implementation that is used to configure the controller
     * instance used by the {@link OptimizationModelApi} instance under test.
     */
    private ModelRepository modelRepo = new TransientModelRepository();
    

	
	
	/**
	 * This method tests the functionality exposed by the API to retrieve the
	 * list of models. The method tests the following scenarios:
	 * <ul>
	 * <li>retrieving the list of models when the list is empty: expected code 200, returned empty list.</li>
	 * <li>retrieving the list of models when the list contains some models: expected code 200, the list should contain the models.</li>
	 * <li>retrieving the list of models when the repository is not available: expected code 503.</li>
	 * <li>retrieving the list of models when the controller is not available: expected code 503.</li>
	 * </ul>
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetModels() {
		
		this.setupApi();
		
		// 1. empty repository: 200 and empty list.
		//
		Response res = this.api.getModels();
		Assert.assertEquals(200, res.getStatus());
		List<Model> actual = (List<Model>) res.getEntity();
		Assert.assertNotNull(actual);
		Assert.assertEquals(0, actual.size());
		
		// 2. with add one model: 200 and a list containing the model.
		//
		Model model = this.getModel();
		this.modelRepo.addItem(model);
		
		res = this.api.getModels();
		Assert.assertEquals(200, res.getStatus());
		actual = (List<Model>) res.getEntity();
		Assert.assertNotNull(actual);
		Assert.assertEquals(1, actual.size());
		
		Assert.assertEquals(model.getId(), actual.get(0).getId());
		
		// 3. remove the controller for models.
		//
		this.env.removeAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
		res = this.api.getModels();
		Assert.assertEquals(503, res.getStatus());
		
		// we ensure that the repository is neither accessible directly or
		// via the environment.
		//
		this.env.setAttribute(ModelController.MODEL_CONTROLLER_INSTANCE, this.controller);
		this.env.removeAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
		this.controller.setRepository(null);
		
		res = this.api.getModels();
		Assert.assertEquals(503, res.getStatus());
		
	}
	
	/**
	 * This method tests the functionality exposed by the API to retrieve a specific model.
	 * The method tests the following scenarios:
	 * <ul>
	 * <li>retrieving a non existing model when the repository is empty: expected code 404.</li>
	 * <li>retrieving an existing model in the repository: expected code 200, the body should contains the model.</li>
	 * <li>retrieving the list of models when the controller is not available: expected code 503.</li>
	 * </ul>
	 */
	@Test
	public void testGetModel() {
		
		this.setupApi();
		
		// empty repository.
		//
		Response res = this.api.getModel(null);
		Assert.assertEquals(404, res.getStatus());
		
		res = this.api.getModel("thisDoesNotExist");
		Assert.assertEquals(404, res.getStatus());
		
		// we add the model.
		//
		Model model = this.getModel();
		this.modelRepo.addItem(model);
		
		res = this.api.getModel("thisDoesNotExist");
		Assert.assertEquals(404, res.getStatus());
		
		res = this.api.getModel(model.getId());
		Assert.assertEquals(200, res.getStatus());
		Model actual = (Model) res.getEntity();
		Assert.assertNotNull(actual);
		Assert.assertEquals(model.getId(), actual.getId());
		
		// we check what happens when the controller is not there.
		//
		this.env.removeAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
		res = this.api.getModel(model.getId());
		Assert.assertEquals(503, res.getStatus());
		
	}
	
	/**
	 * This method tests the functionality exposed by the API to retrieve the default model.
	 * The method tests the following scenarios:
	 * <ul>
	 * <li>retrieving the default model when the repository is empty: expected code 404.</li>
	 * <li>retrieving the default model when there is no default model in the repository: expected code 404.</li>
	 * <li>retrieving the default model when the repository contains it: expected code 200, the response should contained the default model.</li>
	 * <li>retrieving the list of models when the controller is not available: expected code 503.</li>
	 * </ul>
	 */
	@Test
	public void testGetDefaultModel() {
		
		this.setupApi();
		
		// empty repository.
		//
		Response res = this.api.getDefaultModel();
		Assert.assertEquals(404, res.getStatus());
		
		// we add the model.
		//
		Model model = this.getModel();
		model.setDefaultModel(false);
		this.modelRepo.addItem(model);

		res = this.api.getDefaultModel();
		Assert.assertEquals(404, res.getStatus());
		
		// we set it as default model.
		//
		this.controller.setDefaultModel(model);
		res = this.api.getModel(model.getId());
		Assert.assertEquals(200, res.getStatus());
		Model actual = (Model) res.getEntity();
		Assert.assertNotNull(actual);
		Assert.assertEquals(model.getId(), actual.getId());
		
		// we check what happens when the controller is not there.
		//
		this.env.removeAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
		res = this.api.getDefaultModel();
		Assert.assertEquals(503, res.getStatus());		
	}
	
    /**
     * Initialises the {@link Environment} with the components that are needed by
     * the {@link Model} instances to function and configures a new instance
     * of the API with the created {@link Environment}.
     */
    private void setupApi() {
    	
        this.api = new OptimizationModelApi();

        this.modelRepo = new TransientModelRepository();

        this.controller = new BasicModelController();
        this.controller.setRepository(this.modelRepo);

        this.env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, this.modelRepo);
        env.setAttribute(ModelController.MODEL_CONTROLLER_INSTANCE, controller);

        this.api.environment = this.env;
    }
    
    private Model getModel() {
    	
		Model model = new Model("Test Model");
		model.setDefaultModel(false);
		
		return model;
    	
    }

}
