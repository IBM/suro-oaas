package com.ibm.au.optim.suro.model.entities.domain.learning;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class InitialPatient {

	private String surgeryClusterId;
	private int daysRemaining;
	private int numPatients;

	public InitialPatient() { }

	public InitialPatient(String surgeryClusterId, int daysRemaining, int numPatients) {
		this.surgeryClusterId = surgeryClusterId;
		this.daysRemaining = daysRemaining;
		this.numPatients = numPatients;
	}

	public String getSurgeryClusterId() {
		return surgeryClusterId;
	}

	public void setSurgeryClusterId(String surgeryClusterId) {
		this.surgeryClusterId = surgeryClusterId;
	}

	public int getDaysRemaining() {
		return daysRemaining;
	}

	public void setDaysRemaining(int daysRemaining) {
		this.daysRemaining = daysRemaining;
	}

	public int getNumPatients() {
		return numPatients;
	}

	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}
}
