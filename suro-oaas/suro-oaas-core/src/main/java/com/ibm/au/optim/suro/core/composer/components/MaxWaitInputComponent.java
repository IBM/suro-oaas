package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An {@link InputComponent} that constructs information about the maximum waiting period for urgency categories.
 *
 * @author brendanhaesler
 */
public class MaxWaitInputComponent extends HospitalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains max waiting period info.
	 */
	private static final int MAX_WAIT_INDEX = 0;

	/**
	 * Retrieves information from the {@link HospitalController} about urgency categories and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		// get the urgency categories
		HospitalController hospitalController =
						(HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
		Hospital hospital = hospitalController.getHospital(this.getHospitalId());

		if (hospital == null) {
			return;
		}

		List<UrgencyCategory> urgencyCategories = hospital.getUrgencyCategories();

		// sort the categories by the max wait (this assumes that max waits coincide with category importance)
		Collections.sort(urgencyCategories, new Comparator<UrgencyCategory>() {
			@Override
			public int compare(UrgencyCategory o1, UrgencyCategory o2) {
				return o1.getMaxWaitListStay() - o2.getMaxWaitListStay();
			}
		});

		// build the value
		StringBuilder value = new StringBuilder("{\n[");

		for (int i = 0; i < urgencyCategories.size(); ++i) {
			value.append(urgencyCategories.get(i).getMaxWaitListStay());

			if (i + 1 < urgencyCategories.size()) {
				value.append(" ");
			}
		}

		value.append("]\n}");

		this.setSectionValue(MAX_WAIT_INDEX, value.toString());
	}
}
