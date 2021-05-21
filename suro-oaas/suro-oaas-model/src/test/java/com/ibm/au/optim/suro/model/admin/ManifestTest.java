/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import org.junit.Test;
import org.junit.Assert;

/**
 * Class <b>ManifestTest</b>. This class tests the implemented behaviour of the 
 * {@link Manifest} class. A manifest is expected to have a non-empty and non 
 * {@literal null} version, while the other properties can be {@literal null}.
 * Besides the version, a {@link Manifest} instance to be valid needs to have
 * at least one of the {@link ManifestEntity} properties non {@literal null}.
 * 
 * @author Christian Vecchiola
 *
 */
public class ManifestTest {

	/**
	 * This method tests the implemented behaviour of the constructor of the class
	 * {@link Manifest#Manifest(String, ManifestEntity, ManifestEntity, ManifestEntity, 
	 * String)}. The method is expected to accept any value for all the parameters 
	 * except for the fist parameter the <i>version</i>, which is supposed to be
	 * nor {@literal null} neither an empty string.
	 */
	@Test
	public void testConstructor() {
		
		// Test 1. Sunny day, all non null parameters. We expect to get them back
		//         from the getters.
		//

		String expectedVersion = "v1.0.0";
		String expectedDescription = "This is the description for the manifest.";
		ManifestEntity expectedModel = new ManifestEntity("model", null);
		ManifestEntity expectedDataSet = new ManifestEntity("dataSet", null);
		ManifestEntity expectedTemplates = new ManifestEntity("templates", null);

		Manifest manifest = new Manifest(expectedVersion, expectedModel, expectedTemplates, expectedDataSet, expectedDescription);
		String actual = manifest.getVersion();
		Assert.assertEquals(expectedVersion, actual);
		
		ManifestEntity actualEntity = manifest.getModel();
		Assert.assertEquals(expectedModel, actualEntity);
		
		actualEntity = manifest.getTemplates();
		Assert.assertEquals(expectedTemplates, actualEntity);
		
		actualEntity = manifest.getDataSet();
		Assert.assertEquals(expectedDataSet, actualEntity);
		
		actual = manifest.getDescription();
		Assert.assertEquals(expectedDescription, actual);
		
		Assert.assertTrue(manifest.isValid());
		
		
		// Test 2. Null description...
		//
		
		manifest = new Manifest(expectedVersion, expectedModel, expectedTemplates, expectedDataSet, null);
		Assert.assertEquals(expectedVersion, manifest.getVersion());
		Assert.assertEquals(expectedModel, manifest.getModel());
		Assert.assertEquals(expectedTemplates, manifest.getTemplates());
		Assert.assertEquals(expectedDataSet, manifest.getDataSet());
		Assert.assertNull(manifest.getDescription());
		
		// Test 3. Null data set...
		//
		
		manifest = new Manifest(expectedVersion, expectedModel, expectedTemplates, null, expectedDescription);
		Assert.assertEquals(expectedVersion, manifest.getVersion());
		Assert.assertEquals(expectedModel, manifest.getModel());
		Assert.assertEquals(expectedTemplates, manifest.getTemplates());
		Assert.assertNull(manifest.getDataSet());
		Assert.assertEquals(expectedDescription, manifest.getDescription());

		// Test 4. Null templates...
		//
		manifest = new Manifest(expectedVersion, expectedModel, null, expectedDataSet, expectedDescription);
		Assert.assertEquals(expectedVersion, manifest.getVersion());
		Assert.assertEquals(expectedModel, manifest.getModel());
		Assert.assertNull(manifest.getTemplates());
		Assert.assertEquals(expectedDataSet, manifest.getDataSet());
		Assert.assertEquals(expectedDescription, manifest.getDescription());
		
		// Test 5. Null model...
		//
		manifest = new Manifest(expectedVersion, null, expectedTemplates, expectedDataSet, expectedDescription);
		Assert.assertEquals(expectedVersion, manifest.getVersion());
		Assert.assertNull(manifest.getModel());
		Assert.assertEquals(expectedTemplates, manifest.getTemplates());
		Assert.assertEquals(expectedDataSet, manifest.getDataSet());
		Assert.assertEquals(expectedDescription, manifest.getDescription());
		
		// Test 6. Null version, we expect an exception to be thrown...
		//
		try {
			
			manifest = new Manifest(null, expectedModel, expectedTemplates, expectedDataSet, expectedDescription);
			Assert.fail("Manifest.Manifest(null, ManifestEntry, ManifestEntry, ManifestEntry, String) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go.
		}
		

		// Test 7. Empty version, we expect an exception to be thrown...
		//
		try {
			
			manifest = new Manifest("", expectedModel, expectedTemplates, expectedDataSet, expectedDescription);
			Assert.fail("Manifest.Manifest('', ManifestEntry, ManifestEntry, ManifestEntry, String) should throw IllegalArgumentException if the version is an empty string.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go.
		}
		
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and setter methods for the <i>model</i> property.
	 * There are no constraints for the <i>model</i> property. It is also possible to set it to {@literal null}.
	 * The getter is expected to either retrieve the value passed to the constructor, or the value set wih the 
	 * setter.
	 */
	@Test
	public void testGetSetModel() {
	
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String expectedVersion = "v1.0.0";
		ManifestEntity expectedModel = new ManifestEntity("model", null);
		
		Manifest manifest = new Manifest(expectedVersion, expectedModel,  null, null, null);
		ManifestEntity actualModel = manifest.getModel();
		Assert.assertEquals(expectedModel, actualModel);
		
		// Test 2. We get from the getter, what we pass to the setter.
		//
		expectedModel = new ManifestEntity("dtst", null);
		manifest.setModel(expectedModel);
		actualModel = manifest.getModel();
		Assert.assertEquals(expectedModel, actualModel);
		
		// Test 3. We can set null
		//
		manifest.setModel(null);
		actualModel = manifest.getModel();
		Assert.assertNull(actualModel);
	}

	
	/**
	 * This method tests the implemented behaviour of the getter and setter methods for the <i>dataSet</i> property.
	 * There are no constraints for the <i>dataSet</i> property. It is also possible to set it to {@literal null}.
	 * The getter is expected to either retrieve the value passed to the constructor, or the value set with the 
	 * setter.
	 */
	@Test
	public void testGetSetDataSet() {
		
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String expectedVersion = "v1.0.0";
		ManifestEntity expectedDataSet = new ManifestEntity("dataset", null);
		
		Manifest manifest = new Manifest(expectedVersion, null,  null, expectedDataSet, null);
		ManifestEntity actualDataSet = manifest.getDataSet();
		Assert.assertEquals(expectedDataSet, actualDataSet);
		
		// Test 2. We get from the getter, what we pass to the setter.
		//
		expectedDataSet = new ManifestEntity("dtst", null);
		manifest.setDataSet(expectedDataSet);
		actualDataSet = manifest.getDataSet();
		Assert.assertEquals(expectedDataSet, actualDataSet);
		
		// Test 3. We can set null
		//
		manifest.setDataSet(null);
		actualDataSet = manifest.getDataSet();
		Assert.assertNull(actualDataSet);
		
	}

	/**
	 * This class tests the implemented behaviour of the getter and setter methods for the <i>version</i> property.
	 * The constraints that are to be enforced are: a <i>version</i> cannot be {@literal null} and it cannot be an
	 * empty string. The getter is expected to retrieve what the initialisation value passed to the constructor or
	 * if a setter has been invoked the value set by the setter.
	 */
	@Test
	public void testGetSetVersion() {
	
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String expected = "v1.0.0";
		Manifest manifest = new Manifest(expected, null,  null, null, null);
		String actual = manifest.getVersion();
		Assert.assertEquals(expected, actual);
		
		// Test 2. What we set with the setter we get with the getter.
		//
		expected = "v0.0.2";
		manifest.setVersion(expected);
		actual = manifest.getVersion();
		Assert.assertEquals(expected, actual);
		
		// Test 3. Null version should throw IllegalArgumentException.
		//
		try {
			
			manifest.setVersion(null);
			Assert.fail("Manifest.setVersion(null) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go.
		}
		

		// Test 4. Empty version should throw IllegalArgumentException.
		//
		try {
			
			manifest.setVersion("");
			Assert.fail("Manifest.setVersion('') should throw IllegalArgumentException if the version is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go.
		}
		
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and setter methods for the <i>templates</i> property.
	 * There are no constraints for the <i>templates</i> property. It is also possible to set it to {@literal null}.
	 * The getter is expected to either retrieve the value passed to the constructor, or the value set with the setter.
	 */
	@Test
	public void testGetSetTemplates() {
		
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String expectedVersion = "v1.0.0";
		ManifestEntity expectedTemplates = new ManifestEntity("templates", null);
		
		Manifest manifest = new Manifest(expectedVersion, null,  expectedTemplates, null, null);
		ManifestEntity actualTemplates = manifest.getTemplates();
		Assert.assertEquals(expectedTemplates, actualTemplates);
		
		// Test 2. We get from the getter, what we pass to the setter.
		//
		expectedTemplates = new ManifestEntity("tmpls", null);
		manifest.setTemplates(expectedTemplates);
		actualTemplates = manifest.getTemplates();
		Assert.assertEquals(expectedTemplates, actualTemplates);
		
		// Test 3. We can set null
		//
		manifest.setTemplates(null);
		actualTemplates = manifest.getTemplates();
		Assert.assertNull(actualTemplates);
	}

	/**
	 * This method tests the implemented behaviour of the getter and setter methods for the <i>description</i> property.
	 * There are no constraints for the <i>description</i> property. It is also possible to set it to {@literal null}.
	 * The getter is expected to either retrieve the value passed to the constructor, or the value set with the setter.
	 */
	@Test
	public void testGetSetDescription() {

		// Test 1. We get from the getter what we pass in the constructor.
		//
		String expectedVersion = "v1.0.0";
		String expectedDescription = "This is the description for the manifest.";
		
		Manifest manifest = new Manifest(expectedVersion, null, null, null, expectedDescription);
		String actualDescription = manifest.getDescription();
		Assert.assertEquals(expectedDescription, actualDescription);
		
		
		// Test 2. We get what we put with the setter.
		//
		expectedDescription = "This is another description.";
		manifest.setDescription(expectedDescription);
		actualDescription = manifest.getDescription();
		Assert.assertEquals(expectedDescription, actualDescription);
		
		
		// Test 3. Edge case, empty string.
		//
		expectedDescription = "";
		manifest.setDescription(expectedDescription);
		actualDescription = manifest.getDescription();
		Assert.assertEquals(expectedDescription, actualDescription);
		

		// Test 4. Edge case, null string.
		//
		manifest.setDescription(null);
		actualDescription = manifest.getDescription();
		Assert.assertNull(actualDescription);
		
		
	}
	
	/**
	 * This method tests the implemented behaviour of the {@link Manifest#isValid()} method. This method is expected to
	 * return {@literal true} if there is at least one {@link ManifestEntity} property that is not {@literal null}.
	 */
	@Test
	public void testIsValid() {
	
		
		// Test 1. all the values null, it should return false.
		//
		Manifest manifest = new Manifest("0.0.1", null, null, null, null);
		Assert.assertFalse(manifest.isValid());
		
		// Test 2. the model (only)is not null, it should return true.
		//
		manifest.setModel(new ManifestEntity("model", null));
		Assert.assertTrue(manifest.isValid());
		
		// Test 3. the template (only) is not null, it should return true.
		//
		manifest.setModel(null);
		Assert.assertFalse(manifest.isValid());
		manifest.setTemplates(new ManifestEntity("templates", null));
		Assert.assertTrue(manifest.isValid());
		
		// Test 4. the dataset (only) is not null, it should return true.
		//
		manifest.setTemplates(null);
		Assert.assertFalse(manifest.isValid());
		manifest.setDataSet(new ManifestEntity("dataset", null));
		Assert.assertTrue(manifest.isValid());
		
		// Test 5. the description does not change anything.
		//
		manifest.setDataSet(null);
		Assert.assertFalse(manifest.isValid());
		manifest.setDescription("This is the description.");
		Assert.assertFalse(manifest.isValid());
		
		// Test 6. model and templates not null, dataset null, should return true.
		//
		manifest.setModel(new ManifestEntity("model", null));
		manifest.setTemplates(new ManifestEntity("templates", null));
		Assert.assertTrue(manifest.isValid());
		
		// Test 7. model and dataset not null, templates null, should return true.
		//
		manifest.setTemplates(null);
		manifest.setDataSet(new ManifestEntity("dataset", null));
		Assert.assertTrue(manifest.isValid());
		
		// Test 8. model null, dataset and templates not null, should return true.
		//
		manifest.setModel(null);
		manifest.setTemplates(new ManifestEntity("templates", null));
		Assert.assertTrue(manifest.isValid());
		
		// Test 9. model, dataset, and templates are not null, should return true.
		//
		manifest.setModel(new ManifestEntity("model", null));
		Assert.assertTrue(manifest.isValid());
		
	}
	
}
