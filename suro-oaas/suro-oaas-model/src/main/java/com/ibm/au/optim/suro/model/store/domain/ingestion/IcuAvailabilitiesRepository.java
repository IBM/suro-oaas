package com.ibm.au.optim.suro.model.store.domain.ingestion;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;

/**
 * The interface for the {@link IcuAvailabilityList} repository.
 *
 * @author brendanhaesler
 */

public interface IcuAvailabilitiesRepository extends TemporalRepository<IcuAvailability, IcuAvailabilityList> {

	/**
	 * A {@link String} constant containing the name of the attribute that specifies the
	 * instance of the {@link IcuAvailabilitiesRepository} implementation that has been injected
	 * into the environment.
	 */
	String ICU_AVAILABILITY_REPOSITORY_INSTANCE = "repo:icuavailabilities:instance";

	/**
	 * A {@link String} constant containing the name type of {@link IcuAvailabilitiesRepository}
	 * that will be used in the application.
	 */
	String ICU_AVAILABILITY_REPOSITORY_TYPE = IcuAvailabilitiesRepository.class.getName();
}
