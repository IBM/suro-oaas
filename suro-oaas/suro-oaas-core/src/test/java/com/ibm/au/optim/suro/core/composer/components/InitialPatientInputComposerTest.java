package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatientList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class InitialPatientInputComposerTest extends InputComponentTestBase {

	@Override
	@Test
	public void testConstructValues() {
		// create an iniital patient
		InitialPatient patient = new InitialPatient("11111", 28, 3);
		List<InitialPatient> initialPatients = new ArrayList<>();
		initialPatients.add(patient);
		InitialPatientList patientList = new InitialPatientList(TEST_TIME_FROM - TemporalInputComponent.MS_IN_DAY);
		patientList.setRecords(initialPatients);
		ipRepo.addItem(patientList);

		NTuple tuple = new NTuple("11111", 28, 3);

		InitialPatientInputComponent component = createTemporalInputComponent(InitialPatientInputComponent.class);
		component.constructValues(environment);

		Assert.assertEquals("{\n" + tuple + "\n}", component.getSections().get(0).getValue());
	}
}
