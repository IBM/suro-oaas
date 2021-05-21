package com.ibm.au.optim.suro.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.controller.BasicIngestionController;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
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
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientIcuAvailabilityListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientSpecialistAvailabilityListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientWaitingPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.impl.TransientWardAvailabilityListRepository;

import com.ibm.au.jaws.web.core.runtime.Environment;


import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Test cases for the {@link IngestionApi}
 *
 * @author brendanhaesler
 */
public class IngestionApiTest {

	private IngestionApi ingestionApi;
	private IngestionController ingestionController;

	private BasePlanListRepository bpRepo;
	private WaitingPatientListRepository wpRepo;
	private IcuAvailabilitiesRepository iaRepo;
	private WardAvailabilitiesRepository waRepo;
	private SpecialistAvailabilitiesRepository saRepo;

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testBasePlanPostHandler() throws Exception {
		
		setupApi();
		List<BasePlanEntry> bps = new ArrayList<>();
		bps.add(new BasePlanEntry("Cardiac Surgery", 1, 2));
		Response response = ingestionApi.basePlanPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(bps)));
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(bpRepo.getAll().size(), 1);
	}

	@Test
	public void testWaitingListPostHandler() throws Exception {
		
		setupApi();
		List<WaitingPatient> wps = new ArrayList<>();
		wps.add(new WaitingPatient("P1234", "S1234", "Apendectomy", new Date().getTime(), 1));
		Response response = ingestionApi.waitingListPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(wps)));
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(1, wpRepo.getAll().size());
	}

	@Test
	public void testIcuAvailabilityPostHandler() throws Exception {
		
		setupApi();
		List<IcuAvailability> ias = new ArrayList<>();
		ias.add(new IcuAvailability("1234A", new Date().getTime()));
		Response response = ingestionApi.icuAvailabilityPutHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(ias)));
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(1, iaRepo.getAll().size());
	}

	@Test
	public void testWardAvailabilityPostHandler() throws Exception {
		
		setupApi();
		List<WardAvailability> was = new ArrayList<>();
		was.add(new WardAvailability("1234A", "1234B", new Date().getTime()));
		Response response = ingestionApi.wardAvailabilityPostHandler(
						new ByteArrayInputStream(mapper.writeValueAsBytes(was)));
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(1, waRepo.getAll().size());
	}

	@Test
	public void testSpecialistAvailabilityPostHandler() throws Exception {
		
		setupApi();
		List<SpecialistAvailability> sas = new ArrayList<>();
		sas.add(new SpecialistAvailability("1234B", new Date().getTime(), 3));
		Response response = ingestionApi.specialistAvailabilityPostHandler(
						new ByteArrayInputStream(mapper.writeValueAsBytes(sas)));
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(1, saRepo.getAll().size());
	}

	@Test
	public void testIngestionPostHandler() throws Exception {
		
		setupApi();
		Response response;
		List<IcuAvailability> ias = new ArrayList<>();
		String patientId = "1234A";
		long dueDate = new Date().getTime();

		// Test putting something of the wrong format
		List<BasePlanEntry> bps = new ArrayList<>();
		bps.add(new BasePlanEntry("Cardiac Surgery", 1, 1));
		response = ingestionApi.ingestionPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(bps)),
						new TypeReference<List<IcuAvailability>>() { });
		Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

		// Test putting something
		ias.add(new IcuAvailability(patientId, dueDate));
		response = ingestionApi.ingestionPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(ias)),
						new TypeReference<List<IcuAvailability>>() { });
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(1, iaRepo.getAll().size());

		// Test putting another thing
		response = ingestionApi.ingestionPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(ias)),
						new TypeReference<List<IcuAvailability>>() { });
		Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		Assert.assertEquals(2, iaRepo.getAll().size());

		// Test with no repo available
		((BasicIngestionController) ingestionController).getEnvironment().removeAttribute(IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_INSTANCE);
		response = ingestionApi.ingestionPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(ias)),
						new TypeReference<List<IcuAvailability>>() { });
		Assert.assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), response.getStatus());

		// Test with empty list
		((BasicIngestionController) ingestionController).getEnvironment().setAttribute(IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_INSTANCE, new TransientBasePlanListRepository());
		ias = new ArrayList<>();
		response = ingestionApi.ingestionPostHandler(new ByteArrayInputStream(mapper.writeValueAsBytes(ias)),
				new TypeReference<List<IcuAvailability>>() { });
		Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}

	@Test
	public void testGetBeforeTimeHandler() {
		
		long timestamp = 1234567890;
		setupApi();
		BasePlanList bpList = new BasePlanList(timestamp);
		bpList.setRecords(new ArrayList<BasePlanEntry>());
		bpList.getRecords().add(new BasePlanEntry());
		bpRepo.addItem(bpList);

		Assert.assertEquals(1, ((List<?>) ingestionApi.ingestionGetBeforeTimeHandler(BasePlanEntry.class, timestamp).getEntity()).size());
		Assert.assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), ingestionApi.ingestionGetBeforeTimeHandler(Run.class, timestamp).getStatus());
		Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ingestionApi.ingestionGetBeforeTimeHandler(BasePlanEntry.class, timestamp - 5000).getStatus());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBasePlanGetter() {
		
		// data
		long timestamp = 1234567890;
		Response response;

		setupApi();
        BasePlanList list = new BasePlanList(timestamp);
        list.setRecords(new ArrayList<BasePlanEntry>());
        list.getRecords().add(new BasePlanEntry());
        list.getRecords().add(new BasePlanEntry());
        bpRepo.addItem(list);

		response = ingestionApi.basePlanGetHandler(timestamp);
		Assert.assertEquals(2, ((List<BasePlanList>) response.getEntity()).size());
	}

	@SuppressWarnings("unchecked")
	@Test
    public void testWaitListGetter() {
    	
        // data
        long timestamp = 1234567890;
        Response response;

        setupApi();
        WaitingPatientList list = new WaitingPatientList(timestamp);
        list.setRecords(new ArrayList<WaitingPatient>());
        list.getRecords().add(new WaitingPatient());
        list.getRecords().add(new WaitingPatient());
        wpRepo.addItem(list);

        response = ingestionApi.waitingListGetHandler(timestamp);
        Assert.assertEquals(2, ((List<BasePlanList>) response.getEntity()).size());
    }

	@SuppressWarnings("unchecked")
	@Test
    public void testIcuAvailabilityGetter() {
    	
        // data
        long timestamp = 1234567890;
        Response response;

        setupApi();
        IcuAvailabilityList list = new IcuAvailabilityList(timestamp);
        list.setRecords(new ArrayList<IcuAvailability>());
        list.getRecords().add(new IcuAvailability());
        list.getRecords().add(new IcuAvailability());
        iaRepo.addItem(list);

        response = ingestionApi.icuAvailabilityGetHandler(timestamp);
        Assert.assertEquals(2, ((List<BasePlanList>) response.getEntity()).size());
    }

	@SuppressWarnings("unchecked")
	@Test
    public void testWardAvailabilityGetter() {
    	
        // data
        long timestamp = 1234567890;
        Response response;

        setupApi();
        WardAvailabilityList list = new WardAvailabilityList(timestamp);
        list.setRecords(new ArrayList<WardAvailability>());
        list.getRecords().add(new WardAvailability());
        list.getRecords().add(new WardAvailability());
        waRepo.addItem(list);

        response = ingestionApi.wardAvailabilityGetHandler(timestamp);
        Assert.assertEquals(2, ((List<BasePlanList>) response.getEntity()).size());
    }

	@SuppressWarnings("unchecked")
	@Test
    public void testSpecialistdAvailabilityGetter() {
    	
        // data
        long timestamp = 1234567890;
        Response response;

        setupApi();
        SpecialistAvailabilityList list = new SpecialistAvailabilityList(timestamp);
        list.setRecords(new ArrayList<SpecialistAvailability>());
        list.getRecords().add(new SpecialistAvailability());
        list.getRecords().add(new SpecialistAvailability());
        saRepo.addItem(list);

        response = ingestionApi.specialistAvailabilityGetHandler(timestamp);
        Assert.assertEquals(2, ((List<BasePlanList>) response.getEntity()).size());
    }


	/*
	 * HELPER METHODS
	 */

	private Environment setupApi() {
		
		ingestionApi = new IngestionApi();
		bpRepo = new TransientBasePlanListRepository();
		wpRepo = new TransientWaitingPatientListRepository();
		iaRepo = new TransientIcuAvailabilityListRepository();
		waRepo = new TransientWardAvailabilityListRepository();
		saRepo = new TransientSpecialistAvailabilityListRepository();

		Environment env = EnvironmentHelper.mockEnvironment((Properties) null);

		env.setAttribute(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE, bpRepo);
		env.setAttribute(WaitingPatientListRepository.WAITING_PATIENT_REPOSITORY_INSTANCE, wpRepo);
		env.setAttribute(IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_INSTANCE, iaRepo);
		env.setAttribute(WardAvailabilitiesRepository.WARD_AVAILABILITY_REPOSITORY_INSTANCE, waRepo);
		env.setAttribute(SpecialistAvailabilitiesRepository.SPECIALIST_AVAILABILITY_REPOSITORY_INSTANCE, saRepo);


		BasicIngestionController controller = new BasicIngestionController();
		controller.bind(env);
		ingestionController = controller;
		ingestionApi.environment = env;

		env.setAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE, controller);
		return env;
	}

}

