package com.ibm.au.optim.suro.model.store;


import java.io.InputStream;
import java.util.List;

import com.ibm.au.optim.suro.model.entities.DataSet;

/**
 * This is the interface for the data set repository.
 *
 * @author Peter Ilfrich
 */
public interface DataSetRepository extends Repository<DataSet> {
    /**
     * A {@link String} constant that contains the name of the attribute that will contain
     * the instance of the {@link DataSetRepository} implementation that has been injected
     * into the environment.
     */
    String DATA_SET_REPOSITORY_INSTANCE = "repo:dataset:instance";

    /**
     * A {@link String} constant that contains the name of the parameter that will contain
     * the name of the type of {@link DataSetRepository} that will be used in the application.
     */
    String DATA_SET_REPOSITORY_TYPE = DataSetRepository.class.getName();



    /**
     * Attaches a new document to the data set document. The filename, content type and data (the file as an
     * InputStream) are provided as parameters. Also there is a parameter specifying the model to attach the file to.
     *
     * @param set - The data set instance
     * @param fileName - The file to attach
     * @param contentType - The content type of the attachment
     * @param data - The file content represented as an InputStream
     */
    void attach(DataSet set, String fileName, String contentType, InputStream data);


    /**
     * Retrieves an attachment from the database. The id specifies the document and the filename specifies the attachment.
     * @param id - the ID of the document
     * @param fileName - the name of the attachment
     * @return - an InputStream representing the attachment
     */
    InputStream getAttachment(String id, String fileName);


    /**
     * Retrieves all data sets associated with the provided model
     *
     * @param modelId - the ID of the model
     * @return a list of data sets
     */
    List<DataSet> findByModelId(String modelId);
}
