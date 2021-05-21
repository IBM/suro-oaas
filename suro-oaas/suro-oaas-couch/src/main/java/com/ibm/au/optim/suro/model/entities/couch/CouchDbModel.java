package com.ibm.au.optim.suro.model.entities.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Model;

import org.ektorp.support.TypeDiscriminator;

/**
 * Class <b>CouchDbModel</b>. This class is a specialisation of {@link CouchDbDocument}
 * for entities of type {@link Model}.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class CouchDbModel extends CouchDbDocument<Model> {



	/**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = 5378348017059162499L;
	
	
    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
    @TypeDiscriminator
    @JsonProperty("cdbModel")
    private final String cdbModel = "Model";


}
