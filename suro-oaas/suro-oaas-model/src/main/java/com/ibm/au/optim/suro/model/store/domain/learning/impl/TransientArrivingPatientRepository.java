package com.ibm.au.optim.suro.model.store.domain.learning.impl;

import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;
import com.ibm.au.optim.suro.model.store.domain.learning.ArrivingPatientRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class TransientArrivingPatientRepository
				extends AbstractTransientRepository<ArrivingPatient, ArrivingPatient>
				implements ArrivingPatientRepository {

	public List<ArrivingPatient> findByTimeRange(long timeFrom, long timeTo) {
		List<ArrivingPatient> result = new ArrayList<>();

		for (ArrivingPatient patient : repositoryContent.values()) {
			if (patient.getArrivalTime() >= timeFrom && patient.getArrivalTime() <= timeTo) {
				result.add(patient);
			}
		}

		return result;
	}
}
