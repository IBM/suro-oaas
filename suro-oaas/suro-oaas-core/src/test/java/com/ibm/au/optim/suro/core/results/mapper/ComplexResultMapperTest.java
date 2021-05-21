package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSpecification;
import com.ibm.au.optim.suro.model.entities.mapping.MappingType;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class ComplexResultMapperTest extends AbstractResultMapperTest {



    /**
     * 
     */
    @Test
    public void testValidate() throws IOException {
        ComplexResultMapper crm = new ComplexResultMapper();

        OutputMapping mapping = mapper.readValue(this.getClass().getResourceAsStream("/beds_mapping.json"), new TypeReference<OutputMapping>() {
        });
        Assert.assertTrue(crm.validate(mapping));

        mapping = mapper.readValue(this.getClass().getResourceAsStream("/patients_mapping.json"), new TypeReference<OutputMapping>() {
        });
        Assert.assertTrue(crm.validate(mapping));

        mapping = mapper.readValue(this.getClass().getResourceAsStream("/surgeries_mapping.json"), new TypeReference<OutputMapping>() {
        });
        Assert.assertTrue(crm.validate(mapping));


        mapping = new OutputMapping("file.json", MappingType.COMPLEX);

        MappingSource source1 = new MappingSource("key");
        source1.setColumn(new MappingSpecification("label", "key"));
        mapping.setSources(new MappingSource[] { source1 });

        Assert.assertFalse(crm.validate(mapping));

        mapping.setFileName("file.csv");
        Assert.assertTrue(crm.validate(mapping));

        MappingSource source2 = new MappingSource("key");
        source2.setColumn(new MappingSpecification("label", "key"));
        mapping.setSources(new MappingSource[] { source1, source2 });

        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setColumn(new MappingSpecification(null, "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setColumn(new MappingSpecification("label", null));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setColumn(new MappingSpecification((String) null, null));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[1].setColumn(new MappingSpecification(new String[] { "label", "label" }, new String[] { "key", "key" }));
        Assert.assertFalse(crm.validate(mapping));

        // switch to rows
        mapping.getSources()[0].setColumn(null);
        mapping.getSources()[1].setColumn(null);

        mapping.getSources()[0].setRow(new MappingSpecification("label", "key"));
        mapping.getSources()[1].setRow(new MappingSpecification("label", "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification(null, "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification("label", null));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification((String) null, null));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification(new String[]{"label", "label"}, new String[]{"key", "key"}));
        Assert.assertFalse(crm.validate(mapping));
    }


    /**
     * 
     */
    @Test
    public void testBeds() throws IOException {

        OutputMapping mapping = mapper.readValue(this.getClass().getResourceAsStream("/beds_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonNode node = mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        ComplexResultMapper crm = new ComplexResultMapper();
        List<String[]> result = crm.transformToCsv(node, mapping);

        Assert.assertEquals(14, result.size());
        for (String[] row : result) {
            Assert.assertEquals(29, row.length);
        }
    }



    /**
     * 
     */
    @Test
    public void testPatients() throws IOException {
        OutputMapping mapping = mapper.readValue(this.getClass().getResourceAsStream("/patients_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonNode node = mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        ComplexResultMapper crm = new ComplexResultMapper();
        List<String[]> result = crm.transformToCsv(node, mapping);

        // no of rows
        Assert.assertEquals(9, result.size());
        for (String[] row : result) {
            Assert.assertEquals(29, row.length);
        }
    }


    /**
     * 
     */
    @Test
    public void testSurgeries() throws IOException {
        OutputMapping mapping = mapper.readValue(this.getClass().getResourceAsStream("/surgeries_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonNode node = mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        ComplexResultMapper crm = new ComplexResultMapper();
        List<String[]> result = crm.transformToCsv(node, mapping);

        // no of rows
        Assert.assertEquals(127, result.size());
        for (String[] row : result) {
            Assert.assertEquals(30, row.length);
        }

    }



    /**
     * 
     */
    @Test
    public void testUnsupportedOperation() throws IOException {
        ComplexResultMapper crm = new ComplexResultMapper();
        Assert.assertNull(crm.transformToJson(mapper.createObjectNode(), new OutputMapping("test.json", MappingType.COMPLEX)));
    }



}
