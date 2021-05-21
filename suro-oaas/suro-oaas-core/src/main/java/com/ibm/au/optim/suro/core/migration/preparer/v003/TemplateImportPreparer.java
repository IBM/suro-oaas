package com.ibm.au.optim.suro.core.migration.preparer.v003;

import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Initial strategy import preparer. Imports the first 4 strategies into the strategy repository of an empty system.
 * The initial strategies are stored in the initialStrategies.properties file (see implementation for changes).
 *
 * The strategies imported will not be connected to any model as at the time of the creation of this preparer, the data
 * model was not updated yet (adding the modelId to the {@link Strategy})
 *
 * @author Peter Ilfrich
 */
public class TemplateImportPreparer implements DatabasePreparer {

    /**
     * The logger for this preparer
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateImportPreparer.class);

    /**
     * The database repository for the strategies
     */
    private TemplateRepository repository;

    /**
     * 
     */
    private ModelRepository modelRepository;


    /**
     * 
     */
    private List<Template> missingModelIds = new ArrayList<>();

	    
	/**
	 * 
	 */
    private boolean noTemplates = false;
    /**
     * 
     */
    private boolean noModelId = false;

    /**
     * 
     */
    @Override
    public boolean check(Environment env) throws Exception {
        this.repository = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
        this.modelRepository = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);

        // only execute if no strategies are available (or they are not assigned to a model)
        this.noTemplates = (this.repository.getAll().isEmpty());
        this.noModelId = false;

        for (Template template : this.repository.getAll()) {
            if (template.getModelId() == null) {
                noModelId = true;
                missingModelIds.add(template);
            }
        }

        return noTemplates || noModelId;
    }

    /**
     * 
     */
    @Override
    public void execute(Environment env) throws Exception {
        // retrieve default model
        Model defaultModel = modelRepository.findByDefaultFlag();

        // abort if there's no default model
        if (defaultModel == null) {
            LOGGER.error("Retrieval of default model failed. Please ensure the corresponding views exist and there is" +
                    "a model in the system with the default flag set to true.");
            return;
        }


        if (noTemplates) {
            // load properties
            InputStream propStream = this.getClass().getResourceAsStream("/migration/0.0.3/initialStrategies.properties");
            Properties props = new Properties();
            props.load(propStream);

            int i = 0;

            while (props.getProperty("strategy." + i + ".title") != null) {
                // access current strategy properties
                String prefix = "strategy." + i + ".";
                // don't embed the model ID yet.

                String modelId = defaultModel.getId();
                // init empty lists for assumptions and criteria


                // create the strategy

                Template template = new Template();
                template.setLabel(props.getProperty(prefix + "title"));
                template.setDescription(props.getProperty(prefix + "description"));
                template.setModelId(modelId);
                LOGGER.debug("-- Creating strategy " + template.getLabel());
                this.repository.addItem(template);

                // proceed with the next strategy if there is any
                i++;
            }
        }


        if (noModelId) {
            // set the model Id for strategies that are missing a model id
            LOGGER.debug("-- Setting model ID for " + missingModelIds.size() + " strategies.");
            for (Template template : missingModelIds) {
                template.setModelId(defaultModel.getId());
                repository.updateItem(template);
            }
        }
    }

    /**
     * 
     */
    @Override
    public boolean validate(Environment env) throws Exception {
    	
        return (this.check(env) == false);
    }
}
