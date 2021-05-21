package com.ibm.au.optim.suro.model.store.admin.preference.impl.couch;

import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.admin.preference.impl.couch.CouchDbSystemPreference;
import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;

import java.util.List;

import org.ektorp.support.GenerateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class <b>CouchDbSystemPreferenceRepository</b>. CouchDB specific implementation of the system 
 * preference repository allowing to add new preferences, read preferences, find preferences, 
 * delete preferences and update them.
 *
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class CouchDbSystemPreferenceRepository extends AbstractCouchDbRepository<CouchDbSystemPreference, SystemPreference> implements SystemPreferenceRepository {

	/**
	 * A {@link Logger} instance indicating 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbSystemPreferenceRepository.class);
	
	/**
	 * A {@link String} representing the name of the view allowing to 
	 * query the database by the name of the specific preference.
	 */
	public static final String VIEW_BY_NAME = "by_name";
	
    /**
     * Initialises an instance of {@link CouchDbSystemPreferenceRepository}.
     */
    public CouchDbSystemPreferenceRepository() {
        super(CouchDbSystemPreference.class);
    }

    /**
     * Retrieves the system preference that matches the given <i>name</i>.
     * 
     * @param name	a {@link String} representing the name of the preference
     * 				to search for.
     * 
     * @return	a {@link CouchDbSystemPreference} instance or {@literal null}
     * 			if there is an error with the database of there is no preference
     * 			associated to the given <i>name</i>.
     */
    @Override
    @GenerateView
    public SystemPreference findByName(String name) {
    	
        SystemPreference result = null;
        try {
        	
        	List<CouchDbSystemPreference> prefs = this.proxy.getView(CouchDbSystemPreferenceRepository.VIEW_BY_NAME, name);
        	
            for (CouchDbSystemPreference pref : prefs) {
                result = this.getContent(pref);
            }
            
        } catch (Exception e) {
        	
            LOGGER.error("Error retrieving system preference by name " + name + ". This is most likely due to a not yet initialized repository.", e);
        }

        return result;
    }

    /**
     * Removes the system preference that matches the given <i>name</i>.
     * If the preference is not found, nothing happens.
     * 
     * @param name	a {@link String} representing the name of the preference
     * 				to search for.
     */
    @Override
    public void removeByName(String name) {
    	
        SystemPreference result = this.findByName(name);
        if (result != null) {
        
        	this.removeItem(result.getId());
        }
    }

}
