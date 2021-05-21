package com.ibm.au.optim.suro.model.admin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.entities.Model;

/**
 * Class <b>PackageTest</b>. This class tests the implemented behaviour of the
 * {@link Package} class. A package constitutes an update to the data entities
 * that are installed in the application. It may contain a {@link Model}, a 
 * {@link Template} collection, and a {@link DataSet}. At least one of these
 * entities needs to be defined. Moreover, it also defines a version that it
 * cannot be {@literal null} or an empty string.
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class PackageTest {
	


	/**
	 * This method tests the behaviour of the constructor {@link Package#Package(String, DataSet)}. It is expected that
	 * the constructor sets the <i>version</i> and the <i>dataset</i> properties. Both of them cannot be {@literal null}.
	 * If the <i>version</i> is {@literal null} or an empty string, {@link IllegalArgumentException} is thrown, if the
	 * <i>dataset</i> is {@literal null}, {@link IllegalStateException} is thrown.
	 */
	@Test
	public void testConstructorWithVersionAndDataset() {
		
		// Test 1. Sunny day valid parameters, it should pass.
		//
		String expectedVersion = "v0.0.1";
		DataSet expectedDataSet = new DataSet();
		
		Package pkg = new Package(expectedVersion, expectedDataSet);
		String actualVersion = pkg.getVersion();
		Assert.assertEquals(expectedVersion, actualVersion);
		DataSet actualDataSet = pkg.getDataSet();
		Assert.assertEquals(expectedDataSet, actualDataSet);
		
		// Test 2. version is null, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package(null, expectedDataSet);
			Assert.fail("Package.Package(null, DataSet) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 3. version is empty, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package("", expectedDataSet);
			Assert.fail("Package.Package('', DataSet) should throw IllegalArgumentException if the version is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 4. model is null, IllegalStateException should be thrown.
		//
		try {
			
			pkg = new Package(expectedVersion, (DataSet) null);
			Assert.fail("Package.Package(String, null) should throw IllegalStateException if the dataset is null.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok good to go...
			
		}
		
		// Test 5. both null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package(null, (DataSet) null);
			Assert.fail("Package.Package(null, null) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 6. empty and null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package("", (DataSet) null);
			Assert.fail("Package.Package('', null) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}		
	}
	
	/**
	 * This method tests the behaviour of the constructor {@link Package#Package(String, Model)}. It is expected that
	 * the constructor sets the <i>version</i> and the <i>model</i> properties. Both of them cannot be {@literal null}.
	 * If the <i>version</i> is {@literal null} or an empty string, {@link IllegalArgumentException} is thrown, if the
	 * <i>model</i> is {@literal null}, {@link IllegalStateException} is thrown.
	 */
	@Test
	public void testConstructorWithVersionAndModel() {
		
		// Test 1. Sunny day valid parameters, it should pass.
		//
		String expectedVersion = "v0.0.1";
		Model expectedModel = new Model("model");
		
		Package pkg = new Package(expectedVersion, expectedModel);
		String actualVersion = pkg.getVersion();
		Assert.assertEquals(expectedVersion, actualVersion);
		Model actualModel = pkg.getModel();
		Assert.assertEquals(expectedModel, actualModel);
		
		// Test 2. version is null, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package(null, expectedModel);
			Assert.fail("Package.Package(null, Model) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 3. version is empty, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package("", expectedModel);
			Assert.fail("Package.Package('', Model) should throw IllegalArgumentException if the version is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 4. model is null, IllegalStateException should be thrown.
		//
		try {
			
			pkg = new Package(expectedVersion, (Model) null);
			Assert.fail("Package.Package(String, null) should throw IllegalStateException if the model is null.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok good to go...
			
		}
		
		// Test 5. both null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package(null, (Model) null);
			Assert.fail("Package.Package(null, null) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 6. empty and null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package("", (Model) null);
			Assert.fail("Package.Package('', null) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
	}
	
	/**
	 * This method tests the implemented behaviour of the constructor {@link Package#Package(String, Model, List)}. The
	 * expected behaviour is that for an instance of {@link Package} to be created, <i>version</i> cannot be {@literal 
	 * null} or an empty string, and one between <i>model</i> and <i>templates</i> must be valid (i.e. not {@literal null}
	 * for <i>model</i> and not {@literal null} or empty for <i>templates</i>). Moreover, the values set via the constructor
	 * are expected to be retrieved via the getter.
	 */
	@Test
	public void testConstructorWithVersionModelAndTemplates() {
		
		// Test 1. Sunny day valid parameters, it should pass.
		//
		String expectedVersion = "v0.0.1";
		Model expectedModel = new Model("model");
		List<Template> expectedTemplates = new ArrayList<Template>();
		expectedTemplates.add(new Template());
		
		Package pkg = new Package(expectedVersion, expectedModel, expectedTemplates);
		String actualVersion = pkg.getVersion();
		Assert.assertEquals(expectedVersion, actualVersion);
		Model actualModel = pkg.getModel();
		Assert.assertEquals(expectedModel, actualModel);
		List<Template> actualTemplates = pkg.getTemplates();
		Assert.assertEquals(expectedTemplates, actualTemplates);
		
		// Test 2. version is null, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package(null, expectedModel, expectedTemplates);
			Assert.fail("Package.Package(null, Model, List<Template>) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 3. version is empty, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package("", expectedModel, expectedTemplates);
			Assert.fail("Package.Package('', Model, List<Template>) should throw IllegalArgumentException if the version is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 4. model and template are null, IllegalStateException should be thrown.
		//
		try {
			
			pkg = new Package(expectedVersion, (Model) null, (List<Template>) null);
			Assert.fail("Package.Package(String, null, null) should throw IllegalStateException if the model and templates are null.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok good to go...
			
		}
		
		// Test 5. model is null and templates is empty, IllegalStateException should be thrown.
		//
		try {
			
			pkg = new Package(expectedVersion, (Model) null, new ArrayList<Template>());
			Assert.fail("Package.Package(String, null, []) should throw IllegalStateException if the model is null and templates is empty.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok good to go...
			
		}
		
		// Test 6. both null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package(null, (Model) null, (List<Template>) null);
			Assert.fail("Package.Package(null, null, null) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 7. both null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package(null, (Model) null, new ArrayList<Template>());
			Assert.fail("Package.Package(null, null, []) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 8. empty and null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package("", (Model) null, (List<Template>) null);
			Assert.fail("Package.Package('', null, null) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 9. empty and null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package("", (Model) null, new ArrayList<Template>());
			Assert.fail("Package.Package('', null, []) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
	}
	
	/**
	 * This method tests the behaviour of the constructor {@link Package#Package(String, Model, List<Template>, DataSet, String)}. 
	 * It is expected that the constructor sets the <i>version</i>, the <i>model</i>, the <i>templates</i>, the <i>dataset</i> and
	 * the <i>description</i> properties. Moreover, at least one among <i>model</i>, <i>templates</i>, and <i>dataset</i> must be
	 * not {@literal null} (for <i>templates</i> an empty list is not accepted too) otherwise an {@link IllegalStateException} is
	 * thrown. If the <i>version</i> is {@literal null} or an empty string, {@link IllegalArgumentException} is thrown.
	 * 
	 */
	@Test
	public void testConctructorWithAllParameters() {
		
		
		// Test 1. Sunny day valid parameters, it should pass.
		//
		String expectedVersion = "v0.0.1";
		Model expectedModel = new Model("model");
		List<Template> expectedTemplates = new ArrayList<Template>();
		expectedTemplates.add(new Template());
		DataSet expectedDataSet = new DataSet();
		String expectedDescription = "This is a description.";
		
		
		Package pkg = new Package(expectedVersion, expectedModel, expectedTemplates, expectedDataSet, expectedDescription);
		String actualVersion = pkg.getVersion();
		Assert.assertEquals(expectedVersion, actualVersion);
		Model actualModel = pkg.getModel();
		Assert.assertEquals(expectedModel, actualModel);
		List<Template> actualTemplates = pkg.getTemplates();
		Assert.assertEquals(expectedTemplates, actualTemplates);
		DataSet actualDataSet = pkg.getDataSet();
		Assert.assertEquals(expectedDataSet, actualDataSet);
		String actualDescription = pkg.getDescription();
		Assert.assertEquals(expectedDescription, actualDescription);
		
		// Test 2. version is null, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package(null, expectedModel, expectedTemplates, expectedDataSet, expectedDescription);
			Assert.fail("Package.Package(null, Model, List<Template>, DataSet, String) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 3. version is empty, IllegalArgumentException should be thrown.
		//
		try {
			
			pkg = new Package("", expectedModel, expectedTemplates, expectedDataSet, expectedDescription);
			Assert.fail("Package.Package('', Model, List<Template>, DataSet, String) should throw IllegalArgumentException if the version is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
			
		}
		
		// Test 4. model and template are null, IllegalStateException should be thrown.
		//
		try {
			
			pkg = new Package(expectedVersion, (Model) null, (List<Template>) null, null, expectedDescription);
			Assert.fail("Package.Package(String, null, null, null, String) should throw IllegalStateException if the model and templates are null.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok good to go...
			
		}
		
		// Test 5. model is null and templates is empty, IllegalStateException should be thrown.
		//
		try {
			
			pkg = new Package(expectedVersion, (Model) null, new ArrayList<Template>(), null, expectedDescription);
			Assert.fail("Package.Package(String, null, [], null, String) should throw IllegalStateException if the model is null and templates is empty.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok good to go...
			
		}
		
		// Test 6. both null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package(null, (Model) null, (List<Template>) null, null, expectedDescription);
			Assert.fail("Package.Package(null, null, null, null, String) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 7. both null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package(null, (Model) null, new ArrayList<Template>(), null, expectedDescription);
			Assert.fail("Package.Package(null, null, [], null, String) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 8. empty and null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package("", (Model) null, (List<Template>) null, null, expectedDescription);
			Assert.fail("Package.Package('', null, null, null, String) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 9. empty and null, one of the two exceptions should be thrown.
		//
		try {
			

			pkg = new Package("", (Model) null, new ArrayList<Template>(), null, expectedDescription);
			Assert.fail("Package.Package('', null, [], null, String) should throw IllegalStateException or IllegalArgumentException.");
			
		} catch(IllegalArgumentException | IllegalStateException ilex) {
			
			// ok, good to go...
		}
		
		// Test 10. A description set to null, should not create an issue.
		//
		pkg = new Package(expectedVersion, expectedModel, expectedTemplates, expectedDataSet, null);
		Assert.assertNull(pkg.getDescription());
		
		
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
		Package pkg = new Package(expected, new Model("model"),  new ArrayList<Template>(), new DataSet(), "This is a package.");
		String actual = pkg.getVersion();
		Assert.assertEquals(expected, actual);
		
		// Test 2. What we set with the setter we get with the getter.
		//
		expected = "v0.0.2";
		pkg.setVersion(expected);
		actual = pkg.getVersion();
		Assert.assertEquals(expected, actual);
		
		// Test 3. Null version should throw IllegalArgumentException.
		//
		try {
			
			pkg.setVersion(null);
			Assert.fail("Package.setVersion(null) should throw IllegalArgumentException if the version is null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go.
		}
		

		// Test 4. Empty version should throw IllegalArgumentException.
		//
		try {
			
			pkg.setVersion("");
			Assert.fail("Package.setVersion('') should throw IllegalArgumentException if the version is empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok, good to go.
		}
		
	}
	
	/**
	 * This method tests the implemented behaviour for the getter and setter methods for the <i>model</i> property.
	 * There are no specific restrictions on the value of the <i>model</i> property, except for the fact that it
	 * cannot be set to {@literal null} when both the <i>dataset</i> and the <i>templates</i> are {@literal null} 
	 * (for the <i>templates</i> also empty counts). The getter should return what the initialisation value passed
	 * to constructor, or what it is set via the setter.
	 */
	@Test
	public void testGetSetModel() {

		// Test 1. We get from the getter what we pass in the constructor.
		//
		String version = "v1.0.0";
		Model expected = new Model("model", true);
		expected.setDescription("The test model.");
		
		Package pkg = new Package(version, expected, null, null, null);
		Model actual = pkg.getModel();
		Assert.assertEquals(expected, actual);
		
		
		// Test 2. We get from the getter what we get from the setter.
		//
		expected = new Model("anotherModel", false);
		expected.setDescription("This is another instance...");
		
		pkg.setModel(expected);
		actual = pkg.getModel();
		Assert.assertEquals(expected, actual);
		
		
		// Test 3. We set another instance, such as the dataset and we
		//         set the model to null, it should be ok.
		//
		pkg.setDataSet(new DataSet());
		pkg.setModel(null);
		Assert.assertNull(pkg.getModel());
		
		
		// Test 4. We set another instance, such as a non empty list of
		//         templates.
		//
		pkg.setModel(expected);
		List<Template> templates = new ArrayList<Template>();
		templates.add(new Template());
		pkg.setTemplates(templates);
		pkg.setDataSet(null);
		pkg.setModel(null);
		Assert.assertNull(pkg.getModel());
		
		// Test 5. We set all the other two to null, and check whether
		//         an exception is thrown.
		//
		pkg.setModel(expected);
		pkg.setTemplates(null);
		pkg.setDataSet(null);
		
		try {
			
			pkg.setModel(null);
			Assert.fail("Package.setModel(null) should throw IllegalStateException if all the DataSet and List<Template> are null.");
			
		} catch(IllegalStateException isex) {
			
			// ok good to go...
		}
		
		// Test 6. We set the template to an empty list and we expect to
		//         have the exception again.
		//
		pkg.setModel(expected);
		pkg.setTemplates(new ArrayList<Template>());
		try {
			
			pkg.setModel(null);
			Assert.fail("Package.setModel(null) should throw IllegalStateException if all the DataSet is null and List<Template> is emnpty.");
			
		} catch(IllegalStateException isex) {
			
			// ok good to go...
		}
		
	}
	
	/**
	 * This method tests the implemented behaviour for the getter and setter methods for the <i>dataset</i> property.
	 * There are no specific restrictions on the value of the <i>dataset</i> property, except for the fact that it
	 * cannot be set to {@literal null} when both the <i>model</i> and the <i>templates</i> are {@literal null} 
	 * (for the <i>templates</i> also empty counts). The getter should return what the initialisation value passed
	 * to constructor, or what it is set via the setter.
	 */
	@Test
	public void testGetSetDataSet() {
		
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String version = "v1.0.0";
		DataSet expected = new DataSet();
		
		Package pkg = new Package(version, null, null, expected, null);
		DataSet actual = pkg.getDataSet();
		Assert.assertEquals(expected, actual);
		
		
		// Test 2. We get from the getter what we get from the setter.
		//
		expected = new DataSet();
		expected.setLabel("This is another instance...");
		
		pkg.setDataSet(expected);
		actual = pkg.getDataSet();
		Assert.assertEquals(expected, actual);
		
		
		// Test 3. We set another instance, such as the model and we
		//         set the model to null, it should be ok.
		//
		pkg.setModel(new Model("model"));
		pkg.setDataSet(null);
		Assert.assertNull(pkg.getDataSet());
		
		
		// Test 4. We set another instance, such as a non empty list of
		//         templates.
		//
		pkg.setDataSet(expected);
		List<Template> templates = new ArrayList<Template>();
		templates.add(new Template());
		pkg.setTemplates(templates);
		pkg.setModel(null);
		pkg.setDataSet(null);
		Assert.assertNull(pkg.getDataSet());
		
		// Test 5. We set all the other two to null, and check whether
		//         an exception is thrown.
		//
		pkg.setDataSet(expected);
		pkg.setTemplates(null);
		pkg.setModel(null);
		
		try {
			
			pkg.setDataSet(null);
			Assert.fail("Package.setDataSet(null) should throw IllegalStateException if all the Model and List<Template> are null.");
			
		} catch(IllegalStateException isex) {
			
			// ok good to go...
		}
		
		// Test 6. We set the template to an empty list and we expect to
		//         have the exception again.
		//
		pkg.setDataSet(expected);
		pkg.setTemplates(new ArrayList<Template>());
		try {
			
			pkg.setDataSet(null);
			Assert.fail("Package.setDataSet(null) should throw IllegalStateException if all the Model is null and List<Template> is emnpty.");
			
		} catch(IllegalStateException isex) {
			
			// ok good to go...
		}
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the setter methods of the <i>templates</i> 
	 * property. There are no restrictions on the  values that can be set for <i>templates</i>, including setting
	 * {@literal null} values or an empty {@link List} except when both <i>model</i> and <i>dataset</i> are set
	 * to {@literal null}. Moreover, the getter is expected to retrieve the initialisation value passed to the 
	 * constructor, or the value set via the setter if a setter was invoked.
	 */
	@Test
	public void testGetSetTemplates() {
		
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String version = "v1.0.0";
		String description = "This is the package description.";
		List<Template> expected = new ArrayList<Template>();
		expected.add(new Template());
		
		Package pkg = new Package(version, null, expected, null, description);
		List<Template> actual = pkg.getTemplates();
		Assert.assertEquals(expected, actual);
		
		// Test 2. Can we get via the getter what we set with the setter?
		//
		//
		expected = new ArrayList<Template>();
		expected.add(new Template());
		expected.add(new Template());
		expected.add(new Template());
		
		pkg.setTemplates(expected);
		actual = pkg.getTemplates();
		Assert.assertEquals(expected, actual);
		
		// Test 3. We set null, and we should get IllegalStateException.
		//
		try {
			
			pkg.setTemplates(null);
			Assert.fail("Package.setTemplates(null) should throw IllegalStateException if DataSet and Model are both null.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok, good to go.
		}
		
		// Test 4. We set null, and we should get IllegalStateException.
		//
		try {
			
			pkg.setTemplates(new ArrayList<Template>());
			Assert.fail("Package.setTemplates([]) should throw IllegalStateException if DataSet and Model are both null and List<Template> is empty.");
			
		} catch(IllegalStateException ilsex) {
			
			// ok, good to go.
		}
		
		
		// Test 5. We set the dataset, and we check whether we can set the Templates to null.
		//
		pkg.setDataSet(new DataSet());
		pkg.setTemplates(null);
		actual = pkg.getTemplates();
		Assert.assertNull(actual);
		
		// Test 6. We set the dataset, and we check whether we can set an empty list as templates.
		//
		expected = new ArrayList<Template>();
		pkg.setTemplates(expected);
		actual = pkg.getTemplates();
		Assert.assertEquals(expected, actual);
		
		
		// Test 7. We set the Mode, and we check whether we can set the Templates to null.
		//
		pkg.setModel(new Model("model"));
		pkg.setDataSet(null);
		pkg.setTemplates(null);
		Assert.assertNull(pkg.getTemplates());
		

		// Test 8. We set the dataset, and we check whether we can set an empty list as templates.
		//
		expected = new ArrayList<Template>();
		pkg.setTemplates(expected);
		actual = pkg.getTemplates();
		Assert.assertEquals(expected, actual);
		
		
	}
	
	/**
	 * This method tests the implemented behaviour of the getter and the setter methods of the <i>description</i>
	 * property. There are no restrictions on the values that can be set for the <i>description</i>, including
	 * {@literal null}. The getter is expected to retrieve the initialisation value passed to the constructor, or
	 * the value set via the setter if a setter was invoked.
	 */
	@Test
	public void testGetSetDescription() {
		
		// Test 1. We get from the getter what we pass in the constructor.
		//
		String version = "v1.0.0";
		String expected = "This is the package description.";
		Package pkg = new Package(version, new Model("model"),  new ArrayList<Template>(), new DataSet(), expected);
		String actual = pkg.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 2. We get what we set with the setter.
		//
		expected = "This is another description of the package.";
		pkg.setDescription(expected);
		actual = pkg.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 3. Edge case 1:  null, it should be ok.
		//
		pkg.setDescription(null);
		Assert.assertNull(pkg.getDescription());
		
		// Test 4. Edge case 2: an empty description, it should be ok.
		//
		expected = "";
		pkg.setDescription(expected);
		actual = pkg.getDescription();
		Assert.assertEquals(expected, actual);
	}

}
