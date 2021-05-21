package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Waiting Patient interface represents patients on the waiting list for surgery.
 *
 * @author brendanhaesler
 */
public class WaitingPatient {

	/**
	 * The patient's unique id.
	 */
	@JsonProperty("patientId")
	private String patientId;

	/**
	 * The unique operation id. Note this is unique for every instance of an operation, not
	 * just for operation types.
	 */
	@JsonProperty("surgeryId")
	private String surgeryId;

	/**
	 * The name of the surgery to be performed.
	 */
	@JsonProperty("surgeryName")
	private String surgeryName;

	/**
	 * The date after which this patient will be overdue for surgery.
	 */
	@JsonProperty("dueDate")
	private long dueDate;

	/**
	 * The urgency category for this surgery.
	 */
	@JsonProperty("urgencyCategory")
	private int urgencyCategory;

	/**
	 * The default constructor
	 */
	public WaitingPatient() {	}

	/**
	 * Creates a new object holding information about which patients needs what type of surgery and when s/he's due.
	 * @param patientId - the id of the patient
	 * @param surgeryId - the id of the surgery
	 * @param surgeryName - the name of the surgery
	 * @param dueDate - the date when the patient is due
	 * @param urgencyCategory - the urgency category of this surgery type
	 */
	public WaitingPatient(String patientId, String surgeryId, String surgeryName, long dueDate, int urgencyCategory) {
		this.patientId = patientId;
		this.surgeryId = surgeryId;
		this.surgeryName = surgeryName;
		this.dueDate = dueDate;
		this.urgencyCategory = urgencyCategory;
	}

	/**
	 * Gets the patient's unique id.
	 * @return The unique id of the patient.
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * Gets the unique operation id of the planned operation. Note this is unique for every
	 * instance of an operation, not just for operation types.
	 * @return THe unique operation id.
	 */
	public String getSurgeryId() {
		return surgeryId;
	}

	/**
	 * Gets the name of the surgery to be performed.
	 * @return The name of the surgery to be performed.
	 */
	public String getSurgeryName() {
		return surgeryName;
	}

	/**
	 * Gets the date after which this patient will be overdue for surgery.
	 * @return The the date after which this patient will be overdue for surgery.
	 */
	public long getDueDate() {
		return dueDate;
	}

	/**
	 * Gets the urgency category of this surgery.
	 * @return The urgency category of this surgery.
	 */
	public int getUrgencyCategory() {
		return urgencyCategory;
	}

	/**
	 * Sets the patient's id.
	 * @param patientId - The patient's unique id.
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * Sets the operation id.
	 * @param surgeryId - The unique operation id.
	 */
	public void setSurgeryId(String surgeryId) {
		this.surgeryId = surgeryId;
	}

	/**
	 * Sets the name of the surgery.
	 * @param surgeryName - The name of the surgery to be performed.
	 */
	public void setSurgeryName(String surgeryName) {
		this.surgeryName = surgeryName;
	}

	/**
	 * Sets the date after which this patient will be overdue for surgery.
	 * @param dueDate - The date after which this patient will be overdue for surgery.
	 */
	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Sets the urgency category of the surgery.
	 * @param urgencyCategory - The urgency category of this surgery.
	 */
	public void setUrgencyCategory(int urgencyCategory) {
		this.urgencyCategory = urgencyCategory;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof WaitingPatient)) {
			return false;
		}
		
		WaitingPatient rhs = (WaitingPatient) other;
		return new EqualsBuilder()
						.append(surgeryId, rhs.surgeryId)
						.append(patientId, rhs.patientId)
						.append(surgeryName, rhs.surgeryName)
						.append(dueDate, rhs.dueDate)
						.append(urgencyCategory, rhs.urgencyCategory)
						.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 31)
						.append(surgeryId)
						.append(patientId)
						.append(surgeryName)
						.append(dueDate)
						.append(urgencyCategory)
						.toHashCode();
	}
}
