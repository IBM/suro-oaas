package com.ibm.au.optim.suro.model.entities.mapping;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.MappingType;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;


/**
 * Class <b>OutputMappingTest</b>. This class tests the implemented behaviour
 * of the {@link OutputMapping}. This class is used to store the information
 * about how to map the output of the optimisation backend into exports of it
 * that provide a particular view of the data.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class OutputMappingTest {

	/**
	 * A {@link Random} instance that can be used to introducing randomness
	 * in the tests to probe the value spaces.
	 */
	private static Random random = new Random();
    
	/**
	 * This method tests the behaviour of the default constructor of the class.
	 * The constructor is meant to initialise all the properties to the default
	 * values as defined by their underlying type.
	 */
    @Test
    public void testDefaultConstructor() {
    	
    	// Test 1. We ensure that the default constructor initialises all the
    	//         properties to the default values defined by their underlying
    	//         type.
    	//    	
        OutputMapping mapping = new OutputMapping();

        Assert.assertNull(mapping.getFileName());
        Assert.assertNull(mapping.getMappingType());
        Assert.assertNull(mapping.getSources());
        Assert.assertNull(mapping.getTransformer());
    }
    
    /**
     * This method tests the behaviour of the parameterised constructor that 
     * allows to set both the label of the mapping and the information about
     * the mapping type. The values passed to the constructor for these 
     * properties, should be retrieved through the corresponding getters of
     * the instance that has been initialised. All the other properties are
     * set to the default values of the underlying types.
     */
    @Test
    public void testConstructorWithLabelAndMappingType() {
    	
    	String expectedLabel = "thisIsLabel";
    	MappingType expectedType = this.getMappingType();

        OutputMapping mapping = new OutputMapping(expectedLabel, expectedType);
        Assert.assertEquals(expectedLabel, mapping.getFileName());
        Assert.assertEquals(expectedType, mapping.getMappingType());
        Assert.assertNull(mapping.getSources());
        Assert.assertNull(mapping.getTransformer());
    }
    
    /**
     * This method tests the implemented behaviour of the getter and setter
     * of the <i>mappingType</i> property. The method tests that by default
     * the property is set to {@literal null} and that what is set by the
     * setter can be retrieved by the getter. There are no constraints on 
     * the values that can be assigned to the property.
     */
    @Test
    public void testGetSetMappingType() {
        

    	
    	MappingType expectedMappingType = this.getMappingType();
    	
    	// Test 1. By default it is null.
    	//
    	OutputMapping mapping = new OutputMapping();
        Assert.assertNull(mapping.getMappingType());

        // Test 2. Can we get back what we set?
        //
        mapping.setMappingType(expectedMappingType);
        Assert.assertEquals(expectedMappingType, mapping.getMappingType());

        // Test 3. Can we set null?
        //
        mapping.setMappingType(null);
        Assert.assertNull(mapping.getMappingType());
    }

    
    /**
     * This method tests the implemented behaviour of the getter and setter
     * of the <i>fileName</i> property. The method tests that by default
     * the property is set to {@literal null} and that what is set by the
     * setter can be retrieved by the getter. There are no constraints on 
     * the values that can be assigned to the property.
     */
    @Test
    public void testGetSetFileName() {
    	
    	String expected = "ThisIsAFile.name";
    	
    	// Test 1. By default it is null. 
    	//
        OutputMapping mapping = new OutputMapping();
        Assert.assertNull(mapping.getFileName());

        // Test 2. Can we get what we set?
        //
        mapping.setFileName(expected);
        Assert.assertEquals(expected, mapping.getFileName());

        // Test 3. Can we set null?
        //
        mapping.setFileName(null);
        Assert.assertNull(mapping.getFileName());
    }

    
    /**
     * This method tests the implemented behaviour of the getter and setter
     * of the <i>source</i> property. The method tests that by default the
     * property is set to {@literal null} and that what is set by the setter
     * can be retrieved by the getter. There are no constraints on the values
     * that can be assigned to the property.
     */
    @Test
    public void testGetSetSource() {
    	
    	// Test 1. By default it is null.
    	//
        OutputMapping mapping = new OutputMapping();
        Assert.assertNull(mapping.getSources());

        // Test 2. Can we set an empty array?
        //
        mapping.setSources(new MappingSource[] {});
        Assert.assertEquals(0, mapping.getSources().length);

        // Test 3. Can we set null?
        //
        mapping.setSources(null);
        Assert.assertNull(mapping.getSources());

        // Test 4. Can we set a non empty array?
        //
        // 4a. one element.
        //
        
        MappingSource[] expected = new MappingSource[] { this.getMappingSource() };
        
        mapping.setSources(expected);
        MappingSource[] actual = mapping.getSources();
        Assert.assertNotNull(actual);
        Assert.assertEquals(1, actual.length);
        Assert.assertEquals(expected[0], actual[0]);

        // 4b. more elements.
        //
        int size = OutputMappingTest.random.nextInt(50);
        expected = new MappingSource[size];
        for(int i=0; i<size; i++) {
        	expected[i] = this.getMappingSource();
        }
        
        mapping.setSources(expected);
        actual = mapping.getSources();
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.length, actual.length);
        for(int i=0; i<expected.length; i++) {
        	
        	boolean found = false;
        	for(int j=0; j<actual.length; j++) {
        		
        		found = expected[i].equals(actual[j]);
        		if (found == true) {
        			break;
        		}
        	}
        	Assert.assertTrue(found);
        }
    }

    
    /**
     * This method tests the implemented behaviour of the getter and setter
     * of the <i>transformer</i> property. The method tests that by default
     * the property is set to {@literal null} and that what is set by the
     * setter can be retrieved by the getter. There are no constraints on 
     * the values that can be assigned to the property.
     */
    @Test
    public void testGetSetTransformer() {
    	
    	
    	String expected = "com.ibm.au.optim.suro.model.entities.mapping.DefaultTransformer";

    	// Test 1. By default null.
    	//
        OutputMapping mapping = new OutputMapping();
        Assert.assertNull(mapping.getTransformer());

        // Test 2. Can we get what we set?
        //
        mapping.setTransformer(expected);
        Assert.assertEquals(expected, mapping.getTransformer());

        // Test 3. Can we set null?
        //
        mapping.setTransformer(null);
        Assert.assertNull(mapping.getTransformer());
    }
    /**
     * This method tests the implemented behaviour of the {@link MappingOutput#clone()}.
     * The method is expected to create a deep copy of the instance. The method verifies
     * that the cloned instance passes the equality test.
     */
    @Test
    public void testClone() {
    	
    	// Test 1. We create a new instance and we check whether the instance
    	//		   is equivalent to the one that originally cloned it.
    	
    	OutputMapping expected = new OutputMapping();
    	OutputMapping actual = expected.clone();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(expected, actual);
    	
    	// Test 2. Ok now we setup some attributes and clone this again, to check
    	//         whether they're properly cloned.
    	
    	expected.setFileName("thisIsAFileName.log");
    	expected.setMappingType(MappingType.JSON_CATEGORY);
    	expected.setTransformer("com.ibm.au.optim.suro.model.entities.mapping.DefaultTransformer");
    	expected.setSources(new MappingSource[] { this.getMappingSource(), this.getMappingSource() });
    	
    	actual = expected.clone();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(expected, actual);
    	
    	
    	// Test 3. We change some of the properties, and see whether the clone
    	//         is still equal to the original. This should hold true the 
    	//         if the two references are not the same instance, which is the
    	//         purpose of clone.
    	
    	expected.setSources(null);
    	Assert.assertNotEquals(expected, actual);
    	
    }
	
    /**
     * This method tests the implemented behaviour of the {@link MappingOutput#equals(Object)}.
     * This method is expected to implement the equality test. The method should return {@literal 
     * false} if the given argument is {@literal null}, a different instance, or an instance of
     * the same type but with different values.
     */
	@Test 
	public void testEquals() {
		
		// Test 1. Obviousness test.. Is an instance equal to itself?
		//
		
		OutputMapping expected = new OutputMapping("filename.log", MappingType.KEY_TO_COLUMN);
		Assert.assertTrue(expected.equals(expected));
		
		// Test 2. If invoked with null, it should return false.
		//
		
		Assert.assertFalse(expected.equals(null));
		
		// Test 3. If invoked with an instance of an incompatible type it should return false.
		//
		
		Assert.assertFalse(expected.equals(new String("I am different.")));
		
		// Test 4. If we change properties and then clone the instance, the method should return
		//         true.
		
		expected.setTransformer("com.ibm.au.optim.suro.model.entities.mapping.DefaultTransformer");
		expected.setMappingType(MappingType.JSON_CATEGORY);
		expected.setSources(new MappingSource[] {});
		
		OutputMapping actual = expected.clone();
		Assert.assertTrue(expected.equals(actual));
		
		// Test 5. We change some of the properties, and we check that equals return false.
		//
		// 5a. the filename
		//
		expected.setFileName("anotherfile.log");
		Assert.assertNotEquals(expected.getFileName(), actual.getFileName());
		Assert.assertFalse(expected.equals(actual));
		
		// 5b. mapping type
		//
		actual.setFileName(expected.getFileName());
		Assert.assertTrue(expected.equals(actual));
		
		expected.setMappingType(MappingType.TRANSFORMER);
		Assert.assertNotEquals(expected.getMappingType(), actual.getMappingType());
		Assert.assertFalse(expected.equals(actual));
		
		// 5c. source
		//
		actual.setMappingType(expected.getMappingType());
		Assert.assertTrue(expected.equals(actual));
		
		expected.setSources(new MappingSource[] { this.getMappingSource(), this.getMappingSource() } );
		Assert.assertNotEquals(expected.getSources(), actual.getSources());
		Assert.assertFalse(expected.equals(actual));
	
		// 5d. transformer
		//
		actual.setSources(expected.getSources());
		Assert.assertTrue(expected.equals(actual));
		expected.setTransformer("com.ibm.optim.AnotherTransformer");
		Assert.assertNotEquals(expected.getTransformer(), actual.getTransformer());
		Assert.assertFalse(expected.equals(actual));
	}
	
	/**
	 * This method randomly generates an instance of {@link MappingSource}
	 * that can be used for the purpose of testing.
	 * 
	 * @return	a {@link MappingSource} randomly configured.
	 */
	protected MappingSource getMappingSource() {
		
		String label = "label" + OutputMappingTest.random.nextDouble();
		
		MappingSource source = new MappingSource(label);
		source.setSolutionKey("solution_" + OutputMappingTest.random.nextInt() + "_Final");

		// [CV] NOTE: we could add more settings here, if we like. The purpose is
		//            not to test MappingSource but OutputMapping.
		
		return source;
	}
	
	/**
	 * This is a utility method to randomise the selection of the specific
	 * value of the {@link MappingType} enumeration used for the purpose
	 * of testing.
	 * 
	 * @return	a {@link MappingType} value, randomly selected by the list
	 * 			of possible values.
	 */
	protected MappingType getMappingType() {
		
		
		int size = MappingType.values().length;
		MappingType[] values = MappingType.values();
		
		return values[OutputMappingTest.random.nextInt(size)];
	}
}
