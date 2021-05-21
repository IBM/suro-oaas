package com.ibm.au.optim.suro.model.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.util.ProcessResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * The result manager provides functionality to transform and transpose the solution JSON as an output of the
 * optimisation into the desired output format (e.g. CSV or JSON with some transformation).
 *
 * The result manager also stores the result in the run repository.
 *
 * The transformation specification is stored as {@link Model#outputMappings} and can specifies multiple
 * different transformations resulting in multiple files that are attached to the run as result.
 *
 * @author Peter Ilfrich
 */
public interface ResultManager {

    /**
     * The prefix for the property key used in the source.properties to specify the implementing mapper class.
     */
    String TRANSFORMER_PREFIX = "resultmapper.transformer.";

    /**
     * Extracts the results from the provided JSON node using the models output definition to transform and transpose
     * the original JsonNode into all the requested output files. Once generated those files are attached as attachments
     * to the run object.
     * @param run - the run to process
     * @param resultNode - the solution as JSON node
     * @return - a process response indicating the outcome of the operation
     * @throws IOException - in case of serialisation or mapping issues.
     */
    ProcessResponse storeResults(Run run, JsonNode resultNode) throws IOException;

    /**
     * Extracts the results from the provided JSON node using the models output definition to transform and transpose
     * the original JsonNode into all the requested output files. Once generated those files are attached as attachments
     * to the run object. The JsonNode is provided as input stream.
     * @param run - the run to process
     * @param resultStream - the solution as InputStream, this will be parsed as JsonNode and then processed.
     * @return - a process response indicating the outcome of the operation
     * @throws IOException
     */
    ProcessResponse storeResults(Run run, InputStream resultStream) throws IOException;


    /**
     * Retrieves the currently configured model repository
     * @return - the repository that stores the OptimizationModels
     */
    ModelRepository getModelRepository();

    /**
     * Retrieves the currently configured run repository
     * @return - the repository that stores the Runs
     */
    RunRepository getRunRepository();
}
