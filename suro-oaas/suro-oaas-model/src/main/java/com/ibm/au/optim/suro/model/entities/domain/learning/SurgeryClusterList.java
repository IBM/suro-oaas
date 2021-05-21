package com.ibm.au.optim.suro.model.entities.domain.learning;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class SurgeryClusterList extends TemporalList<SurgeryCluster> {
	public SurgeryClusterList() { }

	public SurgeryClusterList(long timeStamp) {
		super(timeStamp);
	}
}
