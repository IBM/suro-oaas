package com.ibm.au.optim.suro.model.control.domain.ingestion;

import org.junit.Assert;

import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;

import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class IngestionControllerTest {

	@Test
    public void testType() {
    	Assert.assertEquals(IngestionController.INGESTION_CONTROLLER_TYPE, IngestionController.class.getName());
    }
}
