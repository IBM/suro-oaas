package com.ibm.au.optim.suro.model.entities.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Class <b>ValueMapping</b>. This class is a container for a collection of properties
 * that are of interest within a data source. {@link ValueMapping} instances are used
 * within the context of an output mapping to define what are the attributes that should
 * be used to compose a specific view on the data.
 *
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class ValueMapping implements Cloneable {

    /**
     * A {@link String} array representing the list of attributes in the data source
     * that contain the information that needs to be extracted. For instance if the
     * data source is a JSON object, this list will map all or of a subset of the
     * properties of that object.
     */
    @JsonProperty("keys")
    private String[] keys;


    /**
     * Initialises an instance of {@link ValueMapping}. The current implementation of
     * this constructor does nothing.
     */
    public ValueMapping() {
    	
    }

    /**
     * Initialises an instnce of {@link ValueMapping} with th given key. This is a
     * convenient method to construct a {@lin ValueMapping} instance that declares
     * a single attribute to be extracted, without creating an array and passing it
     * to the constructor.
     * 
     * 
     * @param key	a {@link String} instance containing the name of the attribute to
     * 				extract. It can be {@literal null}.
     */
    public ValueMapping(String key) {
        
    	this.setKeys(key == null ? null : new String[] { key });
    }

    /**
     * Initialises an instance of {@link ValueMapping} with the given set of attributes.
     * 
     * @param keys	a {@link String} array representing the list of attributes in the 
     * 				data source that contain the information that needs to be extracted.
     * 				It can be {@literal null}, but if not the constructor does a copy of
     * 				the array to avoid that there are external references.
     */
    public ValueMapping(String[] keys) {
        
    	this.setKeys(keys);
    }

    /**
     * Gets the list of attributes that define the value mapping. These corresponds to 
     * the name of the properties in the data source that are of interest to perform a
     * mapping. By default it is {@literal null}.
     * 
     * @return	a {@link String} array containing the name of the attributes of interest
     * 			or {@literal null}.
     */
    public String[] getKeys() {
    	
        return this.keys;
    }

    /**
     * Sets the list of attributes that define the value mapping. These corresponds to 
     * the name of the properties in the data source that are of interest to perform a
     * mapping. By default it is {@literal null}.
     * 
     * @param key	a {@link String} array representing the list of attributes in the 
     * 				data source that contain the information that needs to be extracted.
     * 				It can be {@literal null}, but if not the method does a copy of the
     * 				array to avoid that there are external references.
     */
    public void setKeys(String[] keys) {
    	
        if (keys != null) {
            this.keys = Arrays.copyOf(keys, keys.length);
        }
        else {
            this.keys = null;
        }
    }
    /**
     * This method creates a clone of a {@link ValueMapping} instance. The method creates
     * another instance and configures it with the collection of attributes that the original
     * instance is configure with. The logic implemented in the constructor ensures that the
     * array is copied rather than assigned.
     * 
     * @return 	a {@link ValueMapping} instance whose key has been cloned by the current instance.
     * 			This instance is supposed to pass the equality test when compared with the original.
     */
    public ValueMapping clone() {
    	
    	return new ValueMapping(this.getKeys());
    }
    
    /**
     * This method implements the equality test for {@link ValueMapping} instances.
     * Two {@link ValueMapping} instances are the same if and only if the property
     * {@link ValueMapping#getKeys()} is the same (both {@literal null} or both having
     * the same content in the same order).
     * 
     * @return {@literal true} if the test is successful, {@literal false} otherwise.
     */
    public boolean equals(Object other) {
    	
    	boolean areTheSame = (this == other);
    	if ((areTheSame == false) && (other != null) && (other instanceof ValueMapping)) {
    		
    		
    		String[] a1 = this.getKeys();
    		String[] a2 = ((ValueMapping) other).getKeys();
    		
    		areTheSame = Arrays.deepEquals(a1, a2);
    		
    	}
    	return areTheSame;
    }
}
