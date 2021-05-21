package com.ibm.au.optim.suro.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatient;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class provides API endpoints for managing:
 * - Base Plans
 * - Patient Surgery Waiting Lists
 * - ICU Availability
 * - Ward Availability
 * - Specialist Availability
 *
 * The intended use-case is a hospital ERP system calling the endpoints to update the data in the system with real
 * hospital data. The API calls the {@link IngestionController} to perform any calculations and data manipulation.
 *
 * @author Brendan Haesler
 */
@Path("/timedata")
@Api(value = "/timedata", description = "Time data ingestion API")
public class IngestionApi extends RestApi {

	/**
	 * Logger instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IngestionApi.class);

	/**
	 * Object mapper used to read and write JSON
 	 */
	private static final ObjectMapper MAPPER = new ObjectMapper();

	// Status strings
	private static final String INTERNAL_ERROR = "An internal error has occurred while processing the request.";
	private static final String REPOSITORY_UNAVAILABLE = "Repository could not be accessed.";
	private static final String COULD_NOT_PARSE = "Input data could not be parsed.";
	private static final String RECORDS_ADDED = "Records added successfully.";
	private static final String NO_RECORDS = "No records provided in input data.";
	private static final String RECORD_NOT_FOUND = "No matching records found.";

	/* BASE PLAN */

	/**
	 * Adds a new base plan to the system.
	 *
	 * @param stream - An input stream containing a serialised list of {@link BasePlanList}.
	 * @return A JAX-RS response indicating the result of the request.
	 */
	@ApiOperation(value = "Submit a new base plan.", notes = "Assumes the given information is a full base plan.")
	@ApiResponses(value = {
					@ApiResponse(code = 204, message = RECORDS_ADDED),
					@ApiResponse(code = 400, message = NO_RECORDS),
					@ApiResponse(code = 500, message = INTERNAL_ERROR),
					@ApiResponse(code = 503, message = REPOSITORY_UNAVAILABLE)
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@POST
	@Path("/baseplan")
	@Produces(MediaType.APPLICATION_JSON)
	public Response basePlanPostHandler(InputStream stream) {
		return ingestionPostHandler(stream, new TypeReference<List<BasePlanEntry>>() { });
	}


	/**
	 * Retrieves all entries for the current base plan. The endKey defines the latest timestamp of any BasePlan entry.
	 * Entries with a larger timestamp will be excluded from the result.
	 *
	 * @param endKey - specifies the largest timestamp of any of the returned records.
	 * @return - a list of {@link BasePlanEntry}
	 */
	@ApiOperation(value = "Get all entries in the current base plan.",
					notes = "Returns a list of base plan objects in the current base plan.")
	@ApiResponses(value = {
					@ApiResponse(code = 200, message = "A list of base plan entries.", response = BasePlanEntry.class),
					@ApiResponse(code = 404, message = RECORD_NOT_FOUND),
					@ApiResponse(code = 503, message = REPOSITORY_UNAVAILABLE)
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/baseplan")
	@Produces(MediaType.APPLICATION_JSON)
	public Response basePlanGetHandler(@QueryParam("before") long endKey) {
		return ingestionGetBeforeTimeHandler(BasePlanEntry.class, endKey);
	}

	/* SURGERY HISTORY */

	/* PATIENT SURGERY WAITING LIST */

	/**
	 * Adds a patient waiting list to the system.
	 *
	 * @param stream - An input stream containing a serialised list of {@link WaitingPatient ) objects.}
	 * @return A JAX-RS response indicating the result of the request.
	 */
	@ApiOperation(value = "Submit a new patient waiting list.",
					notes = "Assumes the given information is the full waiting list.")
	@ApiResponses(value = {
					@ApiResponse(code = 204, message = RECORDS_ADDED),
					@ApiResponse(code = 400, message = NO_RECORDS),
					@ApiResponse(code = 500, message = INTERNAL_ERROR),
					@ApiResponse(code = 503, message = REPOSITORY_UNAVAILABLE)
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@POST
	@Path("/waitinglist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response waitingListPostHandler(InputStream stream) {
		return ingestionPostHandler(stream, new TypeReference<List<WaitingPatient>>() { });
	}


	/**
	 * Retrieves the data representing the waiting patient list. Data is delimited to the provided timestamp as a
	 * maximum timestamp.
	 * @param time - the largest timestamp of any record to return
	 * @return - a list of {@link WaitingPatient}
	 */
	@ApiOperation(value = "Get the surgery patient waiting list.",
					notes = "Returns a list of patients waiting for surgery.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "A list of patients waiting for surgery.", response = WaitingPatient.class),
			@ApiResponse(code = 404, message = RECORD_NOT_FOUND),
			@ApiResponse(code = 503, message = "No waiting patient repository available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/waitinglist")
	@Produces(MediaType.APPLICATION_JSON)
	public Response waitingListGetHandler(@QueryParam("before") long time) {
		return ingestionGetBeforeTimeHandler(WaitingPatient.class, time);
	}


	/* ICU AVAILABILITY */

	/**
	 * Adds ICU Availability to the system.
	 *
	 * @param stream - An input stream containing a serialised list of {@link IcuAvailabilityList} objects.
	 * @return A JAX-RS response indicating the result of the request.
	 */
	@ApiOperation(value = "Submit a ICU Availability list.",
					notes = "Assumes the given information is the full ICU availability. Previously existing records not found in the new data will be deleted.")
	@ApiResponses(value = {
					@ApiResponse(code = 204, message = RECORDS_ADDED),
					@ApiResponse(code = 400, message = NO_RECORDS),
					@ApiResponse(code = 500, message = INTERNAL_ERROR),
					@ApiResponse(code = 503, message = REPOSITORY_UNAVAILABLE)
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@POST
	@Path("/icuavailability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response icuAvailabilityPutHandler(InputStream stream) {
		return ingestionPostHandler(stream, new TypeReference<List<IcuAvailability>>() { });
	}


	/**
	 * Retrieves the data representing the ICU availability in the hospital. The data is delimited to timestamps before
	 * the provided timestamp.
	 * @param time - the largest timestamp any record should have
	 * @return - a list of {@link IcuAvailability} before the provided timestamp
	 */
	@ApiOperation(value = "Get current ICU availability.",
					notes = "Returns a list patients in the ICU and their expected leave dates.")
	@ApiResponses(value = {
					@ApiResponse(code = 200, message = "A list of ICU availability documents.",
									response = IcuAvailability.class),
					@ApiResponse(code = 404, message = RECORD_NOT_FOUND),
					@ApiResponse(code = 503, message = "No ICU availability repository available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/icuavailability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response icuAvailabilityGetHandler(@QueryParam("before") long time) {
		return ingestionGetBeforeTimeHandler(IcuAvailability.class, time);
	}

	/* WARD AVAILABILITY */

	/**
	 * Adds Ward Availability to the system.
	 *
	 * @param stream - An input stream containing a serialised list of {@link WardAvailability} objects.
	 * @return A JAX-RS response indicating the result of the request.
	 */
	@ApiOperation(value = "Submit a ward availability list.",
					notes = "Assumes the given information is the full ward availability. Previously existing records not found in the new data will be deleted.")
	@ApiResponses(value = {
					@ApiResponse(code = 204, message = RECORDS_ADDED),
					@ApiResponse(code = 400, message = NO_RECORDS),
					@ApiResponse(code = 500, message = INTERNAL_ERROR),
					@ApiResponse(code = 503, message = REPOSITORY_UNAVAILABLE)
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@POST
	@Path("/wardavailability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response wardAvailabilityPostHandler(InputStream stream) {
		return ingestionPostHandler(stream, new TypeReference<List<WardAvailability>>() { });
	}


	/**
	 * Retrieves the data representing the current bed availability in the wards. The data is delimited to timestamps
	 * before the provided one.
	 * @param time - the largest timestamp any record should have
	 * @return - a list of {@link WardAvailability} records before the provided timestamp
	 */
	@ApiOperation(value = "Get current ward availability.",
					notes = "Returns a list of patients in wards and their expected leave date.")
	@ApiResponses(value = {
					@ApiResponse(code = 200, message = "A list of ward availability documents.",
									response = WardAvailability.class),
					@ApiResponse(code = 404, message = RECORD_NOT_FOUND),
					@ApiResponse(code = 503, message = "No ward availability repository available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/wardavailability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response wardAvailabilityGetHandler(@QueryParam("before") long time) {
		return ingestionGetBeforeTimeHandler(WardAvailability.class, time);
	}

	/* SPECIALIST AVAILABILITY */

	/**
	 * Adds specialist availability to the system.
	 *
	 * @param stream - An input stream containing a serialised list of {@link SpecialistAvailabilityList} objects.
	 * @return A JAX-RS response indicating the result of the request.
	 */
	@ApiOperation(value = "Submit specialist availability.",
					notes = "Assumes the given information is the full specialist availability. Previously existing records not found in the new data will be deleted.")
	@ApiResponses(value = {
					@ApiResponse(code = 204, message = RECORDS_ADDED),
					@ApiResponse(code = 400, message = NO_RECORDS),
					@ApiResponse(code = 500, message = INTERNAL_ERROR),
					@ApiResponse(code = 503, message = REPOSITORY_UNAVAILABLE)
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@POST
	@Path("/specialistavailability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response specialistAvailabilityPostHandler(InputStream stream) {
		return ingestionPostHandler(stream,	new TypeReference<List<SpecialistAvailability>>() { });
	}

	/**
	 * Retrieves the data representing the current specialist availability in the hospital. The data is delimited to
	 * timestamps before the provided one.
	 * @param time - the largest timestamp any record should have
	 * @return - a list of {@link SpecialistAvailability} records before the provided timestamp.
	 */
	@ApiOperation(value = "Get current specialist availabilities.",
					notes = "Returns a list of specialist availabilities for the foreseeable future.")
	@ApiResponses(value = {
					@ApiResponse(code = 200, message = "A list of specialist availability documents.",
									response = SpecialistAvailability.class),
					@ApiResponse(code = 404, message = RECORD_NOT_FOUND),
					@ApiResponse(code = 503, message = "No specialist availability repository available.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
	@GET
	@Path("/specialistavailability")
	@Produces(MediaType.APPLICATION_JSON)
	public Response specialistAvailabilityGetHandler(@QueryParam("before") long time) {
		return ingestionGetBeforeTimeHandler(SpecialistAvailability.class, time);
	}

	/* Helper methods */

	/**
	 * Handles the ingestion of any ingestion object (base plan, availabilities, ...) in a generic way. All that needs
	 * to be provided is the POST data as input stream and a type reference for the de-serialisation.
	 *
	 * @param inputStream - A serialised list of T objects.
	 * @param typeReference - A typeReference object of the type of item stored in the input stream.
	 * @param <R> - The type of object the stream holds a list of.
	 * @param <T> - The type of the records stored in the repository.
	 * @return A JAX-RS response indicating the result of the request.
	 */
	protected <R, T extends TemporalList<R>> Response ingestionPostHandler(
					InputStream inputStream, TypeReference<List<R>> typeReference) {
		// parse the stream
		List<R> records;

		try {
			records = MAPPER.readValue(inputStream, typeReference);
		} catch (IOException e) {
			// this might need to be a 400 instead of a 500, as the reader should only fail if the input is poorly formatted
			LOGGER.error(COULD_NOT_PARSE, e);
			return buildErrorResponse(e);
		}

		// check that there are actually records to add
		if (records.isEmpty()) {
			Exception exception = new Exception(NO_RECORDS);
			LOGGER.error(NO_RECORDS, exception);
			return buildErrorResponse(exception, Response.Status.BAD_REQUEST);
		}

		// add it to the repo
		T result = getIngestionController().createRecordFromList(new Date().getTime(), records);

		if (result == null) {
			Exception exception = new Exception(REPOSITORY_UNAVAILABLE);
			LOGGER.error(REPOSITORY_UNAVAILABLE, exception);
			return buildErrorResponse(exception, Response.Status.SERVICE_UNAVAILABLE);
		}

		return buildResponse(Response.Status.NO_CONTENT, RECORDS_ADDED);
	}

	/**
	 * Generic getter method to retrieve data from the ingestion repositories. The requested type of records is
	 * specified by the record type. The controller will resolve the repository from this and return the list of records
	 * in that repository.
	 *
	 * @param recordType - the type of record (used to determine the repository)
	 * @param time - the maximum timestamp for any records to return
	 * @param <T> - an ingestion object (e.g. {@link BasePlanEntry} or {@link IcuAvailability}.
	 * @return - an HTTP response containing a list of records if they could be found (or an error response in case of
	 * any errors or no data).
	 */
	protected <T> Response ingestionGetBeforeTimeHandler(Class<T> recordType, long time) {
		// get the record asked for
		List<T> ingestionList = getIngestionController().getLatestRecordBeforeTime(recordType, time);

		if (ingestionList == null) {
			// repo was unavailable
			Exception e = new Exception(REPOSITORY_UNAVAILABLE);
			LOGGER.error(REPOSITORY_UNAVAILABLE, e);
			return buildErrorResponse(e, Response.Status.SERVICE_UNAVAILABLE);
		}	else if (ingestionList.isEmpty()) {
			// no records found
			Exception e = new FileNotFoundException(RECORD_NOT_FOUND);
			LOGGER.error(RECORD_NOT_FOUND, e);
			return buildErrorResponse(e, Response.Status.NOT_FOUND);
		}

		// return the records
		return buildResponse(Response.Status.OK, MediaType.APPLICATION_JSON, ingestionList);
	}

	/**
	 * Getter for the ingestion controller. This will retrieve the ingestion controller from the current environment.
	 * @return - the singleton {@link IngestionController} instance.
	 */
	protected IngestionController getIngestionController() {
		return (IngestionController) environment.getAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE);
	}
}
