package com.ibm.au.optim.suro.core.composer;


import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class NTupleTest {

	@Test
	public void testToString() {
		NTuple tuple = new NTuple(12, "Hello World!", new NTuple(4), new NTuple("roflcopter"));
		Assert.assertEquals("<12 \"Hello World!\" <4> <\"roflcopter\">>", tuple.toString());
	}

	@Test
	public void testReadTuple() throws Exception {
		String tupleString = " < 1 2.3 \"This is a string\" 4 <\"This is a nested tuple\" 5   > \"This is not a nested tuple <_< >_>\">";
		StringReader reader = new StringReader(tupleString);

		Assert.assertTrue(NTuple.hasNextNTuple(reader));

		NTuple tuple = NTuple.readTuple(reader);

		Assert.assertEquals("<1 2.3 \"This is a string\" 4 <\"This is a nested tuple\" 5> \"This is not a nested tuple <_< >_>\">",
						tuple.toString());
	}
}
