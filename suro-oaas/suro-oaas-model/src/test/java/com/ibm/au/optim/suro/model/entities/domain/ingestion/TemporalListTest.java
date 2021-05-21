package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;

/**
 * @author brendanhaesler
 */
public class TemporalListTest {

	@Test
	public void testConstructor() {
		
		long timestamp = 123456789;
		TemporalList<BasePlanEntry> bp = new TemporalList<BasePlanEntry>(timestamp) { };
		Assert.assertEquals(timestamp, bp.getTimestamp());
		Assert.assertNull(bp.getRecords());


		timestamp = new Date().getTime();
		TemporalList<BasePlanEntry> bp2 = new TemporalList<BasePlanEntry>() { };
		long timestamp2 = new Date().getTime();

		Assert.assertTrue(bp2.getTimestamp() >= timestamp);
		Assert.assertTrue(bp2.getTimestamp() <= timestamp2);
		Assert.assertNull(bp2.getRecords());
	}

	@Test
	public void testSetters() {
		
		TemporalList<BasePlanEntry> basePlan = new BasePlanList();
		List<BasePlanEntry> entries = new ArrayList<>();
		long timestamp = 123456789;

		basePlan.setTimestamp(timestamp);
		Assert.assertEquals(timestamp, basePlan.getTimestamp());

		basePlan.setRecords(entries);
		Assert.assertEquals(entries, basePlan.getRecords());

		basePlan.setRecords(null);
		Assert.assertNull(basePlan.getRecords());
	}
}
