package com.ibm.au.optim.suro.core.composer.components;

import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryCluster;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryClusterList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class SurgeryClusterInputComponentTest extends InputComponentTestBase {


	/**
	 * 
	 */
	@Override
	@Test
	public void testConstructValues() {
		// add a surgery cluster
		SurgeryCluster cluster = new SurgeryCluster();
		cluster.setClusterId("11111");
		cluster.setChangeOverTime(0.5f);
		cluster.setClusterName("Sandra");
		cluster.setDepartmentId("D01");
		cluster.setDuration(50);
		cluster.setIcuProbability(0.75f);
		cluster.setLengthOfStay(5);
		cluster.setSpecialistTypeId("S01");
		cluster.setUrgencyCategoryId("CAT-1");
		cluster.setWies(0.5f);
		List<SurgeryCluster> surgeryClusters = new ArrayList<>();
		surgeryClusters.add(cluster);
		SurgeryClusterList clusterList = new SurgeryClusterList(TEST_TIME_FROM);
		clusterList.setRecords(surgeryClusters);
		this.scRepo.addItem(clusterList);

		SurgeryClusterInputComponent component = createTemporalInputComponent(SurgeryClusterInputComponent.class);
		component.constructValues(this.environment);

		Assert.assertEquals("{\n" + new NTuple(cluster.getClusterId(),
						cluster.getClusterName(),
						new NTuple(cluster.getDepartmentId()),
						cluster.getDuration(),
						cluster.getChangeOverTime(),
						cluster.getLengthOfStay(),
						cluster.getIcuProbability(),
						cluster.getWardId(),
						cluster.getSpecialistTypeId(),
						cluster.getUrgencyCategoryId(),
						cluster.getWies()) +
						"\n}",
						component.getSections().get(0).getValue());
	}
}
