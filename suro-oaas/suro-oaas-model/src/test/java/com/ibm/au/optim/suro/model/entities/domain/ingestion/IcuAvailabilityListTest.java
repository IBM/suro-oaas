package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;

/**
 * @author Peter Ilfrich
 */
public class IcuAvailabilityListTest {

	@Test
    public void testConstructors() {
    	
        long timeStamp = 1337;

        long ts1 = new Date().getTime();
        IcuAvailabilityList list = new IcuAvailabilityList();
        long ts2 = new Date().getTime();
        Assert.assertTrue(ts1 <= list.getTimestamp());
        Assert.assertTrue(ts2 >= list.getTimestamp());
        Assert.assertNull(list.getRecords());

        list = new IcuAvailabilityList(timeStamp);
        Assert.assertEquals(timeStamp, list.getTimestamp());
        Assert.assertNull(list.getRecords());

    }
}
