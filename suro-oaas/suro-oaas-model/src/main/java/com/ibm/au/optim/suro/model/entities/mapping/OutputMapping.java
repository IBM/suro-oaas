package com.ibm.au.optim.suro.model.entities.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Class <b>OutputMapping</b>. This class defines a mapping to perform the automatic extraction of a subset of the
 * data that is generated by the optimisation backend  into a more useful views of that data. The mapping enables
 * the APIs automatically generating export files from the result files created by an optimisation run. An output
 * mapping is defined by the following properties:
 * <ul>
 * <li>a <i>file name</i>, which represents that name of the file where the data will be exported to.</li>
 * <li>an array of <i>sources</i> which define the sources of data that are used to compose the file.</li>
 * <i>a <i>mapping type</i>, which represents the type of transformation used to generate the export file.</li>
 * <i>a <i>transformer</i>, which is the name of the class that implements the custom logic for the transformation</li>
 * </ul>
 * The very last option is optional, and it is only used when the default mapping types are not sufficient to compose
 * the desired view of the data.
 *
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class OutputMapping implements Cloneable {

    /**
     * A {@link String} representing the name of the file that will contain the output
     * of the transformation and composition defined by this mapping when applied to 
     * the result files defined in the <i>sources</i>.
     */
    @JsonProperty("fileName")
    private String fileName;

    /**
     * A set of sources describing which parts of the solution are used to compose an output and how.
     */
    @JsonProperty("sources")
    private MappingSource[] sources;

    /**
     * The mapping type - see {@link MappingType}
     */
    @JsonProperty("mappingType")
    private MappingType mappingType;

    /**
     * Only set if mappingType = {@link MappingType#TRANSFORMER}
     */
    @JsonProperty("transformer")
    private String transformer;

    /**
     * Initialises an instance of {@link OutputMapping}. The constructor does
     * not perform any specific operation.
     */
    public OutputMapping() {

    }



    /**
     * Initialises an instance of {@link OutputMapping} with the given parameters.
     * 
     * @param fileName	a {@link String} representing the name of the file that is 
     * 					used to store the transformation and composition defined by
     * 					this instance of the mapping. It can be {@literal null}.
     * 
     * @param type		a {@link MappingType} value that defines the transformation
     * 					type to be applied to the data. It can be {@literal null}.
     */
    @JsonCreator
    public OutputMapping(@JsonProperty("fileName") String fileName, @JsonProperty("mappingType") MappingType type) {
    	
    	this.fileName = fileName;
    	this.mappingType  = type;
    }

    /**
     * Gets the fully qualified class name or (or an identifier for) the transformer implementation
     * that needs to be used to generate the output file defined by this mapping. By default this
     * field is {@literal null} and it only points to a transformer when the mapping requires the
     * implementation of custom logic. In this case the value returned by {@link OutputMapping#getMappingType()}
     * should be {@literal MappingType#TRANSFORMER}.
     * 
     * @return  a {@link String} representing the full class name or the identifier of the transformer
     * 			if specified, {@literal null} otherwise.
     */
    public String getTransformer() {
    	
        return this.transformer;
    }

    /**
     * Sets the fully qualified class name of (or an identifier for) the transformer implementation
     * that needs to be used to generate the output file defined by this mapping. This property should
     * be set if a custom logic is required by the mapping. In this case the value returned by {@link 
     * OutputMapping#getMappingType()} should be {@literal MappingType#TRANSFORMER}. It can be {@literal 
     * null}.
     * 
     * @param transformer	a {@link String} representing the identifier of the transformer. 
     */
    public void setTransformer(String transformer) {
    	
        this.transformer = transformer;
    }


    /**
     * Gets the name of the file (document) that will contain the output of the transformation defined
     * by this mapping. By default is set to {@literal null}.
     * 
     * @return	a {@link String} containing the name of the document, or {@literal null}.
     */
    public String getFileName() {
    	
        return this.fileName;
    }

    /**
     * Sets the name of the file (document) that will contain the output of the transformation defined
     * by this mapping.
     * 
     * @param fileName	a {@link String} containing the name of the document. It can be {@literal null}.
     */
    public void setFileName(String fileName) {
    	
        this.fileName = fileName;
    }

    /**
     * Gets the array of sources that are required by the mapping to generate the view defined by the
     * mapping. By default this is {@literal null}.
     * 
     * @return	an array of {@link MappingSource} instances, each of them defining a source of data for
     * 			the transformation defined by the mapping, or {@literal null}.
     */
    public MappingSource[] getSources() {
    	
        return this.sources;
    }


    /**
     * Sets the array of sources that are required by the mapping to generate the view defined by the
     * mapping. The method clones the given argument if not {@literal null}, this is to prevent that
     * references to the original array from outside the instance.
     * 
     * @return sources	an array of {@link MappingSource} instances, each of them defining a source of 
     * 					data for the transformation defined by the mapping. It can be {@literal null}.
     */
    public void setSources(MappingSource[] sources) {
    	
        if (sources == null) {
        	
            this.sources = null;
            
        } else {
        	
            this.sources = Arrays.copyOf(sources, sources.length);
        }
    }

    /**
     * Gets the specific mapping type applied by this mapping. The mapping type controls the nature
     * of the transformation that is applied to the set of sources to transform them and compose the
     * final result. By default it is {@literal null}.
     * 
     * @return	a {@link MappingType} value representing the specific type of mapping, or {@literal null}.
     */
    public MappingType getMappingType() {
    	
        return this.mappingType;
    }
    
    /**
     * Sets the specific mapping type applied by this mapping. The mapping type controls the nature
     * of the transformation that is applied to the set of sources to transform them and compose the
     * final result. By default it is {@literal null}.
     * 
     * @param mappingType	a {@link MappingType} value representing the specific type of mapping. It
     * 						can be {@literal null}.
     */
    public void setMappingType(MappingType mappingType) {
        this.mappingType = mappingType;
    }
    
    /**
     * Clones an instance of {@link OutputMapping}. The method performs a deep copy of the original 
     * instance, without allocating new memory for immutables (i.e. {@link String} instances, and
     * value types). The returned instance is expected to pass the equality test when compared it
     * with the original.
     * 
     * @return a {@link OutputMapping} instance, which is a deep copy of the original.
     */
    public OutputMapping clone() {
    	
    	OutputMapping zombie = new OutputMapping();
    	zombie.setFileName(this.getFileName());
    	zombie.setMappingType(this.getMappingType());
    	zombie.setTransformer(this.getTransformer());
    	
    	// this method will clone the array, therefore
    	// we don't need to do it by ourselves.
    	MappingSource[] target = null;
    	MappingSource[] source = this.getSources();
    	if (source != null) {
    		
    		target = new MappingSource[source.length];
    		for(int i=0; i<source.length; i++) {
    			
    			target[i] = source[i].clone();
    		}
    	}
    	
    	zombie.setSources(target);
    	
    	return zombie;
    	
    }
    
    /**
     * Equality test for a {@link OutputMapping} instance. Two instances of {@link OutputMapping}
     * are the same if and only if all the fields are equal (or do have the same content).
     * 
     * @return {@literal true} if the two instances are the same, {@link false} otherwise.
     */
    public boolean equals(Object other) {
    	
    	boolean areTheSame = (this == other);
    	
    	if ((areTheSame == false) && (other != null) && (other instanceof OutputMapping)) {
    		
    		OutputMapping otherMapping = (OutputMapping) other;
    		
    		String s1 = this.getFileName();
    		String s2 = otherMapping.getFileName();
    		
    		// checking the file names first
    		//
    		areTheSame = (s1 == s2) || (s1 == null && s2 == null) || (s1 != null && s2 != null && s1.equals(s2));
    		
    		if (areTheSame == true) {
    			
    			// checking the mapping type
    			//
    			MappingType mt1 = this.getMappingType();
    			MappingType mt2 = otherMapping.getMappingType();

        		areTheSame = (mt1 == mt2) || (mt1 == null && mt2 == null) || (mt1 != null && mt2 != null && s1.equals(mt2));
        		
        		if (areTheSame == true) {
        			
        			// transformer
        			//
        			
        			s1 = this.getTransformer();
        			s2 = otherMapping.getTransformer();

            		areTheSame = (s1 == s2) || (s1 == null && s2 == null) || (s1 != null && s2 != null && s1.equals(s2));
            		
            		
            		if (areTheSame == true) {
            			
            			// mapping source
            			//
            			
            			MappingSource[] ms1 = this.getSources();
            			MappingSource[] ms2 = otherMapping.getSources();
            			
            			areTheSame = Arrays.deepEquals(ms1, ms2);
            		}
        		}
    		}
    		
    		
    		
    	}
    	
    	return areTheSame;
    }
    
    
}
