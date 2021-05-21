package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientBasePlanListRepository;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class TransientBasePlanListRepositoryTest {

	@Test
	public void testFindByTime() {
		TransientBasePlanListRepository repository = new TransientBasePlanListRepository();
		long timeNow = new Date().getTime();
		long oneWeek = 7 * 24 * 60 * 60 * 1000;

		for (int i = 0; i < 5; ++i) {
			repository.addItem(new BasePlanList(timeNow + i * oneWeek));
		}

		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(timeNow + i * oneWeek, repository.findByTime(timeNow + i * oneWeek).getTimestamp());
		}
	}
}
