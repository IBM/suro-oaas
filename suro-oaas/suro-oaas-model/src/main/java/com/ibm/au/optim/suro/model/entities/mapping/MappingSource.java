package com.ibm.au.optim.suro.model.entities.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>MappingSource</b>. This class defines the source of a mapping, as part of
 * a {@link OutputMapping} transformation. A source specifies a <i>solutionKey</i>,
 * which  points to a portion of the output generated by the optimisation back-end.
 * Additionally the source defines a specification that defines how to pick-up the
 * values from the source.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class MappingSource {

    /**
     * A {@link String} containing the pointer to the output generated by the
     * optimisation backend that is relevant to the transformation. This cannot
     * be {@literal null}.
     */
    @JsonProperty("solutionKey")
    private String solutionKey;

    /**
     * A {@link MappingSpecification} instance that defines the collection of columns
     * that will be present in the composed transformation of the output. These are
     * pointers to the columns in the rendered CSV file.
     */
    @JsonProperty("column")
    private MappingSpecification column;

    /**
     * A {@link ValueMapping} instance that declares the attributes of the source
     * that are of interest to the transformation. This instance will identify those
     * attributes that will be used to compose the information of the rendered
     * mapping.
     */
    @JsonProperty("value")
    private ValueMapping value;

    /**
     * A {@link MappingSpecification} instance that defines the collection of rows
     * in terms of keys and their labels within the resulting table view of the 
     * data.
     */
    @JsonProperty("row")
    private MappingSpecification row;



    /**
     * Initialises an instance of the {@link MappingSource} type with the given 
     * solution key.
     * 
     * @param solutionKey	{@link String} containing the pointer to the output 
     * 						generated by the optimisation backend that is relevant 
     * 						to the transformation. This cannot be {@literal null}.
     * 
     * @throws IllegalArgumentException	if <i>solutionKey</i> is {@literal null}.
     */
    @JsonCreator
    public MappingSource(@JsonProperty("solutionKey") String solutionKey) {
    	
    	this.setSolutionKey(solutionKey);
    }

    /**
     * Gets the value of the solution key. This is a pointer to the output generated by 
     * the optimisation backend that is relevant to the transformation. It cannot be 
     * {@literal null}.
     * 
     * @return a {@link String} containing the pointer.
     */
    public String getSolutionKey() {
    	
        return this.solutionKey;
    }


    /**
     * Sets the value of the solution key. This is a pointer to the output generated by 
     * the optimisation backend that is relevant to the transformation. 
     * 
     * @param solutionKey	a {@link String} containing the pointer. It cannot be {@literal null}.
     * 
     * @throws IllegalArgumentException	if <i>solutionKey</i> is {@literal null}.
     */
    public void setSolutionKey(String solutionKey) {

    	if (solutionKey == null) {
    		throw new IllegalArgumentException("Parameter 'solutionKey'cannot be null.");
    	}
    	
        this.solutionKey = solutionKey;
    }

    /**
     * Gets the collection of attributes in the source that are used for
     * the transformation.
     * 
     * @return	a {@link ValueMapping} instance containing the list of attributes that
     * 			identify the attributes in the data source that are of interest for the
     * 			data extraction. It can be {@literal null}.
     */
    public ValueMapping getValue() {
    	
        return this.value;
    }
    
    /**
     * Sets the collection of attributes in the source that are used for
     * the transformation.
     * 
     * @param value	a {@link ValueMapping} instance containing the list of 
     * 				attributes that identify the attributes in the data source 
     * 				that are of interest for the data extraction. It can be 
     * 				{@literal null}.
     */
    public void setValue(ValueMapping value) {
    	
        this.value = value;
    }

    /**
     * Gets the specification information for rendering the row of the table view
     * generated by the mapping. It can be {@literal null}.
     * 
     * @return	a {@link MappingSpecification} instance that contains the information
     * 			about the mapping for the rows, for the data that originates from this
     * 			source.
     */
    public MappingSpecification getRow() {
    	
        return this.row;
    }

    /**
     * Sets the specification information for rendering the row of the table view
     * generated by the mapping.
     * 
     * @param row	a {@link MappingSpecification} instance that contains the information
     * 				about the mapping for the rows, for the data that originates from this
     * 				source. It can be {@literal null}.
     */
    public void setRow(MappingSpecification row) {
    	
        this.row = row;
    }


    /**
     * Gets the specification information for rendering the column of the table view
     * generated by the mapping. It can be {@literal null}.
     * 
     * @return	a {@link MappingSpecification} instance that contains the information
     * 			about the mapping for the columns, for the data that originates from this
     * 			source.
     */
    public MappingSpecification getColumn() {
    	
        return this.column;
    }


    /**
     * Sets the specification information for rendering the row of the table view
     * generated by the mapping.
     * 
     * @param column	a {@link MappingSpecification} instance that contains the 
     * 					information about the mapping for the rows, for the data 
     * 					that originates from this source. It can be {@literal null}.
     */
    public void setColumn(MappingSpecification column) {
    	
        this.column = column;
    }


    /**
     * This is a convenience method that directly returns the collection of labels that
     * are used in the rendered table view for the rows for those rows that are associated
     * to data fields extracted from this source.
     * 
     * @return 	a {@link String} array that contains the list of labels as returned by
     * 			{@link MappingSpecification#getLabels()} when applied to the reference
     * 			returned by {@link MappingSource#getRwo()} or {@literal null} if that 
     * 			is {@literal null}.
     */
    @JsonIgnore
    public String[] getRowLabels() {
    	
        if (this.row == null) {
            return null;
        } else {
            return this.row.getLabels();
        }
    }

    /**
     * This is a convenience method that directly returns the collection of keys that
     * are used in the rendered table view for the rows for those rows that are associated
     * to data fields extracted from this source.
     * 
     * @return 	a {@link String} array that contains the list of labels as returned by
     * 			{@link MappingSpecification#getEntryKeys()()} when applied to the reference
     * 			returned by {@link MappingSource#getRow()} or {@literal null} if that 
     * 			is {@literal null}.
     */
    @JsonIgnore
    public String[] getRowEntryKeys() {
    	
        if (this.row == null) {
            return null;
        } else {
            return this.row.getEntryKeys();
        }
    }

    /**
     * This is a convenience method that directly returns the collection of labels that
     * are used in the rendered table view for the columns for those columns that are 
     * associated to data fields extracted from this source.
     * 
     * @return 	a {@link String} array that contains the list of labels as returned by
     * 			{@link MappingSpecification#getLabels()} when applied to the reference
     * 			returned by {@link MappingSource#getColumn()} or {@literal null} if that 
     * 			is {@literal null}.
     */
    @JsonIgnore
    public String[] getColumnLabels() {
    	
        if (this.column == null) {
            return null;
        } else {
            return this.column.getLabels();
        }
    }


    /**
     * This is a convenience method that directly returns the collection of keys that
     * are used in the rendered table view for the columns for those columns that are 
     * associated to data fields extracted from this source.
     * 
     * @return 	a {@link String} array that contains the list of labels as returned by
     * 			{@link MappingSpecification#getEntryKeys()()} when applied to the reference
     * 			returned by {@link MappingSource#getRow()} or {@literal null} if that 
     * 			is {@literal null}.
     */
    @JsonIgnore
    public String[] getColumnEntryKeys() {
    	
        if (this.column == null) {
            return null;
        } else {
            return this.column.getEntryKeys();
        }
    }

    /**
     * This is a convenience method that directly returns the collection of attributes of 
     * the data sources that are of interest for the mapping.
     * 
     * @return 	a {@link String} array that contains the list of attributes defined in the
     * 			{@link ValueMapping} instance returned by {@link MappingSource#getValue()}
     * 			or {@literal null} if that is {@literal null}.
     */
    @JsonIgnore
    public String[] getValueKeys() {
    	
        if (this.value == null) {
        
        	return null;
        
        } else {
        	
            return this.value.getKeys();
        }
    }
    /**
     * This method clones an instance of {@link ValueMapping}. The method generates a 
     * deep copy of the instance with the exclusion of immutable and value type fields.
     * The cloned instance is expected to pass the equality test when compared with the
     * original instance.
     * 
     * @return 	an instance of {@link ValueMapping} whose properties have been copied
     * 			or cloned from the current instance. 
     */
    public MappingSource clone() {
    	
    	// we set the new string, because it is immutable.
    	//
    	MappingSource source = new MappingSource(this.solutionKey);
    	
    	MappingSpecification spec = this.getRow();
    	source.setRow(spec == null ? null : spec.clone());
    	
    	spec = this.getColumn();
    	source.setColumn(spec == null ? null : spec.clone());
    	
    	source.setSolutionKey(this.getSolutionKey());
    	
    	ValueMapping value = this.getValue();
    	source.setValue(value == null ? null : value.clone());
    	
    	return source;
    }
    
    /**
     * This method compares two instances of {@link MappingSource} to check whether they
     * are equal to each other. Two instances of {@link MappingSource} are equal to each
     * other if and only if all the fields are equal to each other or have corresponding
     * {@literal null} values.
     * 
     * @return {@literal true} if the two instances are equal, {@literal false} otherwise.
     */
    public boolean equals(Object other) {
    	
    	boolean areTheSame = (this == other);
    	
    	if ((areTheSame == false) && (other != null) && (other instanceof MappingSource)) {
    		
    		MappingSource source = (MappingSource) other;
    		
    		// solution key
    		//
    		String sk1 = this.getSolutionKey();
    		String sk2 = source.getSolutionKey();
    		
    		areTheSame = (sk1 == sk2) || (sk1 == null && sk2 == null) || (sk1 != null && sk2 != null && (sk1.equals(sk2)));
    		if (areTheSame == true) {
    			
    			// mapping value
    			ValueMapping mv1 = this.getValue();
    			ValueMapping mv2 = source.getValue();
    			areTheSame = (mv1 == mv2) || (mv1 == null && mv2 == null) || (mv1 != null && mv2 != null && (mv1.equals(mv2)));
    			
    			
    			
    			if (areTheSame == true) {
    				
    				// row
    				MappingSpecification ms1 = this.getRow();
        			MappingSpecification ms2 = source.getRow();
        			
        			areTheSame = (ms1 == ms2) || (ms1 == null && ms2 == null) || (ms1 != null && ms2 != null && (ms1.equals(ms2)));
    			
        			if (areTheSame == true) {
        			
        				// column
        				
        				ms1 = this.getColumn();
            			ms2 = source.getColumn();
            			
            			areTheSame = (ms1 == ms2) || (ms1 == null && ms2 == null) || (ms1 != null && ms2 != null && (ms1.equals(ms2)));
        			
        				
        			}
    			}
    		}
    	}
    	
    	return areTheSame;
    }
}