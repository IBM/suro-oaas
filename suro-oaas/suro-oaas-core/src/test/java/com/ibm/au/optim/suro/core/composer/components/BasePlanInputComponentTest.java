package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class BasePlanInputComponentTest extends InputComponentTestBase {

	@Override
	@Test
	public void testConstructValues() {
		// add a base plan
		BasePlanList basePlanList = new BasePlanList(TEST_TIME_FROM - TemporalInputComponent.MS_IN_DAY);
		List<BasePlanEntry> basePlanEntries = new ArrayList<>();
		basePlanEntries.add(new BasePlanEntry("S01", 1, 3));
		basePlanList.setRecords(basePlanEntries);
		bpRepo.addItem(basePlanList);

		NTuple tuple = new NTuple(new NTuple("S01"), 1, 3);

		BasePlanInputComponent component = createTemporalInputComponent(BasePlanInputComponent.class);
		component.constructValues(environment);

		Assert.assertEquals("{\n" + tuple + "\n}", component.getSections().get(0).getValue());
	}
}
