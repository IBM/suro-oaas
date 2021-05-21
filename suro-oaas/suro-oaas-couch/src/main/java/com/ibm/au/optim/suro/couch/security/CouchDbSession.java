/**
 * 
 */
package com.ibm.au.optim.suro.couch.security;


import java.util.HashMap;
import java.util.Map;

import com.ibm.au.jaws.web.security.Principal;
import com.ibm.au.jaws.web.security.impl.DefaultSession;

/**
 * Class <b>CouchDbSession</b>. This class extends {@link DefaultSession} and implements
 * the minimal logic that is necessary to persist session entities in <i>CouchDb</i>.
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbSession extends DefaultSession implements CouchDbSecurityEntity {
	
	/**
	 * A {@link String} constant that identifies the type of this class
	 * of security entities.
	 */
	public static final String PROPERTY_TYPE_SESSION = "session";
	
	/**
	 * A {@link String} representing the revision number that
	 * this version of the document has. This information is
	 * quite important when pushing updates to <i>CouchDb</i>
	 * as this number needs to match the one that is stored in
	 * the database to successfully update.
	 */
	protected String rev;
	
	/**
	 * Intialises an instance of {@link CouchDbSession} from the given collection of
	 * <i>attributes</i>.
	 * 
	 * @param attributes	a {@link Map} implementation that contains all the properties
	 * 						that are required to build an instance of {@link CouchDbSession}.
	 * 
	 * 
	 * @throws IllegalArgumentException 	if <i>attributes</i> is {@literal null} or
	 * 										it does contain some invalid property or 
	 * 										misses some of the required ones.
	 */
	public CouchDbSession(Map<String,Object> attributes) {
		super(attributes);
		
		if (attributes.containsKey(CouchDbSecurityEntity.PROPERTY_REVISION) == false) {
			
			throw new IllegalArgumentException("Parameter '_rev' canont be null.");
		}
		
		this.rev = (String) attributes.get(CouchDbSecurityEntity.PROPERTY_REVISION);
	}

	
	/**
	 * Gets the version of the document. This is particular important when submitting
	 * updates to <i>CouchDb</i> since such number needs to be the same as the one
	 * that is store in the database in order to make the update successful.
	 * 
	 * @return a  {@link String} representing the revision number.
	 */
	@Override
	public String getRevision() {
		
		return this.rev;
	}
	
	/**
	 * Converts the current instance into a {@link Map} implementation that contains
	 * the collection of attributes defining a {@link CouchDbSession}.
	 * 
	 * @return 	a {@link Map} implementation containing all the attributes that define
	 * 			a {@link CouchDbSession}.
	 */
	@Override
	public Map<String, Object> toAttributes() {
		
		Map<String, Object> attributes = super.toAttributes();
		
		attributes.put(CouchDbSecurityEntity.PROPERTY_REVISION, this.rev);
		attributes.put(CouchDbSecurityEntity.PROPERTY_TYPE, "session");
		
		return attributes;
	}

	/**
	 * This method generate a partial session that is used to initialise a session entity in
	 * the <i>CouchDb</i> database.
	 * 
	 * @param token			a {@link String} representing the session token.
	 * @param expireDate	a {@literal long} representing the time in milliseconds (since 
	 * 						1970)  of the expire time of the session.
	 * @param principal		a {@link Principal} implementation that provides information about 
	 * 						the identity attached to the session.
	 * 
	 * @return	a {@link Map} implementation containing the information about the session data 
	 * 			required to initialise the session in <i>CouchDb</i>.
	 * 
	 * @throws IllegadlArgumentException if <i>token</i> or <i>principal</i> are {@literal null}.
	 * 			
	 */
	public static Map<String, Object> createSessionData(String token, long expireDate, Principal principal) {
		
		if (token == null) {
			
			throw new IllegalArgumentException("Parameter 'token' cannot be null.");
		}
		
		if (principal == null) {
			
			throw new IllegalArgumentException("Parameter 'principal' cannot be null.");
		}
		
		Map<String,Object> attributes = new HashMap<String, Object>();
		
		attributes.put(DefaultSession.SESSION_EXPIRE, expireDate);
		attributes.put(DefaultSession.SESSION_TOKEN, token);
		attributes.put(DefaultSession.SESSION_PRINCIPAL, principal.getAttributes(true));
		attributes.put(CouchDbSecurityEntity.PROPERTY_TYPE, CouchDbSession.PROPERTY_TYPE_SESSION);
		
		return attributes;
	}
	/**
	 * Gets the value of the {@link CouchDbSecurityEntity#PROPERTY_TYPE} property for
	 * {@link CouchDbSession} entities.
	 * 
	 * @return {@link CouchDbSession#PROPERTY_TYPE_SESSION}.
	 */
	@Override
	public String getType() {
		
		return CouchDbSession.PROPERTY_TYPE_SESSION;
	}
	
	/**
	 * Clones the current instance of {@link CouchDbSession}.
	 * 
	 * @returns a deep copy of the {@link CouchDbSession} instance.
	 */
	@Override
	public Object clone() {
		
		Map<String,Object> attributes = this.toAttributes();
		CouchDbSession clone = new CouchDbSession(attributes);
		return clone;
	}
	



}
