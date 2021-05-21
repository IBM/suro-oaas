package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about the current base plan being used by the hospital.
 *
 * @author brendanhaesler
 */
public class BasePlanInputComponent extends TemporalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains base plan info.
	 */
	private static final int BASE_PLAN_INDEX = 0;

	/**
	 * Retrieves information from the {@link IngestionController} about the base plan and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		// get the ingestion controller
		IngestionController ingestionController =
						(IngestionController) environment.getAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE);

		// get base plan
		List<BasePlanEntry> basePlanEntries =
						ingestionController.getLatestRecordBeforeTime(BasePlanEntry.class, getTimeFrom());

		// create base plan value
		constructBasePlanValue(basePlanEntries);
	}

	/**
	 * Helper method to format the base plan data.
	 * @param basePlanEntries A {@link List} of {@link BasePlanEntry} objects.
	 */
	private void constructBasePlanValue(List<BasePlanEntry> basePlanEntries) {
		StringBuilder value = new StringBuilder("{\n");

		// for each availability
		for (BasePlanEntry basePlan : basePlanEntries) {
			NTuple tuple = new NTuple(new NTuple(basePlan.getSpecialistTypeId()),
							basePlan.getDay(),
							basePlan.getNumSessions());

			value.append(tuple.toString())
							.append("\n");
		}

		value.append("}");

		this.setSectionValue(BASE_PLAN_INDEX, value.toString());
	}
}
