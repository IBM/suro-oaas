package com.ibm.au.optim.suro.core.results;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.results.mapper.ComplexAppendResultMapper;
import com.ibm.au.optim.suro.core.results.mapper.ComplexResultMapper;
import com.ibm.au.optim.suro.core.results.mapper.JsonCategoryResultMapper;
import com.ibm.au.optim.suro.core.results.mapper.KeyToColumnResultMapper;
import com.ibm.au.optim.suro.core.results.transformer.ScheduleTransformer;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.mapping.*;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientRunRepository;
import com.ibm.au.optim.suro.util.ProcessResponse;
import com.ibm.au.jaws.web.core.runtime.Environment;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.Assert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Peter Ilfrich
 */
public class BasicResultManagerTest {

	/**
	 * 
	 */
    ObjectMapper mapper = new ObjectMapper();

    /**
     * 
     */
    private String csvFile = "file.csv";
    /**
     * 
     */
    private String jsonFile = "file.json";

    /**
     * 
     */
    private String solutionKey1 = "key1";
    /**
     * 
     */
    private String solutionKey2 = "key2";

    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testRepositories() {
        Environment env = setupEnvironment(null);

        BasicResultManager manager = new BasicResultManager(env);

        Assert.assertEquals(0, manager.getModelRepository().getAll().size());
        Assert.assertEquals(0, manager.getRunRepository().getAll().size());
    }
    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testGetMapperInstance() {
        Environment env = EnvironmentHelper.mockEnvironment(new Properties());

        BasicResultManager rm = new BasicResultManager(env);
        Assert.assertNull(rm.getMapperInstance(null));
        Assert.assertNull(rm.getMapperInstance(new OutputMapping("file.json", MappingType.TRANSFORMER)));
        Assert.assertNull(rm.getMapperInstance(new OutputMapping("file.json", null)));

        Assert.assertTrue(rm.getMapperInstance(new OutputMapping("file.json", MappingType.COMPLEX)) instanceof ComplexResultMapper);
        Assert.assertFalse(rm.getMapperInstance(new OutputMapping("file.json", MappingType.COMPLEX)) instanceof ComplexAppendResultMapper);

        Assert.assertTrue(rm.getMapperInstance(new OutputMapping("file.json", MappingType.COMPLEX_APPEND)) instanceof ComplexAppendResultMapper);
        Assert.assertTrue(rm.getMapperInstance(new OutputMapping("file.json", MappingType.JSON_CATEGORY)) instanceof JsonCategoryResultMapper);
        Assert.assertTrue(rm.getMapperInstance(new OutputMapping("file.json", MappingType.KEY_TO_COLUMN)) instanceof KeyToColumnResultMapper);

        // test reflection
        Properties props = new Properties();
        props.setProperty("resultmapper.transformer.ScheduleTransformer", ScheduleTransformer.class.getName());
        rm = new BasicResultManager(EnvironmentHelper.mockEnvironment(props));

        OutputMapping mapping = new OutputMapping("file.json", MappingType.TRANSFORMER);
        mapping.setTransformer("ScheduleTransformer");
        Assert.assertTrue(rm.getMapperInstance(mapping) instanceof ScheduleTransformer);
    }
    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testArrayToDelimitedString() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

        BasicResultManager manager = new BasicResultManager(null);

        manager.arrayToDelimitedString(new String[] { "foo", "bar" }, pw);
        pw.close();
        Assert.assertEquals("foo,bar", outputStreamToString(out));
    }

    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testStoreCsvResult() throws IOException {
        BasicResultManager manager = new BasicResultManager(setupEnvironment(null));

        Assert.assertFalse(manager.storeCsvResult(null, null, null));

        Run run = new Run();
        manager.getRunRepository().addItem(run);
        Assert.assertFalse(manager.storeCsvResult(run, null, null));

        String fileName = "file.csv";
        Assert.assertFalse(manager.storeCsvResult(run, fileName, null));

        List<String[]> result = new ArrayList<>();

        Assert.assertTrue(manager.storeCsvResult(run, fileName, result));
        Assert.assertEquals("", IOUtils.toString(manager.getRunRepository().getAttachment(run.getId(), fileName)));

        result.add(new String[] { "1", "2" });
        result.add(new String[] { "foo", "bar" });

        String newFileName = "file2.csv";
        Assert.assertTrue(manager.storeCsvResult(run, newFileName, result));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        pw.println("1,2");
        pw.print("foo,bar");
        pw.close();
        String expectedResult = outputStreamToString(out);

        Assert.assertEquals(expectedResult, IOUtils.toString(manager.getRunRepository().getAttachment(run.getId(), newFileName)));
    }

    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testStoreJsonResult() throws IOException {
        BasicResultManager manager = new BasicResultManager(setupEnvironment(null));

        Assert.assertFalse(manager.storeJsonResult(null, null, null));

        Run run = new Run();
        manager.getRunRepository().addItem(run);
        Assert.assertFalse(manager.storeJsonResult(run, null, null));

        String fileName = "file.json";
        Assert.assertFalse(manager.storeJsonResult(run, fileName, null));

        ObjectNode node = this.mapper.createObjectNode();
        node.put("foo", "bar");

        Assert.assertTrue(manager.storeJsonResult(run, fileName, node));

        JsonNode result = this.mapper.readTree(manager.getRunRepository().getAttachment(run.getId(), fileName));
        Assert.assertEquals("bar", result.get("foo").textValue());
    }

    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testProcessMappingCsv() throws IOException {
        BasicResultManager manager = new BasicResultManager(setupEnvironment(null));

        OutputMapping mapping = new OutputMapping(csvFile, MappingType.KEY_TO_COLUMN);
        mapping.setSources(new MappingSource[] { new MappingSource(this.solutionKey1) });

        Run run = new Run();
        manager.getRunRepository().addItem(run);

        ObjectNode data = this.mapper.createObjectNode();

        ArrayNode array = this.mapper.createArrayNode();
        ObjectNode entry1 = this.mapper.createObjectNode();
        entry1.put("foo", "bar");
        ObjectNode entry2 = this.mapper.createObjectNode();
        entry2.put("foo", "foobar");

        array.add(entry1);
        array.add(entry2);
        data.set(this.solutionKey1, array);

        manager.processMapping(run, data, mapping);

        String output = IOUtils.toString(manager.getRunRepository().getAttachment(run.getId(), this.csvFile));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        pw.println("foo");
        pw.println("bar");
        pw.print("foobar");
        pw.close();
        String expectedResult = outputStreamToString(out);

        Assert.assertEquals(expectedResult, output);
    }
    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testProcessMappingJson() throws IOException {
        // prepare output mapping (see schedule_surgery_types)
        OutputMapping mapping = new OutputMapping(this.jsonFile, MappingType.JSON_CATEGORY);
        MappingSource source = new MappingSource(this.solutionKey1);
        String categoryKey = "category";
        source.setRow(new MappingSpecification(null, categoryKey));
        source.setValue(new ValueMapping(new String[] { "foo", "bar" }));
        mapping.setSources(new MappingSource[] { source });

        // prepare json data
        ArrayNode data = this.mapper.createArrayNode();
        ObjectNode entry1 = this.mapper.createObjectNode();
        ObjectNode entry2 = this.mapper.createObjectNode();

        entry1.put(categoryKey, "foo");
        entry2.put(categoryKey, "bar");

        entry1.put("foo", 1);
        entry1.put("bar", 2);
        entry2.put("foo", 3);
        entry2.put("bar", 4);

        data.add(entry1);
        data.add(entry2);

        ObjectNode solution = this.mapper.createObjectNode();
        solution.set(this.solutionKey1, data);

        // prepare environment
        BasicResultManager manager = new BasicResultManager(setupEnvironment(null));
        Run run = new Run();
        manager.getRunRepository().addItem(run);

        // action
        Assert.assertTrue(manager.processMapping(run, solution, mapping));

        // compare result
        JsonNode node = this.mapper.readTree(manager.getRunRepository().getAttachment(run.getId(), this.jsonFile));
        Assert.assertEquals(1, node.get("foo").get("foo").intValue());
        Assert.assertEquals(2, node.get("foo").get("bar").intValue());
        Assert.assertEquals(3, node.get("bar").get("foo").intValue());
        Assert.assertEquals(4, node.get("bar").get("bar").intValue());
    }
    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testProcessMappingFlow() throws IOException {
        BasicResultManager manager = new BasicResultManager(setupEnvironment(null));
        Assert.assertFalse(manager.processMapping(null, null, null));

        ObjectNode data = this.mapper.createObjectNode();

        Run run = new Run();
        manager.getRunRepository().addItem(run);
        Assert.assertFalse(manager.processMapping(run, null, null));
        Assert.assertFalse(manager.processMapping(run, data, null));

        boolean exceptionOccurred = false;
        try {
            manager.processMapping(run, data, new OutputMapping("test.na", MappingType.KEY_TO_COLUMN));
        }
        catch (IOException ioe) {
            exceptionOccurred = true;
        }
        Assert.assertTrue(exceptionOccurred);

        exceptionOccurred = false;
        try {
            manager.processMapping(run, data, new OutputMapping(this.csvFile, null));
        }
        catch (IOException ioe) {
            exceptionOccurred = true;
        }
        Assert.assertTrue(exceptionOccurred);

        exceptionOccurred = false;
        try {
            manager.processMapping(run, data, new OutputMapping(this.jsonFile, null));
        }
        catch (IOException ioe) {
            exceptionOccurred = true;
        }
        Assert.assertTrue(exceptionOccurred);
    }
    
    /**
     * 
     * @throws IOException
     */
    @Test
    public void testStoreResult() throws IOException {
        BasicResultManager manager = new BasicResultManager(setupEnvironment(null));
        ProcessResponse res = manager.storeResults(null, (JsonNode) null);
        Assert.assertEquals(false, res.isResult());

        Run run = new Run();
        manager.getRunRepository().addItem(run);

        res = manager.storeResults(run, (JsonNode) null);
        Assert.assertEquals(false, res.isResult());

        // prepare json data
        ArrayNode data = this.mapper.createArrayNode();
        ObjectNode entry1 = this.mapper.createObjectNode();
        ObjectNode entry2 = this.mapper.createObjectNode();
        String categoryKey = "category";
        entry1.put(categoryKey, "foo");
        entry2.put(categoryKey, "bar");
        entry1.put("foo", 1);
        entry1.put("bar", 2);
        entry2.put("foo", 3);
        entry2.put("bar", 4);

        data.add(entry1);
        data.add(entry2);

        // prepare csv data
        ArrayNode data2 = this.mapper.createArrayNode();

        ObjectNode e1 = this.mapper.createObjectNode();
        e1.put("foo", "bar");
        ObjectNode e2 = this.mapper.createObjectNode();
        e2.put("foo", "foobar");

        data2.add(e1);
        data2.add(e2);

        // compose solution
        ObjectNode solution = this.mapper.createObjectNode();
        solution.set(this.solutionKey1, data);
        solution.set(this.solutionKey2, data2);


        // test without model or output mapping definition
        // model null
        res = manager.storeResults(run, solution);
        Assert.assertEquals(false, res.isResult());

        // set invalid model id
        run.setModelId("foobar");
        manager.getRunRepository().updateItem(run);

        // model null
        res = manager.storeResults(run, solution);
        Assert.assertEquals(false, res.isResult());

        // create model
        Model model = new Model("model");
        manager.getModelRepository().addItem(model);

        // set correct model id
        run.setModelId(model.getId());
        manager.getRunRepository().updateItem(run);

        // mapping null
        res = manager.storeResults(run, solution);
        Assert.assertEquals(false, res.isResult());

        // update mapping to empty mapping
        model.setOutputMappings(new ArrayList<OutputMapping>());
        manager.getModelRepository().updateItem(model);


        // mapping empty
        res = manager.storeResults(run, solution);
        Assert.assertEquals(false, res.isResult());

        // json mapping declaration
        OutputMapping jsonMapping = new OutputMapping(this.jsonFile, MappingType.JSON_CATEGORY);
        MappingSource jsonSource = new MappingSource(this.solutionKey1);
        jsonSource.setRow(new MappingSpecification(null, categoryKey));
        jsonSource.setValue(new ValueMapping(new String[] { "foo", "bar" }));
        jsonMapping.setSources(new MappingSource[] { jsonSource });

        // csv mapping declaration
        OutputMapping csvMapping = new OutputMapping(this.csvFile, MappingType.KEY_TO_COLUMN);
        csvMapping.setSources(new MappingSource[]{new MappingSource(this.solutionKey2)});

        // invalid mapping declaration
        OutputMapping invalidMapping = new OutputMapping("test.csv", null);

        // store mappings @ model
        List<OutputMapping> mappings = new ArrayList<>();
        mappings.add(csvMapping);
        mappings.add(jsonMapping);
        model.setOutputMappings(mappings);
        manager.getModelRepository().updateItem(model);

        res = manager.storeResults(run, solution);
        Assert.assertTrue(res.isResult());
        Assert.assertNull(res.getMessage());

        // compare json data
        JsonNode node = this.mapper.readTree(manager.getRunRepository().getAttachment(run.getId(), this.jsonFile));
        Assert.assertEquals(1, node.get("foo").get("foo").intValue());
        Assert.assertEquals(2, node.get("foo").get("bar").intValue());
        Assert.assertEquals(3, node.get("bar").get("foo").intValue());
        Assert.assertEquals(4, node.get("bar").get("bar").intValue());


        // compare csv data
        String output = IOUtils.toString(manager.getRunRepository().getAttachment(run.getId(), this.csvFile));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        pw.println("foo");
        pw.println("bar");
        pw.print("foobar");
        pw.close();
        String expectedResult = outputStreamToString(out);

        Assert.assertEquals(expectedResult, output);

        // reset run, this time send input stream
        manager.getRunRepository().removeAll();
        run = new Run();
        run.setModelId(model.getId());
        manager.getRunRepository().addItem(run);

        mappings.add(invalidMapping);
        model.setOutputMappings(mappings);
        manager.getModelRepository().updateItem(model);

        res = manager.storeResults(run, new ByteArrayInputStream(this.mapper.writeValueAsBytes(solution)));
        Assert.assertTrue(res.isResult());
        Assert.assertEquals("[test.csv]", res.getMessage());

        // compare json data
        node = this.mapper.readTree(manager.getRunRepository().getAttachment(run.getId(), this.jsonFile));
        Assert.assertEquals(1, node.get("foo").get("foo").intValue());
        Assert.assertEquals(2, node.get("foo").get("bar").intValue());
        Assert.assertEquals(3, node.get("bar").get("foo").intValue());
        Assert.assertEquals(4, node.get("bar").get("bar").intValue());

        // compare csv data
        output = IOUtils.toString(manager.getRunRepository().getAttachment(run.getId(), this.csvFile));
        out = new ByteArrayOutputStream();
        pw = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        pw.println("foo");
        pw.println("bar");
        pw.print("foobar");
        pw.close();
        expectedResult = outputStreamToString(out);

        Assert.assertEquals(expectedResult, output);

        res = manager.storeResults(run, (InputStream) null);
        Assert.assertFalse(res.isResult());
    }





    /*
     * HELPER METHODS
     */

    /**
     * 
     * @param out
     * @return
     * @throws IOException
     */
    private String outputStreamToString(ByteArrayOutputStream out) throws IOException {
        return IOUtils.toString(new ByteArrayInputStream(out.toByteArray()));
    }

    /**
     * 
     * @param props
     * @return
     */
    private Environment setupEnvironment(Properties props) {
        Environment env = EnvironmentHelper.mockEnvironment(props);
        env.setAttribute(RunRepository.RUN_REPOSITORY_INSTANCE, new TransientRunRepository());
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, new TransientModelRepository());

        return env;
    }
}
