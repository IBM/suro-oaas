package com.ibm.au.optim.suro.model.entities.domain.ingestion;

/**
 * The Specialist Availability represents a specific specialist's availability on a given date.
 *
 * @author brendanhaesler
 */
public class SpecialistAvailabilityList extends TemporalList<SpecialistAvailability> {

	/**
	 * The default constructor
	 */
	public SpecialistAvailabilityList() { }

	/**
	 * Creates a new list of specialist availabilities with the given timestamp.
	 * @param timestamp - the timestamp of when the list was created / ingested
	 */
	public SpecialistAvailabilityList(long timestamp) {
		super(timestamp);
	}
}
