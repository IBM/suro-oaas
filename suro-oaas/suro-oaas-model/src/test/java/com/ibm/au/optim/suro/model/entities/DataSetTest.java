package com.ibm.au.optim.suro.model.entities;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.DataSet;

/**
 * Class <b>DataSetTest</b>. This class tests the implemented behaviour
 * of the {@link DataSet} class. This is a very small collection of 
 * metadata around the DAT file used to compose the submission to the
 * optimisation engine.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class DataSetTest extends EntityTest {

	
	/**
	 * This method tests the {@link DataSet#DataSet()} default constructor.
	 * The method is expected to initialise all the properties to the default
	 * value defined by their underlying type.
	 */
	@Test
    public void testDefaultConstructor() {
        
		DataSet set = new DataSet();
        Assert.assertNull(set.getId());
        Assert.assertNull(set.getLabel());
        Assert.assertNull(set.getDatFile());
        Assert.assertNull(set.getModelId());
        
        // we do not test DatFileVersion because deprecated.
    }

	/**
	 * This method tests the behaviour of the {@link DataSet#DataSet(String, String, InputStream)}
	 * constructor. The method is expected to assign the arguments passed to the constructor to the
	 * corresponding properties defined in the {@link DataSet}. These values can be subsequently
	 * retrieved with the corresponding getter.
	 */
    @Test
    public void testConstructorWithParameters() {
    	
        InputStream datStream = new ByteArrayInputStream("dat-file-content".getBytes(StandardCharsets.UTF_8));

        DataSet set = new DataSet("anotherLabel", "model-id", datStream);
        Assert.assertNull(set.getId());
        Assert.assertEquals(set.getLabel(), "anotherLabel");
        Assert.assertEquals(set.getModelId(), "model-id");
        Assert.assertNotNull(set.getDatFile());
        Assert.assertEquals(set.getDatFile(), datStream);
    }


	/**
	 * This method tests the implemented behaviour of the setter and getter methods for the
	 * <i>label</i> property. There are no particular restrictions on the value that can be
	 * assigned to it. Its default value is {@literal null} and what is set by the setter
	 * should be retrieved by the getter.
	 */
    @Test
	public void testGetSetLabel() {

		// Test 1. null by default.
		//
		DataSet ds = new DataSet();
		Assert.assertNull(ds.getLabel());
		
		// Test 2. we set with the setter, wr get it with the getter.
		//
		String expected = "This is a label.";
        ds.setLabel(expected);
        String actual = ds.getLabel();
        Assert.assertEquals(expected, actual);
        
        // Test 3. can we set null?
        //
        ds.setLabel(null);
        Assert.assertNull(ds.getLabel());	
	}

	/**
	 * This method tests the implemented behaviour of the setter and getter methods for the
	 * <i>datFile</i> property. There are no particular restrictions on the value that can be
	 * assigned to it. Its default value is {@literal null} and what is set by the setter
	 * should be retrieved by the getter.
	 */
    @Test
	public void testGetSetDatFile() {
		
		// Test 1. null by default.
		//
		DataSet ds = new DataSet();
		Assert.assertNull(ds.getDatFile());
		
		// Test 2. we set with the setter, wr get it with the getter.
		//
		InputStream expected = new ByteArrayInputStream("dat-file-content".getBytes(StandardCharsets.UTF_8));
        ds.setDatFile(expected);
        InputStream actual = ds.getDatFile();
        Assert.assertEquals(expected, actual);
        
        // Test 3. can we set null?
        //
        ds.setDatFile(null);
        Assert.assertNull(ds.getDatFile());

        
		
	}


	/**
	 * This method tests the implemented behaviour of the setter and getter methods for the
	 * <i>modelId</i> property. There are no particular restrictions on the value that can be
	 * assigned to it. Its default value is {@literal null} and what is set by the setter
	 * should be retrieved by the getter.
	 */
    @Test
	public void testGetSetModelId() {

		// Test 1. null by default.
		//
		DataSet ds = new DataSet();
		Assert.assertNull(ds.getModelId());
		
		// Test 2. we set with the setter, wr get it with the getter.
		//
		String expected = UUID.randomUUID().toString();
        ds.setModelId(expected);
        String actual = ds.getModelId();
        Assert.assertEquals(expected, actual);
        
        // Test 3. can we set null?
        //
        ds.setModelId(null);
        Assert.assertNull(ds.getModelId());		
		
	}



    /**
     * This method tests the implemented behaviour of {@link Entity#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
    @Override
    @Test
    public void testClone() {
    	
    	super.testClone();
    	
    	// ok, id, attachment, revisions and default values are already tested.
    	
    	DataSet ds = new DataSet();
    	ds.setModelId(UUID.randomUUID().toString());
    	ds.setLabel("This is a label");
    	ds.setId(UUID.randomUUID().toString());
    	ds.setRevision(UUID.randomUUID().toString());
    	
    	DataSet actual = (DataSet) ds.clone();
    	this.equals(ds, actual);
    }
    
    /**
     * This method extends {@link EntityTest#equals(Entity, Entity)} and complements
     * it with checks field-by-field for the properties defined by {@link DataSet}.
     * The {@link InputStream} returned by {@link DataSet#getDatFile()} is the only
     * one not checked.
     * 
     * @param expected	a {@link Entity} reference. It cannot be {@literal null} and
     * 					must be of type {@link DataSet}.
     * @param actual	a {@link Entity} reference. It cannot be {@literal null} and
     * 					must be of type {@link DataSet}.
     */
    @SuppressWarnings("deprecation")
	protected void equals(Entity expected, Entity actual) {
    	
    	super.equals(expected, actual);
    	
    	// we now have to check the specific properties
    	// that are defined in dataset.
    	
    	DataSet ds1 = (DataSet) expected;
    	DataSet ds2 = (DataSet) actual;
    	
    	Assert.assertEquals(ds1.getLabel(), ds2.getLabel());
    	Assert.assertEquals(ds1.getModelId(), ds2.getModelId());
    	Assert.assertEquals(ds1.getDatFileVersion(), ds2.getDatFileVersion());
    	
    }
    
    @Override
    protected Entity createEntity() {
    	
    	return new DataSet();
    }
}
