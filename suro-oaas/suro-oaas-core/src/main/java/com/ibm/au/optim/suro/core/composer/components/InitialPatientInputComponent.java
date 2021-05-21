package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.learning.MachineLearningController;
import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatient;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about patients that are waiting for surgery.
 *
 * @author brendanhaesler
 */
public class InitialPatientInputComponent extends TemporalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains initial patient info.
	 */
	private static final int INITIAL_PATIENT_INFO_INDEX = 0;

	/**
	 * Retrieves information from the {@link MachineLearningController} about initial patients and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		// get the machine learning controller
		MachineLearningController machineLearningController =	(MachineLearningController) environment.getAttribute(
						MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE);

		// get the arriving patients
		List<InitialPatient> patients = machineLearningController.getInitialPatients(this.getTimeFrom());

		// construct the arriving patients value
		constructArrivingPatientValue(patients);
	}

	/**
	 * Helper method to format the initial patient data.
	 * @param patients A {@link List} of {@link InitialPatient}s.
	 */
	private void constructArrivingPatientValue(List<InitialPatient> patients) {
		StringBuilder value = new StringBuilder("{\n");

		for (InitialPatient patient : patients) {
			NTuple tuple = new NTuple(patient.getSurgeryClusterId(), patient.getDaysRemaining(),	patient.getNumPatients());

			value.append(tuple.toString())
							.append("\n");
		}

		value.append("}");

		this.setSectionValue(INITIAL_PATIENT_INFO_INDEX, value.toString());
	}
}
