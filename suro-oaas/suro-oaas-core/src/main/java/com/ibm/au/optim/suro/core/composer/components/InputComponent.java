package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.InputComponentSpecification;
import com.ibm.au.optim.suro.core.composer.InputSection;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A base class defining the basic logic of an Input Component, without any specific logic of how to generate the
 * required values of input sections it defines.
 *
 * @author brendanhaesler
 */
public abstract class InputComponent {

	/**
	 * A list of {@link InputSection}s this component defines.
	 */
	private List<InputSection> sections;

	/**
	 * Metadata required by this InputComponent
	 */
	private Map<String, String> metadata;

	/**
	 * Constructs a default instance of the InputComponent class.
	 */
	public InputComponent() {
		sections = new ArrayList<>();
	}

	/**
	 * Constructs the values of each input section this component defines.
	 * @param environment The environment holding information required to construct values.
	 */
	public abstract void constructValues(Environment environment);

	/**
	 * Reads the specification to create InputSections and metadata.
	 * @param specification The specification holding information about this component.
	 */
	public void readSpecification(InputComponentSpecification specification) {
		sections = new ArrayList<>();

		for (Map.Entry<Integer, String> entry : specification.getSections().entrySet()) {
			sections.add(new InputSection(entry.getKey(), entry.getValue()));
		}

		metadata = specification.getMetadata();
	}

	/**
	 * Sets the value of a specific InputSection this component defines.
	 * @param index The index of the InputSection to be modified.
	 * @param value The value to set to the InputSection.
	 * @return
	 */
	public boolean setSectionValue(int index, String value) {
		if (index < 0 || index >= sections.size()) {
			return false;
		}

		sections.get(index).setValue(value);
		return true;
	}

	/**
	 * Gets the InputSections.
	 * @return A {@link List} of {@link InputSection} objects.
	 */
	public List<InputSection> getSections() {
		return sections;
	}

	/**
	 * Sets the InputSections.
	 * @param sections The new InputSections.
	 */
	public void setSections(List<InputSection> sections) {
		this.sections = sections;
	}

	/**
	 * Gets the metadata of this component.
	 * @return A {@link Map} of MetadataName -> MetadataValue.
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * Sets the metadata of this component.
	 * @param metadata The new matadata.
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
}