package com.ibm.au.optim.suro.model.store.domain.ingestion.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.couch.CouchDbWardAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WardAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

/**
 * Class <b>CouchDbWardAvailabilitiesRepository</b>. CouchDB specific implementation for the
 * {@link WardAvailabilitiesRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link WardAvailabilityList} entities, 
 * via the corresponding {@link CouchDbWardAvailabilityList} documents.
 *
 * @author Brendan Haesler
 */

public class CouchDbWardAvailabilitiesRepository 
	
	extends AbstractCouchDbRepository<CouchDbWardAvailabilityList, WardAvailabilityList> 
	implements WardAvailabilitiesRepository {

	
	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";
	
	
	/**
	 * Initialises a new instance of the {@link CouchDbWardAvailabilitiesRepository} type.
	 */
	public CouchDbWardAvailabilitiesRepository() {
		super(CouchDbWardAvailabilityList.class);
	}


	/**
	 * Retrieves the last collection of ward availabilities before the given <i>timestamp</i>.
	 * 
	 * @param timestamp	a {@literal long} value indicating the upper bound for the timestamp.
	 * 
	 * @return 	a {@link WardAvailabilityList} instance or {@literal null} if there is no entity
	 * 			satisfying the range query.
	 */
	@Override
	public WardAvailabilityList findByTime(long timestamp) {
		List<CouchDbWardAvailabilityList> wardAvailabilities = this.proxy.getViewWithRange(CouchDbWardAvailabilitiesRepository.VIEW_BY_TIME, 0, timestamp);

		if (wardAvailabilities == null || wardAvailabilities.isEmpty()) {
			return null;
		}

		return wardAvailabilities.get(wardAvailabilities.size() - 1).getContent();
	}
}
