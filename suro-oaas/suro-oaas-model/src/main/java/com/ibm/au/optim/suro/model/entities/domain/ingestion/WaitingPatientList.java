package com.ibm.au.optim.suro.model.entities.domain.ingestion;

/**
 * A store object which serves as a collection for {@link WaitingPatient} records.
 *
 * @author brendanhaesler
 */
public class WaitingPatientList extends TemporalList<WaitingPatient> {

	/**
	 * The default constructor
	 */
	public WaitingPatientList() { }

	/**
	 * Creates a new collection of waiting patient records with the provided timestamp
	 * @param timestamp - the timestamp of when the list was created / ingested.
	 */
	public WaitingPatientList(long timestamp) {
		super(timestamp);
	}
}
