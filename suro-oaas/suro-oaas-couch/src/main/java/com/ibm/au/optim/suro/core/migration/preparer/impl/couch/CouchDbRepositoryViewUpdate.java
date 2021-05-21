/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch;

import java.util.Map;

import org.ektorp.support.DesignDocument.View;

import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * Class <b>CouchDbRepositoryViewUpdate</b>. This class extends {@link CouchdbViewUpdate} and
 * provides additional information that are required for repository-specific views. In particular
 * these include:
 * <ul>
 * <li>the interface type implemented by the specific repository</li>
 * <li>the concrete type implementing the specific repository</li>
 * <li>the attribute in the environment to which the repository instance is mapped</li>
 * </ul>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbRepositoryViewUpdate extends CouchDbViewUpdate {
	
	/**
	 * A {@link String} representing the full type name of the interface defining the contract that
	 * a repository implementation for this view needs to abide to. It cannot be {@literal null} or
	 * an empty string.
	 */
	protected String repoType;
	/**
	 * A {@link String} representing the full type name of the actual type implementing the defined
	 * repository interface that needs to be checked against to understand whether the select repository
	 * does need view check and update.
	 */
	protected String repoInstance;
	/**
	 * A {@link String} representing the {@link Environment} attribute to which the repository instance
	 * is mapped. This value is used to 
	 */
	protected String repoAttribute;
	
	/**
	 * Initialises an instance of the {@link CouchDbRepositoryViewUpdate} class.
	 */
	public CouchDbRepositoryViewUpdate() {
		
	}
	
	/**
	 * Initialises an instance of the {@link CouchDbRepositoryViewUpdate} class. With the given name and collection
	 * of views.
	 * 
	 * @param docName		a {@link String} representing the name of the type for which the views
	 * 						are being created.
	 * @param views			a {@link Map} implementation that represents the collection of views that
	 * 						are defined in the view document identified by the given <i>viewName</i>.
	 * @param repoType		a {@link String} representing the full type name of the interface that
	 * 						defines the contract that all the repository implementation for a specific
	 * 						purpose need to abide to. It cannot be {@literal null} or an empty string.
	 * @param repoInstance	a {@link String} representing the full type name of concrete implementation
	 * 						of the repository type for which the view check and update needs to be applied.
	 * 						If the selected repository type matches this full type name then the check 
	 * 						will be performed.
	 * @param repoAttribute	a {@link String} representing the attribute name that is used to look up the
	 * 						repository implementation that has been selected for the function defined by
	 * 						the interface <i>repoType</i>.
	 * 
	 * @throws IllegalArgumentException	if <i>docName</i>, <i>repoType</i>, <i>repoInstance</i>, or <i>repoAttribute</i>
	 * 									are {@literal null} or an empty string.
	 */
	public CouchDbRepositoryViewUpdate(String docName, Map<String,View> views, String repoType, String repoInstance, String repoAttribute) {
		super(docName, views);
		
		this.setDocName(docName);
		this.setViews(views);
		this.setRepoType(repoType);
		this.setRepoInstance(repoInstance);
		this.setRepoAttribute(repoAttribute);
	}
	
	/**
	 * Sets the type name of the interface that defines the contract that all the repository implementations
	 * need to abide to. The interface type identifies a collection of repository implementations that are
	 * capable of performing a certain function. 
	 * 
	 * @param repoType	a {@link String} representing the full type name of the interface type. It cannot be
	 * 					{@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>repoType</i> is {@literal null} or an empty string.
	 */
	public void setRepoType(String repoType) {
		
		if ((repoType == null) || (repoType.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'repoType' cannot be null or an empty string.");
		}
		
		this.repoType = repoType;
	}
	/**
	 * Gets the type name of the interface that defines the contract that all the repository implementations
	 * need to abide to. The interface type identifies a collection of repository implementation that are
	 * capable of performing a certain function.
	 * 
	 * @return	a {@link String} representing the full type name of the interface type. It cannot be {@literal
	 * 			null} or an empty string.
	 */
	public String getRepoType() {
		
		return this.repoType;
	}
	/**
	 * Sets the type name of the concrete type for which the view check needs to be performed. This type is
	 * expected to implement the interface type defined by {@link CouchDbViewUpdate#getRepoType()}. If the type of
	 * the selected implementation matches the concrete type set through this method, the check for the view
	 * will be performed.
	 * 
	 * @param repoInstance	a {@link String} representing the full type name of the concrete type. It cannot 
	 * 						be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>repoInstance</i> is {@literal null} or an empty string.
	 */
	public void setRepoInstance(String repoInstance) {
		
		if ((repoInstance == null) || (repoInstance.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'repoInstance' cannot be null or an empty string.");
		}
		
		this.repoInstance = repoInstance;
	}
	/**
	 * Gets the type name of the concrete type for which the view check needs to be performed. This type is
	 * expected to implement the interface type defined by {@link CouchDbViewUpdate#getRepoType()}. If the type of
	 * the selected implementation matches the concrete type set through this method, the check for the view
	 * will be performed.
	 * 
	 * @return	a {@link String} representing the full type name of the concrete type. It cannot be {@literal 
	 * 			null} or an empty string.
	 */
	public String getRepoInstance() {
		
		return this.repoInstance;
	}
	/**
	 * Sets the name of the attribute that is used to look up the implementation of {@link CouchDbViewUpdate#getRepoType()}
	 * in the {@link Environment}. This attribute is used to retrieve the implementation whose concrete type name
	 * needs to verified against {@link CouchDbViewUpdate#getRepoInstance()} in order to decide whether to apply the
	 * view check or not.
	 * 
	 * @param repoAttribute	a {@link String} representing the attribute name. It cannot be {@literal null} or
	 * 						an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>repoAttribute</i> is {@literal null} or an empty string.
	 */
	public void setRepoAttribute(String repoAttribute) {
		
		if ((repoAttribute == null) || (repoAttribute.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'repoAttribute' cannot be null or an empty string.");
		}
		
		this.repoAttribute = repoAttribute;
	}
	/**
	 * Gets the name of the attribute that is used to look up the implementation of {@link CouchDbViewUpdate#getRepoType()}
	 * in the {@link Environment}. This attribute is used to retrieve the implementation whose concrete type name
	 * needs to verified against {@link CouchDbViewUpdate#getRepoInstance()} in order to decide whether to apply the
	 * view check or not.
	 * 
	 * @return	a {@link String} representing the attribute name. It cannot be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>repoAttribute</i> is {@literal null} or an empty string.
	 */
	public String getRepoAttribute() {
		
		return this.repoAttribute;
	}
}
