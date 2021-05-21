/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.util;

/**
 * Simple string library
 *
 */
public class StringUtils {
	public static final String EMPTY_STRING = "";


	/**
	 * Returns whether the specified string is null or empty (zero length)
	 * 
	 * @param s String to check
	 * @return True if the object is null or contains zero characters, otherwise false
	 */
	public static boolean isNullOrEmpty(String s) {
		return (s == null || EMPTY_STRING.equals(s));
	}

	/**
	 * Safely parse the substring from the specified start and stop indices into
	 * a {@link Long} value. Returns null if the specified string range does not
	 * represent a valid long integer value
	 * 
	 * @param str
	 *            String to parse
	 * @param start
	 *            Substring start index
	 * @param stop
	 *            Substring end index
	 * @return Long value the string represents or null if invalid format
	 */
	public static Long safeParseLong(String str, int start, int stop) {
		try {
			return Long.valueOf(str.substring(start, stop).trim());
		} catch (NumberFormatException nfe) {
			// Ignore format exceptions
		}
		return null;
	}

	/**
	 * Safely parse the substring from the specified start and stop indices into
	 * a {@link Double} value. Returns null if the specified string range does
	 * not represent a valid double value
	 * 
	 * @param str
	 *            String to parse
	 * @param start
	 *            Substring start index
	 * @param stop
	 *            Substring end index
	 * @return Double value the string represents or null if invalid format
	 */
	public static Double safeParseDouble(String str, int start, int stop) {
		try {
			return Double.valueOf(str.substring(start, stop).trim());
		} catch (NumberFormatException nfe) {
			// Ignore format exceptions
		}
		return null;
	}
}
