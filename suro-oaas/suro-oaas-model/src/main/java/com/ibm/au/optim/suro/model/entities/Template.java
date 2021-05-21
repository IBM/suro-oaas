/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.au.jaws.data.DataValidationException;
/**
 * <p>
 * Class <b>RunTemplate</b>. This class extends {@link Entity} and provides the
 * capability of packaging configuration settings and constraints that are applied
 * to an optimization model to run an optimization. A template contains a subset
 * of the parameters made available with the model and default settings for some
 * parameters in order to facilitate the creation of an optimization and steer the
 * behaviour of the algorithm towards, for instance the preference of some objectives.
 * </p>
 * <p>
 * Run templates are prepared by people knowledgeable of the algorithm implemented
 * in the optimization model and therefore capable of generating sensible settings
 * to enforce a certain behaviour.
 * </p>
 * <p>
 * A {@link Template} instance contains the following key elements:
 * <ul>
 * <li>a reference to the optimization model it has been prepared for via the
 * <i>modelId</i> attribute.</li>
 * <li>a collection of template parameters which are a subset of the original
 * parameters of the model, with default settings and additional attributes that
 * control whether these parameters are editable or not.</li>
 * <li><i>title</i> and <i>description</i> which provide information about the
 * characteristics of the template as well as its behaviour.</li>
 * </ul>
 * </p>
 * 
 * @author Christian Vecchiola.
 *
 */
public class Template extends Entity {
	
	/**
	 * A {@link String} containing a short text summarising 
	 * the nature of the template. It can be {@literal null}.
	 */
	protected String label;
	
	/**
	 * A {@link String} containing a descriptive text (with
	 * mode detail) about the characteristics and behaviour 
	 * of the template. It can be {@literal null}.
	 */
	protected String description;
	
	/**
	 * A {@link String} containing the unique identifier of the
	 * {@link Model} instance for which it has been
	 * prepared to.
	 */
	protected String modelId;
	
	/**
	 * A {@link List} implementation of {@link TemplateParameter}
	 * instances that contain the settings of the parameters that
	 * have been made available through this template.
	 */
	protected List<TemplateParameter> parameters;
	
	/**
	 * Initialises an instance of {@link Template}.
	 */
	public Template() {
		
	}


	/**
	 * Gets the unique identifier of the {@link Model} instance
	 * for which this {@link Template} has been prepared for.
	 * 
	 * @return  a {@link String} containing the unique identifier of the 
	 * 			optimization model.
	 */
	public String getModelId() {
		
		return this.modelId;
	}


    /**
	 * Sets the unique identifier of the {@link Model} instance
	 * for which this {@link Template} has been prepared for.
	 * 
	 * @param modelId  	a {@link String} containing the unique identifier of 
	 * 					the optimization model.
     */
	public void setModelId(String modelId) {
		
		this.modelId = modelId;
	}
	
	
	/**
	 * Gets the title for the {@link Template}.
	 * 
	 * @return	a {@link String} containing a short text that summarises the nature 
	 * 			of the template. By default it is {@literal null}.
	 */
	public String getLabel() {
		
		return this.label;
	}

	/**
	 * Sets the title for the {@link Template}. 
	 * 
	 * @param label		a {@link String} containing a short text that summarises
	 * 					the nature of the template. It can be {@literal null}.
	 */
	public void setLabel(String label) {
		
		this.label = label;
	}
	
	/**
	 * Gets the of the behaviour of the {@link Template}. The description
	 * is meant to provide information about the characteristics of the template.
	 * 
	 * @return	a {@link String} containing a descriptive text of the about
	 * 			the template characteristics and behaviour. By default it is
	 * 			{@literal null}.
	 */
	public String getDescription() {
		
		return this.description;
	}

	/**
	 * Sets the description of the {@link Template}. 
	 * 
	 * @param description	a {@link String} containing the information about the
	 * 						template behaves and its charateristics. It can be 
	 * 						{@literal null}.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}

	/**
	 * Sets the list of parameter settings that characterise and specialise the
	 * behaviour of the underlying model.
	 * 
	 * @param parameters	a {@link List} of {@link TemplateParameter} instances,
	 * 						describing the parameters that the template makes available
	 * 						for creating runs.
	 */
	public void setParameters(List<TemplateParameter> parameters) {
		
		this.parameters = parameters;
	}
	/**
	 * Gets the list of parameter settings that characterise and specialise the
	 * behaviour of the underlying model.
	 * 
	 * @return	a {@link List} of {@link TemplateParameter} instances describing 
	 * 			the parameters that the template makes available for creating runs.
	 */
	public List<TemplateParameter> getParameters() {
		
		return this.parameters;
	}
	
	/**
	 * This method retrieves the parameter (if any) that matches the given name.
	 * 
	 * @param name	a {@link String} representing the name of the parameter to
	 * 				search for. It cannot be {@literal null}.
	 * 
	 * @return	a {@link TemplateParameter} instance whose name matches the given
	 * 			<i>name</i> or {@literal null} if not found.
	 * 
	 * @throws IllegalArgumentExceptino	if <i>name</i> is {@literal null}.
	 */
	public TemplateParameter getParameter(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		TemplateParameter found = null;
		
		if (this.parameters != null) {
			
			for(TemplateParameter tp : this.parameters) {
				
				if (name.equals(tp.getName()) == true) {
					
					found = tp;
					break;
				}
			}
		}
		
		return found;
	}
	
	/**
	 * This method populates the given instance of {@link Run} with the parameters that
	 * are defined in the template. The method also calls {@link Template#validate(Run)}
	 * before doing anything else to check that the {@link Run} instance does not violate
	 * any constraint imposed by the strategy and does not have duplicates.
	 * 
	 * @param run	a {@link Run} instance containing the run to populate with the default
	 * 				(constant) parameters defined in the template. It cannot be {@literal 
	 * 				null} and it must belong to the template.
	 * 
	 * @return 	a {@link Run} instance populated with the parameters that are declared in
	 * 			the template but missing in <i>run</i>.
	 * 
	 * @return a new {@link Run} instance with the values that have been populated.
	 * 
	 * @throws IllegalArgumentException	if <i>run</i> is {@literal null}.
	 * 
	 * @throws IllegalStateException 	if the template has a {@literal null} identifier.
	 * 
	 * @throws DataValidationException	see {@link Template#validate(Run)}.
	 * 
	 * 
	 */
	public Run populate(Run run) {
		
		this.validate(run);
		
		Run populated = (Run) run.clone();
		
		List<TemplateParameter> templateParameters = this.getParameters();
		List<Parameter> parameters = populated.getParameters();
		if (parameters == null) {
			
			parameters = new ArrayList<Parameter>();
		}
		
		if (templateParameters != null) {
		
			for(TemplateParameter tp : templateParameters) {
				
				if (parameters.contains(tp) == false) {
					
					Parameter parameter = new Parameter(tp.getName(), tp.getValue());
					parameters.add(parameter);
				}
			}
		
		}
		
		populated.setParameters(parameters);
		
		return populated;
	}


	/**
	 * This method validates that the given run belongs to the current template by checking the 
	 * value of the {@link Run#getTemplateId()} attribute if the run and also verifies that the 
	 * all the parameters that are declared by the run are only free parameters.
	 * 
	 * @param run	a {@link Run} instance that represents an run configured from
	 * 				this instance of {@link Template}. It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException	if <i>run</i> is {@literal null}.
	 * 
	 * @throws IllegalStateException	if template's <i>id</i> or <i>modelId</i> are 
	 * 									{@literal null}
	 * 
	 * @throws DataValidationException	if one of the following tests holds {@literal true}:
	 * 									<ul>
	 * 									<li><i>run</i> has duplicate parameters</li>
	 * 									<li><i>run</i> contains parameters not defined in the 
	 * 									template</li>
	 * 									<li><i>run</i> contains parameters that are declared
	 * 									fixed (constant) in the template</li>
	 * 									<li><i>run</i> has a different template identifier</li>
	 * 									<li><i>run</i> has a different model identifier</li>
	 * 									</ul>
	 * 									
	 */
	public void validate(Run run) {
		
		if (run == null) {
			
			throw new IllegalArgumentException("Parameter 'run' cannot be null.");
		}
		
		String templateId = run.getTemplateId();
		String id = this.getId();
		
		if (id == null) {
			
			throw new IllegalStateException("Cannot perform validation when template identifier is set to null.");
		}
		
		if (id.equals(templateId) == false) {
			
			throw new DataValidationException("The given run does not belong to this template. Template identifier, is different (expected: " + id + ", found: " + templateId + ").");
		}
		
		String modelId = this.getModelId();
				
		if (modelId == null) {
			
			throw new IllegalStateException("Cannot perform validation when the model identifier is set to null.");
			
		} 

		String runModelId = run.getModelId();
		if (modelId.equals(runModelId) == false) {
			
			throw new DataValidationException("The given run has a different model identifier of the current template.");
		}
		
		Map<String, Parameter> alreadySeen = new HashMap<String, Parameter>();
		
		List<Parameter> rParameters = run.getParameters();
		
		if (rParameters == null) {
			
			rParameters = new ArrayList<Parameter>();
			run.setParameters(rParameters);
		} 
		
		// we need to check whether than the parameters in the 
		// list are defined in the strategy and they are not 
		// fixed.
		for(Parameter rp : rParameters) {
			
			String name = rp.getName();
			Parameter found = alreadySeen.get(name);
			if (found != null) {
				
				throw new DataValidationException("Parameter '" + name + "' is a duplicate.");
			}
			
			// ok we have not seen this yet, we can then check
			// whether it belongs to the model.
			
			TemplateParameter tp = this.getParameter(name);
			if (tp == null) {
				
				throw new DataValidationException("Parameter '" + name + "' is not defined for the given template.");
			}
			
			if (tp.isFixed() == true) {
				
				throw new DataValidationException("Parameter '" + name + "' is constant and cannot be set in this template, byt he run.");
			}
			
			// ok, up to this point, we are sure
			// that the parameter has not been seen
			// and that is a non fixed parameter in
			// template.
			
			alreadySeen.put(name, rp);
		}
		
	}
	
	/**
	 * This method clones a {@link Template}. The {@link String} properties
	 * of a {@link Template} are not cloned because the underlying type is
	 * immutable. The method first, invokes the super-class version of the
	 * method and then clones or copies the properties directly defined by
	 * the {@link Template} class.
	 * 
	 * @return  a {@link Template} instance that represents the clone of the
	 * 			the current instance.
	 */
	@Override
	public Entity clone() {
		
		// this call to the super-class version of the method
		// does take care of the super-class defined properties.
		//
		Template zombie = (Template) super.clone();
		
		// here we need to clone the specific properties that
		// are defined in this class.
		
		zombie.setLabel(this.getLabel());
		zombie.setDescription(this.getDescription());
		zombie.setModelId(this.getModelId());
		
		// now the list of template parameters.
		//
		List<TemplateParameter> newParams = null;
		List<TemplateParameter> params = this.getParameters();
		if (params != null) {
			
			newParams = new ArrayList<TemplateParameter>();
			for(TemplateParameter tp : params) {
				newParams.add((TemplateParameter) tp.clone());
			}
		}
		
		zombie.setParameters(newParams);
		
		return zombie;
	}
	
	/**
	 * This method creates an instance of {@link Template}. The method overrides
	 * the base class implementation to change the type of the instance returned
	 * by the method to {@link Template}.
	 * 
	 * @return an instance of {@link Template}.
	 */
	@Override
	protected Entity newInstance() {
		
		return new Template();
	}
	
	
}
