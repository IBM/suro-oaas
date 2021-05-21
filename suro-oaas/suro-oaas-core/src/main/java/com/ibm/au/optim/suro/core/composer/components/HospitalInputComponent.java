package com.ibm.au.optim.suro.core.composer.components;

/**
 * A sub class of {@link InputComponent} for components which need to access the
 * {@link com.ibm.au.optim.suro.model.control.domain.HospitalController}.
 *
 * @author brendanhaesler
 */
public abstract class HospitalInputComponent extends InputComponent {

	/**
	 * The ID of the hospital to access.
	 */
	private String hospitalId;

	/**
	 * Gets the hospital ID.
	 * @return The hospital ID.
	 */
	public String getHospitalId() {
		return hospitalId;
	}

	/**
	 * Sets the hospital ID.
	 * @param hospitalId The new hospital ID.
	 */
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
}
