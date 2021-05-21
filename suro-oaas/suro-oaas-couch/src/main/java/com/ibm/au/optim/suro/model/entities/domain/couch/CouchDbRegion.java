package com.ibm.au.optim.suro.model.entities.domain.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbDocument;
import com.ibm.au.optim.suro.model.entities.domain.Region;

import org.ektorp.support.TypeDiscriminator;

/*
Example Region:
---------------

region: {
	cdbRegion: 'Region',
	name: 'Australia/Victoria',
	intervalType: 'Quarterly',
	firstIntervalStart: 1420070400,
	urgencyCategories: [
		{
			label: 'Cat 1',
			maxWaitlistStay: 7,
			minPointsRequired: 1,
			possiblePoints: 1,
			kpiTargets: [
				{
					interval: 1,
					numberOfPoints: 1,
					requiredOnTimePerformance: 100.0
				}
			]
		}
	]
}

 */

/**
 * Class <b>CouchDbRegion</b>. This class is a specialisation of {@link CouchDbDocument}
 * for entities of type {@link Region}.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class CouchDbRegion extends CouchDbDocument<Region> {

    /**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = 7790677121810381719L;


    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
	@TypeDiscriminator
    @JsonProperty("cdbRegion")
    private final String cdbRegion = "Region";

	/**
	 * Default constructor. This constructor initialises the {@link CouchDbRegion} with a 
	 * {@link Region} instance that has an empty name.
	 */
	public CouchDbRegion() {
		this("");
	}

	/**
	 * Creates a new {@link CouchDbRegion} instance containing a bean with the specified name 
	 * for the region.
	 * 
	 * @param name 	a {@link String} representing the name of the region.
	 */
    public CouchDbRegion(String name) {
        setContent(new Region(name));
    }

}
