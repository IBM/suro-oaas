package com.ibm.au.optim.suro.core.composer;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.composer.components.TemporalInputComponent;
import com.ibm.au.optim.suro.core.controller.BasicHospitalController;
import com.ibm.au.optim.suro.core.controller.BasicIngestionController;
import com.ibm.au.optim.suro.core.controller.BasicMachineLearningController;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.control.domain.learning.MachineLearningController;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientHospitalRepository;
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
import com.ibm.au.optim.suro.model.store.domain.learning.ArrivingPatientRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.InitialPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.SurgeryClusterListRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.impl.TransientArrivingPatientRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.impl.TransientInitialPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.impl.TransientSurgeryClusterListRepository;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * @author brendanhaesler
 */
public abstract class InputComposerTest {


	/**
	 * 
	 */
	protected static final String TEST_SECTION_NAME = "test";

	/**
	 * 
	 */
	protected static final long TEST_TIME_FROM = 1451606400000l; // 01/01/2016 12:00am

	/**
	 * 
	 */
	protected static final long TEST_TIME_TO = TEST_TIME_FROM + TemporalInputComponent.MS_IN_DAY * 2; // 3 day schedule


	/**
	 * 
	 */
	protected Environment environment;

	// Ingestion

	/**
	 * 
	 */
	protected IngestionController ingestionController;

	/**
	 * 
	 */
	protected BasePlanListRepository bpRepo;

	/**
	 * 
	 */
	protected WaitingPatientListRepository wpRepo;

	/**
	 * 
	 */
	protected IcuAvailabilitiesRepository iaRepo;

	/**
	 * 
	 */
	protected WardAvailabilitiesRepository waRepo;

	/**
	 * 
	 */
	protected SpecialistAvailabilitiesRepository saRepo;

	// Hospital

	/**
	 * 
	 */
	protected HospitalController hospitalController;

	/**
	 * 
	 */
	protected HospitalRepository hRepo;

	/**
	 * 
	 */
	protected Hospital hospital;

	// Machine Learning

	/**
	 * 
	 */
	protected MachineLearningController machineLearningController;

	/**
	 * 
	 */
	protected InitialPatientListRepository ipRepo;

	/**
	 * 
	 */
	protected ArrivingPatientRepository apRepo;

	/**
	 * 
	 */
	protected SurgeryClusterListRepository scRepo;


	/**
	 * 
	 */
	@Before
	public void setUp() {
		// Ingestion
		this.bpRepo = new TransientBasePlanListRepository();
		this.wpRepo = new TransientWaitingPatientListRepository();
		this.iaRepo = new TransientIcuAvailabilityListRepository();
		this.waRepo = new TransientWardAvailabilityListRepository();
		this.saRepo = new TransientSpecialistAvailabilityListRepository();

		BasicIngestionController ingestionController = new BasicIngestionController();
		this.ingestionController = ingestionController;

		// Hospital
		this.hRepo = new TransientHospitalRepository();

		BasicHospitalController hospitalController = new BasicHospitalController();
		hospitalController.setHospitalRepository(hRepo);
		this.hospitalController = hospitalController;
		this.hospital = this.hospitalController.createHospital(new Hospital("VIC", "RCH"));

		// Machine Learning
		this.ipRepo = new TransientInitialPatientListRepository();
		this.apRepo = new TransientArrivingPatientRepository();
		this.scRepo = new TransientSurgeryClusterListRepository();

		BasicMachineLearningController machineLearningController = new BasicMachineLearningController();
		machineLearningController.setInitialPatientListRepository(this.ipRepo);
		machineLearningController.setArrivingPatientRepository(this.apRepo);
		machineLearningController.setSurgeryClusterListRepository(this.scRepo);
		this.machineLearningController = machineLearningController;

		// Environment
		this.environment = EnvironmentHelper.mockEnvironment((Properties) null);

		this.environment.setAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE, this.ingestionController);
		this.environment.setAttribute(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE, this.bpRepo);
		this.environment.setAttribute(WaitingPatientListRepository.WAITING_PATIENT_REPOSITORY_INSTANCE, this.wpRepo);
		this.environment.setAttribute(IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_INSTANCE, this.iaRepo);
		this.environment.setAttribute(WardAvailabilitiesRepository.WARD_AVAILABILITY_REPOSITORY_INSTANCE, this.waRepo);
		this.environment.setAttribute(SpecialistAvailabilitiesRepository.SPECIALIST_AVAILABILITY_REPOSITORY_INSTANCE, this.saRepo);

		this.environment.setAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE, this.hospitalController);
		this.environment.setAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE, this.hRepo);

		this.environment.setAttribute(MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE, this.machineLearningController);
		this.environment.setAttribute(InitialPatientListRepository.INITIAL_PATIENT_LIST_REPOSITORY_INSTANCE, this.ipRepo);
		this.environment.setAttribute(ArrivingPatientRepository.ARRIVING_PATIENT_REPOSITORY_INSTANCE, apRepo);
		this.environment.setAttribute(SurgeryClusterListRepository.SURGERY_CLUSTER_LIST_REPOSITORY_INSTANCE, this.scRepo);

		ingestionController.bind(this.environment);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		this.environment = null;

		// Ingestion
		this.ingestionController = null;
		this.bpRepo = null;
		this.wpRepo = null;
		this.iaRepo = null;
		this.waRepo = null;
		this.saRepo = null;

		// Hospital
		this.hospitalController = null;
		this.hRepo = null;
		this.hospital = null;

		// Machine Learning
		this.machineLearningController = null;
		this.ipRepo = null;
		this.apRepo = null;
		this.scRepo = null;
	}
}
