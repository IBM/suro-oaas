package com.ibm.au.optim.suro.util;


import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class ProcessResponseTest {

	@Test
    public void testConstructor() {
        ProcessResponse res = new ProcessResponse();
        Assert.assertNull(res.getMessage());
        Assert.assertNull(res.getException());
        Assert.assertFalse(res.isResult());

        res = new ProcessResponse(true);
        Assert.assertNull(res.getMessage());
        Assert.assertNull(res.getException());
        Assert.assertTrue(res.isResult());

        res = new ProcessResponse(true, "foobar");
        Assert.assertEquals("foobar", res.getMessage());
        Assert.assertTrue(res.isResult());
        Assert.assertNull(res.getException());

        res = new ProcessResponse(true, "foobar", new IOException("test"));
        Assert.assertEquals("foobar", res.getMessage());
        Assert.assertEquals("test", res.getException().getMessage());
        Assert.assertTrue(res.isResult());
    }

    @Test
    public void testSetters() {
        ProcessResponse res = new ProcessResponse();

        res.setMessage("foobar");
        Assert.assertEquals("foobar", res.getMessage());
        res.setMessage(null);
        Assert.assertNull(res.getMessage());

        res.setResult(true);
        Assert.assertTrue(res.isResult());
        res.setResult(false);
        Assert.assertFalse(res.isResult());

        res.setException(new IOException("test"));
        Assert.assertEquals("test", res.getException().getMessage());
        res.setException(null);
        Assert.assertNull(res.getException());
    }
}
