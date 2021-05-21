package com.ibm.au.optim.suro.model.entities.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Class <b>MappingSpecification</b>. This class defines a specification for mapping a portion of a
 * data source into a a row or a column set of a tabular view. {@link MappingSpecification} instances
 * are used within a {@link MappingSource} instance. A mapping specification is defined by a pair of
 * <i>labels</i> and <i>entrieKeys</i>. When one of the two is missing the other is used as replacement.
 * The labels are used as headers (for the columns/rows) while the entryKeys represents the corresponding
 * field names that need to be mapped under the headers.
 *
 * @author Peter Ilfrich
 */
public class MappingSpecification implements Cloneable {

    /**
     * An array of {@link String} representing the names of the fields in the
     * data source that are used to fill the corresponding column/rown in the
     * resulting tabular view. The values in this array are paired with the
     * corresponding values in the <i>labels</i> array. If this array is set
     * to {@literal null}, the values from the <i>labels</i> array will be used.
     */
    @JsonProperty("entryKeys")
    private String[] entryKeys;

    /**
     * An array of {@link String} representing the names of the headers of the
     * column/rown in the resulting tabular view. The values in this array are 
     * paired with the corresponding values in the <i>entryKeys</i> array. If 
     * this array is set to {@literal null}, the values from the <i>entryKeys</i> 
     * array will be used.
     */
    @JsonProperty("labels")
    private String[] labels;


    /**
     * Initialises an instance of {@link MappingSpecification} for a single pair <i>label,entryKey</i>.
     * This is a convenience constructor that simplifies the user tasks in one of the most common cases
     * whereby there is no need to create an array for a single element.
     * 
     * @param label		a {@link String} representing the header of the row/column in the resulting 
     * 					tabular view generated by the mapping according to this specification. It can
     * 					be {@literal null}, and when {@literal null} the value of <i>entryKey</i> will
     * 					be used.
     * 
     * @param entryKey 	a {@link String} representing the value mapped to the cell of the row/column in 
     * 					the resulting tabular view generated by the mapping according to this specification. 
     * 					It can be {@literal null}, and when {@literal null} the value of <i>label</i> will
     * 					be used.
     */
    public MappingSpecification(String label, String entryKey) {
    	
    
        this.setLabels(label == null? null : new String[] { label });
        this.setEntryKeys(entryKey == null ? null : new String[] { entryKey });
        
    }

    /**
     * Initialises an instance of {@link MappingSpecification} with the given arrays of labels and entry keys.
     * The constructor duplicates the arrays passed as arguments to prevent that there are references to the
     * internal field of the instance from outside the class.
     * 
     * @param labels		an array {@link String} instances representing the headers of the rows/columns in the 
     * 						resulting tabular view generated by the mapping according to this specification. It 
     * 						can be {@literal null}, and when {@literal null} the corresponding values of the 
     * 						array <i>entryKeys</i> will be used.
     * 
     * @param kentryKeys	an array {@link String} instances representing the values mapped to the cells of the 
     * 						rows/columns in the resulting tabular view generated by the mapping according to this 
     * 						specification. It can be {@literal null}, and when {@literal null} the corresponding
     * 						values of <i>labels</i> array will be used.
     */
    public MappingSpecification(String[] labels, String[] kentryKeys) {
       
    	this.setLabels(labels);
    	this.setEntryKeys(kentryKeys);
    }

    /**
     * Initiliases a {@link MappingSpecification} instance. This implementation
     * does not perform any operation. Therefore the properties of the created
     * instance are set to the default value defined by the underlying types.
     */
    public MappingSpecification() {

    }

    /**
     * Gets the list of values that are used in the corresponding cells of the
     * columns/rows defined by this mapping specification. By default it is 
     * {@literal null}.
     * 
     * @return 	a {@link String} array containing the name of the fields whose
     * 			values need to be mapped to the cells of the resulting tabular
     * 			view, or {@literal null}.
     */
    public String[] getEntryKeys() {
    	
        return this.entryKeys;
    }

    /**
     * Gets the list of values that are used as headers of the columns/rows defined 
     * by this mapping specification. By default it is {@literal null}.
     * 
     * @return 	a {@link String} array containing the name of the labels for the 
     * 			rows/columns, or {@literal null}.
     */
    public String[] getLabels() {
    	
        return this.labels;
    }

    /**
     * Sets the list of values that are used in the corresponding cells of the
     * columns/rows defined by this mapping specification. By default it is 
     * {@literal null}. If not {@literal null} the content of the array is
     * copied into another array, to prevent having references to an internal
     * field of the instance.
     * 
     * @param entryKeys 	a {@link String} array containing the name of the 
     * 						fields whose values need to be mapped to the cells 
     * 						of the resulting tabular view, or {@literal null}.
     */
    public void setEntryKeys(String[] entryKeys) {
    	
        if (entryKeys != null) {
        	
            this.entryKeys = Arrays.copyOf(entryKeys, entryKeys.length);
        }
        else {
            this.entryKeys = null;
        }
    }

    /**
     * Sets the list of values that are used in the corresponding cells of the
     * columns/rows defined by this mapping specification. By default it is 
     * {@literal null}. If not {@literal null} the content of the array is
     * copied into another array, to prevent having references to an internal
     * field of the instance.
     * 
     * @param labels 	a {@link String} array containing the name of the fields 
     * 					whose values need to be mapped to the cells of the resulting 
     * 					tabular view, or {@literal null}.
     */
    public void setLabels(String[] labels) {
    	
        if (labels == null) {
        	
            this.labels = null;
        }
        else {
            this.labels = Arrays.copyOf(labels, labels.length);
        }
    }


    /**
     * This is a convenience method that can be used to check whether the mapping
     * specification has any information defined with regards to the column or 
     * row headers to use.
     * 
     * @return	{@literal true} if the array of labels is not {@literal null} and
     * 			not empty, {@literal false} otherwise.
     */
    public boolean hasLabels() {
    	
        return this.labels != null && this.labels.length > 0;
    }

    /**
     * This is a convenience method that can be used to check whether the mapping
     * specification has any information defined with regards attributes in the
     * data source to use to fill to the column or row cells.
     * 
     * @return	{@literal true} if the array of entry keys is not {@literal null} 
     * 			and not empty, {@literal false} otherwise.
     */
    public boolean hasEntryKeys() {
    	
        return this.entryKeys != null && this.entryKeys.length > 0;
    }

    /**
     * This is convenience method that can be used to check where the number of
     * labels defined are exactly as <i>number</i>.
     * 
     * @param number	a {@literal int} value defining the expected number of
     * 					labels.
     * 
     * @return 	{@literal true} if the labels array is not {@literal null} and
     * 			it is of the same size of the given number.
     */
    public boolean hasNumberOfLabels(int number) {
    	
        return (this.labels == null && number == 0) || (this.labels != null && this.labels.length == number);
    }


    /**
     * This is convenience method that can be used to check where the number of
     * entry key defined are exactly as <i>number</i>.
     * 
     * @param number	a {@literal int} value defining the expected number of
     * 					entry keys.
     * 
     * @return 	{@literal true} if the entry key array is not {@literal null} and
     * 			it is of the same size of the given number.
     */
    public boolean hasNumberOfEntryKeys(int number) {
    	
        return (this.entryKeys == null && number == 00) || (this.entryKeys != null && this.entryKeys.length == number);
    }

    /**
     * This method creates a clone of a {@link MappingSpecification} instance. This
     * method will create a deep copy of the object. The cloned instance is expected
     * to pass the equality test when compared to the original instance.
     * 
     * @return 	a {@link MappingSpecification} instance whose key has been cloned
     * 			by the current instance.
     */
    public MappingSpecification clone() {
    	
    	return new MappingSpecification(this.getLabels(), this.getEntryKeys());
    }
    
    /**
     * This method checks whether the given instance <i>other</i> is equal to 
     * the current instance. Two {@link MappingSpecification} instancea are
     * the same if both {@link MappingSpecification#getLabel()} and {@link 
     * MappingSpecification#getEntryKey()} are the same.
     * 
     * @return {@literal true} in case of success, {@literal false} otherwise.
     */
    public boolean equals(Object other) {
 
    	boolean areTheSame = (this == other);
    	if ((areTheSame == false) && (other != null) && (other instanceof MappingSpecification)) {
    		
    		MappingSpecification ms = (MappingSpecification) other;
    		
    		// labels.
    		//
    		String[] a1 = this.getLabels();
    		String[] a2 = ms.getLabels(); 
    		
    		areTheSame = Arrays.deepEquals(a1, a2);
    		
    		if (areTheSame == true) {
    			
	    		// entry keys
	    		//
	    		a1 = this.getEntryKeys();
	    		a2 = ms.getEntryKeys();
	    		
	    		areTheSame = Arrays.deepEquals(a1, a2);
    		
    		}
    			
    	}
    	
    	return areTheSame;
    }
    
}
