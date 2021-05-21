/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store.impl.couch;

import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbRun;
import com.ibm.au.optim.suro.model.store.RunRepository;

import org.ektorp.AttachmentInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class <b>CouchDbRunRepository</b>. This is a {@link RunRepository} specific implementation
 * that provides means to store {@link Run} implementation on a <i>CouchDb</i> instance. The
 * repository uses {@link CouchDbRun} instances to leverage the support of the Ektorp library.
 *
 */
public class CouchDbRunRepository extends AbstractCouchDbRepository<CouchDbRun,Run> implements RunRepository {

	/**
	 * A {@link Logger} implementation that records all the log messages created by instances
	 * of this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbRunRepository.class);
	
	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by the job instance
	 * they refer to. This is done by using the {@link Run#getJobId()}
	 * attribute.
	 */
	public static final String VIEW_BY_JOB = "by_jobId";
	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the documents by the template instance
	 * they refer to. This is done by using the {@link Run#getTemplateId()}
	 * attribute.
	 */
	public static final String VIEW_BY_TEMPLATE = "by_templateId";

	
	/**
	 * Initializes an instance of {@link CouchDbRunRepository}. The constructor invokes the base 
	 * class constructor {@link AbstractCouchDbRepository#AbstractCouchDbRepository(Class)} and
	 * passes {@link CouchDbRun} type information as argument.
	 */
	public CouchDbRunRepository() {
		super(CouchDbRun.class);

	}


	/**
	 * Read and commits the input stream as an attachment to the specified run.
	 * 
	 * @param run			a {@link Run} instance representing the recipient for the
	 * 						attachment.
	 * @param fileName		a {@link String} representing the name that the attachment
	 * 						will be mapped to when associated to the run.
	 * @param contentType	a {@link String} representing the MIME type to associate
	 * 						to the attachment.
	 * @param data			a {@link InputStream} implementation that provides access
	 * 						to the binary content of the attachment.
	 */
	@Override
	public void attach(Run run, String fileName, String contentType, InputStream data) {
		
		CouchDbRun cRun = proxy.getItem(run.getId());
		
		AttachmentInputStream ais = new AttachmentInputStream(fileName, data, contentType);
		
		LOGGER.debug("[{}] >>> Attaching {}", run.getId(), fileName);
		
		long t1 = System.currentTimeMillis();
		cRun.setRevision(this.proxy.getDb().createAttachment(cRun.getId(), cRun.getRevision(), ais));
		long t2 = System.currentTimeMillis();

		run.setRevision(cRun.getRevision());
		
		LOGGER.debug("[{}] <<< Attaching [{} ms]", cRun.getId(), t2-t1);
	}
	

	/**
	 * Gets the attachment document that is specified by the given <i>fileName</i>.
	 * 
	 * @param id		a {@link String} instance representing the identifier execution of an optimization 
	 * 					strategy that owns the attachment file. It cannot be {@literal null}.
	 * 
	 * @param fileName	a {@link String} representing the name of the attachment document to retrieve. It
	 * 					cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>id</i> or <i>fileName</i> is {@literal null}.
	 *
	 */
	public InputStream getAttachment(String id, String fileName) {
		
		if (id == null) {
			
			throw new IllegalArgumentException("Parameter 'id' canont be null");
		}
		
		if (fileName == null) {

			throw new IllegalArgumentException("Parameter 'fileName' canont be null");
			
		}
		
		return this.proxy.getDb().getAttachment(id, fileName);
	}

	/**
	 * Retrieves the last run submitted. The method queries the repository for
	 * the collection of {@link Run} instances and retrieves the first of the
	 * list, which by ordering is the last.
	 * 
	 * @return 	a {@link Run} instance representing the last run submitted, or
	 * 			{@literal null} if there are no runs.
	 */
	public Run getLast() {
		
		// [CV] NOTE: I am not sure, this makes sense when we have multiple
		//            models and we assume that the sorting in the list we
		//            receive has an implication on time, besides, for multiple
		//            runs is very time consuming.
		
		Run run = null;
		List<Run> runs = this.getAll();
		
		if (runs.size() > 0) {
			
			run = runs.get(0);
		}
		
		return run;
	}

	/**
	 * This method retrieves the {@link Run} instance that is associated
	 * to the job identified by the given <i>jobId</i>. 
	 * 
	 * @param jobId		a {@link String} representing the unique identifier
	 * 					of the job, representing the execution of the run
	 * 					in the optimisation back-end.
	 * 
	 * @return 	a {@link Run} instance that is associated to the identified
	 * 			job. If not {@literal null}, this means that the value that
	 * 			is returned by {@link Run#getJobId()} when applied to the
	 * 			returned instance is equal to <i>jobId</i>.
	 * 
	 */
	@Override
	public Run findByJobId(String jobId) {

		Run run = null;
		List<CouchDbRun> documents = this.proxy.getView(CouchDbRunRepository.VIEW_BY_JOB, jobId);
		
		if ((documents != null) && (documents.size() > 0)) {
		
			for (CouchDbRun document : documents) {
				run = this.getContent(document);
				break;
			}
		}
		
		return run;
	}

	/**
	 * This method retrieves the {@link Run} instance that is belongs to the
	 * specified template.  
	 * 
	 * @param templateId	a {@link String} representing the unique identifier
	 * 						of the template, that was used as a starting point
	 * 						for creating the run.
	 * 
	 * @return 	a {@link Run} instance that is associated to the identified
	 * 			template. If not {@literal null}, this means that the value that
	 * 			is returned by {@link Run#getTemplateId()} when applied to the
	 * 			returned instance is equal to <i>templateId</i>.
	 * 
	 */
	@Override
	public List<Run> findByTemplateId(String templateId) {
		
		List<Run> runs = new ArrayList<>();
		List<CouchDbRun> documents = this.proxy.getView(CouchDbRunRepository.VIEW_BY_TEMPLATE, templateId);
		
		if ((documents != null) && (documents.size() > 0)) {
		
			for (CouchDbRun document : documents) {
				runs.add(this.getContent(document));
			}
		}
		return runs;
	}


}