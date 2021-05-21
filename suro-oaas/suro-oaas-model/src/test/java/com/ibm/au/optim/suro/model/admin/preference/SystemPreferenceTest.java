package com.ibm.au.optim.suro.model.admin.preference;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class SystemPreferenceTest {

	@Test
    public void testDefaultConstructor() {
        SystemPreference pref = new SystemPreference();
        Assert.assertNull(pref.getName());
        Assert.assertNull(pref.getValue());
        Assert.assertNull(pref.getId());
    }

	@Test
    public void testSetters() {
        SystemPreference pref = new SystemPreference();
        pref.setName("preferenceName");
        pref.setValue("123");
        pref.setId("xqz");

        Assert.assertEquals("preferenceName", pref.getName());
        Assert.assertEquals("123", pref.getValue());
        Assert.assertEquals("xqz", pref.getId());
    }

	@Test
    public void testConstructoreWithNameAndValue() {
        SystemPreference pref = new SystemPreference("name", "value");

        Assert.assertNull(pref.getId());
        Assert.assertEquals("name", pref.getName());
        Assert.assertEquals("value", pref.getValue());
    }


}
