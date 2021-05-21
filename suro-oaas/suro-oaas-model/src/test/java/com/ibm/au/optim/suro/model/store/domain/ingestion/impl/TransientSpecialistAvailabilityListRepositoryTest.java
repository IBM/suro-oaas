package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientSpecialistAvailabilityListRepository;

/**
 * @author brendanhaesler
 */
public class TransientSpecialistAvailabilityListRepositoryTest {

	@Test
	public void testFindByTime() {
		
		TransientSpecialistAvailabilityListRepository repository = new TransientSpecialistAvailabilityListRepository();
		long timeNow = new Date().getTime();
		long oneWeek = 7 * 24 * 60 * 60 * 1000;

		for (int i = 0; i < 5; ++i) {
			repository.addItem(new SpecialistAvailabilityList(timeNow + i * oneWeek));
		}

		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(timeNow + i * oneWeek, repository.findByTime(timeNow + i * oneWeek).getTimestamp());
		}
	}
}
