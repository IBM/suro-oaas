package com.ibm.au.optim.suro.model.entities.domain.ingestion;


/**
 * The base plan represents the cyclical schedule that the hospital uses to plan surgical sessions. This is a list of
 * {@link BasePlanEntry} which is stored as the "baseplan" in the repsository.
 *
 * @author Brendan Haesler
 */
public class BasePlanList extends TemporalList<BasePlanEntry> {

	/**
	 * Default constructor
	 */
	public BasePlanList() { }

	/**
	 * Creates a new list with the specified timestamp.
	 * @param timeStamp - the timestamp when the list was created / ingested.
	 */
	public BasePlanList(long timeStamp) {
		super(timeStamp);
	}
}
