/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.couch.CouchDbConnectionManager;
import com.ibm.au.optim.suro.couch.CouchDbConnectionMetadata;
import com.ibm.au.optim.suro.couch.security.CouchDbSecurityEntity;
import com.ibm.au.optim.suro.couch.security.CouchDbSecurityManager;
import com.ibm.au.optim.suro.couch.security.CouchDbSecurityUtils;
import com.ibm.au.optim.suro.couch.security.CouchDbUser;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.jaws.data.utils.map.MapUtils;
import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * <p>
 * Class <b>CouchDbSecurityEntitiesPreparer</b>. This class implements {@link DatabasePreparer}
 * and provides the capability of loading the security entities into the database to ensure that
 * the security infrastructure is properly set up.
 * </p>
 * <p>
 * This preparer depends on the {@link CouchDbSecurityViewCreationPreparer} that generates the
 * views that are required for this component to retrieve the information needed to check for
 * the presence of security entities in the database.
 * </p>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbSecurityEntitiesPreparer implements DatabasePreparer {
	
	/**
	 * A {@link Logger} implementation that is used to collect all the log messages of instances
	 * of this class.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(CouchDbSecurityEntitiesPreparer.class);

	/**
	 * A {@link String} constant containing the path to the resource file that contains the
	 * security entities to setup.
	 */
	public static final String SECURITY_ENTITIES_FILE = "security/security_entities.json";
	
	/**
	 * A {@link String} constant containing the path to the resource file that contains the
	 * security entities that if found in the database need to be deleted.
	 */
	public static final String SECURITY_OBSOLETE_FILE = "security/security_obsolete.json";
	
	/**
	 * A {@link String} constant containing the path to the resource file that contains the
	 * security users to add.
	 */
	public static final String SECURITY_USERS_FILE = "security/security_users.json";
	
	/**
	 * A {@link String} constant containing the name of the property that can be used to store 
	 * the information about whether or not check default users. The default is set to {@literal 
	 * true}.
	 */
	public static final String SECURITY_CHECK_DEFAULT_USERS 	= "security.check.users";
	/**
	 * A {@link String} constant containing the name of the property that can be used to store
	 * the information about whether or not remove the obsolete entities. The default is set to
	 * {@literal true}.
	 */
	public static final String SECURITY_REMOVE_OBSOLETE			= "security.remove.obsolete";	

	/**
	 * A {@link ObjectMapper} instance that is used to serialise/deserialise the security entities
	 * entities from the resource file.
	 */
	protected static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * A {@link CouchDbConnectionMetadata} instance that wraps the connection information
	 * for the <i>CouchDb</i> instance.
	 */
	protected CouchDbConnectionMetadata metadata; 
	
	/**
	 * A {@link CouchDbConnector} instance that enables the interaction with the database.
	 */
	protected CouchDbConnector connector;
	
	/**
	 * A {@link List} of {@link Map} implementations containing the security entities that
	 * have been loaded from the resource file.
	 */
	protected List<Map<String,Object>> entities;
	
	/**
	 * A {@link List} of {@link Map} implementations containing the security entities that
	 * have been found missing when comparing the default set of entities with the set of
	 * entities stored in the database. This is the collection that is used by the execute
	 * method, if the check operation returns {@literal true}.
	 */
	protected List<Map<String,Object>> missingEntities;
	
	/**
	 * A {@link List} of {@link Map} implementations containing the security entities that
	 * have been marked as obsolete. These belong to a previous setup and are no more needed.
	 */
	protected List<Map<String,Object>> obsoleteEntities;
	
	/**
	 * A {@link List} of {@link Map} implementations containing the default users setup.
	 */
	protected List<Map<String,Object>> users;

	/**
	 * A {@link List} of {@link Map} implementations contaiing the security users that have
	 * have been found missing when comparing the default set of users with the set of users
	 * stored in the database. This is the collection that is used by the execute method, if
	 * the check operation returns {@literal true}.
	 */
	protected List<Map<String,Object>> missingUsers;

	
	/**
	 * A {@link ViewQuery} instance that is used to retrieve all the security entities that
	 * are stored in the database.
	 */
	protected ViewQuery defaultsQuery;
	
	/**
	 * A {@link ViewQuery} instance that is used to retrieve all the users information that
	 * are stored in the database.
	 */
	protected ViewQuery usersQuery;

	/**
	 * A {@literal boolean} flag that keeps track of whether the rules
	 */
	protected boolean rulesOk = true;
	
	/**
	 * A {@literal boolean} flag indicating whether the preparer should check for the
	 * default users and add them if they are not found. The default is set to {@literal 
	 * true}.
	 */
	protected boolean checkUsers = true;
	
	/**
	 * A {@literal boolean} flag indicating whether the preparer should check for the
	 * obsolete entities and remove them. The default is set to {@literal true}.
	 */
	protected boolean removeObsolete = true;

	/**
	 * This method check for the existence of the security entities and 
	 * if none is found it flags the need of executing the preparer.
	 * 
	 * @param env	a {@link Environment} implementation that is used to
	 * 				access the configuration properties and the shared
	 * 				objects.
	 * 
	 * @return  {@literal true} if the <i>CouchDb</i> instance configured
	 * 			does not have any security entity installed, {@link false}
	 * 			otherwise.
	 * 			
	 */
	@Override
	public boolean check(Environment env) throws Exception {

		this.metadata = CouchDbSecurityUtils.getConnectionMetadata(env);
		this.connector = CouchDbConnectionManager.getInstance(this.metadata);
		
		
		
		this.checkUsers = env.getParameter(CouchDbSecurityEntitiesPreparer.SECURITY_CHECK_DEFAULT_USERS, true);
		this.removeObsolete = env.getParameter(CouchDbSecurityEntitiesPreparer.SECURITY_REMOVE_OBSOLETE, true);
		
		boolean needsUpdate = this.checkForUpdate();

		// we already release the instance because we
		// do not need it anymore. Since neither execute
		// nor validate will be run, there will be no
		// further chance to release the connector.
		//
		if (needsUpdate == false) {
			
			CouchDbConnectionManager.releaseInstance(this.metadata);
			LOGGER.info("Security entities already present, nothing to setup.");
			
		} else {
			
			LOGGER.info("Security setup/update is needed.");
		}
		
		return needsUpdate;
	}


	/**
	 * This method performs the insertion of all the security entities that have been identified
	 * as missing during the {@link CouchDbSecurityEntitiesPreparer#check(Environment)} method.
	 * If configured to perform users check, it also adds the set of users that have been found
	 * to be missing.
	 * 
	 * @param env	a {@link Environment} implementation that is used to access the configuration 
	 * 				properties and the shared objects.
	 * 
	 * @throws Exception if any error occurs during the process.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Environment env) throws Exception {

		int processed = 0;
		
		// we do this first so that we are sure that when we add the 
		// others we do not get them back... (small optimisation).
		//
		if (this.removeObsolete == true) {
			
			@SuppressWarnings("rawtypes")
			List<Map> entities = this.connector.queryView(this.defaultsQuery, Map.class);
			for(Map<String,Object> entity : entities) {
				
				String id = (String) entity.remove(CouchDbSecurityEntity.PROPERTY_COUCHDB_ID);
				String revision = (String) entity.remove(CouchDbSecurityEntity.PROPERTY_REVISION);
				
				entity.remove(CouchDbSecurityEntity.PROPERTY_SECURITY);
				
				for(Map<String, Object> obsolete : this.obsoleteEntities) {
					
					boolean found = MapUtils.checkEquals(entity, obsolete);

					if (found == true) {

						this.connector.delete(id, revision);
						break;
					}
				}
			}
			
			LOGGER.info("Removed " + processed + " obsolete security entities.");
			processed = 0;
		}
				
		if (this.missingEntities.size() > 0) {
			
			for(Map<String,Object> entity : this.missingEntities) {
				
				Map<String,Object> prepared = CouchDbSecurityUtils.toDatabase(entity);
				this.connector.create(prepared);
				
				// we remove _id, _rev, and cdbSecurity. We are then sure that we
				// will not fail the comparison.
				// 
				this.removeExtras(prepared);

				processed++;
			}
			
			LOGGER.info("Added " + processed + " security entities");
		}
		
		
		if (this.checkUsers == true) {
			
			processed = 0;
			
			for(Map<String, Object> user : this.missingUsers) {
				
				Map<String,Object> prepared = CouchDbSecurityUtils.toDatabase(user);
				this.connector.create(prepared);

				// we remove _id, _rev, and cdbSecurity. We are then sure that we
				// will not fail the comparison.
				// 
				this.removeExtras(prepared);

				processed++;
			}
			
			LOGGER.info("Added " + processed + " security users");
		}

	}

	/**
	 * This method checks that all the missing security entities (and users if specified) 
	 * have been added. The method retrieves for each of the collection the corresponding
	 * items in the database and verifies that the default set is contained within this
	 * list. If not the validation fails.
	 * 
	 * @param env	a {@link Environment} implementation that is used to access the configuration 
	 * 				properties and the shared objects.
	 * 
	 * @return	{@literal true} if each of the security entities in the database has found 
	 * 			a corresponding one in the local entities collection, {@link  false} otherwise.
	 * 
	 * @throws Exception	if any error occurs during the process.
	 */
	@Override
	public boolean validate(Environment env) throws Exception {

		try {
			
			boolean isValid = true;
			
			List<Map<String,Object>> missing = this.getMissing(this.defaultsQuery, this.entities);
			if (missing.isEmpty() == true) {
			
				LOGGER.info("All the security entities have been verified.");
			
			} else {
				
				LOGGER.info("Security entities, validation failed: " + missing.size() + " entities are missing.");
				isValid = false;
			}
			
			if (this.removeObsolete == true) {
				
				missing = this.getMissing(this.defaultsQuery, this.obsoleteEntities);
				int delta = this.obsoleteEntities.size() -  missing.size();
				if (delta == 0) {
					
					// ok they're all missing, which means if they were present they
					// are all removed.
					LOGGER.info("All the obsolete security entities, have been removed.");
					
				} else {
					
					LOGGER.info("Security entities (obsolete), validation failed: " + delta + " entities still present. ");
					isValid = false;
				}
			}
			
			
			if (this.checkUsers == true) {
				
				missing = this.getMissing(this.usersQuery, this.users);
				if (missing.isEmpty() == true) {
					
					LOGGER.info("All the security users have been verified.");
				
				} else {
					
					LOGGER.info("Security users, validation failed: " + missing.size() + " users are missing.");
					isValid = false;
				}
				
			}
			
			return isValid;
		
		} finally {
			
			CouchDbConnectionManager.releaseInstance(this.metadata);
		}
		
	}
	

	/**
	 * This method queries the configured <i>CouchDb</i> instance has any security entity
	 * installed or not. 
	 * 
	 * @return	{@literal true} if there are no security entity installed, {@link false}
	 * 			otherwise.
	 */
	private boolean checkForUpdate() throws IOException {

		
		// we load the entities from the package.
		//
		this.entities = this.getEntities(CouchDbSecurityEntitiesPreparer.SECURITY_ENTITIES_FILE);
		this.defaultsQuery = CouchDbSecurityManager.Views.getQuery(CouchDbSecurityManager.Views.VIEW_QUERY_BY_SECURITY_DEFAULTS, null);

		this.missingEntities = this.getMissing(this.defaultsQuery, this.entities);

		boolean needsUpdate = this.missingEntities.size() > 0;
		
		if (needsUpdate == true) {
			
			LOGGER.info("Security entities: there are " + this.missingEntities.size() + " documents to add / update.");
		}
		
		if (this.checkUsers == true) {
			
			this.usersQuery = CouchDbSecurityManager.Views.getQuery(CouchDbSecurityManager.Views.VIEW_QUERY_BY_TYPE, CouchDbUser.PROPERTY_TYPE_USER);
			this.users = this.getEntities(CouchDbSecurityEntitiesPreparer.SECURITY_USERS_FILE);
			
			this.missingUsers = this.getMissing(this.usersQuery, this.users);
			
			if(this.missingUsers.size() > 0) {
				
				LOGGER.info("Security users: there are " + this.missingEntities.size() + " documents to add.");
				needsUpdate = true;
			}
			
			
		}
		
		if (this.removeObsolete == true) {
		
			this.obsoleteEntities = this.getEntities(CouchDbSecurityEntitiesPreparer.SECURITY_OBSOLETE_FILE);
		
			List<Map<String,Object>> deleted = this.getMissing(this.defaultsQuery, this.obsoleteEntities);
			
			int delta = this.obsoleteEntities.size() - deleted.size();
			
			if (delta != 0) {
				
				LOGGER.info("Security entities (obsolete): there are " + delta + " documents still present, that will be deleted.");
				needsUpdate = true;
			}
					
		}
		
		return needsUpdate;
	}
	
	/**
	 * This method retrieves the entities that are identified by the given <i>query</i>, compares them 
	 * with <i>defaults</i> and returns the subset of <i>defaults</i> that were not found in the set
	 * that has been retrieved by the database.
	 * 
	 * @param query			a {@link ViewQuery} instance that defines the subset of elements of interest
	 * 						in the database.
	 * @param defaults		a {@link List} or {@link Map} implementation that contains the information
	 * 						about the entities to find.
	 * 
	 * @return	a {@link List} of {@link Map} implementations that contains the elements in <i>defaults</i>
	 * 			that were not found in the subset of elements returned by using the given <i>query</i>. This
	 * 			reference is never {@literal null}, if there are no elements missing, the method returns an
	 * 			empty list.
	 */
	private List<Map<String,Object>> getMissing(ViewQuery query, List<Map<String,Object>> defaults) {

		@SuppressWarnings("rawtypes")
		List<Map> existingItems = this.connector.queryView(query, Map.class);
		
		List<Map<String,Object>> missing = new ArrayList<Map<String,Object>>();
		
		// if the defaults are empty, we do not need to check anything because we
		// do not need to add anything...
		//
		if (defaults.isEmpty() == false) {
		
			// if we have a fresh database, more likely we will be in the case where
			// we have to copy them all, there is no need to check whether they are
			// equal or not.
			//
			if (existingItems.isEmpty() == true)  {
				
				// this is the obvious case, where we need to copy
				// them all without checking..
				//
				for(Map<String,Object> defaultItem : defaults) {
					
					missing.add(defaultItem);
				}
			
			} else {
			
				// this is the most common case, where we already have something installed
				// and we need to check one by one that we have them in the database.
				//
				for(Map<String,Object> defaultItem : defaults) {
					
					boolean found = this.entityExists(defaultItem, existingItems);
					if (found == false) {
						
						missing.add(defaultItem);
					}
				}
			}
		
		}
		
		return missing;
	}

	/**
	 * This method checks whether the entity passed as first argument is contained in 
	 * the collection of elements passed as second argument. The check is performed by
	 * invoking {@link MapUtils#checkEquals(Map, Map)}, which compares structurally the
	 * two maps and checks that the two contains have the same fields and these fields
	 * are of the same type.
	 *  
	 * @param entity			a {@link Map} implementation representing the entity to
	 * 							search for.
	 * @param existingItems		a {@link List} of {@link Map} instances representing the
	 * 							collection of existing items.
	 * 
	 * @return	{@literal true} if the <i>entity</i> is found in <i>existingItems</i>
	 * 			{@literal false} otherwise.
	 */
	@SuppressWarnings("unchecked")
	private boolean entityExists(Map<String, Object> entity, @SuppressWarnings({ "rawtypes" }) List<Map> existingItems) {

		boolean found = false;
	
	
		for(Map<String, Object> existing : existingItems) {
			
			this.removeExtras(existing);
			
			found = MapUtils.checkEquals(entity, existing);
			if (found == true) {
				break;
			}
		}
			
		
		return found;
	}
	
	
	/**
	 * This method retrieves the collection of security entities that are stored in the resource file 
	 * that is passed as the argument and stores it into a {@link List} of {@link Map} implementations.
	 * 
	 * @param resourceFile	a {@link String} representing the path into the resource files that indicates
	 * 						the resource file to deserialise.
	 * 
	 * @return	a {@link List} of {@link Map} implementations if the file has been found and deserialised 
	 * 			or {@literal null} if it was not possible to open a stream to the resource file.
	 * 
	 * @throws IOException	if any error while reading from the stream and deserialising it occurs.
	 */
	protected List<Map<String,Object>> getEntities(String resourceFile) throws IOException {
		
		List<Map<String,Object>> read = null;
		ObjectMapper mapper = new ObjectMapper();
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(resourceFile);
		
		if (input != null) {
			
			try {
			
				read = mapper.readValue(input, new TypeReference<List<Map<String,Object>>>() {});
			
			} finally {
			
				try {
					
					input.close();
				
				} catch(IOException ie) {
				
					LOGGER.error("Error while closing the resource stream for " + CouchDbSecurityEntitiesPreparer.SECURITY_ENTITIES_FILE + ".", ie);
				}
			}
		} else {
			
			LOGGER.error("Could not find the resource file " + CouchDbSecurityEntitiesPreparer.SECURITY_ENTITIES_FILE + ".");
		}
		
		return read;
	}

	/**
	 * Removes the additional fields that that been added to <i>entity</i>
	 * to create a database document. 
	 * 
	 * @param entity	a {@link Map} implementation that contains the field
	 * 					of the document added to the <i>CouchDb</i> database.
	 */
	protected void removeExtras(Map<String,Object> entity) {
		
		entity.remove(CouchDbSecurityEntity.PROPERTY_COUCHDB_ID);
		entity.remove(CouchDbSecurityEntity.PROPERTY_REVISION);
		entity.remove(CouchDbSecurityEntity.PROPERTY_SECURITY);
	}

}
