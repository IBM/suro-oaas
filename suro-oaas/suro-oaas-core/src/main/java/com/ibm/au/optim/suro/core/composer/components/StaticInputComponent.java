package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.InputSection;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;
import java.util.Map;

/**
 * An {@link InputComponent} that constructs static information provided by component metadata.
 *
 * @author brendanhaesler
 */
public class StaticInputComponent extends InputComponent {

	/**
	 * Retrieves information from the metadata and sets it to the
	 * {@link com.ibm.au.optim.suro.core.composer.InputSection}.
	 * @param environment The environment holding information required to construct values.
	 */
	@Override
	public void constructValues(Environment environment) {
		Map<String, String> metadata = getMetadata();
		List<InputSection> sections = getSections();

		for (int index = 0; index < sections.size(); ++index) {
			InputSection section = sections.get(index);
			setSectionValue(index, metadata.get(section.getName()));
		}
	}
}