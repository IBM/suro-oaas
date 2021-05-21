package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

/**
 * Class <b>CouchDbTransformationException</b>. This is the base class for all
 * the exceptions that occurr withing a document transformation.
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbTransformationException extends Exception {


	/**
	 * A {@link String} constant that contains the name of the original type
	 * used in the previous version of the data model for documents representing
	 * optimisation model instances.
	 */
	private static final long serialVersionUID = -6988576642817731322L;

	/**
	 * Initialises an instance of {@link CouchDbTransformationException}
	 * with the given message and original exception.
	 * 
	 * @param message	a {@link String} containing an informative text about the
	 * 					nature of the error that caused this exception to raise.
	 * 					It can be {@literal null}.
	 * 
	 * @param inner		a {@link Exception} instance that represents the original
	 * 					cause of the error. It can be {@literal null}.
	 */
	public CouchDbTransformationException(String message, Exception inner) {
		super(message, inner);
	}
	/**
	 * Initialises an instance of {@link CouchDbTransformationException}
	 * with the given message.
	 * 
	 * @param message	a {@link String} containing an informative text about the
	 * 					nature of the error that caused this exception to raise.
	 * 					It can be {@literal null}.
	 */
	public CouchDbTransformationException(String message) {
		
	}
	
	/**
	 * Initialises an instance of {@link CouchDbTransformationException}.
	 */
	public CouchDbTransformationException() {
		
	}
}
