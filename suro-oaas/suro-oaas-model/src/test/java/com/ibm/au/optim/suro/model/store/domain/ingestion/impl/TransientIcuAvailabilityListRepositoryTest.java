package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientIcuAvailabilityListRepository;

/**
 * @author brendanhaesler
 * @param <TransientIcuAvailabilityListRepositoryTest>
 */
public class TransientIcuAvailabilityListRepositoryTest {

	@Test
	public void testFindByTime() {
		
		TransientIcuAvailabilityListRepository repository = new TransientIcuAvailabilityListRepository();
		long timeNow = new Date().getTime();
		long oneWeek = 7 * 24 * 60 * 60 * 1000;

		for (int i = 0; i < 5; ++i) {
			repository.addItem(new IcuAvailabilityList(timeNow + i * oneWeek));
		}

		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(timeNow + i * oneWeek, repository.findByTime(timeNow + i * oneWeek).getTimestamp());
		}
	}
}
