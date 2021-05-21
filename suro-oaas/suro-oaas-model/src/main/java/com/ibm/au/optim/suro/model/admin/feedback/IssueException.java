/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback;

/**
 * Class <b>IssueException</b>. This class extends {@link Exception} and wraps
 * all the exceptions that occur while interacting with an instance of the 
 * {@link IssueManager} implementation.
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class IssueException extends Exception {

	/**
	 * A {@literal long} value representing the specific serial version
	 * number that is used to ensure type consistency when serialising 
	 * and deserialising instances of the {@link IssueException} class.
	 */
	private static final long serialVersionUID = 7081344173979336535L;
	
	/**
	 * Initialises an instance of the {@link IssueException} with the
	 * given message.
	 * 
	 * @param message	a {@link String} representing the message that
	 * 					will provide additional information about the
	 * 					error that triggered this {@link IssueException}.
	 * 					It cannot be {@literal null} or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>message</i> is {@literal null}
	 * 									or an empty string.
	 */
	public IssueException(String message) {
		this(message, null);
	}
	
	/**
	 * Initialises an instance of {@link IssueException} with the
	 * given message and {@link Throwable} implementation.
	 * 
	 * @param message	a {@link String} representing the message
	 * 					that provides a textual information about
	 * 					the nature of the exception. It cannot be
	 * 					{@literal null} or an empty string.
	 * 
	 * @param cause		a {@link Throwable} implementation that 
	 * 					identifies the underlying cause of this
	 * 					error, if any.
	 * 
	 * @throws IllegalArgumentException	if <i>message</i> is {@literal null}
	 * 									or an empty string.
	 * 					
	 */
	public IssueException(String message, Throwable cause) {
		super(message, cause);
		
		if ((message == null) || (message.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'message' cannot be null or an empty string.");
		}
	}

}
