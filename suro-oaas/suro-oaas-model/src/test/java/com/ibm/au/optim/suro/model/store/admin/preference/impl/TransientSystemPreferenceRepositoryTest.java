package com.ibm.au.optim.suro.model.store.admin.preference.impl;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.admin.preference.SystemPreference;
import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.admin.preference.impl.TransientSystemPreferenceRepository;

/**
 * @author Peter Ilfrich
 */
public class TransientSystemPreferenceRepositoryTest {

	@Test
    public void testCreateItem() {
		
        SystemPreferenceRepository repo = new TransientSystemPreferenceRepository();
        SystemPreference pref = new SystemPreference("newname", "newvalue");

        Assert.assertEquals(0, repo.getAll().size());
        Assert.assertEquals("newname", pref.getName());
        Assert.assertEquals("newvalue", pref.getValue());

        SystemPreference pref2 = new SystemPreference(null, null);
        Assert.assertNull(pref2.getName());
        Assert.assertNull(pref2.getValue());

        Assert.assertEquals(0, repo.getAll().size());
    }

    @Test
    public void testFindByName() {
    	
        SystemPreferenceRepository repo = new TransientSystemPreferenceRepository();
        Assert.assertNull(repo.findByName("newname"));
        SystemPreference pref = new SystemPreference("newname", "newvalue");
        Assert.assertNull(repo.findByName("newname"));

        repo.addItem(pref);
        SystemPreference get = repo.findByName("newname");
        Assert.assertNotNull(get);
        Assert.assertEquals("newvalue", get.getValue());
        Assert.assertEquals("newname", get.getName());
    }
    
    @Test
    public void testRemoveByName() {
        
    	SystemPreferenceRepository repo = new TransientSystemPreferenceRepository();
        SystemPreference pref = new SystemPreference("newname", "newvalue");
        repo.addItem(pref);
        Assert.assertEquals(1, repo.getAll().size());

        repo.removeByName("newname");
        Assert.assertEquals(0, repo.getAll().size());
    }

    @Test
    public void testFindNotExisting() {
        
    	SystemPreferenceRepository repo = new TransientSystemPreferenceRepository();
        SystemPreference pref = new SystemPreference("key", "value");
        repo.addItem(pref);

        Assert.assertNull(repo.findByName("not-existing"));
        Assert.assertNull(repo.findByName(null));

        Assert.assertNotNull(repo.findByName("key"));
    }
    
    @Test
    public void testRemoveNotExisting() {
        
    	SystemPreferenceRepository repo = new TransientSystemPreferenceRepository();
        SystemPreference pref = new SystemPreference("key", "value");
        repo.addItem(pref);
        Assert.assertEquals(1, repo.getAll().size());

        repo.removeByName("not-existing");
        Assert.assertEquals(1, repo.getAll().size());

        repo.removeByName(null);
        Assert.assertEquals(1, repo.getAll().size());
    }
}
