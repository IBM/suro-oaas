package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.control.ResultMapper;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This will create a CSV structure by converting arrays of JSON entries. Each field name in the original JSON solution
 * will be mapped to a column in the CSV.
 *
 * @author Peter Ilfrich
 */
public class KeyToColumnResultMapper extends AbstractJsonMapper implements ResultMapper {

    /**
     * A logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyToColumnResultMapper.class);

    @Override
    public List<String[]> transformToCsv(JsonNode node, OutputMapping mapping) throws IOException {

        List<String[]> result = new ArrayList<>();

        String[] columns = this.getStringArray(createKeyToColumnHeader(node.get(mapping.getSources()[0].getSolutionKey())));
        result.add(columns);

        for (MappingSource source : mapping.getSources()) {
            result.addAll(extractKeyToColumnValues(node.get(source.getSolutionKey()), columns));
        }

        return result;
    }

    @Override
    public JsonNode transformToJson(JsonNode node, OutputMapping mapping) throws IOException {
        return null;
    }


    @Override
    public boolean validate(OutputMapping mapping) {
        boolean mainValidation = validate(mapping, new String[] { "csv" });
        if (!mainValidation) {
            return false;
        }

        for (MappingSource source : mapping.getSources()) {
            // just warn if any unnecessary fields are specified
            if (source.getRow() != null) {
                LOGGER.warn("Output mapping specifies unnecessary row mapping [ file: " + mapping.getFileName() + ", solution: " + source.getSolutionKey() + " ]");
            }
            if (source.getColumn() != null) {
                LOGGER.warn("Output mapping specifies unnecessary column mapping [ file: " + mapping.getFileName() + ", solution: " + source.getSolutionKey() + " ]");
            }
            if (source.getValue() != null) {
                LOGGER.warn("Output mapping specifies unnecessary value mapping [ file: " + mapping.getFileName() + ", solution: " + source.getSolutionKey() + " ]");
            }
        }

        return true;
    }

    /**
     * Compiles the list of headers from the list of entries. The passed parameter is a list of JSON nodes. It will
     * read all the field names of each item and add them (the name of the fields) to the header if they're not already
     * part of it.
     *
     * @param entryList - a list of json nodes containing field: value assignments.
     * @return - the extracted list of distinct field names of the passed entry list
     */
    protected List<String> createKeyToColumnHeader(JsonNode entryList) {
        List<String> columnList = new ArrayList<>();

        if (entryList.isArray()) {
            Iterator<JsonNode> entries = entryList.iterator();
            while (entries.hasNext()) {
                JsonNode entry = entries.next();
                Iterator<String> fieldNames = entry.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    if (!columnList.contains(fieldName)) {
                        columnList.add(fieldName);
                    }
                }
            }
        }

        return columnList;
    }

    /**
     * Creates the matrix of the values extracted from the specified data source (an array of nodes) using the list of
     * columns (as field names).
     * @param data - a list of json nodes
     * @param columns - the list of columns (field names) to extract)
     * @return - a matrix mapping the values to the indices of the array matching the index of the column specifying the
     * field name for the value.
     */
    protected List<String[]> extractKeyToColumnValues(JsonNode data, String[] columns) {
        List<String[]> result = new ArrayList<>();

        if (data.isArray()) {
            Iterator<JsonNode> entries = data.iterator();
            while (entries.hasNext()) {
                JsonNode node = entries.next();
                String[] rowValues = new String[columns.length];
                for (int index = 0; index < columns.length; index++) {
                    rowValues[index] = extractStringValue(node.get(columns[index]));
                }

                result.add(rowValues);
            }
        }

        return result;
    }
}
