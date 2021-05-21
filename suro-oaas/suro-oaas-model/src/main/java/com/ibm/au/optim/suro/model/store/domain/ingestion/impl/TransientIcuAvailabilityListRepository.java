package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.IcuAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;


/**
 * Transient implementation of the {@link IcuAvailabilitiesRepository} interface for testing purposes.
 *
 * @author brendanhaesler
 */
public class TransientIcuAvailabilityListRepository
	extends AbstractTransientRepository<IcuAvailabilityList, IcuAvailabilityList>
	implements IcuAvailabilitiesRepository {

	@Override
	public IcuAvailabilityList findByTime(long timestamp) {
		long mostRecent = 0;
		IcuAvailabilityList result = null;

		for (IcuAvailabilityList icuAvailabilityList : repositoryContent.values()) {
			long tempTime = icuAvailabilityList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = icuAvailabilityList;
			}
		}

		return result;
	}
}
