/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.docloud.util;

import com.ibm.au.optim.suro.docloud.Constants;
import com.ibm.au.optim.suro.model.entities.Run;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

/**
 * Ops file generator used for DOCloud to generate the operational parameters (optimality and maximum runtime).
 * On initialisation of the generator, the parameters are extracted from the provided map of parameters.
 * <p/>
 * Once initialised the {@link OpsGenerator#generate()} method can be used to generate the ops parameters in their
 * expected XML structure. The input stream can be passed directly to DOCloud as the ops parameter stream.
 *
 * @author Peter Ilfrich
 */
public class OpsGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpsGenerator.class);


    private static final String OPTIMALITY_PARAMETER = "epgap";
	private static final String TIME_LIMIT_PARAMETER = "tilim";

	private double optimality;
	private double maxRuntime;




    /**
     * Creates a new instance of the OpsGenerator. The operational parameters are extracted from the parameter map,
     * parsed and stored. After the generator is initialised, the {@link OpsGenerator#generate()} method can be used to
     * produce an input stream containing the ops parameters as XML.
     *
     * @param inputParams - the list of all the input parameters of the run, the required input parameters will be
     *                    extracted from this map. Fallbacks are in place.
     */
	public OpsGenerator(Run run) {
		optimality = run.getMinGap();
		maxRuntime = 60.0 * run.getMaxRunTime();
	}


    /**
     * Returns the double value of the provided parameter, which is extracted from the provided map of parameters. If
     * the parameter doesn't exist, the default value is returned.
     *
     * @param params - the parameters from which to extract the parameter with the specified id
     * @param id - the parameter to extract
     * @param defaultValue - the default value to return if the parameter doesn't exist.
     * @return - the double value of the parameter from the map or the default value.
     */
	protected Double getValue(Map<String, String[]> params, String id, double defaultValue) {
		String[] values = params.get(id);
		if (values != null && values.length > 0) {
			try {
				return Double.parseDouble(values[0]);
			} catch (NumberFormatException nfe) {
				// ignore
			}
		}
		return defaultValue;
	}


    /**
     * Generates the XML specifying the optimality boundary and the time limit and returns it as an input stream, so it
     * can be easily passed to the {@link com.ibm.optim.oaas.client.job.JobClient}
     * @return - an input stream representing the XML specifying the ops parameters.
     */
    public InputStream generate() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        sb.append("<settings version=\"2\"><category name=\"cplex\">").append("\n");

        sb.append(String.format("<setting name=\"%s\" value=\"%s\"/>", TIME_LIMIT_PARAMETER, String.valueOf(maxRuntime))).append("\n");
        sb.append(String.format("<setting name=\"%s\" value=\"%s\"/>", OPTIMALITY_PARAMETER, String.valueOf(optimality))).append("\n");

        sb.append("</category></settings>");

        LOGGER.debug("OPS Parameters: " + sb.toString().replace("\n", ""));

        return IOUtils.toInputStream(sb);
    }


}
