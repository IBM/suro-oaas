/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store;

import com.ibm.au.optim.suro.model.entities.Template;

import java.util.List;

/**
 * Interface <b>StrategyRepository</b>. This interface specialises {@link Repository} for 
 * the type {@link Template} and provides an interface to access strategies items that are
 * stored in the implementation of the repository. A strategy contains information about a 
 * specific approach to solving the optimisation problem and has a collection of depends run, 
 * which identify specific instances of the execution of the strategy.
 * 
 * @author Christian Vecchiola
 *
 */
public interface TemplateRepository extends Repository<Template> {

	/**
	 * A {@link String} constant that contains the name of the attribute that will contain
	 * the instance of the {@link TemplateRepository} implementation that has been injected
	 * into the environment.
	 */
	String TEMPLATE_REPOSITORY_INSTANCE = "repo:template:instance";


	/**
	 * A {@link String} constant that contains the name of the parameter that will contain
	 * the name of the type of {@link TemplateRepository} that will be used in the application.
	 */
	String TEMPLATE_REPOSITORY_TYPE = TemplateRepository.class.getName();

	/**
	 * Retrieves the list of strategies associated with the provided model
	 *
	 * @param modelId - the ID of the model
	 * @return - a list of strategies supported by the provided model
	 */
	List<Template> findByModelId(String modelId);

}
