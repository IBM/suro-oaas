package com.ibm.au.optim.suro.model.admin.preference.impl.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbDocument;

import org.ektorp.support.TypeDiscriminator;

/**
 * Class <b>CouchDbSystemPreference</b>. This class is simply the specialised 
 * implementation of {@link CouchDbDocument} for {@link SystemPreference}.
 *
 * @author Peter Ilfrich
 */
public class CouchDbSystemPreference extends CouchDbDocument<SystemPreference> {


    /**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
    private static final long serialVersionUID = 6L;


    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
    @TypeDiscriminator
    @JsonProperty("cdbPreference")
    private String cdbPreference = "SystemPreference";


}
