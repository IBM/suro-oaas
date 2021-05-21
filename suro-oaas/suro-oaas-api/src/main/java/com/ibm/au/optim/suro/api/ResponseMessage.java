package com.ibm.au.optim.suro.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A simple serializable message for wrapping in a JAX-RS {@link Response}
 * object.
 *
 */
public class ResponseMessage {
	
	/**
	 * Message string
	 */
	@JsonProperty("message")
	String message;

	/**
	 * Creates a new Response Message for the given <b>message</b>
	 * @param message A string containing a message
	 */
	public ResponseMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the message string for this document
	 * @return A string containing a message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets the message for this document
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
