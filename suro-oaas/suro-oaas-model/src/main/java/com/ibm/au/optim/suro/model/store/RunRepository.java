/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store;

import com.ibm.au.optim.suro.model.entities.Run;

import java.io.InputStream;
import java.util.List;

/**
 * Interface <b>RunRepository</b>. This interface extends {@link Repository} and specialises
 * it for accassing and managing a store of {@link Run} instances. The base contract is then
 * extended wit two additional methods:
 * <ul>
 * <li>
 * {@link RunRepository#attach(Run,String,String,InputStream)}: attaches a specific file whose
 * content is defined by the given input stream to the execution of the strategy.
 * </li>
 * <li>
 * {@link RunRepository#getLast()}: gets the last execution that has been added to the repository.
 * </li>
 * </ul>
 * 
 * @author Christian Vecchiola
 *
 */
public interface RunRepository extends Repository<Run> {

	/**
	 * A {@link String} constant that contains the name of the attribute that will contain
	 * the instance of the {@link RunRepository} implementation that has been injected into
	 * the environment.
	 */
	String RUN_REPOSITORY_INSTANCE = "repo:run:instance";
	
	
	/**
	 * A {@link String} constant that contains the name of the parameter that will contain
	 * the name of the type of {@link RunRepository} that will be used in the application.
	 */
	String RUN_REPOSITORY_TYPE = RunRepository.class.getName();

	/**
	 * Attaches a file document to the given strategy execution. 
	 * 
	 * 
	 * @param run			a {@link Run} instance representing the execution of an optimization strategy.
	 * 						It cannot be {@literal null}.
	 * @param fileName		a {@link String} representing the name of the file document. It cannot be {@literal
	 * 						null}.
	 * @param contentType	a {@link String} representing the MIME type of the file.
	 * @param data			a {@link InputStream} representing the source of the content of the file. It
	 * 						cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>run</i>, <i>fileName</i>, or <i>data</i> is {@literal null}.
	 */
	void attach(Run run, String fileName, String contentType, InputStream data);
	
	/**
	 * Gets the last execution of (any of) the strategy added to the repository.
	 * 
	 * @return	a {@link Run} instance containing the information about the last strategy execution.
	 */
	Run getLast();
	
	/**
	 * Gets the attachment document that is specified by the given <i>fileName</i>.
	 * 
	 * @param id		a {@link String} instance representing the identifier execution of an optimization 
	 * 					strategy that owns the attachment file. It cannot be {@literal null}.
	 * 
	 * @param fileName	a {@link String} representing the name of the attachment document to retrieve. It
	 * 					cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>run</i> or <i>fileName</i> is {@literal null}.
	 *
	 */
	InputStream getAttachment(String id, String fileName);


	/**
	 * Returns the run for a specific job ID.
	 * @param jobId - the job ID referencing the job in the optimisation execution environment
	 * @return - a run or null, if the job could not be found
	 */
	Run findByJobId(String jobId);

	/**
	 * Returns all runs that belong to this strategy.
	 * @param strategyId - the id of the strategy
	 * @return - a list of runs
	 */
	List<Run> findByTemplateId(String templateId);
}
