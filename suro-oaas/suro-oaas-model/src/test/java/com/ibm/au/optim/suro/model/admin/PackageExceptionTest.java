/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import org.junit.Test;

import org.junit.Assert;

/**
 * Class <b>PackageExceptionTest</b>. This class tests the implemented behaviour
 * of the {@link PackageException} class. This class is a simple extension of the
 * {@link Exception} class, that replicates the constructors of the base class.
 * Therefore, this class  simply tests that the different values are passed to 
 * the base class as expected during the initialisation process.
 * 
 * @author Christian Vecchiola
 *
 */
public class PackageExceptionTest {

	/**
	 * This class tests the implemented behaviour of the default constructor. The
	 * expected behaviour for this constructor is to initialise the base class with
	 * the default values, which are {@literal null} values for both <i>message</i>
	 * and <i>cause</i>.
	 */
	@Test
	public void testDefaultConstructor() {
		
		PackageException error = new PackageException();
		Assert.assertNull(error.getMessage());
		Assert.assertNull(error.getCause());
		
	}
	/**
	 * This class tests the implemented behaviour of the constructor {@link PackageException#PackageException(String)}
	 * constructor. The expected behaviour is to retrieve from {@link PackageException#getMessage()} whatever instance
	 * has been passed to the constructor. There are no restrictions on the values that the parameter can assume such
	 * as {@literal null} values.
	 */
	@Test
	public void testConstructorWithMessage() {

		// Test 1. Sunny day scenario, non null value or an empty string.
		//
		String expected = "This is an exception";
		PackageException error = new PackageException(expected);
		String actual = error.getMessage();
		Assert.assertEquals(expected, actual);
		
		// Test 2. Edge case, null value for the message.
		//
		expected = "";
		error = new PackageException(expected);
		actual = error.getMessage();
		Assert.assertEquals(expected, actual);
		
		// Test 3. Edge case, an empty string for the message.
		//
		expected = null;
		error = new PackageException(expected);
		actual = error.getMessage();
		Assert.assertNull(actual);
		
	}
	/**
	 * This class tests the implemented behaviour of the constructor {@link PackageException#PackageException(String, Throwable)}
	 * constructor. The expected behaviour is to retrieve the values passed as arguments to the constructor from the corresponding
	 * getters. There are no restrictions on the values that can be passed to both parameters, including {@literal null} values.
	 */
	@Test
	public void testConstructorWithMessageAndCause() {
		
		// Test 1. Sunny day scenario, both non null values.
		//
		String expectedMessage = "This is an exception.";
		Throwable expectedCause = new Exception("This is the real cause");
		
		PackageException error = new PackageException(expectedMessage, expectedCause);
		String actualMessage = error.getMessage();
		Assert.assertEquals(expectedMessage, actualMessage);
		Throwable actualCause = error.getCause();
		Assert.assertEquals(expectedCause, actualCause);
		
		// Test 2. Null value for the cause.
		//
		error = new PackageException(expectedMessage, null);
		actualMessage = error.getMessage();
		Assert.assertEquals(expectedMessage, actualMessage);
		actualCause = error.getCause();
		Assert.assertNull(actualCause);
		
		// Test 3. Null value for both parameters.
		//
		error = new PackageException(null, null);
		actualMessage = error.getMessage();
		Assert.assertNull(actualMessage);
		actualCause = error.getCause();
		Assert.assertNull(actualCause);
		
		// Test 4. Null value for the message.
		//
		error = new PackageException(null, expectedCause);
		actualMessage = error.getMessage();
		Assert.assertNull(actualMessage);
		actualCause = error.getCause();
		Assert.assertEquals(expectedCause, actualCause);
		
		
	}
}
