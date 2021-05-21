package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.Ward;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link InputComponent} that constructs information about wards and ward availabilities over the course of
 * the time period.
 *
 * @author brendanhaesler
 */
public class WardInfoInputComponent extends TemporalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains ward info.
	 */
	private static final int WARD_ID_INDEX = 0;

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that ward availability info.
	 */
	private static final int WARD_AVAILABILITY_INDEX = 1;

	/**
	 * Retrieves information from the {@link HospitalController} and {@link IngestionController} about wards and ward
	 * availabilities and sets it to the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		HospitalController hospitalController =
						(HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
		IngestionController ingestionController =
						(IngestionController) environment.getAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE);

		Hospital hospital = hospitalController.getHospital(this.getHospitalId());

		if (hospital == null) {
			return;
		}

		List<Ward> wards = hospital.getWards();

		if (wards == null) {
			return;
		}

		List<WardAvailability> availabilities =
						ingestionController.getLatestRecordBeforeTime(WardAvailability.class, this.getTimeFrom());

		// create the value for ward ids
		constructWardIdValue(wards);

		// create the bed availabilities from the gathered data
		List<Map<String, Integer>> beds = buildBedAvailabilities(wards, availabilities);

		// create the ward info value
		constructWardInfoValue(beds);
	}

	/**
	 * Helper method to format the ward data.
	 * @param wards A {@link List} of {@link Ward}s.
	 */
	private void constructWardIdValue(List<Ward> wards) {
		// write the ward ids
		StringBuilder wardIdValue = new StringBuilder("{\n");

		for (Ward ward : wards) {
			wardIdValue.append(new NTuple(ward.getId()).toString())
							.append("\n");
		}

		wardIdValue.append("}");

		// set the ward ids to the section
		this.setSectionValue(WARD_ID_INDEX, wardIdValue.toString());
	}

	/**
	 * Helper method to format the ward availability data.
	 * @param beds A {@link List} of {@link Map} of WardID -> NumBeds.
	 */
	private void constructWardInfoValue(List<Map<String, Integer>> beds) {
		// write the availabilities
		StringBuilder value = new StringBuilder("{\n");

		// for each day of the schedule (defined by the number of entries in the list)
		for (int date = 0; date < beds.size(); ++date) {
			// get the map of ward->beds
			Map<String, Integer> wardMap = beds.get(date);

			// for each ward in the map
			for (Map.Entry<String, Integer> entry : wardMap.entrySet()) {
				NTuple tuple = new NTuple(entry.getKey(),  // ward id
								date + 1,                          // day of schedule: 0 based, adjust to 1 based
								entry.getValue());                 // beds available

				value.append(tuple.toString())
								.append("\n");
			}
		}

		value.append("}");

		this.setSectionValue(WARD_AVAILABILITY_INDEX, value.toString());
	}

	/**
	 * Helper method to generate bed availability in wards.
	 * @param wards A {@link List} of {@link Ward}s.
	 * @param availabilities A {@link List} of {@link WardAvailability}.
	 * @return
	 */
	private List<Map<String, Integer>> buildBedAvailabilities(List<Ward> wards, List<WardAvailability> availabilities) {
		// build a list of availabilities
		int days = (int) ((this.getTimeTo() - this.getTimeFrom()) / MS_IN_DAY) + 1;
		List<Map<String, Integer>> beds = new ArrayList<>();

		for (int i = 1; i <= days; ++i) {
			// create a map of wards for each day
			Map<String, Integer> wardMap = new HashMap<>();

			// initialise the beds to the base number for the ward
			for (Ward ward : wards) {
				wardMap.put(ward.getId(), ward.getBedsCount());
			}

			// add the ward map to the list
			beds.add(wardMap);
		}

		// modify the beds by the availabilities
		for (WardAvailability availability : availabilities) {
			// if the availability falls within the time range
			if (availability.getEstimatedLeaveDate() > this.getTimeFrom()) {
				// find which day it's on
				int day = (int) ((availability.getEstimatedLeaveDate() - this.getTimeFrom()) / MS_IN_DAY);

				// modify the availability for all relevant dates
				for (int i = 0; i < day && i < days; ++i) {
					Map<String, Integer> wardMap = beds.get(i);
					wardMap.put(availability.getWardId(), wardMap.get(availability.getWardId()) - 1);
				}
			}
		}

		return beds;
	}
}
