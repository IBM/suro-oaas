package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WardAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;


/**
 * Transient implementation of the {@link WardAvailabilitiesRepository} interface for testing purposes.
 *
 * @author brendanhaesler
 */
public class TransientWardAvailabilityListRepository
	extends AbstractTransientRepository<WardAvailabilityList, WardAvailabilityList>
	implements WardAvailabilitiesRepository {

	@Override
	public WardAvailabilityList findByTime(long timestamp) {
		long mostRecent = 0;
		WardAvailabilityList result = null;

		for (WardAvailabilityList wardAvailabilityList : repositoryContent.values()) {
			long tempTime = wardAvailabilityList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = wardAvailabilityList;
			}
		}

		return result;
	}
}
