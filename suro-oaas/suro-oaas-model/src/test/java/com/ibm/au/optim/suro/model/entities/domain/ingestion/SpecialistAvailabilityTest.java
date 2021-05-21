package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;

/**
 * @author brendanhaesler
 */
public class SpecialistAvailabilityTest {

	@Test
	public void testConstructor() {
		
		String SpecialistTypeId = "1234A";
		long date = new Date().getTime();
		int available = 3;

		SpecialistAvailability sa = new SpecialistAvailability(SpecialistTypeId, date, available);

		Assert.assertEquals(SpecialistTypeId, sa.getSpecialistTypeId());
		Assert.assertEquals(date, sa.getDate());
		Assert.assertEquals(available, sa.getNumAvailable());

		sa = new SpecialistAvailability();
		Assert.assertEquals(0, sa.getNumAvailable());
		Assert.assertEquals(0, sa.getDate());
		Assert.assertNull(sa.getSpecialistTypeId());
	}

	@Test
	public void testSetters() {
		
		String SpecialistTypeId = "1234A";
		long date = new Date().getTime();
		int available = 3;

		SpecialistAvailability sa = new SpecialistAvailability(null, 0, 0);
		sa.setSpecialistTypeId(SpecialistTypeId);
		sa.setDate(date);
		sa.setNumAvailable(available);

		Assert.assertEquals(SpecialistTypeId, sa.getSpecialistTypeId());
		Assert.assertEquals(date, sa.getDate());
		Assert.assertEquals(available, sa.getNumAvailable());
	}

	@Test
	public void testEquals() {
		
		String SpecialistTypeId = "007";
		SpecialistAvailability entry1 = null;
		SpecialistAvailability entry2 = new SpecialistAvailability();

		Assert.assertFalse(entry2.equals(entry1));
		Assert.assertTrue(entry2.equals(entry2));

		entry1 = new SpecialistAvailability();
		entry1.setSpecialistTypeId(SpecialistTypeId);

		Assert.assertFalse(entry2.equals(entry1));
		Assert.assertTrue(entry1.equals(entry1));

		entry2.setSpecialistTypeId(entry1.getSpecialistTypeId());
		Assert.assertTrue(entry2.equals(entry1));
		Assert.assertTrue(entry1.equals(entry2));

		Assert.assertFalse(entry1.equals(SpecialistTypeId));
	}

	@Test
	public void testHashCode() {
		
		SpecialistAvailability entry1 = new SpecialistAvailability();
		SpecialistAvailability entry2 = new SpecialistAvailability();

		String specId1 = "007";
		String specId2 = "OSS-117";
		int day1 = 15;
		int day2 = 12;

		Map<SpecialistAvailability, String> map = new HashMap<>();
		entry1.setSpecialistTypeId(specId1);
		entry1.setDate(day1);
		entry2.setSpecialistTypeId(specId2);
		entry2.setDate(day2);

		map.put(entry1, "A");
		map.put(entry2, "B");
		map.put(new SpecialistAvailability(), "C");

		Assert.assertEquals("C", map.get(new SpecialistAvailability()));

		Assert.assertEquals("A", map.get(entry1));
		Assert.assertEquals("B", map.get(entry2));
		entry1.setSpecialistTypeId(specId2);

		Assert.assertNull(map.get(entry1));
		entry1.setDate(day2);
		map.put(entry1, "A");
		Assert.assertEquals("A", map.get(entry2));
		Assert.assertEquals("C", map.get(new SpecialistAvailability()));
	}
}
