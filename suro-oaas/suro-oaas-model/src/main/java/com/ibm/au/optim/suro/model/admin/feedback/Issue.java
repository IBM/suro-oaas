package com.ibm.au.optim.suro.model.admin.feedback;

/**
 * Interface <b>Issue</b>. An issue represent an entity that is managed by
 * projec tracking system. It can be a bug, feature, or a task. The issue
 * is defined at the very least by a:
 * <ul>
 * <li>type, which defines the nature of the issue</li>
 * <li>name, which provides a short textual information about the issue</li>
 * <li>description, which provides further details about it</li>
 * </ul>
 * 
 * @author Christian Vecchiola
 */
public interface Issue {
	
	/**
	 * Gets the type of the issue.
	 * 
	 * @return	the value of {@link IssueType} that identifies
	 * 			the type of the issue.
	 */
	public IssueType getType();
	
	/**
	 * Sets the type of the issue.
	 * 
	 * @param type	a {@link IssueType} value indicating the nature
	 * 				of the issue.
	 */
	public void setType(IssueType type);
	
	/**
	 * Gets the name of the issue. This provides short textual
	 * information about the issue, it cannot be {@literal null}
	 * or an empty {@link String}.
	 * 
	 * @return	a {@link String} representing the name of the issue.
	 */
	public String getName();
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
	public void setName(String name);
	
	/**
	 * Gets additional details about the issue.
	 * 
	 * @return	a {@link String} containing further information that can
	 * 			detail and provide more context for the issue.
	 */
	public String getDescription();
	
	/**
	 * Sets the description for the issue. This method enables the caller
	 * to enrich the issue with additional details that can help defining
	 * the context of the issue.
	 * 
	 * @param description	a {@link String} providing further information
	 * 						about the issue. It can be {@literal null} or
	 * 						an empty string.
	 */
	public void setDescription(String description);

}
