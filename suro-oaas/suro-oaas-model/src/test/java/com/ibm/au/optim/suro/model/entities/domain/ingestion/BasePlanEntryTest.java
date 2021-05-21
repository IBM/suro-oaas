package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;

/**
 * @author brendanhaesler
 */
public class BasePlanEntryTest {

	@Test
	public void testConstructor() {
		
		String departmentId = "Cardiology";
		int day = 14;
		int numSessions = 2;

		BasePlanEntry basePlanEntry = new BasePlanEntry();
		Assert.assertNotNull(basePlanEntry);

		basePlanEntry = new BasePlanEntry(departmentId, day, numSessions);
		Assert.assertEquals(departmentId, basePlanEntry.getSpecialistTypeId());
		Assert.assertEquals(day, basePlanEntry.getDay());
		Assert.assertEquals(numSessions, basePlanEntry.getNumSessions());
	}

	@Test
	public void testSetters() {
		
		String departmentId = "Cardiology";
		int day = 14;
		int numSessions = 2;

		BasePlanEntry BasePlanEntry = new BasePlanEntry(null, 0, 0);
		BasePlanEntry.setSpecialistTypeId(departmentId);
		BasePlanEntry.setDay(day);
		BasePlanEntry.setNumSessions(numSessions);

		Assert.assertEquals(departmentId, BasePlanEntry.getSpecialistTypeId());
		Assert.assertEquals(day, BasePlanEntry.getDay());
		Assert.assertEquals(numSessions, BasePlanEntry.getNumSessions());
	}

	@Test
	public void testEquals() {
		
		String departmentId = "Cardiology";
		BasePlanEntry entry1 = null;
		BasePlanEntry entry2 = new BasePlanEntry();

		Assert.assertFalse(entry2.equals(entry1));
		Assert.assertTrue(entry2.equals(entry2));

		entry1 = new BasePlanEntry();
		entry1.setSpecialistTypeId(departmentId);

		Assert.assertFalse(entry2.equals(entry1));
		Assert.assertTrue(entry1.equals(entry1));

		entry2.setSpecialistTypeId(entry1.getSpecialistTypeId());
		Assert.assertTrue(entry2.equals(entry1));
		Assert.assertTrue(entry1.equals(entry2));

		Assert.assertFalse(entry1.equals(departmentId));
	}

	@Test
	public void testHashCode() {
		
		BasePlanEntry entry1 = new BasePlanEntry();
		BasePlanEntry entry2 = new BasePlanEntry();

		String department1 = "Cardiology";
		String department2 = "Urology";
		int day1 = 15;
		int day2 = 12;

		Map<BasePlanEntry, String> map = new HashMap<>();
		entry1.setSpecialistTypeId(department1);
		entry1.setDay(day1);
		entry2.setSpecialistTypeId(department2);
		entry2.setDay(day2);

		map.put(entry1, "A");
		map.put(entry2, "B");
		map.put(new BasePlanEntry(), "C");

		Assert.assertEquals("C", map.get(new BasePlanEntry()));

		Assert.assertEquals("A", map.get(entry1));
		Assert.assertEquals("B", map.get(entry2));
		entry1.setSpecialistTypeId(department2);

		Assert.assertNull(map.get(entry1));
		entry1.setDay(day2);
		map.put(entry1, "A");
		Assert.assertEquals("A", map.get(entry2));
		Assert.assertEquals("C", map.get(new BasePlanEntry()));
	}
}
