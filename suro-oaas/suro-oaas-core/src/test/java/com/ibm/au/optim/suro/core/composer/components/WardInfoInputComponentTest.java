package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.InputSection;
import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.Ward;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class WardInfoInputComponentTest extends InputComponentTestBase {

	/**
	 * 
	 */
	@Override
	@Test
	public void testConstructValues() {
		// add a ward
		Ward ward = new Ward("Wardy McWard", 20);
		ward.setId("Agent Ward");
		List<Ward> wards = new ArrayList<>();
		wards.add(ward);
		this.hospital.setWards(wards);

		// add some availabilities in the date range
		List<WardAvailability> availabilities = new ArrayList<>();
		availabilities.add(new WardAvailability(ward.getId(), "P01", TEST_TIME_FROM + TemporalInputComponent.MS_IN_DAY));
		availabilities.add(new WardAvailability(ward.getId(), "P02", TEST_TIME_FROM + 2 * TemporalInputComponent.MS_IN_DAY));
		availabilities.add(new WardAvailability(ward.getId(), "P03", TEST_TIME_FROM + 3 * TemporalInputComponent.MS_IN_DAY));

		// add some availabilities outside the date range
		availabilities.add(new WardAvailability(ward.getId(), "P04", TEST_TIME_FROM));
		availabilities.add(new WardAvailability(ward.getId(), "P05", TEST_TIME_FROM - TemporalInputComponent.MS_IN_DAY));

		WardAvailabilityList list = new WardAvailabilityList(TEST_TIME_FROM - TemporalInputComponent.MS_IN_DAY);
		list.setRecords(availabilities);
		waRepo.addItem(list);

		WardInfoInputComponent component = createTemporalInputComponent(WardInfoInputComponent.class);
		component.getSections().add(new InputSection(1, "test2"));
		component.constructValues(this.environment);

		Assert.assertEquals("{\n" + new NTuple(ward.getId()) + "\n}", component.getSections().get(0).getValue());
		Assert.assertEquals("{\n" + new NTuple(ward.getId(), 1, 17) + "\n" +
						new NTuple(ward.getId(), 2, 18) + "\n" +
						new NTuple(ward.getId(), 3, 19) + "\n}",
						component.getSections().get(1).getValue());
	}
}
