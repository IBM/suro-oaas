package com.ibm.au.optim.suro.model.store.domain.learning;

import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryCluster;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryClusterList;
import com.ibm.au.optim.suro.model.store.domain.ingestion.TemporalRepository;

/**
 * @author brendanhaesler
 */

// TODO: Document this interface
public interface SurgeryClusterListRepository extends TemporalRepository<SurgeryCluster, SurgeryClusterList> {

	String SURGERY_CLUSTER_LIST_REPOSITORY_INSTANCE = "repo:surgeryclusterlist:instance";
	
	String SURGERY_CLUSTER_LIST_REPOSITORY_TYPE = SurgeryClusterListRepository.class.getName();
}
