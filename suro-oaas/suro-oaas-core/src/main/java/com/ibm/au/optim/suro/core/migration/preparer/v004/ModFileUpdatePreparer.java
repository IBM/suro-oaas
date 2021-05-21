package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.*;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.apache.http.entity.ContentType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This preparer will check the mod file that is uploaded for each optimisation model and check if there is a newer
 * model specified in the source code. This allows us to update the mod file in the source code, re-deploy and have
 * the test server updated as well.
 *
 *
 * Currently the preparer also handles the dat files attached to the data set - once the data ingestion and optimisation
 * data composer is finished, this functionality can be removed.
 *
 * @author Peter Ilfrich
 */
public class ModFileUpdatePreparer implements DatabasePreparer {
	
	/**
	 * A {@link String} constant that maps the path in the resources folder of the
	 * file containing the implementation of the optimisation model.
	 */
	public static final String MODEL_PATH = "migration/0.0.4/model/optimization-model-script.mod";
	/**
	 * A {@link String} constant that maps the path in the resources folder of the
	 * file containing the pre-compiled dataset to be used for the submission of
	 * optimisation runs.
	 */
	public static final String DATASET_PATH = "migration/0.0.4/model/optimization-model-data.dat";

    /**
     * The current model version in the database. Increase whenever you want the servers to update to the latest file.
     */
    private static final int CURRENT_MODEL_VERSION = 4;

    /**
     * Works similar as above (just for the dat file) - deprecated due to the new dat file composer, which will replace
     * the dat file in the database (data set) / or at least avoid the need to update the dat file.
     * @deprecated
     */
    @Deprecated
    private static final int CURRENT_DAT_FILE_VERSION = 2;

    /**
     * A {@link List} containing the {@link DataSet} instances that need update. These are collected from the
     * corresponding repository by selecting all those instances whose version number is lower than the value
     * of {@link ModFileUpdatePreparer#CURRENT_DAT_FILE_VERSION}.
     */
    @Deprecated
    private List<DataSet> dataSetsNeedUpdate;

    /**
     * A {@link List} containing the {@link Model} instances that need update. These are collected from the
     * corresponding repository by selecting all those instances whose version number is lower than the value
     * of {@link ModFileUpdatePreparer#CURRENT_MODEL_VERSION}.
     */
    private List<Model> modelsNeedUpdate;

    /**
     * This method checks whether the current data sets stored in the 
     * repository are of the same version of the one currently used by
     * the library. If the version number is lower than the one used by
     * the library the dataset will be flagged for update. The same
     * applies for the optimisation model.
     * 
     * @param env	a {@link Environment} instance that is used to access
     * 				the configuration settings for the application.
     * 
     * @return	{@literal true} if there is either a data set or a model
     * 			to update, {@literal false} otherwise.
     * 
     */
    @SuppressWarnings("deprecation")
	@Override
    public boolean check(Environment env) throws Exception {


        this.modelsNeedUpdate = new ArrayList<>();
        for (Model model : getModelRepo(env).getAll()) {
            if (model.getModelVersion() < CURRENT_MODEL_VERSION) {
                this.modelsNeedUpdate.add(model);
            }
        }

        this.dataSetsNeedUpdate = new ArrayList<>();
        for (DataSet set : getDataSetRepo(env).getAll()) {
            if (set.getDatFileVersion() < CURRENT_DAT_FILE_VERSION) {
                dataSetsNeedUpdate.add(set);
            }
        }

        return ((this.dataSetsNeedUpdate.size() > 0) || (this.modelsNeedUpdate.size() > 0));
    }

    /**
     * This method iterates over all the {@link DataSet} and {@link Model} instances
     * that have been collected during the check phase and updates the corresponding attachments
     * (DAT and MOD files) with those that are in the resources of the library.
     * 
     * @param env	a {@link Environment} instance that is used to access the configuration settings 
     * 				for the application.
     * 
     */
    @SuppressWarnings("deprecation")
	@Override
    public void execute(Environment env) throws Exception {
    	
    	
    	ClassLoader loader = this.getClass().getClassLoader();
    	DataSetRepository dataSetRepo = this.getDataSetRepo(env);

        // handle data sets - TODO: remove this when the data set is reworked (composer integrated)
        InputStream datFile = loader.getResourceAsStream(ModFileUpdatePreparer.DATASET_PATH);
        for (DataSet set : this.dataSetsNeedUpdate) {
        	dataSetRepo.attach(set, DataSet.FILENAME_DAT_FILE, ContentType.TEXT_PLAIN.getMimeType(), datFile);
            set.setDatFileVersion(CURRENT_DAT_FILE_VERSION);
            dataSetRepo.updateItem(set);
        }
        
        ModelRepository modelRepo = this.getModelRepo(env);

        // handle model dat files
        InputStream modFile = loader.getResourceAsStream(ModFileUpdatePreparer.MODEL_PATH);
        for (Model model : modelsNeedUpdate) {
            modelRepo.attach(model, "script.mod", ContentType.TEXT_PLAIN.getMimeType(), modFile);
            model.setModelVersion(CURRENT_MODEL_VERSION);
            modelRepo.updateItem(model);
        }
    }

    /**
     * This method validates that the changes have been applied. The validation
     * is performed by invoking {@link ModFileUpdatePreparer#check(Environment)}
     * and verifying that its return value is set to {@literal false}
     * 
     * @param env	a {@link Environment} instance that is used to access
     * 				the configuration settings for the application.
     * 
     * @return 	{@literal true} if the changes have been applied properly or 
     * 			{@literal false} otherwise.
     */
    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }


    /**
     * Retrieves the repository storing the information about the {@link DataSet} instances
     * used to keep the information about the pre-baked data for the optimization.
     * @deprecated
     * 
     * @param env	a {@link Environment} instance that is used to access
     * 				the configuration settings for the application.
     * 
     * @return	the {@link DataSetRepository} instance providing access to the persistence
     * 			layer where the dataset instances are managed.
     */
    @Deprecated
    protected DataSetRepository getDataSetRepo(Environment env) {
        return (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
    }

    /**
     * Retrieves the repository storing the information about the {@link Model}
     * instances. The method collects the current instance from the given <i>env</i>.
     * 
     * @param env	a {@link Environment} instance that is used to access
     * 				the configuration settings for the application.
     * 
     * @return 	the current {@link ModelRepository} instance that is used to 
     * 			interact with the model instances stored in the repository.
     */
    protected ModelRepository getModelRepo(Environment env) {
        return (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }
}
