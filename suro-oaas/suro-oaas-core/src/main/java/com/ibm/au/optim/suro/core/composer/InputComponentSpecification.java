package com.ibm.au.optim.suro.core.composer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A class containing the metadata required to generate an InputComponent.
 * @author brendanhaesler
 */
public class InputComponentSpecification {

	/**
	 * The class name of the component to be generated.
	 */
	@JsonProperty("componentClassName")
	private String componentClassName;

	/**
	 * The orders and names of sections to be written by the component.
	 */
	@JsonProperty("sections")
	private Map<Integer, String> sections;

	/**
	 * Additional information required by the component to generate its section.
	 */
	@JsonProperty("metadata")
	private Map<String, String> metadata;

	/**
	 * Gets the sections.
	 * @return A map of SectionOrder -> SectionName.
	 */
	public Map<Integer, String> getSections() {
		return sections;
	}

	/**
	 * Sets the sections.
	 * @param sections The new sections.
	 */
	public void setSections(Map<Integer, String> sections) {
		this.sections = sections;
	}

	/**
	 * Gets the class name of the component to be generated.
	 * @return The fully qualified class name.
	 */
	public String getComponentClassName() {
		return componentClassName;
	}

	/**
	 * Sets the class name of the component to be generated.
	 * @param componentClassName The new fully qualified class name.
	 */
	public void setComponentClassName(String componentClassName) {
		this.componentClassName = componentClassName;
	}

	/**
	 * Gets the metadata required by this component.
	 * @return A map of MetadataName -> MetadataContent.
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * Sets the metadata required by this component.
	 * @param metadata The new metadata.
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
}
