package com.ibm.au.optim.suro.model.control;

import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.store.ModelRepository;

import java.util.List;

/**
 * Controller interface to manage OptimisationModels.
 *
 * @author Peter Ilfrich
 */
public interface ModelController {


    /**
     * The namespace where the environment stores the instance of this controller.
     */
	String MODEL_CONTROLLER_INSTANCE = "controller:model:instance";

    /**
     * The type implementing this controller instance.
     */
    String MODEL_CONTROLLER_TYPE = ModelController.class.getName();


    /**
     * Retrieves a model from the repository. The method has an option to also retrieve / load the attachment (the
     * model script file). This should only be done if the model file is required (e.g. for run execution).
     * @param modelId - the ID of the model to retrieve
     * @param loadAttachments - a flag determining if the mod file (script) should be loaded as well.
     * @return - the model instance with or without attachments (mod file, dat file) depending on the parameter
     */
    Model getModel(String modelId, boolean loadAttachments);

    /**
     * Retrieves the current default model.
     * @return - the default optimization model
     */
    Model getDefaultModel();


    /**
     * Creates a new optimisation model instance and adds the document to the storage.
     * @param label - the label of the new model
     * @return a new optimisation model instance
     */
    Model createModel(String label, List<Attachment> attachments);

    /**
     * Adjusts the default flag for optimisation models, replacing the currently marked model with the provided one.
     * @param model - the model which will be the new default model
     */
    void setDefaultModel(Model model);


    /**
     * Provides a new repository for this controller instance.
     * @param repo - the new repository
     */
    void setRepository(ModelRepository repo);

    /**
     * Retrieves the current repository for the optimization model persistent object
     * @return - the repository currently used to store optimization models
     */
    ModelRepository getRepository();



}


