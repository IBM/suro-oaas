package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The ICU availability specifies which patient will leave the ICU bed at which day. Each patient currently occupying an
 * ICU bed, will have a record in here with the estimated day they get out of the ICU bed
 *
 * @author brendanhaesler
 */
public class IcuAvailability {

	/**
	 * The patient's unique id
	 */
	@JsonProperty("patientId")
	private String patientId;

	/**
	 * The estimated date the patient will leave.
	 */
	@JsonProperty("estimatedLeaveDate")
	private long estimatedLeaveDate;


	public IcuAvailability() { }

	/**
	 * Constructs a new instance of the IcuAvailability class with all fields populated.
	 * @param patientId          - the patient's id.
	 * @param estimatedLeaveDate - the estimated leave date.
	 */
	public IcuAvailability(String patientId, long estimatedLeaveDate) {
		this.patientId = patientId;
		this.estimatedLeaveDate = estimatedLeaveDate;
	}

	/**
	 * Gets the patient's id.
	 * @return A unique patient id.
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * Gets the date the patient is expected to leave the icu bed.
	 * @return The patient's estimated leaving date.
	 */
	public long getEstimatedLeaveDate() {
		return estimatedLeaveDate;
	}

	/**
	 * Sets the patient's id.
	 * @param patientId - the patient's id.
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * Sets the date the patient is expected to leave the icu bed.
	 * @param estimatedLeaveDate - the patient's estimated leaving date.
	 */
	public void setEstimatedLeaveDate(long estimatedLeaveDate) {
		this.estimatedLeaveDate = estimatedLeaveDate;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof IcuAvailability)) {
			return false;
		}

		IcuAvailability rhs = (IcuAvailability) other;
		return new EqualsBuilder()
						.append(patientId, rhs.patientId)
						.append(estimatedLeaveDate, rhs.estimatedLeaveDate)
						.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 31)
						.append(patientId)
						.append(estimatedLeaveDate)
						.toHashCode();
	}
}
