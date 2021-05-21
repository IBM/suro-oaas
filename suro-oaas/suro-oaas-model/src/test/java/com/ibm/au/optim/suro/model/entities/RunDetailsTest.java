package com.ibm.au.optim.suro.model.entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.jaws.data.utils.map.MapUtils;
import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.entities.RunLogEntry;

/**
 * Class <b>RunDetailsTest</b>. This class extends {@link EntityTest} and
 * adds methods to test the specific methods of the {@link RunDetails} class.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class RunDetailsTest extends EntityTest {
	
	/**
	 * A {@literal double} constant that is used for testing
	 * equality between {@literal double} instances.
	 */
	public static final double DELTA = 0.0000000000001;

	/**
	 * This method tests the implemented behaviour of {@link RunDetails#RunLog()}.
	 * The default constructor should initialise the property of the instance
	 * to their default values.
	 */
	@Test
    public void testDefaultConstructor() {
    	
		// Test 1. New instance we simply verify default values.
		//
        RunDetails runLog = new RunDetails();

        Assert.assertNull(runLog.getId());
        Assert.assertNull(runLog.getRevision());
        Assert.assertNull(runLog.getAttachments());
        
        Assert.assertNull(runLog.getRunId());
        Assert.assertNull(runLog.getEntries());
        Assert.assertNull(runLog.getAttributes());
        
        Assert.assertEquals(runLog.getBestBound(), 0.0, RunDetailsTest.DELTA);
        Assert.assertEquals(runLog.getBestInteger(), 0.0, RunDetailsTest.DELTA);
        Assert.assertEquals(runLog.getGap(), 0.0, RunDetailsTest.DELTA);
    }
	
	/**
	 * This method tests the implemented behaviour of the parameterised constructor
	 * {@link RunDetails#RunLog(String, List, double, double, double)}. This method sets
	 * all the values that are passed as arguments to the constructor to the properties
	 * in the instance.
	 */
	@Test
    public void testConstructorWithParameters() {
		
        RunLogEntry entry = new RunLogEntry();
        List<RunLogEntry> expectedEntries = new ArrayList<>();
        expectedEntries.add(entry);
        
        Map<String,Object> expectedAttributes = new HashMap<String,Object>();
        expectedAttributes.put("a1", true);

        RunDetails runLog = new RunDetails("run-id", expectedEntries, 12.34, 56.78, 0.05, expectedAttributes);

        Assert.assertEquals(runLog.getRunId(), "run-id");
        Assert.assertEquals(runLog.getBestBound(), 12.34, 0.00000001);
        Assert.assertEquals(runLog.getBestInteger(), 56.78, 0.00000001);
        Assert.assertEquals(runLog.getGap(), 0.05, 0.00000001);

        
        List<RunLogEntry> actualEntries = runLog.getEntries();
        Assert.assertNotNull(actualEntries);
        Assert.assertEquals(expectedEntries.size(), actualEntries.size());
        RunLogEntry ee = expectedEntries.get(0);
        RunLogEntry ea = actualEntries.get(0);
        Assert.assertEquals(ee, ea);
        
        
        Map<String, Object> actualAttributes = runLog.getAttributes();
        Assert.assertNotNull(actualAttributes);
        boolean areTheSame = MapUtils.checkEquals(expectedAttributes, actualAttributes);
        Assert.assertTrue(areTheSame);
        
    }

	
	/**
	 * This method test the implemented behaviour of the getter and setter methods
	 * for the <i>runId</i> property. By default is set to {@literal null} and the
	 * value set with the setter should be retrieved by the corresponding getter.
	 */
	@Test
	public void testGetSetRunId() {
		
		// Test 1. null by default.
		//
		RunDetails runLog = new RunDetails();
		Assert.assertNull(runLog.getRunId());
		
		// Test 2. we set the value and we retrieve it.
		//
		String expected = UUID.randomUUID().toString();
		runLog.setRunId(expected);
		String actual = runLog.getRunId();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we set null?
		//
		runLog.setRunId(null);
		Assert.assertNull(runLog.getRunId());
		
	}

	/**
	 * This method test the implemented behaviour of the getter and setter methods
	 * for the <i>bestBound</i> property. By default is set to zero and the value 
	 * set with the setter should be retrieved by the corresponding getter.
	 */
	@Test
	public void testGetSetBestBound() {
		
		// Test 1. 0 by default.
		//
		RunDetails runLog = new RunDetails();
		Assert.assertEquals(runLog.getBestBound(), 0.0, RunDetailsTest.DELTA);
		
		// Test 2. we set the value and we retrieve it.
		//
		double expected = 0.213241;
		runLog.setBestBound(expected);
		double actual = runLog.getBestBound();
		Assert.assertEquals(expected, actual, RunDetailsTest.DELTA);
		
	}
	
	/**
	 * This method test the implemented behaviour of the getter and setter methods
	 * for the <i>bestInteger</i> property. By default is set to zero and the value 
	 * set with the setter should be retrieved by the corresponding getter.
	 */
	@Test
	public void testGetSetBestInteger() {
		

		// Test 1. 0 by default.
		//
		RunDetails runLog = new RunDetails();
		Assert.assertEquals(runLog.getBestInteger(), 0.0, RunDetailsTest.DELTA);
		
		// Test 2. we set the value and we retrieve it.
		//
		double expected = 0.213241;
		runLog.setBestInteger(expected);
		double actual = runLog.getBestInteger();
		Assert.assertEquals(expected, actual, RunDetailsTest.DELTA);
		
	}

	
	/**
	 * This method test the implemented behaviour of the getter and setter methods
	 * for the <i>gap</i> property. By default is set to zero and the value set with 
	 * the setter should be retrieved by the corresponding getter.
	 */
	@Test
	public void testGetSetGap() {
		

		// Test 1. 0 by default.
		//
		RunDetails runLog = new RunDetails();
		Assert.assertEquals(runLog.getGap(), 0.0, RunDetailsTest.DELTA);
		
		// Test 2. we set the value and we retrieve it.
		//
		double expected = 0.213241;
		runLog.setGap(expected);
		double actual = runLog.getGap();
		Assert.assertEquals(expected, actual, RunDetailsTest.DELTA);
		
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and setter methods
	 * for the <i>entries</i> property. By default is set to {@literal null} and the
	 * value set with the setter should be retrieved by the corresponding getter.
	 */
	@Test
	public void testGetSetEntries() {
		
		// Test 1. null by default.
		//
		RunDetails runLog = new RunDetails();
		Assert.assertNull(runLog.getEntries());
		
		// Test 2. we set an empty list.
		//
		List<RunLogEntry> expected = new ArrayList<RunLogEntry>();
		runLog.setEntries(expected);
		List<RunLogEntry> actual = runLog.getEntries();
		
		Assert.assertNotNull(actual);
		Assert.assertEquals(0, actual.size());
		
		// Test 3. we add a couple of entries.
		//
		
		expected = new ArrayList<RunLogEntry>();
		
		RunLogEntry rle1 = new RunLogEntry(); 
		rle1.setBestBound(0.1);
		rle1.setBestInteger(34.2);
		rle1.setGap(0.001);
		
		expected.add(rle1);
		
		
		RunLogEntry rle2 = new RunLogEntry();
		rle2.setBestBound(0.0002);
		rle2.setBestInteger(56.1);
		rle2.setIinf(12343L);
		
		expected.add(rle2);
		
		runLog.setEntries(expected);
		actual = runLog.getEntries();
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected.size(), actual.size());
		for(RunLogEntry entry : expected) {
			
			Assert.assertTrue(actual.contains(entry));
		}
		
		
		// Test 4. can we set it to null?
		//
		runLog.setEntries(null);
		Assert.assertNull(runLog.getEntries());
		
	}
	
	/**
	 * This method tests the implemented behaviour of {@link RunDetails#addEntry(RunLogEntry)}.
	 * The method is expected to add the {@link RunLogEntry} instance to the list of entries 
	 * and then updates the values of the <i>bestInteger</i>, <i>bestBound</i>, and <i>gap</i>.
	 */
	@Test
    public void testAddEntry() {
    	
        RunDetails result = new RunDetails();
        boolean addResult = result.addEntry(new RunLogEntry());
        Assert.assertTrue(addResult);

        addResult = result.addEntry(new RunLogEntry());
        Assert.assertFalse(addResult);
        Assert.assertEquals(result.getEntries().size(), 1);
        

        RunLogEntry e = new RunLogEntry();
        e.setTime(500);
        addResult = result.addEntry(e);
        Assert.assertTrue(addResult);
        Assert.assertEquals(result.getEntries().size(), 2);

        Assert.assertEquals(0.0, result.getGap(), RunDetailsTest.DELTA);
        Assert.assertEquals(0.0, result.getBestBound(), RunDetailsTest.DELTA);
        Assert.assertEquals(0.0, result.getBestInteger(), RunDetailsTest.DELTA);

        RunLogEntry e2 = new RunLogEntry();
        e2.setGap(13.37);
        e2.setBestInteger(5000.1);
        e2.setBestBound(12000.1);
        e2.setTime(10000);

        addResult = result.addEntry(e2);
        Assert.assertTrue(addResult);

        Assert.assertEquals(13.37, result.getGap(), RunDetailsTest.DELTA);
        Assert.assertEquals(5000.1, result.getBestInteger(), RunDetailsTest.DELTA);
        Assert.assertEquals(12000.1, result.getBestBound(), RunDetailsTest.DELTA);
    }

    
    /**
     * This method tests the implemented behaviour of the getter and the setter for the 
     * <i>attributes</i>  property. By default this property is set to {@literal null} 
     * and this is what the getter called over a fresh instance should return. Moreover, 
     * if we set the value with the setter we should retrieve it with the getter. There 
     * are no particular restrictions to the values that can be assigned ({@literal null} 
     * is allowed).
     */
    @Test
    public void testGetSetAttributes() {
    	
    	// Test 1. By default the value should be null, if we call the default constructor.
    	//
    	
    	RunDetails details = new RunDetails();
    	Assert.assertNull(details.getAttributes());
    	
    	// Test 2. We set a collection of attributes and we see whether they are
    	//         retrieved back by the getter.
    	
    	Map<String,Object> expected = new HashMap<String,Object>();
    	expected.put("a1", true);
    	expected.put("a2", -34);
    	expected.put("a3", "attribute");
    	expected.put("a4", .04);
    	expected.put("a5", null);
    	
    	details.setAttributes(expected);
    	Map<String,Object> actual = details.getAttributes();
    	Assert.assertNotNull(actual);
    	
    	boolean areTheSame = MapUtils.checkEquals(expected, actual);
    	Assert.assertTrue(areTheSame);
    	
    	
    	// Test 3. Can we set null?
    	//
    	details.setAttributes(null);
    	Assert.assertNull(details.getAttributes());
    	
    	
    }
    /**
     * This method tests the implemented behaviour of the getter and setter for a single
     * attribute. This pair is constituted by {@link RunDetails#setAttribute(String, Object)}
     * and {@link RunDetails#getAttributes()}. The expected behaviour is the following:
     * <ul>
     * <li>the name of the attributed cannot be {@literal null} or an empty string</li>
     * <li>if we set an attribute via {@link RunDetails#setAttribute(String,Object)}, we
     * should be able to retrieve the same attribute via {@link RunDetails#getAttribute(String)}
     * when the method is called with the same name.</li>
     * <li>if we remove an attribute this will not be accessible anymore and the getter
     * should return {@literal null}</li>
     * </ul>
     */
    @Test
    public void testGetSetAttribute() {
    	
    	RunDetails details = new RunDetails();
    	
    	// Test 1. with a fresh instance there are no attributes, therefore the getter
    	//         should return null for any attribute I can think of.
    	//
    	
    	Assert.assertNull(details.getAttribute("I_Do_Not_Exist"));
    	
    	// Test 2. we set an attribute and this should be retrieved via the getter.
    	//
    	String attr = "attr";
    	Object expected = new Boolean(true);
    	
    	details.setAttribute(attr, expected);
    	Object actual = details.getAttribute(attr);
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(expected, actual);
    	
    	// additional check, this should be present in the map of attributes
    	// we get from getAttributes and it should have the same value.
    	
    	Map<String,Object> attributes = details.getAttributes();
    	Assert.assertNotNull(attributes);
    	Assert.assertEquals(1, attributes.size());
    	
    	Assert.assertTrue(attributes.containsKey(attr));
    	actual = attributes.get(attr);
    	Assert.assertEquals(expected, actual);
    	
    	
    	// Test 3. we remove the attribute, and we should not be able
    	//         to obtain it anymore.
    	//
    	details.setAttributes(null);
    	actual = details.getAttribute(attr);
    	Assert.assertNull(actual);
    	
    	
    	// Test 4. getAttribute(String) should raise exception when invoked with null
    	//
    	try {
    		
    		details.getAttribute(null);
    		Assert.fail("RunDetails.getAttribute(null) should throw IllegalArgumentException (name cannot be null).");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go.
    	}
    	
    	
    	// Test 5. getAttribute(String) should raise exception when invoked with an empty string
    	//
    	try {
    		
    		details.getAttribute("");
    		Assert.fail("RunDetails.getAttribute('') should throw IllegalArgumentException (name cannot be an empty string).");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go.
    	}
    	
    	
    	// Test 6. setAttribute(String, Object) should raise exception when invoked with null
    	//
    	try {
    		
    		details.setAttribute(null, 21);
    		Assert.fail("RunDetails.setAttribute(null, Object) should throw IllegalArgumentException (name cannot be null).");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go.
    	}
    	
    	
    	// Test 7. setAttribute(String, Object) should raise exception when invoked with an empty string
    	//
    	try {
    		
    		details.setAttribute("", "hello");
    		Assert.fail("RunDetails.setAttribute('', Object) should throw IllegalArgumentException (name cannot be an empty string).");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go.
    	}
    	
    	
    }
    

    /**
     * This method tests the implemented behaviour of {@link RunDetails#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
    @Override
    public void testClone() {
    	
    	super.testClone();
    	
    	List<RunLogEntry> entries = new ArrayList<RunLogEntry>();
    	
    	RunLogEntry entry = new RunLogEntry();
    	
    	entry.setBestBound(34.2);
    	entry.setBestInteger(0.234);
    	entry.setGap(0.002);
    	entry.setIinf(31431743L);
    	entry.setNode(1232242L);
    	entry.setObjective(391.3);
    	entry.setNodesLeft(32134L);
    	entry.setTime(332134L);
    	entry.setSolution(true);
    	entry.setOtherCheck(false);
    	entry.setTotalIterations(35353724L);
    	entry.setRawLine("This is a raw line...");
    	
    	entries.add(entry);
    	
    	Map<String, Object> attributes = new HashMap<String,Object>();
    	attributes.put("a1", 1);
    	attributes.put("a2", true);
    	attributes.put("a3", .01);
    	attributes.put("a4", "thisisastring");
    	
    	RunDetails expected = new RunDetails("run-id", entries, 12.34, 56.78, 0.05, attributes);
    	RunDetails actual = (RunDetails) expected.clone();
    	
    	// we check that the two do not reference to the same instance.
    	//
    	Assert.assertFalse(expected == actual);
    	
    	// same for the list of entries.
    	//
    	List<RunLogEntry> eEntries = expected.getEntries();
    	List<RunLogEntry> aEntries = actual.getEntries();
    	Assert.assertFalse(eEntries == aEntries);
    	
    	// same for the map of attributes. 
    	//
    	Map<String,Object> eAttributes = expected.getAttributes();
    	Map<String,Object> aAttributes = actual.getAttributes();
    	Assert.assertFalse(eAttributes == aAttributes);
    	
    	this.equals(expected, actual);
    }
    

    
    /**
     * This method extends the {@link EntityTest#equals(Entity, Entity)} and complements it
     * with field-by-fields checks on the properties defined in the {@link RunDetails} class.
     * 
     * @param expected	a {@link Entity} reference. It cannot be {@literal null} and must
     * 					be of type {@link RunDetails}.
     * @param actual	a {@link Entity} reference. It cannot be {@literal null} and must
     * 					be of type {@link RunDetails}.
     */
    @Override
    protected void equals(Entity expected, Entity actual) {
    	
    	super.equals(expected, actual);
    	
    	RunDetails re = (RunDetails) expected;
    	RunDetails ra = (RunDetails) actual;
    	
    	Assert.assertEquals(re.getBestBound(), ra.getBestBound(), RunDetailsTest.DELTA);
    	Assert.assertEquals(re.getBestInteger(), ra.getBestInteger(), RunDetailsTest.DELTA);
    	Assert.assertEquals(re.getGap(), ra.getGap(), RunDetailsTest.DELTA);
    	Assert.assertEquals(re.getRunId(), ra.getRunId());

    	List<RunLogEntry> entries = re.getEntries();
    	if (entries != null) {
    		
    		List<RunLogEntry> targets = ra.getEntries();
    		Assert.assertNotNull(targets); 
    		Assert.assertEquals(entries.size(), targets.size());
    		
    		// [CV] NOTE: this is a relaxed check, but it works for the
    		//            purpose of the testing we're carrying out.
    		//
    		for(RunLogEntry entry : entries) {
    		
    			boolean hasEntry = targets.contains(entry);
    			Assert.assertTrue(hasEntry);
    		}
    		
    	} else {
    		
    		Assert.assertNull(ra.getEntries());
    	}
    	
    	Map<String, Object> attributes = re.getAttributes();
    	if (attributes != null) {
    		
    		Map<String,Object> targets = ra.getAttributes();
    		Assert.assertNotNull(targets);
    		Assert.assertEquals(attributes.size(), targets.size());
    		
    		for(Entry<String, Object> pair : attributes.entrySet()) {
    			
    			String key = pair.getKey();
    			
    			Assert.assertTrue(targets.containsKey(key));
    			Object value = targets.get(key);
    			
    			Assert.assertEquals(pair.getValue(), value);
    		}
    		
    		
    	} else {
    		
    		Assert.assertNull(ra.getAttributes());
    	}
    	
    }
    
    /**
     * This method specialises the {@link Entity#createEntity()} method to
     * return an instance of type {@link RunDetails}. 
     * 
     * @return a {@link RunDetails} instance.
     */
    @Override
    protected Entity createEntity() {
    	
    	return new RunDetails();
    }

}
