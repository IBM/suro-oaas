package com.ibm.au.optim.suro.model.entities.mapping;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.mapping.MappingType;


/**
 * Class <b>MappingTypeTest</b>. This class tests the definition of the {@link MappingTest}
 * enumeration. Essentially it checks that the underlying {@link String} values of the enum
 * are as expected and that they are mapped to the proper enum constants.
 * 
 * @author Peter Ilfrich
 */
public class MappingTypeTest {

	/**
	 * This method tests that each of the corresponding {@link String} values are mapped
	 * to the appropriate enum constants defined in the type, by using the method {@link 
	 * MappingType#getType(String)}. Moreover, the method also tests that the method does 
	 * not return a {@link MappingType} value for either {@link null} or a {@link String}
	 * value that is unknown.
	 */
	@Test
    public void testGetByLabel() {
    	Assert.assertEquals(MappingType.COMPLEX, MappingType.getType("complex"));
    	Assert.assertEquals(MappingType.COMPLEX_APPEND, MappingType.getType("complex-append"));

    	Assert.assertEquals(MappingType.JSON_CATEGORY, MappingType.getType("json-category"));
    	Assert.assertEquals(MappingType.KEY_TO_COLUMN, MappingType.getType("key-to-column"));

    	Assert.assertEquals(MappingType.TRANSFORMER, MappingType.getType("transformer"));
    	Assert.assertNull(MappingType.getType("foobar"));
    	Assert.assertNull(MappingType.getType(null));
    }
	/**
	 * This method tests the implementation of the {@link MappingType#toString()} method. 
	 * This method is meant to return the value of the underlying {@link String} instance
	 * mapped to the enumeration constant.
	 */
	@Test
    public void testToString() {
    	Assert.assertEquals("complex", MappingType.COMPLEX.toString());
        Assert.assertEquals("complex-append", MappingType.COMPLEX_APPEND.toString());
        Assert.assertEquals("json-category", MappingType.JSON_CATEGORY.toString());
        Assert.assertEquals("key-to-column", MappingType.KEY_TO_COLUMN.toString());
        Assert.assertEquals("transformer", MappingType.TRANSFORMER.toString());
    }

	/**
	 * This method tests the implementation of the {@link MappingType#valueOf(String)} method.
	 * The method is expected to return the corresponding {@link MappingType} value that matches
	 * the name of the constant that defines the value.
	 */
	@Test
    public void testValueOf() {
    	Assert.assertEquals(MappingType.COMPLEX, MappingType.valueOf("COMPLEX"));
        Assert.assertEquals(MappingType.COMPLEX_APPEND, MappingType.valueOf("COMPLEX_APPEND"));
        Assert.assertEquals(MappingType.JSON_CATEGORY, MappingType.valueOf("JSON_CATEGORY"));
        Assert.assertEquals(MappingType.KEY_TO_COLUMN, MappingType.valueOf("KEY_TO_COLUMN"));
        Assert.assertEquals(MappingType.TRANSFORMER, MappingType.valueOf("TRANSFORMER"));
    }

	/**
	 * This method tests the implementation of the {@link MappingType#values()}. This method is
	 * expected to return an array of {@link MappingType} elements that includes all the possible
	 * values of the enumeration.
	 */
    @Test
    public void testValues() {
    	
    	MappingType[] expectedValues = { MappingType.COMPLEX, MappingType.COMPLEX_APPEND, MappingType.JSON_CATEGORY, MappingType.KEY_TO_COLUMN, MappingType.TRANSFORMER };
    	MappingType[] actualValues = MappingType.values();
    	Assert.assertNotNull(actualValues);
    	
    	Assert.assertEquals(expectedValues.length, actualValues.length);
    	for(int i=0; i<expectedValues.length; i++) {
    		boolean found = false;
    		for(int j=0; j<actualValues.length; j++) {
    			
    			found = (expectedValues[i] == actualValues[j]);
    			if (found == true) {
    				break;
    			}
    		}
    		Assert.assertTrue(found);
    	}
    }
}
