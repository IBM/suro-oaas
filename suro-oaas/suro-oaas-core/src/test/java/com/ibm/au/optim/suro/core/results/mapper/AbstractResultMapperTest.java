package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Iterator;
import java.util.List;

/**
 * @author Peter Ilfrich
 */
public abstract class AbstractResultMapperTest  {

	/**
	 * 
	 */
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * 
     * @param result
     */
    protected void debugPrintResult(List<String[]> result) {
        for (String[] res : result) {
            for (String val : res) {
                System.out.print(", " + val);
            }
            System.out.println();
        }
    }
    
    /**
     * 
     * @param result
     */
    protected void debugPrintResult(JsonNode result) {
        if (result.isArray()) {
            Iterator<JsonNode> nodes = result.elements();
            while (nodes.hasNext()) {
                System.out.println("------next node------");
                debugPrintResultEntry(nodes.next());
            }
        }
        else {
            debugPrintResultEntry(result);
        }
    }

    /**
     * 
     * @param objectNode
     */
    private void debugPrintResultEntry(JsonNode objectNode) {
        Iterator<String> fields = objectNode.fieldNames();
        while (fields.hasNext()) {
            String fieldName = fields.next();
            JsonNode value = objectNode.get(fieldName);
            System.out.println(fieldName + " : " + value);
        }
    }
}
