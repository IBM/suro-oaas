package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.entities.domain.Department;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * An {@link InputComponent} that constructs information about hospital departments.
 *
 * @author brendanhaesler
 */
public class DepartmentInputComponent extends HospitalInputComponent {

	/**
	 * The index of the {@link com.ibm.au.optim.suro.core.composer.InputSection} that contains department info.
	 */
	private static final int DEPARTMENT_INFO_INDEX = 0;

	/**
	 * Retrieves information from the {@link HospitalController} about departments and sets it to
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

		// Get the departments
		List<Department> departments = hospital.getDepartments();

		// create the departments value
		constructDepartmentValue(departments);
	}

	/**
	 * Helper method to format the department data.
	 * @param departments A {@link List} of {@link Department}s.
	 */
	private void constructDepartmentValue(List<Department> departments) {
		StringBuilder value = new StringBuilder("{\n");

		for (Department department : departments) {
			NTuple tuple = new NTuple(department.getId(), department.getName(), department.getMaxSimultaneousSessions());

			value.append(tuple.toString())
							.append("\n");
		}

		value.append("}");

		this.setSectionValue(DEPARTMENT_INFO_INDEX, value.toString());
	}
}
