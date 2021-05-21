package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.SpecialistType;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about specialist types.
 *
 * @author brendanhaesler
 */
public class SpecialistInputComponent extends HospitalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains specialist type info.
	 */
	private static final int SPECIALIST_ID_INDEX = 0;

	/**
	 * Retrieves information from the {@link HospitalController} about specialist types and sets it to
	 * the {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		// Get the hospital
		HospitalController hospitalController =
						(HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
		Hospital hospital = hospitalController.getHospital(this.getHospitalId());

		if (hospital == null) {
			return;
		}

		// Get the specialist types
		List<SpecialistType> specialistTypes = hospital.getSpecialistTypes();

		// create the specialist id value
		constructSpecialistInfoValue(specialistTypes);
	}

	/**
	 * Helper method to format the specialist type data.
	 * @param specialistTypes A {@link List} of {@link SpecialistType}s.
	 */
	private void constructSpecialistInfoValue(List<SpecialistType> specialistTypes) {
		StringBuilder value = new StringBuilder("{\n");

		for (SpecialistType specialistType : specialistTypes) {
			value.append(new NTuple(specialistType.getId()).toString())
							.append("\n");
		}

		value.append("}");

		this.setSectionValue(SPECIALIST_ID_INDEX, value.toString());
	}
}
