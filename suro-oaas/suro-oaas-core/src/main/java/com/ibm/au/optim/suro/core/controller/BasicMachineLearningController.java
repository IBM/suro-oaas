package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.optim.suro.model.control.domain.learning.MachineLearningController;
import com.ibm.au.optim.suro.model.entities.domain.learning.*;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.store.domain.learning.ArrivingPatientRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.InitialPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.SurgeryClusterListRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.List;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class BasicMachineLearningController extends AbstractSuroService implements MachineLearningController {

	private InitialPatientListRepository initialPatientListRepository;
	private ArrivingPatientRepository arrivingPatientRepository;
	private SurgeryClusterListRepository surgeryClusterListRepository;

	@Override
	public List<SurgeryCluster> getSurgeryClusters(long timestamp) {
		SurgeryClusterList surgeryClusterList = surgeryClusterListRepository.findByTime(timestamp);
		return surgeryClusterList == null ? null : surgeryClusterList.getRecords();
	}

	@Override
	public List<InitialPatient> getInitialPatients(long timestamp) {
		InitialPatientList initialPatientList = initialPatientListRepository.findByTime(timestamp);
		return initialPatientList == null ? null : initialPatientList.getRecords();
	}

	@Override
	public List<ArrivingPatient> getArrivingPatients(long timeFrom, long timeTo) {
		return arrivingPatientRepository.findByTimeRange(timeFrom, timeTo);
	}

	@Override
	public void addSurgeryClusters(long timestamp, List<SurgeryCluster> surgeryClusters) {
		SurgeryClusterList surgeryClusterList = new SurgeryClusterList(timestamp);
		surgeryClusterList.setRecords(surgeryClusters);
		surgeryClusterListRepository.addItem(surgeryClusterList);
	}

	@Override
	public void addInitialPatients(long timestamp, List<InitialPatient> initialPatients) {
		InitialPatientList initialPatientList = new InitialPatientList(timestamp);
		initialPatientList.setRecords(initialPatients);
		initialPatientListRepository.addItem(initialPatientList);
	}

	@Override
	public void addArrivingPatient(ArrivingPatient arrivingPatient) {
		arrivingPatientRepository.addItem(arrivingPatient);
	}

	@Override
	protected void doBind(Environment environment) throws Exception {
		this.initialPatientListRepository = (InitialPatientListRepository) environment.getAttribute(
						InitialPatientListRepository.INITIAL_PATIENT_LIST_REPOSITORY_INSTANCE);
		this.arrivingPatientRepository = (ArrivingPatientRepository) environment.getAttribute(
						ArrivingPatientRepository.ARRIVING_PATIENT_REPOSITORY_INSTANCE);
		this.surgeryClusterListRepository = (SurgeryClusterListRepository) environment.getAttribute(
						SurgeryClusterListRepository.SURGERY_CLUSTER_LIST_REPOSITORY_INSTANCE);
	}

	@Override
	protected void doRelease() throws Exception {
		this.initialPatientListRepository = null;
		this.arrivingPatientRepository = null;
		this.surgeryClusterListRepository = null;
	}

	public InitialPatientListRepository getInitialPatientListRepository() {
		return initialPatientListRepository;
	}

	public void setInitialPatientListRepository(InitialPatientListRepository initialPatientListRepository) {
		this.initialPatientListRepository = initialPatientListRepository;
	}

	public ArrivingPatientRepository getArrivingPatientRepository() {
		return arrivingPatientRepository;
	}

	public void setArrivingPatientRepository(ArrivingPatientRepository arrivingPatientRepository) {
		this.arrivingPatientRepository = arrivingPatientRepository;
	}

	public SurgeryClusterListRepository getSurgeryClusterListRepository() {
		return surgeryClusterListRepository;
	}

	public void setSurgeryClusterListRepository(SurgeryClusterListRepository surgeryClusterListRepository) {
		this.surgeryClusterListRepository = surgeryClusterListRepository;
	}
}
