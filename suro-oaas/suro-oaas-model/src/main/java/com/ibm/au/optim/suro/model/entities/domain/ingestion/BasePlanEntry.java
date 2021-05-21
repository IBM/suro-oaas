package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * A base plan entry specifies how many sessions are processed by a specific specialist type on a specific day. The item
 * represents what the hospital planned before running any optimisation.
 *
 * @author Brendan Haesler
 */
public class BasePlanEntry {

	/**
	 * The specialist id this base plan applies to.
	 */
	@JsonProperty("specialistTypeId")
	private String specialistTypeId;

	/**
	 * The day of the 4 week base plan cycle this base plan applies to.
	 */
	@JsonProperty("day")
	private int day;

	/**
	 * The number of sessions planned for this base plan.
	 */
	@JsonProperty("numSessions")
	private int numSessions;

	public BasePlanEntry() { }

	/**
	 * Constructs a new instance of the TransientBasePlan class with all fields populated.
	 *
	 * @param specialistTypeId - the specialist id.
	 * @param day          - the day of the 4 week cycle.
	 * @param numSessions  - the number of sessions scheduled.
	 */
	public BasePlanEntry(String specialistTypeId, int day, int numSessions) {
		this.specialistTypeId = specialistTypeId;
		this.day = day;
		this.numSessions = numSessions;
	}

	/**
	 * Gets the specialist id for this base plan.
	 * @return The specialist id.
	 */
	public String getSpecialistTypeId() {
		return specialistTypeId;
	}

	/**
	 * Gets the day of the 4 week cycle this base plan applies to.
	 * @return The day of the base plan cycle.
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Gets the number of sessions that are planned for this base plan.
	 * @return The number of sessions planned.
	 */
	public int getNumSessions() {
		return numSessions;
	}

	/**
	 * Sets the specialist id this base plan applies to.
	 * @param specialistTypeId - the specialist id.
	 */
	public void setSpecialistTypeId(String specialistTypeId) {
		this.specialistTypeId = specialistTypeId;
	}

	/**
	 * Sets the day of the 4 week cycle this base plan applies to.
	 * @param day - the day of the 4 week cycle.
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * Sets the number of sessions planned for this base plan.
	 * @param numSessions - the number of sessions planned.
	 */
	public void setNumSessions(int numSessions) {
		this.numSessions = numSessions;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || !(other instanceof BasePlanEntry)) {
			return false;
		}

		BasePlanEntry rhs = (BasePlanEntry) other;
		return new EqualsBuilder()
						.append(specialistTypeId, rhs.specialistTypeId)
						.append(day, rhs.day)
						.append(numSessions, rhs.numSessions)
						.isEquals();
	}

	@Override
	public int hashCode() {
		int result = specialistTypeId != null ? specialistTypeId.hashCode() : 0;
		result = 31 * result + day;
		result = 31 * result + numSessions;
		return result;
	}
}
