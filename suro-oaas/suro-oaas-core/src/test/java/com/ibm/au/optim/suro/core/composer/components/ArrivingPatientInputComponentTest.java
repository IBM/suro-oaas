package com.ibm.au.optim.suro.core.composer.components;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;

/**
 * @author brendanhaesler
 */
public class ArrivingPatientInputComponentTest extends InputComponentTestBase {

	@Override
	@Test
	public void testConstructValues() {
		// add a patient who is arriving
		String clusterId = "11111";
		long arrivalTime = TEST_TIME_FROM + 2 * TemporalInputComponent.MS_IN_DAY;
		int numPatients = 3;

		ArrivingPatient ap = new ArrivingPatient(clusterId, arrivalTime, numPatients);
		apRepo.addItem(ap);

		NTuple tuple = new NTuple(clusterId,
						(arrivalTime - TEST_TIME_FROM) / TemporalInputComponent.MS_IN_DAY + 1,
						numPatients);

		ArrivingPatientInputComponent component = createTemporalInputComponent(ArrivingPatientInputComponent.class);
		component.constructValues(environment);

		Assert.assertEquals("{\n" + tuple + "\n}",	component.getSections().get(0).getValue());
	}
}
