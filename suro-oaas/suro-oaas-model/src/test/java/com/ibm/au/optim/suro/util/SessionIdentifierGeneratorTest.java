package com.ibm.au.optim.suro.util;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author Peter Ilfrich
 */
public class SessionIdentifierGeneratorTest {

	@Test
    public void testUniqueness() {
        SessionIdentifierGenerator gen = new SessionIdentifierGenerator();
        for (int i = 0; i < 500; i++) {
        	Assert.assertNotSame(gen.next(), gen.next());
        }
    }
}
