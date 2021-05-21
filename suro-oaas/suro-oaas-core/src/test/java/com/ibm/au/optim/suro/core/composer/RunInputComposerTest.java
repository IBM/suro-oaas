package com.ibm.au.optim.suro.core.composer;

import com.ibm.au.optim.suro.model.entities.domain.UrgencyCategory;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryCluster;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryClusterList;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author brendanhaesler
 */
public class RunInputComposerTest extends InputComposerTest {

	@Test
	public void testCompose() {
		// Create composer
		RunInputComposer composer = new RunInputComposer();
		composer.setEnvironment(environment);
		composer.setComponentSpecFilename("/migration/0.0.4/RunInputSpecTest.json");
		composer.setHospitalId(hospital.getId());
		composer.setTimeFrom(TEST_TIME_FROM);
		composer.setTimeTo(TEST_TIME_TO);

		// The test specification contains SurgeryCluster info, maxwaitliststay and static components

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

		// create some urgency categories
		List<UrgencyCategory> urgencyCategories = new ArrayList<>();
		urgencyCategories.add(new UrgencyCategory(null, "CAT-1", 30, 1, 1));
		urgencyCategories.add(new UrgencyCategory(null, "CAT-3", 365, 0, 3));
		urgencyCategories.add(new UrgencyCategory(null, "CAT-2", 90, 0, 3));
		this.hospital.setUrgencyCategories(urgencyCategories);

		// compose!
		StringWriter stringWriter = new StringWriter();
		composer.compose(stringWriter);
		
		// [CV] NOTE: where is the test here?
	}
}
