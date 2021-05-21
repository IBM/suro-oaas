package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.ModelParameter;
import com.ibm.au.optim.suro.model.entities.Objective;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.entities.TemplateParameter;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientModelRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientRunRepository;
import com.ibm.au.optim.suro.model.store.impl.TransientTemplateRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Peter Ilfrich
 */
public class OptimizationModelAndTemplateMetaExtensionPreparerTest {

	/**
	 * 
	 */
    private OptimizationModelAndTemplateMetaExtensionPreparer preparer = new OptimizationModelAndTemplateMetaExtensionPreparer();

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCheckWithExistingData() throws Exception {
        Environment env = setupEnvironment();

        Model model = new Model("model");
        ModelRepository mr = this.getModelRepo(env); 
        mr.addItem(model);
        
        Template template = new Template();
        TemplateRepository tr = this.getTemplateRepo(env);
        tr.addItem(template);

        List<TemplateParameter> templateParams = new ArrayList<TemplateParameter>();
        List<ModelParameter> modelParams = new ArrayList<ModelParameter>();
        List<Objective> objectives = new ArrayList<Objective>();
        List<OutputMapping> mappings = new ArrayList<OutputMapping>();

        model.setParameters(modelParams);
        model.setObjectives(objectives);
        model.setOutputMappings(mappings);
        template.setParameters(templateParams);

        mr.updateItem(model);
        tr.updateItem(template);

        Assert.assertFalse(this.preparer.check(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCheckWithoutData() throws Exception {
    	
        Environment env = setupEnvironment();

        Model model = new Model("m1");
        ModelRepository mr = this.getModelRepo(env);
        mr.addItem(model);
        
        Template template = new Template();
        TemplateRepository tr = this.getTemplateRepo(env);
        tr.addItem(template);

        List<ModelParameter> modelParams = new ArrayList<ModelParameter>();
        List<Objective> objectives = new ArrayList<Objective>();
        List<TemplateParameter> templateParams = new ArrayList<TemplateParameter>();
        List<OutputMapping> mappings = new ArrayList<>();

        Assert.assertTrue(this.preparer.check(env));

        model.setParameters(modelParams);
        mr.updateItem(model);
        Assert.assertTrue(this.preparer.check(env));

        model.setObjectives(objectives);
        mr.updateItem(model);
        Assert.assertTrue(this.preparer.check(env));

        model.setOutputMappings(mappings);
        mr.updateItem(model);
        Assert.assertTrue(this.preparer.check(env));

        template.setParameters(templateParams);
        this.getTemplateRepo(env).updateItem(template);
        Assert.assertFalse(this.preparer.check(env));


        // reverse
        model.setParameters(null);
        mr.updateItem(model);
        Assert.assertTrue(this.preparer.check(env));

        model.setObjectives(null);
        mr.updateItem(model);
        Assert.assertTrue(this.preparer.check(env));

        model.setOutputMappings(null);
        mr.updateItem(model);
        Assert.assertTrue(this.preparer.check(env));

        template.setParameters(null);
        tr.updateItem(template);
        Assert.assertTrue(this.preparer.check(env));

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidate() throws Exception {
    	
        Environment env = setupEnvironment();

        Model model = new Model("m1");
        ModelRepository mr = this.getModelRepo(env);
        mr.addItem(model);
        
        Template template = new Template();
        TemplateRepository tr = this.getTemplateRepo(env);
        tr.addItem(template);

        List<TemplateParameter> templateParams = new ArrayList<TemplateParameter>();
        List<Objective> objectives = new ArrayList<Objective>();
        List<OutputMapping> mappings = new ArrayList<OutputMapping>();
        List<ModelParameter> modelParams = new ArrayList<ModelParameter>();

        model.setParameters(modelParams);
        model.setObjectives(objectives);
        model.setOutputMappings(mappings);
        template.setParameters(templateParams);

        mr.updateItem(model);
        tr.updateItem(template);

        Assert.assertTrue(this.preparer.validate(env));

        model.setObjectives(null);
        template.setParameters(null);
        mr.updateItem(model);
        tr.updateItem(template);

        Assert.assertFalse(this.preparer.validate(env));
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception {
        Environment env = setupEnvironment();

        Model model = new Model("label");
        ModelRepository mr = this.getModelRepo(env);
        mr.addItem(model);
        
        Template template = new Template();
        template.setLabel("Maximize the performance of the hospital");
        TemplateRepository tr = this.getTemplateRepo(env);
        tr.addItem(template);

        Assert.assertTrue(this.preparer.check(env));
        this.preparer.execute(env);

        model = mr.getItem(model.getId());
        template = tr.getItem(template.getId());

        Assert.assertEquals(8, model.getParameters().size());
        Assert.assertEquals(8, model.getObjectives().size());
        Assert.assertEquals(9, model.getOutputMappings().size());
        Assert.assertEquals(5, template.getParameters().size());
    }





    /**
     * 
     * @return
     */
    private Environment setupEnvironment() {
    	
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, new TransientModelRepository());
        env.setAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE, new TransientTemplateRepository());
        env.setAttribute(RunRepository.RUN_REPOSITORY_INSTANCE, new TransientRunRepository());

        ((TransientTemplateRepository) getTemplateRepo(env)).bind(env);
        return env;
    }

    /**
     * 
     * @param env
     * @return
     */
    private ModelRepository getModelRepo(Environment env) {
        return (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }

    /**
     * 
     * @param env
     * @return
     */
    private TemplateRepository getTemplateRepo(Environment env) {
        return (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
    }

}
