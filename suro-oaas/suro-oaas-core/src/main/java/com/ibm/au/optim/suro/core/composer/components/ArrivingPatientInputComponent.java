package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.learning.MachineLearningController;
import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about patients that are predicted to arrive over the course of
 * the time period.
 *
 * @author brendanhaesler
 */
public class ArrivingPatientInputComponent extends TemporalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains arriving patient info.
	 */
	private static final int ARRIVING_PATIENT_INFO_INDEX = 0;

	/**
	 * Retrieves information from the {@link MachineLearningController} about predicted arriving patients and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		// get the machine learning controller
		MachineLearningController machineLearningController =	(MachineLearningController) environment.getAttribute(
						MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE);

		// get the arriving patients
		List<ArrivingPatient> patients = machineLearningController.getArrivingPatients(this.getTimeFrom(), this.getTimeTo());

		// construct the arriving patients value
		constructArrivingPatientValue(patients);
	}

	/**
	 * Helper method to format the arriving patient data.
	 * @param patients A {@link List} of {@link ArrivingPatient}s.
	 */
	private void constructArrivingPatientValue(List<ArrivingPatient> patients) {
		StringBuilder value = new StringBuilder("{\n");

		for (ArrivingPatient patient : patients) {
			NTuple tuple = new NTuple(patient.getSurgeryClusterId(),
							(int) ((patient.getArrivalTime() - this.getTimeFrom()) / MS_IN_DAY) + 1,
							patient.getNumPatients());

			value.append(tuple.toString())
							.append("\n");
		}

		value.append("}");

		this.setSectionValue(ARRIVING_PATIENT_INFO_INDEX, value.toString());
	}
}
