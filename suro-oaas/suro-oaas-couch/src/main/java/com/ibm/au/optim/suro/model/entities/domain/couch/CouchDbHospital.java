package com.ibm.au.optim.suro.model.entities.domain.couch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbDocument;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;

import org.ektorp.support.TypeDiscriminator;



/*
Example Hospital:
-----------------

hospital: {
	cdbHospital: 'Hospital',
	regionId: 'njk34nj3k4n3j4jk344',
	name: 'Lygon Street Hospital for the Mentally Ill'
	wards: [
		{
			name: 'South',
			bedsCount: 24
		},
		{
			name: 'North',
			bedsCount: 10
		}
	],
	departments: [
		{
			name: 'Cardiology',
			maxSimultaneousSessions: 3
		},
		{
			name: 'Dental',
			maxSimultaneousSessions: 2
		}
	],
	specialistTypes: [
		{
			departmentName: 'Cardiology',
			label: 'Heart Surgeon'
		},
		{
			departmentName: 'Dental',
			label: 'General Dentist'
		}
	],
	urgencyCategories: [
		{
			label: 'Cat 1',
			maxWaitlistStay: 5,
			minPointsRequired: 1,
			possiblePoints: 1,
			kpiTargets: [
				{
					interval: 1,
					numberOfPoints: 1,
					requiredOnTimePerformance: 80.0
				}
			]
		}
	],
	icuBedCount: 8,
	theatreSessionsPerDay: 27,
	sessionDuration: 4,
	theatreCount: 3
}

 */

/** 
 * Class <b>CouchDbHospital</b>. This class is a specialisation of {@link CouchDbDocument}
 * for entities of type {@link Hospital}.
 *
 * @author Peter Ilfrich
 */
public class CouchDbHospital extends CouchDbDocument<Hospital> {


    /**
	 * This is a {@literal long} value that can be used to uniquely identify the implementation
	 * of a given class for types that are subject to binary serialisation. The information is
	 * used to verify that this value actually matches the one of the same type defined in the
	 * deserialisation domain before attempting the deserialisation.
	 */
	private static final long serialVersionUID = -2931274148023876379L;

    /**
     * This is the field that is used by the <i>CouchDb</i> driver (Ektorp) to
     * identify all the documents that can be mapped to instances of this type.
     */
	@TypeDiscriminator
    @JsonProperty("cdbHospital")
    private final String cdbHospital = "Hospital";

	/**
	 * Initialises an instance of {@link CouchDbHospital} with an {@link Hospital} instancw with
	 * the given name and region identifier.
	 *  
	 * @param regionId	the id of the region of the hospital (e.g. Australia/Victoria).
	 * @param name		the name of the hospital.
	 */
    public CouchDbHospital(String regionId, String name) {
        this.setContent(new Hospital(regionId, name));
    }

	/**
	 * Default constructor. Initialises an instance of {@link CouchDbHospital} with an empty
	 * {@link Hospital} instance.
	 */
    public CouchDbHospital() {
        this("", "");
    }

}