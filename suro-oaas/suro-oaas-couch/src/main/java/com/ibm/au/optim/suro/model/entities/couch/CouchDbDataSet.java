package com.ibm.au.optim.suro.model.entities.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.DataSet;

import org.ektorp.support.TypeDiscriminator;



/**
 * Class <b>CouchDbDataSet</b>. This class is a specialisation of {@link CouchDbDocument}
 * for entities of type {@link DataSet}.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class CouchDbDataSet extends CouchDbDocument<DataSet> {


	/**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = 8747893913416172269L;
	
    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
    @TypeDiscriminator
    @JsonProperty("cdbData")
    private final String cdbData = "DataSet";

    /**
     * Default constructor, which will initialise a new data set with no label and no model ID
     */
    public CouchDbDataSet() {
    	
    	// [CV] NOTE: not sure we need this.
    	//
        this.setContent(new DataSet());
    }



}
