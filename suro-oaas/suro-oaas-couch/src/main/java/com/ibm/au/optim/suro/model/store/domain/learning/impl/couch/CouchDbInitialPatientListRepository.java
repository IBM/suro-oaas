package com.ibm.au.optim.suro.model.store.domain.learning.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatientList;
import com.ibm.au.optim.suro.model.entities.domain.learning.couch.CouchDbInitialPatientList;
import com.ibm.au.optim.suro.model.store.domain.learning.InitialPatientListRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

/**
 * Class <b>CouchDbInitialPatientListRepository</b>. CouchDB specific implementation for the
 * {@link InitialPatientListRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link InitialPatientList} entities, 
 * via the corresponding {@link CouchDbInitialPatientList} documents.
 * 
 * @author Brendan Haesler
 */
public class CouchDbInitialPatientListRepository 
				extends AbstractCouchDbRepository<CouchDbInitialPatientList, InitialPatientList> 
				implements InitialPatientListRepository {
	

	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";
	
	/**
	 * Initialises an instance of {@link CouchDbInitialPatientListRepository}.
	 */
	public CouchDbInitialPatientListRepository() {
		super(CouchDbInitialPatientList.class);
	}

	/**
	 * Retrieves the collection of patients that are known by the given timestamp.
	 * The method queries the repository and retrieves all those patients that are
	 * known to be earlier than <i>timestamp</i>. Among the set of collections that
	 * are stored in the repository the latest is returned, which is the most up to
	 * date.
	 * 
	 * @param timestamp		a {@literal long} value indicating the timestamp for the
	 * 						time of interest.
	 * 
	 * @return	a {@link InitialPatientList} instance representing the most up to 
	 * 			date patient list prior to <i>timestamp</i>, or {@literal null} if
	 * 			there is no list before the selected time.
	 */
	@Override
	public InitialPatientList findByTime(long timestamp) {
		
		List<CouchDbInitialPatientList> initialPatients = this.proxy.getViewWithRange(CouchDbInitialPatientListRepository.VIEW_BY_TIME, 0, timestamp);

		if (initialPatients == null || initialPatients.isEmpty()) {
			return null;
		}

		return initialPatients.get(initialPatients.size() - 1).getContent();
	}
}
