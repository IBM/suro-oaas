package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Implementation to manage OptimisationModels. Has the capabilities to create and retrieve models and set the default
 * model.
 *
 * @author Peter Ilfrich
 */
public class BasicModelController extends AbstractSuroService implements ModelController {

    public BasicModelController() {

    }


    private static final Logger LOGGER = LoggerFactory.getLogger(BasicModelController.class);

    /**
     * The repository to access the optimisation models
     */
    private ModelRepository repo = null;

    @Override
    protected void doRelease() {
        this.repo = null;
    }

    @Override
    protected void doBind(Environment environment) {
        this.repo = (ModelRepository) environment.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }

    @Override
    public Model getModel(String modelId, boolean loadAttachments) {
        if (modelId == null) {
            return null;
        }

        Model model = repo.getItem(modelId);
        if (model == null) {
            return null;
        } else if (loadAttachments) {
        	
        	List<Attachment> attachments = model.getAttachments();
        	if ((attachments != null) && (attachments.size() > 0)) {
        		for(Attachment attachment : attachments) {
        			
        			String name = attachment.getName();
        			InputStream stream = repo.getAttachment(modelId, name);
        			attachment.store(stream);
        		}
        	}
        	// [CV] NOTE: this was the previous code, we now generalise the logic
        	//            for any type of attachment.
        	//
            // model.setModFile(repo.getAttachment(modelId, Model.FILENAME_MOD_FILE));
            // model.setOpsFile(repo.getAttachment(modelId, Model.FILENAME_OPS_FILE));
        }

        return model;
    }

    @Override
    public Model getDefaultModel() {
        return this.repo.findByDefaultFlag();
    }

    @Override
    public Model createModel(String label, List<Attachment> attachments) {
        // create document
        Model model = new Model(label, false);
        repo.addItem(model);

        for(Attachment attachment : attachments) {
        	// add attachments
        	repo.attach(model, attachment.getName(), attachment.getContentType(), attachment.getStream());
        }
        return model;
    }

    @Override
    public void setDefaultModel(Model model) {
        if (model != null) {
        	

	        LOGGER.debug("Setting default model: " + model.getLabel() + " (" + model.getId() + ")");
	        for (Model mod : repo.getAll()) {
	            if (mod.getId().equals(model.getId())) {
	                mod.setDefaultModel(true);
	                repo.updateItem(mod);
	            } else if (mod.isDefaultModel()) {
	                LOGGER.debug("Removing default flag from model: " + model.getLabel() + " (" + model.getId() + ")");
	                mod.setDefaultModel(false);
	                repo.updateItem(mod);
	            }
	        }
        
        }
    }


    @Override
    public void setRepository(ModelRepository repo) {
        this.repo = repo;
    }

    @Override
    public ModelRepository getRepository() {
        return repo;
    }
}
