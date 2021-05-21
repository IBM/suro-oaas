/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback;

/**
 * Enum <b>IssueType</b>. Enumerates the different types of issues. An issue can
 * be a bug ({@link IssueType#BUG}), or a feature ({@link IssueType#FEATURE}), or 
 * a work item ({@link IssueType#WORK_ITEM}).
 * 
 * @author Christian Vecchiola
 *
 */
public enum IssueType {

	/**
	 * The issue is a bug, which indicates a malfuction
	 * in the application.
	 */
	BUG,
	/**
	 * The issue is a features, which indicates a desired
	 * or implemented capability of the application.
	 */
	FEATURE,
	/**
	 * The issue is a task, which indicates one or more
	 * operations to be performed either to solve a 
	 * problem or implement a capability.
	 */
	TASK,
	/**
	 * The issue is of other nature, it is used for future
	 * extensions.
	 */
	OTHER
}
