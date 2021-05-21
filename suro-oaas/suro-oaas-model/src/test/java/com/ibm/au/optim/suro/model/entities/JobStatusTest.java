package com.ibm.au.optim.suro.model.entities;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.JobStatus;


/**
 * @author Peter Ilfrich
 */
public class JobStatusTest  {

	@Test
    public void testExisting() {
        Assert.assertEquals("CREATED", JobStatus.CREATED.getIdentifier());
        Assert.assertEquals("Creating Job", JobStatus.CREATED.getLabel());

        Assert.assertEquals("SUBMITTED", JobStatus.SUBMITTED.getIdentifier());
        Assert.assertEquals("RUNNING", JobStatus.RUNNING.getIdentifier());
        Assert.assertEquals("PROCESSED", JobStatus.PROCESSED.getIdentifier());
        Assert.assertEquals("COMPLETED", JobStatus.COMPLETED.getIdentifier());
        Assert.assertEquals("INTERRUPT", JobStatus.INTERRUPT.getIdentifier());
        Assert.assertEquals("EXCEPTION", JobStatus.EXCEPTION.getIdentifier());
        Assert.assertEquals("FAILED", JobStatus.FAILED.getIdentifier());

    }

	@Test
    public void testComparison() {
		Assert.assertTrue(JobStatus.CREATED.equals(JobStatus.CREATED));
		Assert.assertFalse(JobStatus.CREATED.equals(JobStatus.PROCESSED));
		Assert.assertFalse(JobStatus.INTERRUPT.equals(JobStatus.FAILED));

		Assert.assertEquals(JobStatus.CREATED.hashCode(), JobStatus.CREATED.hashCode());
		Assert.assertEquals(JobStatus.FAILED.hashCode(), JobStatus.FAILED.hashCode());
		Assert.assertNotSame(JobStatus.CREATED.hashCode(), JobStatus.PROCESSED.hashCode());
		Assert.assertNotSame(JobStatus.COMPLETED.hashCode(), JobStatus.RUNNING.hashCode());

		Assert.assertFalse(JobStatus.CREATED.equals(null));
		Assert.assertFalse(JobStatus.CREATED.equals("CREATED"));
    }
}
