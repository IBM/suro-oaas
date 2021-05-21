package com.ibm.au.optim.suro.model.entities.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Entity;

/**
 * Class <b>CouchDbDocument</b>. This class is the base class to add {@link Entity} instances in <i>CouchDb</i>.
 * The class acts like a wrapper around the entity so that we can freely operate on the fields of the entity
 * without having restrictions imposed by <i>CouchDb</i>. The entity is stored as <i>content</i> field in the
 * document. This superclass offers the storage for the content and getters/setters to access the content.
 *
 * @author Peter Ilfrich
 */
public class CouchDbDocument<T extends Entity> extends org.ektorp.support.CouchDbDocument {


    /**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = 6463345731549702381L;
	
	/**
	 * This is the property that will eventually store the {@link Entity} instance wrapped by
	 * this document.
	 */
	@JsonProperty("content")
    private T content;

    /**
     * Gets the {@link Entity} instance that is wrapped by the current document. It can be 
     * {@literal null} (default value).
     * 
     * @return 	an instance of the specialised type <i>T,<i> inherithing from {@link Entity}
     * 			or {@literal null}.
     */
    public T getContent() {
    	
        return this.content;
    }

    /**
     * Sets the {@link Entity} instance that is wrapped by the current document. It can be 
     * {@literal null} (default value).
     * 
     * @param content 	an instance of the specialised type <i>T,<i> inherithing from {@link Entity}
     * 					or {@literal null}.
     */
    public void setContent(T content) {
        this.content = content;
    }

    /**
     * Generates an hash code for instances of the {@link CouchDbDocument} class.
     * The hash code is generated from unique identifier of the document that is
     * retrieved from {@link CouchDbDocument#getId()}. If the identifier is set
     * to {@literal null} then the value of 0 is returned.
     * 
     * @return 	the value of {@link String#hashCode()} when applied to {@link CouchDbDocument#getId()}
     * 			in case the identifier is not {@literal null}, 0 otherwise.
     */
	@Override
	public int hashCode() {
		
		String id = this.getId();
		
		return (id == null ? 0 : id.hashCode());
	}
	
	/**
	 * This method performs the equality check. The equality check for {@link CouchDbDocument}
	 * instances is performed by checking whether two document have the same identifier. This
	 * method can be compared to an instance of {@link CouchDbDocument} with the same identifier.
	 * 
	 * @return 	{@literal true} if an instance is compared to itself, or if it is compared to 
	 * 			either a {@link CouchDbDocument} instance that has the same identifier. In all 
	 * 			the other cases {@literal false} is returned.
	 */
	@Override
	public boolean equals(Object obj) {
		
		boolean areTheSame = (this == obj);
		
		if ((areTheSame == false) && (obj != null)) {

			String id = this.getId();
			
			if (obj instanceof CouchDbDocument) {
				
				CouchDbDocument<?> doc = (CouchDbDocument<?>) obj;
				areTheSame = (id != null) && (id.equals(doc.getId()));
			
			} 
			
		}
		
		return areTheSame;
	}
    
}
