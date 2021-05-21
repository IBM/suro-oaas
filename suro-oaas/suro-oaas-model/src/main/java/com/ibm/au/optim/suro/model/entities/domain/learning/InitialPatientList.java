package com.ibm.au.optim.suro.model.entities.domain.learning;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class InitialPatientList extends TemporalList<InitialPatient> {
	public InitialPatientList() { }

	public InitialPatientList(long timeStamp) {
		super(timeStamp);
	}
}
