package com.ibm.au.optim.suro.model.store;


import java.io.InputStream;

import com.ibm.au.optim.suro.model.entities.Model;

/**
 * This is the interface for the simulation model repository. The implementation will change over time, moving from a
 * static model repository to a database model repository.
 *
 * @author Peter Ilfrich
 */
public interface ModelRepository extends Repository<Model> {
    /**
     * A {@link String} constant that contains the name of the attribute that will contain
     * the instance of the {@link ModelRepository} implementation that has been injected
     * into the environment.
     */
    String OPTIMISATION_MODEL_REPOSITORY_INSTANCE = "repo:model:instance";

    /**
     * A {@link String} constant that contains the name of the parameter that will contain
     * the name of the type of {@link ModelRepository} that will be used in the application.
     */
    String OPTIMISATION_MODEL_REPOSITORY_TYPE = ModelRepository.class.getName();


    /**
     * Attaches a new document to the simulation model document. The filename, content type and data (the file as an
     * InputStream) are provided as parameters. Also there is a parameter specifying the model to attach the file to.
     * @param model - The simulation model instance
     * @param fileName - The file to attach
     * @param contentType - The content type of the attachment
     * @param data - The file content represented as an InputStream
     */
    void attach(Model model, String fileName, String contentType, InputStream data);


    /**
     * Retrieves an attachment from the database. The id specifies the document and the filename specifies the attachment.
     * @param id - the ID of the document
     * @param fileName - the name of the attachment
     * @return - an InputStream representing the attachment
     */
    InputStream getAttachment(String id, String fileName);


    /**
     * Retrieves the model that is marked as default.
     * @return - the default optimisation model
     */
    Model findByDefaultFlag();
}
