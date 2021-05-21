package com.ibm.au.optim.suro.core.results.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.core.results.mapper.AbstractResultMapperTest;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.MappingType;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class ScheduleTransformerTest extends AbstractResultMapperTest {
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testSchedule() throws IOException {

        JsonNode node = this.mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        ScheduleTransformer crm = new ScheduleTransformer();
        JsonNode result = crm.transformToJson(node, null);

        Assert.assertEquals(4, result.size());
    }
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testValidate() throws IOException {
        ScheduleTransformer crm = new ScheduleTransformer();
        Assert.assertFalse(crm.validate(null));
        Assert.assertFalse(crm.validate(new OutputMapping()));
        OutputMapping mapping = new OutputMapping("test.json", MappingType.TRANSFORMER);
        mapping.setSources(new MappingSource[] { new MappingSource("key") });
        Assert.assertTrue(crm.validate(mapping));
        Assert.assertFalse(crm.validate(new OutputMapping("test.csv", MappingType.TRANSFORMER)));
    }

    /**
     * 
     * @throws IOException
     */
    @Test
    public void testUnsupportedOperation() throws IOException {
        ScheduleTransformer transformer = new ScheduleTransformer();
        Assert.assertNull(transformer.transformToCsv(this.mapper.createObjectNode(), new OutputMapping("test.csv", MappingType.TRANSFORMER)));
    }
}
