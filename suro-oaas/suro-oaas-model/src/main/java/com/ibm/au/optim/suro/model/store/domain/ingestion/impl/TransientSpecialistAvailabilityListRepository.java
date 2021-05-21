package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.SpecialistAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;


/**
 * Transient implementation of the {@link SpecialistAvailabilitiesRepository} interface for testing purposes.
 *
 * @author brendanhaesler
 */
public class TransientSpecialistAvailabilityListRepository
	extends AbstractTransientRepository<SpecialistAvailabilityList, SpecialistAvailabilityList>
	implements SpecialistAvailabilitiesRepository {

	@Override
	public SpecialistAvailabilityList findByTime(long timestamp) {
		long mostRecent = 0;
		SpecialistAvailabilityList result = null;

		for (SpecialistAvailabilityList specialistAvailabilityList : repositoryContent.values()) {
			long tempTime = specialistAvailabilityList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = specialistAvailabilityList;
			}
		}

		return result;
	}
}
