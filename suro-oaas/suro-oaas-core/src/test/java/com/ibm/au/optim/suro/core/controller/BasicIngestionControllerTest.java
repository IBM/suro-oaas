package com.ibm.au.optim.suro.core.controller;


import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;

import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatient;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.BasePlanListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.IcuAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.SpecialistAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WaitingPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WardAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientBasePlanListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientWaitingPatientListRepository;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class BasicIngestionControllerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
    public void testRecordCreation() throws Exception {
        // data
        long timeStamp = new Date().getTime();
        List<BasePlanEntry> records = new ArrayList<>();
        records.add(new BasePlanEntry());
        records.add(new BasePlanEntry());

        // prepare
        TransientBasePlanListRepository bpRepo = new TransientBasePlanListRepository();
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE, bpRepo);

        // bind
        BasicIngestionController control = new BasicIngestionController();
        control.doBind(env);

        // execute
        BasePlanList bpList = control.createRecordFromList(timeStamp, records);

        // Assert.assert
        Assert.assertEquals(1, bpRepo.getAll().size());
        Assert.assertEquals(timeStamp, bpList.getTimestamp());
        Assert.assertEquals(2, bpList.getRecords().size());
        Assert.assertNotNull(bpList.getId());

        // try error cases
        Assert.assertNull(control.createRecordFromList(0, null));
        Assert.assertEquals(1, bpRepo.getAll().size());

        Assert.assertNull(control.createRecordFromList(0, new ArrayList<WaitingPatient>()));
        Assert.assertEquals(1, bpRepo.getAll().size());

        env.removeAttribute(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE);
        Assert.assertNull(control.createRecordFromList(timeStamp, records));
        Assert.assertEquals(1, bpRepo.getAll().size());
    }
	/**
	 * 
	 * @throws Exception
	 */
	@Test
    public void testBinding() throws Exception {
        // prepare
        Properties props = new Properties();
        props.setProperty("foo", "bar");
        Environment env = EnvironmentHelper.mockEnvironment(props);

        BasicIngestionController control = new BasicIngestionController();

        // bind
        control.doBind(env);

        // validate
        Assert.assertEquals("bar", control.getEnvironment().getParameter("foo"));
        Assert.assertEquals(5, control.repositoryMap.size());
        Assert.assertEquals(5, control.itemToListMapping.size());

        Assert.assertEquals(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE, control.repositoryMap.get(BasePlanEntry.class));
        Assert.assertEquals(WaitingPatientListRepository.WAITING_PATIENT_REPOSITORY_INSTANCE, control.repositoryMap.get(WaitingPatient.class));
        Assert.assertEquals(IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_INSTANCE, control.repositoryMap.get(IcuAvailability.class));
        Assert.assertEquals(WardAvailabilitiesRepository.WARD_AVAILABILITY_REPOSITORY_INSTANCE, control.repositoryMap.get(WardAvailability.class));
        Assert.assertEquals(SpecialistAvailabilitiesRepository.SPECIALIST_AVAILABILITY_REPOSITORY_INSTANCE, control.repositoryMap.get(SpecialistAvailability.class));


        Assert.assertEquals(BasePlanList.class, control.itemToListMapping.get(BasePlanEntry.class));
        Assert.assertEquals(WaitingPatientList.class, control.itemToListMapping.get(WaitingPatient.class));
        Assert.assertEquals(IcuAvailabilityList.class, control.itemToListMapping.get(IcuAvailability.class));
        Assert.assertEquals(WardAvailabilityList.class, control.itemToListMapping.get(WardAvailability.class));
        Assert.assertEquals(SpecialistAvailabilityList.class, control.itemToListMapping.get(SpecialistAvailability.class));


        // release
        control.doRelease();

        // validate
        Assert.assertNull(control.itemToListMapping);
        Assert.assertNull(control.getEnvironment());
        Assert.assertNull(control.environment);
        Assert.assertNull(control.repositoryMap);
    }
	/**
	 * 
	 * @throws Exception
	 */
	@Test
    public void testRepositoryGetter() throws Exception {
        // prepare
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        BasicIngestionController control = new BasicIngestionController();
        control.doBind(env);

        TransientBasePlanListRepository bpRepo = new TransientBasePlanListRepository();
        env.setAttribute(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE, bpRepo);
        bpRepo.addItem(new BasePlanList());
        Assert.assertEquals(1, control.getRepositoryByType(BasePlanEntry.class).getAll().size());

        TransientWaitingPatientListRepository patientRepo = new TransientWaitingPatientListRepository();
        env.setAttribute(WaitingPatientListRepository.WAITING_PATIENT_REPOSITORY_INSTANCE, patientRepo);
        patientRepo.addItem(new WaitingPatientList());
        patientRepo.addItem(new WaitingPatientList());
        patientRepo.addItem(new WaitingPatientList());
        Assert.assertEquals(3, control.getRepositoryByType(WaitingPatient.class).getAll().size());

    }
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
    public void testGetLatestRecord() throws Exception {
        // prepare
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        BasicIngestionController control = new BasicIngestionController();
        control.doBind(env);
        long ts = 1234567890;

        // prepare repo
        TransientBasePlanListRepository repo = new TransientBasePlanListRepository();
        BasePlanList bpList = new BasePlanList();
        bpList.setTimestamp(ts);
        List<BasePlanEntry> records = new ArrayList<>();
        records.add(new BasePlanEntry());
        records.add(new BasePlanEntry());
        bpList.setRecords(records);
        repo.addItem(bpList);

        // repository not set
        Assert.assertNull(control.getLatestRecordBeforeTime(BasePlanEntry.class, ts));

        // set repo
        env.setAttribute(TransientBasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE, repo);

        // test wrong timestamp
        Assert.assertEquals(0, control.getLatestRecordBeforeTime(BasePlanEntry.class, ts - 5000).size());
        // test wrong class
        Assert.assertNull(control.getLatestRecordBeforeTime(WaitingPatient.class, ts));
        Assert.assertNull(control.getLatestRecordBeforeTime(Run.class, ts));
        Assert.assertNull(control.getLatestRecordBeforeTime(null, ts));

        Assert.assertEquals(2, control.getLatestRecordBeforeTime(BasePlanEntry.class, ts).size());
        Assert.assertEquals(2, control.getLatestRecordBeforeTime(BasePlanEntry.class, ts + 5000).size());

    }
}
