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
public class InputComponentTest {
    /**
     * 
     */
    @Test
	public void testReadSpecification() {
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

		Assert.assertEquals(2, component.getSections().size());
		Assert.assertEquals(0, component.getSections().get(0).getOrder());
		Assert.assertEquals("section0", component.getSections().get(0).getName());
		Assert.assertEquals(1, component.getSections().get(1).getOrder());
		Assert.assertEquals("section1", component.getSections().get(1).getName());
		Assert.assertEquals(metadata, component.getMetadata());
	}
    /**
     * 
     */
    @Test
	public void testSetSectionValue() {
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
		component.setSectionValue(0, "Hello World!");

		Assert.assertEquals("Hello World!", component.getSections().get(0).getValue());
	}
}
