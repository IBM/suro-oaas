package com.ibm.au.optim.suro.model.entities;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.RunLogEntry;


/**
 * Class <b>RunLogEntryTest</b>. This class tests the implemented behaviour of the 
 * {@link RunLogEntry} class. The class is essentially a bean. Therefore the testing
 * method will simply test that the getter and the setter work as expected.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class RunLogEntryTest {

	/**
	 * This method tests the implemented behaviour of the {@link RunLogEntry#RunLogEntry()}
	 * constructor. The method is expected to set all the properties to their type defaults.
	 */
	@Test
    public void testDefaultConstructor() {
		
		
        RunLogEntry entry = new RunLogEntry();
        Assert.assertNull(entry.getBestBound());
        Assert.assertNull(entry.getBestInteger());
        Assert.assertNull(entry.getGap());
        Assert.assertFalse(entry.getOtherCheck());
        Assert.assertNull(entry.getIinf());
        Assert.assertNull(entry.getNode());
        Assert.assertNull(entry.getNodesLeft());
        Assert.assertNull(entry.getTotalIterations());
        Assert.assertEquals(0, entry.getTime());
        Assert.assertFalse(entry.isSolution());
        Assert.assertNull(entry.getObjective());
        Assert.assertNull(entry.getRawLine());
    }
	
	/**
	 * This method tests the implemented behaviour of the {@link RunLogEntry#equals(Object)}
	 * method. The method is expected to return {@literal true} if and only if the two instances
	 * are references to the same instance or they have all the field set to the same value.
	 */
	@Test 
	public void testEquals() {
		
		
		// Test 1. Obviousness test, should return true.
		//
		RunLogEntry expected = new RunLogEntry();
		Assert.assertTrue(expected.equals(expected));
		
		// Test 2. Invoked with null, should return false.
		//
		Assert.assertFalse(expected.equals(null));
		
		// Test 3. Invoked with a different object, should return false.
		//
		Assert.assertFalse(expected.equals(new Object()));
		
		// Test 4. Invoke with a different object (same fresh instance).
		//
		RunLogEntry actual = new RunLogEntry();
		Assert.assertTrue(expected.equals(actual));
		
		// Test 5. Change one property, should return false now.
		//
		expected.setBestBound(0.01);
		Assert.assertFalse(expected.equals(actual));
		
		// Test 6. We set all the values.. and use clone..
		//
		expected.setBestInteger(231231.0);
		expected.setGap(0.00001);
		expected.setSolution(true);
		expected.setOtherCheck(true);
		expected.setObjective(2131414.2121);
		expected.setTime(213472L);
		expected.setIinf(2312L);
		expected.setNode(21L);
		expected.setNodesLeft(2202022L);
		expected.setTotalIterations(231321644L);
		expected.setRawLine("This is a raw line.");
		
		actual = expected.clone();
		Assert.assertTrue(expected.equals(actual));
		
		// Test 7. We change one property at a time.
		//
		actual.setBestBound(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setBestInteger(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setObjective(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setGap(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setTime(23L);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setSolution(false);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setOtherCheck(false);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setNode(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setNodesLeft(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setIinf(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setTotalIterations(null);
		Assert.assertFalse(expected.equals(actual));
		
		actual = expected.clone();
		actual.setRawLine(null);
		Assert.assertFalse(expected.equals(actual));
		
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>bestBound</i> property. The default
	 * value is set to {@literal null} and the value set by the setter should
	 * be retrieved by the getter. There are no constraints on the value that
	 * can be set.
	 */
	@Test
	public void testGetSetBestBound() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getBestBound());
		
		// Test 2. we set the value and we retrieve it.
		//
		Double expected = new Double(34.1);
		entry.setBestBound(expected);
		Double actual = entry.getBestBound();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setBestBound(null);
		Assert.assertNull(entry.getBestBound());
		
	}
	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>bestInteger</i> property. The default
	 * value is set to {@literal null} and the value set by the setter should
	 * be retrieved by the getter. There are no constraints on the value that
	 * can be set.
	 */
	@Test
	public void testGetSetBestInteger() {

		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getBestInteger());
		
		// Test 2. we set the value and we retrieve it.
		//
		Double expected = new Double(32.1);
		entry.setBestInteger(expected);
		Double actual = entry.getBestInteger();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setBestInteger(null);
		Assert.assertNull(entry.getBestInteger());
	}

	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>gap</i> property. The default value
	 * is set to {@literal null} and the value set by the setter should be
	 * retrieved by the getter. There are no constraints on the value that
	 * can be set.
	 */
	@Test
	public void testGetSetGap() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getGap());
		
		// Test 2. we set the value and we retrieve it.
		//
		Double expected = new Double(0.1);
		entry.setGap(expected);
		Double actual = entry.getGap();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setGap(null);
		Assert.assertNull(entry.getGap());
	}

	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>objective</i> property. The default 
	 * value is set to {@literal null} and the value set by the setter should 
	 * be retrieved by the getter. There are no constraints on the value that
	 * can be set.
	 */
	@Test
	public void testGetSetObjective() {

		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getObjective());
		
		// Test 2. we set the value and we retrieve it.
		//
		Double expected = new Double(0.1);
		entry.setObjective(expected);
		Double actual = entry.getObjective();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setObjective(null);
		Assert.assertNull(entry.getObjective());
	}

	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>solution</i> property. The default 
	 * value is set to {@literal false} and the value set by the setter 
	 * should be retrieved by the getter. There are no constraints on the 
	 * value that can be set.
	 */
	@Test
	public void testGetSetSolution() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertFalse(entry.isSolution());
		
		// Test 2. we set the value and we retrieve it.
		//
		boolean expected = true;
		entry.setSolution(expected);
		boolean actual = entry.isSolution();
		Assert.assertEquals(expected, actual);
				
	}


	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>otherCheck</i> property. The default 
	 * value is set to {@literal false} and the value set by the setter 
	 * should be retrieved by the getter. There are no constraints on the 
	 * value that can be set.
	 */
	@Test
	public void testGetSetOtherCheck() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertFalse(entry.getOtherCheck());
		
		// Test 2. we set the value and we retrieve it.
		//
		boolean expected = true;
		entry.setOtherCheck(expected);
		boolean actual = entry.getOtherCheck();
		Assert.assertEquals(expected, actual);
	}

	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>iinf</i> property. The default 
	 * value is set to {@literal null} and the value set by the setter 
	 * should be retrieved by the getter. There are no constraints on the 
	 * value that can be set.
	 */
	@Test
	public void testGetSetIinf() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getIinf());
		
		// Test 2. we set the value and we retrieve it.
		//
		Long expected = 2143423L;
		entry.setIinf(expected);
		Long actual = entry.getIinf();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setIinf(null);
		Assert.assertNull(entry.getIinf());
	}

	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>node</i> property. The default 
	 * value is set to {@literal null} and the value set by the setter 
	 * should be retrieved by the getter. There are no constraints on the 
	 * value that can be set.
	 */
	@Test
	public void testGetSetNode() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getNode());
		
		// Test 2. we set the value and we retrieve it.
		//
		Long expected = 21433L;
		entry.setNode(expected);
		Long actual = entry.getNode();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setNode(null);
		Assert.assertNull(entry.getNode());
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>nodesLeft</i> property. The default 
	 * value is set to {@literal null} and the value set by the setter 
	 * should be retrieved by the getter. There are no constraints on the 
	 * value that can be set.
	 */
	@Test
	public void testGetSetNodesLeft() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getNodesLeft());
		
		// Test 2. we set the value and we retrieve it.
		//
		Long expected = 21433L;
		entry.setNodesLeft(expected);
		Long actual = entry.getNodesLeft();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setNodesLeft(null);
		Assert.assertNull(entry.getNodesLeft());	
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>totalIterations</i> property. The 
	 * default value is set to {@literal null} and the value set by the 
	 * setter should be retrieved by the getter. There are no constraints 
	 * on the value that can be set.
	 */
	@Test
	public void testGetSetTotalIterations() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getTotalIterations());
		
		// Test 2. we set the value and we retrieve it.
		//
		Long expected = 21433L;
		entry.setTotalIterations(expected);
		Long actual = entry.getTotalIterations();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setTotalIterations(null);
		Assert.assertNull(entry.getTotalIterations());	
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>time</i> property. The default
	 * default value is set to zero and the value set by the setter should
	 * be retrieved by the getter. There are no constraints on the value 
	 * that can be set.
	 */
	@Test
	public void testGetSetTime() {
		
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertEquals(0, entry.getTime());
		
		// Test 2. we set the value and we retrieve it.
		//
		long expected = 21433L;
		entry.setTime(expected);
		long actual = entry.getTime();
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the 
	 * setter methods of the {@link <i>rawLine</i> property. The default
	 * value is set to {@literal null} and the value set by the setter 
	 * should be retrieved by the getter. There are no constraints on the 
	 * value that can be set.
	 */
	@Test
	public void testGetSetRawLine() {
	
		// Test 1. null by default.
		//
		RunLogEntry entry = new RunLogEntry();
		Assert.assertNull(entry.getRawLine());
		
		// Test 2. we set the value and we retrieve it.
		//
		String expected = "This is a line... 21433L";
		entry.setRawLine(expected);
		String actual = entry.getRawLine();
		Assert.assertEquals(expected, actual);
		
		// Test 3. null test..
		entry.setRawLine(null);
		Assert.assertNull(entry.getRawLine());	
	}

    
    /**
     * This method tests the implementation of the {@link RunLogEntry#toString()}
     * method. The method is expected to dump the value of the properties in the
     * resulting string. Therefore, the test will simply lookup the values of the
     * properties in the string returned by the tested method.
     */
    @Test
    public void testToString() {
    	
        RunLogEntry entry = new RunLogEntry();
        entry.setTime(1337);
        entry.setGap(0.005);
        entry.setTotalIterations(new Long(123333));
        entry.setBestBound(1200.5);
        entry.setBestInteger(13.37);
        entry.setIinf(new Long(5555));
        entry.setObjective(12.99);
        entry.setNodesLeft(new Long(444));
        entry.setOtherCheck(true);
        entry.setNode(new Long(1100));
        entry.setSolution(true);
        entry.setRawLine("foobar");

        String result = entry.toString();

        Assert.assertTrue(result.contains("foobar"));
        Assert.assertTrue(result.contains("true"));
        Assert.assertFalse(result.contains("false"));
        Assert.assertTrue(result.contains("1337"));
        Assert.assertTrue(result.contains("0.005"));
        Assert.assertTrue(result.contains("123333"));
        Assert.assertTrue(result.contains("1200.5"));

        Assert.assertTrue(result.contains("13.37"));
        Assert.assertTrue(result.contains("5555"));
        Assert.assertTrue(result.contains("12.99"));
        Assert.assertTrue(result.contains("444"));
        Assert.assertTrue(result.contains("1100"));
    }

    /**
     * This method tests the implemented behaviour of {@link RunLogEntry#clone()}.
     * The method is expected to clone complex properties and to shallow copy 
     * immutable properties or simple ones.
     */
    @Test
    public void testClone() {
    	
    	RunLogEntry expected = new RunLogEntry();
    	RunLogEntry actual = expected.clone();
    	Assert.assertNotNull(actual);
    	this.equals(expected, actual);
    	
    	expected.setBestBound(0.1);
    	expected.setBestInteger(34.2);
    	expected.setObjective(65.1);
    	expected.setGap(0.0001);
    	expected.setSolution(true);
    	expected.setOtherCheck(true);
    	expected.setTime(213112421);
    	expected.setNode(231313251L);
    	expected.setNodesLeft(3625L);
    	expected.setIinf(32L);
    	expected.setTotalIterations(34658L);
    	expected.setRawLine("This is the raw line.");
    	

    	actual = expected.clone();
    	Assert.assertNotNull(actual);
    	this.equals(expected, actual);
    	
    	
    }
    
    /**
     * This method compares field by field the two instances passed as argument
     * to check whether each property has the same value.
     * 
     * @param expected	a {@link RunLogEntry} instance. It cannot be {@literal null}.
     * @param actual	a {@link RunLogEntry} instance. It cannot be {@literal null}.
     */
    protected void equals(RunLogEntry expected, RunLogEntry actual) {
    	
    	Assert.assertEquals(expected.getObjective(), actual.getObjective());
    	Assert.assertEquals(expected.getBestBound(), actual.getBestBound());
    	Assert.assertEquals(expected.getGap(), actual.getGap());
    	Assert.assertEquals(expected.isSolution(), actual.isSolution());
    	Assert.assertEquals(expected.getTime(), actual.getTime());
    	Assert.assertEquals(expected.getOtherCheck(), actual.getOtherCheck());
    	Assert.assertEquals(expected.getIinf(), actual.getIinf());
    	Assert.assertEquals(expected.getTotalIterations(), actual.getTotalIterations());
    	Assert.assertEquals(expected.getNode(), actual.getNode());
    	Assert.assertEquals(expected.getNodesLeft(), actual.getNodesLeft());
    	Assert.assertEquals(expected.getRawLine(), actual.getRawLine());
    }
}
