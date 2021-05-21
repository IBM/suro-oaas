package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.control.ResultMapper;
import com.ibm.au.optim.suro.model.entities.mapping.ComplexStringKey;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This appender works similar to the {@link ComplexResultMapper}. It will use the mapping configuration to extract
 * data from multiple sources and convert them to CSV. The main difference is that the append result mapper requires the
 * column labels to be different.
 *
 * This mapper will create a matrix for each provided source and then produce a bigger matrix, which is the sub-matrices
 * appended horizontally. This also means that the row labels need to match.
 *
 * @author Peter Ilfrich
 */
public class ComplexAppendResultMapper extends ComplexResultMapper implements ResultMapper {


    /**
     * A logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexAppendResultMapper.class);


    /**
     * 
     */
    @Override
    public List<String[]> transformToCsv(JsonNode node, OutputMapping mapping) throws IOException {

        List<String[]> result = new ArrayList<>();

        // extract information
        String[] rowLabels = extractRowLabels(mapping);
        String[] columnLabels = Arrays.copyOf(rowLabels, rowLabels.length);

        for (MappingSource source : mapping.getSources()) {
            List<String[]> rows = this.extractRows(node.get(source.getSolutionKey()).iterator(), source, extractColumns(new MappingSource[] { source }, node));
            columnLabels = appendColumnLabels(columnLabels, extractColumns(new MappingSource[]{ source }, node), source);


            if (result.isEmpty()) {
                result.add(columnLabels);
            } else {
                result.set(0, columnLabels);
            }

            result = appendRows(result, rowLabels, rows);
        }

        return result;

    }

    /**
     * 
     */
    @Override
    public boolean validate(OutputMapping mapping) {

        boolean standardValidation = validate(mapping, new String[] { "csv" });
        if (!standardValidation) {
            return false;
        }

        boolean useLabels = false;
        boolean useKeys = false;
        List<String> usedKeyOrLabel = new ArrayList<>();

        // validate source consistency
        for (MappingSource source : mapping.getSources()) {

            // at least a label or an entry key needs to be provided
            if (source.getColumn() == null ||
                    !source.getColumn().hasNumberOfLabels(1) &&
                    !source.getColumn().hasNumberOfEntryKeys(1)) {
                LOGGER.error("Output mapping specifies invalid column definition for source with ");
                return false;
            }

            // check row definition
            if ((source.getRowLabels() == null || source.getRowLabels().length < 1) &&
                    (source.getRowEntryKeys() == null || source.getRowEntryKeys().length < 1)) {
                LOGGER.error("Output mapping specifies invalid row definition for source [ file: '" + mapping.getFileName() + "', solutionKey: '" + source.getSolutionKey() + "'");
                return false;
            }

            String[] currentKeyOrLabel = new String[] {};

            // first source, figure out whether to use labels or keys
            if (!useLabels && !useKeys) {
                if (source.getRow().hasLabels()) {
                    // use labels
                    useLabels = true;
                    usedKeyOrLabel.addAll(Arrays.asList(source.getRow().getLabels()));

                } else if (source.getRow().hasEntryKeys()) {
                    // use keys
                    useKeys = true;
                    usedKeyOrLabel.addAll(Arrays.asList(source.getRow().getEntryKeys()));
                }

            } else if (useLabels) {
                // determine current labels and check consistent number of items

                if (source.getRow().getLabels() == null || source.getRow().getLabels().length != usedKeyOrLabel.size()) {
                    LOGGER.error("Output mapping specifies invalid source (incorrect number of labels) [ file: '" + mapping.getFileName() + "', solutionKey: '" + source.getSolutionKey() + "' ]");
                    return false;
                }
                currentKeyOrLabel = source.getRow().getLabels();

            } else if (useKeys) {
                if (source.getRow().getEntryKeys() == null || source.getRow().getEntryKeys().length != usedKeyOrLabel.size()) {
                    LOGGER.error("Output mapping specifies invalid source (incorrect number of entry keys) [ file: '" + mapping.getFileName() + "', solutionKey: '" + source.getSolutionKey() + "' ]");
                    return false;
                }
                currentKeyOrLabel = source.getRow().getEntryKeys();
            }

            // validate current labels
            for (String labelOrKey : currentKeyOrLabel) {
                if (!usedKeyOrLabel.contains(labelOrKey)) {
                    LOGGER.error("Output mapping specifies label or key for row mapping which doesn't match previous entries [ file: '" + mapping.getFileName() + "', solutionKey: '" + source.getSolutionKey() + "', label/key: '" + labelOrKey + "' ]");
                    return false;
                }
            }


            // value mapping is irrelevant (fallback is available)
        }

        return true;
    }

    /**
     * Extracts the row label mappings from the mapping source. This will take the first source and read the row labels
     * from this first source. Because the consistency checker should complain if the row labels are not identical for
     * each source.
     *
     * @param mapping - the output mapping
     * @return - the list of row labels (1st row, first few columns)
     */
    protected String[] extractRowLabels(OutputMapping mapping) {
        return mapping.getSources()[0].getRow().getLabels();
    }

    /**
     * This method will extend the old list of columns labels (e.g. period 1, period2, ...) with a list of additional
     * labels. Each of the additional labels (might be values, e.g. 1, 2, 3) will receive a prefix from the mapping
     * source that is responsible for this section (the columns) of the result.
     *
     * @param oldLabels - the existing list of labels (header row)
     * @param additionalLabels - the header labels to append to the old labels list
     * @param source - the source specifying any prefix for the new labels
     * @return - a list of column labels as it is printed into the CSV.
     */
    protected String[] appendColumnLabels(String[] oldLabels, String[] additionalLabels, MappingSource source) {
        String[] result = Arrays.copyOf(oldLabels, oldLabels.length + additionalLabels.length);
        // retrieve prefix from the source
        String prefix = source.getColumn().getLabels()[0];

        // start inserting after the old labels
        int index = oldLabels.length;

        for (String label : additionalLabels) {
            StringBuilder buf = new StringBuilder();
            // use prefix to compile column label
            buf.append(prefix).append(" ").append(label);
            result[index] = buf.toString();
            index++;
        }

        return result;
    }


    /**
     * Array merge operation, which will append the new data array to the old data array and create an array that has
     * the size of the old data + the size of the new data.
     *
     * @param oldData - the existing data
     * @param newData - the new data to append
     * @return - an array containing first oldData and then newData
     */
    protected String[] appendData(String[] oldData, String[] newData) {
        return (String[]) ArrayUtils.addAll(oldData, newData);
    }

    /**
     * This method is used to compile the result. The oldResult contains the data of the CSV output so far. The
     * rowLabels are the first few columns of the next row to add. The rowData contains the remaining data of the row.
     * The length of items in oldResult should be the same as the length of the row labels and the length of the row
     * data combined.
     *
     * @param oldResult - the existing result
     * @param rowLabels - the labels for the new rows
     * @param rowData - a list of new rows to append. They will use the rowLabels as prefix (columns) for each row.
     * @return - the oldResult plus the new rowData.
     */
    protected List<String[]> appendRows(List<String[]> oldResult, String[] rowLabels, List<String[]> rowData) {

        int currentSize = 0;

        Map<ComplexStringKey, String[]> values = new HashMap<>();
        // ignore the header row [ .subList() ]
        for (String[] oldEntry : oldResult.subList(1, oldResult.size())) {
            // extract result into map (key -> values[])
            ComplexStringKey key = new ComplexStringKey(Arrays.copyOfRange(oldEntry, 0, rowLabels.length));
            String[] value = Arrays.copyOfRange(oldEntry, rowLabels.length, oldEntry.length);
            values.put(key, value);
            // store current length of the data in case we have to pre-fill new entries
            currentSize = value.length;
        }

        // add row data to the map
        values = compileMapWithComplexKeys(values, rowLabels, rowData, currentSize);

        // update key length
        int keyLength = (!values.keySet().isEmpty()) ? values.keySet().iterator().next().getContent().length : 0;

        return convertMapToList(oldResult, values, keyLength);
    }


    /**
     * This method will use the row data and extract
     * This is part of the functionality to append new rows to the existing result.
     * @param values - the old values
     * @param rowLabels - the labels for the rows (primarily used to figure out how long the row label is)
     * @param rowData - the row data including row label values (which are used compile the complex keys)
     * @param currentSize - the current size of the result set. This is used to determine at which point of the array to
     *                    insert the new data.
     *
     * @return - an updated map of the input parameter values, which contains the data extracted from rowData.
     */
    protected Map<ComplexStringKey, String[]> compileMapWithComplexKeys(Map<ComplexStringKey, String[]> values, String[] rowLabels, List<String[]> rowData, int currentSize) {

        // preload result if provided
        Map<ComplexStringKey, String[]> result = new HashMap<>();
        if (values != null) {
            result = values;
        }

        // iterate through the new data
        for (String[] rowEntry : rowData) {
            // extract key and value
            ComplexStringKey key = new ComplexStringKey(Arrays.copyOfRange(rowEntry, 0, rowLabels.length));
            String[] value = Arrays.copyOfRange(rowEntry, rowLabels.length, rowEntry.length);


            // handle case if result for key is empty
            if (result.get(key) == null) {
                if (currentSize > 0) {
                    // pre-fill with empty strings
                    String[] newValue = new String[currentSize];
                    for (int i = 0; i < currentSize; i++) {
                        newValue[i] = "";
                    }
                    result.put(key, newValue);
                } else {
                    result.put(key, new String[] {});
                }
            }

            // append to existing entry
            result.put(key, appendData(result.get(key), value));
        }

        return result;
    }

    /**
     * Converts a map of complex keys (row labels) to string arrays (containing the values) into a list of strings,
     * which can be used to store the data as CSV.
     *
     * @param oldResult - the old result used to extract the header (oldResult.get(0);)
     * @param values - the values containing all the complex keys and their value sets
     * @param keyLength - the length of the complex key (row key, how many of the first n columns are part of the label)
     * @return - a list of string arrays containing the CSV data of this transformation.
     */
    protected List<String[]> convertMapToList(List<String[]> oldResult, Map<ComplexStringKey, String[]> values, int keyLength) {
        // transform result back into list of string[]
        List<String[]> result = new ArrayList<>();

        // add header
        result.add(oldResult.get(0));

        // add values
        for (Map.Entry<ComplexStringKey, String[]> entry : values.entrySet()) {
            String[] row = Arrays.copyOf(entry.getKey().getContent(), keyLength + entry.getValue().length);
            String[] value = entry.getValue();

            for (int i = 0; i < value.length; i++) {
                row[i + keyLength] = value[i];
            }

            result.add(row);
        }

        return result;
    }



}
