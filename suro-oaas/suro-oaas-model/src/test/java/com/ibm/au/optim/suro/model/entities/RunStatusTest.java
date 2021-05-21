package com.ibm.au.optim.suro.model.entities;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.RunStatus;


/**
 * @author Peter Ilfrich
 */
public class RunStatusTest {


	@Test
    public void testExisting() {
    	Assert.assertEquals("COMPLETED", RunStatus.COMPLETED.getIdentifier());
    	Assert.assertEquals("Complete", RunStatus.COMPLETED.getLabel());

    	Assert.assertEquals("QUEUED", RunStatus.QUEUED.getIdentifier());
    	Assert.assertEquals("CREATE_JOB", RunStatus.CREATE_JOB.getIdentifier());
    	Assert.assertEquals("PROCESSING", RunStatus.PROCESSING.getIdentifier());
    	Assert.assertEquals("COLLECTING_RESULTS", RunStatus.COLLECTING_RESULTS.getIdentifier());
    	Assert.assertEquals("ABORTING", RunStatus.ABORTING.getIdentifier());
    	Assert.assertEquals("ABORTED", RunStatus.ABORTED.getIdentifier());
    	Assert.assertEquals("FAILED", RunStatus.FAILED.getIdentifier());
    	Assert.assertEquals("INVALID", RunStatus.INVALID.getIdentifier());
    	Assert.assertEquals("RESUME", RunStatus.RESUME.getIdentifier());
    }
    
    @Test
    public void testComparison() {
    	Assert.assertTrue(RunStatus.COMPLETED.equals(RunStatus.COMPLETED));
    	Assert.assertTrue(RunStatus.ABORTED.equals(RunStatus.ABORTED));
    	Assert.assertFalse(RunStatus.ABORTED.equals(RunStatus.COMPLETED));

    	Assert.assertEquals(RunStatus.ABORTED.hashCode(), RunStatus.ABORTED.hashCode());
    	Assert.assertNotSame(RunStatus.ABORTED.hashCode(), RunStatus.ABORTING.hashCode());
    }
}
