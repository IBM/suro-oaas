package com.ibm.au.optim.suro.core.results.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSpecification;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;
import com.ibm.au.optim.suro.model.entities.mapping.ValueMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class AbstractJsonMapperTest extends AbstractResultMapperTest {

	/**
	 * 
	 */
    private String key = "foo";
    /**
     * 
     */
    private String value = "bar";
    /**
     * 
     */
    private String label = "foobar";
    /**
     * 
     */
    private String column = "column";


    /**
     * 
     */
    @Test
    public void testExtractStringValue() {
        AbstractJsonMapper rm = new ComplexResultMapper();
        ObjectNode node = this.mapper.createObjectNode();

        Assert.assertNull(rm.extractStringValue(null));
        Assert.assertNull(rm.extractStringValue(node.get("not-existing")));

        node.put(this.key, this.value);
        Assert.assertEquals(value, rm.extractStringValue(node.get(this.key)));

        node.put(this.key, 1);
        Assert.assertEquals("1", rm.extractStringValue(node.get(this.key)));

        node.put(this.key, 1.337);
        Assert.assertEquals("1.337", rm.extractStringValue(node.get(this.key)));

        node.put(this.key, 1.00001);
        Assert.assertEquals("1", rm.extractStringValue(node.get(this.key)));

        node.put(this.key, 2.99999971);
        Assert.assertEquals("3", rm.extractStringValue(node.get(this.key)));

        ArrayNode array = this.mapper.createArrayNode();
        array.add(node);
        array.add(node);
        Assert.assertNull(rm.extractStringValue(array));

        node.put(this.key, new BigDecimal(5000));
        Assert.assertEquals("", rm.extractStringValue(node.get(this.key)));

        Assert.assertNull(rm.extractStringValue(node));
    }


    /**
     * 
     */
    @Test
    public void testSingleKeyRowExtraction() {
        // prepare data
        ObjectNode node1 = this.mapper.createObjectNode();
        ObjectNode node2 = this.mapper.createObjectNode();

        node1.put(this.key, this.value);
        node1.put(this.column, "1");
        node2.put(this.key, this.label);
        node2.put(this.column, "2");

        MappingSource source = new MappingSource("");
        // value null, key null
        source.setColumn(new MappingSpecification(this.label, this.column));
        source.setRow(new MappingSpecification(this.label, null));

        List<JsonNode> dataList = new ArrayList<>();
        dataList.add(node1);
        dataList.add(node2);
        
        ComplexResultMapper cm = new ComplexResultMapper();

        // no row key, means empty strings
        String[] result = cm.extractSingleKeyRows(source, dataList.iterator(), new String[]{"1", "2"});
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(this.label, result[0]);
        Assert.assertEquals("", result[1]);
        Assert.assertEquals("", result[2]);

        // set key for the row
        source.setRow(new MappingSpecification(this.label, this.key));
        cm = new ComplexResultMapper();
        result = cm.extractSingleKeyRows(source, dataList.iterator(), new String[]{"1", "2"});
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(this.label, result[0]);
        Assert.assertEquals(this.value, result[1]);
        Assert.assertEquals(this.label, result[2]);

        // set value
        source.setValue(new ValueMapping(this.column));
        cm = new ComplexResultMapper();
        result = cm.extractSingleKeyRows(source, dataList.iterator(), new String[]{"1", "2"});
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(this.label, result[0]);
        Assert.assertEquals("1", result[1]);
        Assert.assertEquals("2", result[2]);

        // provide no data
        List<JsonNode> emptyList = new ArrayList<>();
        cm = new ComplexResultMapper();
        result = cm.extractSingleKeyRows(source, emptyList.iterator(), new String[]{"1", "2"});
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(this.label, result[0]);
        Assert.assertNull(result[1]);
        Assert.assertNull(result[2]);

        // set a not matching column
        node1.put(column, "3");
        cm = new ComplexResultMapper();
        result = cm.extractSingleKeyRows(source, dataList.iterator(), new String[]{"1", "2"});
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(this.label, result[0]);
        Assert.assertNull(result[1]);
        Assert.assertEquals("2", result[2]);

        // remove column field
        node2.remove(this.column);
        result = new ComplexResultMapper().extractSingleKeyRows(source, dataList.iterator(), new String[]{"1", "2"});
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(this.label, result[0]);
        Assert.assertNull(result[1]);
        Assert.assertNull(result[2]);
    }



    /**
     * 
     */
    @Test
    public void testRowExtractionWithEmptyRowKey() {
    	
        MappingSource source = new MappingSource("");
        AbstractJsonMapper rm = new ComplexResultMapper();
        // null entry key array, returns no results
        source.setRow(new MappingSpecification(null, (String) null));
        List<String[]> result = rm.extractRows(new ArrayList<JsonNode>().iterator(), source, new String[]{"1", "2"});
        Assert.assertEquals(0, result.size());

        // empty entry key array, returns no results
        source.getRow().setEntryKeys(new String[]{});
        result = rm.extractRows(new ArrayList<JsonNode>().iterator(), source, new String[]{"1", "2"});
        Assert.assertEquals(0, result.size());
    }


    /**
     * 
     */
    @Test
    public void testColumnExtractionWithEmptyColumnKey() {
        MappingSource source = new MappingSource("");
        AbstractJsonMapper rm = new ComplexResultMapper();

        ArrayNode data = this.mapper.createArrayNode();
        data.add(this.mapper.createObjectNode());
        data.add(this.mapper.createObjectNode());

        // no column spec
        String[] result = rm.extractColumns(new MappingSource[]{source}, data);
        Assert.assertEquals(0, result.length);

        // null entry key array, returns no results
        source.setColumn(new MappingSpecification(null, (String) null));
        result = rm.extractColumns(new MappingSource[]{source}, data);
        Assert.assertEquals(0, result.length);

        // empty entry key array, returns no results
        source.getColumn().setEntryKeys(new String[]{});
        result = rm.extractColumns(new MappingSource[]{source}, data);
        Assert.assertEquals(0, result.length);

        source.getColumn().setEntryKeys(new String[]{"foo", "bar"});
        result = rm.extractColumns(new MappingSource[]{source}, data);
        Assert.assertEquals(0, result.length);
    }


    /**
     * 
     */
    @Test
    public void testRowCreation() {
        AbstractJsonMapper rm = new ComplexResultMapper();

        Assert.assertTrue(Arrays.deepEquals(new String[]{}, rm.createRow(null, null)));

        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo"}, rm.createRow(null, new String[]{"foo"})));
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo"}, rm.createRow(new String[]{"foo"}, null)));

        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo", "bar", "foobar"}, rm.createRow(new String[]{"foo", "bar"}, new String[]{"foobar"})));
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo", "bar"}, rm.createRow(new String[]{}, new String[]{"foo", "bar"})));
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo", "bar"}, rm.createRow(new String[]{"foo", "bar"}, new String[]{})));
    }

    /**
     * 
     */
    @Test
    public void testHeaderCreation() {
        AbstractJsonMapper rm = new ComplexResultMapper();

        OutputMapping mapping = new OutputMapping();
        MappingSource source = new MappingSource("");
        String[] columns = new String[]{"1", "2"};
        mapping.setSources(new MappingSource[]{source});

        Assert.assertTrue(Arrays.deepEquals(new String[]{"1", "2"}, rm.createHeader(mapping, columns)));

        MappingSpecification row = new MappingSpecification();
        source.setRow(row);
        mapping.getSources()[0].setRow(row);

        Assert.assertTrue(Arrays.deepEquals(new String[]{"1", "2"}, rm.createHeader(mapping, columns)));
        Assert.assertTrue(Arrays.deepEquals(new String[]{}, rm.createHeader(mapping, new String[]{})));

        row.setLabels(new String[]{});
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{"1", "2"}, rm.createHeader(mapping, columns)));
        Assert.assertTrue(Arrays.deepEquals(new String[]{}, rm.createHeader(mapping, new String[]{})));

        row.setLabels(new String[]{"foo"});
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{"Item", "1", "2"}, rm.createHeader(mapping, columns)));

        row.setLabels(null);
        row.setEntryKeys(new String[]{});
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{}, rm.createHeader(mapping, new String[]{})));

        row.setLabels(null);
        row.setEntryKeys(new String[]{"foobar"});
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{"Item"}, rm.createHeader(mapping, new String[]{})));

        row.setLabels(new String[]{"foo", "bar"});
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{"Item"}, rm.createHeader(mapping, new String[]{})));
        Assert.assertTrue(Arrays.deepEquals(new String[]{"Item", "1", "2"}, rm.createHeader(mapping, columns)));

        row.setEntryKeys(null);
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo", "bar"}, rm.createHeader(mapping, new String[]{})));
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foo", "bar", "1", "2"}, rm.createHeader(mapping, columns)));

        row.setLabels(null);
        mapping.getSources()[0].setRow(row);
        Assert.assertTrue(Arrays.deepEquals(new String[]{}, rm.createHeader(mapping, new String[]{})));
        Assert.assertTrue(Arrays.deepEquals(new String[]{"1", "2"}, rm.createHeader(mapping, columns)));

        row.setLabels(new String[] { "foobar", "bar" });
        MappingSource source2 = new MappingSource("");
        source2.setRow(row);
        mapping.setSources(new MappingSource[] { source, source2 });
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foobar", "bar", "1", "2"}, rm.createHeader(mapping, columns)));
        Assert.assertTrue(Arrays.deepEquals(new String[]{ "foobar", "bar" }, rm.createHeader(mapping, new String[] {})));

        mapping.setSources(new MappingSource[] { source2, source });
        Assert.assertTrue(Arrays.deepEquals(new String[]{"foobar", "bar", "1", "2"}, rm.createHeader(mapping, columns)));
        Assert.assertTrue(Arrays.deepEquals(new String[]{ "foobar", "bar" }, rm.createHeader(mapping, new String[] {})));

        source2.setRow(new MappingSpecification("foobar", null));

        mapping.setSources(new MappingSource[] { source2, source });
        Assert.assertTrue(Arrays.deepEquals(new String[]{"Item", "1", "2"}, rm.createHeader(mapping, columns)));
        Assert.assertTrue(Arrays.deepEquals(new String[]{ "Item" }, rm.createHeader(mapping, new String[] {})));

        mapping.setSources(new MappingSource[] { source, source2 });
        Assert.assertTrue(Arrays.deepEquals(new String[]{"Item", "1", "2"}, rm.createHeader(mapping, columns)));
        Assert.assertTrue(Arrays.deepEquals(new String[]{ "Item" }, rm.createHeader(mapping, new String[] {})));
    }
}
