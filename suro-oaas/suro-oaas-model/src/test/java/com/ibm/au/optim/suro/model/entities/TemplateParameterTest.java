/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>TemplateParameterTest</b>. This class extends {@link ParameterTest} and provides
 * the capability for testing the additional features added by the {@link TemplateParameter}
 * to the {@link Parameter} base class, by still checking all the expected behaviour of the
 * {@link Parameter} defined methods.
 * 
 * @author Christian Vecchiola
 *
 */
public class TemplateParameterTest extends ParameterTest {
	
	/**
	 * Tests the behaviour of the constructor with the name. When creating an instance with this constructor we expect to have a {@link TemplateParameter} 
	 * instance whose name is the one passed as argument and the value is set to {@literal null}. A value {@literal null} or empty for the name of the 
	 * parameter should raise an {@link IllegalArgumentException}. The value of {@link TemplateParameter#isFixed()} should be {@literal false}.
	 */
	@Test
	public void testConstructorWithName() {
		
		String expected = "expected";
		TemplateParameter parameter = new TemplateParameter(expected);
		
		String actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		Assert.assertNull(parameter.getValue());
		Assert.assertFalse(parameter.isFixed());
		
		
		
		expected = null;
		try {
			
			parameter = new TemplateParameter(expected);
			Assert.fail("TemplateParameter.TemplateParameter(null) should throw IllegalArgumentException: a null parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
		expected = "";
		try {
			
			parameter = new TemplateParameter(expected);
			Assert.fail("TemplateParameter.TemplateParameter('') should throw IllegalArgumentException: an empty parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
	}
	
	/**
	 * Tests the behaviour of the constructor with both name and value. We expect that an instance of {@link TemplateParameter} created with this constructor 
	 * will have its name and value set to the corresponding constructor parameters. A {@literal null} value or an empty string for the name should raise a 
	 * {@link IllegalArgumentException}. The value of {@link TemplateParameter#isFixed()} should be {@literal false}.
	 */
	@Test
	public void testConstructorWithNameAndValue() {
		
		String expected = "actualName";
		TemplateParameter parameter = new TemplateParameter(expected, null);
		
		String actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		Assert.assertNull(parameter.getValue());
		Assert.assertFalse(parameter.isFixed());
		
		Object expectedValue = "ThisIsNotNull";
		parameter = new TemplateParameter(expected, expectedValue);
		
		actual = parameter.getName();
		Assert.assertEquals(expected, actual);
		
		Object actualValue = parameter.getValue();
		Assert.assertEquals(expectedValue, actualValue);
		Assert.assertFalse(parameter.isFixed());
		
		
		
		expected = null;
		try {
			
			parameter = new TemplateParameter(expected, expectedValue);
			Assert.fail("TemplateParameter.TemplateParameter(null) should throw IllegalArgumentException: a null parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
		expected = "";
		try {
			
			parameter = new TemplateParameter(expected, expectedValue);
			Assert.fail("TemplateParameter.TemplateParameter('') should throw IllegalArgumentException: an empty parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		// These are additional edge cases we would like to check.
		
		
		try {
			
			parameter = new TemplateParameter(null, null);
			Assert.fail("TemplateParameter.TemplateParameter(null,null) should throw IllegalArgumentException: null value for a name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// all good.
		}
		
		try {
			
			parameter = new TemplateParameter("", null);
			Assert.fail("TemplateParameter.TemplateParameter('',null) should throw IllegalArgumentException: null value for a name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// all good.
		}
		
	}
	
	/**
	 * This method tests the behaviour of the constructor {@link TemplateParameter#TemplateParameter(String, Object, boolean)}.
	 * This method is meant to initialise an template parameter with the values passed as arguments. After the creation the 
	 * values returned by {@literal TemplateParameter#getName()}, {@literal TemplateParameter#getValue()} and {@link TemplateParameter#isFixed()}
	 * must correspond to the value of the first, second, and third argument, respectively. A {@literal null} or empty value of
	 * the name should trigger an {@link IllegalArgumentException}.
	 */
	@Test
	public void testConstructorWithNameAndValueAndFixed() {
		

		String expectedName = "tp1";
		Object expectedValue = new Integer(34);
		boolean expectedFixed = true;
		
		TemplateParameter tp1 = new TemplateParameter(expectedName, expectedValue, expectedFixed);
		String actualName = tp1.getName();
		Assert.assertEquals(expectedName, actualName);
		
		Object actualValue = tp1.getValue();
		Assert.assertEquals(expectedValue, actualValue);
		
		boolean actualFixed = tp1.isFixed();
		Assert.assertEquals(expectedFixed, actualFixed);
		
		// let's do it with false now.
		//
		expectedFixed = false;
		
		tp1 = new TemplateParameter(expectedName, expectedValue, expectedFixed);
		actualName = tp1.getName();
		Assert.assertEquals(expectedName, actualName);
		
		actualValue = tp1.getValue();
		Assert.assertEquals(expectedValue, actualValue);
		
		actualFixed = tp1.isFixed();
		Assert.assertEquals(expectedFixed, actualFixed);
		
		
		// invalid cases...
		
		
		
		expectedName = null;
		try {
			
			tp1 = new TemplateParameter(expectedName, expectedValue, expectedFixed);
			Assert.fail("TemplateParameter.TemplateParameter(null,Object,boolean) should throw IllegalArgumentException: a null parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
		expectedName = "";
		try {
			
			tp1 = new TemplateParameter(expectedName, expectedValue, expectedFixed);
			Assert.fail("TemplateParameter.TemplateParameter('',Object,boolean) should throw IllegalArgumentException: an empty parameter name is not allowed.");
			
		} catch(IllegalArgumentException ex) {
			
			// ok, all good this means that we have
			// captured the right exception.
			
		}
		
		
	}
	
	/**
	 * Test the getter and setter for the fixed property. The value set via 
	 * {@link TemplateParameter#setFixed(boolean)} must be equal to the value 
	 * returned by {@link TemplateParameter#isFixed()} called soon after.
	 * The default value of the property is {@literal boolean}.
	 */
	@Test
	public void testGetSetFixed() {

		TemplateParameter tp1 = new TemplateParameter("tp1", 23);
		Assert.assertFalse(tp1.isFixed());
		
		boolean expectedFixed = this.random.nextBoolean();
		tp1.setFixed(expectedFixed);
		boolean actualFixed = tp1.isFixed();
		Assert.assertEquals(expectedFixed, actualFixed);
		
			
		
	}

    /**
     * This method tests the implemented behaviour of {@link TemplateParameter#clone()}.
     * The method is expected to clone complex properties and to shallow copy immutable 
     * properties or simple ones.
     */
	@Override
    public void testClone() {
		
		// this cover name and value.
		//
		super.testClone();
		
		// we just check fixed.
		//
		TemplateParameter expected = new TemplateParameter("t1");
		expected.setFixed(true);
		
		Parameter actual = expected.clone();
		this.equals(expected, actual);
    	
    }
	
	/**
	 * We override the base methods to ensure that for the tests in this class
	 * we return an instance of {@link TemplateParameter}.
	 * 
	 * @return a {@link TemplateParameter} instance.
	 */
	@Override
	protected Parameter getParameter() {
		
		return new TemplateParameter("tp1");
	}

	/**
	 * We override the base methods to ensure that for the tests in this class
	 * we return an instance of {@link TemplateParameter}.
	 * 
	 * @param name		a {@link String} non {@literal null} neither empty that
	 * 					represents the name of the parameter.
	 * 
	 * @return a {@link TemplateParameter} instance, with the given name.
	 */
	@Override
	protected Parameter getParameter(String name) {
		
		return new TemplateParameter(name);
	}

	/**
	 * We override the base methods to ensure that for the tests in this class
	 * we return an instance of {@link TemplateParameter}.
	 * 
	 * @param name		a {@link String} non {@literal null} neither empty that
	 * 					represents the name of the parameter.
	 * 
	 * @param value		a {@link Object} reference representing the value of the
	 * 					parameter.
	 * 
	 * @return a {@link TemplateParameter} instance with the given name and value.
	 */
	@Override
	protected Parameter getParameter(String name, Object value) {
		
		return new TemplateParameter(name, value);
	}
	
	/**
	 * This method extends {@link ParameterTest#equals(Parameter,Parameter)} and
	 * extends the field by field check to the <i>fixed</i> property.
	 * 
	 * @param	a {@link Parameter} instance, must be not {@literal null} and of
	 * 			type {@link TemplateParameter}.
	 * @param	a {@link Parameter} instance, must be not {@literal null} and of
	 * 			type {@link TemplateParameter}.
	 */
	@Override
	protected void equals(Parameter expected, Parameter actual) {
		
		super.equals(expected, actual);
		
		Assert.assertEquals(((TemplateParameter) expected).isFixed(),
							((TemplateParameter) actual).isFixed());
	}
	
}
