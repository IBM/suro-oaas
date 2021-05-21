package com.ibm.au.optim.suro.model.admin.preference;


import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;

/**
 * Manager interface for preferences. This manager offers CRUD operations for system preferences.
 *
 * @author Peter Ilfrich
 */
public interface PreferenceManager {

    /**
     * The name of the instance as it is stored inside the environment
     */
    String PREFERENCE_MANAGER_INSTANCE = "manager:preference:instance";

    /**
     * The implementing type of the preference manager
     */
    String PREFERENCE_MANAGER_TYPE = PreferenceManager.class.getName();


    /**
     * Retrieves a system preference with the given name. If no such preference exists, null will be returned
     * @param preferenceName - the name of the preference
     * @return - the preference value (as string) or null (if the preference doesn't exist)
     */
    String getSystemPreference(String preferenceName);

    /**
     * Stores a system preferences with the provided name and value. If the preference already exists, it will be
     * updated.
     * @param preferenceName - the name of the preference
     * @param value - the value of the preference
     */
    void setSystemPreference(String preferenceName, String value);

    /**
     * Removes a system preference with the specified name.
     * @param preferenceName - the name of the preference to remove
     */
    void removeSystemPreference(String preferenceName);

    /**
     * Sets the repository for this manager class. In specific use cases it might make sense to use a different
     * repository to retrieve preferences.
     *
     * @param repo - the repository instance
     */
    void setRepository(SystemPreferenceRepository repo);

    /**
     * Retrieves the repository for this manager class.
     * @return - the repository holding system preferences.
     */
    SystemPreferenceRepository getRepository();
}
