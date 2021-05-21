package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.core.admin.preference.BasicPreferenceManager;
import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.admin.preference.impl.TransientSystemPreferenceRepository;

import java.util.Properties;

import com.ibm.au.jaws.web.core.runtime.Environment;

import junit.framework.TestCase;

/**
 * @author Peter Ilfrich
 */
public class DatabaseVersionPreparerTest extends TestCase {


    public void testHappyPath() throws Exception {
        Environment env = setupEnvironment();
        DatabaseVersionPreparer prep = new DatabaseVersionPreparer();
        assertTrue(prep.check(env));

        prep.execute(env);
        assertTrue(prep.validate(env));

        SystemPreferenceRepository repo = getRepository(env);
        assertEquals(1, repo.getAll().size());
        assertNotNull(repo.findByName(Core.PREFERENCE_DB_VERSION_NAME));
        assertEquals(SuroCore.DATABASE_MIN_VERSION, repo.findByName(Core.PREFERENCE_DB_VERSION_NAME).getValue());
    }

    public void testAlreadyExist() throws Exception {
        Environment env = setupEnvironment();
        getRepository(env).addItem(new SystemPreference(Core.PREFERENCE_DB_VERSION_NAME, SuroCore.DATABASE_MIN_VERSION));

        DatabaseVersionPreparer prep = new DatabaseVersionPreparer();
        assertFalse(prep.check(env));
        assertTrue(prep.validate(env));

        prep.execute(env);
        assertEquals(1, getRepository(env).getAll().size());
    }

    public void testValidationFailed() throws Exception {
        Environment env = setupEnvironment();
        getRepository(env).addItem(new SystemPreference("Abc", "some-value"));
        getRepository(env).addItem(new SystemPreference("Def", "some-other-value"));

        DatabaseVersionPreparer prep = new DatabaseVersionPreparer();
        assertFalse(prep.validate(env));
    }



    private Environment setupEnvironment() {
        PreferenceManager pm = new BasicPreferenceManager();
        pm.setRepository(new TransientSystemPreferenceRepository());

        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE, pm);

        return env;
    }

    private SystemPreferenceRepository getRepository(Environment env) {
        return ((PreferenceManager) env.getAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE)).getRepository();
    }
}
