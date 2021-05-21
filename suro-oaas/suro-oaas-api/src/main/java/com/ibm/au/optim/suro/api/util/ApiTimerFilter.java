/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.api.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple servlet filter that logs the processing time of each HTTP request
 */
public class ApiTimerFilter implements javax.servlet.Filter {
	private static final Logger logger = LoggerFactory
			.getLogger(ApiTimerFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {

		long startTime = System.currentTimeMillis();
		filter.doFilter(request, response);
		long requestTime = System.currentTimeMillis() - startTime;

		String method = "";
		if (request instanceof HttpServletRequest) {
			method = ((HttpServletRequest) request).getMethod();
		}

		logger.info("[" + method + " " + getRequestUrl(request)
				+ "] Request time: " + requestTime + "ms");
	}

	private static String getRequestUrl(ServletRequest request) {
		if (request instanceof HttpServletRequest) {
			return ((HttpServletRequest) request).getRequestURI().toString();
		}
		return "";
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
