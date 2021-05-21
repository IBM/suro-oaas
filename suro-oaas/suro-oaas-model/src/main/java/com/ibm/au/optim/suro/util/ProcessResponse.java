package com.ibm.au.optim.suro.util;

/**
 * A technical object representing the outcome of an operation. It will provide an outcome (true/false), which indicates
 * the general outcome of the operation. Additionally it can also provide a message (which can be used to transfer data
 * or IDs) and an option exception if one occurred.
 *
 * @author Peter Ilfrich
 */
public class ProcessResponse {

    /**
     * The outcome of the process
     */
    private boolean result;

    /**
     * An optional message associated with the process execution
     */
    private String message;

    /**
     * An optional exception that occurred during the process execution
     */
    private Exception exception;


    /**
     * Default constructor
     */
    public ProcessResponse() {
    }

    /**
     * Creates a new process response with an outcome, a message and an exception
     * @param result - the outcome of the operation
     * @param message - the message from the operation
     * @param exception - an exception that occurred during the operation
     */
    public ProcessResponse(boolean result, String message, Exception exception) {
        this.result = result;
        this.message = message;
        this.exception = exception;
    }

    /**
     * Creates a new process response with an outcome and a message.
     * This case assumes no exceptions.
     * @param result - the outcome of the operation
     * @param message - a message from the operation
     */
    public ProcessResponse(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * Creates a new process response just with an outcome.
     * @param result - the outcome of the operation
     */
    public ProcessResponse(boolean result) {
        this.result = result;
    }

    /**
     * Getter for the outcome
     * @return - whether the operation was successful or not
     */
    public boolean isResult() {
        return result;
    }

    /**
     * Setter for the outcome
     * @param result - whether the operation was successful or not
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * Getter for the message
     * @return - a message passed from the operation
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the message
     * @param message - a message created by the operation to pass to the calling method.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for the exception
     * @return - an exception that occurred during the operation
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Setter for the exception
     * @param exception - an exception that occurred.
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
