package com.ibm.au.optim.suro.model.entities.domain.ingestion;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class with static methods offering date calculations and potentially other helper functions that can be used
 * in the data ingestion.
 *
 * @author brendanhaesler
 */
public abstract class IngestionUtility {

	/**
	 * Subtracts the milliseconds that are already passed since midnight of the provided day and returns the midnight
	 * timestamp of the same day.
	 *
	 * @param date - an arbitrary timestamp
	 * @return - the timestamp of the same day as the provided timestamp, but at 12:00 am.
	 */
	protected static long getDayStart(long date) {
		// create the calendar instance and initialise it
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(date));

		// extract info
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_YEAR);

		// reset calendar
		cal.clear();
		// set year and day-of-year
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_YEAR, day);

		// return timestamp
		return cal.getTimeInMillis();
	}
}
