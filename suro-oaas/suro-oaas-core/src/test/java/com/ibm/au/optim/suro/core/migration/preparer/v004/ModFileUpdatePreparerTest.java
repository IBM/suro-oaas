package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientDataSetRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;

import org.junit.Test;

import java.util.Properties;

import org.junit.Assert;

import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * @author Peter Ilfrich
 */
public class ModFileUpdatePreparerTest {

    /**
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings("deprecation")
	public void testCheck() throws Exception {
    	
        Environment env = this.createEnvironment();
        ModFileUpdatePreparer preparer = new ModFileUpdatePreparer();

        Assert.assertFalse(preparer.check(env));

        ModelRepository modelRepo = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
        DataSetRepository dataSetRepo = (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);

        Model model = new Model("model");
        modelRepo.addItem(model);

        Assert.assertTrue(preparer.check(env));

        model.setModelVersion(999999);
        modelRepo.updateItem(model);
        Assert.assertFalse(preparer.check(env));

        DataSet set = new DataSet();
        dataSetRepo.addItem(set);

        Assert.assertTrue(preparer.check(env));

        set.setDatFileVersion(999999);
        dataSetRepo.updateItem(set);
        Assert.assertFalse(preparer.check(env));
    }


    /**
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings("deprecation")
	public void testValidate() throws Exception {
    	
        Environment env = this.createEnvironment();
        ModFileUpdatePreparer preparer = new ModFileUpdatePreparer();

        Assert.assertTrue(preparer.validate(env));

        ModelRepository modelRepo = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
        DataSetRepository dataSetRepo = (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);

        Model model = new Model("model");
        modelRepo.addItem(model);

        Assert.assertFalse(preparer.validate(env));

        model.setModelVersion(999999);
        modelRepo.updateItem(model);
        Assert.assertTrue(preparer.validate(env));

        DataSet set = new DataSet();
        dataSetRepo.addItem(set);

        Assert.assertFalse(preparer.validate(env));

        set.setDatFileVersion(999999);
        dataSetRepo.updateItem(set);
        Assert.assertTrue(preparer.validate(env));
    }


    /**
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings("deprecation")
	public void testExecute() throws Exception {
    	
        // preparation
        Environment env = this.createEnvironment();
        ModFileUpdatePreparer preparer = new ModFileUpdatePreparer();
        ModelRepository modelRepo = (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
        DataSetRepository dataSetRepo = (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);

        // make sure the lists are initiated
        Assert.assertFalse(preparer.check(env));
        preparer.execute(env);
        Assert.assertEquals(0, dataSetRepo.getAll().size());
        Assert.assertEquals(0, modelRepo.getAll().size());

        modelRepo.addItem(new Model("model"));
        dataSetRepo.addItem(new DataSet());

        Assert.assertTrue(preparer.check(env));
        preparer.execute(env);
        Assert.assertEquals(1, dataSetRepo.getAll().size());
        Assert.assertEquals(1, modelRepo.getAll().size());

        Model model = modelRepo.getAll().get(0);
        DataSet set = dataSetRepo.getAll().get(0);

        Assert.assertNotNull(modelRepo.getAttachment(model.getId(), "script.mod"));
        Assert.assertNotNull(dataSetRepo.getAttachment(set.getId(), DataSet.FILENAME_DAT_FILE));

        int currentModVersion = model.getModelVersion();
        int currentDatVersion = set.getDatFileVersion();

        model.setModelVersion(currentModVersion - 1);
        modelRepo.updateItem(model);
        set.setDatFileVersion(currentDatVersion - 1);
        dataSetRepo.updateItem(set);

        // fix the numbers
        Assert.assertTrue(preparer.check(env));
        preparer.execute(env);
        Assert.assertEquals(1, dataSetRepo.getAll().size());
        Assert.assertEquals(1, modelRepo.getAll().size());

        model = modelRepo.getAll().get(0);
        set = dataSetRepo.getAll().get(0);
        Assert.assertEquals(currentModVersion, model.getModelVersion());
        Assert.assertEquals(currentDatVersion, set.getDatFileVersion());

        // use higher version
        model.setModelVersion(currentModVersion + 2);
        modelRepo.updateItem(model);
        set.setDatFileVersion(currentDatVersion + 2);
        dataSetRepo.updateItem(set);

        Assert.assertFalse(preparer.check(env));
        model = modelRepo.getAll().get(0);
        set = dataSetRepo.getAll().get(0);

        Assert.assertEquals(currentDatVersion + 2, set.getDatFileVersion());
        Assert.assertEquals(currentModVersion + 2, model.getModelVersion());

        // use matching version
        model.setModelVersion(currentModVersion);
        modelRepo.updateItem(model);
        set.setDatFileVersion(currentDatVersion);
        dataSetRepo.updateItem(set);

        Assert.assertFalse(preparer.check(env));
        preparer.execute(env);
        model = modelRepo.getAll().get(0);
        set = dataSetRepo.getAll().get(0);

        Assert.assertEquals(currentDatVersion, set.getDatFileVersion());
        Assert.assertEquals(currentModVersion, model.getModelVersion());
    }

    /**
     * Sets up the environment with a model and dataset repository.
     * @return - the environment
     */
    private Environment createEnvironment() {
    	
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);

        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, new TransientModelRepository());
        env.setAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE, new TransientDataSetRepository());

        return env;
    }
}
