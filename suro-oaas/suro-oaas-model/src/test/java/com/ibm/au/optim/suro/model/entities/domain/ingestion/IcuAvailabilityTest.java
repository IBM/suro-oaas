package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailability;

/**
 * @author brendanhaesler
 */
public class IcuAvailabilityTest {

	@Test
	public void testConstructor() {
		
		String patientId = "1234A";
		long leaveDate = new Date().getTime();

		IcuAvailability icu = new IcuAvailability(patientId, leaveDate);

		Assert.assertEquals(patientId, icu.getPatientId());
		Assert.assertEquals(leaveDate, icu.getEstimatedLeaveDate());
	}
	
	@Test
	public void testSetters() {
		
		String patientId = "1234A";
		long leaveDate = new Date().getTime();

		IcuAvailability icu = new IcuAvailability(null, 0);
		icu.setPatientId(patientId);
		icu.setEstimatedLeaveDate(leaveDate);

		Assert.assertEquals(patientId, icu.getPatientId());
		Assert.assertEquals(leaveDate, icu.getEstimatedLeaveDate());
	}

	@Test
	public void testEquals() {
		
		String pid1 = "123A";
		String pid2 = "0815";

		IcuAvailability av1 = new IcuAvailability(pid1, 0);
		IcuAvailability av2 = new IcuAvailability(pid2, 0);

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
		
		IcuAvailability entry1 = new IcuAvailability();
		IcuAvailability entry2 = new IcuAvailability();

		String pid1 = "123A";
		String pid2 = "0815";

		int day1 = 15;
		int day2 = 12;

		Map<IcuAvailability, String> map = new HashMap<>();
		entry1.setPatientId(pid1);
		entry1.setEstimatedLeaveDate(day1);
		entry2.setPatientId(pid2);
		entry2.setEstimatedLeaveDate(day2);

		map.put(entry1, "A");
		map.put(entry2, "B");
		map.put(new IcuAvailability(), "C");

		Assert.assertEquals("C", map.get(new IcuAvailability()));

		Assert.assertEquals("A", map.get(entry1));
		Assert.assertEquals("B", map.get(entry2));
		entry1.setPatientId(pid2);

		Assert.assertNull(map.get(entry1));
		entry1.setEstimatedLeaveDate(day2);
		map.put(entry1, "A");
		Assert.assertEquals("A", map.get(entry2));
		Assert.assertEquals("C", map.get(new IcuAvailability()));
	}
}
