/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback.impl.redmine;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>RedmineIssue</b>. This class represents a generic issue in 
 * Redmine. It contains only a subset of the information that defines
 * an issue in the tracking system. These are primarily the attributes
 * that we will need to set for the purpose of submitting bugs or new
 * features.
 * 
 * @author Christian Vecchiola
 *
 */
public class RedmineIssue {
	
	/**
	 * A {@literal int} value that represents the "unset"value for
	 * properties that represent reference to unique identifier in
	 * <i>Redmine</i>.
	 */
	public static final int REDMINE_ISSUE_NOT_DEFINED = 0;
	
	/**
	 * A {@literal int} representing the unique identifier of the priority
	 * level for the issue.
	 */
	private int priorityId = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
	/**
	 * A {@literal int} representing the unique identifier of the project
	 * the issue belongs to.
	 */
	private int projectId = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
	/**
	 * A {@literal int} representing the unique identifier of the tracker
	 * that is used to track this type of issue.
	 */
	private int trackerId = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
	/**
	 * A {@literal int} representing the unique identifier of the status
	 * that is assigned to the issue.
	 */
	private int statusId = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
	/**
	 * A {@link String} representing the main matter of the issue.
	 */
	private String subject = null;
	/**
	 * A {@link String} containing further details about the issue.
	 */
	private String description = null;
	
	/**
	 * A {@literal int} containing the unique identifier of the category.
	 */
	private int categoryId = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
	
	
	/**
	 * Gets the unique identifier of the project the issue belongs to.
	 * 
	 * @return	a {@literal int} representing the unique identifier.
	 */
	@JsonProperty("project_id")
	public int getProjectId() {
		
		return this.projectId;
	}
	/**
	 * Sets the unique identifier of the project the issue belongs to.
	 * 
	 * @param projectId	a {@literal int} representing the unique identifier.
	 */
	@JsonProperty("project_id")
	public void setProjectId(int projectId) {
		
		this.projectId = projectId;
	}
	/**
	 * Gets the unique identifier of the priority level the issue has.
	 * 
	 * @return a {@literal int} representing the unique identifier.
	 */
	@JsonProperty("priority_id")
	public int getPriorityId() {
	
		return this.priorityId;
	}
	/**
	 * Sets the unique identifier of the priority level the issue has.
	 * 
	 * @param priorityId	a {@literal int} representing the unique
	 * 						identifier.
	 */
	@JsonProperty("priority_id")
	public void setPriorityId(int priorityId) {
		
		this.priorityId = priorityId;
	}
	/**
	 * Gets the unique identifier of the tracker used to manage the
	 * issue. A tracker is what distinguish a <i>bug</i>, from a 
	 * <i>feature</i>, or a <i>task</i>.
	 * 
	 * @return	a {@literal int} representing the unique identifier.
	 */
	@JsonProperty("tracker_id")
	public int getTrackerId() {
		
		return this.trackerId;
	}
	/**
	 * Sets the unique identifier of the tracker used to manage the
	 * issue. A tracker is what distinguish a <i>bug</i>, from a 
	 * <i>feature</i>, or a <i>task</i>.
	 * 
	 * @param trackerId	a {@literal int} representing the unique 
	 * 					identifier.
	 */
	@JsonProperty("tracker_id")
	public void setTrackerId(int trackerId) {
		
		this.trackerId = trackerId;
	}
	/**
	 * Sets the id of the status the issue has. The status identifies
	 * the phase in the issue lifecycle (<i>new</i>, <i>open</i>, ...).
	 * 
	 * @param statusId	a {@literal int} representing the unique identifier.
	 */
	@JsonProperty("status_id")
	public void setStatusId(int statusId) {
		
		this.statusId = statusId;
	}
	/**
	 * Sets the id of the status the issue has. The status identifies
	 * the phase in the issue lifecycle (<i>new</i>, <i>open</i>, ...).
	 * 
	 * @retuen	a {@literal int} representing the unique identifier.
	 */
	@JsonProperty("status_id")
	public int getStatusId() {
		
		return this.statusId;
	}
	/**
	 * Sets the category of the issue.
	 * 
	 * @param categoryId	a {@literal int} representing the unique identifier
	 * 						of the category.
	 */
	@JsonProperty("category_id")
	public void setCategoryId(int categoryId) {
		
		this.categoryId = categoryId;
	}
	/**
	 * Gets the category of the issue.
	 * 
	 * @return	a {@literal int} representing the unique identifier of the
	 * 			category of the issue.
	 */
	@JsonProperty("category_id")
	public int getCategoryId() {
		
		return this.categoryId;
	}
	/**
	 * Gets a short textual description of the issue.
	 * 
	 * @return	a {@link String} representing the subject.
	 */
	public String getSubject() {
		
		return this.subject;
	}
	/**
	 * Sets a short textual description of the issue.
	 * 
	 * @param subject	a {@link String} representing the subject.
	 */
	public void setSubject(String subject) {
		
		this.subject = subject;
	}
	/**
	 * Sets further details about the issue.
	 * 
	 * @return	a {@link String} representing the description.
	 */
	public String getDescription() {
		
		return this.description;
	}
	/**
	 * Gets further details about the issue.
	 * 
	 * @param description	a {@link String} representing the description.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}
	/**
	 * This method clones the existing instance and copies all the values
	 * that this bean has into the cloned one.
	 * 
	 * @returns a {@link RedmineIssue} representing the clone of this instance.
	 */
	public RedmineIssue clone() {
		
		RedmineIssue clone = new RedmineIssue();
		
		clone.setSubject(this.getSubject());
		clone.setDescription(this.getDescription());
		clone.setProjectId(this.getProjectId());
		clone.setPriorityId(this.getPriorityId());
		clone.setCategoryId(this.getCategoryId());
		clone.setStatusId(this.getStatusId());
		clone.setTrackerId(this.getTrackerId());
		
		return clone;
		
	}

}
