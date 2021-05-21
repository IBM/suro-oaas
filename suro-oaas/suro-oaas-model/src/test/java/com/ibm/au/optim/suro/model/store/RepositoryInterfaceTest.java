package com.ibm.au.optim.suro.model.store;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.model.store.admin.preference.SystemPreferenceRepository;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.BasePlanListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.IcuAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.SpecialistAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WaitingPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WardAvailabilitiesRepository;


/**
 * Class <b>RepositoryInterfaceTest</b>. This class aggregates all the global tests that are associated
 * to the {@link Repository} interfaces.
 * 
 * @author Peter Ilfrich & Christian Vecchiola
 */
public class RepositoryInterfaceTest {


	/**
	 * This method tests that all the known repository interfaces have the corresponding constants
	 * containing the definition of the type mapped to the full class name of the interface. This
	 * value is used in the configuration files and therefore we need to be sure that they match to
	 * the value we expect to find.
	 */
	@Test
    public void testTypeAccessors() {
    	
        Assert.assertEquals("BasePlanRepository type key should match the full type name.",
							BasePlanListRepository.class.getName(), 
        					BasePlanListRepository.BASE_PLAN_REPOSITORY_TYPE);
        
        Assert.assertEquals("DataSetRepository type key should match the full type name.",
        					DataSetRepository.class.getName(), 
        					DataSetRepository.DATA_SET_REPOSITORY_TYPE);
        
        Assert.assertEquals("HospitalRepository type key should match the full type name.",
        					HospitalRepository.class.getName(), 
        					HospitalRepository.HOSPITAL_REPOSITORY_TYPE);
        
        Assert.assertEquals("OptimizationModelRepository type key should match the full type name.",
        					ModelRepository.class.getName(),
        					ModelRepository.OPTIMISATION_MODEL_REPOSITORY_TYPE);
        
        Assert.assertEquals("OptimizationResultRepository type key should match the full type name.",
        					RunDetailsRepository.class.getName(), 
        					RunDetailsRepository.DETAILS_REPOSITORY_TYPE);
        
        Assert.assertEquals("RegionRepository type key should match the full type name.",
        					RegionRepository.class.getName(), 
        					RegionRepository.REGION_REPOSITORY_TYPE);
        
        Assert.assertEquals("RunRepository type key should match the full type name.",
        					RunRepository.class.getName(), 
        					RunRepository.RUN_REPOSITORY_TYPE);
        
        Assert.assertEquals("SpecialistAvailabilitiesRepository type key should match the full type name.",
        					SpecialistAvailabilitiesRepository.class.getName(), 
        					SpecialistAvailabilitiesRepository.SPECIALIST_AVAILABILITY_REPOSITORY_TYPE);
        
        Assert.assertEquals("TemplateRepository type key should match the full type name.",
        					TemplateRepository.class.getName(),
        					TemplateRepository.TEMPLATE_REPOSITORY_TYPE);
        
        Assert.assertEquals("SystemPreferenceRepository type key should match the full type name.",
        					SystemPreferenceRepository.class.getName(), 
        					SystemPreferenceRepository.PREFERENCE_REPO_TYPE);
        
        Assert.assertEquals("WaitingPatientListRepository type key should match the full type name.",
        					WaitingPatientListRepository.class.getName(), 
        					WaitingPatientListRepository.WAITING_PATIENT_REPOSITORY_TYPE);
        
        Assert.assertEquals("WardAvailabilitiesRepository type key should match the full type name.",
        					WardAvailabilitiesRepository.class.getName(), 
        					WardAvailabilitiesRepository.WARD_AVAILABILITY_REPOSITORY_TYPE);
        
        Assert.assertEquals("IcuAvailabilitiesRepository type key should match the full type name.",
        					IcuAvailabilitiesRepository.class.getName(), 
        					IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_TYPE);
    }
}
