package com.ibm.au.optim.suro.core.admin.preference;

import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * Runtime service that handles system preferences. It provides access methods to set, get and remove system preferences.
 * System preferences are stored in the database (by default).
 *
 * @author Peter Ilfrich
 */
public class BasicPreferenceManager  extends AbstractSuroService implements PreferenceManager {

    private SystemPreferenceRepository repository;

    @Override
    protected void doBind(Environment environment) throws Exception {
        repository = (SystemPreferenceRepository) environment.getAttribute(SystemPreferenceRepository.PREFERENCE_REPO_INSTANCE);
    }

    @Override
    protected void doRelease() throws Exception {
        repository = null;
    }

    @Override
    public String getSystemPreference(String preferenceName) {
        SystemPreference pref = repository.findByName(preferenceName);
        return pref == null ? null : pref.getValue();
    }

    @Override
    public void setSystemPreference(String preferenceName, String value) {
        SystemPreference preference = repository.findByName(preferenceName);
        if (preference == null) {
            preference = new SystemPreference(preferenceName, value);
            repository.addItem(preference);
        } else {
            preference.setValue(value);
            repository.updateItem(preference);
        }

    }

    @Override
    public void removeSystemPreference(String preferenceName) {
        SystemPreference preference = repository.findByName(preferenceName);
        if (preference != null) {
            repository.removeItem(preference.getId());
        }
    }

    @Override
    public void setRepository(SystemPreferenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public SystemPreferenceRepository getRepository() {
        return repository;
    }
}
