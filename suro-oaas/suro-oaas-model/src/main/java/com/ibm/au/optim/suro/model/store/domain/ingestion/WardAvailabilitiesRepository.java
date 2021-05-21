package com.ibm.au.optim.suro.model.store.domain.ingestion;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;

/**
 * The interface for the {@link WardAvailability} repository.
 *
 * @author brendanhaesler
 */

public interface WardAvailabilitiesRepository extends TemporalRepository<WardAvailability, WardAvailabilityList> {

	/**
	 * A {@link String} constant containing the name of the attribute that specifies the
	 * instance of the {@link WardAvailabilitiesRepository} implementation that has been
	 * injected into the environment.
	 */
	String WARD_AVAILABILITY_REPOSITORY_INSTANCE = "repo:wardavailabilities:instance";

	/**
	 * A {@link String} constant containing the name type of {@link WardAvailabilitiesRepository}
	 * that will be used in the application.
	 */
	String WARD_AVAILABILITY_REPOSITORY_TYPE = WardAvailabilitiesRepository.class.getName();
}
