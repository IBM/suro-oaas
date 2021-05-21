package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.SpecialistType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class SpecialistInputComponentTest extends InputComponentTestBase {

	/**
	 * 
	 */
	@Override
	@Test
	public void testConstructValues() {
		// add a specialist type
		SpecialistType type = new SpecialistType("Fitter", "Menswear");
		type.setId("SO1");
		List<SpecialistType> specialistTypes = new ArrayList<>();
		specialistTypes.add(type);
		this.hospital.setSpecialistTypes(specialistTypes);

		SpecialistInputComponent component = createHospitalInputComponent(SpecialistInputComponent.class);
		component.constructValues(this.environment);

		Assert.assertEquals("{\n" + new NTuple(type.getId()) + "\n}", component.getSections().get(0).getValue());
	}
}
