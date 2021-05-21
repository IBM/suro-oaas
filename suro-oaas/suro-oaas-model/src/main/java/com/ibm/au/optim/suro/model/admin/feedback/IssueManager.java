/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback;

/**
 * Interface <b>IssueManager</b>. This interface defines the set of operations
 * that all the implementation of issue manager need to fullfil in order to be
 * integrated into the application.
 * 
 * @author Christian Vecchiola
 */
public interface IssueManager {

	/**
	 * A {@link String} constant containing the name of the attribute that
	 * will hold a reference to the default instance of {@link IssueManager}
	 * that has been created.
	 */
	String ISSUE_MANAGER_INSTANCE = "manager:issue:instance";

	/**
	 * A {@link String} constant containing the name of the attribute that
	 * will hold the implementation of the {@link IssueManager} that has 
	 * been made available.
	 */
	String ISSUE_MANAGER_TYPE = IssueManager.class.getName();

	/**
	 * Submits an {@link Issue} to the underlying issue management system. If
	 * the operation is successful the method returns, otherwise it throws an
	 * {@link IssueException}.
	 * 
	 * @param issue	an instance of {@link Issue} that contains information 
	 * 				about the issue to be reported. It cannot be {@literal null}.
	 * 
	 * @throws IssueException	if any error occurred while trying to submit
	 * 							the given {@link Issue} to the underlying
	 * 							issue management system.
	 * 
	 * @throws IllegalArgumentException if <i>issue</i> is {@literal null}.
	 */
	void submitIssue(Issue issue) throws IssueException;
}
