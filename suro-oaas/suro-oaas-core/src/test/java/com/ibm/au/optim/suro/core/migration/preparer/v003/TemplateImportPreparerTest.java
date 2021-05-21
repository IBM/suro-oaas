package com.ibm.au.optim.suro.core.migration.preparer.v003;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientRunRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientTemplateRepository;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * @author Peter Ilfrich
 */
public class TemplateImportPreparerTest {


    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCheckFailed() throws Exception {
        Environment env = setupEnvironment();

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertTrue(prep.check(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCheckPassed() throws Exception {
        Environment env = setupEnvironment();
        Template t = new Template();
        t.setModelId(getOptimizationModelRepository(env).findByDefaultFlag().getId());
        this.getTemplateRepository(env).addItem(t);

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertFalse(prep.check(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception  {
        Environment env = setupEnvironment();

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertTrue(prep.check(env));

        prep.execute(env);
        Assert.assertEquals(4, this.getTemplateRepository(env).getAll().size());

        Assert.assertTrue(prep.validate(env));
    }


    /**
     * 
     * @throws Exception
     */
    @Test
    public void testPassExecute() throws Exception {
        Environment env = setupEnvironment();

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertTrue(prep.check(env));

        prep.execute(env);
        Assert.assertEquals(4, this.getTemplateRepository(env).getAll().size());

        Assert.assertFalse(prep.check(env));

        prep.execute(env);
        Assert.assertEquals(4, this.getTemplateRepository(env).getAll().size());
        Assert.assertTrue(prep.validate(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testNoDefaultModel() throws Exception {
        Environment env = setupEnvironment();
        this.getOptimizationModelRepository(env).removeAll();

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertTrue(prep.check(env));

        prep.execute(env);
        Assert.assertEquals(0, this.getTemplateRepository(env).getAll().size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidateFailed() throws Exception  {
        Environment env = setupEnvironment();

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertFalse(prep.validate(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidatePassed() throws Exception  {
        Environment env = setupEnvironment();

        Template t = new Template();
        t.setModelId(getOptimizationModelRepository(env).findByDefaultFlag().getId());
        this.getTemplateRepository(env).addItem(t);

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertTrue(prep.validate(env));
    }
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testAlignStrategies() throws Exception {
        Environment env = setupEnvironment();
        TemplateRepository tr = this.getTemplateRepository(env);
        tr.addItem(new Template());
        tr.addItem(new Template());
        tr.addItem(new Template());

        TemplateImportPreparer prep = new TemplateImportPreparer();
        Assert.assertTrue(prep.check(env));

        prep.execute(env);
        Model model = this.getOptimizationModelRepository(env).findByDefaultFlag();
        for (Template t : this.getTemplateRepository(env).getAll()) {
            Assert.assertEquals(model.getId(), t.getModelId());
        }

        Assert.assertTrue(prep.validate(env));
    }





    /**
     * 
     * @return
     */
    private Environment setupEnvironment() {
    	
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);

        ModelRepository repo = new TransientModelRepository();
        Model model = new Model("model");
        model.setDefaultModel(true);
        model.setLabel("Model");
        repo.addItem(model);
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, repo);
        env.setAttribute(RunRepository.RUN_REPOSITORY_INSTANCE, new TransientRunRepository());

        TransientTemplateRepository templateRepository = new TransientTemplateRepository();
        env.setAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE, templateRepository);
        templateRepository.bind(env);

        return env;
    }

    /**
     * 
     * @param env
     * @return
     */
    private TemplateRepository getTemplateRepository(Environment env) {
        return (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
    }

    private ModelRepository getOptimizationModelRepository(Environment env) {
        return (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }
}
