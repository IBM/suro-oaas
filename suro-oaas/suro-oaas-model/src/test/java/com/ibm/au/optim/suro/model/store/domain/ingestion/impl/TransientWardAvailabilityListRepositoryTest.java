package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientWardAvailabilityListRepository;

/**
 * @author brendanhaesler
 */
public class TransientWardAvailabilityListRepositoryTest {

	@Test
	public void testFindByTime() {
		
		TransientWardAvailabilityListRepository repository = new TransientWardAvailabilityListRepository();
		long timeNow = new Date().getTime();
		long oneWeek = 7 * 24 * 60 * 60 * 1000;

		for (int i = 0; i < 5; ++i) {
			repository.addItem(new WardAvailabilityList(timeNow + i * oneWeek));
		}

		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(timeNow + i * oneWeek, repository.findByTime(timeNow + i * oneWeek).getTimestamp());
		}
	}
}
