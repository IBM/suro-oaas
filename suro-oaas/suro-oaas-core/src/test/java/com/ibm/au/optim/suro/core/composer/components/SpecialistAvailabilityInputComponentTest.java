package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class SpecialistAvailabilityInputComponentTest extends InputComponentTestBase {

	/**
	 * 
	 */
	@Override
	@Test
	public void testConstructValues() {
		List<SpecialistAvailability> availabilities = new ArrayList<>();

		// create an availability inside the date range
		availabilities.add(new SpecialistAvailability("S01", TEST_TIME_FROM, 3));

		// and a couple outside the date range
		availabilities.add(new SpecialistAvailability("S02", TEST_TIME_FROM + 5 * TemporalInputComponent.MS_IN_DAY, 3));
		availabilities.add(new SpecialistAvailability("S02", TEST_TIME_FROM - 5 * TemporalInputComponent.MS_IN_DAY, 3));

		SpecialistAvailabilityList availabilityList = new SpecialistAvailabilityList(TEST_TIME_FROM);
		availabilityList.setRecords(availabilities);
		this.saRepo.addItem(availabilityList);

		SpecialistAvailabilityInputComponent component =
						createTemporalInputComponent(SpecialistAvailabilityInputComponent.class);
		component.constructValues(this.environment);

		Assert.assertEquals("{\n" + new NTuple("S01", 1, 3) + "\n}", component.getSections().get(0).getValue());
	}
}
