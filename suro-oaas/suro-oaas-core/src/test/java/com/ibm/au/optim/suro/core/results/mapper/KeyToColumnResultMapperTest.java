package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.mapping.*;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class KeyToColumnResultMapperTest extends AbstractResultMapperTest {




    /**
     * 
     */
    @Test
    public void testTargets() throws IOException {
        OutputMapping mapping = this.mapper.readValue(this.getClass().getResourceAsStream("/k2c_targets_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonNode node = this.mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        KeyToColumnResultMapper crm = new KeyToColumnResultMapper();
        List<String[]> result = crm.transformToCsv(node, mapping);

        Assert.assertEquals(13, result.size());
        for (String[] row : result) {
            Assert.assertEquals(4, row.length);
        }
    }


    /**
     * 
     */
    @Test
    public void testValidate() throws IOException {
        OutputMapping mapping = this.mapper.readValue(this.getClass().getResourceAsStream("/k2c_targets_mapping.json"), new TypeReference<OutputMapping>() {
        });

        KeyToColumnResultMapper ktcMapper = new KeyToColumnResultMapper();
        Assert.assertTrue(ktcMapper.validate(mapping));


        mapping = new OutputMapping(null, MappingType.KEY_TO_COLUMN);
        Assert.assertFalse(ktcMapper.validate(mapping));

        mapping.setFileName("test.json");
        Assert.assertFalse(ktcMapper.validate(mapping));

        mapping.setSources(new MappingSource[] { new MappingSource("key") });
        Assert.assertFalse(ktcMapper.validate(mapping));

        mapping.setFileName("test.csv");
        Assert.assertTrue(ktcMapper.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification("label", "key"));
        Assert.assertTrue(ktcMapper.validate(mapping));

        mapping.getSources()[0].setColumn(new MappingSpecification("label", "key"));
        Assert.assertTrue(ktcMapper.validate(mapping));

        mapping.getSources()[0].setValue(new ValueMapping(new String[] { "label" }));
        Assert.assertTrue(ktcMapper.validate(mapping));
    }


    /**
     * 
     */
    @Test
    public void testUnsupportedOperation() throws IOException {
        KeyToColumnResultMapper crm = new KeyToColumnResultMapper();
        Assert.assertNull(crm.transformToJson(this.mapper.createObjectNode(), new OutputMapping("test.json", MappingType.KEY_TO_COLUMN)));
    }
}
