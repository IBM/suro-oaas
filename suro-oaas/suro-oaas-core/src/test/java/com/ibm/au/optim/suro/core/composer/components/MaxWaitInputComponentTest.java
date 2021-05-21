package com.ibm.au.optim.suro.core.composer.components;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;

/**
 * @author brendanhaesler
 */
public class MaxWaitInputComponentTest extends InputComponentTestBase {

	/**
	 * 
	 */
	@Override
	@Test
	public void testConstructValues() {
		// create some urgency categories
		List<UrgencyCategory> urgencyCategories = new ArrayList<>();
		urgencyCategories.add(new UrgencyCategory(null, "CAT-1", 30, 1, 1));
		urgencyCategories.add(new UrgencyCategory(null, "CAT-3", 365, 0, 3));
		urgencyCategories.add(new UrgencyCategory(null, "CAT-2", 90, 0, 3));
		hospital.setUrgencyCategories(urgencyCategories);

		MaxWaitInputComponent component = createHospitalInputComponent(MaxWaitInputComponent.class);
		component.constructValues(environment);

		Assert.assertEquals("{\n[30 90 365]\n}", component.getSections().get(0).getValue());
	}
}
