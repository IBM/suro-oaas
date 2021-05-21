package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WaitingPatientListRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;


/**
 * Transient implementation of the {@link WaitingPatientListRepository} interface for testing purposes.
 * @author brendanhaesler
 */
public class TransientWaitingPatientListRepository
	extends AbstractTransientRepository<WaitingPatientList, WaitingPatientList>
	implements WaitingPatientListRepository {

	@Override
	public WaitingPatientList findByTime(long timestamp) {
		long mostRecent = 0;
		WaitingPatientList result = null;

		for (WaitingPatientList waitingPatientList : repositoryContent.values()) {
			long tempTime = waitingPatientList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = waitingPatientList;
			}
		}

		return result;
	}
}
