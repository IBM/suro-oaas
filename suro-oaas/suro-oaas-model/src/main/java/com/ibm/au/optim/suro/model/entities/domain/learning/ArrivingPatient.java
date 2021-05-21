package com.ibm.au.optim.suro.model.entities.domain.learning;

import com.ibm.au.optim.suro.model.entities.Entity;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class ArrivingPatient extends Entity {

	private String surgeryClusterId;
	private long arrivalTime;
	private int numPatients;

	public ArrivingPatient() { }

	public ArrivingPatient(String surgeryClusterId, long arrivalTime, int numPatients) {
		this.surgeryClusterId = surgeryClusterId;
		this.arrivalTime = arrivalTime;
		this.numPatients = numPatients;
	}

	public String getSurgeryClusterId() {
		return surgeryClusterId;
	}

	public void setSurgeryClusterId(String surgeryClusterId) {
		this.surgeryClusterId = surgeryClusterId;
	}

	public long getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(long arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getNumPatients() {
		return numPatients;
	}

	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}
}
