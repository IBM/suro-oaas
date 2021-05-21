/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>Attachment</b>. This class holds metadata about a given
 * attachment.
 *
 * @author Peter Ilfrich & Christian Vecchiola
 */
public class Attachment implements Cloneable {
	
	/**
	 * A {@link String} instance containing the name of the attachment. This cannot
	 * be {@literal null} or an empty string.
	 */
	@JsonProperty("name")
	protected String name;

	/**
	 * A {@literal long} containing the size in bytes of the attachment described
	 * this {@link Attachment}.
	 */
    @JsonProperty("contentLength")
    protected long contentLength;

    /**
     * A {@link String} containing the MIME type of the attachment described
     * by this {@link Attachment}.
     */
    @JsonProperty("contentType")
    protected String contentType;
    
    /**
     * This is a {@link InputStream} implementation that is used to hold the
     * content of the attachment. The property is not meant to be persistent
     * but to hold the data temporarily.
     */
    @JsonIgnore
    protected InputStream stream;


    /**
     * Creates an instance of the {@link Attachment} type with the given
     * attributes.
     * 
     * @param name			a {@link String} representing the name of the
     * 						attachment. It cannot be {@literal null} or an
     * 						empty string.
     * @param contentLength	a {@literal long} representing the size in bytes of
     * 						the attachment described by this entity.
     * 
     * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or
     * 									an empty string, or <i>contentLength</i>
     * 									is negative.
     */
    @JsonCreator
    public Attachment(@JsonProperty("name") String name, @JsonProperty("contentLength") long contentLength) {
    	this(name, contentLength, null);
    	
    }

    /**
     * Creates an instance of the {@link Attachment} type with the given
     * attributes.
     * 
     * @param name			a {@link String} representing the name of the
     * 						attachment. It cannot be {@literal null} or an
     * 						empty string.
     * @param contentLength	a {@literal long} representing the size in bytes of
     * 						the attachment described by this entity.
     * @param contentType	a {@link String} representing the MIME type of the
     * 						attachment described by this entity.
     * 
     * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or
     * 									an empty string, or <i>contentLength</i>
     * 									is negative.
     */
    public Attachment(String name, long contentLength, String contentType) {
    	
    	this.setName(name);
        this.setContentLength(contentLength);
        this.setContentType(contentType);
    }

    /**
     * Sets the name of the attachment. 
     * 
     * @param name	a {@link String} representing the name of the attachment. It
     * 				cannot be {@literal null} or an empty string.
     * 
     * @throws IllegalArgumentException	if <i>name</i> is {@literal null} or an
     * 									empty string.
     */
    public void setName(String name) {
		
    	if ((name == null) || (name.isEmpty() == true)) {
    		
    		throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
    	}
    	
    	this.name = name;
	}
    
    /**
     * Gets the name of the attachment.
     * 
     * @return	a {@link String} containing the name of the attachment. It cannot
     * 			be {@literal null} or an empty string.
     */
    public String getName() {
    	
    	return this.name;
    }

	/**
     * Gets the size in bytes of the attachment.
     * 
     * @return	a {@literal long} representing the size in bytes of the attachment.
     */
    public long getContentLength() {
    	
        return this.contentLength;
    }

    /**
     * Sets the size in bytes of the attachment.
     * 
     * @param contentLength	a {@literal long} representing the size in bytes of the
     * 						attachment.
     * 
     * @throws IllegalArgumentException	if <i>contentLength</i> is negative.
     */
    public void setContentLength(long contentLength) {
    	
    	if (contentLength < 0) {
    		
    		throw new IllegalArgumentException("Parameter 'contentLength' cannot be negative.");
    	}
    	
        this.contentLength = contentLength;
    }

    /**
     * Gets the MIME type of the content of the attachment.
     * 
     * @return a {@link String} representing the MIME type of the attachment.
     */
    public String getContentType() {
    	
        return this.contentType;
    }

    /**
     * Sets the MIME type of the content of the attachment.
     * 
     * @param contentType 	a {@link String} representing the MIME type of the
     * 						content of the attachment.
     */
    public void setContentType(String contentType) {
    	
        this.contentType = contentType;
    }
    
    /**
     * Stores the binary content for the attachment into the instance. This
     * method allows saving a copy of the content of the attachment to be
     * consumed later. This operation replaces any pre-existing content saved
     * into the attachment. Therefore, a {@literal null} references will cause
     * the previous content to be deleted.
     * 
     * @param stream	a {@link InputStream} implementation providing access
     * 					to the binary content of the attachment.
     */
    public void store(InputStream stream) {
    	
    	this.stream = stream;
    }
    
    /**
     * Gets access to the attachment's binary data. This method should be used
     * in combination with {@link Attachment#store(InputStream stream)}.
     * 
     * @return	a {@link InputStream} implementation, or {@literal null} if no
     * 			attachment binary has been stored, by calling the method {@link 
     * 			Attachment#store(InputStream)}.
     */
    public InputStream getStream() {
    	
    	return this.stream;
    }

    /**
     * Clones the current attachment. The method does not clone the {@link String}
     * properties because the {@link String} class is immutable.
     * 
     * @return 	an {@link Attachment} instance whose properties have been cloned,
     * 			or copied if immutable, from the current instance.
     */
    public Attachment clone() {
    	
    	// we do not need to clone strings because they are immutable.
    	// to change their value we need to create another reference.
    	//
    	Attachment zombie = new Attachment(this.getName(),this.getContentLength(), this.getContentType());
    	
    	return zombie;
    	
    }
    
    /**
     * Tests <i>other</i> for equality. An {@link Attachment} instance is equal to another
     * instance if:
     * <ul>
     * <li>the other instance is not {@literal null} and an {@link Attachment} instance</li>
     * <li>if the two instances have the same name.</li>
     * </ul>
     * 
     * @param other	an {@link Object} reference. It should not be {@literal null} and of type
     * 				{@link Attachment}.
     * 
     * @return	{@literal true} if two instances are of type {@link Attachment} and both have
     * 			the same name (not {@literal null}. {@literal false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
    	
    	boolean areTheSame = (this == other);
    	
    	if ((areTheSame == false) && (other != null)) {
	    		
	    	if (other instanceof Attachment) {
	    		
	    		Attachment a = (Attachment) other;
	    		String name = this.getName();
	    		String otherName = a.getName();
	    		
	    		areTheSame = (name != null) && (otherName != null) && (name.equals(otherName));
	    	}
			
		}
    	
    	return areTheSame;
    }
    /**
     * Gets the hash code of the instance. The hash code of an {@link Attachment} is computed
     * out of the attachment name if defined, otherwise is defaulted to 0.
     * 
     * @return	a {@literal int} value, which is either {@link String#hashCode()} applied to
     * 			{@link Attachment#getName()} if not {@literal null} or 0 otherwise.
     */
    @Override
    public int hashCode() {
 
    	return this.getName().hashCode();
    }
    
}