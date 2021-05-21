package com.ibm.au.optim.suro.core.migration.preparer.v005;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.entities.Template;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

import java.util.List;

/**
 * @author Peter Ilfrich
 */
public class DataModelPreparerTest extends TestCase {


    public void testResourceLoad() throws Exception {
        List<Template> templates = new ObjectMapper().readValue(this.getClass().getResourceAsStream("/migration/0.0.5/package/templates.json"), new TypeReference<List<Template>>() {
        });
        assertFalse(templates.isEmpty());
    }


}
