package com.ibm.au.optim.suro.core.migration.preparer.v003;

import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Preparer to create the initial optimisation model and dataset, if they don't exist yet.
 * The preparer requires a .mod, .ops and .dat file to be present as resources. They will be used to injest the first
 * model and dataset into the system.
 *
 * @author Peter Ilfrich
 */
public class OptimizationModelDataSetPreparer implements DatabasePreparer {


    /**
     * The repository for the OptimisationModel
     */
    private ModelRepository modelRepo = null;

    /**
     * The repository for the DataSet
     */
    private DataSetRepository setRepo = null;

    private boolean noModels;
    private boolean noSets;

    /**
     * The logger for this preparer
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OptimizationModelDataSetPreparer.class);

    @Override
    public boolean check(Environment env) throws Exception {
        // retrieve repositories
        modelRepo = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
        setRepo = (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);

        // find out if entries are available
        this.noModels = modelRepo.getAll().isEmpty();
        this.noSets = setRepo.getAll().isEmpty();

        boolean noDefaultModel = modelRepo.findByDefaultFlag() == null;

        // return if either of the repositories is empty.
        return noModels || noSets || noDefaultModel;
    }

    @Override
    public void execute(Environment env) throws Exception {
        // initialize the controllers
        ModelController modelController = (ModelController) env.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
        DataSetController setController = (DataSetController) env.getAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE);

        // load resources
        InputStream modFile = this.getClass().getResourceAsStream("/suro/opl/OaaSFirstRun.mod");
        InputStream opsFile = this.getClass().getResourceAsStream("/suro/opl/OaaSFirstRun.ops");
        InputStream datFile = this.getClass().getResourceAsStream("/suro/opl/OaaSFirstRun_v2.dat");
        
        List<Attachment> attachments = new ArrayList<Attachment>();
        
        Attachment scriptMod = new Attachment("script.mod", 0, "text/plain");
        scriptMod.store(modFile);
        attachments.add(scriptMod);
        
        Attachment modelOps = new Attachment("model.ops", 0, "text/plain");
        modelOps.store(opsFile);
        attachments.add(modelOps);

        // handle models
        Model model;
        if (noModels) {
            // create the initial model
            LOGGER.debug("-- Creating default model");
            model = modelController.createModel("Default", attachments);
            
            modelController.setDefaultModel(model);
        } else {
            // load the initial model to create the first data set
            model = modelRepo.getAll().get(0);
            if (!model.isDefaultModel()) {
                LOGGER.debug("-- Setting model as default");
                modelController.setDefaultModel(model);
            } else {
                LOGGER.debug("model " + model.getId() + " is already default: " + model.isDefaultModel());
            }
        }

        // handle data sets
        if (noSets) {
            // create the set
            LOGGER.debug("-- Creating default data set");
            setController.createDataSet(model, "Default", datFile);
        }
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }
}
