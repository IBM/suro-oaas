package com.ibm.au.optim.suro.model.store.domain.ingestion;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;
import com.ibm.au.optim.suro.model.store.Repository;

/**
 * Interface <b>TemporalRepository</b>. Base interface for all time-based repository instances. 
 * The temporal repository holds time-based data from the hospital in regards to patients and 
 * resources. Each repository offers a findByTime method to allow access to the latest valid
 * data set to use.
 *
 * @author brendanhaesler
 */
public interface TemporalRepository<R, T extends TemporalList<R>> extends Repository<T> {

	/**
	 * Retrieves an entry from the repository, that represents a collection of records of last 
	 * ingested items of type T.
	 * 
	 * @param timestamp 	a {@literal long} value representing the timestamp to search for, any 
	 * 						document that has a smaller or equal timestamp is a candidate.
	 * 
	 * @return 	an entry in the repository, which represents the collection of records that have
	 * 			been ingested 
	 */
	T findByTime(long timestamp);
}
