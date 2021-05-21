package com.ibm.au.optim.suro.model.store.domain.ingestion;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatient;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;

/**
 * The interface for the {@link WaitingPatient} repository.
 *
 * @author brendanhaesler
 */

public interface WaitingPatientListRepository extends TemporalRepository<WaitingPatient, WaitingPatientList> {

	/**
	 * A {@link String} constant containing the name of the attribute that specifies the
	 * instance of the {@link WaitingPatientListRepository} implementation that has been injected
	 * into the environment.
	 */
	String WAITING_PATIENT_REPOSITORY_INSTANCE = "repo:waitingpatientlist:instance";

	/**
	 * A {@link String} constant containing the name type of {@link WaitingPatientListRepository}
	 * that will be used in the application.
	 */
	String WAITING_PATIENT_REPOSITORY_TYPE = WaitingPatientListRepository.class.getName();
}
