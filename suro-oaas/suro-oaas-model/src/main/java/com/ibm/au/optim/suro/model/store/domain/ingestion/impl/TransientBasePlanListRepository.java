package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.BasePlanListRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;


/**
 * Transient implementation of the {@link BasePlanListRepository} interface for testing purposes.
 *
 * @author brendanhaesler
 */
public class TransientBasePlanListRepository
		extends AbstractTransientRepository<BasePlanList, BasePlanList>
		implements BasePlanListRepository {

	@Override
	public BasePlanList findByTime(long timestamp) {
		long mostRecent = 0;
		BasePlanList result = null;

		for (BasePlanList basePlanList : repositoryContent.values()) {
			long tempTime = basePlanList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = basePlanList;
			}
		}

		return result;
	}
}
