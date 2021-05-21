/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.v005;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.TemplateRepository;

/**
 * Class <b>DataModelPreparer</b>. This class implements {@link DatabasePreparer} and loads the version
 * of the new data model. This preparer replaces the previous version of preparers that 
 * 
 * @author Christian Vecchiola
 *
 */
public class DataModelPreparer implements DatabasePreparer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataModelPreparer.class);
	
	
	
	protected boolean noModel = true;
	protected boolean noTemplates = true;
	
	/**
	 * The preparer checks that there is no {@link Model} installed. This is the assumption
	 * for running the execute method and upload the new model data.
	 */
	@Override
	public boolean check(Environment env) throws Exception {
		
		if (env == null) {
			
			throw new IllegalArgumentException("Parameter env cannot be null.");
		}
		
		
		ModelController mc = (ModelController) env.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
		
		if (mc != null) {
		
			Model model = mc.getDefaultModel();
			this.noModel = (model == null);
			
			if (this.noModel == true) {
				
				LOGGER.info("Default model missing, preparer will be executed.");
			
			} else {
				
				LOGGER.info("Default model is present, skipping model update.");
			}
		
		} else {
			
			throw new Exception("Model controller not present.");
		}
		
		TemplateRepository tr = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
		if (tr != null) {
		
			List<Template> templates = tr.getAll(); 
			this.noTemplates = templates.isEmpty();
			
			if (this.noTemplates == true) {
				
				LOGGER.info("Templates are not present, preparer will be executed.");
				
			} else {
				
				LOGGER.info("Templates are up to date.");
			}
		
		} else {
			
			throw new Exception("Templare repository not present.");
		}
		
		return (this.noModel == true) || (this.noTemplates == true);
		
	}


	@Override
	public void execute(Environment env) throws Exception {



		if (noTemplates) {

			ModelController modelController = (ModelController) env.getAttribute(ModelController.MODEL_CONTROLLER_INSTANCE);
			Model defaultModel = modelController.getDefaultModel();

			TemplateRepository tr = (TemplateRepository) env.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
			List<Template> templates = new ObjectMapper().readValue(this.getClass().getResourceAsStream("/migration/0.0.5/package/templates.json"), new TypeReference<List<Template>>() {
			});
			for (Template template : templates) {
				template.setModelId(defaultModel.getId());
				tr.addItem(template);
			}
		}
	

	}

	/* (non-Javadoc)
	 * @see com.ibm.au.optim.suro.model.store.DatabasePreparer#validate(com.ibm.au.jaws.web.core.runtime.Environment)
	 */
	@Override
	public boolean validate(Environment env) throws Exception {
		return !check(env);
	}

}
