package com.ibm.au.optim.suro.model.store.admin.preference;

import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.store.Repository;

/**
 * Repository holding system preferences (basic key/value pairs). Currently the implementation only supports String
 * values for the preferences.
 *
 * @author Peter Ilfrich
 */
public interface SystemPreferenceRepository extends Repository<SystemPreference> {

    /**
     * The environment attribute name for the preference repository.
     */
    String PREFERENCE_REPO_INSTANCE = "repo:preference:instance";

    /**
     * The type of the preference repository.
     */
    String PREFERENCE_REPO_TYPE = SystemPreferenceRepository.class.getName();

    /**
     * Finds a system preference by its name.
     * @param name - the name of the preference
     * @return - the preference object holding name and value
     */
    SystemPreference findByName(String name);

    /**
     * Removes a system preference by name from the repository.
     * @param name - the name of the preference to remove.
     */
    void removeByName(String name);
}
