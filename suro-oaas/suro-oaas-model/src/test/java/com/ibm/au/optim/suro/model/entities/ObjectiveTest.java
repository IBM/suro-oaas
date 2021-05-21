package com.ibm.au.optim.suro.model.entities;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>ObjectiveTest</b>. This class tests the implemented behaviour
 * of the {@link Objective}. An {@link Objective} instance has always a
 * <i>name</i> and a <i>label</i> not {@literal null} and not empty. The
 * <i>description</i> can be {@literal null}. If not specific the <i>label</i>
 * takes the value of the <i>name</i>.
 * 
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class ObjectiveTest {
    

	/**
	 * This method tests the implemented behaviour of {@link Objective#Objective(String)}.
	 * The method is expected to initialise an instance of {@link Objective} whose <i>name</i>
	 * is passed as argument to the constructor, <i>label</i> is the same as the <i>name</i>
	 * and <i>description</i> is {@literal null}. The <i>name</i> cannot be {@literal null} or
	 * an empty string. 
	 */
    @Test
    public void testConstructorWithName() {
    	
    	try {
    	
    		new Objective(null);
    		Assert.fail("Objective.Objective(null) should throw IllegalArgumentException.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, this means that the exception has
    		// been thrown as expected.
    	}
    	
    	String expectedName = "o1";
    	
        Objective obj = new Objective(expectedName);
        Assert.assertEquals(expectedName, obj.getName());
        Assert.assertEquals(expectedName, obj.getLabel());
        Assert.assertNull(obj.getDescription());
        
    }
    
	/**
	 * This method tests the implemented behaviour of {@link Objective#Objective(String,String,String)}.
	 * The method is expected to initialise an instance of {@link Objective} whose <i>name</i> is passed 
	 * as first argument to the constructor, <i>label</i> is passed as second argument, and <i>description</i>
	 * is set to the third argument. Neither <i>name</i> nor <i>label</i> can be {@literal null} or an
	 * empty string.
	 */
    @Test
    public void testConstructorWithNameLabelAndDescription() {
    	
    	String expectedName = "o1";
    	
        try {
        	
    		new Objective(null, "Label", "This is a description.");
    		Assert.fail("Objective.Objective(null, String, String) should throw IllegalArgumentException.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, this means that the exception has
    		// been thrown as expected.
    	}
        
        try {

    		new Objective(expectedName, null, "This is a description.");
    		Assert.fail("Objective.Objective(String, null, String) should throw IllegalArgumentException.");
        	
        } catch(IllegalArgumentException ilex) {
        	
        	// ok, this means that exception has
        	// been thrown
        }
        
        Objective obj = new Objective(expectedName, "Label", null);
        Assert.assertEquals(expectedName, obj.getName());
        Assert.assertEquals("Label", obj.getLabel());
        Assert.assertNull(obj.getDescription());

        obj = new Objective(expectedName, "Label", "This is a description.");
        Assert.assertEquals(expectedName, obj.getName());
        Assert.assertEquals("Label", obj.getLabel());
        Assert.assertEquals("This is a description.", obj.getDescription());
        
        
        
    }
    
    /**
     * This method tests the implemented behaviour of the getter and setter
     * methods for the <i>label</i> property. The label cannot be {@literal 
     * null} or an empty string. Moreover, the value of set with the setter
     * must be returned by the getter. The default value is equal to the 
     * value of the <i>name</i> property.
     */
    @Test
    public void testGetSetLabel() {
    	

		// Test 1. equal to the name by the default.
		//
		Objective objective = new Objective("o1");
		Assert.assertEquals(objective.getName(), objective.getLabel());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "thisIsALabel";
		objective.setLabel(expected);
		String actual = objective.getLabel();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value? No!
		//
		try {
		
			objective.setLabel(null);
			Assert.fail("Objective.setLabel(null) should throw an IllegalArgumentException when label is null.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
		}

		// Test 4. can we assign an empty value? No!
		//
		try {
		
			objective.setLabel("");
			Assert.fail("Objective.setName('') should throw an IllegalArgumentException when label is an empty string.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
		}
    }

    
    /**
     * This method tests the implemented behaviour of the getter and setter
     * methods for the <i>name</i> property. The name cannot be {@literal 
     * null} or an empty string. Moreover, the value of set with the setter
     * must be returned by the getter. 
     */
    @Test
    public void testGetSetName() {

    	String expectedName = "o1";
    	
		// Test 1. null by default.
		//
		Objective objective = new Objective(expectedName);
		String actualName = objective.getName();
		Assert.assertEquals(expectedName, actualName);
		
		// Test 2. can we assign a value and retrieve it?
		//
		expectedName = "o2";
		objective.setName(expectedName);
		actualName = objective.getName();
		Assert.assertEquals(expectedName, actualName);
		
		// Test 3. can we assign a null value? No!
		//
		try {
		
			objective.setName(null);
			Assert.fail("Objective.setName(null) should throw an IllegalArgumentException when name is null.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
		}

		// Test 4. can we assign an empty value? No!
		//
		try {
		
			objective.setName("");
			Assert.fail("Objective.setName('') should throw an IllegalArgumentException when name is an empty string.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
		}
    	
    	
    }

    /**
     * This method tests the implemented behaviour of the getter and setter
     * methods for the <i>description</i> property. The value of set with the setter
     * must be returned by the getter. There are no restrictions on the value of the
     * property and its default is {@literal null}.
     */
    @Test
    public void testGetSetDescription() {
    	

		// Test 1. null by default.
		//
		Objective objective = new Objective("o1");
		Assert.assertNull(objective.getDescription());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "This is a description.";
		objective.setDescription(expected);
		String actual = objective.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		objective.setDescription(null);
		Assert.assertNull(objective.getDescription());
    	
    }
    
    /**
     * This method tests the implemented behaviour of {@link Objective#equals(Object)}.
     * The method is expected to return {@literal true} if the instances compared are
     * both of type {@link Objective} and have the same name.
     */
    @Test
    public void testEquals() {
    	
    	// Test 1. obviousness test.
    	//
        Objective obj1 = new Objective("o1");
        Assert.assertTrue(obj1.equals(obj1));
        
        // Test 2. same name, should return true.
        //
        Objective obj2 = new Objective("o1");
        Assert.assertTrue(obj1.equals(obj2));
        
        
        // Test 3. different name, should return false
        //
        obj1.setName("o3");
        Assert.assertFalse(obj1.equals(obj2));

        // Test 4. when name the same again, should return true
        //
        obj2.setName("o3");
        Assert.assertTrue(obj1.equals(obj2));

        // Test 5. change of the label property does not affect
        //         the equality test.
        //
        obj1.setLabel("label");
        Assert.assertTrue(obj1.equals(obj2));

        obj2.setLabel("label");
        Assert.assertTrue(obj1.equals(obj2));

        // Test 6. change of the description property does not
        //		   affect the equality test.
        //
        obj1.setDescription("description");
        Assert.assertTrue(obj1.equals(obj2));

        obj2.setDescription("description");
        Assert.assertTrue(obj1.equals(obj2));
        

        // Test 7. different type of object, should return false.
        //
        Assert.assertFalse(obj1.equals("o1"));
        
        // Test 8. invoked with null, should return false.
        //
        Assert.assertFalse(obj1.equals(null));
    }

    /**
     * This method tests the implemented behaviour of {@link Objective#clone()}.
     * The method is expected to clone complex properties and to shallow copy 
     * immutable properties or simple ones.
     */
    @Test
    public void testClone() {
    
    	Objective expected = new Objective("o1");
    	Objective actual = expected.clone();
    	this.equals(expected, actual);
    	
    	expected.setName("o2");
    	expected.setLabel("This is a label.");
    	expected.setDescription("This is the description.");
    	
    	actual = expected.clone();
    	this.equals(expected, actual);
    }
    
    /**
     * This method tests that the two instances of {@link Objective} passed as arguments
     * are the same. It does so by comparing field by field all the properties of the 
     * {@link Objective} class (those defined directly in the class).
     * 
     * @param expected	a {@link Objective} reference. It cannot be {@literal null}.
     * @param actual	a {@link Objective} reference. It cannot be {@literal null}.
     */
    protected void equals(Objective expected, Objective actual) {
    	
    	Assert.assertEquals(expected.getName(), actual.getName());
    	Assert.assertEquals(expected.getLabel(), actual.getLabel());
    	Assert.assertEquals(expected.getDescription(), actual.getDescription());
    }
    
    
}
