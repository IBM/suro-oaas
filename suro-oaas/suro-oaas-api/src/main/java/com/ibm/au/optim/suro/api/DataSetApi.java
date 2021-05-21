package com.ibm.au.optim.suro.api;

import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.jaws.data.DataServiceException;
import com.wordnik.swagger.annotations.*;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * <p>
 * A JAX-RS (JSR-311) REST endpoint for the SURO <b>Data Set</b> API. This
 * class provides callable Web service operations for uploading, listing, and retrieving
 * data sets {@link DataSet}.
 * </p>
 *
 * @author Jeremy Wazny
 * @deprecated
 */
@Deprecated
@Path("/datasets")
@Api(value = "/datasets", description = "Data sets")
public class DataSetApi extends RestApi {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetApi.class);

    /**
     * <p/>
     * Endpoint for uploading a set of files, which get stored together
     * in the same data set {@link DataSet}.
     *
     * @return A JAX-RS response wrapping the newly created {@link DataSet} document
     * or an error if it could not be created.
     */
	@ApiImplicitParams(value = {		
			@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
	})
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putFiles(@Context HttpServletRequest request) {

        LOGGER.info("putFiles");
        LOGGER.info(request.toString());
        if (ServletFileUpload.isMultipartContent(request)) {
            LOGGER.info("isMultipartContent");
        }

        if (!ServletFileUpload.isMultipartContent(request)) {
            LOGGER.info("NOT isMultipartContent");
            return buildResponse(Status.BAD_REQUEST, "Bad request");
        }

        java.nio.file.Path tmpdir;
        try {
            tmpdir = Files.createTempDirectory("suro");
            (new File(tmpdir.toString())).deleteOnExit();
        } catch (IOException e) {
            LOGGER.error("Could not create temporary directory to receive uploaded files", e);
            return buildErrorResponse(e);
        }
        final ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator it;
        List<String> filePaths = new ArrayList<>();
        List<String> filenames = new ArrayList<>();

        try {
            it = upload.getItemIterator(request);
            while (it.hasNext()) {
                FileItemStream item = it.next();
                String name = item.getName();

                if (!item.isFormField()) {
                    // Not a simple form field, i.e. it's a file.
                    InputStream in = item.openStream();
                    LOGGER.info("receiving: " + name);

                    java.nio.file.Path file = tmpdir.resolve(name);
                    filenames.add(name);
                    filePaths.add(file.toString());
                    OutputStream out = new FileOutputStream(file.toString());
                    IOUtils.copy(in, out);
                    out.close();
                    LOGGER.info("finished " + file.toString());
                }
            }
        } catch (FileUploadException | IOException e) {
            LOGGER.error("File upload failed", e);
            return buildErrorResponse(e);
        }

        // Create couch data set object and attach files.
        LOGGER.info("getting repository");
        DataSetRepository repo = this.getDataSetRepository();
        Model model = ((ModelRepository) environment.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE)).getAll().get(0);
        LOGGER.info("creating data set");
        Map<String, String[]> params = new HashMap<>();
        params.put("modelId", new String[]{model.getId()});
        params.put("label", new String[]{new Date().toString()});
        DataSet set = new DataSet(new Date().toString(), model.getId(), null);
        repo.addItem(set);
        LOGGER.info("created data set: " + set);
        for (int i = 0; i < filePaths.size(); ++i) {
            FileInputStream in;
            String path = filePaths.get(i);
            String name = filenames.get(i);
            try {
                in = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                LOGGER.error("Could not open uploaded file");
                return buildErrorResponse(e);
            }
            LOGGER.info("attaching: " + name);
            repo.attach(set, name, "text/plain", in);
        }
        LOGGER.info("ok");
        return buildResponse(Status.OK, set);
    }

    /**
     * <p>
     * Provides a list of {@link DataSet} documents that are available.
     * </p>
     *
     * @return A list of data sets
     */
    @ApiOperation(value = "Gets the list of data sets.", notes = "Retrieves all the data sets available for optimisation runs.", response = DataSet.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A list of data sets.", response = DataSet.class),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataSets() {
        try {

            DataSetRepository repo = this.getDataSetRepository();
            List<DataSet> sets = repo.getAll();
            return buildResponse(Status.OK, sets);

        } catch (Exception e) {
            LOGGER.error("Error getting data sets", e);
            return buildErrorResponse(e);
        }
    }

    /**
     * <p>
     * Returns a {@link DataSet} for the specified document id.
     * </p>
     *
     * @param dataSetId A {@link DataSet} document ID
     * @return A JAX-RS response wrapping a {@link DataSet} document or Not
     * Found error if the strategy does not exist
     */
    @ApiOperation(value = "Get a data set", notes = "Retrieves the data set for the given data set ID.", response = DataSet.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A data set document", response = DataSet.class),
            @ApiResponse(code = 404, message = "Data set not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{dataSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataSet(
            @ApiParam(value = "The data set id", required = true) @PathParam("dataSetId") String dataSetId) {

        DataSetController cnt = this.getDataSetController();
        DataSet set = cnt.getDataSet(dataSetId, true);
        return buildResponse(Status.OK, set);
    }

    /**
     * <p>
     * Returns a specific file contained within the named {@link DataSet} for the specified document id.
     * </p>
     *
     * @param dataSetId A {@link DataSet} document ID
     * @param filename  A filename.
     * @return A JAX-RS response wrapping a {@link DataSet} document or Not
     * Found error if the strategy does not exist
     */
    @ApiOperation(value = "Get a data set", notes = "Retrieves the data set for the given data set ID.", response = DataSet.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "A file", response = DataSet.class),
            @ApiResponse(code = 404, message = "Data set or file not found"),
            @ApiResponse(code = 500, message = "An internal error has occurred while processing the request.")
    })
    @ApiImplicitParams(value = {		
    		@ApiImplicitParam(name = "Authorization", value="authentication header or authorization token (post authentication) base64 encoded.", required=false, dataType="String", paramType="header") 
    })
    @GET
    @Path("/{dataSetId}/{filename}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDataSetFile(
            @ApiParam(value = "The data set id", required = true) @PathParam("dataSetId") String dataSetId,
            @ApiParam(value = "The filename", required = true) @PathParam("filename") String filename) {

        DataSetRepository repo = getDataSetRepository();
        InputStream str = repo.getAttachment(dataSetId, filename);
        try {
            return buildResponse(Status.OK, IOUtils.toString(str));
        } catch (IOException e) {
            LOGGER.error("Error reading attached file", e);
            return buildErrorResponse(e);
        }
    }

    /**
     * This method returns the implementation {@link DataSetRepository} that
     * has been currently configured with the application. It retrieves such
     * instance from the {@link com.ibm.au.jaws.web.core.runtime.Environment} implementation injected in the
     * instance by looking up the {@link DataSetRepository#DATA_SET_REPOSITORY_INSTANCE}
     * attribute.
     *
     * @return the configured {@link DataSetRepository} implementation.
     */
    protected DataSetRepository getDataSetRepository() {

        DataSetRepository repo = (DataSetRepository) this.environment.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);

        if (repo == null) {
            throw new DataServiceException("DataSet repository is not available.");
        }

        return repo;
    }

    protected DataSetController getDataSetController() {
        DataSetController cnt = (DataSetController) this.environment.getAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE);
        if (cnt == null) {
            throw new DataServiceException("DataSet controller is not available.");
        }
        return cnt;
    }
}
