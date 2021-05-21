package com.ibm.au.optim.suro.model.store.domain.learning;

import java.util.List;

import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;
import com.ibm.au.optim.suro.model.store.Repository;

/**
 * @author brendanhaesler
 */

// TODO: Document this interface
public interface ArrivingPatientRepository extends Repository<ArrivingPatient> {

	String ARRIVING_PATIENT_REPOSITORY_INSTANCE = "repo:arrivingpatient:instance";

	String ARRIVING_PATIENT_REPOSITORY_TYPE = ArrivingPatientRepository.class.getName();

	List<ArrivingPatient> findByTimeRange(long timeFrom, long timeTo);
}
