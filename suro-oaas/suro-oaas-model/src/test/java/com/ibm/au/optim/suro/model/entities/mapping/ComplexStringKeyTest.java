package com.ibm.au.optim.suro.model.entities.mapping;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


/**
 * Class <b>ComplexStringKeyTest</b>. This class tests the implemented behaviour
 * of the {@link ComplexStringKey} class. This class is essentially a wrapper around
 * an array of {@link String} instances, so that they can be convenirenty used as 
 * keys within a {@link Map} implementation. The current class tests the logic 
 * constructor, the default methods inherithed and specialised gfrom {@link Object}
 * and the property getter and setter.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class ComplexStringKeyTest {

	/**
	 * This method tests the implemented behaviour of the {@link ComplexStringKey} constructor. 
	 * The expected behaviour is to create an instance of {@link ComplexStringKey} configured 
	 * with the given array of {@link String}. If not {@literal null}, the content of the array
	 * is copied into another array to avoid references.
	 */
	@Test
    public void testConstructor() {
        
		// Test 1. Initialisation with null.
		//
    	ComplexStringKey key = new ComplexStringKey(null);
        Assert.assertNull(key.getContent());

        // Test 2. Edge case an empty array.
        //
        String[] expected = new String[] {};
        key = new ComplexStringKey(expected);
        String[] actual = key.getContent();
        Assert.assertNotNull(actual);
        // the array reference should be different
        // because it has been copied.
        //
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(expected.length, actual.length);
        
        // Test 3. Normal case. The array should preserve
        //         the order.
        
        expected = new String[] { "this", "is", "a", "test" };
        key = new ComplexStringKey(expected);
        actual = key.getContent();
        Assert.assertNotNull(actual);
        // the array reference should be different
        // because it has been copied.
        //
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(expected.length, actual.length);
        for(int i=0; i<expected.length; i++) {
        	
        	Assert.assertEquals(expected[i], actual[i]);
        }
        
    }


	/**
	 * This method tests the implemented behaviour of the getter and setter for the <i>content</i>
	 * property. The getter is expected to retrieve what was originally passed to constructor and
	 * set with the setter. There are no particular restrictions on the values that can be assigned.
	 * Moreover, the setter is expected to create another instance of the array when the original
	 * argument is not {@literal null}.
	 */
	@Test
    public void testGetSetContent() {
        
		// Test 1. Do we get with the getter, what it was initialised?
		//
    	ComplexStringKey key = new ComplexStringKey(null);
    	Assert.assertNull(key.getContent());
    	
    	
    	// Test 2. Let's set the value explicitly and see whether we
    	//         can retrieve it.
    	
    	// 2a. empty array.

        String[] expected = new String[] {};
        key.setContent(expected);
        String[] actual = key.getContent();
        Assert.assertNotNull(actual);
        // the array reference should be different
        // because it has been copied.
        //
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(expected.length, actual.length);
        
        
        // 2b. an non empty array.
        //
        expected = new String[] { "this", "is", "a", "test" };
        key.setContent(expected);
        actual = key.getContent();
        Assert.assertNotNull(actual);
        // the array reference should be different
        // because it has been copied.
        //
        Assert.assertFalse(expected == actual);
        Assert.assertEquals(expected.length, actual.length);
        for(int i=0; i<expected.length; i++) {
        	
        	Assert.assertEquals(expected[i], actual[i]);
        }
    	
    	
    	// Test 3. Can we accept null?
    	//
        key.setContent(null);
    	Assert.assertNull(key.getContent());
    }

	/**
	 * This method tests the effective use of {@link ComplexStringKey} as a key within a {@link Map} 
	 * implementation. The method assumes that the {@link ComplexStringKey#hashCode()} is properly 
	 * implemented.
	 */
	@Test
    public void testHashSet() {
    	
        ComplexStringKey key1 = new ComplexStringKey(new String[] { "foo", "bar" });
        ComplexStringKey key2 = new ComplexStringKey(new String[] { "bar", "foo" });
        ComplexStringKey emptyKey = new ComplexStringKey(new String[] { });
        ComplexStringKey emptyKey2 = new ComplexStringKey(new String[] { "" });
        ComplexStringKey nullKey = new ComplexStringKey(null);

        Map<ComplexStringKey, String> map = new HashMap<ComplexStringKey, String>();
        String label1 = "foobar";
        String label2 = "barfoo";
        String nullLabel = "null";
        String emptyLabel = "empty";

        map.put(key1, label1);
        map.put(key2, label2);
        map.put(nullKey, nullLabel);


        Assert.assertEquals(3, map.size());
        Assert.assertEquals(3, map.keySet().size());
        Assert.assertEquals(3, map.values().size());

        Assert.assertEquals(label1, map.get(key1));
        Assert.assertEquals(label2, map.get(key2));
        Assert.assertEquals(nullLabel, map.get(nullKey));

        Assert.assertNull(map.get(emptyKey));
        Assert.assertNull(map.get(emptyKey2));

        map.put(emptyKey, emptyLabel);
        Assert.assertEquals(emptyLabel, map.get(emptyKey));
        Assert.assertNull(map.get(emptyKey2));

        map.put(emptyKey2, nullLabel);
        Assert.assertEquals(emptyLabel, map.get(emptyKey));
        Assert.assertEquals(nullLabel, map.get(emptyKey2));
    }
	
	/**
	 * This method tests the implemented behaviour of the {@link ComplexStringKey#clone()}.
	 * The semantics of the method is to generate a copy of the fields of the original
	 * instance. In this specific case {@link ComplexStringKey} defines only an array of
	 * {@link String} instances, which are immutable. Therefore there will only be a
	 * copy of the array.
	 */
	@Test
	public void testClone() {
		
		// Test 1. Create a fresh object and test whether the clone method does work.
		//
		ComplexStringKey expected = new ComplexStringKey(null);
		ComplexStringKey actual = expected.clone();
		Assert.assertNotNull(actual);
		// we should have a new instance and not the same one.
		Assert.assertFalse(expected == actual); 
		Assert.assertEquals(expected, actual);
		
		// Test 2. We now change some properties and we check what happens
		//         when we clone it again.
		expected.setContent(new String[] { "this", "is", "a" , "test" });
		actual = expected.clone();
		Assert.assertNotNull(actual);
		// we should have a new instance and not the same one.
		Assert.assertFalse(expected == actual); 
		Assert.assertEquals(expected, actual);
		
		// we now want to be sure, that the clone has worked by
		// duplicating the array.
		//
		Assert.assertFalse(expected.getContent() == actual.getContent());
	}
	
	/**
	 * This method tests the implemented behaviour of the {@link ComplexStringKey#equals(Object)}
	 * method. The method is expected to return {@link true} when an instance is compared to itself,
	 * or when it is compared to another instance that has the same set of components of the key in
	 * the same order. In all other cases it should return {@literal false}.
	 */
	@Test 
	public void testEquals() {
		
		// Test 1. Obviousness test.
		//
		ComplexStringKey expected = new ComplexStringKey(null);
		Assert.assertTrue(expected.equals(expected));
		
		expected = new ComplexStringKey(new String[] {});
		Assert.assertTrue(expected.equals(expected));
		
		expected = new ComplexStringKey(new String[] { "this", "is", "a", "test"});
		Assert.assertTrue(expected.equals(expected));
		
		
		// Test 2. Behaviour against clone.
		//
		ComplexStringKey actual = expected.clone();
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
		actual.getContent()[0] = "Maybe";
		Assert.assertFalse(expected.equals(actual));
		
		// what happens if we set null?
		//
		expected.setContent(null);
		Assert.assertFalse(expected.equals(actual));

	}
	
	/**
	 * This method tests the implemented behaviour of the {@link ComplexStringKey#hashCode()} method.
	 * This is expected to generated a combined key by joining all the components with a comma and then
	 * invoking the {@link String#hashCode()} on the resulting string. If the content is {@literal null}
	 * the value returned is 0.
	 */
	@Test 
	public void testHashCode() {
		
		// Test 1. We should get 0, if we have a null content.
		//
		ComplexStringKey key = new ComplexStringKey(null);
		Assert.assertEquals(0, key.hashCode());
		
		// Test 2. We should get "".hashCode() if we have an empty array.
		//
		int expected = "".hashCode();
		key.setContent(new String[] {});
		int actual = key.hashCode();
		Assert.assertEquals(expected, actual);
		
		// Test 3. We create a test for the general case.
		
		String source =  "This,is,a,test,";
		
		expected = source.hashCode();
		String[] content = source.split(","); // note, empty string are removed (if trailing).
		key.setContent(content);
		actual = key.hashCode();
		Assert.assertEquals(expected, actual);
	}
	
	
}
