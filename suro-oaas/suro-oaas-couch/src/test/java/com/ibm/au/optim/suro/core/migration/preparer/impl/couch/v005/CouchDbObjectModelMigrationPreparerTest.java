/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;

import com.ibm.au.optim.suro.couch.CouchDbTests;


/**r
 * Class <b>CouchDbObjectModelMigrationPreparerTest</b>. This class tests the
 * implemented behaviour of the {@link CouchObjectModelMigrationPreparer}. The
 * duty of this class is to perform the migration of the libraries
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbObjectModelMigrationPreparerTest {

	@Category(CouchDbTests.class)
	@Test
	public void testCheck() throws Exception {
		
		CouchDbObjectModelMigrationPreparer preparer = new CouchDbObjectModelMigrationPreparer();
		
		// Test 1. we check whether the dat
		//
		//
		InputStream confStream = this.getClass().getClassLoader().getResourceAsStream("couch-local-test.properties");
		
		Environment env = EnvironmentHelper.mockEnvironment(confStream, false);
		Long token = null;
		
		try {
			
			token = env.bind();
			
			// we would need to add the following steps here:
			//
			
			// 1. wipe the content of the database.
			// 2. load the expected set of data to perform the test on.
			
			
			boolean actual = preparer.check(env);
			Assert.assertTrue(actual);
		
		
		// 3. finally { wipe the content of the databae }
		
		} finally {
			
			if (token != null) {
				
				env.release(token);
			}
		}
	}
	
	@Category(CouchDbTests.class)
	@Test
	public void testValidate() throws Exception {
		
		CouchDbObjectModelMigrationPreparer preparer = new CouchDbObjectModelMigrationPreparer();

		
		// Test 1. we check that the validation works, by starting from a check operation that
		//		   triggers the execution of the preparer. We then run validate after to ensure
		//		   that the changes are applied as expected.
		//
		Properties properties = this.getProperties("couch-local-test.properties");
		Environment env  = EnvironmentHelper.mockEnvironment(properties, false);
		
		
		properties.setProperty(CouchDbObjectModelMigrationPreparer.OBJECT_MODEL_PREPARER_EMULATE, "false");
		properties.setProperty(CouchDbObjectModelMigrationPreparer.OBJECT_MODEL_PREPARER_UPGRADE, "true");
		properties.remove(CouchDbObjectModelMigrationPreparer.OBJECT_MODEL_PREPARER_TRACE);
		
		Long token = null;
		
		try {
			
			token = env.bind();
			
			// we ensure that the preparer needs to be executed.
			//
			boolean update = preparer.check(env);
			Assert.assertTrue(update);
			
			// we execute the preparer and we check that the file
			// contains something.
			//
			preparer.execute(env);
			
			
			// if we have reached this point this means that there
			// are no errors during the execution the preparer in
			// the previous method.
			//
			boolean isValid = preparer.validate(env);
			Assert.assertTrue(isValid);
			
			
			
		} finally {
			
			if (token != null) {
				
				env.release(token);
			}
		}
		
		
		
	}
	
	public void testExecuteWithEmulate() {
		
	} 
	
	public void testExecuteWithUpgrade() {
		
	}
	
	@Category(CouchDbTests.class)
	// @Test
	public void testExecuteWithTrace() throws Exception {
		
		CouchDbObjectModelMigrationPreparer preparer = new CouchDbObjectModelMigrationPreparer();
		
		// Test 1. we check whether the trace file gets written if we specify a trace file.
		//
		//
		Properties properties = this.getProperties("couch-local-test.properties");
		Environment env  = EnvironmentHelper.mockEnvironment(properties, false);
		
		
		Path traceFilePath = Files.createTempFile(preparer.getClass().getSimpleName(), ".v005");
		
		File traceFile = traceFilePath.toFile();
		
		long initialLength = traceFile.length();
		
		properties.setProperty(CouchDbObjectModelMigrationPreparer.OBJECT_MODEL_PREPARER_EMULATE, "true");
		properties.setProperty(CouchDbObjectModelMigrationPreparer.OBJECT_MODEL_PREPARER_UPGRADE, "false");
		properties.setProperty(CouchDbObjectModelMigrationPreparer.OBJECT_MODEL_PREPARER_TRACE, traceFilePath.toString());
		
		Long token = null;
		
		try {
			
			token = env.bind();
			
			// we ensure that the preparer needs to be executed.
			//
			boolean update = preparer.check(env);
			Assert.assertTrue(update);
			
			// we execute the preparer and we check that the file
			// contains something.
			//
			preparer.execute(env);
			
			Assert.assertTrue(traceFile.exists());
			Assert.assertTrue(traceFile.length() > initialLength);
			
			
			
		} finally {
			
			if (token != null) {
				
				env.release(token);
			}
			
			traceFile.delete();
		}
				
	}
	
	/**
	 * This is a simple utility method that is used to configure the environment with 
	 * a resource property file.
	 * 
	 * @param propertiesFile	a {@link String} containing the path to the resource 
	 * 							file that store the properties used to define the
	 * 							configuration parameters for the {@link Environment}.
	 * 
	 * @return	an {@link Environment} instance whose configuration parameters are 
	 * 			defined by the properties file referenced by <i>propertiesFile</i>.
	 * 			The {@link Environment} instance is not bound.
	 * 			
	 * 
	 * @throws IOException	if there is any error while reading the properties file
	 * 						from the resources.
	 */
	protected Environment getEnvironment(String propertiesFile) throws IOException {
		
		InputStream confStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
		Environment env = EnvironmentHelper.mockEnvironment(confStream);
		
		return env;
	}
	
	/**
	 * This method simply creates a {@link Properties} instance that provides access
	 * to the set of properties defined in the resource file <i>propertiesFile</i>.
	 * 
	 * @param propertiesFile	a {@link String} containing the path to the file in
	 * 							the resources that contains the properties.
	 * 
	 * @return	a {@link Properties} instance.
	 * 
	 * @throws IOException	if there is any error while reading the properties from
	 * 						resource file.
	 */
	protected Properties getProperties(String propertiesFile) throws IOException {
		
		InputStream confStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFile);
		
		Properties properties = new Properties();
		properties.load(confStream);
		
		return properties;
		
	}
	
}
