package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Ward Availability interface represents patient's occupying beds in a specific ward
 * and the expected date they will leave.
 *
 * @author brendanhaesler
 */
public class WardAvailability {

	/** The unique ward id. */
	@JsonProperty("wardId")
	private String wardId;

	/** The unique patient id */
	@JsonProperty("patientId")
	private String patientId;

	/** The estimated date that the patient will leave the ward */
	@JsonProperty("estimatedLeaveDate")
	private long estimatedLeaveDate;

	/**
	 * The default constructor
	 */
	public WardAvailability() {	}

	/**
	 * Creates a new item defining when which patient leaves from which ward.
	 * @param wardId - the id of the ward the patient is in
	 * @param patientId - the id of the patient
	 * @param estimatedLeaveDate - the timestamp for the estimated leave date.
	 */
	public WardAvailability(String wardId, String patientId, long estimatedLeaveDate) {
		this.wardId = wardId;
		this.patientId = patientId;
		this.estimatedLeaveDate = estimatedLeaveDate;
	}

	/**
	 * Gets the unique ward id.
	 * @return The unique ward id.
	 */
	public String getWardId() {
		return wardId;
	}

	/**
	 * Gets the unique patient id of the patient in the ward bed.
	 * @return The unique patient id.
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * Gets the estimated date that the patient will leave the ward.
	 * @return The estimated date that the patient will leave the ward.
	 */
	public long getEstimatedLeaveDate() {
		return estimatedLeaveDate;
	}

	/**
	 * Sets the unique wward id.
	 * @param wardId - The unique ward id.
	 */
	public void setWardId(String wardId) {
		this.wardId = wardId;
	}

	/**
	 * Sets the unique patient id.
	 * @param patientId - The unique patient id.
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * Sets the estimated date that the patient will leave the ward.
	 * @param estimatedLeaveDate - The estimated date that the patient will leave the ward.
	 */
	public void setEstimatedLeaveDate(long estimatedLeaveDate) {
		this.estimatedLeaveDate = estimatedLeaveDate;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof WardAvailability)) {
			return false;
		}

		WardAvailability rhs = (WardAvailability) other;
		return new EqualsBuilder()
						.append(estimatedLeaveDate, rhs.estimatedLeaveDate)
						.append(wardId, rhs.wardId)
						.append(patientId, rhs.patientId)
						.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(15, 31)
						.append(estimatedLeaveDate)
						.append(wardId)
						.append(patientId)
						.toHashCode();
	}
}
