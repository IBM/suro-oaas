package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.ModelParameter;
import com.ibm.au.optim.suro.model.entities.Objective;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Ilfrich
 */
public class OptimizationModelAndTemplateMetaExtensionPreparer implements DatabasePreparer {

    
	private static final String MODEL_PARAMETERS_PATH 	= 	"/migration/0.0.4/model/optimization-model-parameters.json";
	private static final String MODEL_OBJECTIVES_PATH	=	"/migration/0.0.4/model/optimization-model-objectives.json";
	private static final String MODEL_MAPPINGS_PATH		=	"/migration/0.0.4/model/optimization-model-mappings.json";
	private static final String MODEL_TEMPLATES_PATH 	= 	"/migration/0.0.4/model/optimization-model-templates.json";
	
	
	private List<String> modelWithoutOutputMappings;
    private List<String> modelWithoutObjectives;
    private List<String> modelWithoutParameters;
    private List<String> templateWithoutParameters;
    
    


    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean check(Environment env) throws Exception {
        
    	this.modelWithoutOutputMappings = new ArrayList<>();
        this.modelWithoutObjectives = new ArrayList<>();
        this.modelWithoutParameters = new ArrayList<>();

        this.templateWithoutParameters = new ArrayList<>();


        // check models
        for (Model model : getModelRepository(env).getAll()) {
            if (model.getOutputMappings() == null) {
                this.modelWithoutOutputMappings.add(model.getId());
            }

            if (model.getObjectives() == null) {
                this.modelWithoutObjectives.add(model.getId());
            }

            if (model.getParameters() == null) {
                this.modelWithoutParameters.add(model.getId());
            }
        }

        // check strategies
        for (Template strategy : getTemplateRepository(env).getAll()) {


            if (strategy.getParameters() == null) {
                this.templateWithoutParameters.add(strategy.getId());
            }
        }

        // [CV] NOTE: at least one is not empty.
        //
        return ((this.modelWithoutOutputMappings.isEmpty() == false) 	||
        		(this.modelWithoutObjectives.isEmpty() == false) 		|| 
        		(this.modelWithoutParameters.isEmpty() == false) 		|| 
        		(this.templateWithoutParameters.isEmpty() == false));
    }

    @Override
    public void execute(Environment env) throws Exception {
        // read initial output mapping (compatible with legacy web UI)
        List<OutputMapping> outputMappings = MAPPER.readValue(this.getClass().getResourceAsStream(OptimizationModelAndTemplateMetaExtensionPreparer.MODEL_MAPPINGS_PATH), 
        													  new TypeReference<List<OutputMapping>>() {});
        
        ModelRepository repo = this.getModelRepository(env);

        // create output mapping for models that don't have them yet
        for (String modelId : this.modelWithoutOutputMappings) {
        	
            Model model = repo.getItem(modelId);
            model.setOutputMappings(outputMappings);
            repo.updateItem(model);
        }

        // read initial objective definition
        List<Objective> objectives = MAPPER.readValue(this.getClass().getResourceAsStream(OptimizationModelAndTemplateMetaExtensionPreparer.MODEL_OBJECTIVES_PATH), 
        											  new TypeReference<List<Objective>>() { });

        // create objectives for models that don't have them yet
        for (String modelId : this.modelWithoutObjectives) {
            Model model = repo.getItem(modelId);
            model.setObjectives(objectives);
            repo.updateItem(model);
        }

        // read the predefined strategy input parameter
        List<ModelParameter> modelParameters = MAPPER.readValue(this.getClass().getResourceAsStream(OptimizationModelAndTemplateMetaExtensionPreparer.MODEL_PARAMETERS_PATH), 
        														new TypeReference<List<ModelParameter>>() { });

        // create available input parameter (name, description, identifier) @ the model
        for (String modelId: this.modelWithoutParameters) {
            Model model = getModelRepository(env).getItem(modelId);
            model.setParameters(modelParameters);
            repo.updateItem(model);
        }

        // load strategy data
        List<Template> templates = MAPPER.readValue(this.getClass().getResourceAsStream(OptimizationModelAndTemplateMetaExtensionPreparer.MODEL_TEMPLATES_PATH), 
        														 new TypeReference<List<Template>>() { });

        TemplateRepository templateRepo = this.getTemplateRepository(env);
        
        // process available input parameters for strategies (string list)
        for (String templateId : this.templateWithoutParameters) {
            Template template = templateRepo.getItem(templateId);
            for (Template t : templates) {
                if (t.getLabel().equals(template.getLabel())) {
                    template.setParameters(t.getParameters());
                    templateRepo.updateItem(template);
                }
            }
        }


    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }


    private ModelRepository getModelRepository(Environment env) {
    	
        return (ModelRepository) env.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
    }

    private TemplateRepository getTemplateRepository(Environment env) {
    	
        return (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
    }
}
