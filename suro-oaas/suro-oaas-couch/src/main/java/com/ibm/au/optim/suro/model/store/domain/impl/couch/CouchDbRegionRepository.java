package com.ibm.au.optim.suro.model.store.domain.impl.couch;

import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.entities.domain.couch.CouchDbRegion;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

import org.ektorp.support.GenerateView;


/**
 * Class <b>CouchDbRegionRepository</b>. CouchDB specific implementation for
 * the {@link RegionRepository} interface. This repository specialises the 
 * base class {@link AbstractCouchDbRepository} for the management of {@link 
 * Region} entities, via the corresponding {@link CouchDbRegion} documents.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class CouchDbRegionRepository	 
							extends AbstractCouchDbRepository<CouchDbRegion, Region> 
							implements RegionRepository {


	/**
	 * A {@link String} constant containing the name of the view that is used
	 * to query the databaase by sorting the document according to their name.
	 */
	public static final String VIEW_BY_NAME = "by_name";
	
	
    /**
     * Creates an instance of {@link CouchDbRegionRepository}.
     */
    public CouchDbRegionRepository() {
        super(CouchDbRegion.class);
    }

    /**
     * Gets the region that matches the given <i>name</i>. The method queries 
     * the database for the collection of region matching the given name and
     * returns the first occurrence.
     * 
     * @param name	a {@link String} representing the name of the region to
     * 				search for.
     * 
     * @return 	a {@link Region} instance whose name is <i>name</i> or {@literal 
     * 			null} if not found.
     */
    @Override
    @GenerateView
    public Region findByName(String name) {
    	
        Region result = null;
        
        List<CouchDbRegion> documents = this.proxy.getView(CouchDbRegionRepository.VIEW_BY_NAME, name);
        
        if ((documents != null) && (documents.isEmpty() == false)) {
        	
	        for (CouchDbRegion document : documents) {
	        	
	            result = this.getContent(document);
	            break;
	        }
        
        }
        return result;
    }

}
