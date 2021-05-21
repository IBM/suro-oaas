package com.ibm.au.optim.suro.model.entities.couch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Run;

import org.ektorp.support.TypeDiscriminator;

/**
 * Class <b>CouchDbRun</b>. This class extends {@link CouchDbDocument} and provides
 * the bindings that are necessary to serialize and deserialize {@link Run} instances
 * to and from <i>CouchDb</i>.
 * 
 * 
 * @author Christian Vecchiola, Matt Davis
 *
 */
@JsonIgnoreProperties("jobData")
public class CouchDbRun extends CouchDbDocument<Run>  {

	/**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = 8093608765899001927L;


    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
	@TypeDiscriminator
	@JsonProperty("cdbRun")
	private String cdbRun = "Run";


}