/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.notify;

/**
 * Exception for wrapping events raised from invoking notifiers
 *
 * @author unknown
 */
public class NotifierException extends Exception {


	private static final long serialVersionUID = -1185666011220512188L;

	/**
	 * Creates a new notifier exception with the provided message.
	 * @param message - the message of the exception
	 */
	public NotifierException(String message) {
		this(message, null);
	}

	/**
	 * Creates a new notifier exception with the provided message and the provided cause of the exception (which is
	 * another exception).
	 * @param message - the message of the exception
	 * @param cause - the root cause for this exception (another exception that caused this Exception from being created)
	 */
	public NotifierException(String message, Throwable cause) {
		super(message, cause);
	}
}
