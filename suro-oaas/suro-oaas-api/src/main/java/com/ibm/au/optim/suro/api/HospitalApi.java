package com.ibm.au.optim.suro.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.entities.domain.Department;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.SpecialistType;
import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;
import com.ibm.au.optim.suro.model.entities.domain.Ward;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * This class provides API endpoints to manage all hospital data. It allows CRUD operations on
 * hospitals, wards, departments, urgency categories (override).
 *
 * @author Peter Ilfrich
 */
@Path("/hospitals")
@Api(value = "/hospitals", description = "Hospital Setup API")
public class HospitalApi extends RestApi {


    /**
     * ObjectMapper used to serialise and de-serialise JSON/objects
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Error message returned if a hospital could not be found.
     */
    private static final String ERROR_HOSPITAL_NOT_FOUND = "Hospital not found";
    /**
     * Error message returned when the {@link HospitalController} cannot be found.
     */
    private static final String ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE = "Cannot access hospital management services.";
    /**
     * Error message returned when the {@link HospitalApi} generates an unexpected error.
     */
    private static final String ERROR_HOSPITAL_SERVER_ERROR = "An internal error occurred while serving the request.";
    /**
     * Error message returned when an invalid data document is passed to the APIs.
     */
    private static final String ERROR_HOSPITAL_INVALID_DATA = "Invalid %1s data.";
    
    /*
     * ****************************************************************************************
     *                                  CREATE OPERATIONS
     * ****************************************************************************************
     */


    /**
     * Creates a new hospital in the hospital repository. The passed data can also contain
     * departments, wards, urgency categories and specialist types
     *
     * @param stream - the hospital JSON data represented as input stream
     * @return - the created hospital with an _id assigned by the repository.
     */

    @POST
    @ApiOperation(value = "Creates a new hospital.", notes = "Creates a new Hospital with departments, wards, urgency categories and specialist types.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Hospital created"),
            @ApiResponse(code = 400, message = "Invalid hospital data."),
            @ApiResponse(code = 409, message = "There is already a hospital in the system"),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHospital(InputStream stream) {

        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            List<Hospital> hospitals = controller.getHospitals();
            if (hospitals.isEmpty() == true) {

                try {

                    Hospital hospital = MAPPER.readValue(stream, new TypeReference<Hospital>() {
                    });
                    Hospital newHospital = controller.createHospital(hospital);

                    response = this.buildResponse(Status.CREATED, newHospital);

                } catch (IOException ioex) {

                    response = this.buildResponse(Status.BAD_REQUEST, new ResponseMessage(String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "hospital")));
                }

            } else {

                response = this.buildResponse(Status.CONFLICT, new ResponseMessage("A hospital is already defined in the repository."));
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;

    }


    /**
     * Creates a new department with the provided data
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the department as input stream
     * @return - a response representing the outcome of the operation.
     */
    @POST
    @Path("/{hospitalId}/department")
    @ApiOperation(value = "Creates a new department within the hospital.", notes = "Adds a new department to the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Department created"),
            @ApiResponse(code = 400, message = "Invalid department data"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 409, message = "A department with that name already exists"),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDepartment(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {


        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital != null) {

                try {

                    Department department = MAPPER.readValue(stream, new TypeReference<Department>() {
                    });
                    Hospital result = controller.createDepartment(hospital, department);
                    if (result == null) {

                        response = this.buildResponse(Status.CONFLICT, new ResponseMessage("Department already exists."));

                    } else {

                        response = this.updateHospitalWithResponse(result, Status.CREATED);
                    }

                } catch (IOException ioex) {

                    response = this.buildResponse(Status.BAD_REQUEST, new ResponseMessage(String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "department")));
                }

            } else {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;

    }


    /**
     * Creates a new ward with the provided data
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the ward as input stream
     * @return - a response representing the outcome of the operation.
     */
    @POST
    @Path("/{hospitalId}/ward")
    @ApiOperation(value = "Creates a new ward within the hospital.", notes = "Adds a new ward to the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Ward created"),
            @ApiResponse(code = 400, message = "Invalid ward data"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 409, message = "A ward with that name already exists"),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWard(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital != null) {

                try {

                    Ward ward = MAPPER.readValue(stream, new TypeReference<Ward>() {
                    });
                    Hospital result = controller.createWard(hospital, ward);
                    if (result == null) {

                        response = this.buildResponse(Status.CONFLICT, new ResponseMessage("Ward already exists."));

                    } else {

                        response = this.updateHospitalWithResponse(result, Status.CREATED);
                    }

                } catch (IOException ioex) {

                    response = this.buildResponse(Status.BAD_REQUEST, new ResponseMessage(String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "ward")));
                }

            } else {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;

    }


    /**
     * Creates a new specialist type with the provided data
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the specialist type as input stream
     * @return - a response representing the outcome of the operation.
     */
    @POST
    @Path("/{hospitalId}/specialistType")
    @ApiOperation(value = "Creates a new specialist type within the hospital.", notes = "Adds a new specialist type to the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Specialist type created"),
            @ApiResponse(code = 400, message = "Invalid specialist type data"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 409, message = "A specialist type with that name already exists in this department."),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSpecialistType(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital != null) {

                try {

                    SpecialistType type = MAPPER.readValue(stream, new TypeReference<SpecialistType>() {
                    });
                    Hospital result = controller.createSpecialistType(hospital, type);
                    if (result == null) {

                        response = this.buildResponse(Status.CONFLICT, new ResponseMessage("Specialist type already exists."));

                    } else {

                        response = this.updateHospitalWithResponse(result, Status.CREATED);
                    }

                } catch (IOException ioex) {

                    response = this.buildResponse(Status.BAD_REQUEST, new ResponseMessage(String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "specialist type")));
                }

            } else {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /*
     * ****************************************************************************************
     *                                  READ OPERATIONS
     * ****************************************************************************************
     */

    /**
     * Reads a hospital from the database
     *
     * @param hospitalId - the id of the hospital
     * @return - a response containing the requested hospital data
     */
    @ApiOperation(value = "Retrieves the entire hospital data for a give hospital.", notes = "Retrieves hospital meta data, departments, wards, etc.", response = Hospital.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The requested hospital.", response = Hospital.class),
            @ApiResponse(code = 404, message = "The requested hospital could not be found."),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @GET
    @Path("/{hospitalId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalComplete(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId) {

        Response response = null;

        HospitalController controller = this.getHospitalController();
        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                response = this.buildResponse(Status.OK, hospital);
            }


        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;

    }

    /**
     * Retrieves a list of all hospitals in the system.
     *
     * @return - a response containing the list of hospitals in the system.
     */
    @ApiOperation(value = "Retrieves the entire hospital data for a give hospital.", notes = "Retrieves hospital meta data, departments, wards, etc.", response = Hospital.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The requested hospital.", response = Hospital.class),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitals() {


        Response response = null;

        HospitalController controller = this.getHospitalController();
        if (controller != null) {

            List<Hospital> hospitals = controller.getHospitals();
            if (hospitals == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                response = this.buildResponse(Status.OK, hospitals);
            }


        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }


    /**
     * Reads a list of wards from the database
     *
     * @param hospitalId - the id of the hospital
     * @return - a response containing the requested hospital data
     */
    @ApiOperation(value = "Retrieves a list of wards.", notes = "Retrieves all wards for the current hospital.", response = Ward.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of wards of the given hospital.", response = Ward.class),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @GET
    @Path("/{hospitalId}/wards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalWards(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId) {


        Response response = null;

        HospitalController controller = this.getHospitalController();
        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                response = this.buildResponse(Status.OK, hospital.getWards());
            }


        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Reads a list of departments from the database
     *
     * @param hospitalId - the id of the hospital
     * @return - a response containing the requested hospital data
     */
    @ApiOperation(value = "Retrieves a list of departments.", notes = "Retrieves all departments for the current hospital.", response = Department.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of departments of the given hospital.", response = Department.class),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @GET
    @Path("/{hospitalId}/departments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalDepartments(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId) {


        Response response = null;

        HospitalController controller = this.getHospitalController();
        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                response = this.buildResponse(Status.OK, hospital.getDepartments());
            }


        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Reads a list of specialist types from the database
     *
     * @param hospitalId - the id of the hospital
     * @return - a response containing the requested hospital data
     */
    @ApiOperation(value = "Retrieves a list of specialist types.", notes = "Retrieves all specialist types for the current hospital.", response = SpecialistType.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of specialist types of the given hospital.", response = SpecialistType.class),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @GET
    @Path("/{hospitalId}/specialistTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalSpecialistTypes(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId) {


        Response response = null;

        HospitalController controller = this.getHospitalController();
        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                response = this.buildResponse(Status.OK, hospital.getSpecialistTypes());
            }


        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Reads a list of categories from the database
     *
     * @param hospitalId - the id of the hospital
     * @return - a response containing the requested hospital data
     */
    @ApiOperation(value = "Retrieves a list of specialist types.", notes = "Retrieves all specialist types for the current hospital.", response = SpecialistType.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of specialist types of the given hospital.", response = SpecialistType.class),
            @ApiResponse(code = 404, message = "The requested hospital could not be found or there are no urgency categories."),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @GET
    @Path("/{hospitalId}/urgencyCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalUrgencyCategories(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId) {


        Response response = null;

        HospitalController controller = this.getHospitalController();
        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                List<UrgencyCategory> effectiveCategories = controller.compileUrgencyCategories(hospital);
                if (effectiveCategories.isEmpty()) {

                    response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage("No urgency categories available."));

                } else {

                    response = this.buildResponse(Status.OK, effectiveCategories);
                }
            }


        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;

    }

    /*
     * ****************************************************************************************
     *                                  UPDATE OPERATIONS
     * ****************************************************************************************
     */

    /**
     * Updates all departments of a specific hospital by overwriting all the data with the provided
     * data.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the department data as input stream
     * @return - a response representing the outcome of the operation
     */
    @PUT
    @Path("/{hospitalId}/departments")
    @ApiOperation(value = "Updates a department within the hospital.", notes = "Updates a department of the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Department updated"),
            @ApiResponse(code = 404, message = ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 409, message = "Duplicate department name"),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDepartments(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;

        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    List<Department> departments = new ObjectMapper().readValue(stream, new TypeReference<List<Department>>() {
                    });
                    Hospital result = getHospitalController().updateDepartments(hospital, departments);
                    if (result == null) {

                        response = this.buildResponse(Status.CONFLICT, "There are at least 2 departments with the same name.");

                    } else {

                        response = this.updateHospitalWithResponse(result, Status.OK);
                    }

                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "department"));
                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;

    }


    /**
     * Updates all wards of a specific hospital by overwriting all the data with the provided data.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the ward data as input stream
     * @return - a response representing the outcome of the operation
     */
    @PUT
    @Path("/{hospitalId}/wards")
    @ApiOperation(value = "Updates a ward within the hospital.", notes = "Updates a ward in the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ward updated"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 409, message = "Duplicate ward name"),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWards(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;

        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    List<Ward> wards = new ObjectMapper().readValue(stream, new TypeReference<List<Ward>>() {
                    });
                    Hospital result = getHospitalController().updateWards(hospital, wards);
                    if (result == null) {

                        response = this.buildResponse(Status.CONFLICT, "There are at least 2 wards with the same name.");

                    } else {

                        response = this.updateHospitalWithResponse(result, Status.OK);
                    }

                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "ward"));
                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Updates all specialist types of a specific hospital by overwriting all the data with the
     * provided data.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the specialist type data as input stream
     * @return - a response representing the outcome of the operation
     */
    @PUT
    @Path("/{hospitalId}/specialistTypes")
    @ApiOperation(value = "Updates a specialist type within the hospital.", notes = "Updates a specialist type in the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Specialist type updated"),
            @ApiResponse(code = 404, message = ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 409, message = "Duplicate specialist type"),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSpecialistTypes(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;

        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    List<SpecialistType> types = new ObjectMapper().readValue(stream, new TypeReference<List<SpecialistType>>() {
                    });
                    Hospital result = getHospitalController().updateSpecialistTypes(hospital, types);
                    if (result == null) {

                        response = this.buildResponse(Status.CONFLICT, "There are at least 2 spacialist types with the same name.");

                    } else {

                        response = this.updateHospitalWithResponse(result, Status.OK);
                    }

                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "specialist types"));

                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }


    /**
     * Updates all urgency categories of a specific hospital by overwriting all the data with the
     * provided data.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the urgency category data as input stream
     * @return - a response representing the outcome of the operation
     */
    @PUT
    @Path("/{hospitalId}/urgencyCategory")
    @ApiOperation(value = "Updates the urgency categories in the system.", notes = "Updates urgency categories in the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Urgency category updated"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUrgencyCategories(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response;

        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    List<UrgencyCategory> categories = new ObjectMapper().readValue(stream, new TypeReference<List<UrgencyCategory>>() {
                    });
                    Hospital result = controller.updateUrgencyCategories(hospital, categories);

                    response = this.updateHospitalWithResponse(result, Status.OK);


                } catch (IOException ie) {

                    LOGGER.error(String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "urgency categories"), ie);
                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "urgency categories"));

                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }


    /**
     * Updates the hospital meta data of a specific hospital by overwriting all the data with the
     * provided data.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the hospital meta data data as input stream
     * @return - a response representing the outcome of the operation
     */
    @PUT
    @Path("/{hospitalId}")
    @ApiOperation(value = "Updates an entire hospital with new data.", notes = "Provides new data for the entire hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Specialist type created"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHospital(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) throws Exception {

        try {
            Response response;

            HospitalController controller = this.getHospitalController();

            if (controller != null) {

                Hospital hospital = controller.getHospital(hospitalId);
                if (hospital == null) {

                    response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

                } else {

                    try {

                        Hospital newData = new ObjectMapper().readValue(stream, new TypeReference<Hospital>() {
                        });
                        newData.setRevision(hospital.getRevision());
                        newData.setId(hospital.getId());

                        Hospital result = controller.updateHospital(newData);

                        response = this.updateHospitalWithResponse(result, Status.OK);


                    } catch (IOException ie) {

                        response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "urgency categories"));

                    }
                }

            } else {

                response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
            }

            return response;
        } catch (Exception e2) {
            LOGGER.error("Error during execution of updateHospital", e2);
            return this.buildErrorResponse(e2);
        }


    }


    /**
     * Updates the hospital meta data of a specific hospital by overwriting all the data with the
     * provided data.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the hospital meta data data as input stream
     * @return - a response representing the outcome of the operation
     */
    @PUT
    @Path("/{hospitalId}/meta")
    @ApiOperation(value = "Updates the hospital meta data.", notes = "Updating the hospital meta data means to update " +
            "the region, the name and the 4 numeric attributes of the hospital root object.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Hospital meta data updated."),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHospitalMetaData(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {


        Response response = null;

        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    Hospital newData = new ObjectMapper().readValue(stream, new TypeReference<Hospital>() {
                    });
                    Hospital result = controller.updateHospitalMetaData(hospital, newData);

                    response = this.updateHospitalWithResponse(result, Status.OK);


                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "uergency categories"));

                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;


    }

    /*
     * ****************************************************************************************
     *                                  DELETE OPERATIONS
     * ****************************************************************************************
     */


    /**
     * Deletes a department from a hospital.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the department to be removed as input stream
     * @return a response object representing the outcome of the operation.
     */
    @DELETE
    @Path("/{hospitalId}/department")
    @ApiOperation(value = "Deletes a department from the hospital.", notes = "Deletes a department from the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Department deleted."),
            @ApiResponse(code = 400, message = "Invalid department data."),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDepartment(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;


        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);

            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    Department department = new ObjectMapper().readValue(stream, new TypeReference<Department>() {
                    });

                    Hospital result = controller.removeDepartment(hospital, department);
                    response = this.updateHospitalWithResponse(result, Status.OK);

                } catch (IOException ie) {
                    LOGGER.error("Invalid department data" , ie);
                    response = this.buildResponse(Status.BAD_REQUEST, new ResponseMessage(String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "department")));
                }

            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Deletes a ward from a hospital.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the ward to be removed as input stream
     * @return a response object representing the outcome of the operation.
     */
    @DELETE
    @Path("/{hospitalId}/ward")
    @ApiOperation(value = "Deletes a ward from the hospital.", notes = "Deletes a ward from the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ward deleted"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWard(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    Ward ward = new ObjectMapper().readValue(stream, new TypeReference<Ward>() {
                    });

                    Hospital result = getHospitalController().removeWard(hospital, ward);

                    response = this.updateHospitalWithResponse(result, Status.OK);

                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "ward"));
                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Deletes a specialist type from a hospital.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the specialist type to be removed as input stream
     * @return a response object representing the outcome of the operation.
     */
    @DELETE
    @Path("/{hospitalId}/specialistType")
    @ApiOperation(value = "Deletes a specialist type from the hospital.", notes = "Deletes a specialist type from the hospital.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Specialist type deleted"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSpecialistType(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {


        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {

                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    SpecialistType type = new ObjectMapper().readValue(stream, new TypeReference<SpecialistType>() {
                    });

                    Hospital result = getHospitalController().removeSpecialistType(hospital, type);

                    response = this.updateHospitalWithResponse(result, Status.OK);

                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "specialist type"));
                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /**
     * Deletes an urgency category from a hospital.
     *
     * @param hospitalId - the id of the hospital
     * @param stream     - the urgency category to be removed as input stream
     * @return a response object representing the outcome of the operation.
     */
    @DELETE
    @Path("/{hospitalId}/urgencyCategory")
    @ApiOperation(value = "Deletes an urgency category from the hospital.", notes = "Deletes the data for an urgency category from the hospital. This will result in the hospital using the region defaults.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Urgency category deleted"),
            @ApiResponse(code = 404, message = HospitalApi.ERROR_HOSPITAL_NOT_FOUND),
            @ApiResponse(code = 500, message = HospitalApi.ERROR_HOSPITAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE)
    })
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "Authorization", value = "authentication header or authorization token (post authentication) base64 encoded.", required = false, dataType = "String", paramType = "header")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUrgencyCategory(
            @ApiParam(value = "The hospital id", required = true) @PathParam("hospitalId") String hospitalId, InputStream stream) {

        Response response = null;
        HospitalController controller = this.getHospitalController();

        if (controller != null) {

            Hospital hospital = controller.getHospital(hospitalId);
            if (hospital == null) {
                response = this.buildResponse(Status.NOT_FOUND, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_NOT_FOUND));

            } else {

                try {

                    UrgencyCategory cat = new ObjectMapper().readValue(stream, new TypeReference<UrgencyCategory>() {
                    });

                    Hospital result = getHospitalController().removeUrgencyCategory(hospital, cat);

                    response = this.updateHospitalWithResponse(result, Status.OK);

                } catch (IOException ie) {

                    response = this.buildResponse(Status.BAD_REQUEST, String.format(HospitalApi.ERROR_HOSPITAL_INVALID_DATA, "urgency category"));
                }
            }

        } else {

            response = this.buildResponse(Status.SERVICE_UNAVAILABLE, new ResponseMessage(HospitalApi.ERROR_HOSPITAL_SERVICES_NOT_AVAILABLE));
        }

        return response;
    }

    /* HELPER METHODS */

    /**
     * Retrieves the hospital controller from the current environment and returns it.
     *
     * @return - a hospital controller
     */
    protected HospitalController getHospitalController() {
        return (HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
    }

    /**
     * Retrieves the hospital with the provided id from the hospital controller.
     *
     * @param hospitalId - the id of the requested hospital
     * @return - a hospital instance or null, if the hospital with the specified id doesn't exist.
     */
    protected Hospital getHospital(String hospitalId) {
        return getHospitalController().getHospital(hospitalId);
    }


    /**
     * Updates the hospital with the provided new data and creates an empty response with the
     * provided response status
     *
     * @param newData - the new hospital data
     * @param status  - the status code for the response
     * @return - a response object with the provided status
     */
    protected Response updateHospitalWithResponse(Hospital newData, Status status) {

        Response response = null;

        if (newData == null) {
            response = this.buildErrorResponse(new NullPointerException("Update operation failed."));
        } else {
            response = this.buildResponse(status, newData);
        }

        return response;
    }

}
