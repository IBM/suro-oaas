/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.admin.feedback.Issue;
import com.ibm.au.optim.suro.model.admin.feedback.IssueType;

/**
 * Class <b>SimpleIssue</b>. This class implements the {@link Issue}
 * interface and provides a simple bean that fullfill the contract defined
 * by the interface, with no additional features. It can be used in any
 * context where the {@link Issue} implementations are needed.
 * 
 * @author Christian Vecchiola
 *
 */
public class SimpleIssue implements Issue {
	
	/**
	 * A {@link IssueType} value representing the type of the issue.
	 */
	protected IssueType type = IssueType.BUG;
	
	/**
	 * A {@link String} representing the name of the issue.
	 */
	protected String name;
	/**
	 * A {@link String} representing the description for the issue.
	 */
	protected String description;

	/**
	 * Initialises an instance of {@link SimpleIssue} with the given <i>name</i>.
	 * 
	 * @param name	a {@link String} representing the name of the issue. It cannot
	 * 				be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or an 
	 * 									empty string.
	 */
	@JsonCreator
	public SimpleIssue(@JsonProperty("name") String name) {
		
		this.setName(name);
	}
	/**
	 * Initialises an instance of {@link SimpleIssue} with the given <i>name</i>,
	 * <i>description</i> and <i>type</i>.
	 * 
	 * @param name			a {@link String} representing the name of the issue. 
	 * 						It cannot be {@literal null} or an empty string.
	 * @param description	a {@link String} representing the description of the
	 * 						issue.
	 * @param type 			a {@link IssueType} value representing the type of the
	 * 						issue.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or an 
	 * 									empty string.
	 */
	public SimpleIssue(String name, String description, IssueType type) {
		
		this.setName(name);
		this.setDescription(description);
		this.setType(type);
	}
	
	/**
	 * Gets the type of the issue.
	 * 
	 * @return	the value of {@link IssueType} that identifies
	 * 			the type of the issue.
	 */
	@Override
	public IssueType getType() {
		
		return this.type;
	}
	
	/**
	 * Sets the type of the issue.
	 * 
	 * @param type	a {@link IssueType} value indicating the nature
	 * 				of the issue.
	 */
	@Override
	public void setType(IssueType type) {
		
		this.type = type;
	}
	
	/**
	 * Gets the name of the issue. This provides short textual
	 * information about the issue, it cannot be {@literal null}
	 * or an empty {@link String}.
	 * 
	 * @return	a {@link String} representing the name of the issue.
	 */
	@Override
	public String getName() {
		
		return this.name;
	}

	/**
	 * Sets the name of the issue. This provides short textual
	 * information about the issue. 
	 * 
	 * @param name	a {@link String} representing the name of the issue.
	 * 				It cannot be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>name</i> is {@literal null}
	 * 									or an empty string.
	 */
	@Override
	public void setName(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		this.name = name;

	}
	
	/**
	 * Gets additional details about the issue.
	 * 
	 * @return	a {@link String} containing further information that can
	 * 			detail and provide more context for the issue.
	 */
	@Override
	public String getDescription() {
		
		return this.description;
	}
	/**
	 * Sets the description for the issue. This method enables the caller
	 * to enrich the issue with additional details that can help defining
	 * the context of the issue.
	 * 
	 * @param description	a {@link String} providing further information
	 * 						about the issue. It can be {@literal null} or
	 * 						an empty string.
	 */
	@Override
	public void setDescription(String description) {
		
		this.description = description;

	}

}
