package com.ibm.au.optim.suro.model.store.domain.learning.impl;

import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatientList;
import com.ibm.au.optim.suro.model.store.domain.learning.InitialPatientListRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class TransientInitialPatientListRepository
				extends AbstractTransientRepository<InitialPatientList, InitialPatientList>
				implements InitialPatientListRepository {

	@Override
	public InitialPatientList findByTime(long timestamp) {
		long mostRecent = 0;
		InitialPatientList result = null;

		for (InitialPatientList initialPatientList : repositoryContent.values()) {
			long tempTime = initialPatientList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = initialPatientList;
			}
		}

		return result;
	}
}
