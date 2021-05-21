package com.ibm.au.optim.suro.model.entities.mapping;



import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>ValueMappingTest</b>. This class tests the implemented behaviour of the 
 * {@link ValueMapping} class. This class is meant to hold an array of attribute 
 * names that identify the properties of interest for the mapping. What we test is
 * the logic of the constructors, getters and setters and the specific implementations
 * of the method inherited from {@link Object}.
 * 
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class ValueMappingTest {

	/**
	 * This method tests the behaviour of the {@link ValueMapping#ValueMapping()}
	 * constructor. The method is expected to initialise the array of keys to
	 * {@link null}.
	 */
    @Test
    public void testDefaultConstructor() {
    	
        ValueMapping mapping = new ValueMapping();
        String[] actual = mapping.getKeys();
        Assert.assertNull(actual);

    }
    
    /**
     * This method tests the implemented behaviour of the {@link ValueMapping#ValueMapping(String[])}]
     * constructor. The method is expected to create a new array with the values contained in the
     * array passed as argument to the constructor, if the argument is not {@literal null} otherwise
     * the value will be set to {@literal null}.
     */
    @Test
    public void testConstructorWithStringArray() {

    	// Test 1. with a single value.
    	//
        String[] expected = new String[] { "Splinter" };

        ValueMapping mapping = new ValueMapping(expected);
        String[] actual = mapping.getKeys();
        Assert.assertNotNull(actual);
        Assert.assertEquals(1, actual.length);
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(expected[0], actual[0]);

        // Test 2. with a bunch of values.
        //
        expected = new String[] { "Leonardo", "Michelangelo", "Donatello", "Raffaello" };
        mapping = new ValueMapping(expected);
        actual = mapping.getKeys(); 
        Assert.assertNotNull(actual);
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(expected.length, actual.length);
        
        for(int k=0; k<expected.length; k++) {
        	Assert.assertEquals(expected[k], actual[k]);
        }

        // Test 3. with an empty array.
        //
        expected = new String[] {};
        mapping = new ValueMapping(expected);
        actual = mapping.getKeys();
        Assert.assertNotNull(actual);
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(0, actual.length);
        
        // Test 4. null parameter.
        //
        expected = null;
        mapping = new ValueMapping(expected);
        actual = mapping.getKeys();
        Assert.assertNull(actual);
    }

    /**
     * This method tests the implemented behaviour of the {@link ValueMapping#ValueMapping(String)}
     * constructor. The method is expected to create an array containing a single element that is
     * the string passed as egument if not {@literal null}. If {@literal null} the value returned
     * by {@link ValueMapping#getKeys()} should be {@literal null}.
     */
    @Test
    public void testConstructorWithString() {
    	
    	// Test 1. null string.
    	//
    	String expected = null;
        ValueMapping mapping = new ValueMapping(expected);
        String[] actual = mapping.getKeys();
        Assert.assertNull(actual);
        
        // Test 2. a non null string
        //
        expected = "Shroeder";
        mapping = new ValueMapping(expected);
        actual = mapping.getKeys();
        Assert.assertNotNull(actual);
        Assert.assertEquals(1, actual.length);
        Assert.assertEquals(expected, actual[0]);
    	
    }

    /**
     * This method tests the implemented behaviour of the getter and setter methods
     * of the <i>keys</i> property. The getter is expected to return the array of
     * keys either initialised through the constructor or via the setter method. By
     * default the getter should return {@literal null}.
     */
    @Test
    public void testGetSetKeys() {
    	
    	// Test 1. The test should by default return null..
    	//
        ValueMapping mapping = new ValueMapping();
        Assert.assertNull(mapping.getKeys());

        // Test 2. We assign a bunch of values and check whether we get them back.
        //
        String[] expected = new String[] { "April", "Rocksteady", "Bebop" };
        mapping.setKeys(expected);
        String[] actual = mapping.getKeys(); 
        Assert.assertNotNull(actual);
        Assert.assertFalse(expected == actual); // this should be copy.
        Assert.assertEquals(expected.length, actual.length);
        
        for(int k=0; k<expected.length; k++) {
        	Assert.assertEquals(expected[k], actual[k]);
        }


        // Test 3. with an empty array.
        //
        expected = new String[] {};
        mapping = new ValueMapping(expected);
        actual = mapping.getKeys();
        Assert.assertNotNull(actual);
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(0, actual.length);
        
        // Test 4. null parameter.
        //
        expected = null;
        mapping = new ValueMapping(expected);
        actual = mapping.getKeys();
        Assert.assertNull(actual);
        
    }

	/**
	 * This method tests the implemented behaviour of the {@link ValueMapping#clone()}.
	 * The semantics of the method is to generate a copy of the fields of the original
	 * instance. In this specific case {@link ValueMapping} defines only an array of
	 * {@link String} instances, which are immutable. Therefore there will only be a
	 * copy of the array.
	 */
	@Test
	public void testClone() {
		
		// Test 1. Create a fresh object and test whether the clone method does work.
		//
		ValueMapping expected = new ValueMapping();
		ValueMapping actual = expected.clone();
		Assert.assertNotNull(actual);
		// we should have a new instance and not the same one.
		Assert.assertFalse(expected == actual); 
		Assert.assertEquals(expected, actual);
		
		// Test 2. We now change some properties and we check what happens
		//         when we clone it again.
		expected.setKeys(new String[] { "this", "is", "a" , "test" });
		actual = expected.clone();
		Assert.assertNotNull(actual);
		// we should have a new instance and not the same one.
		Assert.assertFalse(expected == actual); 
		Assert.assertEquals(expected, actual);
		
		// we now want to be sure, that the clone has worked by
		// duplicating the array.
		//
		Assert.assertFalse(expected.getKeys() == actual.getKeys());
    }

	
	/**
	 * This method tests the implemented behaviour of the {@link ValueMapping#equals(Object)} method
	 * The method is expected to return {@link true} when an instance is compared to itself, or when
	 * it is compared to another instance that has the same set of components of the key in the same
	 * order. In all other cases it should return {@literal false}.
	 */
	@Test 
	public void testEquals() {
		
		// Test 1. Obviousness test.
		//
		ValueMapping expected = new ValueMapping();
		Assert.assertTrue(expected.equals(expected));
		
		expected = new ValueMapping((String) null);
		Assert.assertTrue(expected.equals(expected));
		
		expected = new ValueMapping((String[]) null);
		Assert.assertTrue(expected.equals(expected));
		
		expected = new ValueMapping(new String[] {});
		Assert.assertTrue(expected.equals(expected));
		
		expected = new ValueMapping(new String[] { "this", "is", "a", "test"});
		Assert.assertTrue(expected.equals(expected));
		
		
		// Test 2. Behaviour against clone.
		//
		ValueMapping actual = expected.clone();
		Assert.assertTrue(expected.equals(actual));
		
		// Test 3. What happens if we pass null? false.
		//
		Assert.assertFalse(expected.equals(null));
		
		// Test 4. What happens if we pass a different (incompatible) type?
		//
		Assert.assertFalse(expected.equals(new String()));
		
		// Test 5. Ok, let's change something in the clone.
		//
		//
		actual.getKeys()[0] = "Maybe";
		Assert.assertFalse(expected.equals(actual));
		
		// what happens if we set null?
		//
		expected.setKeys(null);
		Assert.assertFalse(expected.equals(actual));
		
	}
}
