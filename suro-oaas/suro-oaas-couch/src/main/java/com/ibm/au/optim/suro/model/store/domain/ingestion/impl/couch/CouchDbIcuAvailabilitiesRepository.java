package com.ibm.au.optim.suro.model.store.domain.ingestion.impl.couch;


import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.couch.CouchDbIcuAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.IcuAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

/**
 * Class <b>CouchDbIcuAvailabilitiesRepository</b>. CouchDB specific implementation for the
 * {@link IcuAvailabilitiesRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link IcuAvailabilityList} entities, 
 * via the corresponding {@link CouchDbIcuAvailabilityList} documents.
 *
 * @author Brendan Haesler
 */
public class CouchDbIcuAvailabilitiesRepository
	
		extends AbstractCouchDbRepository<CouchDbIcuAvailabilityList, IcuAvailabilityList>
		implements IcuAvailabilitiesRepository {
	
	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";

	/**
	 * Initialises an instance of {@link CouchDbIcuAvailabilitiesRepository}.
	 */
	public CouchDbIcuAvailabilitiesRepository() {
		super(CouchDbIcuAvailabilityList.class);
	}


	/**
	 * Retrieves the list of ICU availabilities that was entered into the repository
	 * at the closest time before the given <i>timestamp</i>. The method queries the
	 * database and retrieves the last entity whose time is smaller than timestamp.
	 * 
	 * @param timestamp	a {@literal long} value indicating the timestamp.
	 * 
	 * @return	a {@link IcuAvailabilityList} instance, or {@literal null} if there 
	 * 			are no entities that satisfy the query.
	 */
	@Override
	public IcuAvailabilityList findByTime(long timestamp) {
		
		List<CouchDbIcuAvailabilityList> icuAvailabilities = proxy.getViewWithRange(CouchDbIcuAvailabilitiesRepository.VIEW_BY_TIME, 0, timestamp);

		if (icuAvailabilities == null || icuAvailabilities.isEmpty()) {
			return null;
		}

		return icuAvailabilities.get(icuAvailabilities.size() - 1).getContent();
	}
}
