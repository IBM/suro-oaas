package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.InputComposerTest;
import com.ibm.au.optim.suro.core.composer.InputSection;
import com.ibm.au.jaws.data.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author brendanhaesler
 */
public abstract class InputComponentTestBase extends InputComposerTest {


	/**
	 * 
	 */
	public abstract void testConstructValues();


	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected <T extends InputComponent> T createAbstractInputComponent(Class<T> clazz) {
		
		InputComponent component = ReflectionUtils.createInstance(clazz);
		List<InputSection> sections = new ArrayList<>();
		sections.add(new InputSection(0, TEST_SECTION_NAME));
		component.setSections(sections);
		return (T) component;
	}


	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected <T extends HospitalInputComponent> T createHospitalInputComponent(Class<T> clazz) {
		
		HospitalInputComponent component = createAbstractInputComponent(clazz);
		component.setHospitalId(this.hospital == null ? null : this.hospital.getId());
		return (T) component;
	}


	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected <T extends TemporalInputComponent> T createTemporalInputComponent(Class<T> clazz) {
		
		TemporalInputComponent component = createHospitalInputComponent(clazz);
		component.setTimeFrom(TEST_TIME_FROM);
		component.setTimeTo(TEST_TIME_TO);
		return (T) component;
	}
}
