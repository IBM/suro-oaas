/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store;

import com.ibm.au.optim.suro.model.entities.RunDetails;

/**
 * Interface <b>RunDetailsRepository</b>.  This interface extends {@link Repository} and
 * specialises it for {@link RunDetails} instances. The base contract is extended to add
 * one more method that is {@link RunDetailsRepository#findByRunId(String)} which enables
 * the search of optimization result instances by specifying the template identifier that 
 * they belong to.
 * 
 * 
 * @author Christian Vecchiola.
 *
 */
public interface RunDetailsRepository extends Repository<RunDetails> {

	/**
	 * A {@link String} constant that contains the name of the attribute that will contain
	 * the instance of the {@link RunDetailsRepository} implementation that has been 
	 * injected into the environment.
	 */
	String DETAILS_REPOSITORY_INSTANCE = "repo:details:instance";
	
	
	/**
	 * A {@link String} constant that contains the name of the parameter that will contain
	 * the name of the type of {@link RunDetailsRepository} that will be used in the 
	 * application.
	 */
	String DETAILS_REPOSITORY_TYPE = RunDetailsRepository.class.getName();


	/**
	 * Retrieves the optimization result that belongs to the specific execution of a strategy
	 * identified by <i>runId</i>
	 * 
	 * @param runId	a {@link String} representing the unique identifier of the execution of
	 * 				an optimization strategy for which we're interestd in collecting the
	 * 				results. It cannot be {@literal null}.
	 * 
	 * @return	an instance of {@link RunDetails} that defines the result for the
	 * 			run identified by <i>runId</i>.
	 * 
	 * @throws IllegalArgumentException	if <i>runId</i> is {@literal null}.
	 */
	RunDetails findByRunId(String runId);
}
