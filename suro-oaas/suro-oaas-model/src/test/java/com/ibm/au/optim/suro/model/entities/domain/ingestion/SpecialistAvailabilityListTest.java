package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;

/**
 * @author Peter Ilfrich
 */
public class SpecialistAvailabilityListTest {


	@Test
    public void testConstructors() {
    	
        long timeStamp = 1337;

        long ts1 = new Date().getTime();
        SpecialistAvailabilityList list = new SpecialistAvailabilityList();
        long ts2 = new Date().getTime();
        Assert.assertTrue(ts1 <= list.getTimestamp());
        Assert.assertTrue(ts2 >= list.getTimestamp());
        Assert.assertNull(list.getRecords());

        list = new SpecialistAvailabilityList(timeStamp);
        Assert.assertEquals(timeStamp, list.getTimestamp());
        Assert.assertNull(list.getRecords());

    }
}
