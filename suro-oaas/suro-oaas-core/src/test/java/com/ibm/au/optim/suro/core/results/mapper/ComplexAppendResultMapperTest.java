package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.au.optim.suro.model.entities.mapping.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class ComplexAppendResultMapperTest extends AbstractResultMapperTest {


    /**
     * 
     */
    @Test
    public void testWaitList() throws IOException {
        OutputMapping mapping = mapper.readValue(this.getClass().getResourceAsStream("/append_waitlist_mapping.json"), new TypeReference<OutputMapping>() {
        });

        JsonNode node = mapper.readTree(this.getClass().getResourceAsStream("/solution.json"));

        ComplexAppendResultMapper crm = new ComplexAppendResultMapper();
        List<String[]> result = crm.transformToCsv(node, mapping);

        Assert.assertEquals(127, result.size());
        for (String[] row : result) {
            Assert.assertEquals(59, row.length);
        }
    }


    /**
     * 
     */
    @Test
    public void testValidate() throws IOException {
        OutputMapping mapping = mapper.readValue(this.getClass().getResourceAsStream("/append_waitlist_mapping.json"), new TypeReference<OutputMapping>() {
        });

        ComplexAppendResultMapper crm = new ComplexAppendResultMapper();
        Assert.assertTrue(crm.validate(mapping));
        Assert.assertFalse(crm.validate(null));
        Assert.assertFalse(crm.validate(new OutputMapping()));
        Assert.assertFalse(crm.validate(new OutputMapping("test.json", MappingType.COMPLEX_APPEND)));
        Assert.assertFalse(crm.validate(new OutputMapping("test.csv", MappingType.COMPLEX_APPEND)));

        mapping = new OutputMapping("test.csv", MappingType.COMPLEX_APPEND);
        mapping.setSources(new MappingSource[] {});

        Assert.assertFalse(crm.validate(mapping));

        MappingSource source1 = new MappingSource("");
        mapping.setSources(new MappingSource[] { source1 });
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setSolutionKey("key");
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification());
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setRow(new MappingSpecification(new String[]{}, new String[]{}));
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setRow(new MappingSpecification("label", "key"));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setColumn(new MappingSpecification());
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setColumn(new MappingSpecification(new String[] {}, new String[] {}));
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setColumn(new MappingSpecification("label", "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[0].setRow(null);
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setRow(new MappingSpecification());
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setRow(new MappingSpecification(new String[]{}, new String[]{}));
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[0].setRow(new MappingSpecification("label", "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification(null, "key"));
        mapping.getSources()[0].setColumn(new MappingSpecification(null, "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification((String[]) null, null));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[0].setRow(new MappingSpecification(new String[] {}, new String[] { "key" }));
        Assert.assertTrue(crm.validate(mapping));

        // add a second source
        source1 = mapping.getSources()[0];
        MappingSource source2 = new MappingSource("key");
        source2.setRow(new MappingSpecification(new String[] {}, new String[] { "key" }));
        source2.setColumn(source1.getColumn());
        mapping.setSources(new MappingSource[] { source1, source2 });
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification(new String[] {}, new String[] { "other-key" }));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification(new String[] {}, new String[] { "key", "key" }));
        Assert.assertFalse(crm.validate(mapping));

        // switch to using labels
        mapping.getSources()[0].setRow(new MappingSpecification("label", null));
        mapping.getSources()[1].setRow(new MappingSpecification("label", null));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification(new String[] { "label", "label" }, null));
        Assert.assertFalse(crm.validate(mapping));
        mapping.getSources()[1].setRow(new MappingSpecification(null, "key"));
        Assert.assertFalse(crm.validate(mapping));

        // switch to using keys
        mapping.getSources()[0].setRow(new MappingSpecification(null, "key"));
        mapping.getSources()[1].setRow(new MappingSpecification(null, "key"));
        Assert.assertTrue(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification(null, new String[] { "key", "key" }));
        Assert.assertFalse(crm.validate(mapping));

        mapping.getSources()[1].setRow(new MappingSpecification("label", null));
        Assert.assertFalse(crm.validate(mapping));
    }


    /**
     * 
     */
    @Test
    public void testCompileComplexKeys() {
        ComplexAppendResultMapper rm = new ComplexAppendResultMapper();
        Assert.assertEquals(0, rm.compileMapWithComplexKeys(null, null, new ArrayList<String[]>(), 0).size());


        List<String[]> data = new ArrayList<>();
        data.add(new String[] { "foo", "foo", "bar" });

        Map<ComplexStringKey, String[]> result = rm.compileMapWithComplexKeys(null, new String[] { "foo" }, data, 2);
        Assert.assertEquals(1, result.size());

        String[] dat = result.get(new ComplexStringKey(new String[] { "foo" }));;
        Assert.assertEquals(4, dat.length);

        Assert.assertEquals("", dat[0]);
        Assert.assertEquals("", dat[1]);
        Assert.assertEquals("foo", dat[2]);
        Assert.assertEquals("bar", dat[3]);
    }


    /**
     * 
     */
    @Test
    public void testUnsupportedOperation() throws IOException {
        ComplexAppendResultMapper crm = new ComplexAppendResultMapper();
        Assert.assertNull(crm.transformToJson(mapper.createObjectNode(), new OutputMapping("test.json", MappingType.COMPLEX_APPEND)));
    }

}
