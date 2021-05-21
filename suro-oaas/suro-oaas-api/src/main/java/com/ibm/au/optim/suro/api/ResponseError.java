/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.api;

/**
 * Class <b>Error</b>. This class wraps some runtime error that is occurred
 * while serving the client requests. This instance is used as a simple POJO
 * that is sent back to the client.
 */
public class ResponseError {
	/**
	 * An {@link Integer} instance representing the error code. This is matched
	 * with the HTTP Status code of the response.
	 **/
	private Integer code = null;
	/**
	 * A {@link String} providing an informative message about the error that
	 * occurred.
	 **/
	private String message = null;

	/**
	 * Gets the code of the error.
	 * 
	 * @return an {@link Integer} instance representing the error code. This is
	 *         matched with the HTTP Status code of the response.
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * Sets the code of the error.
	 * 
	 * @param code
	 *            an {@link Integer} instance representing the error code. This
	 *            is matched with the HTTP Status code of the response.
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * Gers the error message.
	 * 
	 * @return a {@link String} providing an informative message about the error
	 *         that occurred.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message.
	 * 
	 * @param message
	 *            a {@link String} providing an informative message about the
	 *            error that occurred.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Provides a {@link String} representation of the instance.
	 * 
	 * @return a {@link String} representing the JSON formatted data of the
	 *         error.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Error {\n");

		sb.append("  code: ").append(code).append("\n");
		sb.append("  message: ").append(message).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
