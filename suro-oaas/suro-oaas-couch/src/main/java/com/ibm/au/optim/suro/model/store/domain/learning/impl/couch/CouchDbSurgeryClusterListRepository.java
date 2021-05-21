package com.ibm.au.optim.suro.model.store.domain.learning.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryClusterList;
import com.ibm.au.optim.suro.model.entities.domain.learning.couch.CouchDbSurgeryClusterList;
import com.ibm.au.optim.suro.model.store.domain.learning.SurgeryClusterListRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;


/**
 * Class <b>CouchDbSurgeryClusterListRepository</b>. CouchDB specific implementation for the
 * {@link SurgeryClusterListRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link SurgeryClusterList} entities, 
 * via the corresponding {@link CouchDbSurgeryClusterList} documents.
 * 
 * @author Brendan Haesler
 */
public class CouchDbSurgeryClusterListRepository
				extends AbstractCouchDbRepository<CouchDbSurgeryClusterList, SurgeryClusterList>
				implements SurgeryClusterListRepository {

	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the document by their timestamp
	 * attribute.
	 */
	public static final String VIEW_BY_TIME = "by_time";
	
	/**
	 * Initialises an instance of {@link CouchDbSurgeryClusterListRepository}.
	 */
	public CouchDbSurgeryClusterListRepository() {
		super(CouchDbSurgeryClusterList.class);
	}

	/**
	 * Retrieves the collection of cluste surgeries that are known by the given <i>timestamp</i>.
	 * The method queries the repository and retrieves all those cluster surgeries that are known
	 * to be earlier than <i>timestamp</i>. Among the set of collections that are stored in the 
	 * repository the latest is returned, which is the most up to date.
	 * 
	 * @param timestamp		a {@literal long} value indicating the timestamp for the time of interest.
	 * 
	 * @return	a {@link SurgeryClusterList} instance representing the most up to  date surgery cluster
	 * 			list prior to <i>timestamp</i>, or {@literal null} if there is no list before the selected 
	 * 			time.
	 */
	@Override
	public SurgeryClusterList findByTime(long timestamp) {
		List<CouchDbSurgeryClusterList> surgeryClusters = proxy.getViewWithRange("by_time", 0, timestamp);

		if (surgeryClusters == null || surgeryClusters.isEmpty()) {
			return null;
		}

		return surgeryClusters.get(surgeryClusters.size() - 1).getContent();
	}
}
