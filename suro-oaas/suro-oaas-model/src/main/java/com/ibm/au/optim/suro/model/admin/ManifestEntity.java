package com.ibm.au.optim.suro.model.admin;

import java.util.List;

/**
 * Class <b>ManifestEntity</b>. This class defines a resource that is composed
 * by a file name indicating the main content of the entity and a collection of
 * attachments, that are optional to the resource.
 * 
 * @author Christian Vecchiola.
 *
 */
public class ManifestEntity {
	
	/**
	 * A {@link String} representing the absolute or relative path of the 
	 * resource. If the path is relative, the location of the manifest is
	 * considered to be the root path. It cannot be {@literal null} or an
	 * empty string.
	 */
	protected String path;
	
	/**
	 * A {@link List} implementation containing the list of attachments
	 * that are associated to the resource describe by this instance. It
	 * can be {@literal null} or empty.
	 */
	protected List<String> attachments;
	
	/**
	 * Initialises an instance of {@link ManifestEntity} for the resource
	 * identified by the given <i>path</i> and <i>attachments</i>.
	 * 
	 * @param path			a {@link String} representing the absolute or 
	 * 						relative path of the resource. If the path is 
	 * 						relative, the directory location of the manifest 
	 * 						is considered to be the root path. It cannot be 
	 * 						{@literal null} or an empty string.
	 * 
	 * @param attachments	a {@link List} implementation containing the list 
	 * 						of attachments that are associated to the resource 
	 * 						describe by this instance. It can be {@literal null} 
	 * 						or empty.
	 * 
	 * @throws IllegalArgumentException	if <i>path</i> is {@literal null} or an
	 * 									empty string.
	 */
	public ManifestEntity(String path, List<String> attachments) {
		
		this.setPath(path);
		this.setAttachments(attachments);
	}

	/**
	 * Gets the location of the resource defined by this instance of {@link ManifestEntity}.
	 * The path can either be relative or absolute. If relative the directory location of
	 * the manifest file is considered to be the root directory.
	 * 
	 * @return	a {@link String} representing the path to the resource. It cannot be {@literal 
	 * 			null} or an empty string.	
	 */
	public String getPath() {
		
		return this.path;
	}
	
	/**
	 * Sets the location of the resource defined by this instance of {@link ManifestEntity}.
	 * The path can either be relative or absolute. If relative the directory location of
	 * the manifest file is considered to be the root directory.
	 * 
	 * @param path	a {@link String} representing the path to the resource. It cannot be {@literal 
	 * 				null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>path</i> is {@literal null} or empty.
	 */
	public void setPath(String path) {
		
		if ((path == null) || (path.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'path' cannot be null.");
		}
		
		this.path = path;
	}

	/**
	 * Gets the list of attachments that are associated to the resource defined by this
	 * instance of {@link ManifestEntity}.
	 * 
	 * @return	a {@link List} containing the paths to the attachments associated to the
	 * 			resource. It can be {@literal null} or an empty string.
	 */
	public List<String> getAttachments() {
		
		return this.attachments;
	}

	/**
	 * Sets the list of attachments that are associated to the resource defined by this
	 * instance of {@link ManifestEntity}.
	 * 
	 * @param attachments	a {@link List} containing the paths to the attachments associated 
	 * 						to the resource. It can be {@literal null} or an empty string.
	 */
	public void setAttachments(List<String> attachments) {

		this.attachments = attachments;
	}
}