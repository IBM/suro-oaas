package com.ibm.au.optim.suro.model.entities;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.Entity;
import com.ibm.au.optim.suro.model.entities.Run;

/**
 * Class <b>EntityTest</b>. This class tests the behaviour implemented in the {@link 
 * Entity} class. The tests have been designed to be re-used by test classes that tests 
 * the behaviour of the classes inherited by the {@link Entity} class. Besides the test 
 * methods, the key for enabling tests reuse is to abstract the creation of {@link Entity} 
 * instance into a method that can be overridden in inherited classes to provide a different
 * instance, and making the test method for {@link Entity} use this method to retrieve
 * instances for test.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class EntityTest {

	
	/**
	 * This method tests the behaviour of the {@link Entity#equals(Object)} method.
	 * The method is designed to perform the check for equality by using the <i>id</i>
	 * property of the instance and comparing the corresponding values of this property
	 * for the two instances of {@link Entity} that are compared.
	 */
	@Test
    public void testEquals() {
		
		// basic testing, we check that when the ids are different
		// the equals method returns false, and when they are the
		// same the method returns true.
		//
        Entity obj = this.createEntity();
        obj.setId("test");

        Entity match = this.createEntity();
        match.setId("test");

        Entity different = this.createEntity();
        different.setId("different");

        Assert.assertTrue(obj.equals(match));
        Assert.assertFalse(obj.equals(different));
        Assert.assertFalse(match.equals(different));
        
        
        // edge testing, first test, we set the identifier
        // specifically to null.
        //
        obj = this.createEntity();
        obj.setId(null);
        Assert.assertFalse(obj.equals(different));
        
        // ok, null != null, the equals operator should
        // not be kicked in...
        //
        different.setId(null);
        Assert.assertFalse(obj.equals(different));
        
        // ok let's try with different types.
        //
        obj.setId("test");
        Assert.assertFalse(obj.equals("test"));
        Assert.assertFalse(obj.equals(null));
    }
    
    /**
     * <p>
     * This method tests the behaviour of the setter and getter methods 
     * for the property <i>id</i>. The default conditions are not tested
     * because they depend on the specific class implementation inheriting
     * from {@link Entity}. What is tested is that the getter retrieves
     * the value assigned to the id and the setter sets that value.
     * </p>
     * <p>
     * For testing the default conditions see the test method {@link 
     * EntityTest#testDefaultConstructor()}.
     * </p>
     */
    @Test 
    public void testGetSetId() {
    
    	Entity entity = this.createEntity();
    	
    	// test with the common case
    	
    	String expected = "2ab34cdef44211290ef";
    	entity.setId(expected);
    	
    	String actual = entity.getId();
    	Assert.assertEquals(expected, actual);
    	
    	// test with null
    	entity.setId(null);
    	actual = entity.getId();
    	Assert.assertNull(actual);
    	
    }
    
    /**
     * <p>
     * This method tests the behaviour of the setter and getter methods 
     * for the property <i>revision</i>. The default conditions are not
     * tested because they depend on the specific class implementation 
     * inheriting from {@link Entity}. What is tested is that the getter
     * retrieves the value assigned to the revision and the setter sets
     * that value.
     * </p>
     * <p>
     * For testing the default conditions see the test method {@link 
     * EntityTest#testDefaultConstructor()}.
     * </p>
     */
    @Test 
    public void testGetSetRevision() {
    
    	Entity entity = this.createEntity();
    	
    	// test with the common case
    	
    	String expected = "rev-2ab34cdef44211290ef";
    	entity.setRevision(expected);
    	
    	String actual = entity.getRevision();
    	Assert.assertEquals(expected, actual);
    	
    	// test with null
    	entity.setRevision(null);
    	actual = entity.getRevision();
    	Assert.assertNull(actual);
    	
    }

    /**
     * This method tests the behaviour of the default constructor for
     * an {@link Entity} instance. The expected behaviour is to have
     * an instance whose properties (id, revision, and attachments)
     * are set to {@literal null}.
     */
    @Test
    public void testDefaultConstructor() {
    	
    	Entity entity = new Entity();
    	Assert.assertNull(entity.getId());
    	Assert.assertNull(entity.getRevision());
    	Assert.assertNull(entity.getAttachments());
    }
    
    /**
     * This method tests the implemented behaviour of {@link Entity#getAttachment(String)}.
     * The method is expected to return an {@link Attachment} instance whose name is the
     * same as the argument passed to the method if found. Otherwise {@literal null}.
     * Moreover, when invoked with an empty or {@literal null} string it should throw a
     * {@link IllegalArgumentException}.
     */
    @Test
    public void testGetAttachment() {
    	
    	// Test 1. empty entity, getAttachment returns null.
    	//
    	Entity entity = this.createEntity();
    	Assert.assertNull(entity.getAttachment("notExisting"));
    	
    	// Test 2. we add an attachment and we retrieve it.
    	//
    	Attachment expected = new Attachment("a1", 1312, "text/plain");
    	List<Attachment> attachments = new ArrayList<Attachment>();
    	attachments.add(expected);
    	entity.setAttachments(attachments);
    	Attachment actual = entity.getAttachment(expected.getName());
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(expected, actual);
    	
    	// Test 3. we remove the attachment.
    	//
    	attachments = entity.getAttachments();
    	attachments.remove(expected);
    	entity.setAttachments(attachments);
    	actual = entity.getAttachment(expected.getName());
    	Assert.assertNull(actual);
    	
    	
    	// Test 4. call with null.
    	//
    	
    	try {
    		
    		entity.getAttachment(null);
    		Assert.fail("Entity.getAttachment(null) should throw IllegalArgumentException (name cannot be null).");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 5. call with empty string.
    	//
    	
    	try {
    		
    		entity.getAttachment("");
    		Assert.fail("Entity.getAttachment('') should throw IllegalArgumentException (name cannot be an empty string).");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go...
    	}
    	
    	
    	
    }
    
    /**
     * This method tests the behaviour of the getter and setter methods
     * defined for the <i>attachments</i> property. The method tests the 
     * default condition that implies that {@link Entity} instance has
     * a {@literal null} list of attachments. Moreover we test that the 
     * list of attachments that are set via the setter can is the same (in 
     * terms of content) to the one that is retrieved via the corresponding 
     * getter.
     */
    @Test
    public void testGetSetAttachments() {
    	
    	Entity entity = this.createEntity();
    	
    	Attachment a1 = new Attachment("test.txt", 1337, "text/plain");
    	Attachment a2 = new Attachment("cat.png", 3233, "image/png");

        List<Attachment> expected = new ArrayList<Attachment>();
        expected.add(a1);
        expected.add(a2);

        
        // default condition. Let's test this.
        //
    	Assert.assertNull(entity.getAttachments());
        entity.setAttachments(expected);
         
        List<Attachment> actual = entity.getAttachments();
        Assert.assertEquals(expected.size(), actual.size());
         
        int size = expected.size();
        for(int i=0; i<size; i++) {
         	
        	Attachment eA = expected.get(i);
         	Attachment aA = expected.get(i);
         	
         	Assert.assertEquals(eA, aA);
        }
    	
    }

    /**
     * This method tests the implemented behaviour of {@link Entity#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
    @Test
    public void testClone() {
    	
    	// 1. First test, we simply check what we do if we execute the
    	//    method clone() once the instance has been created.
    	//
    	Entity expected = this.createEntity();
    	
    	Entity actual = expected.clone();
    	this.equals(expected, actual);
    	
    	// 2. Second test, we pass some additional information, set some
    	//    properties and check again whether the clone operation has
    	//    the same effect.
    	//
    	expected.setId(UUID.randomUUID().toString());
    	expected.setRevision(UUID.randomUUID().toString());
    	
    	List<Attachment> attachments = new ArrayList<Attachment>();
    	attachments.add(new Attachment("a1", 0, "text/plain"));
    	attachments.add(new Attachment("a2", 324002, "image/jpeg"));
    	expected.setAttachments(attachments);
    	
    	actual = expected.clone();
    	this.equals(expected, actual);
    }

    

    /**
     * This method tests the behaviour of the {@link Entity#hashCode()}
     * method. Its implementation is based on the hashcode of the <i>id</i>
     * property. Therefore the value returned by the method should be 
     * equal to the value returned by the {@link String#hashCode()} method
     * when applied to the <i>id</i> property.
     */
    @Test
    public void testHashCode() {
    	
        Entity obj = new Run();
        Assert.assertEquals(1, obj.hashCode());

        String id = "514h123hu12313h12h31231h3h";
        obj.setId(id);
        Assert.assertEquals(id.hashCode(), obj.hashCode());
    }

    /**
     * This method abstracts the creation of an {@link Entity}
     * instance, so that we can subclass this test class and
     * re-use all the tests defined for an entity to all the
     * class that extend this class.
     * 
     * @return	a {@link Entity} instance.
     */
    protected Entity createEntity() {
    	
    	return new Entity();
    }
    
    /**
     * This method checks whether two entities are the same by performing a field
     * by field comparison. The {@link List} of {@link Attachment} is compared by
     * size first and the by looking whether all the elements in the first can be
     * found in the second.
     * 
     * @param expected	a {@link Entity} instance, the first for the comparison.
     * @param actual	a {@link Entity} instance, the second for the comparison.
     */
    protected void equals(Entity expected, Entity actual) {
    	
    	Assert.assertEquals(expected.getId(), actual.getId());
    	Assert.assertEquals(expected.getRevision(), actual.getRevision());
    	
    	List<Attachment> attachments = expected.getAttachments();
    	if (attachments != null) {
    	
    		Assert.assertEquals(expected.getAttachments().size(), actual.getAttachments().size());
    		List<Attachment> targets = actual.getAttachments();
			Assert.assertNotNull(targets);
    		for(Attachment attachment : attachments) {
    			Assert.assertTrue(targets.contains(attachment));
    		}
    	
    	} else {
    		
    		Assert.assertNull(actual.getAttachments());
    	}
    	
    	
    }
    
}
