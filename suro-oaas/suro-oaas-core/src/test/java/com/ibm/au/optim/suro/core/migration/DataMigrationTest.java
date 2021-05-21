package com.ibm.au.optim.suro.core.migration;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.core.admin.preference.BasicPreferenceManager;
import com.ibm.au.optim.suro.core.migration.DataMigration;
import com.ibm.au.optim.suro.core.migration.preparer.DummyPreparer;
import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.store.admin.preference.impl.TransientSystemPreferenceRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

import junit.framework.TestCase;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class DataMigrationTest extends TestCase {
    
	/**
	 * 
	 */
    private final static String FUTURE_VERSION = "0.0.9";



    /**
     * 
     */
    @Test
    public void testDummyMigration() {
        DataMigration mig = new DataMigration();
        // happy path
        Environment env = setupEnvironment(true, true, false, false, false);
        mig.bind(env);
        DummyPreparer prep = (DummyPreparer) env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE);
        this.assertCoreReady(env);
        Assert.assertTrue(prep.isCheckExecuted());
        Assert.assertTrue(prep.isExecuteExecuted());
        Assert.assertTrue(prep.isValidateExecuted());
    }
    /**
     * 
     */
    @Test
    public void testInvalidPreparerMigration() {
        DataMigration mig = new DataMigration();
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, FUTURE_VERSION);
        properties.setProperty(DataMigration.CFG_PREFIX_MIGRATION + FUTURE_VERSION, "com.ibm.au.optim.suro.core.migration.preparer.NotExistingPreparer");
        Environment env = setupEnvironment(properties, true, true, false, false, false);

        mig.bind(env);
        this.assertCoreReady(env);
        Assert.assertNull(env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE));
    }
    /**
     * 
     */
    @Test
    public void testInvalidPreparerCast() {
        DataMigration mig = new DataMigration();
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, FUTURE_VERSION);
        properties.setProperty(DataMigration.CFG_PREFIX_MIGRATION + FUTURE_VERSION, "com.ibm.au.optim.suro.core.migration.DataMigration");
        Environment env = setupEnvironment(properties, true, true, false, false, false);

        mig.bind(env);
        this.assertCoreReady(env);
        Assert.assertNull(env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE));
    }
    /**
     * 
     */
    @Test
    public void testEmptyVersionString() {
        DataMigration mig = new DataMigration();
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, "");
        Environment env = setupEnvironment(properties, true, true, false, false, false);

        mig.bind(env);
        this.assertCoreReady(env);
        Assert.assertNull(env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE));
    }
    /**
     * 
     */
    @Test
    public void testNullPreparerMigration() {
        DataMigration mig = new DataMigration();
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, FUTURE_VERSION);
        Environment env = setupEnvironment(properties, true, true, false, false, false);

        mig.bind(env);
        this.assertCoreReady(env);
        Assert.assertNull(env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE));
    }
    /**
     * 
     */
    @Test
    public void testEmptyPreparerMigration() {
        DataMigration mig = new DataMigration();
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, FUTURE_VERSION);
        properties.setProperty(DataMigration.CFG_PREFIX_MIGRATION + FUTURE_VERSION, "");
        Environment env = setupEnvironment(properties, true, true, false, false, false);

        mig.bind(env);
        this.assertCoreReady(env);
    }

    /**
     * 
     */
    @Test
    public void testPastPreparer() {
        DataMigration mig = new DataMigration();
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, "0.0.1");
        properties.setProperty(DataMigration.CFG_PREFIX_MIGRATION + "0.0.1", "com.ibm.au.optim.suro.core.migration.preparer.DummyPreparer");
        Environment env = setupEnvironment(properties, true, true, false, false, false);

        mig.bind(env);
        // migration finishes without error
        this.assertCoreReady(env);
        // shouldn't be stored in environment, because it is a past preparer and should not be executed
        Assert.assertNull(env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE));
    }
    /**
     * 
     */
    @Test
    public void testCheckException() {
        DataMigration mig = new DataMigration();
        Environment env = setupEnvironment(true, true, true, false, false);

        mig.bind(env);

        DummyPreparer preparer = (DummyPreparer) env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE);
        this.assertCoreReady(env);
        Assert.assertTrue(preparer.isCheckExecuted());
        Assert.assertFalse(preparer.isExecuteExecuted());
        Assert.assertFalse(preparer.isValidateExecuted());
    }
    /**
     * 
     */
    @Test
    public void testExecuteException() {
        DataMigration mig = new DataMigration();
        Environment env = setupEnvironment(true, true, false, true, false);

        mig.bind(env);

        DummyPreparer preparer = (DummyPreparer) env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE);
        this.assertCoreReady(env);
        Assert.assertTrue(preparer.isCheckExecuted());
        Assert.assertTrue(preparer.isExecuteExecuted());
        Assert.assertFalse(preparer.isValidateExecuted());
    }
    /**
     * 
     */
    @Test
    public void testValidateException() {
        DataMigration mig = new DataMigration();
        Environment env = setupEnvironment(true, true, false, false, true);

        mig.bind(env);

        DummyPreparer preparer = (DummyPreparer) env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE);
        this.assertCoreReady(env);
        Assert.assertTrue(preparer.isCheckExecuted());
        Assert.assertTrue(preparer.isExecuteExecuted());
        Assert.assertTrue(preparer.isValidateExecuted());
    }
    /**
     * 
     */
    @Test
    public void testCheckPassed() {
        DataMigration mig = new DataMigration();
        Environment env = setupEnvironment(false, true, false, false, true);

        mig.bind(env);

        DummyPreparer preparer = (DummyPreparer) env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE);
        this.assertCoreReady(env);
        Assert.assertTrue(preparer.isCheckExecuted());
        Assert.assertFalse(preparer.isExecuteExecuted());
        Assert.assertFalse(preparer.isValidateExecuted());
    }
    /**
     * 
     */
    @Test
    public void testValidateFailed() {
        DataMigration mig = new DataMigration();
        Environment env = setupEnvironment(true, false, false, false, false);

        mig.bind(env);

        DummyPreparer preparer = (DummyPreparer) env.getAttribute(DummyPreparer.DUMMYPREPARER_INSTANCE);
        this.assertCoreReady(env);
        Assert.assertTrue(preparer.isCheckExecuted());
        Assert.assertTrue(preparer.isExecuteExecuted());
        Assert.assertTrue(preparer.isValidateExecuted());
    }









    /**
     * 
     */
    private void assertCoreReady(Environment env) {
        Assert.assertTrue(((Core) env.getAttribute(Core.CORE_INSTANCE)).isReady());
    }
    /**
     * 
     */
    private Environment setupEnvironment(Properties properties, boolean check, boolean validate, boolean checkException, boolean executeException, boolean validateException) {
        Environment env = EnvironmentHelper.mockEnvironment(properties);
        PreferenceManager manager = new BasicPreferenceManager();
        manager.setRepository(new TransientSystemPreferenceRepository());
        env.setAttribute(PreferenceManager.PREFERENCE_MANAGER_INSTANCE, manager);

        SuroCore c = new SuroCore();
        c.bind(env);
        env.setAttribute(Core.CORE_INSTANCE, c);

        env.setAttribute(DummyPreparer.KEY_CHECK_RESULT, check);
        env.setAttribute(DummyPreparer.KEY_VALIDATE_RESULT, validate);
        env.setAttribute(DummyPreparer.KEY_CHECK_EXCEPTION, checkException);
        env.setAttribute(DummyPreparer.KEY_EXECUTE_EXCEPTION, executeException);
        env.setAttribute(DummyPreparer.KEY_VALIDATE_EXCEPTION, validateException);

        return env;
    }
    /**
     * 
     */
    private Environment setupEnvironment(boolean check, boolean validate, boolean checkException, boolean executeException, boolean validateException) {
        Properties properties = new Properties();
        properties.setProperty(DataMigration.CFG_VERSION_LIST, FUTURE_VERSION);
        properties.setProperty(DataMigration.CFG_PREFIX_MIGRATION + FUTURE_VERSION, "com.ibm.au.optim.suro.core.migration.preparer.DummyPreparer");

        return setupEnvironment(properties, check, validate, checkException, executeException, validateException);
    }


}
