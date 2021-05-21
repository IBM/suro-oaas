package com.ibm.au.optim.suro.model.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import java.io.IOException;
import java.util.List;

/**
 * Result mappers are used to transform the JSON result produced by the optimisation (e.g. solution.json) into the
 * corresponding required output files, which can be in different format. A mapper is always using a configuration
 * @see {@link OutputMapping} and some data.
 *
 * The result mappers and transformers already assume that the mapping configuration is valid and checked for
 * consistency. So currently the classes implementing the interfaces may not be the most robust.
 *
 * @author Peter Ilfrich
 */
public interface ResultMapper {


    /**
     * Takes the solution json node and uses the output mapping specification to convert the JSON into the desired CSV
     * structure. The result mapping defines which part of the json contains the data that I want and how to transform
     * the data from JSON into CSV.
     *
     * @param node - the original json node containing the result
     * @param mapping - the output mapping to process.
     * @return - a list of string arrays, where each array represents a row in a CSV file.
     * @throws IOException - the most likely cause of IOExceptions in this context is probably the
     * {@link com.fasterxml.jackson.databind.ObjectMapper}, which is used to created and manipulate json nodes.
     */
    List<String[]> transformToCsv(JsonNode node, OutputMapping mapping) throws IOException;

    /**
     * Takes the solution JSON, extracts the part of the json that is specified by the mapping. Additionally some
     * conversion in terms of the structure can happen as well (depends on the definition of the mapping)
     *
     * @param node - the solution node
     * @param mapping - the mapping specifying the part of the result to process and how to return it.
     * @return - a JSON node containing the extracted data from the original node
     * @throws IOException - the most likely cause of IOExceptions here are in the context of CRUD operations on JSON
     * nodes.
     */
    JsonNode transformToJson(JsonNode node, OutputMapping mapping) throws IOException;

    /**
     * Validates the provided output mapping for consistency with the requirements of the mapper.
     * @param mapping - the output mapping
     * @return - true if the validation is successful, false if the validation failed (which also should log an error)
     */
    boolean validate(OutputMapping mapping);
}
