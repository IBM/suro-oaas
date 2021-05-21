package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.Entity;

import java.util.Date;
import java.util.List;

/**
 * Abstract super class of different types of lists stored in the repositories. This is used for time-based data, where
 * there is a main timestamp for the entire collection and a list of single records providing detailed information for
 * specific time intervals.
 *
 * @author brendanhaesler
 */
public abstract class TemporalList<T> extends Entity {

	@JsonProperty("timestamp")
	private long timestamp;

	@JsonProperty("records")
	private List<T> records;

	/**
	 * The default constructor. This will set the timestamp to the current date.
	 */
	public TemporalList() {
		timestamp = new Date().getTime();
	}

	/**
	 * Creates a new list with the specified timestamp.
	 * @param timestamp
	 */
	public TemporalList(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Retrieves the timestamp of this list.
	 * @return - the timestamp of this list
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets a new timestamp for this list
	 * @param timestamp - the new timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Retrieves the list of records contained in this list item.
	 * @return - the list of records.
	 */
	public List<T> getRecords() {
		return records;
	}

	/**
	 * Sets a new list of records for this collection
	 * @param records - the new list of records
	 */
	public void setRecords(List<T> records) {
		this.records = records;
	}
}
