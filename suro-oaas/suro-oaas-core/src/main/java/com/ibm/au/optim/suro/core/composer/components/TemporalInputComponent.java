package com.ibm.au.optim.suro.core.composer.components;

/**
 * A sub class of {@link InputComponent} for components which need to access the
 * {@link com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController}.
 *
 * @author brendanhaesler
 */
public abstract class TemporalInputComponent extends HospitalInputComponent {

	/**
	 * The number of milliseconds in a day. Used for timestamp calculations.
	 */
	public static final long MS_IN_DAY = 1000l * 60l * 60l * 24l;

	/**
	 * The start time of the schedule to be generated.
	 */
	private long timeFrom;

	/**
	 * The end time of the schedule to be generated.
	 */
	private long timeTo;

	/**
	 * Gets the start time of the schedule to be generated.
	 * @return The start time.
	 */
	public long getTimeFrom() {
		return timeFrom;
	}

	/**
	 * Sets the start time of the schedule to be generated.
	 * @param timeFrom The new start time.
	 */
	public void setTimeFrom(long timeFrom) {
		this.timeFrom = timeFrom;
	}

	/**
	 * Gets the end time of the schedule to be generated.
	 * @return The end time of the schedule to be generated.
	 */
	public long getTimeTo() {
		return timeTo;
	}

	/**
	 * Sets the end time of the schedule to be generated.
	 * @param timeTo The new end time.
	 */
	public void setTimeTo(long timeTo) {
		this.timeTo = timeTo;
	}
}
