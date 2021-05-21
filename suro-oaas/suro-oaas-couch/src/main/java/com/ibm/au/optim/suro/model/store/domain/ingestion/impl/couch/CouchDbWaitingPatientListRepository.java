package com.ibm.au.optim.suro.model.store.domain.ingestion.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.couch.CouchDbWaitingPatientList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WaitingPatientListRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

/**
 * Class <b>CouchDbWaitingPatientListRepository</b>. CouchDB specific implementation for the
 * {@link WaitingPatientListRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link WaitingPatientList} entities, 
 * via the corresponding {@link CouchDbWaitingPatientList} documents.
 *
 * @author Brendan Haesler
 */
public class CouchDbWaitingPatientListRepository
		extends AbstractCouchDbRepository<CouchDbWaitingPatientList, WaitingPatientList>
		implements WaitingPatientListRepository {

	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";
	
	/**
	 * Initialises a new instance of the {@link CouchDbWaitingPatientListRepository} type.
	 */
	public CouchDbWaitingPatientListRepository() {
		super(CouchDbWaitingPatientList.class);
	}

	/**
	 * Retrieves the last collection of waiting patient list before the given <i>timestamp</i>.
	 * 
	 * @param timestamp	a {@literal long} value indicating the upper bound for the timestamp.
	 * 
	 * @return 	a {@link WaitingPatientList} instance or {@literal null} if there is no entity
	 * 			satisfying the range query.
	 */
	@Override
	public WaitingPatientList findByTime(long timestamp) {
		List<CouchDbWaitingPatientList> basePlans = this.proxy.getViewWithRange(CouchDbWaitingPatientListRepository.VIEW_BY_TIME, 0, timestamp);

		if (basePlans == null || basePlans.isEmpty()) {
			return null;
		}

		return basePlans.get(basePlans.size() - 1).getContent();
	}
}
