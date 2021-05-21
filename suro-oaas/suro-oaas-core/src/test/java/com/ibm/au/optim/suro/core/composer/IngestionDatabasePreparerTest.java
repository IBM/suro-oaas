package com.ibm.au.optim.suro.core.composer;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.optim.suro.core.composer.components.TemporalInputComponent;
import com.ibm.au.optim.suro.core.migration.preparer.v004.IngestionDatabasePreparer;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;

/**
 * @author brendanhaesler
 */
public class IngestionDatabasePreparerTest extends InputComposerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPopulateDatabasesFromDatFile() throws Exception {
		
		// remove the hospital that was setup in the base class method
		this.hospitalController.deleteHospital(this.hospital);

		// create the preparer
		IngestionDatabasePreparer preparer = new IngestionDatabasePreparer();

		// test check and validate
		Assert.assertTrue(preparer.check(this.environment));
		Assert.assertFalse(preparer.validate(this.environment));

		// test execute
		preparer.execute(this.environment);

		// test check and validate
		Assert.assertFalse(preparer.check(this.environment));
		Assert.assertTrue(preparer.validate(this.environment));

		this.hospital = this.hospitalController.getHospitals().get(0);

		// Departments
		Assert.assertEquals(11, this.hospital.getDepartments().size());

		// Specialist Info
		Assert.assertEquals(1, this.saRepo.getAll().size());
		Assert.assertEquals(4015, this.ingestionController.getLatestRecordBeforeTime(SpecialistAvailability.class, TEST_TIME_FROM).size());

		// Ward Info
		Assert.assertEquals(1, this.hospital.getWards().size());
		Assert.assertEquals(1, this.waRepo.getAll().size());
		Assert.assertEquals(5, this.waRepo.findByTime(TEST_TIME_FROM).getRecords().size());

		// Arriving Patients
		Assert.assertEquals(4030, this.machineLearningController.getArrivingPatients(TEST_TIME_FROM,
						TEST_TIME_TO + 366 * TemporalInputComponent.MS_IN_DAY).size());

		// Initial Patients
		Assert.assertEquals(1, this.ipRepo.getAll().size());
		Assert.assertEquals(1457, this.machineLearningController.getInitialPatients(TEST_TIME_FROM).size());

		// Base Plan
		Assert.assertEquals(1, this.bpRepo.getAll().size());
		Assert.assertEquals(28 * 11, this.ingestionController.getLatestRecordBeforeTime(BasePlanEntry.class, TEST_TIME_FROM).size());

		// Surgery Clusters
		Assert.assertEquals(1, this.scRepo.getAll().size());
		Assert.assertEquals(126, this.machineLearningController.getSurgeryClusters(TEST_TIME_FROM).size());

		// Urgency Categories
		Assert.assertEquals(3, this.hospital.getUrgencyCategories().size());
	}
}
