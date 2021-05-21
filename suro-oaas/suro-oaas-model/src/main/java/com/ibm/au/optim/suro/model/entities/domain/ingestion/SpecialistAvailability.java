package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Record to store how many specialists of a specific type are available at a specific day.
 *
 * @author brendanhaesler
 */
public class SpecialistAvailability {

	/**
	 * The specialist's type.
	 */
	@JsonProperty("specialistTypeId")
	private String specialistTypeId;

	/**
	 * The date to which this availability applies.
	 */
	@JsonProperty("date")
	private long date;

	/**
	 * A value indicating whether or not the specialist is numAvailable.
	 */
	@JsonProperty("numAvailable")
	private int numAvailable;

	/**
	 * Constructs a new instance of the SpecialistAvailabilityList class.
	 */
	public SpecialistAvailability() {	}

	/**
	 * Constructs a new instance of the SpecialistAvailabilityList class.
	 * @param specialistTypeId - the specialist's speciality type.
	 * @param date           - the date for this availability.
	 * @param numAvailable      - a value indicating whether the specialist is numAvailable.
	 */
	public SpecialistAvailability(String specialistTypeId, long date, int numAvailable) {
		this.specialistTypeId = specialistTypeId;
		this.date = date;
		this.numAvailable = numAvailable;
	}

	/**
	 * Gets the specialists's type.
	 * @return A specialist type.
	 */
	public String getSpecialistTypeId() {
		return specialistTypeId;
	}

	/**
	 * Gets the date for which this availability applies.
	 * @return A date.
	 */
	public long getDate() {
		return date;
	}

	/**
	 * Indicates whether the specialist is numAvailable or not.
	 * @return A value indicating whether the specialist is numAvailable or not.
	 */
	public int getNumAvailable() {
		return numAvailable;
	}

	/**
	 * Sets the specialist's type.
	 * @param specialistTypeId - the specialist's type.
	 */
	public void setSpecialistTypeId(String specialistTypeId) {
		this.specialistTypeId = specialistTypeId;
	}

	/**
	 * Sets the date for which this availability applies.
	 * @param date - a date.
	 */
	public void setDate(long date) {
		this.date = date;
	}

	/**
	 * Sets a value indicating whether or not the specialist is numAvailable.
	 * @param numAvailable - a value indicating whether or not the specialist is numAvailable.
	 */
	public void setNumAvailable(int numAvailable) {
		this.numAvailable = numAvailable;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof SpecialistAvailability)) {
			return false;
		}

		SpecialistAvailability rhs = (SpecialistAvailability) other;

		return new EqualsBuilder()
						.append(date, rhs.date)
						.append(specialistTypeId, rhs.specialistTypeId)
						.append(numAvailable, rhs.numAvailable)
						.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
						.append(date)
						.append(specialistTypeId)
						.append(numAvailable)
						.toHashCode();
	}
}
