package com.ibm.au.optim.suro.model.entities;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>ParameterTest</b>. This class tests the behaviour defined for the 
 * class {@link Parameter}. A parameter is a pair <i>name</i>,<i>value</i>,
 * where the value of the name cannot be {@literal null} or an empty string.
 * 
 * @author Christian Vecchiola
 *
 */
public class ParameterTest {
	
	/**
	 * A {@link Random} instance used to generate variance in the values that
	 * are tested.
	 */
	protected Random random = new Random();
	
	/**
	 * Tests the default constructor. A {@link Parameter} instance created with
	 * the default constructor should have its name set to {@link Parameter#DEFAULT_NAME}
	 * and its value set to {@literal null}.
	 */
	@Test
	public void testDefaultConstructor() {
		
		
		Parameter parameter = new Parameter();
		
		String actualName = parameter.getName();
		Assert.assertNotNull(actualName);
		Assert.assertEquals(Parameter.DEFAULT_NAME, actualName);
		
		Object actualValue = parameter.getValue();
		Assert.assertNull(actualValue);
		
	}
	
	/**
	 * Tests the behaviour of the constructor with the name. When creating an instance 
	 * with this constructor we expect to have a {@link Parameter} instance whose name
	 * is the one passed as argument and the value is set to {@literal null}. A value
	 * {@literal null} or empty for the name of the parameter should raise an {@link 
	 * IllegalArgumentException}. 
	 */
	@Test
	public void testConstructorWithName() {
		
		String expected = "expected";
		Parameter parameter = new Parameter(expected);
		
		String actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		Assert.assertNull(parameter.getValue());
		
		
		expected = null;
		try {
			
			parameter = new Parameter(expected);
			Assert.fail("Parameter.Parameter(null) should throw IllegalArgumentException: a null parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
		expected = "";
		try {
			
			parameter = new Parameter(expected);
			Assert.fail("Parameter.Parameter('') should throw IllegalArgumentException: an empty parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
	}
	
	/**
	 * Tests the behaviour of the constructor with both name and value.
	 * We expect that an instance of {@link Parameter} created with this
	 * constructor will have its name and value set to the corresponding
	 * constructor parameters. A {@literal null} value or an empty string
	 * for the name should raise a {@link IllegalArgumentException}.
	 */
	@Test
	public void testConstructorWithNameAndValue() {
		
		String expected = "actualName";
		Parameter parameter = new Parameter(expected, null);
		
		String actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		Assert.assertNull(parameter.getValue());
		
		Object expectedValue = "ThisIsNotNull";
		parameter = new Parameter(expected, expectedValue);
		
		actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		Object actualValue = parameter.getValue();
		Assert.assertEquals(expectedValue, actualValue);
		
		
		
		expected = null;
		try {
			
			parameter = new Parameter(expected, expectedValue);
			Assert.fail("Parameter.Parameter(null) should throw IllegalArgumentException: a null parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
		expected = "";
		try {
			
			parameter = new Parameter(expected, expectedValue);
			Assert.fail("Parameter.Parameter('') should throw IllegalArgumentException: an empty parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		// These are additional edge cases we would like to check.
		
		
		try {
			
			parameter = new Parameter(null, null);
			Assert.fail("Parameter.Parameter(null,null) should throw IllegalArgumentException: null value for a name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// all good.
		}
		
		try {
			
			parameter = new Parameter("", null);
			Assert.fail("Parameter.Parameter('',null) should throw IllegalArgumentException: null value for a name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// all good.
		}
		
	}
	
	/**
	 * Test the getter and setter for the name property. These control the name
	 * of the parameter. The value set via {@link Parameter#setName(String)} must
	 * be equal to the value returned by {@link Parameter#getName()} called soon
	 * after. If name is {@literal null} or an empty string, the setter should
	 * throw an {@link IllegalArgumentException}.
	 */
	@Test
	public void testGetSetName() {
		
		String expected = "p1";
		Parameter parameter = this.getParameter(expected);
		
		String actual = parameter.getName();
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected, actual);
		
		
		expected = "p2";
		parameter.setName(expected);
		
		actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		
		// we need to check the invalid cases...
		//
		expected = null;
		try {
			
			parameter.setName(expected);
			Assert.fail("Parameter.setName(null) should throw IllegalArgumentException: a null parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
		expected = "";
		try {
			
			parameter.setName(expected);;
			Assert.fail("Parameter.setName('') should throw IllegalArgumentException: an empty parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
	}

	
	/**
	 * Test the getter and setter for the value property. These control the value
	 * of the parameter. The value set via {@link Parameter#setValue(Object)} must
	 * be equal to the value returned by {@link Parameter#getValue()} called soon
	 * after. 
	 */
	@Test
	public void testGetSetValue() {
		
		Parameter parameter = this.getParameter();
		
		Object actual = parameter.getValue();
		Assert.assertNull(actual);
		
		Object expected = this.getValue();
		
		parameter = this.getParameter("p1", expected);
		actual = parameter.getValue();
		Assert.assertEquals(expected, actual);
		
		
		expected = this.getValue();
		parameter.setValue(expected);
		
		actual = parameter.getValue();
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * Verifies the behaviour of the equality test, which is based on the test
	 * of the <i>name</i> of the parameter. Therefore, it is sufficient that
	 * two {@link Parameter} instances have the same <i>name</i> to be equal.
	 */
	@Test
	public void testEquals() {
		
		
		Parameter p1 = this.getParameter("p1", 3);
		Parameter p2 = this.getParameter("p2", 4);
		Parameter p3 = this.getParameter("p1", 5);

		// Test 1. obviousness test.
		//
		Assert.assertTrue(p1.equals(p1));
		
		// Test 2. false when called with null.
		//
		Assert.assertFalse(p1.equals(null));
		
		// Test 3. false when called with a non-Parameter instance.
		Assert.assertFalse(p1.equals(new Object()));
		
		// Test 4. true when the parameter have the same name.
		//
		Assert.assertTrue(p1.equals(p3));
		
		// Test 5. true when they are the same instance.
		//
		Assert.assertFalse(p1.equals(p2));
		
	}
	
	/**
	 * Verifies the behaviour of the {@link Parameter#hashCode()} method. This is meant
	 * to return the hash code value of the {@link String} instance that represents the
	 * name of the parameter.
	 */
	@Test
	public void testHashCode() {
		
		String pName1 = "p1";
		String pName2 = "p2";
		
		Parameter p1 = this.getParameter(pName1, this.getValue());
		Parameter p2 = this.getParameter(pName2, this.getValue());
		Parameter p3 = this.getParameter(pName1, this.getValue());
		
		// the hash code should be the same as the name
		//
		Assert.assertEquals(pName1.hashCode(), p1.hashCode());
		Assert.assertEquals(pName2.hashCode(), p2.hashCode());
		
		// if the two names hash codes are different, the corresponding parameter names are different.
		//
		Assert.assertEquals(pName1.hashCode() != pName2.hashCode(), p1.hashCode() != p2.hashCode());
		
		// same name, same hash code.
		//
		Assert.assertEquals(p1.hashCode(), p3.hashCode());
	
	}

    /**
     * This method tests the implemented behaviour of {@link Parameter#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
	@Test
    public void testClone() {
    	
		Parameter expected = this.getParameter();
		
		Parameter actual = expected.clone();
		Assert.assertNotNull(actual);
		
		this.equals(expected, actual);
		
		expected.setName("p31");
		expected.setValue(this.getValue());
		actual = expected.clone();
		
		this.equals(expected, actual);
    }

	/**
	 * This method compares two instances of {@link Parameter} by performing a 
	 * field by field comparison.
	 * 
	 * @param expected	a {@link Parameter} instance, must not be {@literal null}.
	 * @param actual	a {@link Parameter} instance, must not be {@literal null}.
	 */
	protected void equals(Parameter expected, Parameter actual) {
		
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getValue(), actual.getValue());
	}

	/**
	 * This is an extension method that will be redefined by inherited test classes 
	 * that can is used to create a specific instance of the {@link Parameter} class. 
	 * The reason why we abstract the creation of the instance is because we can then 
	 * easily implements all the tests and constraints to any class that inherits from 
	 * {@link Parameter} by creating test classes inheriting from this test class.
	 * 
	 * @return	a {@link Parameter} instance.
	 */
	protected Parameter getParameter() {
		
		return new Parameter();
	}
	
	/**
	 * Factory method for creating a parameter with a given name a default value.
	 * 
	 * @param name	a {@link String} representing the name of the parameter. It cannot
	 * 				be {@literal null} or an empty string.
	 * 
	 * @return a {@link Parameter} instance with the given name.	
	 */
	protected Parameter getParameter(String name) {
		
		return new Parameter(name);
	}

	
	/**
	 * Factory method for creating a parameter with a given name  and value.
	 * 
	 * @param name	a {@link String} representing the name of the parameter. It cannot
	 * 				be {@literal null} or an empty string.
	 * 
	 * @param value	a {@link Object} reference representing the value to set for the
	 * 				parameter.
	 * 
	 * @return a {@link Parameter} instance with the given name and value.	
	 */
	protected Parameter getParameter(String name, Object value) {
		
		return new Parameter(name, value);
	}
	
	/**
	 * Factory method for creating a value for a parameter.
	 * 
	 * @return	a {@link Object} reference representing the value of the parameter
	 * 			chosen for the test.
	 */
	protected Object getValue() {
		
		return "ThisIsRandom" + this.random.nextLong();
	}

}
