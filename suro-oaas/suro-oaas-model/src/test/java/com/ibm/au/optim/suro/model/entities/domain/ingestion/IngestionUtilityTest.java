package com.ibm.au.optim.suro.model.entities.domain.ingestion;


import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.IngestionUtility;

/**
 * @author Peter Ilfrich
 */
public class IngestionUtilityTest {

	@Test
    public void testDayStart() {
    	
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(IngestionUtility.getDayStart((12 * 60 * 60 * 1000) + (59 * 60 * 1000) + (59 * 1000)));

        Assert.assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, cal.get(Calendar.MINUTE));
        Assert.assertEquals(0, cal.get(Calendar.SECOND));
        Assert.assertEquals(0, cal.get(Calendar.MILLISECOND));

        Assert.assertEquals(1970, cal.get(Calendar.YEAR));
        Assert.assertEquals(0, cal.get(Calendar.MONTH));
        // careful with time zones!!
        Assert.assertEquals(1, cal.get(Calendar.DAY_OF_YEAR));

    }
}
