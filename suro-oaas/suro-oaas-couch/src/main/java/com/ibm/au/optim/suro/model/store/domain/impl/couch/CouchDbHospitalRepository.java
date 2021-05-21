package com.ibm.au.optim.suro.model.store.domain.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.entities.domain.couch.CouchDbHospital;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import org.ektorp.support.GenerateView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class <b>CouchDbHospitalRepository</b>. CouchDB specific implementation for
 * the {@link HospitalRepository} interface. This repository specialises the 
 * base class {@link AbstractCouchDbRepository} for the management of {@link 
 * Hospital} entities, via the corresponding {@link CouchDbHospital} documents.
 * 
 * 
 * @author Peter Ilfrich
 */
public class CouchDbHospitalRepository 
									extends AbstractCouchDbRepository<CouchDbHospital, Hospital> 
									implements HospitalRepository {


	/**
	 * A {@link String} constant containing the name of the view that is used
	 * to query the databaase by sorting the document according to the region
	 * they belong to.
	 */
	public static final String VIEW_BY_REGION = "by_regionId";
	
    /**
     * Initialises an instance of {@link CouchDbHospitalRepository}.
     */
    public CouchDbHospitalRepository() {
        super(CouchDbHospital.class);
    }

    /**
     * Retrieves the list of hospitals that are stored in the system and are
     * associated to the region identified by <i>regionId</i>.
     * 
     * @param regionId	a {@link String} representing the unique identifier 
     * 					of the region of interest.
     * 
     * @return 	a {@link List} implementation containing the list of {@link Hospital}
     * 			entities.
     */
    @Override
    @GenerateView
    public List<Hospital> findByRegion(String regionId) {
    	
        List<Hospital> hospitals = new ArrayList<>();
        
        List<CouchDbHospital> documents = this.proxy.getView(CouchDbHospitalRepository.VIEW_BY_REGION, regionId);
        
        if ((documents != null) && (documents.isEmpty() == false)) {
        
	        for (CouchDbHospital document : documents) {
	            hospitals.add(this.getContent(document));
	        }
        }
        return hospitals;
    }

}
