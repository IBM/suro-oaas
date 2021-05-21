package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatient;

/**
 * Tests for the {@link WaitingPatient}.
 *
 * @author brendanhaesler
 */
public class WaitingPatientTest {

	@Test
	public void testConstructor() {
		
		String patientId = "1234A";
		String operationId = "4321B";
		String surgeryName = "Apendectomy";
		long dueDate = new Date().getTime();
		int urgencyCategory = 3;

		WaitingPatient wp = new WaitingPatient(patientId, operationId, surgeryName, dueDate, urgencyCategory);

		Assert.assertEquals(patientId, wp.getPatientId());
		Assert.assertEquals(operationId, wp.getSurgeryId());
		Assert.assertEquals(surgeryName, wp.getSurgeryName());
		Assert.assertEquals(dueDate, wp.getDueDate());
		Assert.assertEquals(urgencyCategory, wp.getUrgencyCategory());
	}

	@Test
	public void testSetters() {
		
		String patientId = "1234A";
		String operationId = "4321B";
		String surgeryName = "Apendectomy";
		long dueDate = new Date().getTime();
		int urgencyCategory = 3;

		WaitingPatient wp = new WaitingPatient(null, null, null, 0L, 0);
		wp.setPatientId(patientId);
		wp.setSurgeryId(operationId);
		wp.setSurgeryName(surgeryName);
		wp.setDueDate(dueDate);
		wp.setUrgencyCategory(urgencyCategory);

		Assert.assertEquals(patientId, wp.getPatientId());
		Assert.assertEquals(operationId, wp.getSurgeryId());
		Assert.assertEquals(surgeryName, wp.getSurgeryName());
		Assert.assertEquals(dueDate, wp.getDueDate());
		Assert.assertEquals(urgencyCategory, wp.getUrgencyCategory());
	}


	@Test
	public void testEquals() {
		
		String operationId = "007";
		WaitingPatient entry1 = null;
		WaitingPatient entry2 = new WaitingPatient();

		Assert.assertFalse(entry2.equals(entry1));
		Assert.assertTrue(entry2.equals(entry2));

		entry1 = new WaitingPatient();
		entry1.setSurgeryId(operationId);

		Assert.assertFalse(entry2.equals(entry1));
		Assert.assertTrue(entry1.equals(entry1));

		entry2.setSurgeryId(entry1.getSurgeryId());
		Assert.assertTrue(entry2.equals(entry1));
		Assert.assertTrue(entry1.equals(entry2));

		Assert.assertFalse(entry1.equals(operationId));
	}

	@Test
	public void testHashCode() {
		
		WaitingPatient entry1 = new WaitingPatient();
		WaitingPatient entry2 = new WaitingPatient();

		String specId1 = "007";
		String specId2 = "OSS-117";
		int day1 = 15;
		int day2 = 12;

		Map<WaitingPatient, String> map = new HashMap<>();
		entry1.setSurgeryId(specId1);
		entry1.setDueDate(day1);
		entry2.setSurgeryId(specId2);
		entry2.setDueDate(day2);

		map.put(entry1, "A");
		map.put(entry2, "B");
		map.put(new WaitingPatient(), "C");

		Assert.assertEquals("C", map.get(new WaitingPatient()));

		Assert.assertEquals("A", map.get(entry1));
		Assert.assertEquals("B", map.get(entry2));
		entry1.setSurgeryId(specId2);

		Assert.assertNull(map.get(entry1));
		entry1.setDueDate(day2);
		map.put(entry1, "A");
		Assert.assertEquals("A", map.get(entry2));
		Assert.assertEquals("C", map.get(new WaitingPatient()));
	}
}
