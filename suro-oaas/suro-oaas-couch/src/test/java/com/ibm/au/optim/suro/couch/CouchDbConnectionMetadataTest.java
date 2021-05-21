/**
 * 
 */
package com.ibm.au.optim.suro.couch;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>CouchDbConnectionMetadataTest</b>. This class tests the behaviour
 * and implementation of the class {@link CouchDbConnectionMetadata}. The tested
 * class is a wrapper to store connection information.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbConnectionMetadataTest {

	/**
	 * This method tests the behaviour of the constructor of {@link CouchDbConnectionMetadata}
	 * with regards to the setup of the <i>url</i> argument when set to {@literal null}. The
	 * expected behaviour is to set the url to the default value that is represented by the
	 * property {@link CouchDbConnectionMetadata#COUCHDB_DEFAULT_URL}.
	 */
	@Test
	public void testConstructorWithNullUrl() {
		
		CouchDbConnectionMetadata metadata = new CouchDbConnectionMetadata(null,"database", "username", "password");
		
		String actualUrl = metadata.getUrl();
		
		Assert.assertEquals("Url should be set to '" + CouchDbConnectionMetadata.COUCHDB_DEFAULT_URL + "' when initialised to null.", CouchDbConnectionMetadata.COUCHDB_DEFAULT_URL, actualUrl);
	}
	/**
	 * This method tests the behaviour of the constructor of {@link CouchDbConnectionMetadata}
	 * with regards to the setup of the <i>database</i> property when set to {@literal null}.
	 * The expected behaviour is that an {@link IllegalArgumentException} is thrown.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorWithNullDatabase() {
		
		@SuppressWarnings("unused")
		CouchDbConnectionMetadata metadata = new CouchDbConnectionMetadata("http://localhost:5984/", null, "username", "password");
		Assert.fail("CouchDbConnectionMetadata constructor should fail when database parameter is null.");
	}
	
	/**
	 * This method tests the behaviour of the constructor of {@link CouchDbConnectionMetadata}
	 * with regards to valid parameters. The expected behaviour is that the corresponding getters
	 * will return the values passed as arguments.
	 */
	@Test
	public void testConstructorWithValidParameters() {
	
		String expectedUrl = "https://www.couchdb.org:5984/";
		String expectedDatabase = "datastore";
		String expectedUsername = "user";
		String expectedPassword = "pwd";
		
		CouchDbConnectionMetadata metadata = new CouchDbConnectionMetadata(expectedUrl, expectedDatabase, expectedUsername, expectedPassword);
		
		String actual = metadata.getUrl();
		Assert.assertEquals("CouchDbConnectionMetadata constructor did not set url property as expected.", expectedUrl, actual);

		actual = metadata.getDatabase();
		Assert.assertEquals("CouchDbConnectionMetadata constructor did not set database property as expected.", expectedDatabase, actual);
		
		
		actual = metadata.getUsername();
		Assert.assertEquals("CouchDbConnectionMetadata constructor did not set username property as expected.", expectedUsername, actual);

		actual = metadata.getPassword();
		Assert.assertEquals("CouchDbConnectionMetadata constructor did not set password property as expected.", expectedPassword, actual);
		
	}
	
	/**
	 * This method tests the behaviour of the {@link CouchDbConnectionMetadata} with regards
	 * to a {@literal null} <i>username</i> property. The expected behaviour is that {@literal 
	 * null} values are accepted and the corresponding getter should return {@literal null}.
	 */
	@Test
	public void testConstructorWithValidNullUsername() {
		
		CouchDbConnectionMetadata metadata = new CouchDbConnectionMetadata("https://www.couchdb.org:5984/", "db", null, "pwd");
		
		
		String actual = metadata.getUsername();
		Assert.assertNull("CouchDbConnectionMetadata constructor did not set username property as expected.", actual);
	}
	
	/**
	 * This method tests the behaviour of the {@link CouchDbConnectionMetadata} with regards
	 * to a {@literal null} <i>password</i> property. The expected behaviour is that {@literal 
	 * null} values are accepted and the corresponding getter should return {@literal null}.
	 */
	@Test
	public void testConstructorWithValidNullPassword() {
		
		CouchDbConnectionMetadata metadata = new CouchDbConnectionMetadata("https://www.couchdb.org:5984/", "db", "user", null);
		
		
		String actual = metadata.getPassword();
		Assert.assertNull("CouchDbConnectionMetadata constructor did not set username property as expected.", actual);
	}
}
