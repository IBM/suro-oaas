package com.ibm.au.optim.suro.model.store.domain.learning;

import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatientList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.TemporalRepository;

/**
 * @author brendanhaesler
 */

// TODO: Document this interface
public interface InitialPatientListRepository extends TemporalRepository<InitialPatient, InitialPatientList> {

	String INITIAL_PATIENT_LIST_REPOSITORY_INSTANCE = "repo:initialpatientlist:instance";

	String INITIAL_PATIENT_LIST_REPOSITORY_TYPE = InitialPatientListRepository.class.getName();
}
