package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

import java.util.Map;

import org.ektorp.ViewQuery;

import com.ibm.au.jaws.data.utils.map.MapUtils;
import com.ibm.au.optim.suro.couch.security.CouchDbRule;
import com.ibm.au.optim.suro.couch.security.CouchDbSecurityEntity;
import com.ibm.au.optim.suro.couch.security.CouchDbSecurityManager;
import com.ibm.au.optim.suro.model.entities.RunDetails;

/**
 * Class <b>CouchDbRuleTransformation</b>. This class extends {@link CouchDbDocumentTransformation}
 * and specialises it to handle the update of the security rules in the database. Specifically, it
 * look for the rule that matches the <i>/strategies</i> endpoint and replaces the value of that
 * endpoint with the new </i>/templates</i> endpoint.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbRuleTransformation extends CouchDbDocumentTransformation {
	
	/**
	 * A {@link String} constant that identifies the specific endpoint to
	 * look in the collection of security rules. This is the endpoint that
	 * matches the strategies endpoint.
	 */
	private static final String STRATEGY_ENDPOINT = "r:^/strategies(.*)";
	/**
	 * A {@link String} constant that identifies the specific endpoint that
	 * represents the templates. This endpoint replaces the old strategies
	 * endpoint.
	 */
	private static final String TEMPLATE_ENDPOINT = "r:^/templates(.*)";

	/**
	 * Initialises an instance of {@link CouchDbRuleTransformation}. This
	 * constructor configures the base class to look for those documents
	 * that are related to the security infrastructure. These, have an
	 * associated view named after the {@link CouchDbSecurityManager}
	 * class.
	 */
	public CouchDbRuleTransformation() {
		
		super(CouchDbSecurityManager.class.getSimpleName());
		this.setDeleteView(false);
	}
	

	/**
	 * Gets the name of the type discriminator property for security documents. 
	 * 
	 * @return a {@link String} pointing to {@link CouchDbSecurityEntity#PROPERTY_SECURITY}.
	 */
	@Override
	protected String getOldTypeDiscriminator() {
		// TODO Auto-generated method stub
		return CouchDbSecurityEntity.PROPERTY_SECURITY;
	}

	/**
	 * Gets the name of the type discriminator property for security documents. This is the same as the
	 * one in the previous data model.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbSecurityEntity#PROPERTY_SECURITY}.
	 */
	@Override
	protected String getNewTypeDiscriminator() {

		return CouchDbSecurityEntity.PROPERTY_SECURITY;
	}


	/**
	 * Gets the updated value that is mapped by the type discriminator property for security documents.
	 * The value corresponds the {@link RunDetails} simple class name.
	 * 
	 * @return 	a {@link String} containing the current security version.
	 */
	@Override
	protected String getTypeDiscriminatorValue() {

		return CouchDbSecurityEntity.SECURITY_VERSION;
	}

	/**
	 * <p>
	 * This method specialises the transformation of the <i>CouchDb</i> documents to upgrade
	 * the security rules to the new endpoints. In particular, only one security rule needs
	 * to be changed, the one that matches the <i>/strategies</i> endpoint, which needs to be
	 * remapped to the <i>/templates</i> endpoint.
	 * </p>
	 * <p>
	 * The security entities do not have a wrapper, but the fields are already exposed as
	 * top level attributes of the document. Therefore we need to override this method rather
	 * than {@link CouchDbDocumentTransformation#transformContent(String, Map)}, which would
	 * operate on the {@link CouchDbDocumentTransformation#DOCUMENT_CONTENT_ATTRIBUTE} field.
	 * </p>
	 * 

	 * @param id		a {@link String} representing the unique identifier of the document.
	 * 
	 * @param content	a {@link Map} implementation containing the type specific portion of
	 * 					the <i>CouchDb</i> document.
	 * 
	 * @return	a {@link Map} implementation representing the transformed set of attributes.
	 * 
	 * @throws CouchDbDocumentTransformationException	if there is any error in the conversion.
	 */
	@Override
	protected Map<String, Object> transform(String id, Map<String, Object> source) throws CouchDbDocumentTransformationException {

		Map<String, Object> target = MapUtils.clone(source, true);
			
		String endpoint = (String) target.get(CouchDbRule.RULE_ENDPOINT);
		
		
		if (endpoint.equals(CouchDbRuleTransformation.STRATEGY_ENDPOINT) == true) {
			
			target.put(CouchDbRule.RULE_ENDPOINT, CouchDbRuleTransformation.TEMPLATE_ENDPOINT);
		
		} 
		
		
		return target;
	}
	
	/**
	 * This method is for transforming the content attribute of a <i>CouchDb</i> document.
	 * The security entities do not have such attribute, therefore this method is not 
	 * invoked.
	 * 
	 * @param content	a {@link Map} implementation.
	 * 
	 * @return 	a {@link Map} implementation (the one passed as argument).
	 * 
	 * 
	 * @throws CouchdbDocumentTransformationException	if there is any error in the
	 * 													conversion process.
	 * 
	 */
	@Override
	protected Map<String,Object> transformContent(String id, Map<String, Object> content) throws CouchDbDocumentTransformationException {

		// we want a true clone.
		//
		
		return content;
		
	}
	
	/**
	 * <p>
	 * This method specialises the base class implementation of the method in order 
	 * to constrain the collection of document retrieved for update only to those
	 * document that represent security rules. This is done by selecting a specific
	 * view which returns only those endpoints.
	 * </p>
	 * 
	 * @param documentType	a {@link String} representing the simple class name
	 * 						of the Java type that identifies the type of documents
	 * 						to be queried through the generated view. It cannot be
	 * 						{@literal null} or an empty string.
	 * 
	 * @return	a {@link ViewQuery} configure to retrieve a collection of documents
	 * 			via design document identifier.
	 * 
	 * @throws IllegalArgumentException		if <i>documentType</i> is {@literal null}
	 * 										or an empty string.
	 */
	@Override
	protected ViewQuery getViewQueryFor(String documentType) {
		
		ViewQuery view = super.getViewQueryFor(documentType);
		view = view.viewName("by_endpoint");
			
		
		
		return view;
	}

}
