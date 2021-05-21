package com.ibm.au.optim.suro.model.entities.domain.ingestion.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbDocument;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;

import org.ektorp.support.TypeDiscriminator;

/**
 * Class <b>CouchDbWaitingPatientList</b>. This class is a specialisation of {@link CouchDbDocument}
 * for entities of type {@link WaitingPatientList}.
 *
 * @author Brendan Haesler
 */
public class CouchDbWaitingPatientList extends CouchDbDocument<WaitingPatientList> {


    /**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = -3653317256476214238L;
	
    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
	@TypeDiscriminator
	@JsonProperty("cdbWaitingPatientList")
	private final String cdbWaitingPatientList = "WaitingPatientList";
}
