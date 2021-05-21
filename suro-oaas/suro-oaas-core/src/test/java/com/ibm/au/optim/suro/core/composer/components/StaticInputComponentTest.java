package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.InputComponentSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class StaticInputComponentTest extends InputComponentTestBase {

	/**
	 * 
	 */
	@Override
	@Test
	public void testConstructValues() {
		StaticInputComponent component = new StaticInputComponent();
		InputComponentSpecification spec = new InputComponentSpecification();

		Map<Integer, String> sections = new TreeMap<>();
		sections.put(0, "section0");
		sections.put(1, "section1");
		spec.setSections(sections);

		Map<String, String> metadata = new HashMap<>();
		metadata.put("section0", "value 0");
		metadata.put("section1", "value 1");
		spec.setMetadata(metadata);

		component.readSpecification(spec);
		component.constructValues(null);

		Assert.assertEquals("value 0", component.getSections().get(0).getValue());
		Assert.assertEquals("value 1", component.getSections().get(1).getValue());
	}
}
