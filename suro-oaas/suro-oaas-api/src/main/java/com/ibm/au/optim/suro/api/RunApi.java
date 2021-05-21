/**
 * 
 */
package com.ibm.au.optim.suro.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InputMismatchException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunDetailsRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.fasterxml.jackson.core.type.TypeReference;

import com.ibm.au.jaws.data.DataNotFoundException;
import com.ibm.au.jaws.data.DataServiceException;
import com.ibm.au.jaws.data.DataValidationException;
import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * Class <b>RunApi</b>. This class extends {@link RestApi} and provides
 * capabilities for managing the information about a run. This resource manages
 * all the operations to {@link Run} entities: those that are children of a
 * {@link Strategy} and those not. As a result of this, the {@link RunApi}
 * instance can be configured with a {@link Strategy} instance that represents
 * the owner of the {@link Run} instances served by this API.
 * 
 * @author Christian Vecchiola
 *
 */
@Path("/runs")
@Api(value = "/runs", description = "API for accessing and managing optimization runs.")
public class RunApi extends RestApi {
	
    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RunApi.class);


    /**
     * For JSON object deserialization
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String SOLUTION_ATTACHMENT = "solution.json";


	/**
	 * A {@link Template} instance that is used to filter the instances
	 * of {@link Run} that the resource will manage and serve, if not
	 * {@literal null}.
	 */
    protected Template template;
	
	/**
	 * Initialises an instance of {@link RunApi} and configures it with
	 * the given instance of {@link Strategy}. This constructor is used
	 * for direct creation of the resource.
	 * 
	 * @param template	if not {@literal null}, the instance represents
	 * 					the {@link Template} that will be used to filter
	 * 					the {@link Run} instances served by the resource.
	 * 
	 * @param environment	a {@link Environment} implementation used to
	 * 						access the common configuration parameters
	 * 						and shared objects.
	 * 
	 * @param context	a {@link ServletContext} implementation that provides
	 * 					access to the servlet context hosting the current
	 * 					resource.
	 */
	public RunApi(Template template, Environment environment, ServletContext context) {

		this.template = template;
		this.environment = environment;
		this.context = context;
	}

	/**
	 * Initialises an instance of {@link RunApi} to serve all the {@link Run}
	 * resources in the repository.
	 */
	public RunApi() {
		this(null, null, null);
	}


    /**
     * <p>
     * Gets the list of all optimization {@link Run}s. The method will return all
     * the {@link Run} instances that belong to a given {@link Strategy} if the
     * resource has been configured with a strategy, otherwise it will return all
     * the {@link Run} instances in the repository.
     * </p>
     *
     * @return  a {@link Response} implementation containing either the list of 
     * 			{@link Run} instances that have been retrieved, or the description
     * 			of an error that prevented the retrieval.
     * 
     */
    @ApiOperation(value = "Get all runs in the repository.", notes = "Returns a list of all runs that have been, in descending order by creation time. The list of runs includes those that are executing and finished.", response = Run.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of run documents", response = Run.class),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRuns() {
	
		RunRepository repo = this.getRunRepository();
		List<Run> runs = null;
		
		if (this.template != null) {
			
			runs = repo.findByTemplateId(this.template.getId());

		} else {

			runs = repo.getAll();
			
		}
		
		Response response = this.buildResponse(Status.OK, runs);

		return response;
	}

	
    /**
     * <p>
     * Returns the last optimization {@link Run} created. This run may have a
     * job that is currently executing, has completed with a feasible or optimal
     * solution, finished without finding a solution, or failed with an error.
     * </p>
     *
     * @return A JAX-RS response containing a {@link Run} document
     */
    @ApiOperation(value = "Gets the last run created.", notes = "Retrieves the last run created by the system, regardless of strategy.", response = Run.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A run document.", response = Run.class),
            @ApiResponse(code = 404, message = "No run found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/last")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLastRun() {

		RunRepository repo = this.getRunRepository();

		Run run = repo.getLast();
		if (run != null) {
			return buildResponse(Status.OK, run);
		} else {
			return this.buildErrorResponse(new DataNotFoundException("No runs available"), Status.NOT_FOUND);
		}
    }
    /**
     * This method returns the {@link Run} instance that is matched by the given identifier. The method behaves
     * differently according to the context in which it is executed. If there is a pre-configured strategy the
     * method not only tries to retrieve the run instance but it also check that the instance belongs to the
     * pre-conofigured strategy instance. This is done by checking that {@link Run#getStrategyId()} equals {@link 
     * Strategy#getId()}. If the test fails an error is returned.
     *
     * @param runId 	a {@link String} representing the unique identifier of the {@link Run} instance to retrieve.
     * 
     * @return 	a {@link Response} implementation that contains either the corresponding {@link Run} instance or
     * 			a description of the error that prevented the retrieval.
     */
    @ApiOperation(value = "Retrieves the requested run.", notes = "Returns a run given the run ID", response = Run.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A run document was found for the strategy", response = Run.class),
            @ApiResponse(code = 404, message = "Run not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/{runId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRun(@ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {

		Response response = null;

		RunRepository repo = this.getRunRepository();

		Run run = repo.getItem(runId);
		if (run != null) {
			
			if (this.template != null) {

				response = this.checkOwnership(run);
			}
			
			// [CV] NOTE: ok if we have a response that is not
			//			  null, this means that the ownership
			//			  has not been successfully verified and
			//			  we need to return an error.
			//
			//
			if (response == null) {
				
				// in this case, we have either verified the ownership
				// or no strategy was configured, which means that the
				// client can retrieve the run we originally pulled from
				// the repository.
				//
				response = this.buildResponse(Status.OK, run);
			}
			

		} else {

			response = this.buildErrorResponse(new FileNotFoundException("Run not found"), Status.NOT_FOUND);
		}

		return response;
	}
    /**
     * <p>
     * Submit a job request to create a new run for a given template and set of
     * job parameters. The request for a job is passed the associated
     * optimization runtime. A new {@link Run} containing a {@link Run#getId()} is
     * returned for future interaction with this job, including retrieving
     * results of the optimization.
     * </p>
     * <p/>
     * <p>
     * Parameters are specified in key-value pairs as POST data. Acceptable
     * parameters include
     * <ul>
     * <li><i>optimalityBoundary</i> - The required gap to meet before accepting
     * a solution as optimal</li>
     * <li><i>maxRuntime</i> - The maximum amount of time for the optimization
     * engine to try to solve, in fractional minutes</li>
     * <ul>
     * </p>
     *
     * @param stream	an {@link InputStream} implementation that is used to access
     * 					the content of the body of the request, containing the data
     * 					about the {@link Run} being submitted.
     *
     * @return A JAX-RS response containing a new {@link Run} or error
     * 
     * @throws DataServiceException	if any of the repositories are not available or
     * 								the core is not active or missing.
     */
    @ApiOperation(value = "Starts a new optimization run for the specified template and run parameters.", notes = "Create and return a new run to create a proposed schedule plan for this template and optimization run parameters. "
            + "A new job is requested from the optimization engine using the model and parameters assigned to the template.", response = Run.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Run created", response = ResponseMessage.class),
            @ApiResponse(code = 400, message = "The content specified for the creation of the run was not valid."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {	
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", 
    				required=false, dataType="String", paramType="header") 
    })
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRun(InputStream stream) {

		try {
			Response response = null;

			Run run = this.readRun(stream);

			// if we submit a run with no template id and we are invoked
			// under the path of a template, then that means that we have
			// already an implicit template id that we can use.
			//
			String id = run.getTemplateId();
			if ((id == null) && (this.template != null)) {

				run.setTemplateId(this.template.getId());

				// we also infer the model identifier from the template
				// if the user has not set it up.
				//
				if (run.getModelId() == null) {

					run.setModelId(this.template.getModelId());
				}
			}


			// at this point we are not anymore allowed to have
			// a model and a template identifier that are null.
			//

			run = this.validate(run);

			Core core = (Core) this.environment.getAttribute(Core.CORE_INSTANCE);
			if (core != null) {

				// submit new run
				Run newRun = core.submitRun(run);
				if (newRun == null) {

					LOGGER.error("Core services are not ready", new DataServiceException("Core services are not ready."));
					throw new DataServiceException("Core services are not ready");

				}

				response = this.buildResponse(Status.OK, newRun);

			} else {

				LOGGER.error("Could not access core services", new DataServiceException("Could not access core services."));
				throw new DataServiceException("Could not access core services.");
			}

			return response;
		}
		catch (Exception e) {
			LOGGER.error("Error during run submission", e);
			return buildErrorResponse(e, Status.SERVICE_UNAVAILABLE);
		}


    }

	/**
     * This method retrieves the optimisation data that is attached to an ongoing or completed {@link Run} instance
     * matched by the identifier passed as argument. The method behaves differently according to the context in which 
     * it is executed. If there is a pre-configured strategy the method not only tries to retrieve the run instance but 
     * it also check that the instance belongs to the pre-conofigured strategy instance. This is done by checking that 
     * {@link Run#getStrategyId()} equals {@link Strategy#getId()}. If the test fails an error is returned.
     * 
     * @param runId		a {@link String} representing the unique identifier of the run instance for which the optimisation
     * 					data is requested.
     * 
     * @return	a {@link Response} implementation containing the requested optimisation data or the description of an
     * 			error that prevented its retrieval.
     */
    @ApiOperation(value = "Gets the optimization solver output data for the requested run", notes = "The optimization solver output data provides information about how the CPLEX solver found a solution, "
            + "including the chronological even history showing the progression toward the solution."
            + "<strong>Note:</strong>  <i>This object does not contain solution or schedule data</i>.", response = RunDetails.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A run was found with an optimization result", response = RunDetails.class),
            @ApiResponse(code = 404, message = "Run not found"),            
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
    @Path("/{runId}/details")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getOptimizationData(@ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {


		LOGGER.debug("Retrieving optimisation result by run ID");
		
		Response response = this.checkRunPrerequisites(runId);

		if (response == null) {
		
			RunDetailsRepository repo = this.getOptimizationResultRepository();

			RunDetails data = repo.findByRunId(runId);
			
			if (data != null) {
	
				response = this.buildResponse(Status.OK, data);
	
			} else {
	
				response = this.buildErrorResponse(new DataNotFoundException("Run not found"), Status.NOT_FOUND);
			}
			
		}
		
		return response;
	}
    
    /**
     * This method retrieves the specific attachment file that contains the details of the solution of the optimisation
     * process executed by the run. The method behaves differently according to the context in which it is executed. If 
     * there is a pre-configured strategy the method not only tries to retrieve the run instance but it also check that 
     * the instance belongs to the pre-conofigured strategy instance. This is done by checking that {@link Run#getStrategyId()} 
     * equals {@link Strategy#getId()}. If the test fails an error is returned.
     * 
     * @param runId			a {@link String} representing the unique identifier of the run instance for which the specific
     * 						result file, identified by <i>fileName</i> is requested.
     * 
     * @param fileName		a {@link String} representing the name of the result file that needs to be retrieved.
     * 
     * @param attachment 	a {@literal boolean} value indicating whether to return the content of the document with a
     * 						content disposition of type <i>attachment</i> ({@literal true}) or not ({@literal false}).
     * 
     * @return	a {@link Response} implementation containing the requested result file or the description of an
     * 			error that prevented its retrieval.
     */
    @ApiOperation(value = "Download the requested solution file for this run.",
            notes = "Streams a solution file for the given run and filename.  The response will contain the document's content type and size.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Document stream"),
            @ApiResponse(code = 404, message = "Run not found or file not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/{runId}/results/{fileName}")
	public Response getResultDocument(
										@ApiParam(value = "The run id", required = true) @PathParam("runId") String runId, 
										@ApiParam(value = "The solution document filename", required = true) @PathParam("fileName") String fileName, 
										@ApiParam(value = "A flag that controls the presentation of the content") @QueryParam("attachment") @DefaultValue("false") boolean attachment) {

		Response response = null;

		try {
			// read attachment
			response = this.checkRunPrerequisites(runId);

			if (response == null) {

				RunRepository repo = this.getRunRepository();
				
				final InputStream ais = repo.getAttachment(runId, fileName);

				// If document not found, throw a not found exception
				if (ais == null) {
					throw new DataNotFoundException(runId + "/" + fileName);
				}

				// Prepare to stream response
				StreamingOutput stream = new StreamingOutput() {

					@Override
					public void write(OutputStream output) throws IOException, WebApplicationException {
						try {
							IOUtils.copy(ais, output);
						} finally {
							IOUtils.closeQuietly(ais);
						}
					}
				};

				MediaType mediaType = this.getMediaType(fileName);
				ResponseBuilder builder = Response.ok(stream, mediaType);
				
				if (attachment == true) {
					
					builder = builder.header("Content-Disposition", "attachment; filename=" + fileName);
				}
								   
				response = builder.build();
			}

		} catch (DataNotFoundException dnf) {

			// If either the Run or the requested document attachment is not
			// found, then return
			// a 404 Not Found response
			LOGGER.warn("Result file not found for " + fileName + " for run " + runId, dnf);
			response = this.buildErrorResponse(dnf, Status.NOT_FOUND);
		}

		return response;
	}
    

	/**
	 * Retrieves the solution.json for a specific run as JSON object
	 * @param runId
	 * @return
     */
	@ApiOperation(value = "Download the solution.json as JSON response from the specified run.",
			notes = "Will read the solution.json from the database (if it exists) and return it as JSON response to the client.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Solution provided."),
			@ApiResponse(code = 404, message = "Run not found or solution.json not found for the specified run."),
			@ApiResponse(code = 500, message = "An internal error has occurred while processing the request.")
	})
	@ApiImplicitParams(value = {
			@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header")
	})
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{runId}/solution")
	@Deprecated
	public Response getRunSolution(@ApiParam(value = "The run id", required = true) @PathParam(value = "runId") String runId) {

		// retrieve the attachment from the repository
		RunRepository repo = this.getRunRepository();
		try {

			// fetch attachment from the repo
			InputStream solutionData = repo.getAttachment(runId, SOLUTION_ATTACHMENT);

			// try to write the solution data into the response
			try {
				// return the solution as JSON
				return Response.status(200).entity(IOUtils.toString(solutionData)).build();

			} catch (IOException ie) {
				// error writing the solution.json to the response entity
				LOGGER.error("Error serialising solution.json", ie);
				return buildErrorResponse(ie, Status.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception notFound) {

			// when the document cannot be found (or the attachment)
			return buildErrorResponse(notFound, Status.NOT_FOUND);
		}

	}


    /**
     * This method stops the execution of the {@link Run} instance that is matched by the given identifier.
     * The method behaves differently according to the context in which it is executed. If there is a 
     * pre-configured strategy the method not only tries to retrieve the run instance but it also check that 
     * the instance belongs to the pre-conofigured strategy instance. This is done by checking that {@link 
     * Run#getStrategyId()} equals {@link Strategy#getId()}. If the test fails an error is returned.
     * 
     * @param runId		a {@link String} representing the unique identifier of the {@link Run} instance to
     * 					stop.
     * 
     * @return	a {@link Response} implementation that contains information about the outcome of the operation.
     */
    @ApiOperation(value = "Stops the optimization job associated with this run if currently solving.", notes = "This operation sends a command to the underlying optimization engine requesting an abort job. "
            + "If a feasible solution has already been found, the service will construct a result set for the last (best) solution."
            + "This operation does not provide any result.", response = ResponseMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stop run requested", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Run not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
            
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@PUT
	@Path("/{runId}/stop")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopRun(@ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {

		Response response = null;

		try {
			
			response = this.checkRunPrerequisites(runId);

			LOGGER.debug("Calling stopRun() for " + runId);
			Core core = (Core) this.environment.getAttribute(Core.CORE_INSTANCE);
			boolean result = core.abortRun(runId);

			if (result == true) {
			
				response = this.buildResponse(Status.OK, new ResponseMessage("Stop run requested"));
			
			} else {
				
				String message = "Internal error while stopping the run.";
				Status status = Status.INTERNAL_SERVER_ERROR;
				
				if (core.isReady() == false) {
					
					message = "Core services not ready.";
					status = Status.SERVICE_UNAVAILABLE;
				}
				
			
				response = this.buildResponse(status, new ResponseMessage(message));
				
			}

		} catch (Exception dnf) {

			LOGGER.error("Calling stopRun() caused an exception: " + dnf, dnf);
			response = this.buildErrorResponse(dnf);
		}

		return response;

	}
    
    /**
     * This method removes from the repository the {@link Run} instance that matches the given
     * unique identifier. The method behaves differently according to the context in which it is 
     * executed. If there is a pre-configured strategy the method not only tries to retrieve the 
     * run instance but it also check that the instance belongs to the pre-conofigured strategy 
     * instance. This is done by checking that {@link Run#getStrategyId()} equals {@link Strategy#getId()}. 
     * If the test fails an error is returned.
     *  
     * @param runId		a {@link String} representing the unique identifier of the {@link Run}
     * 					that needs to be deleted.
     * 
     * @return	a {@link Response} implementation containing information about the outcome of
     * 			the operation.
     */
    @ApiOperation(value = "Deletes the requested run.", notes = "Deletes the run for this strategy, including all schedule results.  If an associated optimization job is in progress, the job is also aborted and deleted.", response = ResponseMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Run deleted", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Run not found"),
            @ApiResponse(code = 409, message = "There is no strategy matching the given identifier, or if existing the specified such strategy did not originate the specified run."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@DELETE
	@Path("/{runId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRun(@ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {

		LOGGER.debug("deleteRun: requested deletion of run " + runId);

		Response response = null;

		try {

			response = this.checkRunPrerequisites(runId);

			if (response == null) {

				Core core = (Core) this.environment.getAttribute(Core.CORE_INSTANCE);
				
				// TODO: DocumentNotFound (dnf) is not an exception that should be
				//       passed along, but we should wrap it into a more implementation
				//		 independent exception.
				//
				boolean result = core.deleteRun(runId);
				if (result) {
					response = this.buildResponse(Status.OK, new ResponseMessage("Run deleted"));
				} else {
					
					String message = "Internal error while deleting the run: abortion failed.";
					Status status = Status.INTERNAL_SERVER_ERROR;
					
					if (core.isReady() == false) {
						
						message = "Core services not ready.";
						status = Status.SERVICE_UNAVAILABLE;
					}
					
					
					
					// TODO: a boolean for delete is not enough information for translating
					//       the appropriate semantic to the client: possible values could
					//       be 409 / 503 core not ready, 500 any other error.
					//
					response = this.buildResponse(status, new ResponseMessage(message));
				}
			}

		} catch (Exception dnf) {

			LOGGER.error("Exception (not-found) while executing deleteRun API", dnf);
			response = this.buildErrorResponse(dnf, Status.NOT_FOUND);
		}

		return response;
	}

	/**
	 * This method checks that all the required pre-requisites for serving the request 
	 * about the specific {@link Run} instance are satisfied. The method first tries to
	 * retrieve the {@link Run} instance that matches <i>runId</i> and if not {@literal null}
	 * it checks whether it belongs to the specified {@link Strategy}, if any. 
	 * 
	 * @param runId	a {@link String} representing the unique identifier for which the
	 * 				pre-conditions needs to be verified for.
	 * 
	 * @return	{@literal null} if pre-requisites are satisfied, otherwise a {@link Response}
	 * 			implementation that contains the error messages describing the pre-condition
	 * 			that has been violated.
	 */
	protected Response checkRunPrerequisites(String runId) {
		
		Response response = null;
		RunRepository repo = this.getRunRepository();
		Run run = repo.getItem(runId);
		
		if (run != null) {
			
			if (this.template != null) {
				
				response = this.checkOwnership(run);
			}
			
		} else {
			
			response = this.buildErrorResponse(new DataNotFoundException("Run does not exist."), Status.NOT_FOUND);
		}
		
		return response;
	}

	/**
	 * This method checks whether the given {@link Run} instance belongs 
	 * to the pre-configured {@link Strategy} with the resource. If this
	 * is the case then {@link Run#getStrategyId()} must equal to {@link
	 * Run#getId()}.
	 * 
	 * @param run	a {@link Run} instance for which the pre-conditions of
	 * 				ownership by the pre-configured strategy need to be
	 * 				verified.
	 * 
	 * @return	{@literal null} if the {@link Run} instance belongs to the
	 * 			pre-configured {@link Strategy} with the resource, otherwise
	 * 			a {@link Response} implementation that describes the violation
	 * 			of the pre-condition.
	 */
	protected Response checkOwnership(Run run) {
		
		Response response = null;
		
		if (run.getTemplateId().equals(this.template.getId()) == false) {

			response = this.buildErrorResponse(new InputMismatchException("The run specified doesn't belong to the specified strategy"), Status.CONFLICT);
		}

		return response;

	}
	
	/**
	 * This method reads the {@link Run} data from the given {@link InputStream}.
	 * 
	 * @param stream	a {@link InputStream} implementation providing access to the
	 * 					{@link Run} data, submitted by the client.
	 * 
	 * @return	a {@link Run} instance that corresponds to the content being submitted
	 * 			to the client and read by the current method.
	 * 
	 * @throws DataValidationException 	if there is any error while trying to deserialise
	 * 									the {@link Run} instance from the input stream.
	 */
	protected Run readRun(InputStream stream) {
		
		try {
			
        	Run run = MAPPER.readValue(stream, new TypeReference<Run>() {});
        	
        	return run;
			
		} catch(Exception ioex) {
			
			LOGGER.error("Error while reading the run submission.", ioex);
			
			throw new DataValidationException("Invalid run submission [details: " + ioex.getMessage() + "].", ioex);
		}
	}
	
	/**
	 * This method validates the current instance of the {@link Run} against the constraints set by the 
	 * {@link Model} and {@link Template} instances that the run belongs to. The method retrieves the
	 * repositories for the underlying <i>model</i>, <i>template</i>, and <i>dataset</i> and based on
	 * the specified identifiers in the {@link Run} retrieves the corresponding instances. If the current
	 * API have a predefined template this will be used as template for the validation. 
	 * 
	 * @param run	a {@link Run} instance that needs to be validated.
	 * 
	 * @return	an {@link Run} instance that has been validated and populated with
	 * 			all the required defaults.
	 * 
	 * @throws DataValidationException	if the <i>run</i> instance is not valid or it has any 
	 * 									inconsistent data.
	 * @throws DataServiceException		if it is not possible to access the backing services 
	 * 									required for the validation (i.e. {@link ModelRepository}, 
	 * 									{@link TemplateReposuitory}, and {@link DataSetRepository}).
	 */
	protected Run validate(Run run) {
		
		Run validated = null;
		
		TemplateRepository tRepo = (TemplateRepository) this.environment.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
		ModelRepository mRepo = (ModelRepository) this.environment.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
		DataSetRepository dRepo = (DataSetRepository) this.environment.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
		
		if ((tRepo != null) && (mRepo != null) && (dRepo != null)) {
			
			Template owner = this.template;
			
			if (owner == null) {
				
				owner = tRepo.getItem(run.getTemplateId());
			
			}
			
			if (owner != null) {
			
				// we pull the model from the template because this
				// is the one that we should consider and not the
				// one submitted by the run.
				//
				Model model = mRepo.getItem(owner.getModelId());
				if (model != null) {
					
					
					if (run.getDataSetId() == null) {
						
						// we will find the one dataset that is associated
						// to the model and plug that into the run instance.
						
						List<DataSet> sets = dRepo.findByModelId(model.getId());
						if (sets.isEmpty() == true) {
							
							throw new DataValidationException("There is no data set associated to the model associated with the run. [Model Id: " + model.getId() + "].");
						}
						
						// ok we do have at least one data set.
						//
						//
						DataSet ds = sets.get(0);
						
						if (sets.size() > 1) {
							
							LOGGER.warn("Multiple dataset instances are associated to the model, selecting first dataset. [Model Id: " + model.getId() + ", DataSet Id: " + ds.getId() + "].");
						}
						
						run.setDataSetId(ds.getId());
					}
					
					
					// this does all the parameter checking and validates
					// that both template and run are coherent to their
					// corresponding owners.
					//
					validated = model.populate(owner, run);
					
					
					
				} else {
					
					throw new DataValidationException("Could retrieve model information, template associated with the run, must be associated to an existing model [Template Id: " + owner.getId() + ", Model Id: " + owner.getModelId() + "].");
				}
			
			
			} else {
				
				throw new DataValidationException("Could not retrieve template information, run must be associated to an existing template [Template Id: " + run.getTemplateId() + "].");
			}
			
			
		} else {

			LOGGER.error("Repositories not avaliable [Template: " + (tRepo == null  ? "Offline" : "Online") + 
												   ", Model: " + (mRepo == null ? "Offline" : "Online") + 
												   ", Dataset: " + (dRepo == null ? "Offine" : "Online") + "].");
			
			throw new DataServiceException("Cannot access repository to perform validation.");
		}
		
		
		return validated;
	}

	/**
	 * This method returns the implementation {@link RunRepository} that has
	 * been currently configured with the application. It retrieves such
	 * instance from the {@link com.ibm.au.jaws.web.core.runtime.Environment}
	 * implementation injected in the instance by looking up the
	 * {@link RunRepository#RUN_REPOSITORY_INSTANCE} attributed.
	 *
	 * @return the configured {@link RunRepository} implementation.
	 */
	protected RunRepository getRunRepository() {

		RunRepository repo = (RunRepository) this.environment.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);

		if (repo == null) {
			
			throw new DataServiceException("Run repository is not available.");
		}

		return repo;
	}

	/**
	 * This method returns the implementation {@link RunDetailsRepository} that has been currently configured with the
	 * application. It retrieves such instance from the {@link com.ibm.au.jaws.web.core.runtime.Environment} implementation
	 * injected in the instance by looking up the {@link RunDetailsRepository#DETAILS_REPOSITORY_INSTANCE} attribute.
	 *
	 * @return the configured {@link RunDetailsRepository}
	 *         implementation.
	 */
	protected RunDetailsRepository getOptimizationResultRepository() {

		RunDetailsRepository repo = (RunDetailsRepository) this.environment.getAttribute(RunDetailsRepository.DETAILS_REPOSITORY_INSTANCE);

		if (repo == null) {
			
			throw new DataServiceException("Optimization result repository is not available.");
		}

		return repo;
	}

	/**
	 * This method returns the corresponding {@link MediaType} that matches the
	 * given <i>fileName</i>. The method can only recognize the
	 * <i>application/json</i>, <i>text/csv</i> and <i>text/plain</i> media
	 * types.
	 *
	 * @param fileName
	 *            a {@link String} representing the name of the file.
	 * @return a {@link MediaType} instance that matches the given file.
	 */
	protected MediaType getMediaType(String fileName) {

		if (fileName == null || fileName.isEmpty()) {
			return null;
		}

		MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;

		if (fileName.toLowerCase().endsWith(".json") == true) {
			mediaType = MediaType.APPLICATION_JSON_TYPE;
		} else if (fileName.toLowerCase().endsWith(".csv") == true) {
			mediaType = new MediaType("text", "csv");
		}

		return mediaType;
	}

}
