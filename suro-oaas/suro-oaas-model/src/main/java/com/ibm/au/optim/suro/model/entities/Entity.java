/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Class <b>Entity</b>. This class models the characteristics of an entity.
 * An entity is represented by a unique identifier and a revision, which 
 * provides information about the changes that have been made to entity
 * over time. 
 * 
 * @author Christian Vecchiola
 *
 */
public class Entity implements Cloneable {



	/**
	 * Unique identifier of the entity. This is a {@link String}
	 * that is meant to univocally identify the entity.
	 */
    @JsonIgnore
    private String id;

    /**
     * Unique revision identifier.
     */
    @JsonIgnore
    private String revision;

	/**
	 * A {@link List} implementation that contains the mapping between
	 * the attachments of the entity and its corresponding metadata.
	 */
    @JsonIgnore
    private List<Attachment> attachments;


    /**
     * Getter for the id of the object
     * @return - the objects unique id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter for the id of the object
     * @param id - the new id of the object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the revision of the object
     * @return - the revision of the object
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Setter for the revision of the object
     * @param revision - the new revision of the object
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }
    
    
	/**
     * Gets the attachment of the entity.
     * 
     * @return	a {@link List} implementation containing the name of the
     * 			attachments as key and the corresponding metadata as
     * 			values.
     */
    public List<Attachment> getAttachments() {
    	
        return this.attachments;
    }


    /**
     * Sets the attachments for the entity.
     * 
     * @param attachments	a {@link List} implementation containing the name of the
     * 						attachments as key and the corresponding metadata as
     * 						values.
     */
    public void setAttachments(List<Attachment> attachments) {
    	
        this.attachments = attachments;
    }
    
    /**
     * Gets the attachment identified by <i>fileName</i>.
     * 
     * @param fileName	a {@link String} representing the name of the attachment. It
     * 					cannot be {@literal null} or an empty string.
     * 
     * @return	an {@link Attachment} instance whose name is <i>fileName</i> or
     * 			{@literal null} if not found.
     * 
     * @throws IllegalArgumentException	if <i>fileName</i> is {@literal null} or an empty string.
     */
    public Attachment getAttachment(String fileName) {
    	
    	if ((fileName == null) || (fileName.isEmpty() == true)) {
    		
    		throw new IllegalArgumentException("Parameter 'fileName' cannot be null or an empty string.");
    	}
    	
    	
    	List<Attachment> attachments = this.getAttachments();
    	Attachment found = null;
    	
    	if ((attachments != null) && (attachments.size() > 0)) {
    		
    		for(Attachment attachment : attachments) {
    			
    			String candidate = attachment.getName();
    			
    			if (candidate.equals(fileName) == true) {
    				
    				found = attachment;
    				break;
    			}
    		}
    	}
    	
    	return found;
    }
    
    /**
     * Equality test for an entity. An entity is equal to another entity
     * if they're of the same class and if their unique identifier is the
     * same.
     * 
     * @param obj	this is an {@link Object} reference that is supposed
     * 				to be of the same type of this actual type and not
     * 				{@literal null}.	
     * 
     * @return 	{@literal true} if <i>obj</i> is not {@literal null} and of
     * 			the same type of this instance and with the same identifier.
     * 			If the current (or the compared) instance have a {@literal 
     * 			null} value for the <i>id</i> property, the method returns
     * 			{@literal false}.
     */
    @Override
    public boolean equals(Object obj) {
    	
        
        boolean areTheSame = (obj == this);
        
        if ((areTheSame == false) && (obj != null)) {
        
	        // [CV] NOTE: do we really need to do this class comparision?
	        // 			  The rationale of having a unique identifier is for
	        //			  using it as a sole element
	        //
	        if (this.getClass().getName().equals(obj.getClass().getName())) {
	        	
	            Entity comp = (Entity) obj;
	            
	            String id = this.getId();
	            if (id != null) {
	            	
	            	areTheSame = id.equals(comp.getId());
	            }
	            
	        }
        
        }
        
        return areTheSame;
    }
    


    /**
     * Gets the hash code of the entity. The hash code of the entity is
     * a number that is computed out of its identifier property if not
     * {@literal null}.
     * 
     * @return 	1 if the identifier property is set to {@literal null},
     * 			otherwise the value returned by {@link String#hashCode()}
     * 			when applied to {@link Entity#getId()}.
     */
    @Override
    public int hashCode() {
    	
        if (this.getId() == null) {
        	
            return 1;
            
        } else {
        	
            return this.getId().hashCode();
        }
    }
    /**
     * Clones the current entity. The method does not clone the {@link String}
     * properties because the {@link String} class is immutable.
     * 
     * @return 	an {@link Entity} instance whose properties have been cloned,
     * 			or copied if immutable, from the current instance.
     */
    public Entity clone() {
    	
    	Entity zombie = this.newInstance();
    	zombie.setId(this.getId());
    	zombie.setRevision(this.getRevision());
    	
    	List<Attachment> newAtts = null;
    	List<Attachment> atts = this.getAttachments();
    	if (atts != null) {
    		
    		newAtts = new ArrayList<Attachment>();
    		for(Attachment a : atts) {
    			
    			newAtts.add(a.clone());
    		}
    	}
    	
    	zombie.setAttachments(newAtts);
    	
    	return zombie;
    }
    
    /**
     * This is an internal utility method that is used to create an instance
     * of the type that needs to be clone. This method has been defined to
     * enable the re-use of the super class versions of the clone() method
     * in the implementation of the very same method in inherited classes.
     * 
     * @return	a {@link Entity} instance.
     */
    protected Entity newInstance() {
    	
    	return new Entity();
    }
    

}
