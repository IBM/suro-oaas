/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.api;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that provides common methods for REST API endpoints in this
 * application
 *
 */
public abstract class RestApi {
	
	@Context
	protected ServletContext context;
	
	@Context
	protected Environment environment;

	protected static final Logger LOGGER = LoggerFactory.getLogger(RestApi.class);
	
	/**
	 * Returns the current servlet context for the REST API endpoint
	 * 
	 * @return A servlet context
	 */
	protected ServletContext getContext() {
		return this.context;
	}
	

	/**
	 * Create a JAX-RS response with HTTP 500 status code for the given
	 * exception
	 * 
	 * @param ex
	 *            Exception to wrap for the error
	 * @return A JAX-RS Response
	 */
	protected Response buildErrorResponse(Exception ex) {
		LOGGER.error("Exception during API call", ex);
		return buildErrorResponse(ex, Status.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Create a JAX-RS response for the specified error code and exception
	 * 
	 * @param ex
	 *            An exception to wrap for the error
	 * @param status
	 *            A HTTP status code
	 * @return A JAX-RS Response
	 */
	protected Response buildErrorResponse(Exception ex, Status status) {
		ResponseError error = new ResponseError();
		error.setMessage(ex.getMessage());
		error.setCode(status.getStatusCode());
		return this.buildResponse(status, error);
	}

	/**
	 * Builds a response object for a given content type and HTTP status code
	 * 
	 * @param status
	 *            An HTTP status code
	 * @param contentType
	 *            The HTTP Content-Type the response contains
	 * @param entity
	 *            The object send in response
	 * @return A JAX-RS response type
	 */
	protected Response buildResponse(Status status, String contentType, Object entity) {
		
		if (contentType == null && entity == null) {
			return this.buildResponse(status);
		} else {
			return Response.status(status).type(contentType).entity(entity).build();
		}
	}

	/**
	 * Builds a JAX-RS response object for a response structured as JSON
	 * 
	 * @param status
	 *            An HTTP status code
	 * @param entity
	 *            The object being sent as the response
	 * @return A JAX-RS response type
	 */
	protected Response buildResponse(Status status, Object entity) {
		if (entity == null) {
			return this.buildResponse(status);
		} else {
			return this.buildResponse(status, MediaType.APPLICATION_JSON, entity);
		}
	}


	/**
	 * Returns a simple status response.
	 * @param status - the status to return
	 * @return - a response with the given status
	 */
	protected Response buildResponse(Status status) {
		return Response.status(status).build();
	}
}
