package com.ibm.au.optim.suro.model;

import com.ibm.au.optim.suro.model.admin.feedback.IssueManager;
import com.ibm.au.optim.suro.model.admin.preference.PreferenceManager;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.control.RunController;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.control.job.JobController;

import org.junit.Test;
import org.junit.Assert;

/**
 * Class <b>ServiceInterfaceTest</b>. This class simply tests that all the interfaces that we
 * are using do have the corresponding constant fields defining the {@link Environment} variable
 * used to retrieve the type implementation, equal to the corresponding class name. 
 * 
 * @author Peter Ilfrich
 */
public class ServiceInterfaceTest {

	/**
	 * This method tests that the various interfaces are properly defined and the constant 
	 * variables utilised as environment variable map to the correponding class names of the
	 * interfaces.
	 */
	@Test
    public void testTypeAccessor() {
		
        Assert.assertEquals(JobController.class.getName(), JobController.JOB_CONTROLLER_TYPE);
        Assert.assertEquals(RunController.class.getName(), RunController.RUN_CONTROLLER_TYPE);
        Assert.assertEquals(ModelController.class.getName(), ModelController.MODEL_CONTROLLER_TYPE);
        Assert.assertEquals(DataSetController.class.getName(), DataSetController.DATFILE_PROVIDER_TYPE);
        Assert.assertEquals(HospitalController.class.getName(), HospitalController.HOSPITAL_CONTROLLER_TYPE);
        Assert.assertEquals(IngestionController.class.getName(), IngestionController.INGESTION_CONTROLLER_TYPE);
        Assert.assertEquals(Core.class.getName(), Core.CORE_TYPE);
        Assert.assertEquals(PreferenceManager.class.getName(), PreferenceManager.PREFERENCE_MANAGER_TYPE);
        Assert.assertEquals(IssueManager.class.getName(), IssueManager.ISSUE_MANAGER_TYPE);
    }
}
