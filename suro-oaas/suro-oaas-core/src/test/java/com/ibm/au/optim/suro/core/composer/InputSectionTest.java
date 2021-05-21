package com.ibm.au.optim.suro.core.composer;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author brendanhaesler
 */
public class InputSectionTest  {


	/**
	 * 
	 */
	@Test
	public void testConstructor() {
		InputSection section = new InputSection(1, "Steve");

		Assert.assertEquals(1, section.getOrder());
		Assert.assertEquals("Steve", section.getName());
		Assert.assertEquals("", section.getValue());
	}


	/**
	 * 
	 */
	@Test
	public void testToString() {
		InputSection section = new InputSection(1, "Steve");
		section.setValue("{\n<\"some\" \"typical\" \"value\">\n}");

		Assert.assertEquals("Steve = {\n<\"some\" \"typical\" \"value\">\n};", section.toString());
	}
}
