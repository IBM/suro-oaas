package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;

/**
 * Tests for the WardAvailability class.
 *
 * @author brendanhaesler
 */
public class WardAvailabilityTest {

	@Test
	public void testConstructor() {
		
		String wardId = "W123";
		String patientId = "1234A";
		long estimatedLeaveDate = new Date().getTime();

		WardAvailability wa = new WardAvailability(wardId, patientId, estimatedLeaveDate);

		Assert.assertEquals(wardId, wa.getWardId());
		Assert.assertEquals(patientId, wa.getPatientId());
		Assert.assertEquals(estimatedLeaveDate, wa.getEstimatedLeaveDate());

		wa = new WardAvailability();
		Assert.assertNull(wa.getPatientId());
		Assert.assertNull(wa.getWardId());
		Assert.assertEquals(0, wa.getEstimatedLeaveDate());
	}

	@Test
	public void testSetters() {
		
		String wardId = "W123";
		String patientId = "1234A";
		long estimatedLeaveDate = new Date().getTime();

		WardAvailability wa = new WardAvailability(null, null, 0);
		wa.setWardId(wardId);
		wa.setPatientId(patientId);
		wa.setEstimatedLeaveDate(estimatedLeaveDate);

		Assert.assertEquals(wardId, wa.getWardId());
		Assert.assertEquals(patientId, wa.getPatientId());
		Assert.assertEquals(estimatedLeaveDate, wa.getEstimatedLeaveDate());
	}

	@Test
	public void testEquals() {
		
		String pid1 = "123A";
		String pid2 = "0815";

		WardAvailability av1 = new WardAvailability(null, pid1, 0);
		WardAvailability av2 = new WardAvailability(null, pid2, 0);

		Assert.assertFalse(av1.equals(av2));
		Assert.assertFalse(av1.equals(null));
		Assert.assertFalse(av1.equals(pid1));
		Assert.assertTrue(av1.equals(av1));

		av2.setEstimatedLeaveDate(5);
		av2.setPatientId(av1.getPatientId());
		Assert.assertFalse(av1.equals(av2));
		Assert.assertFalse(av2.equals(5));

		av1.setEstimatedLeaveDate(av2.getEstimatedLeaveDate());
		Assert.assertTrue(av1.equals(av2));
	}

	@Test
	public void testHashCode() {
		
		WardAvailability entry1 = new WardAvailability();
		WardAvailability entry2 = new WardAvailability();

		String pid1 = "123A";
		String pid2 = "0815";

		int day1 = 15;
		int day2 = 12;

		Map<WardAvailability, String> map = new HashMap<>();
		entry1.setPatientId(pid1);
		entry1.setEstimatedLeaveDate(day1);
		entry2.setPatientId(pid2);
		entry2.setEstimatedLeaveDate(day2);

		map.put(entry1, "A");
		map.put(entry2, "B");
		map.put(new WardAvailability(), "C");

		Assert.assertEquals("C", map.get(new WardAvailability()));

		Assert.assertEquals("A", map.get(entry1));
		Assert.assertEquals("B", map.get(entry2));
		entry1.setPatientId(pid2);

		Assert.assertNull(map.get(entry1));
		entry1.setEstimatedLeaveDate(day2);
		map.put(entry1, "A");
		Assert.assertEquals("A", map.get(entry2));
		Assert.assertEquals("C", map.get(new WardAvailability()));
	}
}
