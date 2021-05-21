package com.ibm.au.optim.suro.model.store.domain.ingestion;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;

/**
 * The interface for the {@link SpecialistAvailabilityList} repository.
 *
 * @author brendanhaesler
 */

public interface SpecialistAvailabilitiesRepository
				extends TemporalRepository<SpecialistAvailability, SpecialistAvailabilityList> {

	/**
	 * A {@link String} constant containing the name of the attribute that specifies the
	 * instance of the {@link SpecialistAvailabilitiesRepository} implementation that has been
	 * injected into the environment.
	 */
	String SPECIALIST_AVAILABILITY_REPOSITORY_INSTANCE = "repo:specialistavailabilities:instance";

	/**
	 * A {@link String} constant containing the name type of {@link SpecialistAvailabilitiesRepository}
	 * that will be used in the application.
	 */
	String SPECIALIST_AVAILABILITY_REPOSITORY_TYPE = SpecialistAvailabilitiesRepository.class.getName();
}
