/**
 * 
 */
package com.ibm.au.optim.suro.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Class <b>ModelApi</b>. This class extends {@link RestApi} and implements
 * an endpoint that can be used to access the optimization model metadata. A 
 * model contains a descriptive view of the set of objectives and available
 * parameters for composing an optimization job. The model also contains the
 * references to the underlying files that implement it, but these are not
 * exposed by the APIs.
 * 
 * @author Christian Vecchiola
 */
@Path("/models")
@Api(value = "/models", description = "API for accessing and managing optimization models.")
public class OptimizationModelApi extends RestApi {
	
    /**
     * {@link Logger} instance that collects all the messages that are generated
     * by the instances of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OptimizationModelApi.class);
	
	
	/**
	 * <p>
	 * Returns the model that has been specified by the client in the URL path.
	 * A model contains the description of the objectives and the available
	 * parameters for the strategies to compose an optmisation job.
	 * </p>
	 * 
	 * @param modelId	a {@link String} representing the unique identifier of the
	 * 					model. This parameter is extracted from the REST resource
	 * 					path that this method is bound to and therefore it is expected
	 * 					to not to be {@literal null}.
	 * 
	 * @return 	A JAX-RS response containing the specified model if found, or a 
	 * 			simple response message that informs the client about the error 
	 * 			occurred.
	 */
    @ApiOperation(value = "Gets the list of models.", notes = "Retrieves the list of models that are available through the APIs.", response = Model.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "An array of optimization model documents.", response = Model.class),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModels() {
    	
    	Response response = null;
    	
    	ModelController controller = this.getModelController();
    	
    	if (controller != null) {
    		
    		ModelRepository repository = controller.getRepository();
    		
    		if (repository != null) {
    		
    			List<Model> models = repository.getAll();
    			response = this.buildResponse(Status.OK, models);
    			
    		} else {
    			
    			LOGGER.warn("Could not retrieve the OptimizationModelRepository - Path:  /models/");
    			response = this.serviceNotAvailable();
    		}
    		
    	} else {
    		
    		LOGGER.warn("Could not retrieve the OptimizationModelController - Path: /models/");
    		response = this.serviceNotAvailable();
    	}
		
		return response;
	}
	
	/**
	 * <p>
	 * Returns the model that has been specified by the client in the URL path.
	 * A model contains the description of the objectives and the available
	 * parameters for the strategies to compose an optmisation job.
	 * </p>
	 * 
	 * @param modelId	a {@link String} representing the unique identifier of the
	 * 					model. This parameter is extracted from the REST resource
	 * 					path that this method is bound to and therefore it is expected
	 * 					to not to be {@literal null}.
	 * 
	 * @return 	A JAX-RS response containing the specified model if found, or a 
	 * 			simple response message that informs the client about the error 
	 * 			occurred.
	 */
    @ApiOperation(value = "Gets the specified optimization model.", notes = "Retrieves the specified model as indicated by the client.", response = Model.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "An optimization model document.", response = Model.class),
            @ApiResponse(code = 404, message = "No optimization model found. This means that there is no optimization model whose unique identifier matches the specified one."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/{modelId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModel(@ApiParam(value = "The model id", required = true) @PathParam("modelId") String modelId) {
    	
    	Response response = null;
    	
    	ModelController controller = this.getModelController();
    	
    	if (controller != null) {
    		
    		Model model = controller.getModel(modelId, false);
    		
    		response = this.buildResponse(model == null ? Status.NOT_FOUND : Status.OK, model);
    		
    	} else {
    		
    		LOGGER.warn("Could not retrieve the OptimizationModelController - Path: /models/" + modelId);
    		response = this.serviceNotAvailable();
    	}
		
		return response;
	}
    /**
     * <p>
     * Returns the default optimization model created. This is the model that is
     * used by the application if no specific model is selected. A model contains
     * the description of the objectives and the available parameters for the 
     * strategies to compose an optimisation job.
     * </p>
     *
	 * @return 	A JAX-RS response containing the default model if found, or a simple 
	 * 			response message that informs the client about the error occurred.
	 */
    @ApiOperation(value = "Gets the default optimization model.", notes = "Retrieves the default model. This is the one currently selected by the application.", response = Model.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "An optimization model document.", response = Model.class),
            @ApiResponse(code = 404, message = "No optimization model found. This means that there is no optimization model set as the default one."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/default")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDefaultModel() {
    	
    	Response response = null;
    	
    	ModelController controller = this.getModelController();
    	
    	if (controller != null) {
    		
    		Model model = controller.getDefaultModel();
    		
    		response = this.buildResponse(model == null ? Status.NOT_FOUND : Status.OK, model);
    		
    	} else {
    		
    		LOGGER.warn("Could not retrieve the OptimizationModelController - Path: /models/default");

    		response = this.serviceNotAvailable();
    	}
		
		return response;
	}

	/**
	 * This is a simple helper method to retrieve the current {@link ModelController}
	 * implementation.
	 * 
	 * @return	an instance implementing {@link ModelController} that has been configured
	 * 			with the APIs to access the services the core provides for dealing with 
	 * 			the {@link Model} instances and the associated repository.
	 */
	protected ModelController getModelController() {
	
		ModelController controller = (ModelController) this.environment.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
		return controller;
	}
	
	/**
	 * This is simply an helper method that is used to create a response when
	 * one of the backing services for performing the requested operation has
	 * not been retrieved.
	 * 
	 * @return	a {@link Response} implementation that contains the status code
	 * 			mapped by {@link Status#SERVICE_UNAVAILABLE} and a simple message.
	 */
	protected Response serviceNotAvailable() {
		
		Response response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage("Could not access repository services."));
		return response;
	}
	

	 
}
