/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

/**
 * <p>
 * Class <b>CouchDbDocumentTransformationException</b>. This class extends the class {@link 
 * CouchDbTransformationException} and it is designed to identify the specific class of 
 * exceptions that occur when trying to convert a <i>CouchDb</i> document from one version of 
 * the object model to the next version.
 * </p>
 * <p>
 * The relevant information contained in this class is the unique identifier of the document,
 * if available, that generated the exception, so that applications can trace back the error
 * to the data document that originated it.
 * </p>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbDocumentTransformationException extends CouchDbTransformationException {


	
	/**
	 * A {@literal long} number that univocally identifies the current
	 * version of this class. This value is used by the binary serialisation
	 * and deserialisation process to ensure that instances can be matched
	 * with a type definitino that is unchanged between the serialisatino
	 * and the deserialisation. 
	 */
	private static final long serialVersionUID = -1871099645106741549L;
	
	/**
	 * A {@link String} representing the unique identifier if any of 
	 * the <i>CouchDb</i> document that originate the exception.
	 */
	protected String id;
	
	/**
	 * Initialises an instance of {@link CouchDbDocumentTransformationException}
	 * for the given document identifier and with the given message.
	 * 
	 * @param id		a {@link String} representing the unique identifier of
	 * 					the document that originated this exception during the
	 * 					transformation process. It can be {@literal null} if 
	 * 					this information is missing.
	 * 
	 * @param message	a {@link String} containing an informative text about the
	 * 					nature of the error that caused this exception to raise.
	 * 					It can be {@literal null}.
	 * 
	 * @param inner		a {@link Exception} instance that represents the original
	 * 					cause of the error. It can be {@literal null}.
	 */
	public CouchDbDocumentTransformationException(String id, String message, Exception inner) {
		super(message, inner);
	}
	/**
	 * Initialises an instance of {@link CouchDbDocumentTransformationException}
	 * for the given document identifier and with the given message.
	 * 
	 * @param id		a {@link String} representing the unique identifier of
	 * 					the document that originated this exception during the
	 * 					transformation process. It can be {@literal null} if 
	 * 					this information is missing.
	 * 
	 * @param message	a {@link String} containing an informative text about the
	 * 					nature of the error that caused this exception to raise.
	 * 					It can be {@literal null}.
	 */
	public CouchDbDocumentTransformationException(String id, String message) {
		
	}
	
	/**
	 * Initialises an instance of {@link CouchDbDocumentTransformationException}
	 * for the given document identifier.
	 * 
	 * @param id		a {@link String} representing the unique identifier of
	 * 					the document that originated this exception during the
	 * 					transformation process. It can be {@literal null} if 
	 * 					this information is missing.
	 */
	public CouchDbDocumentTransformationException(String id) {
		
		this.id = id;
	}
	
	/**
	 * Gets the unique identifier of the document, if any, that originally generated
	 * this exception to occur.
	 * 
	 * @return	a {@link String} representing the unique identifier of the document
	 * 			if this information was availabe, or {@literal null} if not provided.
	 */
	public String getDocumentId() {
		
		return this.id;
	}

}
