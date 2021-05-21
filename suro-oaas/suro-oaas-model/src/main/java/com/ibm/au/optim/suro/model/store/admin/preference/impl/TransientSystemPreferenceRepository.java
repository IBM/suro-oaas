package com.ibm.au.optim.suro.model.store.admin.preference.impl;

import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;

/**
 * @author Peter Ilfrich
 */
public class TransientSystemPreferenceRepository extends AbstractTransientRepository<SystemPreference, SystemPreference> implements SystemPreferenceRepository {

    @Override
    public SystemPreference findByName (String name) {
        if (name == null) {
            return null;
        }

        for (SystemPreference pref : this.repositoryContent.values()) {
            if (name.equals(pref.getName())) {
                return pref;
            }
        }

        return null;
    }

    @Override
    public void removeByName(String name) {
        SystemPreference pref = findByName(name);
        if (pref != null) {
            removeItem(pref.getId());
        }
    }
}