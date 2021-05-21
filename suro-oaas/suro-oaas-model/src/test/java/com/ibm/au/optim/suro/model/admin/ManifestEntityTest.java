/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>ManifestEntityTest</b>. This class tests the implemented
 * behaviour of the {@link ManifestEntity} class. A manifest entity
 * holds the information about the path to the main resource which
 * cannot be {@literal null} or an empty string and array of path to
 * attachments, which can be {@literal null} or empty.
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class ManifestEntityTest {

	/**
	 * This method tests the implemented behaviour of the {@link ManifestEntity#ManifestEntity(String, java.util.List)}
	 * constructor. This constructor is expected to initialise valid {@link ManifestEntity} instances. Therefore, the
	 * path property cannot be {@literal null} or an empty string.
	 * 
	 */
	@Test
	public void testConstructorWithParameters() {
		
		// Test 1. Sunny day... valid parameters
		//
		String expectedPath = "entity.txt";
		List<String> expectedAttachments = new ArrayList<String>();
		expectedAttachments.add("attachments/att1.dat");
		expectedAttachments.add("attachments/att2.txt");
		
		
		ManifestEntity entity = new ManifestEntity(expectedPath, expectedAttachments);
		String actualPath = entity.getPath();
		Assert.assertNotNull(actualPath);
		Assert.assertEquals(expectedPath, actualPath);
		
		List<String> actualAttachments = entity.getAttachments();
		Assert.assertNotNull(actualAttachments);
		Assert.assertEquals(expectedAttachments, actualAttachments);
		
		
		// Test 2. Edge case, empty list...
		//
		
		expectedAttachments.clear();
		entity = new ManifestEntity(expectedPath, expectedAttachments);
		// ok, if we got here, this means that an empty list can be passed.
		//
		actualAttachments = entity.getAttachments();
		Assert.assertEquals(expectedAttachments, actualAttachments);
		
		// Test 3. Edge case, nul list....
		//
		entity = new ManifestEntity(expectedPath, null);
		// ok, if we got here, this means that a null list can be passed.
		//
		actualAttachments = entity.getAttachments();
		Assert.assertNull(actualAttachments);
		
		
		// Test 3. Null path.
		//
		try {
			
			entity = new ManifestEntity(null, expectedAttachments);
			Assert.fail("ManifestEntity(null, List<String>) should throw IllegalArgumentException, when the path is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go..
		}
		
		
		// Test 4. Empty path.
		//
		try {
			
			entity = new ManifestEntity("", expectedAttachments);
			Assert.fail("ManifestEntity('', List<String>) should throw IllegalArgumentException, when the path is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go..
		}
	}
	
	/**
	 * This method tests the implemented behaviour of the pair of getter and setter for the <i>path</i> property.
	 * The constraints that are tested are:
	 * <ul>
	 * <li>a <i>path</i> cannot be {@literal null}.</li>
	 * <li>a <i>path</i> cannot be an empty string.</li>
	 * </ul>
	 * Moreover, the getter should either retrieve the value set with the constructor, or the value set with the setter.
	 */
	@Test
	public void testGetSetPath() {
		
		// Test 1. Default value, as set through the constuctor.
		//
		//
		String expectedPath = "path";
		ManifestEntity entity = new ManifestEntity(expectedPath, null);
		String actualPath = entity.getPath();
		Assert.assertEquals(expectedPath, actualPath);
		
		// Test 2. Setting a value with the setter and retrieving it with
		//         the getter.
		//
		expectedPath = "anotherPath";
		entity.setPath(expectedPath);
		actualPath = entity.getPath();
		Assert.assertEquals(expectedPath, actualPath);
		
		// Test 3. Null path.
		//
		try {

			entity.setPath(null);
			Assert.fail("ManifestEntity.setPath(null) should throw IllegalArgumentException, when the path is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go..
		}
		
		
		// Test 4. Empty path.
		//
		try {
			
			entity.setPath("");
			Assert.fail("ManifestEntity.setPath('') should throw IllegalArgumentException, when the path is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go..
		}
	}
	
	
	/**
	 * This method tests the implemented behaviour of the pair of getter and setter for the <i>attachment</i> property.
	 * There are no constraints for this property, and the getter should retrieve the default value initialised with
	 * the constructor or set with the setter.
	 */
	@Test
	public void testGetSetAttachments() {
		
		// Test 1. Sunny day... valid parameters
		//
		String expectedPath = "entity.txt";
		List<String> expectedAttachments = new ArrayList<String>();
		expectedAttachments.add("attachments/att1.dat");
		expectedAttachments.add("attachments/att2.txt");
		
		ManifestEntity entity = new ManifestEntity(expectedPath, expectedAttachments);
		List<String> actualAttachments = entity.getAttachments();
		Assert.assertEquals(expectedAttachments, actualAttachments);
		
		// Test 2. What we put with the setter, we get with the getter
		//
		// 2.a - generic list with content.
		//
		expectedAttachments = new ArrayList<String>();
		expectedAttachments.add("att/att2.log");
		entity.setAttachments(expectedAttachments);
		actualAttachments = entity.getAttachments();
		Assert.assertEquals(expectedAttachments, actualAttachments);
		
		// 2.b - null list.
		//
		entity.setAttachments(null);
		actualAttachments = entity.getAttachments();
		Assert.assertNull(actualAttachments);
		
		// 2.c - empty list.
		//
		expectedAttachments.clear();
		entity.setAttachments(expectedAttachments);
		actualAttachments = entity.getAttachments();
		Assert.assertEquals(0, actualAttachments.size());
		
		
	}
}
