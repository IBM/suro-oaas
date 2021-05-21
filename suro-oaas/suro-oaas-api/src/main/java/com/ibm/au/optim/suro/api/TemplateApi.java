/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.api;


import com.ibm.au.optim.suro.model.entities.*;
import com.ibm.au.optim.suro.model.store.*;
import com.ibm.au.jaws.data.DataNotFoundException;
import com.ibm.au.jaws.data.DataServiceException;
import com.wordnik.swagger.annotations.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * A JAX-RS (JSR-311) REST endpoint for the SURO <b>Strategies</b> API. This
 * class provides callable Web service operations for viewing an optimization
 * {@link Template}, creating new optimization {@link Run}s, viewing
 * optimization {@link Run}s and retrieving optimization solutions.
 * </p>
 */
@Path("/templates")
@Api(value = "/templates", description = "Optimisation Templates")
public class TemplateApi extends RestApi {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateApi.class);

    /**
     * <p>
     * Provides a list of {@link Template} documents that are currently
     * configured for the application. A template provides for a set of
     * assumptions and criteria that runs should optimize against given a
     * (default) set of parameters.
     * </p>
     *
     * @return A list of strategies
     */
    @ApiOperation(value = "Gets the list of strategies.", notes = "Retrieves all the strategies available for optimisation runs.", response = Template.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of strategies.", response = Template.class),
            @ApiResponse(code = 404, message = "No default optimization model available."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStrategies() {

    	Response response = null;
    	
    	
        try {

            Object instance = this.environment.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
            ModelRepository modelRepo = (ModelRepository) instance;
            
            if (modelRepo == null) {
            	
            	response = this.buildErrorResponse(new DataServiceException("Could not retrieve strategies, data service is not available [model repository]."), Status.SERVICE_UNAVAILABLE);
            
            } else {
            
	            Model defaultModel = modelRepo.findByDefaultFlag();
	
	            if (defaultModel == null) {
	            
	            	response = this.buildErrorResponse(new DataNotFoundException("No default optimization model available in the optimization model repository"), Status.NOT_FOUND);
	           
	            } else {
	
		            TemplateRepository repo = this.getTemplateRepository();
		            List<Template> templates = repo.findByModelId(defaultModel.getId());
		
		            response = this.buildResponse(Status.OK, templates);
	            }
            }

        } catch (Exception e) {
        	
            LOGGER.error("Error getting strategies", e);
            response = this.buildErrorResponse(e);
        }
        
        return response;
    }



    /**
     * <p>
     * Returns a {@link Template} for the specified document id.
     * </p>
     *
     * @param templateId A {@link Template} document ID
     * @return A JAX-RS response wrapping a {@link Template} document or Not
     * Found error if the template does not exist
     */
    @ApiOperation(value = "Get a template and associated runs", notes = "Retrieves the template for the given template ID.  Each document contains a list of runs that have been executed for the template.", response = Template.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A template document", response = Template.class),
            @ApiResponse(code = 404, message = "Template not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{templateId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplate(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId) {

    	Response response = null;
    	
        TemplateRepository repo = this.getTemplateRepository();
        Template template = repo.getItem(templateId);

        if (template != null) {
            response = this.buildResponse(Status.OK, template);
        } else {
            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.NOT_FOUND);
        }
        
        return response;
    }

    /**
     * <p>
     * Submit a job request to create a new run for a given template and set of job parameters. 
     * The request for a job is passed the associated optimization runtime. A new {@link Run} 
     * containing a {@link Run#getId()} is returned for future interaction with this job, including 
     * retrieving results of the optimization.
     * </p>
     *
     * @param templateId a {@link String} containing the unique identifier of the
     * 					 template from which the {@link Run} was created.
     *
     * @param stream	an {@link InputStream} implementation that is used to access
     * 					the content of the body of the request, containing the data
     * 					about the {@link Run} being submitted.
     * 
     * @return A JAX-RS response containing a new {@link Run} or error
     */
    @ApiOperation(value = "Starts a new optimization run for the specified template and run parameters.", notes = "Create and return a new run to create a proposed schedule plan for this template and optimization run parameters. "
            + "A new job is requested from the optimization engine using the model and parameters assigned to the template.", response = Run.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Run created", response = ResponseMessage.class),
            @ApiResponse(code = 400, message = "The content specified for the creation of the run was not valid."),
            @ApiResponse(code = 404, message = "Template not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {	
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", 
    				required=false, dataType="String", paramType="header") 
    })
    @POST
    @Path("/{templateId}/runs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitNewRun(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId, InputStream stream) {

    	Response response = null;
    	
        try {
        	
        	TemplateRepository repo = this.getTemplateRepository();
            Template template = repo.getItem(templateId);
            if (template != null) {

                RunApi resource = new RunApi(template, this.environment, this.context);
                response = resource.addRun(stream);

            } else {

                response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
            }

        } catch(DataServiceException dvex) {
        	
        	ResponseMessage message = new ResponseMessage(dvex.getMessage());
        	response = this.buildResponse(Status.SERVICE_UNAVAILABLE, message);
        	
        } catch (Exception ex) {

            LOGGER.error("Error while launching new optimization run", ex);
            response = this.buildErrorResponse(ex);
        }
        
        return response;
    }

    /**
     * <p>
     * Gets the list of all optimization {@link Run}s for the specified
     * {@link Template} ID.
     * </p>
     *
     * @param templateId A document ID for a {@link Template} document
     * @return A JAX-RS response containing a set of {@link Run} documents or
     * Not Found error if the template does not exist
     */
    @ApiOperation(value = "Get all runs for the template", notes = "Returns a list of all runs that have been created for this template, in descending order by creation time.  The list of runs includes those that are executing and finished.", response = Run.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of run documents", response = Run.class),
            @ApiResponse(code = 409, message = "The selected template does not exist."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{templateId}/runs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunsForTemplate(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId) {

    	Response response = null;
    	
        TemplateRepository repo = this.getTemplateRepository();
        Template template = repo.getItem(templateId);
        if (template != null) {

            RunApi resource = new RunApi(template, this.environment, this.context);
            response = resource.getRuns();

        } else {

            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
        }
        
        return response;

    }

    /**
     * <p>
     * Retrieves the specified {@link Run} for the given template and run
     * document IDs.
     * </p>
     *
     * @param runId A {@link Run} document ID
     * @return A JAX-RS Response wrapping a {@link Run} document or Not Found
     * error if run does not exist
     */
    @ApiOperation(value = "Retrieves the requested run for the selected template", notes = "Returns a run for the template given by template ID and run ID", response = Run.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A run document was found for the template", response = Run.class),
            @ApiResponse(code = 404, message = "Run not found"),
            @ApiResponse(code = 409, message = "There is no template matching the given identifier, or if existing the specified such template did not originate the specified run."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{templateId}/runs/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplateRun(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId,
            @ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {

    	Response response = null;
    	
        TemplateRepository repo = this.getTemplateRepository();
        Template template = repo.getItem(templateId);
        if (template != null) {

            RunApi resource = new RunApi(template, this.environment, this.context);
            response = resource.getRun(runId);

        } else {

            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
        }
    	
    	return response;
    }

    /**
     * <p>
     * Retrieves the {@link RunDetails} data set for the specified
     * {@link Run}. This document contains a list of
     * {@link RunLogEntry} objects that correspond to the
     * mathematical solver's event history and progression toward a feasible or
     * optimal solution.
     * </p>
     *
     * @param templateId A {@link Template} document ID
     * @param runId      A {@link Run} document ID
     * @return A JAX-RS Response wrapping a {@link RunDetails} document
     * containing a list of entries or Not Found error if run does not
     * exist
     */
    @ApiOperation(value = "Gets the optimization solver output data for the requested run", notes = "The optimization solver output data provides information about how the CPLEX solver found a solution, "
            + "including the chronological even history showing the progression toward the solution."
            + "<strong>Note:</strong>  <i>This object does not contain solution or schedule data</i>.", response = RunDetails.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A run was found with an optimization result", response = RunDetails.class),
            @ApiResponse(code = 404, message = "Run not found"),            
            @ApiResponse(code = 409, message = "There is no template matching the given identifier, or if existing the specified such template did not originate the specified run."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
    		@ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{templateId}/runs/{runId}/optimData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRunOptimizationData(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId,
            @ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {

    	Response response = null;
    	
        TemplateRepository repo = this.getTemplateRepository();
        Template template = repo.getItem(templateId);
        if (template != null) {

            RunApi resource = new RunApi(template, this.environment, this.context);
            response = resource.getOptimizationData(runId);

        } else {

            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
        }
    	
    	return response;
    }

    /**
     * <p>
     * Sends an abort command to the optimization engine to stop the
     * optimization task associated with the specified {@link Run} ID. If the
     * run does not have an executing task, then no action is taken.
     * </p>
     *
     * @param templateId A {@link Template} document ID
     * @param runId      A {@link Run} document ID
     * @return A JAX-RS Response with an event confirmation message or Not Found
     * error if run does not exist
     */
    @ApiOperation(value = "Stops the optimization job associated with this run if currently solving.", notes = "This operation sends a command to the underlying optimization engine requesting an abort job. "
            + "If a feasible solution has already been found, the service will construct a result set for the last (best) solution."
            + "This operation does not provide any result.", response = ResponseMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stop run requested", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Run not found"),
            @ApiResponse(code = 409, message = "There is no template matching the given identifier, or if existing the specified such template did not originate the specified run."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
            
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @PUT
    @Path("/{templateId}/runs/{runId}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopRun(@ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId,
                            @ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {
     
    	Response response = null;

        TemplateRepository repo = this.getTemplateRepository();
        Template template = repo.getItem(templateId);
        if (template != null) {

            RunApi resource = new RunApi(template, this.environment, this.context);
            response = resource.stopRun(runId);

        } else {

            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
        }
    	
    	return response;
    }

    /**
     * <p>
     * Deletes the requested {@link Run} document and associated document
     * metadata. If the run has an associated optimization job, the job
     * controller will attempt to cancel and delete the job as well. If the
     * associated job does not exist, then the job controller will take no
     * further action.
     * </p>
     *
     * @param templateId A {@link Template} document ID
     * @param runId      A {@link Run} document ID
     * @return A JAX-RS Response containing a {@link ResponseMessage} object or
     * Not Found error if run does not exist
     */
    @ApiOperation(value = "Deletes the requested run.", notes = "Deletes the run for this template, including all schedule results.  If an associated optimization job is in progress, the job is also aborted and deleted.", response = ResponseMessage.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Run deleted", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Run not found"),
            @ApiResponse(code = 409, message = "There is no template matching the given identifier, or if existing the specified such template did not originate the specified run."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @DELETE
    @Path("/{templateId}/runs/{runId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRun(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId,
            @ApiParam(value = "The run id", required = true) @PathParam("runId") String runId) {
 
    	

    	Response response = null;
    	TemplateRepository repo = this.getTemplateRepository();
        Template template = repo.getItem(templateId);
        if (template != null) {

            RunApi resource = new RunApi(template, this.environment, this.context);
            response = resource.deleteRun(runId);

        } else {

            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
        }
    	
    	return response;
    }

    /**
     * <p>
     * Retrieves a result document for the given file name from a {@link Run}
     * ID.
     * </p>
     * <p/>
     * <p>
     * If the run or document is not found, a HTTP 404 Not Found error is
     * returned.
     * </p>
     *
     * @param templateId	a {@link String} representing the unique identifier of the template instance from which the
     * 						run of interest was created. 
     *
     * @param runId			a {@link String} representing the unique identifier of the run instance for which the specific
     * 						result file, identified by <i>fileName</i> is requested.
     * 
     * @param fileName		a {@link String} representing the name of the result file that needs to be retrieved.
     * 
     * @param attachment 	a {@literal boolean} value indicating whether to return the content of the document with a
     * 						content disposition of type <i>attachment</i> ({@literal true}) or not ({@literal false}).
     * @return A JAX-RS response containing a stream for output
     */
    @ApiOperation(value = "Download the requested solution file for this run.",
            notes = "Streams a solution file for the given run and filename.  The response will contain the document's content type and size.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Document stream"),
            @ApiResponse(code = 404, message = "Run not found or file not found"),
            @ApiResponse(code = 409, message = "There is no template matching the given identifier, or if existing the specified such template did not originate the specified run."),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "Repository services are off line or not available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{templateId}/runs/{runId}/results/{fileName}")
    public Response getRunResultDocument(
            @ApiParam(value = "The template id", required = true) @PathParam("templateId") String templateId,
            @ApiParam(value = "The run id", required = true) @PathParam("runId") String runId,
            @ApiParam(value = "The solution document filename", required = true) @PathParam("fileName") String fileName, 
			@ApiParam(value = "A flag that controls the presentation of the content") @QueryParam("attachment") @DefaultValue("false") boolean attachment) {


    	Response response = null;
    	
    	TemplateRepository repo = this.getTemplateRepository();
    	Template template = repo.getItem(templateId);
        if (template != null) {

            RunApi resource = new RunApi(template, this.environment, this.context);
            response = resource.getResultDocument(runId, fileName, attachment);

        } else {

            response = this.buildErrorResponse(new FileNotFoundException("Template not found"), Status.CONFLICT);
        }
    	
    	return response;
    }


    /**
     * This method returns the implementation {@link TemplateRepository} that
     * has been currently configured with the application. It retrieves such
     * instance from the {@link com.ibm.au.jaws.web.core.runtime.Environment} implementation injected in the
     * instance by looking up the {@link TemplateRepository#TEMPLATE_REPOSITORY_INSTANCE}
     * attribute.
     *
     * @return the configured {@link TemplateRepository} implementation.
     */
    protected TemplateRepository getTemplateRepository() {

    	TemplateRepository repo = (TemplateRepository) this.environment.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);

        if (repo == null) {
            throw new DataServiceException("Template repository is not available.");
        }

        return repo;
    }
    


}
