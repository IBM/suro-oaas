/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.entities.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Template;

import org.ektorp.support.TypeDiscriminator;


/**
 * Class <b>CouchDbTemplate</b>. This class extends {@link CouchDbDocument} and provides
 * the bindings that are necessary to serialize and deserialize {@link Strategy} instances
 * to and from <i>CouchDb</i>.
 *
 */
public class CouchDbTemplate extends CouchDbDocument<Template> {

	/**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = 1477071516532735627L;


    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
	@TypeDiscriminator
	@JsonProperty("cdbTemplate")
	private final String cdbTemplate = "Template";


}
