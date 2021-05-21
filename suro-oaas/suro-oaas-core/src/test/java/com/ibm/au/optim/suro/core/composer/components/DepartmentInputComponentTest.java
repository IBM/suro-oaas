package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.Department;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class DepartmentInputComponentTest extends InputComponentTestBase {

	@Override
	@Test
	public void testConstructValues() {
		// add a hospital and department
		List<Department> departments = new ArrayList<>();
		Department department = new Department("Menswear", 3);
		departments.add(department);
		hospital.setDepartments(departments);

		NTuple tuple = new NTuple(department.getId(), department.getName(), department.getMaxSimultaneousSessions());

		DepartmentInputComponent component = createHospitalInputComponent(DepartmentInputComponent.class);
		component.constructValues(environment);

		Assert.assertEquals("{\n" + tuple + "\n}", component.getSections().get(0).getValue());
	}
}
