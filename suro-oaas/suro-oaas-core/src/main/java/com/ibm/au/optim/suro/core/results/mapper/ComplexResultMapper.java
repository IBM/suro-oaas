package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.control.ResultMapper;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the default result mapper used for most output sources. It will take a list of sources (solutionKeys) and
 * combine them into a list of rows with a single label and x values for each column (e.g. for each period)
 *
 * @author Peter Ilfrich
 */
public class ComplexResultMapper extends AbstractJsonMapper implements ResultMapper {


    /**
     * A logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexResultMapper.class);

    @Override
    public List<String[]> transformToCsv(JsonNode node, OutputMapping mapping) throws IOException {

        MappingSource[] sources = mapping.getSources();

        List<String[]> result = new ArrayList<>();
        String[] columns = extractColumns(sources, node);

        result.add(createHeader(mapping, columns));

        for(MappingSource source : sources) {
            JsonNode solutionPart = node.get(source.getSolutionKey());

            if (solutionPart.isArray()) {
                result.addAll(this.extractRows(solutionPart.iterator(), source, columns));
            } else {
                // add differently structured data (TODO: check if such data exists)
            }
        }

        return result;
    }

    @Override
    public JsonNode transformToJson(JsonNode node, OutputMapping mapping) throws IOException {
        return null;
    }

    @Override
    public boolean validate(OutputMapping mapping) {
        boolean baseValidation = validate(mapping, new String[] { "csv" });
        if (!baseValidation) {
            return false;
        }

        int columnSize = -1;
        int rowSize = -1;
        for (MappingSource source : mapping.getSources()) {
            // first element
            if (columnSize == -1) {
                columnSize = getMappingSpecificationMaxSize(source.getColumn());
                rowSize = getMappingSpecificationMaxSize(source.getRow());
            }

            int currentColumnSize = getMappingSpecificationMaxSize(source.getColumn());
            int currentRowSize = getMappingSpecificationMaxSize(source.getRow());

            // no rows or columns specified
            if (currentColumnSize == 0 && currentRowSize == 0) {
                LOGGER.error("Output mapping contains source without row or column mapping " +
                "[ fileName: '" + mapping.getFileName() + "', solutionKey: '" +  source.getSolutionKey() + "' ]");
                return false;
            }

            if (currentColumnSize != columnSize || currentRowSize != rowSize) {
                LOGGER.error(new StringBuilder().append("Output mapping contains source with mismatching row or column mapping size ")
                .append("[ fileName: '").append(mapping.getFileName()).append("', solutionKey: '").append(source.getSolutionKey())
                .append("', columns: [ expected: ").append(columnSize).append(", is: ").append(currentColumnSize).append(" ], rows: [ expected: ")
                .append(rowSize).append(", is: ").append(currentRowSize).append(" ]]").toString());
                return false;
            }
        }

        return true;
    }


}
