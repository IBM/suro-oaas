package com.ibm.au.optim.suro.model.control;

import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.DataSetRepository;

import java.io.InputStream;

/**
 * Interface for providing the dat file.
 *
 * @author Peter Ilfrich
 */
public interface DataSetController {

    /**
     * The environment attribute name used to store the controller instance
     */
    String DATA_SET_CONTROLLER_INSTANCE = "controller:dataset:instance";

    /**
     * The implementing class name of the DataSetController
     */
    String DATFILE_PROVIDER_TYPE = DataSetController.class.getName();


    /**
     * Creates a new data set in the repository with the specified data.
     * @param model - the model the data set belongs to
     * @param label - the label of the data set
     * @param datFile - an input stream representing the content of the dat file.
     * @return - the data set store object
     */
    DataSet createDataSet(Model model, String label, InputStream datFile);

    /**
     * Retrieves the requested data set from the repository and additionally also loads the dat file if requested.
     * @param datasetId - the id of the data set
     * @param loadData - a boolean flag indicating if the dat file should be loaded as well or not (separate call to
     *                 retrieve attachment)
     * @return - the data set as retrieved from the repository
     */
    DataSet getDataSet(String datasetId, boolean loadData);

    /**
     * Retrieves the currently configured data set repository
     * @return - the current data set repository
     */
    DataSetRepository getRepository();

    /**
     * Sets the current data set repository for this controller.
     * @param repo - the new data set repository used for this controller.
     */
    void setRepository(DataSetRepository repo);

}
