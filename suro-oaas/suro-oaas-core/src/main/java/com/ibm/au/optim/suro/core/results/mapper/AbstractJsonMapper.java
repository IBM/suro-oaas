package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.mapping.ComplexStringKey;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSpecification;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for all the JSON and CSV mappers and transformers. This method provides base functionality to all
 * sub-classes to transform between the original JSON and the desired output format.
 *
 * @author Peter Ilfrich
 */
public abstract class AbstractJsonMapper {

    /**
     * A logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJsonMapper.class);


    /**
     * Validates the essential requirements for any mapping. Subclasses can call this method from the super class
     * providing the allowed file extensions.
     *
     * If the validation fails an error message will be logged.
     *
     * @param mapping - the mapping to validate
     * @param allowedFileExtension - a string array of file extensions (without the dot) - e.g. [ "csv", "json" ]
     * @return - true if the validation is successful, false if the validation failed.
     */
    public boolean validate(OutputMapping mapping, String[] allowedFileExtension) {
        if (mapping == null) {
            return false;
        }

        if (mapping.getFileName() == null) {
            LOGGER.error("Output mapping doesn't provide file name.");
            return false;
        }

        // check if file name specifies valid extension
        boolean validExtension = false;
        for (String ext : allowedFileExtension) {
            if (mapping.getFileName() != null && mapping.getFileName().toLowerCase().endsWith("." + ext)) {
                validExtension = true;
            }
        }
        if (!validExtension) {
            LOGGER.error("Output mapping specifies a file format that isn't compatible with the specified mapping type " +
                    "' [ file: '" + mapping.getFileName() + "'; extensions: '" + Arrays.toString(allowedFileExtension) + "' ]");
            return false;
        }

        if (mapping.getSources() == null || mapping.getSources().length == 0) {
            LOGGER.error("Output mapping doesn't specify a source " + mapping.getFileName());
            return false;
        }

        // validate that each source has a solution key (minimum requirement)
        for (MappingSource source : mapping.getSources()) {

            if (source.getSolutionKey() == null) {
                LOGGER.error("No solution key provided for source in " + mapping.getFileName() + ".");
                return false;
            }
        }

        return true;
    }

    /**
     * Determines the maximum number of entries in either the label or entry key of the provided specification.
     * @param spec - a row or column specification
     * @return - the maximum size of the spec
     */
    public int getMappingSpecificationMaxSize(MappingSpecification spec) {
        int max = 0;
        if (spec == null) {
            return max;
        }

        if (spec.getLabels() != null) {
            max = spec.getLabels().length;
        }

        if (spec.getEntryKeys() != null) {
            max = Math.max(max, spec.getEntryKeys().length);
        }

        return max;
    }

    /**
     * Retrieves the value from a JSON node and makes sure to return it as String (performing the necessary conversions)
     * @param node - the node to extract the value from
     * @return - the string value represented by this node.
     */
    protected String extractStringValue(JsonNode node) {
        if (node == null || node.isNull() || !node.isValueNode()) {
            return null;
        }

        if (node.isInt()) {
            return Integer.toString(node.intValue());
        } else if (node.isDouble()) {
            double val = node.doubleValue();
            double rounded = Math.round(val);
            if (Math.abs(rounded - val) < 0.001) {
                return Long.toString(Math.round(val));
            }
            return Double.toString(val);
        } else if (node.isTextual()) {
            return node.asText();
        } else {
            return "";
        }
    }

    /**
     * This method only assumes one key per columns and creates a single row of data from the solution data by iterating
     * over a list of entries containing the specific column values for the output.
     *
     * @param source - the source specifying which values to extract from the solution and the name of the row
     * @param solutionData - a list of JSON nodes extracted from the original solution JSON using the solutionKey from
     *                     the source
     * @param columns - the list of columns to fill. For each column the method will find a value in the solution data
     *
     * @return - an array containing a single row label in the first position, followed by data representing each of
     * the specified columns.
     */
    protected String[] extractSingleKeyRows(MappingSource source, Iterator<JsonNode> solutionData, String[] columns) {
        MappingSpecification row = source.getRow();

        String[] currentRow = new String[columns.length + 1];
        currentRow[0] = row.getLabels()[0];

        while (solutionData.hasNext()) {
            JsonNode entry = solutionData.next();

            // if entry cannot be matched to column, skip entry
            String currentColumn = extractStringValue(entry.get(source.getColumn().getEntryKeys()[0]));
            if (currentColumn == null) {
                continue;
            }

            // extract current value
            String currentValue;
            if (source.getValue() != null) {
                // can only be single key
                String key = source.getValue().getKeys()[0];
                currentValue = extractStringValue(entry.get(key));
            } else if (row.getEntryKeys() != null) {
                currentValue = extractStringValue(entry.get(row.getEntryKeys()[0]));
            } else {
                // specification doesn't allow to extract value, set empty string
                currentValue = "";
            }

            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equals(currentColumn)) {
                    // shift + 1 for the row label
                    currentRow[i + 1] = currentValue;
                }
            }
        }

        return currentRow;
    }

    /**
     * Extracts multiple lines from the solution data offering support for complex keys as well.
     *
     * @param source - the mapping source specifying all the mappings
     * @param solutionData - the extracted list of entries from the original solution which contain the data for the
     *                     current extraction
     * @param columns - the list of columns labels
     * @return - a list of string arrays (rows) representing the extracted information.
     */
    protected List<String[]> extractMultiKeyRows(MappingSource source, Iterator<JsonNode> solutionData, String[] columns) {
        List<String[]> rows = new ArrayList<>();
        Map<ComplexStringKey, String[]> rowValues = new HashMap<>();


        MappingSpecification row = source.getRow();
        String[] keys = row.getEntryKeys();

        while (solutionData.hasNext()) {
            JsonNode entry = solutionData.next();
            String[] complexKeyBase = new String[keys.length];
            for (int i = 0; i < keys.length; i++) {
                complexKeyBase[i] = extractStringValue(entry.get(keys[i]));
            }
            ComplexStringKey rowKey = new ComplexStringKey(complexKeyBase);

            String[] values = rowValues.get(rowKey);
            if (values == null) {
                values = new String[columns.length];
                rowValues.put(rowKey, values);
            }

            String currentColumn = extractStringValue(entry.get(source.getColumn().getEntryKeys()[0]));

            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equals(currentColumn)) {
                    // extract value
                    String currentValue = extractStringValue(entry.get(source.getValue().getKeys()[0]));
                    values[i] = currentValue;
                }
            }
        }


        rows.addAll(createMultiKeyRows(rowValues, columns));
        return rows;
    }

    /**
     * Creates a list of multi key rows. Essentially this creates the output apart from the header.
     *
     * @param rowValues - the values for each row. Each row is represented by a {@link ComplexStringKey} which is the
     *                  key.
     * @param columns - only used to determine the length of the columns
     * @return - a matrix of values including the row labels (key), but excluding the header (containing the columns)
     */
    public List<String[]> createMultiKeyRows(Map<ComplexStringKey, String[]> rowValues, String[] columns) {
        List<String[]> rows = new ArrayList<>();

        // write rows
        for (Map.Entry<ComplexStringKey, String[]> entry : rowValues.entrySet()) {
            ComplexStringKey key = entry.getKey();
            String[] currentRow = new String[key.getContent().length + columns.length];

            int index = 0;
            for (String keyPart : key.getContent()) {
                currentRow[index] = keyPart;
                index++;
            }

            for (String value : rowValues.get(key)) {
                currentRow[index] = value;
                index++;
            }

            rows.add(currentRow);
        }

        return rows;
    }

    /**
     * Creates a list of string arrays (rows) from the solution data.
     * @param solutionData - the part of the solution json that contains the data required for this mapping
     * @param source - the mapping source specifying how data is mapped from the original to the output.
     * @param columns - the list of columns (will only be used to determine the length of the data.
     * @return - a matrix of row values with complex or single row keys (row labels) and a list of values
     */
    protected  List<String[]> extractRows(Iterator<JsonNode> solutionData, MappingSource source, String[] columns) {
        List<String[]> rows = new ArrayList<>();

        MappingSpecification row = source.getRow();

        if (row.getLabels() != null && row.getLabels().length == 1) {
            // e.g. "total theatres" (+1 = "Item")
            rows.add(extractSingleKeyRows(source, solutionData, columns));

        }  else if (row.getEntryKeys() != null && row.getEntryKeys().length >= 1) {
            // complex keys, like: "Unit Code","Surgery Type","Urgency Category"
            rows.addAll(extractMultiKeyRows(source, solutionData, columns));
        }

        return rows;
    }

    /**
     * Extract the list of column labels. This can either be done by looking into the mapping sources, where the labels
     * of the columns might be fixed. If not, the data can be found by extracting it from the provided solution JSON
     * node. The sources will be used to extract the column labels from the original data.
     *
     * @param sources - the list of mapping sources that compile the current output file.
     * @param node - the entire solution JSON node.
     * @return - a list of column labels (not including the row label column labels (first x depending on the size of
     * the ComplexStringKey).
     */
    protected String[] extractColumns(MappingSource[] sources, JsonNode node) {
        List<String> columns = new ArrayList<>();

        for (MappingSource source : sources) {
            if (source.getColumn() == null || source.getColumn().getEntryKeys() == null || source.getColumn().getEntryKeys().length != 1) {
                continue;
            }

            Iterator<JsonNode> entries = node.get(source.getSolutionKey()).iterator();
            while (entries.hasNext()) {
                JsonNode entry = entries.next();
                String columnValue = extractStringValue(entry.get(source.getColumn().getEntryKeys()[0]));
                if (!columns.contains(columnValue)) {
                    columns.add(columnValue);
                }
            }
        }

        return getStringArray(columns);
    }

    /**
     * Creates the header of a CSV file with the provided columns and the specification of the output mapping.
     *
     * @param mapping - the output mapping used to find labels for the header.
     * @param columns - the list of column labels (e.g. "1", "2,", "3,", ...)
     * @return - the first row of the CSV containing all the headers (row header and column labels)
     */
    protected String[] createHeader(OutputMapping mapping, String[] columns) {
        MappingSource[] sources = mapping.getSources();
        List<String> baseColumns = new ArrayList<>();

        String[] columnLabels = Arrays.copyOf(columns, columns.length);

        for (MappingSource source : sources) {
            MappingSpecification row = source.getRow();
            if (row == null) {
                continue;
            }

            if ((row.getLabels() != null && row.getLabels().length == 1) || (row.getEntryKeys() != null && row.getEntryKeys().length == 1)) {
                // just return "Item" in the first column
                if (source.getColumn() != null && source.getColumn().getLabels() != null && source.getColumn().getLabels().length == 1) {
                    String labelPrefix = source.getColumn().getLabels()[0];
                    for (int i = 0; i < columnLabels.length; i++) {
                        StringBuilder buf = new StringBuilder();
                        buf.append(labelPrefix).append(" ").append(columnLabels[i]);
                        columnLabels[i] = buf.toString();
                    }
                }
                return createRow(new String[] { "Item" }, columnLabels);
            } else if (row.getLabels() != null && row.getLabels().length > 1 && baseColumns.isEmpty()) {
                // first item, just add labels
                for (String label : row.getLabels()) {
                    baseColumns.add(label);
                }
            }
        }

        return createRow(getStringArray(baseColumns), columns);
    }

    /**
     * Creates a row combining the labels and values (array concatenation)
     * @param labels - the first couple of items
     * @param values - the last items
     * @return - the combined array of first couple of items and last items.
     */
    protected String[] createRow(String[] labels, String[] values) {
        if (labels == null) {
            labels = new String[0];
        }
        if (values == null) {
            values = new String[0];
        }

        String[] result = new String[labels.length + values.length];
        int index = 0;
        for (String label : labels) {
            result[index] = label;
            index++;
        }
        for (String value : values) {
            result[index] = value;
            index++;
        }

        return result;
    }

    /**
     * Converts a list of strings into a String[] array.
     * @param strings - the list of strings
     * @return - the array of strings
     */
    protected String[] getStringArray(List<String> strings) {
        String[] result = new String[strings.size()];
        int index = 0;
        for (String string : strings) {
            result[index] = string;
            index++;
        }

        return result;
    }
}
