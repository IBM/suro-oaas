package com.ibm.au.optim.suro.model.entities.domain.ingestion;

/**
 * A store object which serves as a collection for {@link WardAvailability} records.
 *
 * @author brendanhaesler
 */
public class WardAvailabilityList extends TemporalList<WardAvailability> {

	/**
	 * The default constructor
	 */
	public WardAvailabilityList() { }

	/**
	 * Creates a new collection of ward availability records with the provided timestamp
	 * @param timestamp - the timestamp of when the list was created / ingested.
	 */
	public WardAvailabilityList(long timestamp) {
		super(timestamp);
	}
}
