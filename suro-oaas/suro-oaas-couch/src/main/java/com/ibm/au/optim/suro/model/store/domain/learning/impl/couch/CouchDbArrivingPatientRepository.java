package com.ibm.au.optim.suro.model.store.domain.learning.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.couch.CouchDbArrivingPatient;
import com.ibm.au.optim.suro.model.store.domain.learning.ArrivingPatientRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Class <b>CouchDbArrivingPatientRepository</b>. CouchDB specific implementation for the
 * {@link ArrivingPatientRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link ArrivingPatient} entities, 
 * via the corresponding {@link CouchDbArrivingPatient} documents.
 * 
 * @author Brendan Haesler
 */
public class CouchDbArrivingPatientRepository
				extends AbstractCouchDbRepository<CouchDbArrivingPatient, ArrivingPatient>
				implements ArrivingPatientRepository {

	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";

	/**
	 * Initialises an instance of {@link CouchDbArrivingPatientRepository}.
	 */
	public CouchDbArrivingPatientRepository() {
		super(CouchDbArrivingPatient.class);
	}

	/**
	 * This method retrieves the list of expected incoming patients in the time
	 * range identified by <i>timeFrom</i> and <i>timeTo</i>.
	 * 
	 * @param timeFrom	a {@literal long} indicating the lower bound of the time
	 * 					range of interest expressed as a timestamp.
	 * @param timeTo	a {@literal long} indicating the upper bound of the time
	 * 					range of interest expressed as a timestamp.
	 * 
	 * @return 	a {@link List} implementation of {@link ArrivingPatient} instances
	 * 			representing the patients that are expected to come to the hospital
	 * 			within the selected time range. If there are no patients the list
	 * 			is {@literal null}.
	 */
	@Override
	public List<ArrivingPatient> findByTimeRange(long timeFrom, long timeTo) {
		
		List<CouchDbArrivingPatient> documents = this.proxy.getViewWithRange(CouchDbArrivingPatientRepository.VIEW_BY_TIME, timeFrom, timeTo);

		if (documents == null || documents.isEmpty()) {
			return null;
		}

		List<ArrivingPatient> patients = new ArrayList<>();

		for (CouchDbArrivingPatient document : documents) {
			patients.add(document.getContent());
		}

		return patients;
	}
}
