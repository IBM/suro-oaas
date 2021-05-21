package com.ibm.au.optim.suro.core.results;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.core.results.mapper.ComplexAppendResultMapper;
import com.ibm.au.optim.suro.core.results.mapper.ComplexResultMapper;
import com.ibm.au.optim.suro.core.results.mapper.JsonCategoryResultMapper;
import com.ibm.au.optim.suro.core.results.mapper.KeyToColumnResultMapper;
import com.ibm.au.optim.suro.model.control.ResultManager;
import com.ibm.au.optim.suro.model.control.ResultMapper;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.mapping.MappingType;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.util.ProcessResponse;
import com.ibm.au.jaws.data.utils.ReflectionUtils;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the result manager.
 *
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
public class BasicResultManager implements ResultManager {

    private Environment environment;

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicResultManager.class);
    private static final char CSV_DELIMITER = ',';
    
    private static final Map<MappingType, Class<?>> mappings = new HashMap<MappingType, Class<?>>();

    static {
    	
    	mappings.put(MappingType.COMPLEX, ComplexResultMapper.class);
    	mappings.put(MappingType.COMPLEX_APPEND, ComplexAppendResultMapper.class);
    	mappings.put(MappingType.JSON_CATEGORY, JsonCategoryResultMapper.class);
    	mappings.put(MappingType.KEY_TO_COLUMN, KeyToColumnResultMapper.class);
    }
    
    /**
     * Creates a new instance of the result manager. The environment needs to be provided for the manager to be able to
     * access the repositories.
     * @param env - the environment storing the repositories
     */
    public BasicResultManager(Environment env) {
        this.environment = env;
    }



    @Override
    public ProcessResponse storeResults(Run run, JsonNode resultNode) throws IOException {
        // invalid parameters
        if (run == null || resultNode == null || run.getModelId() == null) {
            return new ProcessResponse(false);
        }

        Model model = getModelRepository().getItem(run.getModelId());
        if (model == null || model.getOutputMappings() == null || model.getOutputMappings().isEmpty()) {
            return new ProcessResponse(false);
        }

        List<String> failedOutput = new ArrayList<>();
        List<String> successOutput = new ArrayList<>();
        for (OutputMapping mapping : model.getOutputMappings()) {
            try {
                boolean res = processMapping(run, resultNode, mapping);
                if (res) {
                    successOutput.add(mapping.getFileName());
                    continue;
                }
            }
            catch (IOException e) {
                LOGGER.error("IOException during result processing for mapping " + mapping.getFileName(), e);
            }
            failedOutput.add(mapping.getFileName());
        }


        if (failedOutput.isEmpty()) {
            return new ProcessResponse(true);
        } else {
            String join = Arrays.toString(failedOutput.toArray());
            return new ProcessResponse(true, join);
        }
    }

    @Override
    public ProcessResponse storeResults(Run run, InputStream resultStream) throws IOException {
        if (resultStream == null) {
            return new ProcessResponse(false);
        }

        JsonNode node = new ObjectMapper().readTree(resultStream);
        return storeResults(run, node);

    }

    @Override
    public ModelRepository getModelRepository() {
        return (ModelRepository) environment.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }

    @Override
    public RunRepository getRunRepository() {
        return (RunRepository) environment.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
    }


    protected boolean processMapping(Run run, JsonNode data, OutputMapping mapping) throws IOException {
        if (run == null || data == null || mapping == null) {
            return false;
        }

        ResultMapper mapper = this.getMapperInstance(mapping);
        if (mapper == null) {
            throw new IOException("Could not find mapper for " + mapping.getFileName() + " (" + mapping.getMappingType() + ")");
        }

        if (!mapper.validate(mapping)) {
            throw new IOException("Output mapping does not comply with the specified mapping type.");
        }

        if (mapping.getFileName().toLowerCase().endsWith(".json")) {
            JsonNode transformedResult = mapper.transformToJson(data, mapping);
            return storeJsonResult(run, mapping.getFileName(), transformedResult);

        } else if (mapping.getFileName().toLowerCase().endsWith(".csv")) {
            List<String[]> transformedResult = mapper.transformToCsv(data, mapping);
            return storeCsvResult(run, mapping.getFileName(), transformedResult);

        } else {
            throw new IOException("Unsupported file extension.");
        }
    }

    protected boolean storeJsonResult(Run run, String fileName, JsonNode jsonResult) throws IOException {
        if (run == null || fileName == null || jsonResult == null) {
            return false;
        }
        getRunRepository().attach(run, fileName, ContentType.APPLICATION_JSON.toString(), new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(jsonResult)));
        return true;
    }

    protected ResultMapper getMapperInstance(OutputMapping mapping) {
    	
    	ResultMapper mapper = null;
    	
        if (mapping != null) {
        
        	MappingType type = mapping.getMappingType();
        	
        	if (type != MappingType.TRANSFORMER) {
        	
        		Class<?> mapperType = BasicResultManager.mappings.get(type);
        		if (mapperType != null) {
        			
        			mapper =  (ResultMapper) ReflectionUtils.createInstance(mapperType);
        		}
        		
        	} else {
        		
        		String className = this.environment.getParameter(ResultManager.TRANSFORMER_PREFIX + mapping.getTransformer(), null);
                if (className != null) {
                   mapper = (ResultMapper) ReflectionUtils.createInstance(className);
                }
        	}
        }
        
        return mapper;

    }

    protected boolean storeCsvResult(Run run, String fileName, List<String[]> csvResult) {
        if (run == null || fileName == null || csvResult == null) {
            return false;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // write data to output stream
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        boolean first = true;
        for (String[] row : csvResult) {
            if (first) {
                first = false;
            } else {
                pw.println();
            }
            arrayToDelimitedString(row, pw);
        }
        pw.close();

        // store as attachment
        getRunRepository().attach(run, fileName, "text/csv", new ByteArrayInputStream(out.toByteArray()));

        return true;
    }


    protected void arrayToDelimitedString(String[] values, PrintWriter pw) {
        boolean first = true;
        for (String s : values) {
            if (!first) {
                pw.print(CSV_DELIMITER);
            }
            pw.print(s);
            first = false;
        }
    }
}
