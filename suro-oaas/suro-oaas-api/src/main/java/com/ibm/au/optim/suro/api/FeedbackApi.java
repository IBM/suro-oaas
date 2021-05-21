package com.ibm.au.optim.suro.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.admin.feedback.IssueException;
import com.ibm.au.optim.suro.model.admin.feedback.IssueManager;
import com.ibm.au.optim.suro.model.admin.feedback.IssueType;
import com.ibm.au.optim.suro.model.admin.feedback.impl.SimpleIssue;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;

/**
 * Feedback API used by the management UI and the demo applications to directly submit issues from within the web
 * applications without having to access the actual bug tracker. The API provides endpoints to create new bugs and
 * features (feature wish / feature request). The UI needs to differentiate the type of the issue to be created and call
 * a different endpoint.
 *
 * The issue manager will then enrich the ticket with additional server debugging information.
 *
 * @author Peter Ilfrich
 */
@Path("/feedback")
@Api(value = "/feedback", description = "Feedback API")
public class FeedbackApi extends RestApi {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackApi.class);

    /**
     * Creates a new bug in the corresponding bug tracker / issue manager. The data is provided by the parameter as an
     * InputStream, which is a serialised version of the {@link SimpleIssue}
     *
     * @param stream - a serialised version of a SimpleIssue.
     * @return - the updated SimpleIssue if the request is successful.
     */
    @ApiOperation(value = "Submit an issue to the issue manager.", notes = "Create a new bug item in the issue manager..", response = SimpleIssue.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The new bug.", response = SimpleIssue.class),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "No issue manager available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @POST
    @Path("/issues")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addIssue(InputStream stream) {
        try {
            SimpleIssue issue = new ObjectMapper().readValue(stream, new TypeReference<SimpleIssue>() {
            });
            issue.setType(IssueType.BUG);
            return submitIssue(issue);
        } catch (IOException ie) {
            LOGGER.error("Exception while adding a new issue.", ie);
            return buildErrorResponse(ie);
        }
    }


    /**
     * Creates a new feature (feature request / feature wish) in the corresponding issue tracker. The data is provided
     * by the parameter as an InputStream, which is a serialised version of the {@link SimpleIssue}
     *
     * @param stream
     * @return
     */
    @ApiOperation(value = "Submit an issue to the issue manager.", notes = "Create a new feature in the issue manager..", response = SimpleIssue.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The new feature.", response = SimpleIssue.class),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request."),
            @ApiResponse(code = 503, message = "No issue manager available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @POST
    @Path("/features")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addFeature(InputStream stream) {
        try {
            SimpleIssue issue = new ObjectMapper().readValue(stream, new TypeReference<SimpleIssue>() {
            });
            issue.setType(IssueType.FEATURE);
            return submitIssue(issue);
        } catch (IOException ie) {
            LOGGER.error("Exception while adding a new feature.", ie);
            return buildErrorResponse(ie);
        }
    }

    /**
     * Shared functionality of the feedback API to submit an issue to the issue manager. It handles issues with missing
     * issue managers and also if the creation of the ticket in the issue manager failed.
     *
     * @param issue - the issue to create
     * @return - the API response that needs to be returned to the caller of the endpoint.
     */
    protected Response submitIssue(SimpleIssue issue) {
        try {
            IssueManager im = (IssueManager) environment.getAttribute(IssueManager.ISSUE_MANAGER_INSTANCE);
            try {
                im.submitIssue(issue);
                return buildResponse(Status.CREATED, issue);
            } catch (IssueException ie) {
                LOGGER.error("Exception while submitting an issue", ie);
                return buildResponse(Status.INTERNAL_SERVER_ERROR, null);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while submitting an issue", e);
            return buildResponse(Status.SERVICE_UNAVAILABLE, null, null);
        }
    }
}
