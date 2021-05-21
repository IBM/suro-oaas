/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store.impl.couch;


import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbTemplate;
import com.ibm.au.optim.suro.model.store.TemplateRepository;

import org.ektorp.support.GenerateView;

import java.util.ArrayList;
import java.util.List;


/**
 * Class <b>CouchDbTemplateRepository</b>. This is a {@link StrategyRepository} specific implementation
 * that provides means to store {@link Strategy} implementation on a <i>CouchDb</i> instance. The
 * repository uses {@link CouchDbTemplate} instances to leverage the support of the Ektorp library.
 *
 */
public class CouchDbTemplateRepository extends AbstractCouchDbRepository<CouchDbTemplate, Template> implements TemplateRepository {

	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the documents by the <i>modelId</i>
	 * attribute.
	 */
	public static final String VIEW_BY_MODEL = "by_modelId";
	
	
	/**
	 * Initializes an instance of {@link CouchDbTemplateRepository}. The constructor invokes the base
	 * class constructor {@link AbstractCouchDbRepository#AbstractCouchDbRepository(Class)} and passes
	 * {@link CouchDbTemplate} type information as argument.
	 */
	public CouchDbTemplateRepository() {
		super(CouchDbTemplate.class);
	}

	/**
	 * Retrieves all the templates that are defined for the {@link Model}
	 * identified by <i>modelId</i>.
	 * 
	 * @param modelId	a {@link String} representing the unique identifier
	 * 					of the model for which the template of interest have
	 * 					been prepared for.
	 * 
	 * @return	a {@link List} implementation containing the collection of
	 * 			{@link Template} instances that represent the templates that
	 * 			have been prepared for the model.
	 */
	@Override
	@GenerateView
	public List<Template> findByModelId(String modelId) {
		List<Template> output =  new ArrayList<>();

		for (CouchDbTemplate template : this.proxy.getView(CouchDbTemplateRepository.VIEW_BY_MODEL, modelId)) {
			output.add(this.getContent(template));
		}

		return output;
	}




}