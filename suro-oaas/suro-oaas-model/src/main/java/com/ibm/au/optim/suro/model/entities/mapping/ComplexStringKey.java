package com.ibm.au.optim.suro.model.entities.mapping;

import java.util.Arrays;

/**
 * Class <b>ComplexStringKey</b>. This class models a complex key, which is composed by a collection
 * of {@link String} instances. This type is essentially a wrapper class around an array of strings
 * so that they can conveniently used as a key within a {@link Map} implementation.
 *
 * @author Peter Ilfrich
 */
public class ComplexStringKey {

    /**
     * The content of the key. This is an array of {@link String} instances that together define the complex key.
     */
    private String[] content;

    /**
     * Initialises an instance of the {@link ComplexStringKey} type with the given content.
     * 
     * @param key	an array of {@link String} instances defining the components of the key.
     * 				It can be {@literal null}, if not {@literal null} the constructor makes
     * 				a copy of the array.
     */
    public ComplexStringKey(String[] key) {
    	
        if (key != null) {
        	
            this.content = Arrays.copyOf(key, key.length);
        }
    }

    /**
     * Gets the components of the complex key. It can be {@literal null}.
     * 
     * @return	a {@link String} array containing the components of the key, or {@literal null}.
     */
    public String[] getContent() {
        
    	return this.content;
    }

    /**
     * Sets the components of the complex key. It can be {@literal null}.
     * 
     * @param content	an array of {@link String} instances defining the components of the key.
     * 					It can be {@literal null}, if not {@literal null} the method makes a copy
     * 					of the array.
     */
    public void setContent(String[] content) {
    	
        if (content != null) {
            
        	this.content = Arrays.copyOf(content, content.length);
        
        } else {
          
        	this.content = null;
        }
    }

    /**
     * This method creates a clone of a {@link ComplexStringKey} instance. The
     * method creates a deep copy of the original instance except for the {@link 
     * String} instances contained in the {@link ComplexStringKey#getContent()}
     * array, because they're immutable.
     * 
     * @return 	a {@link ComplexStringKey} instance whose key has been cloned.
     */
    public ComplexStringKey clone() {
    	
    	return new ComplexStringKey(this.getContent());
    }

    /**
     * Performs the equality test for {@link ComplexStringKey} instances. The test
     * is performed by confronting the content of the array of keys defining the
     * instance.
     * 
     * @return  {@literal true} if the instance is compared with itself, or to
     * 			another instance of {@link ComplexStringKey} whose array of keys
     * 			is identical. {@literal false} in all the other cases.
     */
    @Override
    public boolean equals(Object obj) {
    	
    	boolean areTheSame = (this == obj);
    	
        if ((areTheSame == false) && (obj != null) && (obj instanceof ComplexStringKey)) {
        
        	ComplexStringKey key = (ComplexStringKey) obj;
            areTheSame = Arrays.deepEquals(this.content, key.content);
        
        }

        return areTheSame;
    }

    /**
     * Gets the hash code for the current instance. The hash code is generated
     * by concatenating the components of the key with a comma and then calling
     * {@link String#hashCode()} on the resulting string.
     * 
     * @return 	a {@literal int} representing the computed hash for the instance.
     * 			If the array of components is {@literal null} the value returned
     * 			is zero.
     */
    @Override
    public int hashCode() {
    	
        if (this.content == null) {
            
        	return 0;
        }
        
        // concatenate key components
        StringBuilder concat = new StringBuilder();
        
        for (String key : this.content) {
            concat.append(key).append(",");
        }
        
        return concat.toString().hashCode();
    }
    
    
}
