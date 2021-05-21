package com.ibm.au.optim.suro.model.store.domain.ingestion.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.couch.CouchDbBasePlanList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.BasePlanListRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

/**
 * Class <b>CouchDbBasePlanListRepository</b>. This class extends {@link AbstractCouchDbRepository}
 * and specialises its operations for {@link BasePlanList} entities. 
 *
 * @author Brendan Haesler
 */

public class CouchDbBasePlanListRepository
		extends AbstractCouchDbRepository<CouchDbBasePlanList, BasePlanList>
		implements BasePlanListRepository {
	
	
	/**
	 * A {@link String} constant containing the name of the view that
	 * is used to query the information about the base plan by time.
	 */
	public static final String VIEW_BY_TIME = "by_time";

	/**
	 * Initialises a new instance of the {@link CouchDbBasePlanListRepository}.
	 */
	public CouchDbBasePlanListRepository() {
		super(CouchDbBasePlanList.class);
	}

	/**
	 * Gets the collection of base plans that was last ingested until the time
	 * defined by the given timestamp. The method queries the database and 
	 * return the last occurrence of base plan list that it finds.
	 * 
	 * @param timestamp	a {@literal long} value indicating the last timestamp
	 * 					of interest.
	 * 
	 * @return 	a {@link BasePlanList} instance representing the collection
	 * 			of {@link BasePlan} entities ingested until the given timestamp.
	 * 			
	 */
	@Override
	public BasePlanList findByTime(long timestamp) {
		List<CouchDbBasePlanList> basePlans = this.proxy.getViewWithRange(CouchDbBasePlanListRepository.VIEW_BY_TIME, 0, timestamp);

		if (basePlans == null || basePlans.isEmpty()) {
			return null;
		}

		CouchDbBasePlanList entities = basePlans.get(basePlans.size() - 1);
		
		return entities.getContent();
	}
}
