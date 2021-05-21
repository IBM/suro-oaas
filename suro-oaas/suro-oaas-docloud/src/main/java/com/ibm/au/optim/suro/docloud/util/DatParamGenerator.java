/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.docloud.util;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates CPLEX .dat tuples for the SURO web application model
 *
 * TODO: rework class to use tuple definitions and a better way of passing in parameters.
 */
public class DatParamGenerator {
	private static final Logger logger = LoggerFactory
			.getLogger(DatParamGenerator.class);

	private Double inputWeightPoints;
	private Double inputWeightOverdue;
	private Double inputWeightOntime;
	private Double inputWeightCat1;
	private Double inputWeightDuration;
	private Double inputWeightBed;
	private Double inputWeightImbalance;

	private Integer inputExtraNormalBeds;
	private Integer inputExtraIcuBeds;
	private Integer inputExtraSurgerySessions;
	private Integer inputExtraChangesAllowed;

	/**
	 * Creates a new generator instance and provides it with the passed parameter map.
	 * @param params - a map of parameters as they are extracted from the frontend.
	 */
	public DatParamGenerator(Map<String, String[]> params) {
		inputWeightPoints = getValue(params, "inputWeightPoints", 10.0);
		inputWeightOverdue = getValue(params, "inputWeightOverdue", 10.0);
		inputWeightOntime = getValue(params, "inputWeightOntime", 1.0);
		inputWeightCat1 = getValue(params, "inputWeightCat1", 0.0);
		inputWeightDuration = getValue(params, "inputWeightDuration", 0.0);
		inputWeightBed = getValue(params, "inputWeightBed", 0.0);
		inputWeightImbalance = getValue(params, "inputWeightImbalance", 0.0);

		inputExtraNormalBeds = getValue(params, "inputExtraNormalBeds", 0.0)
				.intValue();
		inputExtraIcuBeds = getValue(params, "inputExtraIcuBeds", 0.0)
				.intValue();
		inputExtraSurgerySessions = getValue(params,
				"inputExtraSurgerySessions", 0.0).intValue();
		inputExtraChangesAllowed = getValue(params, "inputExtraChangesAllowed",
				12.0).intValue();
	}

	/**
	 * Extracts the double value from a parameter value in the passed map.
	 * @param params - the entire map with all parameters
	 * @param id - the name of the key to extract
	 * @param def - the default value that is returned, if the value contained in the map cannot be parsed or doesn't
	 *            exist.
	 * @return - the double value of the parameter
	 */
	public Double getValue(Map<String, String[]> params, String id, double def) {
		String[] vals = params.get(id);
		if (vals != null) {
			try {
				return Double.parseDouble(vals[0]);
			} catch (NumberFormatException nfe) {
				// ignore
			}
		}
		return def;
	}

	/**
	 * Generates tuple definitions that are appended to the pre-created dat file containing hospital data and time-based
	 * data.
	 * @return - an input stream representing the parameters in the form of tuples.
	 */
	public InputStream generate() {
		StringBuilder s = new StringBuilder();
		s.append("\n");
		s.append("weights = <").append(inputWeightPoints).append(", ")
				.append(inputWeightOverdue).append(", ")
				.append(inputWeightOntime).append(", ").append(inputWeightCat1)
				.append(", ").append(inputWeightDuration).append(", ")
				.append(inputWeightBed).append(", ")
				.append(inputWeightImbalance).append(">;");
		s.append("\n");
		s.append("extraResources = <").append(inputExtraNormalBeds)
				.append(", ").append(inputExtraIcuBeds).append(", ")
				.append(inputExtraSurgerySessions).append(", ")
				.append(inputExtraChangesAllowed).append(">;");
		s.append("\n");

		logger.debug("DAT Parameters: " + s.toString().replace("\n", " | "));

		return IOUtils.toInputStream(s);
	}
}
