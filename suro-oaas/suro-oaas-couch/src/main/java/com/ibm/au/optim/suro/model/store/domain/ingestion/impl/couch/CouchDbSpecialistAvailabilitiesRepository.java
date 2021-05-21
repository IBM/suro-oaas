package com.ibm.au.optim.suro.model.store.domain.ingestion.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.couch.CouchDbSpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.SpecialistAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

/**
 * Class <b>CouchDbSpecialistAvailabilitiesRepository</b>. CouchDB specific implementation for the
 * {@link SpecialistAvailabilitiesRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link SpecialistAvailabilityList} entities, 
 * via the corresponding {@link CouchDbSpecialistAvailabilityList} documents.
 *
 * @author Brendan Haesler
 */
public class CouchDbSpecialistAvailabilitiesRepository
	
		extends AbstractCouchDbRepository<CouchDbSpecialistAvailabilityList, SpecialistAvailabilityList>
		implements SpecialistAvailabilitiesRepository {
	
	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";
	
	
	/**
	 * Initialises a new instance of the {@link CouchDbSpecialistAvailabilitiesRepository} class.
	 */
	public CouchDbSpecialistAvailabilitiesRepository() {
		super(CouchDbSpecialistAvailabilityList.class);
	}

	/**
	 * Retrieves the last collection of specialist availabilities before the given <i>timestamp</i>.
	 * 
	 * @param timestamp	a {@literal long} value indicating the upper bound for the timestamp.
	 * 
	 * @return 	a {@link SpecialisaAvailabilityList} instance or {@literal null} if there is no entity
	 * 			satisfying the range query.
	 */
	@Override
	public SpecialistAvailabilityList findByTime(long timestamp) {
		List<CouchDbSpecialistAvailabilityList> specialistAvailabilities = this.proxy.getViewWithRange(CouchDbSpecialistAvailabilitiesRepository.VIEW_BY_TIME, 0, timestamp);

		if (specialistAvailabilities == null || specialistAvailabilities.isEmpty()) {
			return null;
		}

		return specialistAvailabilities.get(specialistAvailabilities.size() - 1).getContent();
	}
}
