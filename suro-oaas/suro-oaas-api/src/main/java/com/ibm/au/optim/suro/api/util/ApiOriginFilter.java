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
import javax.servlet.http.HttpServletResponse;

/**
 * Class <b>ApiOriginFilter</b>. This class implement
 * {@link javax.servlet.Filter} and adds to each response that has been
 * generated by the REST API some additional headers that allow to control the
 * access from other servers.
 *
 */
public class ApiOriginFilter implements javax.servlet.Filter {

	/**
	 * Filters the given request. This method injects the following headers into
	 * the response:
	 * <ul>
	 * <li>Access-Control-Allow-Origin: *</li>
	 * <li>Access-Control-Allow-Methods: GET, POST, DELETE, PUT</li>
	 * <li>Access-Control-Allow-Headers: Content-Type</li>
	 * </ul>
	 * 
	 * @param request
	 *            the {@link ServletRequest} implementation that represents the
	 *            client request.
	 * @param response
	 *            the {@link ServletResponse} implementation that represents the
	 *            response being prepared by the processing chain in which the
	 *            filter operates.
	 * @param chain
	 *            a {@link FilterChain} implementation that represents the
	 *            remanining part of the processing chain to execute.s
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		res.addHeader("Access-Control-Allow-Headers", "Content-Type");
		chain.doFilter(request, response);
	}

	/**
	 * Finalises the filter. The method has an empty implementation.
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Initialises the filter. The method has an empty implementation.
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}