package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * @author Peter Ilfrich
 */
public class DatabaseVersionPreparer implements DatabasePreparer {


    @Override
    public boolean check(Environment env) throws Exception {
        PreferenceManager prefMgr = (PreferenceManager) env.getAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE);
        return (prefMgr.getSystemPreference(Core.PREFERENCE_DB_VERSION_NAME) == null);
    }

    @Override
    public void execute(Environment env) throws Exception {
        PreferenceManager prefMgr = (PreferenceManager) env.getAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE);
        prefMgr.setSystemPreference(Core.PREFERENCE_DB_VERSION_NAME, SuroCore.DATABASE_MIN_VERSION);
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }
}
