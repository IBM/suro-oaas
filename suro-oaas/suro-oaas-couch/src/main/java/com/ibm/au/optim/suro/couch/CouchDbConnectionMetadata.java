/**
 * 
 */
package com.ibm.au.optim.suro.couch;

/**
 * Class <b>CouchDbConnectionMetadata</b>. This class contains the useful information
 * that define a connection: url, database, username, and password. Instances of this 
 * class are used by the {@link CouchDbConnectionManager} to minimise the number of
 * connections to <i>CouchDb</i> instances.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbConnectionMetadata {
	
	
    /**
     * A {@link String} constant that contains the default URL for a <i>CouchDb</i> instance.
     * This url points to the port 5984 of the localhost.
     */
    public static final String COUCHDB_DEFAULT_URL = "http://localhost:5984"; 
    
    
    /**
     * A {@link String} constant that contains the name of the parameter that is used to retrieve
     * the url specified by the user of the <i>CouchDb</i> instance.
     */
    public static final String COUCHDB_URL = "couchdb.url";
    /**
     * A {@link String} constant that contains the name of the parameter that is used to retrieve
     * the user name required to connect to the <i>CouchDb</i> instance. This is an optional parameter.
     */
    public static final String COUCHDB_USERNAME = "couchdb.username";
    /**
     * A {@link String} constant that contains the name of the parameter that is used to retrieve
     * the password required to connect to the <i>CouchDb</i> instance. This is an optional parameter.
     */
    public static final String COUCHDB_PASSWORD = "couchdb.password";
    /**
     * A {@link String} constant that contains the name of the parameter that is used to specify
     * the database in the <i>CouchDb</i> instance that the client needs to connect to.
     */
    public static final String COUCHDB_DATABASE = "couchdb.database";
	
    /**
     * A {@link String} representing the url to the <i>CouchDb<i> instance.
     */
	protected String url;
	/**
	 * A {@link String} representing the name of the database referenced by the connection.
	 */
	protected String database;
	/**
	 * A {@link String} representing the username (if any) used to authenticate.
	 */
	protected String username;
	/**
	 * A {@link String} representined the password (if any) used to authenticate.
	 */
	protected String password;
	
	/**
	 * Initialises an instance of {@link CouchDbConnectionMetadata} with the givne parameters.
	 * 
	 * @param url      a {@link String} representing the url to the <i>CouchDb</i> server hosting the database. It
     *                 can be {@literal null} or empty and in that case it is set to the default value {@link 
     *                 CouchDbConnectionMetadata#COUCHDB_DEFAULT_URL}.
     * @param database a {@link String} representing the name of the database that the connection should give access
     *                 to. It cannot be {@literal null}.
     * @param username a {@link String} representing the username required to connect to the <i>CouchDb</i> instance.
     *                 It can be {@literal null}.
     * @param password a {@link String} representing the password required to connect to the <i>CouchDb</i> instance.
     *                 It can be {@literal null}.
	 */
	public CouchDbConnectionMetadata(String url, String database, String username, String password) {
		
		if ((url == null) || (url.isEmpty() == true)) {
			
			this.url = CouchDbConnectionMetadata.COUCHDB_DEFAULT_URL;
		} else {
			
			this.url = url;
		}
		
		if (database == null) {
			
			throw new IllegalArgumentException("Parameter 'database' cannot be null.");
		}
		
		this.database = database;
		this.username = username;
		this.password = password;
	}
	/**
	 * Gets a {@link String} representing the url (host and port) to the <i>CouchDb</i> instance
	 * that defines the connection information.
	 * 
	 * @return	a {@link String}, it is never {@literal null}.
	 */
	public String getUrl() {
		
		return this.url;
	}
	/**
	 * Gets a {@link String} representing the name of the user that will be used to authenticate
	 * against the <i>CouchDb</i> instance defined by this connection information.
	 * 
	 * @return	a {@link String}, it can be {@literal null} if no user credentials are required. 
	 */
	public String getUsername() {
		
		return this.username;
	}

	/**
	 * Gets a {@link String} representing the password of the user that will be used to authenticate
	 * against the <i>CouchDb</i> instance defined by this connection information.
	 * 
	 * @return	a {@link String}, it can be {@literal null} if no user credentials are required. 
	 */
	public String getPassword() {
		
		return this.password;
	}

	/**
	 * Gets a {@link String} representing the name of the database references in the <i>CouchDb</i>
	 * instance defined by this connection information.
	 * 
	 * @return	a {@link String}, it is never null. 
	 */
	public String getDatabase() {
		
		return this.database;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CouchDbConnectionMetadata)) {
			return false;
		}

		CouchDbConnectionMetadata ccmd = (CouchDbConnectionMetadata) obj;
		return  ccmd.getDatabase().equals(this.database) &&
				ccmd.getUrl().equals(this.url) &&
				ccmd.getUsername().equals(this.username) &&
				ccmd.getPassword().equals(this.password);
	}

	/**
     * This function computes the hash of the four parameter passed to provide a unique identifier for the combination
     * of parameters. This is used to uniquely identify connection that because have the same parameters can be reused
     * across clients that require them.
     **/
	@Override
	public int hashCode() {
		
        int hashCode = this.url.hashCode() + 
        			   (this.username != null ? this.username.hashCode() : 0) + 
        			   (this.password != null ? this.password.hashCode() : 0) + 
        			   this.database.hashCode();
      
        return hashCode;

	}
	/**
	 * Provides a string representation of the {@link CouchDbConnectionMetadata} instance.
	 * 
	 * @return	a {@link String} in the following form:
	 * 			<p>
	 * 			[url: <i>url</i>, database: <i>database</i>, username: <i>username|&lt;null&gt;</i>, password: <i>********|&lt;null&gt;</i>] 
	 * 			</p>
	 * 			
	 */
	@Override
	public String toString() {
		
		return  "[url: " + this.url + 
				", database: " + this.database + 
				", username: " + (this.username != null ? this.username : "<null>") + 
				", password: " + (password != null ? "******" : "<null>") + "].";
	}
	

}
