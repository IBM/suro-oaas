package com.ibm.au.optim.suro.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Generator that generates random alphanumeric strings that are guaranteed to be unique.
 *
 * @author Peter Ilfrich
 */
public class SessionIdentifierGenerator {

    private SecureRandom random = new SecureRandom();

    /**
     * Creates a new unique identifier.
     * @return - a unique string identifier
     */
    public synchronized String next() {
            return new BigInteger(130, random).toString(32);
        }
}
