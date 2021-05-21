package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about the availability of specialists over the course of
 * the time period.
 *
 * @author brendanhaesler
 */
public class SpecialistAvailabilityInputComponent extends TemporalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains specialist availability
	 * info.
	 */
	private static final int SPECIALIST_AVAILABILITY_INDEX = 0;

	/**
	 * Retrieves information from the {@link IngestionController} about specialist availabilities and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {

		// get the ingestion controller
		IngestionController ingestionController =
						(IngestionController) environment.getAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE);

		// get specialist availabilities
		List<SpecialistAvailability> availabilities =
						ingestionController.getLatestRecordBeforeTime(SpecialistAvailability.class, getTimeFrom());

		// create specialist availability value
		constructSpecialistAvailabilityValue(availabilities);
	}

	/**
	 * Helper method to format the specialist availability data.
	 * @param availabilities A {@link List} of {@link SpecialistAvailability}s.
	 */
	private void constructSpecialistAvailabilityValue(List<SpecialistAvailability> availabilities) {
		StringBuilder value = new StringBuilder("{\n");

		// for each availability
		for (SpecialistAvailability a : availabilities) {
			// if the availability refers to a time between the first and last day of this run
			if (a.getDate() >= this.getTimeFrom() && a.getDate() < (this.getTimeTo() + MS_IN_DAY)) {
				NTuple tuple = new NTuple(a.getSpecialistTypeId(),
								((a.getDate() - this.getTimeFrom()) / MS_IN_DAY) + 1,
								a.getNumAvailable());

				value.append(tuple.toString())
								.append("\n");
			}
		}

		value.append("}");

		this.setSectionValue(SPECIALIST_AVAILABILITY_INDEX, value.toString());
	}
}
