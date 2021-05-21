package com.ibm.au.optim.suro.core.composer;

/**
 * A class that holds all the information of a single section of the input dat input file required by the cplex mod
 * file.
 *
 * @author brendanhaesler
 */
public class InputSection {

	/**
	 * The name of the section.
	 */
	private String name;

	/**
	 * The order this section should appear in relative to other sections.
	 */
	private int order;

	/**
	 * The value that should be written for this section.
	 */
	private String value;

	/**
	 * Constructs a new instance of the InputSection class.
	 * @param order The order this section should appear in relative to other sections.
	 * @param name The name of this section.
	 */
	public InputSection(int order, String name) {
		this.order = order;
		this.name = name;
		this.value = "";
	}

	/**
	 * Gets the section name.
	 * @return The section name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the section name.
	 * @param name The new section name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the order of this section.
	 * @return The order of this section.
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order of this section.
	 * @param order The new order.
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Gets the value of this section.
	 * @return The value of this section.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of this section.
	 * @param value The new value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns a String representation of the InputSection
	 * @return The String of the section in the format expected by cplex.
	 */
	public String toString() {
		return new StringBuilder(name)
						.append(" = ")
						.append(value)
						.append(";")
						.toString();
	}
}
