/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

/**
 * Class <b>PackageException</b>. This is the base class for all the exceptions
 * that occur while interacting with {@link Package} instances. It is primarily
 * used by {@link PackageManager} implementations to notify about errors with
 * packages or their deployment.
 * 
 * @author Christian Vecchiola
 *
 */
public class PackageException extends Exception {

	/**
	 * A {@literal long} constant that is used to serialise and deserialise
	 * instance of this class and to ensure that the same versoion of the
	 * type is mapped to the instances when the serialisaton moves instances
	 * between JVMs.
	 */
	private static final long serialVersionUID = -3525219648187803593L;
	
	/**
	 * Initialises an instance of the {@link PackageException} class.
	 */
	public PackageException() {
		
	}
	/**
	 * Initialises an instance of the {@link PackageException} class with the
	 * given <i>message</i>.
	 * 
	 * @param message	a {@link String} providing additional information about
	 * 					the cause of the exception. It can be {@literal null}
	 * 					or empty.
	 */
	public PackageException(String message) {
		super(message);
		
	}
	/**
	 * Initialises an instance of the {@link PackageException} class with the
	 * given <i>message</i> and <i>inner</i> cause.
	 * 
	 * 
	 * @param message	a {@link String} providing additional information about
	 * 					the cause of the exception. It can be {@literal null}
	 * 					or empty.
	 * 
	 * @param inner		a {@link Throwable} instance that represents the original
	 * 					cause that raised this exception. It can be {@literal null}.
	 */
	public PackageException(String message, Throwable inner) {
		super(message, inner);
	}

}
