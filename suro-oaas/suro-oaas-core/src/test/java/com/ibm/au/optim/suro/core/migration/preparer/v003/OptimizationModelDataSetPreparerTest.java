package com.ibm.au.optim.suro.core.migration.preparer.v003;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.controller.BasicDataSetController;
import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientDataSetRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.jaws.web.core.runtime.Environment;

import junit.framework.TestCase;

/**
 * @author Peter Ilfrich
 */
public class OptimizationModelDataSetPreparerTest extends TestCase {

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSystemInit() throws Exception {

        Environment env = setupEnvironment();
        OptimizationModelDataSetPreparer prep = new OptimizationModelDataSetPreparer();
        Assert.assertTrue(prep.check(env));

        prep.execute(env);

        Assert.assertEquals(1, getOptimizationModelRepository(env).getAll().size());
        Assert.assertEquals(1, getDataSetRepository(env).getAll().size());

        Assert.assertTrue(prep.validate(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSkipExecution() throws Exception {
        Environment env = setupEnvironment();

        getOptimizationModelRepository(env).addItem(new Model("label", true));
        Model model = getOptimizationModelRepository(env).findByDefaultFlag();
        getDataSetRepository(env).addItem(new DataSet("set-label", model.getId(), null));

        OptimizationModelDataSetPreparer prep = new OptimizationModelDataSetPreparer();
        Assert.assertFalse(prep.check(env));

        prep.execute(env);
        Assert.assertEquals(1, getDataSetRepository(env).getAll().size());
        Assert.assertEquals(1, getOptimizationModelRepository(env).getAll().size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testSetDefaultModel() throws Exception {
        Environment env = setupEnvironment();
        getOptimizationModelRepository(env).addItem(new Model("label", false));

        Assert.assertNull(getOptimizationModelRepository(env).findByDefaultFlag());
        OptimizationModelDataSetPreparer prep = new OptimizationModelDataSetPreparer();
        Assert.assertTrue(prep.check(env));

        prep.execute(env);

        Assert.assertEquals(1, getOptimizationModelRepository(env).getAll().size());
        Assert.assertEquals(1, getDataSetRepository(env).getAll().size());
        Assert.assertNotNull(getOptimizationModelRepository(env).findByDefaultFlag());

        for (DataSet ds : getDataSetRepository(env).getAll()) {
            Assert.assertEquals(getOptimizationModelRepository(env).findByDefaultFlag().getId(), ds.getModelId());
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidationFailed() throws Exception {
        Environment env = setupEnvironment();

        getOptimizationModelRepository(env).addItem(new Model("label", false));
        Model model = getOptimizationModelRepository(env).getAll().get(0);
        getDataSetRepository(env).addItem(new DataSet("set-label", model.getId(), null));

        OptimizationModelDataSetPreparer prep = new OptimizationModelDataSetPreparer();
        Assert.assertFalse(prep.validate(env));
    }







    /**
     * 
     * @return
     */
    private Environment setupEnvironment() {
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);

        // create repos
        DataSetRepository dsRepo = new TransientDataSetRepository();
        ModelRepository modelRepo = new TransientModelRepository();
        // store repos
        env.setAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE, dsRepo);
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, modelRepo);
        // create controllers and bind repos
        ModelController modelControl = new BasicModelController();
        modelControl.setRepository(modelRepo);
        DataSetController dsControl = new BasicDataSetController();
        dsControl.setRepository(dsRepo);

        // store controllers
        env.setAttribute(ModelController.MODEL_CONTROLLER_INSTANCE, modelControl);
        env.setAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE, dsControl);

        return env;
    }

    private DataSetRepository getDataSetRepository(Environment env) {
        return (DataSetRepository) env.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
    }

    private ModelRepository getOptimizationModelRepository(Environment env) {
        return (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }
}
