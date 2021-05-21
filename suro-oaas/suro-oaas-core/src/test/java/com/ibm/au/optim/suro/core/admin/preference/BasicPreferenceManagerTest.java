package com.ibm.au.optim.suro.core.admin.preference;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.store.admin.preference.impl.TransientSystemPreferenceRepository;


/**
 * @author Peter Ilfrich
 */
public class BasicPreferenceManagerTest {
	
    /**
     * 
     */
    @Test
    public void testCreatePreference() {
        BasicPreferenceManager mgr = createMgr();
        mgr.setSystemPreference("test1", "test2");

        SystemPreference pref = mgr.getRepository().findByName("test1");
        Assert.assertEquals("test2", pref.getValue());
        Assert.assertEquals("test1", pref.getName());
    }
    /**
     * 
     */
    @Test
    public void testUpdatePreference() {
        BasicPreferenceManager mgr = createMgr();
        mgr.setSystemPreference("test1", "test2");
        mgr.setSystemPreference("test1", "foobar");

        SystemPreference pref = mgr.getRepository().findByName("test1");
        Assert.assertEquals("foobar", pref.getValue());
        Assert.assertEquals(1, mgr.getRepository().getAll().size());
    }

    /**
     * 
     */
    @Test
    public void testSetNullPreference() {
        BasicPreferenceManager mgr = createMgr();
        mgr.setSystemPreference(null, null);

        Assert.assertEquals(1, mgr.getRepository().getAll().size());
        SystemPreference pref = mgr.getRepository().getAll().get(0);
        Assert.assertNull(pref.getName());
        Assert.assertNull(pref.getValue());
    }

    /**
     * 
     */
    @Test
    public void testGetPreference() {
        BasicPreferenceManager mgr = createMgr();
        mgr.setSystemPreference("test1", "test2");

        Assert.assertNull(mgr.getSystemPreference(null));
        Assert.assertNull(mgr.getSystemPreference("not-existing"));
        Assert.assertNotNull(mgr.getSystemPreference("test1"));

        String prefValue = mgr.getSystemPreference("test1");
        Assert.assertEquals("test2", prefValue);
    }
    /**
     * 
     */
    @Test
    public void testRemovePreference() {
        BasicPreferenceManager mgr = createMgr();
        mgr.setSystemPreference("test1", "testA");
        mgr.setSystemPreference("test1", "testB");
        mgr.setSystemPreference("test2", "testC");

        Assert.assertEquals(2, mgr.getRepository().getAll().size());
        mgr.removeSystemPreference(null);
        Assert.assertEquals(2, mgr.getRepository().getAll().size());
        mgr.removeSystemPreference("not-existing");
        Assert.assertEquals(2, mgr.getRepository().getAll().size());
        mgr.removeSystemPreference("test1");
        Assert.assertEquals(1, mgr.getRepository().getAll().size());
        mgr.removeSystemPreference("test2");
        Assert.assertEquals(0, mgr.getRepository().getAll().size());
    }
	
	
	/**
	 * 
	 * @return
	 */
    public BasicPreferenceManager createMgr() {
    	
        BasicPreferenceManager mgr = new BasicPreferenceManager();
        mgr.setRepository(new TransientSystemPreferenceRepository());

        return mgr;
    }
}
