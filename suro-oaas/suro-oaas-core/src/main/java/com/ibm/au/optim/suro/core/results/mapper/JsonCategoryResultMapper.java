package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.au.optim.suro.model.control.ResultMapper;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * This result mapper will take a piece of the solution and extract a single attribute of the entries in that part of
 * the solution, which will be used as new field names in a JSON node. This will essentially convert an array of N small
 * JSON nodes into one single JSON node with N fields. Each field name is the value of the extracted field name of the
 * array item. The output is JSON.
 *
 * @author Peter Ilfrich
 */
public class JsonCategoryResultMapper extends AbstractJsonMapper implements ResultMapper {

    /**
     * A logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonCategoryResultMapper.class);

    @Override
    public List<String[]> transformToCsv(JsonNode node, OutputMapping mapping) throws IOException {

        return null;

    }

    @Override
    public JsonNode transformToJson(JsonNode node, OutputMapping mapping) throws IOException {
        // transform the array with n elements into 1 entry with n fields
        Map<String, JsonNode> nodes = new HashMap<>();
        for (MappingSource source : mapping.getSources()) {
            Iterator<JsonNode> data = node.get(source.getSolutionKey()).iterator();
            while (data.hasNext()) {
                JsonNode entry = data.next();
                String key = extractStringValue(entry.get(source.getRow().getEntryKeys()[0]));
                JsonNode value = removeFields(entry, source.getRow().getEntryKeys());

                nodes.put(key, value);
            }
        }

        // create json array
        JsonNode result = createJsonObject(nodes);

        return result;
    }

    @Override
    public boolean validate(OutputMapping mapping) {
        boolean mainValidation = validate(mapping, new String[] { "json" });
        if (!mainValidation) {
            return false;
        }

        for (MappingSource source : mapping.getSources()) {
            // check that each source has exactly one row key
            if (source.getRow() == null || source.getRow().getEntryKeys() == null || source.getRow().getEntryKeys().length == 0) {
                LOGGER.error("Output mapping doesn't specify an entry key mapping for a source " +
                        "[ fileName: " + mapping.getFileName() + ", solutionKey: " + source.getSolutionKey() + " ] ");
                return false;
            }

            // check that each source has at least 1 value key
            if (source.getValue() == null || source.getValue().getKeys() == null || source.getValue().getKeys().length == 0) {
                LOGGER.error("Output mapping doesn't specify a value key mapping for a source " +
                        "[ fileName: " + mapping.getFileName() + ", solutionKey: " + source.getSolutionKey() + " ] ");
                return false;
            }

            if (source.getColumn() != null) {
                LOGGER.warn("Output mapping contains a source with a column mapping, even though it is not required. " +
                "[ fileName: " + mapping.getFileName() + " , solutionKey: " + source.getSolutionKey() + " ]");
            }
        }

        return true;
    }

    /**
     * Removes a list of fields from the original json node.
     *
     * @param originalNode - the original json node
     * @param fieldsToRemove - the fields to remove from the json node
     * @return - an object node without the fields specified in fieldsToRemove
     */
    protected JsonNode removeFields(JsonNode originalNode, String[] fieldsToRemove) {
        // create copy
        ObjectNode copy = new ObjectMapper().createObjectNode();
        Iterator<Map.Entry<String, JsonNode>> fields = originalNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            copy.set(field.getKey(), field.getValue());
        }

        // remove fields
        for (String field : fieldsToRemove) {
            copy.remove(field);
        }

        return copy;
    }

    /**
     * Creates a new JSON node containing fields and node values. The fields are determined by the keys of the provided
     * map. The values are provided by the value of the provided map.
     *
     * @param nodes - the map of field -> value mappings
     * @return - the new json node containing the specified fields.
     */
    protected JsonNode createJsonObject(Map<String, JsonNode> nodes) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.setAll(nodes);

        return node;
    }

}
