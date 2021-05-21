/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch;

import java.io.IOException;
import java.io.InputStream;

import org.ektorp.CouchDbConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.couch.CouchDbConnectionManager;
import com.ibm.au.optim.suro.couch.CouchDbConnectionMetadata;
import com.ibm.au.optim.suro.couch.security.CouchDbSecurityUtils;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * Class <b>CouchDbSecurityViewCrationPreparer</b>. This class implements the {@link DatabasePreparer}
 * interface and provides a mechanism for checking that the version of the views and if they are
 * different updates them with the ones stored within this package.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbSecurityViewCreationPreparer extends CouchDbViewCreationPreparer {
	
	/**
	 * A {@link String} representing the path to the resource file that contains the information about the
	 * updated definition of the views.
	 */
	private static final String VIEW_RESOURCE_FILE = "security/security_views.json";
	
	/**
	 * A {@link Logger} instance that collects all the messages generated by instances of this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbSecurityViewCreationPreparer.class);

	
	/**
	 * The array of {@link CouchDbViewUpdate} instances that are used to check and
	 * update the content of the <i>CouchDb</i> instance that holds the views.
	 */
	protected CouchDbViewUpdate[] views;
	
	/**
	 * A {@link CouchDbConnectionMetadata} instance that will hold the information about the 
	 * the connection to the <i>CouchDb</i> instance containing the security entities.
	 */
	protected CouchDbConnectionMetadata metadata;
	
	/**
	 * A {@link CouchDbConnector} instance that provides the connection to the <i>CouchDb</i>
	 * instance (database) managing the security entities.
	 */
	protected CouchDbConnector connector;
	
	/**
	 * This method sets the connector that is used to verify the version of the views that are stored
	 * in the <i>CouchDb</i> instance accessible through that connector and eventually update them.
	 * 
	 * @param connector	a {@link CouchDbConnector} instance providing connectivity to the <i>CouchDb</i>
	 * 					instance managing the security entities.
	 */
	public void setConnector(CouchDbConnector connector) {
		
		this.connector = connector;
	}
	

	/**
	 * This method executes the check for the views to verify that each of the views are udpated according to
	 * the version that is required by the codebase in this package. This method simply executes the base
	 * version of the method wrapped within a try {} finally {} to guarantee the release of the {@link CouchDbConnector}
	 * instance if needed.
	 * 
	 * @param env	a {@link Environment} implementation providing access to the configuration settings and
	 * 				the shared objects of the application.
	 * 
	 * @throws Exception	if there is any error occurring during the execution of the update.
	 */
	public void execute(Environment env) throws Exception {
		
		try {
			
			super.execute(env);
			
		} finally {
			
			// we release the connector only we obtained it autonomously,
			// which means that "metadata" is not null.
			//
			if (this.metadata != null) {
				
				CouchDbConnectionManager.releaseInstance(this.metadata);
			} 
			this.connector = null;
		}
	}
	
    /**
     * This method retrieves the information about the local and updated views from the resource file of the
     * module. It converts it into a collection of {@link CouchDbViewUpdate} instances that will be used to
     * manage the verification and the update of the views in the <i>CouchDb</i> instances that are backing
     * the security infrastructure of the application.
     * 
     * @return	an array of {@link CouchDbViewUpdate} containing the definition of the views that are used
     * 			by the module to access the data. If {@literal null} the information could not be retrieved.
     */
	@Override
	protected CouchDbViewUpdate[] getViews()  {
		
		CouchDbViewUpdate[] views = null;
		
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(CouchDbSecurityViewCreationPreparer.VIEW_RESOURCE_FILE);
       
        if (input != null) {
        	
        
        	try {
        	
        		ObjectMapper mapper = new ObjectMapper();
        		CouchDbViewUpdate view = mapper.readValue(input, CouchDbViewUpdate.class);
        		
        		views = new CouchDbViewUpdate[] { view };
        		
        	} catch(IOException ioex) {
        		
        		LOGGER.error("Could not deserialize view information from the resource file.", ioex);
        		
        	} finally {
        		
        		try {
        		
        			input.close();
        		
        		} catch(IOException ioex) {
        			
        			LOGGER.error("Could not close the stream of the resource file.", ioex);
        			
        		}
        	}
        }
        

		return views;
	}


	/**
	 * This method retrieves the required instance of {@link CouchDbConnector} that can be
	 * used to check and update the views defined by the given {@link CouchDbViewUpdate} 
	 * <i>vu</i>.
	 * 
     * @param viewUpdate	an instance of {@link CouchDbViewUpdate} that contains information 
     * 						about the view to be checked.
     * @param env			an instance of {@link Environment} that contains the configuration 
     * 						and shared components of the application.
     * 
     * @return	a {@link CouchDbConnector} instance that can be used to establish a connection 
     * 			to the <i>CouchDb</i> instance that stores the view information for <i>viewUpdate</i>, 
     * 			or {@literal null} if not found.
	 */
	@Override
	protected CouchDbConnector getConnector(CouchDbViewUpdate vu, Environment env)  {

		if (this.connector == null) {
			
			try {
					
				this.metadata = CouchDbSecurityUtils.getConnectionMetadata(env);
					
				this.connector = CouchDbConnectionManager.getInstance(this.metadata);
				
			} catch(Exception ex) {
				
				LOGGER.error("Could not get an instance of database connector [viewName: " + vu.getDocName() + "]", ex);
			}
		}
		
		return this.connector;
	}

}
