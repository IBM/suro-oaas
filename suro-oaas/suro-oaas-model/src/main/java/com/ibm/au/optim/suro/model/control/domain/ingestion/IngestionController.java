package com.ibm.au.optim.suro.model.control.domain.ingestion;

import java.util.List;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;

/**
 * This interface is responsible for handling the data ingestion logic. The IngestionApi will call the controller to add
 * new records and retrieve the latest records for the {@link }.
 * @author Brendan Haesler
 */
public interface IngestionController {

	/**
	 * The name of the key storing the ingestion controller in the environment
	 */
	String INGESTION_CONTROLLER_INSTANCE = "controller:ingestion:instance";

	/**
	 * The type of the class implementing the ingestion controller
	 */
	String INGESTION_CONTROLLER_TYPE = IngestionController.class.getName();

	/**
	 * Creates a new entry in the repository responsible for managing objects of the passed type. It will store the list
	 * of records in a list object and return this list object after creating it.
	 *
	 * @param timestamp - the timestamp for the new data
	 * @param records - the records representing the data
	 * @param <R> - the type of the list
	 * @param <T> - the type of the object inside the list
	 * @return - a list of records, which has been added to the repository.
	 */
	<R, T extends TemporalList<R>> T createRecordFromList(long timestamp, List<R> records);

	/**
	 * Retrieves the latest records from the responsible repository. The time parameter can be used to make sure that
	 * the records retrieved have a newer timestamp than the timestamp provided.
	 * @param recordType - the type of record (e.g. BasePlanEntry.class)
	 * @param time - the minimum timestamp of any records that are returned. Pass 0 to get any record that the
	 *                repository contains
	 * @param <T> - the type of entry / record that is contained in the returned list (e.g. BasePlanEntry.class)
	 * @return - a list of single records, which is newer than time.
	 */
	<T> List<T> getLatestRecordBeforeTime(Class<T> recordType, long time);
}
