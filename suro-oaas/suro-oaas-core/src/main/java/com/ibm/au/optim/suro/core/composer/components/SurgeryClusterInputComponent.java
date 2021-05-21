package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.learning.MachineLearningController;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryCluster;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about surgery clusters.
 *
 * @author brendanhaesler
 */
public class SurgeryClusterInputComponent extends TemporalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains surgery cluster info.
	 */
	private static final int SURGERIES_INDEX = 0;

	/**
	 * Retrieves information from the {@link MachineLearningController} about surgery clusters and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		// get the machine learning controller
		MachineLearningController machineLearningController =	(MachineLearningController) environment.getAttribute(
						MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE);

		// get the arriving patients
		List<SurgeryCluster> surgeries = machineLearningController.getSurgeryClusters(this.getTimeFrom());

		// construct the arriving patients value
		constructSurgeriesValue(surgeries);
	}

	/**
	 * Helper method to format the surgery cluster data.
	 * @param surgeries A {@link List} of {@link SurgeryCluster}s.
	 */
	private void constructSurgeriesValue(List<SurgeryCluster> surgeries) {
		StringBuilder value = new StringBuilder("{\n");

		for (SurgeryCluster surgery : surgeries) {
			NTuple tuple = new NTuple(surgery.getClusterId(),
							surgery.getClusterName(),
							new NTuple(surgery.getDepartmentId()),
							surgery.getDuration(),
							surgery.getChangeOverTime(),
							surgery.getLengthOfStay(),
							surgery.getIcuProbability(),
							surgery.getWardId(),
							surgery.getSpecialistTypeId(),
							surgery.getUrgencyCategoryId(),
							surgery.getWies());

			value.append(tuple.toString())
							.append("\n");
		}

		value.append("}");
		this.setSectionValue(SURGERIES_INDEX, value.toString());
	}
}
