package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.mapping.*;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class JsonCategoryResultMapperTest extends AbstractResultMapperTest {


    /**
     * 
     */
    @Test
    public void testSurgeryTypes() throws IOException {
    	
        OutputMapping mapping = this.mapper.readValue(this.getClass().getResourceAsStream("/json_schedule-surgery_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonNode node = this.mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        JsonCategoryResultMapper crm = new JsonCategoryResultMapper();
        JsonNode result = crm.transformToJson(node, mapping);
        Assert.assertNotNull(result);

        Assert.assertEquals(126, result.size());
        Assert.assertEquals(5, result.get(result.fieldNames().next()).size());
    }



    /**
     * 
     */
    @Test
    public void testValidate() throws IOException {
    	
        OutputMapping mapping = this.mapper.readValue(this.getClass().getResourceAsStream("/json_schedule-surgery_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonCategoryResultMapper crm = new JsonCategoryResultMapper();
        Assert.assertTrue(crm.validate(mapping));

        mapping.setFileName("test.csv");
        Assert.assertFalse(crm.validate(mapping));

        mapping.setFileName("test.json");
        Assert.assertTrue(crm.validate(mapping));

        mapping.setSources(null);
        Assert.assertFalse(crm.validate(mapping));
        mapping.setSources(new MappingSource[] {});
        Assert.assertFalse(crm.validate(mapping));

        MappingSource source1 = new MappingSource("key");
        source1.setRow(new MappingSpecification(null, "key"));
        source1.setValue(new ValueMapping("key"));
        mapping.setSources(new MappingSource[] { source1 });
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[0].setColumn(new MappingSpecification("label", "key"));
        Assert.assertTrue(crm.validate(mapping));
        mapping.getSources()[0].setColumn(null);

        mapping.getSources()[0].setValue(null);
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setValue(new ValueMapping((String) null));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setValue(new ValueMapping(new String[] { }));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setValue(new ValueMapping(new String[] { "key "}));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[0].setRow(null);
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification(null, (String) null));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification(new String[] {}, new String[] {}));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification("label", "key"));
        Assert.assertTrue(crm.validate(mapping));

    }


    /**
     * 
     */
    @Test
    public void testUnsupportedOperation() throws IOException {
        JsonCategoryResultMapper crm = new JsonCategoryResultMapper();
        Assert.assertNull(crm.transformToCsv(this.mapper.createObjectNode(), new OutputMapping("test.csv", MappingType.JSON_CATEGORY)));
    }
}
