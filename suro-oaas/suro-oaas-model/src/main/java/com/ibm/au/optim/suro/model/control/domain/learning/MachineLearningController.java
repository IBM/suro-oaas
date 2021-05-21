package com.ibm.au.optim.suro.model.control.domain.learning;

import java.util.List;

import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryCluster;

/**
 * @author brendanhaesler
 */

// TODO: Document this interface
public interface MachineLearningController {

	String MACHINE_LEARNING_CONTROLLER_INSTANCE = "controller:learning:instance";

	String MACHINE_LEARNING_CONTROLLER_TYPE = MachineLearningController.class.getName();

	// Get surgery clusters
	List<SurgeryCluster> getSurgeryClusters(long timestamp);

	// Classify initial patients for time period
	List<InitialPatient> getInitialPatients(long timestamp);

	// Get predicated arriving patients for time period
	List<ArrivingPatient> getArrivingPatients(long timeFrom, long timeTo);

	void addSurgeryClusters(long timestamp, List<SurgeryCluster> surgeryClusters);

	void addInitialPatients(long timestamp, List<InitialPatient> initialPatients);

	void addArrivingPatient(ArrivingPatient arrivingPatient);
}
