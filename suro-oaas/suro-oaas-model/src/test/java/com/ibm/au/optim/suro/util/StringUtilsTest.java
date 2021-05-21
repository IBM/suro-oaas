package com.ibm.au.optim.suro.util;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author Peter Ilfrich
 */
public class StringUtilsTest {

	@Test
    public void testNullCheck() {
    	Assert.assertFalse(StringUtils.isNullOrEmpty("s"));
        Assert.assertTrue(StringUtils.isNullOrEmpty(""));
        Assert.assertTrue(StringUtils.isNullOrEmpty(null));
    }
	@Test
    public void testParseLong() {
    	Assert.assertEquals(new Long(144), StringUtils.safeParseLong("51445", 1, 4));
        Assert.assertNotSame(new Long(144), StringUtils.safeParseLong("144", 1, 2));
        Assert.assertNull(StringUtils.safeParseLong("abc5555", 1, 5));
        Assert.assertEquals(new Long(144), StringUtils.safeParseLong("144", 0, 3));
    }
	@Test
    public void testParseDouble() {
    	Assert.assertEquals(new Double(144.11), StringUtils.safeParseDouble("5144.115", 1, 7));
    	Assert.assertNotSame(new Double(144.11), StringUtils.safeParseDouble("144.11", 1, 2));
    	Assert.assertNull(StringUtils.safeParseDouble("abc5555", 1, 5));
    	Assert.assertEquals(new Double(144.11), StringUtils.safeParseDouble("144.11", 0, 6));
    }
	@Test
    public void testEmptyConstructor() {
        StringUtils su = new StringUtils();
        Assert.assertNotNull(su);
    }
}
