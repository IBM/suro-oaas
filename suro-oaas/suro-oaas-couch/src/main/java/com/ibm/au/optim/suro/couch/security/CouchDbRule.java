/**
 * 
 */
package com.ibm.au.optim.suro.couch.security;

import java.util.Map;

import com.ibm.au.jaws.web.security.impl.DefaultRule;

/**
 * Class <b>CouchDbRule</b>. This class extends {@link DefaultRule} and implements
 * the minimal logic that is necessary to persist session entities in <i>CouchDb</i>.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbRule extends DefaultRule implements CouchDbSecurityEntity {
	
	/**
	 * A {@link String} constant that identifies the type of this class
	 * of security entities.
	 */
	public static final String PROPERTY_TYPE_RULE = "rule";
	/**
	 * A {@link String} containing the unique identifier of the rule.
	 */
	protected String id;
	/**
	 * A {@link String} representing the revision number that
	 * this version of the document has. This information is
	 * quite important when pushing updates to <i>CouchDb</i>
	 * as this number needs to match the one that is stored in
	 * the database to successfully update.
	 */
	protected String rev;
	
	/**
	 * Intialises an instance of {@link CouchDbRule} from the given collection of
	 * <i>attributes</i>.
	 * 
	 * @param attributes	a {@link Map} implementation that contains all the properties
	 * 						that are required to build an instance of {@link CouchDbRule}.
	 * 
	 * 
	 * @throws IllegalArgumentException 	if <i>attributes</i> is {@literal null} or
	 * 										it does contain some invalid property or 
	 * 										misses some of the required ones.
	 */
	public CouchDbRule(Map<String,Object> attributes) {
		super(attributes);
		
		if (attributes.containsKey(CouchDbSecurityEntity.PROPERTY_ID) == false) {

			throw new IllegalArgumentException("Parameter 'id' canont be null.");
		}
		
		this.id = (String) attributes.get(CouchDbSecurityEntity.PROPERTY_ID);
		
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
	 * Gets the unique identifier of the rule document. 
	 * 
	 * @return a {@link String} representing the unique identifier.
	 */
	@Override
	public String getId() {
		
		return this.id;
	}	
	/**
	 * Converts the current instance into a {@link Map} implementation that contains
	 * the collection of attributes defining a {@link CouchDbRule}.
	 * 
	 * @return 	a {@link Map} implementation containing all the attributes that define
	 * 			a {@link CouchDbRule}.
	 */
	@Override
	public Map<String,Object> toAttributes() {
		
		Map<String, Object> attributes = super.toAttributes();
		
		attributes.put(CouchDbSecurityEntity.PROPERTY_ID, this.id);
		attributes.put(CouchDbSecurityEntity.PROPERTY_REVISION, this.rev);
		attributes.put(CouchDbSecurityEntity.PROPERTY_TYPE, CouchDbRule.PROPERTY_TYPE_RULE);
		
		return attributes;
	}
	
	/**
	 * Gets the value of the {@link CouchDbSecurityEntity#PROPERTY_TYPE} property for
	 * {@link CouchDbRule} entities.
	 * 
	 * @return {@link CouchDbRule#PROPERTY_TYPE_RULE}.
	 */
	@Override
	public String getType() {
		
		return CouchDbRule.PROPERTY_TYPE_RULE;
	}
	


}
