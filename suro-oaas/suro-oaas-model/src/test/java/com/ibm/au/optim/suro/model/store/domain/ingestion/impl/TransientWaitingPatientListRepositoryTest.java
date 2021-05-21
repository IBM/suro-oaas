package com.ibm.au.optim.suro.model.store.domain.ingestion.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientWaitingPatientListRepository;

/**
 * @author brendanhaesler
 */
public class TransientWaitingPatientListRepositoryTest {

	@Test
	public void testFindByTime() {
		
		TransientWaitingPatientListRepository repository = new TransientWaitingPatientListRepository();
		long timeNow = new Date().getTime();
		long oneWeek = 7 * 24 * 60 * 60 * 1000;

		for (int i = 0; i < 5; ++i) {
			repository.addItem(new WaitingPatientList(timeNow + i * oneWeek));
		}

		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(timeNow + i * oneWeek, repository.findByTime(timeNow + i * oneWeek).getTimestamp());
		}
	}
}
