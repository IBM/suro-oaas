package com.ibm.au.optim.suro.model.store.domain.learning.impl;

import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryClusterList;
import com.ibm.au.optim.suro.model.store.domain.learning.SurgeryClusterListRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class TransientSurgeryClusterListRepository
				extends AbstractTransientRepository<SurgeryClusterList, SurgeryClusterList>
				implements SurgeryClusterListRepository {

	@Override
	public SurgeryClusterList findByTime(long timestamp) {
		long mostRecent = 0;
		SurgeryClusterList result = null;

		for (SurgeryClusterList surgeryClusterList : repositoryContent.values()) {
			long tempTime = surgeryClusterList.getTimestamp();

			if (tempTime <= timestamp && tempTime > mostRecent) {
				mostRecent = tempTime;
				result = surgeryClusterList;
			}
		}

		return result;
	}
}
