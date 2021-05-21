/**
 * Copyright (C) 2015 IBM Corporation
 * All Rights Reserved
 */
package com.ibm.au.optim.suro.model.store.impl.couch;

import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbRunDetails;
import com.ibm.au.optim.suro.model.store.RunDetailsRepository;

import org.ektorp.support.GenerateView;

import java.util.List;

/**
 * Class <b>CouchDbRunDetailsRepository</b>. This is a {@link RunDetailsRepository} specific 
 * implementation that provides means to store {@link RunDetails} implementation on a <i>CouchDb</i>
 * instance. The repository uses {@link CouchDbRunDetails} instances to leverage the support of the 
 * Ektorp library.
 *
 */
public class CouchDbRunDetailsRepository extends AbstractCouchDbRepository<CouchDbRunDetails, RunDetails> implements RunDetailsRepository {

	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by the run instance
	 * they refer to. This is done by using the {@link RunDetails#getRunId()}
	 * attribute.
	 */
	public static final String BY_RUN = "by_runId";
	
	
	/**
	 * Initializes an instance of {@link CouchDbRunDetailsRepository}. 
	 */
	public CouchDbRunDetailsRepository() {

        super(CouchDbRunDetails.class);

	}

	/**
	 * Retrieves the run log details document that belong to the specified run. 
	 * 
	 * @param runId		a {@link String} representing the unique identifier of  
	 * 					the {@link Run} instance for which we need to retrieve 
	 * 					the log details.
	 * 						
	 * @return 	a {@link RunDetails} instance containing the run log details for the
	 * 			selected run, or {@literal null} if there is no such document in
	 * 			the repository.
	 */
	@Override
	@GenerateView
    public RunDetails findByRunId(String runId) {
		
		List<CouchDbRunDetails> results = this.proxy.getView(CouchDbRunDetailsRepository.BY_RUN, runId);
		
		RunDetails logDetails = null;
		
		if (results.size() > 0) {
			
			logDetails = this.getContent(results.get(0));
		}
		
		return logDetails;
	}

}