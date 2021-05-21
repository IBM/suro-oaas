/**
 * 
 */
package com.ibm.au.optim.suro.couch.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;

import com.ibm.au.optim.suro.couch.CouchDbConnectionManager;
import com.ibm.au.optim.suro.couch.CouchDbConnectionMetadata;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.security.Principal;
import com.ibm.au.jaws.web.security.Rule;
import com.ibm.au.jaws.web.security.Session;
import com.ibm.au.jaws.web.security.User;
import com.ibm.au.jaws.web.security.impl.DefaultSecurityManager;
import com.ibm.au.jaws.web.security.impl.DefaultSecurityPreferences;
import com.ibm.au.jaws.web.security.impl.DefaultSession;
import com.ibm.au.jaws.web.security.impl.InMemorySessionManager;
import com.ibm.au.jaws.web.security.impl.RequestContext;
import com.ibm.au.jaws.web.security.impl.SessionManager;

/**
 * Class <b>CouchDbSecurityManager</b>. This class extends {@link DefaultSecurityManager} and completes
 * the capability defined by the base class with support for CouchDb as a backing storage.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbSecurityManager extends DefaultSecurityManager {
	
	/**
	 * Class <b>Views</b>. This class wraps some useful constants about the views being used to
	 * manage the security entities and the capability to create {@link ViewQuery} instances that
	 * are can be used to query the security entities.
	 * 
	 * @author Christian Vecchiola
	 *
	 */
	public static class Views {

		/**
		 * A {@link String} constant that contains the information about the unique identifier of the
		 * design document used to store the information about the views.
		 */
		public static final String VIEW_DOCUMENT_ID = "_design/CouchDbSecurityManager";
	
		/**
		 * A {@link String} constant containing the name of the view that is used to query the database 
		 * by the <i>type</i> attribute.
		 */
		public static final String VIEW_QUERY_BY_TYPE = "by_type";
		/**
		 * A {@link String} constant containing the name of the view that is used to query the database
		 * to retrieve all the session documents and index them by the <i>token</i> attribute.
		 */
		public static final String VIEW_QUERY_BY_TOKEN = "by_token";
		/**
		 * A {@link String} constant containing the name of the view that is used to query the database
		 * to retrieve all the user document and index them by the <i>name</i> attribute.
		 */
		public static final String VIEW_QUERY_BY_NAME = "by_username";
		/**
		 * A {@link String} constant containing the name of the view that is used to query the database
		 * to retrieve all the user documents and index them by the <i>endpoint</i> attribute.
		 */
		public static final String VIEW_QUERY_BY_ENDPOINT = "by_endpoint";
		/**
		 * A {@link String} constant containing the name of the view that is used to query the database
		 * to retrieve all the rule documents that have a endpoint expressed through a regular expression.
		 */
		public static final String VIEW_QUERY_BY_REGEX = "by_regex";
		/**
		 * A {@link String} constant containing the name of the view that is used to query the database
		 * to retrieve all the security entities.
		 */
		public static final String VIEW_QUERY_BY_TAG = "by_security_tag";
		
		/**
		 * A {@link String} constant containing he name of the view that is used to query the database
		 * to retrieve all the security entities necessary for the setup.
		 */
		public static final String VIEW_QUERY_BY_SECURITY_DEFAULTS = "by_security_defaults";
		
		/**
		 * Gets the view query instance that is configured to retrieve the documents that
		 * map the specified view.
		 * 
		 * @param viewName	a {@link String} representing the name of the view. It cannot be
		 * 					{@literal null}.
		 * @param key		a {@link String} representing the specific value of the key. If
		 * 					{@literal null} the view is not configured to retrieve a specific
		 * 					document or collection of documents mapped to that key.
		 * 
		 * @return a  {@link ViewQuery} instance configured to execute the specified view.
		 */
		public static ViewQuery getQuery(String viewName, String key) {
			
			if (viewName == null) {
				
				throw new IllegalArgumentException("Parameter 'viewName' cannot be null.");
			}
			
			ViewQuery query = new ViewQuery().designDocId(Views.VIEW_DOCUMENT_ID)
								  			 .viewName(viewName)
								  			 .includeDocs(true);
			if (key != null) {
				
				query = query.key(key);
			}
			
			return query;
		}
	
	}
	/**
	 * This is the flag that controls the session management behaviour in the configuration
	 * parameters (see {@link ParameterSource}). When this value is set to {@literal true}
	 * the sessions will be managed in memory.
	 */
	public static final String VOLATILE_SESSION_PARAMETER = "com.ibm.au.optim.suro.couch.security.CouchDbSecurityManager.volatileSessions";
	
	/**
	 * A {@link CouchDbConnectionMetadata} instance that contains information about the connection to
	 * the <i>CouchDb</i> instance containining the persisted data for the security manager.
	 */
	protected CouchDbConnectionMetadata metadata;
	/**
	 * A {@link CouchDbConnector} instance that provides connection to the <i>CouchDb</i> instance
	 * that contains the persisted data for the security manager. 
	 */
	protected CouchDbConnector db;
	
	/**
	 * An {@link Object} instance used to synchronise access to the database and ensure that the
	 * calls for updating the database are successful and do not generate version mismatch.
	 */
	protected Object synchLock = new Object();
	
	/**
	 * A {@literal boolean} flag used to control the session management behaviour. If the flag
	 * is set to {@literal true} the sessions are managed in memory rather than being persisted
	 * on the database. This is particularly useful when sessions are frequently updated and we
	 * do not want to pollute the database with continuous revisions of the session document.
	 * The default is {@literal true}.
	 */
	protected boolean volatileSessions = true;
	
	/**
	 * This instance of {@link SessionManager} it is used to perform the management of sessions
	 * in memory. It will be instantiated to {@link InMemorySessionManager}.
	 */
	protected SessionManager sessions = null;

	/**
	 * This method sets the flag that controls the session management behaviour. If set to 
	 * {@literal true} the sessions will be managed in memory rather than being persisted 
	 * into the database. The default behaviour is set to {@literal true}.
	 * 
	 * @param volatileSessions	a {@literal boolean} flag indicating the session management
	 * 							behaviour.
	 */
	public void setVolatileSessions(boolean volatileSessions) {
		
		this.volatileSessions = volatileSessions;
		
		if (volatileSessions == true) {
			
			this.sessions = new InMemorySessionManager();
		}
	}
	
	/**
	 * This method returns the value of the flag that controls the session management behaviour.
	 * If set to {@literal true} the sessions will be managed in memory rather than being persisted 
	 * into the database. The default behaviour is set to {@literal true}.
	 * 
	 * @return	a {@literal boolean} flag indicating whether the session management is handled in
	 * 			memory ({@literal true}) or not ({@literal false}).
	 */
	public boolean getVolatileSessions() {
		
		return this.volatileSessions;
	}
	
	
	/**
	 * This method retrieves the connection information to the <i>CouchDb</i> instance that will
	 * store the security entities and initialises the {@link CouchDbConnector} field that will
	 * be used to interact with the database. The method then calls the base class version of the
	 * method to complete the initialisation.
	 * 
	 * @param environment	a {@link Environment} implementation that contains the properties for
	 * 						initialising the security manager.
	 */
	@Override
	protected void doBind(Environment environment) {
		
		this.metadata = CouchDbSecurityUtils.getConnectionMetadata(environment);
		try {
			
			this.db = CouchDbConnectionManager.getInstance(this.metadata);
			
			this.setVolatileSessions(environment.getParameter(CouchDbSecurityManager.VOLATILE_SESSION_PARAMETER, true));
			
			
			super.doBind(environment);
		
		} catch(Exception ex) {
			
			// we should re-throw the exception because something went 
			// wrong. 
			
			throw new RuntimeException("Could not initialise the connection to CouchDb.", ex);
		}
	}
	
	/**
	 * This method releases the {@link CouchDbConnector} instance that has been used
	 * to interact with the <i>CouchDb</i> instance storing the security entities.
	 */
	@Override
	protected void doRelease() {
		
		this.db = null;
		
		if (this.getVolatileSessions() == true) {
			
			this.sessions = null;
		}
		
		CouchDbConnectionManager.releaseInstance(this.metadata);
		
		super.doRelease();
	}
	
	/**
	 * This method overrides the base class version and queries the <i>CouchDb</i> instance
	 * configured with the security manager to retrieve the properties that are required to 
	 * construct a {@link DefaultSecurityPreferences} instance. These are all the documents
	 * that are of type <i>property</i>.
	 * 
	 * @return  a {@link DefaultSecurityPreferences} instance that has been configured with 
	 * 			the properties that were retrieved from the database. Those who were not set
	 * 			git assigned to their default values.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected DefaultSecurityPreferences getPreferences() {
	
		ViewQuery query = Views.getQuery(Views.VIEW_QUERY_BY_TYPE, "property");
		

		
		Map<String, Object> preferences = new HashMap<String,Object>();
		
		@SuppressWarnings("rawtypes")
		List<Map> properties = this.db.queryView(query, Map.class);
		for(Map<String, Object> property : properties) {
			
			String name = (String) property.get("name");
			Object value = property.get("value");
			
			preferences.put(name, value);
		}
		
		DefaultSecurityPreferences dsp = new DefaultSecurityPreferences(preferences);
		
		return dsp;
	}
	
	
	/**
	 * This method retrieves the {@link Rule} instance that matches the current endpoint request.
	 * The method first check for an rule entity in the <i>CouchDb</i> database that exactly matches
	 * the given endpoint in <i>request</i>, if not found it searches for a rule whose regular
	 * expression matches the given endpoint and returns it.
	 * 
	 * @param traceId	a {@link String} representing the unique identifier of the request being made.
	 * @param request	a {@link RequestContext} instance that contains the information about the
	 * 					request being made including the endpoint.
	 * 
	 * @return 	a {@link Rule} which exactly matches the requested endpoint, or a {@link Rule} whose 
	 * 			regula rexpression matches the given endpoint, or {@literal null}.		
	 * 	
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Rule getRule(String traceId, RequestContext request) {

		String endpoint = request.getEndpoint();
		
		ViewQuery query = Views.getQuery(Views.VIEW_QUERY_BY_ENDPOINT, endpoint);
		
		CouchDbRule rule = null;
		@SuppressWarnings("rawtypes")
		List<Map> rules = this.db.queryView(query, Map.class);
				
		
		if (rules.isEmpty() == true) {
			
			query = Views.getQuery(Views.VIEW_QUERY_BY_REGEX, null);
			
			rules = this.db.queryView(query, Map.class);
			if (rules.isEmpty() == false) {
				
				for(Map<String,Object> r : rules) {

					r = CouchDbSecurityUtils.fromDatabase(r);
					CouchDbRule cdbRule = new CouchDbRule(r);
					
					if (cdbRule.matches(endpoint) == true) {
						
						rule = cdbRule;
						break;
					}
				}
			}
			
		} else {
			
			if (rules.size() > 1) {
				
				this.warn(traceId, "Multiple rules (" + rules.size() + ") match: " + endpoint +". Selecting the first rule." );
			}
			
			Map<String,Object> attributes = (Map<String,Object>) rules.get(0);
			attributes = CouchDbSecurityUtils.fromDatabase(attributes);
			rule = new CouchDbRule(attributes);
			
		}
		
		return rule;
	}

	/**
	 * This method retrieves the user entity whose username matches the given <i>name</i>.
	 * 
	 * @return 	a {@link User} implementation that contains the information of the user matching
	 * 			the given <i>name</i> if found, or {@literal null} if there is no user matching
	 * 			the given <i>name</i>.
	 */
	@Override
	protected User getUser(String traceId, String name) {

		
		ViewQuery query = Views.getQuery(Views.VIEW_QUERY_BY_NAME, name);
		
		
		CouchDbUser user = null;
		
		
		@SuppressWarnings("rawtypes")
		List<Map> users = this.db.queryView(query, Map.class);
		
		if (users.isEmpty() == false) {
			
			if (users.size() > 1) {
				
				this.warn(traceId, "Multiple users bound to name: " + name + ", selecting first one.");
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> attributes = (Map<String,Object>) users.get(0);
			attributes = CouchDbSecurityUtils.fromDatabase(attributes);
			user = new CouchDbUser(attributes);
			
		}
		
		return user;
		
	}

	/**
	 * <p>
	 * This method creates a {@link Session} entity for the given <i>principal</i> that
	 * will expire <i>expireTime</i> milliseconds from now. The method creates a {@link 
	 * Map} implementation that contains all the information to define a session. 
	 * </p>
	 * <p>
	 * According to the value of {@link CouchDbSecurityManager#getVolatileSessions()} it 
	 * then saves the session in memory or it inserts it into the <i>CouchDb</i> database 
	 * and if the operation is successful it creates a {@link CouchDbSession} object representing 
	 * those attributes.
	 * </p>
	 * 
	 * @param traceId     a {@link String} representing an unique identifier associated 
	 *                    to the request.
	 * @param principal   a {@link Principal} instance containing the information about
	 *                    the user currently being authenticated.
	 * @param expireTime  an integer representing the session expiration time.
	 *                
	 * @return a {@link Session} instance that contains the information being constructed
	 *         for the current request.
	 */
	@Override
	protected Session createSession(String traceId, Principal principal, int expireTime) {

		String identityId = principal.getId();
		
		this.debug(traceId, "creating session for user id: '" + identityId + "'");

		Session session = null;
		
		if (this.getVolatileSessions() == true) {
			
			session = this.sessions.createSession(principal, expireTime);
			
		} else {

			// we generate the token and the id will be
			// generated by the database.
			String token = this.generateToken();
			Date now = new Date();
			now = new Date(now.getTime() + (long) (Session.ONE_MINUTE) * (long) (expireTime));
			
			Map<String, Object> attributes = CouchDbSession.createSessionData(token, now.getTime(), principal);
			
			attributes = CouchDbSecurityUtils.toDatabase(attributes);
			
			this.db.create(attributes);
			
			attributes = CouchDbSecurityUtils.fromDatabase(attributes);
			
			session = new CouchDbSession(attributes);
		
		}
		
		return session;
	}

	/**
	 * <p>
	 * Gets the {@link Session} instance that matches the given authentication token. 
	 * </p>
	 * <p>
	 * According to the value of of {@link CouchDbSecurityManager#getVolatileSessions()} it 
	 * then retrieves the session from memory or queries the <i>CouchDb</i> database for session 
	 * entities whose <i>token</i> attributes matches the given token. If found it creates a 
	 * {@link CouchDbSession} instance containing the retrieved information.
	 * </p>
	 * 
	 * @param traceId  a {@link String} representing an unique identifier associated to
	 *                 the request.
	 * @param token    a {@link String} representing the authentication token passed
	 *                 along with the request.
	 *                
	 * @return 	a {@link Session} instance that is associated to <i>token</i> or {@literal null} 
	 * 			if the session is not existing.
	 *         
	 */
	@Override
	protected Session getSession(String traceId, String token) {
		
		Session session = null;
		
		if (this.getVolatileSessions() == true) {
			
			session = this.sessions.getSession(token);
			
		} else {

			ViewQuery query = Views.getQuery(Views.VIEW_QUERY_BY_TOKEN, token);
			
			
			@SuppressWarnings("rawtypes")
			List<Map> sessions = this.db.queryView(query, Map.class);
			if (sessions.isEmpty() == false) {
				
				if (sessions.size() > 1) {
					
					this.warn(traceId, "Multiple sessions bound to token: " + token + ", selecting first one.");
				}
				
				@SuppressWarnings("unchecked")
				Map<String,Object> attributes = (Map<String,Object>) sessions.get(0);
				attributes = CouchDbSecurityUtils.fromDatabase(attributes);
				session = new CouchDbSession(attributes);
			}
		}
		
		return session;
	}

	/**
	 * <p>
	 * Removes the session that is passed as a parameter.
	 * </p>
	 * <p>
	 * According to the value of {@link CouchDbSecurityManager#getVolatileSessions()} it
	 * removes the session identified by the token from the in memory cache or removes it
	 * from the associated <i>CouchDb</i> database. In this second case the method first 
	 * retrieves the instance of the session stored in the database and then deletes it. 
	 * This is to ensure that the operation is successful and there are no revision conflicts.
	 * </p>
	 * 
	 * @param traceId  a {@link String} representing an unique identifier associated 
	 *                 to the request.
	 * @param session  an instance of the {@link Session} class containing
	 *                 the information to remove from the sessions store.
	 */
	@Override
	protected void removeSession(String traceId, Session session) {

		synchronized (this.synchLock) {
			
			
			if (this.getVolatileSessions() == true) {
			
				this.sessions.removeSession(session.getToken());
				
			} else {
				
				// we need to be sure that we are getting the most 
				// updated session to delete it.
				//
				Object zombie = this.db.find(Map.class, session.getId());
				if (zombie != null) {
				
					@SuppressWarnings("unchecked")
					Map<String,Object> latest = (Map<String,Object>) zombie; 
					this.db.delete(latest);
					
				} else {
					
					this.warn(traceId, "Session: " + session + " does not exist.");
				}
			
			}
		
		}
	}

	/**
	 * <p>
	 * Updates the session with the new values. 
	 * </p>
	 * <p>
	 * According to the value of {@link CouchDbSecurityManager#getVolatileSessions()} it updates
	 * the value of the session in the memory cache or in the associated <i>CouchDb</i> instance.
	 * </p>
	 * In this second case the method first retrieves the corresponding entity in the <i>CouchDb</i> 
	 * database to the given <i>session</i>. It then checks whether the <i>session</i> instance is 
	 * of type {@link CouchDbSession} and if so, it generates a corresponding {@link Map} implementation 
	 * and replaces the revision field with the one  that has been retrieved from the database. If the 
	 * given <i>session</i> does not implement {@link CouchDbSession} it then retrieves the single properties 
	 * and replaces them in the entity retrieved from the database before the update. This is to ensure
	 * that the update operation is successful and no conflicts are generated.
	 * </p>
	 * 
	 * @param traceId  a {@link String} representing an unique identifier associated 
	 *                 to the request.
	 * @param session  an instance of {@link Session} containing the renewed
	 *                 values (essentially the expire date).
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void updateSession(String traceId, Session session) {

		
		synchronized (this.synchLock) {

			
			if (this.getVolatileSessions() == true) {
				
				this.sessions.updateSession(session);
				
			} else {
				
				Map<String,Object> latest = this.db.find(Map.class, session.getId());
				
				if (latest != null) {

					Map<String,Object> update = null;
					// we swap the revision, and we then update the rest.
					//
					
					if (session instanceof CouchDbSession) {
					
						update = ((CouchDbSession) session).toAttributes();
						
						update.put(CouchDbSecurityEntity.PROPERTY_REVISION, latest.get(CouchDbSecurityEntity.PROPERTY_REVISION));
						
						update = CouchDbSecurityUtils.toDatabase(update);
						
					} else {
						
						// here we do not get to call toDatabase because this comes straight from
						// the database.
						//
						update = latest;
						update.put(DefaultSession.SESSION_TOKEN, session.getToken());
						update.put(DefaultSession.SESSION_EXPIRE, session.getExpireDate().getTime());
						
						Principal principal = session.getPrincipal();
						if (principal != null) {
							update.put(DefaultSession.SESSION_PRINCIPAL, principal.getAttributes(true));
						
						} else {
							
							update.put(DefaultSession.SESSION_PRINCIPAL, null);
						}
					}
					
					this.db.update(update);
				
				} else {
					
					this.error(traceId, "Session (id: " + session.getId() + ") does not exist", null);
				}
			}
		}
		
		
	}

	

	
	
	

}
