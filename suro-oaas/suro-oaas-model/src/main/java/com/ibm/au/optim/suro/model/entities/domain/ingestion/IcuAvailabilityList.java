package com.ibm.au.optim.suro.model.entities.domain.ingestion;

/**
 * The ICU Availability interface represents patients and their expected stays in ICU beds.
 *
 * @author brendanhaesler
 */
public class IcuAvailabilityList extends TemporalList<IcuAvailability> {

	/**
	 * Default constructor
	 */
	public IcuAvailabilityList() { }

	/**
	 * Creates a new list of ICU availabilities with the specified timestamp for the entire collection of records.
	 * @param timestamp - the time when the data was created / ingested
	 */
	public IcuAvailabilityList(long timestamp) {
		super(timestamp);
	}
}
