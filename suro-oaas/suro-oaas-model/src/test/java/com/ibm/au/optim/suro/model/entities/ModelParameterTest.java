/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.ModelParameter.ParameterType;

/**
 * Class <b>ModelParameterTest</b>. This class extends {@link ParameterTest} and provides
 * the capability for testing the additional features added by the {@link ModelParameter}
 * to the {@link Parameter} base class, by still checking all the expected behaviour of
 *  the {@link Parameter} defined methods.
 * 
 * @author Christian Vecchiola
 *
 *
 */
public class ModelParameterTest extends ParameterTest {
	
	/**
	 * A {@link Random} instance used to randomise the generation of values for the properties
	 * under test.
	 */
	protected Random randome = new Random();
	/**
	 * A {@link ParameterTest} array that contains all the available types for the parameters.
	 */
	protected ParameterType[] types = new ParameterType[] { 
															ParameterType.BOOLEAN, 
															ParameterType.INT, 
															ParameterType.DOUBLE, 
															ParameterType.STRING 
														  };

	/**
	 * This method tests the implemented behaviour of the constructor. The constructor is
	 * expected to enforce the following constraints:
	 * <ul>
	 * <li><i>name</i> cannot be {@literal null} or an empty string.</li>
	 * <li><i>type</i> cannot be {@literal null}</li>
	 * <li><i>value</i> must be compatible with <i>type</i></li>
	 * <li><i>value</i> must be compatible with <i>range</i> (if defined)</li>
	 * <li><i>range</i> should be valid, consistent, and compatible with <i>type</i></li>
	 * <ul>
	 */
	@Test
	public void testConstructor() {

		// 1. checking for a null name.
		//
		try {
			
			ParameterType type = this.getParameterType(null);
			Object value = this.getValueFor(type, null, null);
			
			new ModelParameter(null, value, type, null, null);
			Assert.fail("ModelParameter(null,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, with a null name.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with a null name cannot be created.
		}
		
		
		// 2a. checking for an empty string as a name
		//
		try {
			
			ParameterType type = this.getParameterType(null);
			Object value = this.getValueFor(type, null, null);
			
			new ModelParameter("", value, type, null, null);
			Assert.fail("ModelParameter('',Object,ParameterType,Object[],Object) should throw IllegalArgumentException, with an empty name.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with a null name cannot be created.
		}
		
		
		// 2b. checking for a null type.
		//
		try {
			
			
			new ModelParameter("param", 34, null, null, null);
			Assert.fail("ModelParameter(String,Object,null,Object[],Object) should throw IllegalArgumentException, with a null parameter type.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with a null name cannot be created.
		}
		
		
		
		// 3. checking for incompatible types
		//
		
		ParameterType base = this.getParameterType(null);
		ParameterType other = this.getParameterType(base);
		Object value = this.getValueFor(other, null, null);
		
		try {

			new ModelParameter("param", value, base, null, null);
			Assert.fail("ModelParameter(String,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, when value is not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		
		// 4. checking for values of the range that are not compatible
		//    with the type.
		
		base = this.getParameterType(ParameterType.BOOLEAN);
		value = this.getValueFor(base, null, null);
		
		try {
			
			new ModelParameter("param", value, base, new Object[] {true, false}, null);
			Assert.fail("ModelParameter(String,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, when range values are not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		
		// 5. checking for values in the range that are not consistent by type.
		//
		
		base = this.getParameterType(ParameterType.BOOLEAN);
		value = this.getValueFor(base, null, null);
		
		try {
			
			new ModelParameter("param", value, base, new Object[] {true, ""}, null);
			Assert.fail("ModelParameter(String,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, when range values is not type consistent.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		// 6a. checking for values in the range that are not consistent in order (int).
		//
		
		Object lower = this.getValueFor(ParameterType.INT, null, null);
		Object upper = this.getValueFor(ParameterType.INT, ((Integer) lower) + 10, null);
		value = this.getValueFor(ParameterType.INT, lower, upper);
		
		try {
			
			new ModelParameter("param", value, ParameterType.INT, new Object[] {upper, lower}, null);
			Assert.fail("ModelParameter(String,Object,INT,Object[],Object) should throw IllegalArgumentException, when value is not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		// 6b. checking for values in the range that are not consistent in order (double).
		//
		
		lower = this.getValueFor(ParameterType.DOUBLE, null, null);
		upper = this.getValueFor(ParameterType.DOUBLE, ((Double) lower) + 10.0, null);
		value = this.getValueFor(ParameterType.DOUBLE, lower, upper);
		
		try {
			
			new ModelParameter("param", value, ParameterType.DOUBLE, new Object[] {upper, lower}, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE,Object[],Object) should throw IllegalArgumentException, when value is not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		// 7. checking for an invalid range (number of elements not 2).
		//
		
		ParameterType type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		lower = this.getValueFor(type,null,null);
		upper = this.getValueFor(type, lower, null);
		Object middle = this.getValueFor(type, lower, upper);
		value = this.getValueFor(type, lower, middle);
		
		try {
			new ModelParameter("param", value, type, new Object[] { lower, middle, upper }, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE | INT,Object[],Object) shoudl throw IllegalArgumentException, when the range is represented by more than two values.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good here, we have an exception.
			
		}
		
		// 8a. checking for values that are out of the range (< min)
		//
		
		type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		lower = this.getValueFor(type,null,null);
		upper = this.getValueFor(type, lower, null);
		middle = this.getValueFor(type, lower, upper);
		
		try {
			
			new ModelParameter("param", lower, type, new Object[] { middle, upper }, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE | INT,Object[],Object) shoudl throw IllegalArgumentException, when the value is out of range (< min).");
			
					
		} catch(IllegalArgumentException ilex) {
			
			// ok, we got this... the exception has been thrown.
		}
		
		// 8a. checking for values that are out of the range (> max)
		//
		
		type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		lower = this.getValueFor(type,null,null);
		upper = this.getValueFor(type, lower, null);
		middle = this.getValueFor(type, lower, upper);
		
		try {
			
			new ModelParameter("param", upper, type, new Object[] { lower, middle }, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE | INT,Object[],Object) shoudl throw IllegalArgumentException, when the value is out of range (> max).");
					
		} catch(IllegalArgumentException ilex) {
			
			// ok, we got this... the exception has been thrown.
		}
		
		// 9. Finally.... sunny day tests...
		//
		
		// 9a. we test a simple assignment that contains the value that is compatible with the type.
		//
		
		for(int i=0; i<this.types.length; i++) {

			ParameterType expectedType = this.types[i];
			Object expectedValue = this.getValueFor(expectedType, null, null);
			
			ModelParameter parameter = new ModelParameter("p", expectedValue, expectedType, null, null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(expectedValue, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getLowerBound());
			Assert.assertNull(parameter.getUpperBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		
		}
		
		// 9b. setting a null value...?
		//

		for(int i=0; i<this.types.length; i++) {


			ParameterType expectedType = this.types[i];
			ModelParameter parameter = new ModelParameter("p", null, expectedType, null, null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertNull(parameter.getValue());

			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getLowerBound());
			Assert.assertNull(parameter.getUpperBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		
		}
		
		ParameterType[] ordered = new ParameterType[] { ParameterType.INT, ParameterType.DOUBLE };
		
		// 9c. setting a value in range (< max)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			value = this.getValueFor(expectedType, null, upper);
			
			ModelParameter parameter = new ModelParameter("p", value, expectedType, new Object[] { null, upper },  null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(value, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getLowerBound());
			Assert.assertEquals(upper, parameter.getUpperBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		}
		
		// 9d. setting a value in range (> min)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			lower = this.getValueFor(expectedType, null, null);
			value = this.getValueFor(expectedType, lower, null);
			
			ModelParameter parameter = new ModelParameter("p", value, expectedType, new Object[] { lower, null },  null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(value, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getUpperBound());
			Assert.assertEquals(lower, parameter.getLowerBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		}
		
		// 9e. setting a value in range [min, max]
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			lower = this.getValueFor(expectedType, null, upper);
			value = this.getValueFor(expectedType, lower, upper);
			
			ModelParameter parameter = new ModelParameter("p", value, expectedType, new Object[] { lower, upper },  null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(value, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertEquals(upper, parameter.getUpperBound());
			Assert.assertEquals(lower, parameter.getLowerBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		}
		
		// 9f. setting a value in range (= min)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			lower = this.getValueFor(expectedType, null, upper);
			
			ModelParameter parameter = new ModelParameter("p", lower, expectedType, new Object[] { lower, upper },  null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(lower, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertEquals(lower, parameter.getLowerBound());
			Assert.assertEquals(upper, parameter.getUpperBound());
			Assert.assertEquals(parameter.getValue(), parameter.getLowerBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		}
		
		// 9g. setting a value in range (= max)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			lower = this.getValueFor(expectedType, null, upper);
			
			ModelParameter parameter = new ModelParameter("p", upper, expectedType, new Object[] { lower, upper },  null);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(upper, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertEquals(lower, parameter.getLowerBound());
			Assert.assertEquals(upper, parameter.getUpperBound());
			Assert.assertEquals(parameter.getValue(), parameter.getUpperBound());
			
			Assert.assertNull(parameter.getLabel());
			Assert.assertNull(parameter.getDescription());
		}
		
		// 9h. setting a compatible type (int --> double)
		//
		Integer intValue = new Integer(this.random.nextInt());
		
		ModelParameter parameter = new ModelParameter("p", intValue, ParameterType.DOUBLE, null, null);
		Object actualValue = parameter.getValue();
		Assert.assertEquals(Double.class, actualValue.getClass());
		Assert.assertEquals((int) intValue, ((Number) actualValue).intValue());
		

		Assert.assertEquals("p", parameter.getName());
		
		Assert.assertEquals(ParameterType.DOUBLE, parameter.getType());
		Assert.assertNull(parameter.getObjective());
		Assert.assertEquals(null, parameter.getLowerBound());
		Assert.assertEquals(null, parameter.getUpperBound());
		

		Assert.assertNull(parameter.getLabel());
		Assert.assertNull(parameter.getDescription());
		
		
	}
	
	
	/**
	 * This method tests the implemented behaviour of the constructor. The constructor is
	 * expected to enforce the following constraints:
	 * 
	 * <ul>
	 * <li><i>name</i> cannot be {@literal null} or an empty string.</li>
	 * <li><i>type</i> cannot be {@literal null}</li>
	 * <li><i>value</i> must be compatible with <i>type</i></li>
	 * <li><i>value</i> must be compatible with <i>range</i> (if defined)</li>
	 * <li><i>range</i> should be valid, consistent, and compatible with <i>type</i></li>
	 * <ul>
	 * 
	 * The test suite executed is almost the same as {@link ModelParameterTest#testConstructor()}
	 * but the constructor being tested is {@link ModelParameter#ModelParameter(String, Object, 
	 * ParameterType, Object[], String, String, String)}, which includes the two additional optional
	 * parameters <i>label</i> and <i>description</i>.
	 */
	@Test
	public void testConstructorWithFullParameter() {

		// 1. checking for a null name.
		//
		try {
			
			ParameterType type = this.getParameterType(null);
			Object value = this.getValueFor(type, null, null);
			
			new ModelParameter(null, value, type, null, null, null, null);
			Assert.fail("ModelParameter(null,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, with a null name.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with a null name cannot be created.
		}
		
		
		// 2a. checking for an empty string as a name
		//
		try {
			
			ParameterType type = this.getParameterType(null);
			Object value = this.getValueFor(type, null, null);
			
			new ModelParameter("", value, type, null, null, null, null);
			Assert.fail("ModelParameter('',Object,ParameterType,Object[],Object) should throw IllegalArgumentException, with an empty name.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with a null name cannot be created.
		}
		
		
		// 2b. checking for a null type.
		//
		try {
			
			
			new ModelParameter("param", 34, null, null, null, null, null);
			Assert.fail("ModelParameter(String,Object,null,Object[],Object) should throw IllegalArgumentException, with a null parameter type.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with a null name cannot be created.
		}
		
		
		
		// 3. checking for incompatible types
		//
		
		ParameterType base = this.getParameterType(null);
		ParameterType other = this.getParameterType(base);
		Object value = this.getValueFor(other, null, null);
		
		try {

			new ModelParameter("param", value, base, null, null, null, null);
			Assert.fail("ModelParameter(String,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, when value is not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		
		// 4. checking for values of the range that are not compatible
		//    with the type.
		
		base = this.getParameterType(ParameterType.BOOLEAN);
		value = this.getValueFor(base, null, null);
		
		try {
			
			new ModelParameter("param", value, base, new Object[] {true, false}, null, null, null);
			Assert.fail("ModelParameter(String,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, when range values are not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		
		// 5. checking for values in the range that are not consistent by type.
		//
		
		base = this.getParameterType(ParameterType.BOOLEAN);
		value = this.getValueFor(base, null, null);
		
		try {
			
			new ModelParameter("param", value, base, new Object[] {true, ""}, null, null, null);
			Assert.fail("ModelParameter(String,Object,ParameterType,Object[],Object) should throw IllegalArgumentException, when range values is not type consistent.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		// 6a. checking for values in the range that are not consistent in order (int).
		//
		
		Object lower = this.getValueFor(ParameterType.INT, null, null);
		Object upper = this.getValueFor(ParameterType.INT, ((Integer) lower) + 10, null);
		value = this.getValueFor(ParameterType.INT, lower, upper);
		
		try {
			
			new ModelParameter("param", value, ParameterType.INT, new Object[] {upper, lower}, null, null, null);
			Assert.fail("ModelParameter(String,Object,INT,Object[],Object) should throw IllegalArgumentException, when value is not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		// 6b. checking for values in the range that are not consistent in order (double).
		//
		
		lower = this.getValueFor(ParameterType.DOUBLE, null, null);
		upper = this.getValueFor(ParameterType.DOUBLE, ((Double) lower) + 10.0, null);
		value = this.getValueFor(ParameterType.DOUBLE, lower, upper);
		
		try {
			
			new ModelParameter("param", value, ParameterType.DOUBLE, new Object[] {upper, lower}, null, null, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE,Object[],Object) should throw IllegalArgumentException, when value is not compatible with type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good, we have verified that the parameter with an incompatible throws an exception.
		}
		
		
		// 7. checking for an invalid range (number of elements not 2).
		//
		
		ParameterType type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		lower = this.getValueFor(type,null,null);
		upper = this.getValueFor(type, lower, null);
		Object middle = this.getValueFor(type, lower, upper);
		value = this.getValueFor(type, lower, middle);
		
		try {
			new ModelParameter("param", value, type, new Object[] { lower, middle, upper }, null, null, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE | INT,Object[],Object) shoudl throw IllegalArgumentException, when the range is represented by more than two values.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok all good here, we have an exception.
			
		}
		
		// 8a. checking for values that are out of the range (< min)
		//
		
		type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		lower = this.getValueFor(type,null,null);
		upper = this.getValueFor(type, lower, null);
		middle = this.getValueFor(type, lower, upper);
		
		try {
			
			new ModelParameter("param", lower, type, new Object[] { middle, upper }, null, null, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE | INT,Object[],Object) shoudl throw IllegalArgumentException, when the value is out of range (< min).");
			
					
		} catch(IllegalArgumentException ilex) {
			
			// ok, we got this... the exception has been thrown.
		}
		
		// 8a. checking for values that are out of the range (> max)
		//
		
		type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		lower = this.getValueFor(type,null,null);
		upper = this.getValueFor(type, lower, null);
		middle = this.getValueFor(type, lower, upper);
		
		try {
			
			new ModelParameter("param", upper, type, new Object[] { lower, middle }, null, null, null);
			Assert.fail("ModelParameter(String,Object,DOUBLE | INT,Object[],Object) shoudl throw IllegalArgumentException, when the value is out of range (> max).");
					
		} catch(IllegalArgumentException ilex) {
			
			// ok, we got this... the exception has been thrown.
		}
		
		// 9. Finally.... sunny day tests...
		//
		
		// 9a. we test a simple assignment that contains the value that is compatible with the type.
		//

		String expectedLabel = "theLabel";
		String expectedDescription = "this is a very long description to the label of the parameter.";
		
		for(int i=0; i<this.types.length; i++) {

			ParameterType expectedType = this.types[i];
			Object expectedValue = this.getValueFor(expectedType, null, null);
			
			ModelParameter parameter = new ModelParameter("p", expectedValue, expectedType, null, null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(expectedValue, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getLowerBound());
			Assert.assertNull(parameter.getUpperBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		
		}
		
		// 9b. setting a null value...?
		//

		for(int i=0; i<this.types.length; i++) {


			ParameterType expectedType = this.types[i];
			ModelParameter parameter = new ModelParameter("p", null, expectedType, null, null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertNull(parameter.getValue());

			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getLowerBound());
			Assert.assertNull(parameter.getUpperBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		
		}
		
		ParameterType[] ordered = new ParameterType[] { ParameterType.INT, ParameterType.DOUBLE };
		
		// 9c. setting a value in range (< max)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			value = this.getValueFor(expectedType, null, upper);
			
			ModelParameter parameter = new ModelParameter("p", value, expectedType, new Object[] { null, upper },  null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(value, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getLowerBound());
			Assert.assertEquals(upper, parameter.getUpperBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		}
		
		// 9d. setting a value in range (> min)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			lower = this.getValueFor(expectedType, null, null);
			value = this.getValueFor(expectedType, lower, null);
			
			ModelParameter parameter = new ModelParameter("p", value, expectedType, new Object[] { lower, null },  null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(value, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertNull(parameter.getUpperBound());
			Assert.assertEquals(lower, parameter.getLowerBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		}
		
		// 9e. setting a value in range [min, max]
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			lower = this.getValueFor(expectedType, null, upper);
			value = this.getValueFor(expectedType, lower, upper);
			
			ModelParameter parameter = new ModelParameter("p", value, expectedType, new Object[] { lower, upper },  null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(value, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertEquals(upper, parameter.getUpperBound());
			Assert.assertEquals(lower, parameter.getLowerBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		}
		
		// 9f. setting a value in range (= min)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			lower = this.getValueFor(expectedType, null, upper);
			
			ModelParameter parameter = new ModelParameter("p", lower, expectedType, new Object[] { lower, upper },  null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(lower, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertEquals(lower, parameter.getLowerBound());
			Assert.assertEquals(upper, parameter.getUpperBound());
			Assert.assertEquals(parameter.getValue(), parameter.getLowerBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		}
		
		// 9g. setting a value in range (= max)
		for (int i=0; i<ordered.length; i++) {
			
			ParameterType expectedType = this.types[i];
			upper = this.getValueFor(expectedType, null, null);
			lower = this.getValueFor(expectedType, null, upper);
			
			ModelParameter parameter = new ModelParameter("p", upper, expectedType, new Object[] { lower, upper },  null, expectedLabel, expectedDescription);
			
			Assert.assertEquals("p", parameter.getName());
			Assert.assertEquals(upper, parameter.getValue());
			
			Assert.assertEquals(expectedType, parameter.getType());
			Assert.assertNull(parameter.getObjective());
			Assert.assertEquals(lower, parameter.getLowerBound());
			Assert.assertEquals(upper, parameter.getUpperBound());
			Assert.assertEquals(parameter.getValue(), parameter.getUpperBound());
			
			Assert.assertEquals(expectedLabel, parameter.getLabel());
			Assert.assertEquals(expectedDescription, parameter.getDescription());
		}
		
		// 9h. setting a compatible type (int --> double)
		//
		Integer intValue = new Integer(this.random.nextInt());
		
		ModelParameter parameter = new ModelParameter("p", intValue, ParameterType.DOUBLE, null, null, expectedLabel, expectedDescription);
		Object actualValue = parameter.getValue();
		Assert.assertEquals(Double.class, actualValue.getClass());
		Assert.assertEquals((int) intValue, ((Number) actualValue).intValue());
		

		Assert.assertEquals("p", parameter.getName());
		
		Assert.assertEquals(ParameterType.DOUBLE, parameter.getType());
		Assert.assertNull(parameter.getObjective());
		Assert.assertEquals(null, parameter.getLowerBound());
		Assert.assertEquals(null, parameter.getUpperBound());
		
		Assert.assertEquals(expectedLabel, parameter.getLabel());
		Assert.assertEquals(expectedDescription, parameter.getDescription());
		
		
	}

	/**
	 * This method tests the implemented behaviour of the setter and
	 * getter for the <i>label</i> property. The property is set by
	 * default to {@literal null} and there are no constraints on the
	 * values that the property can assume. The getter is expected to
	 * retrieve either the default value or what set with the setter.
	 */
	@Test
	public void testGetSetLabel() {

		ParameterType type = this.getParameterType(null);
		Object value = this.getValueFor(type, null, null);
		ModelParameter parameter = new ModelParameter("param", value, type, null, null);
		
		// Test 1. by default it should return null because this is
		//         what it is should be set to.
		//
		Assert.assertNull(parameter.getLabel());
		
		// Test 2. we set the value and we check whether we can get
		//         it back from the getter.
		//
		String expected = "this is a label";
		parameter.setLabel(expected);
		String actual = parameter.getLabel();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we set null?
		//
		//
		parameter.setLabel(null);
		Assert.assertNull(parameter.getLabel());

	}
	
	/**
	 * This method tests the implemented behaviour of the setter and
	 * getter for the <i>description</i> property. The property is set 
	 * by default to {@literal null} and there are no constraints on 
	 * the values that the property can assume. The getter is expected
	 * to retrieve either the default value or what set with the setter.
	 */
	@Test
	public void testGetSetDescription() {

		ParameterType type = this.getParameterType(null);
		Object value = this.getValueFor(type, null, null);
		ModelParameter parameter = new ModelParameter("param", value, type, null, null);
		
		// Test 1. by default it should return null because this is
		//         what it is should be set to.
		//
		Assert.assertNull(parameter.getDescription());
		
		// Test 2. we set the value and we check whether we can get
		//         it back from the getter.
		//
		String expected = "this is a label";
		parameter.setDescription(expected);
		String actual = parameter.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we set null?
		//
		//
		parameter.setDescription(null);
		Assert.assertNull(parameter.getDescription());
	}
	
	/**
	 * This method tests the implementation of the setter and getter 
	 * methods for the <i>objective</i> property. The property is by
	 * default set to {@literal null} and it is expected that the value
	 * provided with the setter method, is the one that will be retrieved
	 * by the getter method.
	 */
	@Test
	public void testGetSetObjective() {
		
		// 1. First test, let's check that by default the getter returns
		//	  the value the ModelParameter was initialised with for what
		//    regards the objective.
		//
		ParameterType type = this.getParameterType(null);
		Object value = this.getValueFor(type, null, null);
		String expected = "objective";
		ModelParameter parameter = new ModelParameter("param", value, type, null, expected);
		String actual = parameter.getObjective();
		Assert.assertEquals(expected, actual);
		
		
		// 2. Test 2, let's check whether we can set null values and we
		//    get them back.
		//
		parameter.setObjective(null);
		Assert.assertNull(parameter.getObjective());
		
		
		// 3. Test 3, let's check whether we can assign a different value
		//    that is not null.
		//
		expected = "anotherObjective";
		parameter.setObjective(expected);
		actual = parameter.getObjective();
		Assert.assertEquals(expected, actual);
		
	}
	
	/**
	 * This method tests the implementation of the setter and getter of
	 * the range property. The property is meant to be compliant with the
	 * current type, composed by only 2 elements, where the first needs to
	 * be smaller than the second (except when one of the two or both are
	 * {@literal null}. Moreover, the range also need to include the current
	 * value for the parameter. A violation of any of these conditions should
	 * raise a {@link IllegalArgumentException}.
	 */
	@Test
	public void testGetSetRange() {
		
		ParameterType type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		Object value = this.getValueFor(type, null, null);
		
		// 1. First test, let's check that the getter returns the value that
		//    we originally set.
		//
		// 1a. null values.
		//
		Object[] range = null;
		ModelParameter parameter = new ModelParameter("param", value, type, range, null);
		Object[] actual = parameter.getRange();
		Assert.assertNull(actual);
		Assert.assertNull(parameter.getLowerBound());
		Assert.assertNull(parameter.getUpperBound());
		
		// 1b. a range of values. [k1, +oo)
		//
		Object expectedLower = value;
		value = this.getValueFor(type, expectedLower, null);
		parameter = new ModelParameter("param", value, type, new Object[] { expectedLower, null }, null);
		Object actualLower = parameter.getLowerBound();
		Assert.assertEquals(expectedLower, actualLower);
		Assert.assertNull(parameter.getUpperBound());
		actual = parameter.getRange();
		Assert.assertNotNull(actual);
		Assert.assertEquals(2, actual.length);
		Assert.assertEquals(expectedLower, actual[0]);
		Assert.assertNull(actual[1]);
		
		// 1c. another range of values. (-oo, k2]
		//
		Object expectedUpper = this.getValueFor(type, value, null);
		parameter = new ModelParameter("param", value, type, new Object[] { null, expectedUpper }, null);
		Object actualUpper = parameter.getUpperBound();
		Assert.assertEquals(expectedUpper, actualUpper);
		Assert.assertNull(parameter.getLowerBound());
		actual = parameter.getRange();
		Assert.assertNotNull(actual);
		Assert.assertEquals(2, actual.length);
		Assert.assertEquals(expectedUpper, actual[1]);
		Assert.assertNull(actual[0]);
		
		// 1d. a closed range of values [k1, k2]
		//
		parameter = new ModelParameter("param", value, type, new Object[] { expectedLower, expectedUpper }, null);
		actualLower = parameter.getLowerBound();
		Assert.assertEquals(expectedLower, actualLower);
		actualUpper = parameter.getUpperBound();
		Assert.assertEquals(expectedUpper, actualUpper);
		actual = parameter.getRange();
		Assert.assertNotNull(actual);
		Assert.assertEquals(2, actual.length);
		Assert.assertEquals(expectedLower, actual[0]);
		Assert.assertEquals(expectedUpper, actual[1]);
		
		
		// 2. Let's now test that the setter and the getter are now working as expected.
		//
		
		// we first test that on a good setup, we can set the range to null.
		//
		type = this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE;
		value = this.getValueFor(type, null, null);
		expectedUpper = this.getValueFor(type, value, null);
		expectedLower = this.getValueFor(type, null, value);
		parameter = new ModelParameter("param", value, type, new Object[] { expectedLower, expectedUpper }, null);
		
		parameter.setRange(null);
		Assert.assertNull(parameter.getLowerBound());
		Assert.assertNull(parameter.getUpperBound());
		actual = parameter.getRange();
		if (actual != null) {
			
			Assert.assertEquals(2, actual.length);
			Assert.assertNull(actual[0]);
			Assert.assertNull(actual[1]);
		}
		
		// ok let's try: [k1, +oo)
		//
		parameter.setRange(new Object[] { expectedLower, null });
		Assert.assertEquals(expectedLower, parameter.getLowerBound());
		Assert.assertNull(parameter.getUpperBound());
		actual = parameter.getRange();
		Assert.assertNotNull(actual);
		Assert.assertEquals(2, actual.length);
		Assert.assertEquals(expectedLower, actual[0]);
		Assert.assertNull(actual[1]);
		
		// now let's try: (-oo, k2]
		//
		parameter.setRange(new Object[] { null, expectedUpper });
		Assert.assertEquals(expectedUpper, parameter.getUpperBound());
		Assert.assertNull(parameter.getLowerBound());
		actual = parameter.getRange();
		Assert.assertNotNull(actual);
		Assert.assertEquals(2, actual.length);
		Assert.assertEquals(expectedUpper, actual[1]);
		Assert.assertNull(actual[0]);
		
		// ok, another attempt: (-oo, +oo)
		//
		parameter.setRange(new Object[] { null, null });
		Assert.assertNull(parameter.getLowerBound());
		Assert.assertNull(parameter.getUpperBound());
		actual = parameter.getRange();
		if (actual != null) {
			
			Assert.assertEquals(2, actual.length);
			Assert.assertNull(actual[0]);
			Assert.assertNull(actual[1]);
		}
		
		
		// 3. value incompatibility
		//
		
		// 3a. invalid range (bounds) ... 
		//
		try {
			
			parameter.setRange(new Object[] { expectedUpper, expectedLower });
			Assert.fail("ModelParameter.setRange(Object[]) should throw an IllegalArgumentException when bounds are inverted [" + expectedUpper + ", " + expectedLower + "]");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok we're good to go.
		}
		
		
		value = parameter.getValue();
		Object newLower = this.getValueFor(type, value, null);
		
		try {
			
			parameter.setRange(new Object[]{ newLower, null });
			Assert.fail("ModelParameter.setRange(Object[]) should throw an IllegalArgumentException when the value is not in the new range to be set.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok we're good to go...
		}
		
		// 3b. invalid range (size) ....
		//
		try {
			parameter.setRange(new Object[] { expectedLower, value, expectedUpper });
			Assert.fail("ModelParameter.setRange(Object[]) should throw an IllegalArgumentException when the array has more than two elements");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok we're good to go...
		}
		
		try {
			
			parameter.setRange(new Object[] { expectedLower });
			Assert.fail("ModelParameter.setRange(Object[]) should throw an IllegalArgumentException when the array has one element.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok we're good to go...
		}
		
		try {
			
			parameter.setRange(new Object[] {});
			Assert.fail("ModelParameter.setRange(Object[]) should throw an IllegalArgumentException when the array has no elements.");
			
			
		} catch(IllegalArgumentException ilex) {
			
			// ok we're good to go...
			
		}
		
		// 3c. invalid value types.
		//
		try {
			
			parameter.setRange(new Object[] { false, true });
			Assert.fail("ModelParameter.setRange(Object[]) should throw an IllegalArgumentExcepton when the array bounds are of different type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok we're good to go....
		}
		
		
	}
	
	/**
	 * This method tests the implementation of the setter and the getter of the
	 * <i>type</i> property. This property is meant to define type constraints
	 * on the parameter. When set if the previous value of the parameter (and
	 * the corresponding range if any) is not compatible, it will be erased (as
	 * well as the range).
	 */
	@Test
	public void testGetSetType() {

		ParameterType expected = this.getParameterType(null);
		Object value = this.getValueFor(expected, null, null);
		
		// 1. very first test, we check that the getter returns what was originally
		//    configured with in the absence of any other operation.
		//
		ModelParameter parameter = new ModelParameter("param", value, expected, null, null);
		
		ParameterType actual = parameter.getType();
		Assert.assertEquals(expected, actual);
		
		
		// 2. we set the type and we ensure that:
		//    1. that is the type we retrieve
		//    2. if the value is incompatible with the type, the value is set to null.
		//    3. the new type constraint is enforced if we try to set a new value.
		
		// this method ensures that we do not get a compatible type.
		//
		ParameterType different = this.getParameterType(expected);
		parameter.setType(different);
		actual = parameter.getType();
		Assert.assertEquals(different, actual);
		
		// next thing to check is whether the valued has been set to null.
		Object newValue = parameter.getValue();
		Assert.assertNull(newValue);
		Assert.assertNull(parameter.getRange());
		
		// third thing is to check whether by setting the old value we get
		// an exception, because incompatible type.
		
		try {
			
			parameter.setValue(value);
			Assert.fail("ModelParameter.setValue(Object) should throw IllegalArgumentException if the type constraint is not verified.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go.
		}
		
		// 3. we check whether the method setType throws an exception if
		//    we pass a null argument.
		//
		
		try {
			
			parameter.setType(null);
			Assert.fail("ModelParameter.setType(null) should throw IllegalArgumentException.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go.
		}
		
		
		
	}
	
	/**
	 * This method tests the implementation of the setter and the getter of the
	 * <i>value</i> property. The following scenarios should raise an {@link 
	 * IllegalArgumentException}:
	 * <ul>
	 * <li><i>value</i> must be compatible with <i>type</i></li>
	 * <li><i>value</i> must be compatible with <i>range</i> (if defined)</li>
	 * <ul>
	 */
	@Test
	public void testGetSetValue() {

		ParameterType type = this.getParameterType(null);
		Object expected = this.getValueFor(type, null, null);

		// 1. Sunny day test, we set a value from the constructor and we check that
		//	  this is returned by the getter. Test executed with a valid value.
		//
		ModelParameter parameter = new ModelParameter("param", expected, type, null, null);
		Object actual = parameter.getValue();
		Assert.assertEquals(expected, actual);
		
		// 2. Changing value to {@link null}
		//
		parameter.setValue(null);
		actual = parameter.getValue();
		Assert.assertNull(actual);
		
		// 3. Setting a value that it is not compatible with
		//    the current type.
		//
		ParameterType anotherType = this.getParameterType(type);
		Object another = this.getValueFor(anotherType, null, null);
		try {
			
			parameter.setValue(another);
			Assert.fail("ModelParameter.setValue(Object) should throw IllegalArgumentException when the value is of incompatible type.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, we're good...
		}
		
		// 4. Setting values that are not compatible with the range.
		//
		type = (this.random.nextBoolean() ? ParameterType.INT : ParameterType.DOUBLE);
		Object expectedLower = this.getValueFor(type, null, null);
		Object expectedUpper = this.getValueFor(type, expectedLower, null);
		Object value = this.getValueFor(type, expectedLower, expectedUpper);
		
		parameter = new ModelParameter("param", value, type, new Object[] { expectedLower, expectedUpper }, null);
		value = this.getValueFor(type, null, expectedLower);
		try {
			
			parameter.setValue(value);
			Assert.fail("ModelParameter.setValue(Object) shoud throw IllegalArgumentException when the value is outside the range (< min).");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, we're good...
		}
		
		value = this.getValueFor(type, expectedUpper, null);
		try {
			
			parameter.setValue(value);
			Assert.fail("ModelParameter.setValue(Object) shoud throw IllegalArgumentException when the value is outside the range (> max).");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, we're good...
		}
		
	}

    /**
     * This method tests the implemented behaviour of {@link ModelParameter#clone()}.
     * The method is expected to clone complex properties and to shallow copy immutable 
     * properties or simple ones.
     */
	@Override
	@Test
    public void testClone() {
		
		super.testClone();
		
		// the above takes care of the standard properties (name, and value)
		// we now create a couple of instances and check the ModelParameter
		// specific properties to verify whether they are properly cloned.
		
		ModelParameter expected = new ModelParameter("m1", 34, ParameterType.INT, new Object[] { 12, 100 }, "o1");
    	ModelParameter actual = (ModelParameter) expected.clone();
    	Assert.assertNotNull(actual);
    	this.equals(expected, actual);
    	
    	expected.setType(ParameterType.DOUBLE);
    	expected.setRange(new Object[] { null, null});
    	actual = (ModelParameter) expected.clone();
    	Assert.assertNotNull(actual);
    	this.equals(expected, actual);
    	
    	expected.setRange(new Object[] { null, 50.0 });
    	actual = (ModelParameter) expected.clone();
    	Assert.assertNotNull(actual);
    	this.equals(expected, actual);
    	
    	
    	expected.setRange(new Object[] { 12.1, null });
    	actual = (ModelParameter) expected.clone();
    	Assert.assertNotNull(actual);
    	this.equals(expected, actual);
    	
    	
    	
    }
	
	/**
	 * This method returns a randomly selected parameter type. If the
	 * value of <i>other</i> is non {@literal null}, this method will
	 * ensure that the parameter type returned is not compatible with
	 * the given type.
	 * 
	 * @return one of the {@link ParameterType} values.
	 */
	protected ParameterType getParameterType(ParameterType other) {
		
		// we ensure that we get a number that can be used as an index
		// for the array of types.
		//
		int index = Math.abs(this.random.nextInt(this.types.length));
		
		if (other != null) {
			
			for(int i=0; i<this.types.length; i++) {
				
				if (this.types[i] == other) {
					
					// we sum 2 to be sure that in the case of
					// INT we never get DOUBLE and viceversa.
					//
					index = (i + 2) % this.types.length;
				}
			}
		}
		
		return this.types[index];
	}
	
	/**
	 * Generates a random value of the given <i>type</i> within the given
	 * constraints. The range values are ignored if the type is either a
	 * {@link ParameterType#STRING} or {@link ParameterType#BOOLEAN}.
	 * 
	 * @param type	a {@link ParameterType} value specifying the type of the
	 * 				value that needs to be created.
	 * @param min	a {@link Object} reference that if not null, defines the
	 * 				lower limit of the range of allowed parameter. It can be
	 * 				{@literal null}, but if not its value must be compatible
	 * 				with the desired type.
	 * @param max	a {@link Object} reference that if not null, defines the
	 * 				upper limit of the range of allowed parameter. It can be
	 * 				{@literal null}, but if not its value must be compatible
	 * 				with the desired type.
	 * 
	 * @return	an {@link Object} reference that is compatible with <i>type</i>
	 * 			and whose value is within the defined <i>range</i> if any.
	 */
	protected Object getValueFor(ParameterType type, Object min, Object max) {
		
		Object value = null;
		
		switch(type) {
		
			case STRING:	{
				
				String minString = (min == null ? "" : (String) min);
				String maxString = (String) max;
				String midString = null;
				
				// now that we know, that min will never be null...
				// we can easily do the following:
				
				if (maxString == null) {
					
					midString = minString + "sfbweiasnfjeaq";
					
				} else {
					
					int comparison = minString.compareTo(maxString);
					
					if (comparison > 0) {
						
						Assert.fail("[getValueFor] - Min and max parameters are inconsisten [min: " + min + ", max: " + max + "].");
					}
					
					if (comparison == 0) {
						
						midString = minString;
					
					} else {
						
						int s1 = minString.length();
						int s2 = maxString.length();
						int sm = s1 < s2 ? s1 : s2; 
						int pivot = sm;
						char next = '\0';
						
						for(int i=0; i<sm; i++) {
							
							char c1 = minString.charAt(i);
							char c2 = maxString.charAt(i);
							
							int delta = c2 - c1;
							if (delta > 1) {
								pivot = i;
								next = c2;
								break;
							}
						}
						
						// this is the minimum common string,
						//
						midString = minString.substring(0,pivot);
							
						
						// now we need to look at the remainder
						// of maxString and check how we can 
						// make a string that stays in between
						// the empty string and the remainder.
						
						midString = midString + (char) ((int) (next) - 1);
						
					}
				}
				
				value = midString;
				
			} break;
			case BOOLEAN:	{
				
				Boolean minBoolean = (min == null ? Boolean.FALSE : (Boolean) min);
				Boolean maxBoolean = (max == null ? Boolean.TRUE : (Boolean) max);
				
				if (minBoolean.compareTo(maxBoolean) > 0) {
					Assert.fail("[getValueFor] - Min and max parameters are inconsisten [min: " + min + ", max: " + max + "].");
				}
				
				if (minBoolean.equals(maxBoolean)) {
					
					// both upper and lower are the same in this case.
					//
					value = minBoolean;
				
				} else {
					
					// if we have a proper range, than any boolean
					// would do.
					//
					value = this.random.nextBoolean();
				}
				
			} break;
			case INT:		{
				
				Integer minInt = (min == null ? (Integer.MIN_VALUE) : (Integer) min);
				Integer maxInt = (max == null ? (Integer.MAX_VALUE) : (Integer) max);
				
				if (minInt > maxInt) {
					
					Assert.fail("[getValueFor] - Min and max parameters are inconsisten [min: " + min + ", max: " + max + "].");	
				}
				
				// dividing maxInt and minInt by 2 ensures that the resulting
				// number will always be smaller than the maximum integer
				// number that can be represented.
				//
				Integer delta = (maxInt / 2) - (minInt / 2);
				
				// delta would be used to collect the remainder of the division
				// of the absolute part of the random number. This ensures that
				// the number will always be constrained by 0 and delta - 1. We
				// then add one unit to delta, to be sure that we can cover the
				// entire range.
				
				delta = delta + 1;
				
				// because we have divided by two the two numbers their difference
				// is also divided by two, which means that delta now captures only
				// half of the range that we would like to distributed the numbers
				// in. Therefore, we add it twice to the minimum value.
				
				value = minInt + (Math.abs(this.random.nextInt()) % delta) + 
								 (Math.abs(this.random.nextInt()) % delta);
				
				
				
			} break;
			case DOUBLE: 	{
				
				Double minDouble = (min == null ? Double.MIN_VALUE : (Double) min);
				Double maxDouble = (max == null ? Double.MAX_VALUE : (Double) max);
				

				if (minDouble > maxDouble) {
					
					Assert.fail("[getValueFor] - Min and max parameters are inconsisten [min: " + min + ", max: " + max + "].");	
				}
				
				
				// ok we have both ranges...
				
				value = this.random.nextDouble() * (maxDouble - minDouble) + minDouble;
				
			
				
				
			} break;
		}
		
		return value;
	}
	
	/**
	 * This is an extension method that will be redefined by inherited test classes that can be 
	 * used to create a specific instance of the {@link Parameter} class. The reason why we abstract
	 * the creation of the instance is because we can then easily implements all the tests and 
	 * constraints to any class that inherits from {@link Parameter} by creating test classes 
	 * inheriting from this test class.
	 * 
	 * @return	a {@link ModelParameter} instance, the name is set {@link Parameter#DEFAULT_NAME},
	 * 			the type is set to {@link ParameterType#STRING}, the value to {@literal null} and
	 * 			so are the range and the objective.
	 */
	protected Parameter getParameter() {
		
		return new ModelParameter(Parameter.DEFAULT_NAME, null, ParameterType.INT, null, null);
	}
	
	/**
	 * Factory method for creating a parameter with a given name a default value. This
	 * method creates an instance of {@link ModelParameter} and configures the value
	 * to {@literal null} and the mapped type to {@link ParameterType#INT}.
	 * 
	 * @param name	a {@link String} representing the name of the parameter. It cannot
	 * 				be {@literal null} or an empty string.
	 * 
	 * @return a {@link ModelParameter} instance with the given name.	
	 */
	protected Parameter getParameter(String name) {
		
		return new ModelParameter(name, null, ParameterType.INT, null, null);
	}

	
	/**
	 * Factory method for creating a parameter with a given name  and value. The method
	 * inspect the type of the value and creates a {@link ModelParameter} instance with
	 * the corresponding {@link ParameterType} if mapped. If the value is set to {@literal 
	 * null} the default type is {link ParameterType#INT}.
	 * 
	 * @param name	a {@link String} representing the name of the parameter. It cannot
	 * 				be {@literal null} or an empty string.
	 * 
	 * @param value	a {@link Object} reference representing the value to set for the
	 * 				parameter.
	 * 
	 * @return a {@link ModelParameter} instance with the given name and value.	
	 */
	protected Parameter getParameter(String name, Object value) {
		
		ParameterType type = ParameterType.INT;
		
		if (value != null) {
			
			Class<?> clazz = value.getClass();
			if (clazz == Boolean.class) {
				
				type = ParameterType.BOOLEAN;
				
			} else if (clazz == Integer.class) {
				
				type = ParameterType.INT;
				
			} else if (clazz == Double.class) {
				
				type = ParameterType.DOUBLE;
				
			} else if (clazz == String.class) {
				
				type = ParameterType.STRING;
			}
		}
		
		return new ModelParameter(name, value, type, null, null);
	}
	
	/**
	 * Factory method for creating a value for a parameter. The method returns
	 * {@literal null} to be sure that it is compatible with any possible type
	 * that is being set for the corresponding {@link ModelParameter} instance.
	 * 
	 * @return	a {@link Object} reference representing the value of the parameter
	 * 			chosen for the test. The value is {@literal null}.
	 */
	protected Object getValue() {
		
		return null;
	}
	
	/**
	 * This method extends {@link ParameterTest#equals(Parameter,Parameter)} and
	 * extends the field by field check to the <i>fixed</i> property.
	 * 
	 * @param	a {@link Parameter} instance, must be not {@literal null} and of
	 * 			type {@link ModelParameter}.
	 * @param	a {@link Parameter} instance, must be not {@literal null} and of
	 * 			type {@link ModelParameter}.
	 */
	@Override
	protected void equals(Parameter expected, Parameter actual) {
		
		super.equals(expected, actual);
		
		ModelParameter me = (ModelParameter) expected;
		ModelParameter ma = (ModelParameter) actual;
		
		Assert.assertEquals(me.getType(), ma.getType());
		Assert.assertEquals(me.getLowerBound(), ma.getLowerBound());
		Assert.assertEquals(me.getUpperBound(), ma.getUpperBound());
		Assert.assertEquals(me.getLabel(), ma.getLabel());
		Assert.assertEquals(me.getDescription(), ma.getDescription());
		
	}
}
