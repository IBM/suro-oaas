/**
 * 
 */
package com.ibm.au.optim.suro.model.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.jaws.data.DataValidationException;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

/**
 * Class <b>OptimizationModel</b>. It extends the {@link Entity} object
 * and it is the abstraction used to store the information about a given 
 * optimization model, such as the list of objectives but more importantly 
 * the list of parameters (together with their constraints) that are available 
 * to tune the model. It also provides information about the underlying files
 * that actually implement the model being represented by the class.
 * 
 * @author Christian Vecchiola
 *
 */
public class Model extends Entity {

	/**
	 * A {@literal boolean} flag indicating whether the model is the
	 * default model used by the application or not. By default this
	 * value is set to {@literal false}, which means that the model
	 * is not the default one.
	 */
	@JsonProperty("default")
	protected boolean defaultModel = false;
	/**
	 * A {@link String} representing the friendly name for the model. 
	 * This can be used to populate user interfaces for model management
	 * purpose.
	 */
	@JsonProperty("label")
	protected String label;
	/**
	 * A {@link String} containing a textual description of the model and
	 * its capabilities. Again, this can be used in user interfaces for
	 * the purpose of model management.
	 */
	@JsonProperty("description")
	protected String description;
	/**
	 * A {@literal int} value indicating the current version of the model.
	 * This flag is different from the revision, which is more related to
	 * the updates made on the storage entity.
	 */
	@JsonProperty("modelVersion")
	protected int modelVersion	=	0;
	/**
	 * A {@link List} implementation containing the list of objectives that
	 * are used in the optimisation function implemented by the model. The
	 * objectives and their importance in reaching the overall optimum when
	 * solving the optimisation problem, is controlled by weights. The value
	 * of the weights is mapped by some of the parameters of the model.
	 */
	@JsonProperty("objectives")
	protected List<Objective> objectives;
	/**
	 * A {@link List} implementation containing the parameters that customise
	 * the behaviour of the model. These parameters contain information about
	 * the type and the domain of allowed values as well as whether they control
	 * the weight of objectives.
	 */
	@JsonProperty("parameters")
	protected List<ModelParameter> parameters;
	/**
	 * A {@link List} of mappings that can be used to generate views on the
	 * results obtained by the optimization process and export these information
	 * into files for ease of access to selected slices of the data.
	 */
    @JsonProperty("outputMappings")
    protected List<OutputMapping> outputMappings;
    
    


    /**
     * Initialises an instance of the {@link Model} with the given
     * label.
	 * 
	 * @param label	a {@link String} containing the name of the model. It cannot
     * 				be {@literal null} or an empty {@link String}.
     * 
     * @throws IllegalArgumentException	if <i>label</i> is {@literal null} or 
     * 									an empty string or composed by spaces.
     */
    @JsonCreator
	public Model(@JsonProperty("label") String label) {
    	
    	this.setLabel(label);
    }
    
    /**
     * Initialises an instance of {@link Model}.
     * 
	 * 
	 * @param label				a {@link String} containing the name of the model. It cannot
     * 							be {@literal null} or an empty {@link String}.
     * @param isDefaultModel	a {@literal boolean} value indicating whether the model is 
     * 							the default model or not.
     * 
     * @throws IllegalArgumentException	<i>label</i> is {@literal null} or 
     * 									an empty string or composed by spaces.
     */
    public Model(String label, boolean isDefaultModel) {
    	
    	this.setLabel(label);
    	this.setDefaultModel(isDefaultModel);
    }
    
    /**
     * Gets a flag indicating whether the model is the default model used 
     * by the application or not.
     * 
     * @return	{@literal true} if the model is the default one, {@literal
     * 			false} if not. By default this value is set to {@literal 
     * 			false}, which means that the model is not the default one.
     */
    public boolean isDefaultModel() {
    	
		return this.defaultModel;
	}

    /**
     * Sets a flag indicating whether the model is the default model used by 
     * the application or not.
     * 
     * 
     * @param defaultModel	{@literal true} if the model is the default one, 
     * 						{@literal false} if not. By default this value is
     * 					 	set to {@literal false}, which means that the model 
     * 						is not the default one.
     */
	public void setDefaultModel(boolean defaultModel) {
		
		this.defaultModel = defaultModel;
	}

	/**
	 * Gets the friendly name of the model. This name can be used to represent
	 * and refer to the model in user interfaces for the purpose of model
	 * management.
	 * 
	 * @return 	a {@link String} containing the name of the model. It cannot be 
	 * 			{@literal null} or an empty {@link String} or composed only by
	 * 			white spaces.
	 */
	public String getLabel() {
		
		return this.label;
	}

	/**
	 * Sets the friendly name of the model. This name can be used to represent
	 * and refer to the model in user interfaces for the purpose of model
	 * management.
	 * 
	 * @param label	a {@link String} containing the name of the model. By default
	 * 				it can be {@literal null}.
     * 
     * @throws IllegalArgumentException	if <i>label</i> is {@literal null} or 
     * 									an empty string or composed by spaces.
	 */
	public void setLabel(String label) {
		
		if ((label == null) || (label.trim().isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter label cannot be null, an empty string, or composed by spaces only.");
		}
		
		this.label = label;
	}
	
	/**
	 * Gets a description of the model. This text can be used to provide additional
	 * information on the model.
	 * 
	 * @return 	a {@link String} containing the description of the model. It can be
	 * 			{@literal null}.
	 */
	public String getDescription() {
		
		return this.description;
	}

	/**
	 * Sets a description of the model. This text can be used to provide additional
	 * information on the model.
	 * 
	 * @param description	a {@link String} containing the descriptive information
	 * 						about the model. It can be {@literal null}.
     * 
	 */
	public void setDescription(String description) {
		
		
		this.description = description;
	}

	/**
	 * Gets the version of the underlying model files.
	 * 
	 * @return	a {@literal int} representing the version of the
	 * 			files that define the optimisation model. The 
	 * 			default is 0.
	 */
	public int getModelVersion() {
		
		return this.modelVersion;
	}

	/**
	 * Sets the version of the underlying model files.
	 * 
	 * @param modelVersion	a {@literal int} representing the version of the
	 * 						files that define the optimisation model. 
	 */
	public void setModelVersion(int modelVersion) {
		
		this.modelVersion = modelVersion;
	}



	/**
	 * Gets the list of objectives defined in the optimisation function implemented by
	 * the model. The objectoves and their importance in reaching the overall optimum
	 * when solving the optimisation problem is controlled by weights whose value is
	 * mapped by some of the parameters.
	 * 
	 * @return	a {@link List} implementation of {@link Objective} instances representing 
	 * 			the objectives defined in the model.
	 */
	public List<Objective> getObjectives() {
		
		return this.objectives;
	}

	/**
	 * Sets the list of objectives defined in the optimisation function implemented 
	 * by the model. The objectives and their importance in reaching the overall 
	 * optimum when solving the optimisation problem, is controlled by weights. The 
	 * value of the weights is mapped by some of the parameters of the model.
	 *
	 * @param objectives	a {@link List} implementation of {@link Objective} instances
	 * 						representing the objectives defined in the model.
	 */
	public void setObjectives(List<Objective> objectives) {

		// TODO: check for null references for the list and inside?
		//
		this.objectives = objectives;
	}
	
	/**
	 * Gets the list of parameters defined by the model. The parameter can either control
	 * the value of the weights applied to the objectives or other aspects of the model.
	 * 	
	 * @return	a {@link List} implementation containing {@link ModelParameter}
	 * 			instances each of them defining the parameters of the model.			
	 */
	public List<ModelParameter> getParameters() {
		
		return this.parameters;
	}
	
	/**
	 * Sets the list of parameters defined by the model. The parameter can either control
	 * the value of the weights applied to the objectives or other aspects of the model.
	 * 	
	 * @param parameters	{@link List} implementation containing {@link ModelParameter}
	 * 						instances each of them defining the parameters of the model.			
	 */
	public void setParameters(List<ModelParameter> parameters) {
		
		this.parameters = parameters;
	}
	

	/**
	 * Gets the list of output mappings defined by the model. Output mappings can be used
	 * to automatically generate <i>sliced</i> views of the data returned by the solution
	 * of the optimisation model to provide insights on some aspect of the problem solved.
	 * 
	 * @return	a {@link List} implementation containing {@link OutputMapping} instances
	 * 			each of them defining the mapping of some entitites in the results files
	 * 			onto the predefined views.
	 */
	public List<OutputMapping> getOutputMappings() {
		
		return this.outputMappings;
	}


	/**
	 * Sets the list of output mappings defined by the model. Output mappings can be used
	 * to automatically generate <i>sliced</i> views of the data returned by the solution
	 * of the optimisation model to provide insights on some aspect of the problem solved.
	 * 
	 * @param outputMapping	a {@link List} implementation containing {@link OutputMapping} 
	 * 						instances each of them defining the mapping of some entitites 
	 * 						in the results files onto the predefined views.
	 */
	public void setOutputMappings(List<OutputMapping> outputMapping) {
		
		this.outputMappings = outputMapping;
	}
	
	/**
	 * This method retrieves that parameter whose name matches the given <i>name</i>.
	 * 
	 * @param name	a {@link String} representing the name of the parameter to search
	 * 				for. It cannot be {@literal null} or an empty {@link String} or a.
	 *  
	 * @return	a {@link ModelParameter} instance whose name matches the given <i>name</i>
	 * 			or {@literal null} if there is no parameter with the given name.
	 * 
	 * @throws	IllegalArgumentException	if <i>name</i> is {@literal null} or an empty
	 * 										string.
	 */
	public ModelParameter getParameter(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		ModelParameter found = null;
		
		List<ModelParameter> parameters = this.getParameters();
		
		if (parameters != null) {
		
			for(ModelParameter parameter : parameters) {
				
				if (name.equals(parameter.getName()) == true) {
					
					found = parameter;
					break;
				}
			}
		}
		
		return found;
	}

	/**
	 * This method retrieves that objective whose name matches the given <i>name</i>.
	 * 
	 * @param name	a {@link String} representing the name of the objective to search
	 * 				for. It cannot be {@literal null} or an empty {@link String} or a.
	 *  
	 * @return	a {@link Objective} instance whose name matches the given <i>name</i>
	 * 			or {@literal null} if there is no objective with the given name.
	 * 
	 * @throws	IllegalArgumentException	if <i>name</i> is {@literal null} or an empty
	 * 										string.
	 */
	public Objective getObjective(String name) {
		
		if ((name == null) || (name.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'name' cannot be null or an empty string.");
		}
		
		Objective found = null;
		
		List<Objective> objectives = this.getObjectives();
		
		if (objectives != null) {
		
			for(Objective objective : objectives) {
				
				if (name.equals(objective.getName()) == true) {
					
					found = objective;
					break;
				}
			}
		}
		
		return found;
		
	}
	
	/**
	 * This method validates the parameter passed as argument against the constraints
	 * that the model defines for the corresponding parameter if any.
	 * 
	 * @param parameter	an instance of {@link Parameter} or an instance of an inherited
	 * 					class, which represents the parameter to validate against the 
	 * 					corresponding parameter in the model. It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException	if the given <i>parameter</i> is {@literal null}.
	 * @throws DataValidationException	if the given <i>parameter</i> does not have a 
	 * 									corresponding parameter in the model or it is not
	 * 									compliant with the constraints set by the model for
	 * 									that parameter.
	 */
	public void validate(Parameter parameter) {
		
		if (parameter == null) {
			
			throw new IllegalArgumentException("Parameter cannot be null.");
		}
		
		String name = parameter.getName();
		ModelParameter ancestor = this.getParameter(name);
		
		if (ancestor == null) {
			
			// ok a given parameter does not have a corresponding
			// parameter in the model.
			//
			throw new DataValidationException("Parameter '" + name + "' does not exist in the current model.");
		}
		
		Object value = parameter.getValue();
		
		
		// The quickest way to check whether a parameter has a compliant 
		// value with with the type and range constraints imposed on the
		// corresponding model parameter, is to try to set the value to
		// test as value of the model parameter. 
		//
		// Because we do not want to loose the original default value we
		// cache it locally before executing the test and set it back in
		// a finally block.
		//
		Object cache  = ancestor.getValue();
		
		try {
			
			ancestor.setValue(value);
			
			
		} catch(IllegalArgumentException ilex) { 
			
			throw new DataValidationException("Parameter '" + name + "' has an incompatible value.", ilex);
		
		} finally {
			
			// whether we have an exception or not we set the
			// parameter back to its original value.
			//
			ancestor.setValue(cache);
		}
	}
	
	/**
	 * <p>
	 * This method populates the given <i>template</i> with the missing default parameters
	 * that are subsumed from the current {@link Model} instance. This method invokes fist
	 * {@link Model#validate(Template)} to check that the {@link Template} has been created
	 * for the current model and does not violate any of the type constraints imposed by
	 * the parameters defined by the model.
	 * </p>
	 * <p>
	 * If the execution of the method is successful at the end of the execution the returned 
	 * {@link Template} instance has non null list of parameters, but it could be empty if 
	 * the model does not declare any parameter, otherwise it will have all the parameters of 
	 * the model defined with either the value in the original template or the value subsumed 
	 * from the model if they were not declared in the template.
	 * </p>
	 * 
	 * 
	 * @param template	a {@link Template} instance that represents a template prepared for
	 * 					creating runs for the current model. This cannot be {@literal null}
	 * 					and the value of {@link Template#getModelId()} must match the value
	 * 					returned by {@link Model#getId()} when applied to this instance.
	 * 
	 * @return  a {@link Template} instance with all the populated parameters from the model,
	 * 			if there was any missing parameter.
	 * 
	 * @throws IllegalArgumentException	if <i>template</i> is {@literal null}.
	 * @throws IllegalStateException	if {@link Model#getId()} is {@literal null}.
	 * 
	 * @throws DataValidationException	see {@link Model#validate(Template)} for more information.
	 */
	public Template populate(Template template) {
		
		this.validate(template);
		
		Template populated = (Template) template.clone();
		
		// ok we know now that if there is any parameter defined for the template this
		// already has a compliant value, what we need to do then is to just copy into
		// the template those parameters that do not occur in the template's list.
		
		List<TemplateParameter> parameters = populated.getParameters();
		
		List<ModelParameter> modelParams = this.getParameters();
		if (parameters == null) {
			
			parameters = new ArrayList<TemplateParameter>();
		}
		
		
		// if the model parameters list is null, and the validation has passed this
		// means that the template list is either null or an empty string, we don't
		// need to do anything.
		//
		if (modelParams != null) {
			
			for(ModelParameter mParam : modelParams) {
				
				// we create a template parameter that is fixed and we check whether the 
				// parameter is already contained in the list. If it is not found, then
				// we already have an instance that we can add.
				//
				TemplateParameter tp = new TemplateParameter(mParam.getName(), mParam.getValue());
				tp.setFixed(true);
				
				// equality test for parameters is always implemented by performing the
				// equality test for the corresponding names and not the values.
				//
				if (parameters.contains(tp) == false) {
					parameters.add(tp);
				}
			}
		
		}
		
		// we just ensure that the set of parameters is persisted into the template
		// by setting again the list of parameters. This is needed when the list of
		// the parameters is null, but it is also a better practice when it is not
		// null because it ensures that if there was any cloning operation in the 
		// getter of the list, by calling the setter we are sure that they are set
		// as parameters in the template.
		//
		populated.setParameters(parameters);
		
		return populated;
	}
	
	/**
	 * This method checks that the given <i>template</i> is compliant with the constraints
	 * defined by the current model. The method checks that all the parameters listed in the
	 * template are defined in the model, it also checks that the default values set for the
	 * parameters are compliant with the type and range constraints set for the corresponding
	 * parameter in the model.
	 * 
	 * @param template	an instance of {@link Template} representing the template that is 
	 * 					subject to the test. It must be not {@literal null} and the value of
	 * 					the <i>modelId</i> property should much the unique identifier of this
	 * 					instance of {@literal Model}.
	 * 
	 * @throws IllegalArgumentException	if <i>template</i> is {@literal null}.
	 * 
	 * @throws IllegalStateException	if {@link Model#getId()} is {@literal null}.
	 * 
	 * @throws DataValidationException	if one of the following tests holds {@literal true}:
	 * 									<ul>
	 * 									<li>the value of {@link Template#getModelId()} differs
	 * 									from {@link Model#getId()} applied to this model.</li>
	 * 									<li>the template has some parameters not defined in the
	 * 									model.</li>
	 * 									<li>the template has at least one parameter whose type
	 * 									or value does not match the constraint imposed by the 
	 * 									corresponding model parameter for the value.</li>
	 * 									</ul>
	 */
	public void validate(Template template) {
		
		if (template == null) {
			
			throw new IllegalArgumentException("Parameter 'template' cannot be null");
		}

		String id = this.getId();
		if (id == null) {
			
			throw new IllegalStateException("Cannot perform template validation when the model identifier is null.");
		}
		
		String modelId = template.getModelId();
		if (id.equals(modelId) == false) {
			
			throw new DataValidationException("The given template has not been prepared for this model. Model identifier is different (expected: " + id + ", found: " + modelId + ")." );
		}
		
		List<ModelParameter> parameters = this.getParameters();
		List<TemplateParameter> templateParameters = template.getParameters();
		
		if ((parameters == null) || (parameters.size() > 0)) {
		
			if ((templateParameters != null) && (templateParameters.size() > 0)) {
				
				Map<String, TemplateParameter> alreadySeen = new HashMap<String, TemplateParameter>();
				
				
				for(TemplateParameter parameter : templateParameters) {
	
					String name = parameter.getName();
					TemplateParameter found = alreadySeen.get(name);
							
					if (found != null) {
					
						throw new DataValidationException("Template parameter '" + name + "' is a duplicated.");
					}
					this.validate(parameter);
					alreadySeen.put(name, parameter);
				}
			}
		
		} else {
			
			// in this case we need to simply check that the
			// size of the template parameter list is null or
			// an empty list.
			
			if ((templateParameters != null) && (templateParameters.size() > 0)) {
				
				throw new DataValidationException("The model does not declare any parameter, while the template does include parameter.");
			}
		}
	}
	
	/**
	 * <p>
	 * This method populates the run with the transitive set of parameters that are
	 * subsumed by the template first and then the model. Before populating the list
	 * of parameters both the <i>template</i> and the <i>run</i> are validated. The
	 * template is validated against the model, and the run against the template. 
	 * </p>
	 * <p>
	 * The values of the template that are fixed are populated into the run. Then the
	 * model checks for all the declared parameters in the run that their value are
	 * compliant with the corresponding  parameter constraints defined in the model
	 * for the very same parameters. Those parameters that were not defined in the
	 * template but present in the model are then added into the list of parameters
	 * defined in the run.
	 * </p>
	 * 
	 * @param template	a {@link Template} instance representing the template that has	
	 * 					been used to create the run. It cannot be {@literal null} and
	 * 					its model identifier must match the value of the identifier of
	 * 					this instance of the model.
	 * 
	 * @param run		a {@link Run} instance representing the run that needs to be
	 * 					populated. It cannot be {@literal null} and the value of the
	 * 					template identifier must match the identifier of <i>template</i>.
	 * 					Moreover, the model identifier must match the unique identifier
	 * 					of this instance of the model.
	 * 
	 * @throws IllegalArgumentException	if <i>template</i> or <i>run</i> are {@literal null}.
	 * @throws IllegalStateException	if {@link Model#getId()} is {@literal null} or
	 * 									{@link Template#getId()} is {@literal null}.
	 * 
	 * @throws DataValidationException	if either <i>template</i> or <i>run</i> violate
	 * 									some constraints.
	 */
	public Run populate(Template template, Run run) {
		
		
		
		
		// this is done up front to ensure the preservation of the semantic.
		// parameters should be checked first before throwing any other type
		// of exception.
		//

		if (template == null) {
			
			throw new IllegalArgumentException("Parameter 'template' cannot be null.");
		}
		
		if (run == null) {
			
			throw new IllegalArgumentException("Parameter 'run' cannot be null.");
		}
		
		
		// ok we are sure now that the template is compliant with the model, but
		// we do not want to copy all the values because we to still distinguish
		// the errors between a parameter defined in the run and the model but
		// not in the template the run belongs to.
		//
		this.validate(template);
		
		
		// we now call template validate to ensure that the run is valid with
		// reference to the template passed as argument. We also copy the values
		// that are in the template.
		//
		Run populated = template.populate(run);
		
		
		// ok now we simply need to check all the parameters and ensure that
		// they are compliant with the value set by the model.
		//
		
		List<Parameter> rParams = populated.getParameters();
		List<ModelParameter> mParams = this.getParameters();
		
		Map<String, Parameter> transitive = new HashMap<String,Parameter>();
		for(Parameter rParam : rParams) {
			
			transitive.put(rParam.getName(), rParam);
		}
		
		if (mParams != null) {
		
			for(ModelParameter mParam : mParams) {
			
				String name = mParam.getName();
				// is it already in the list of the parameters defined
				// by the run?
				//
				Parameter rParam = transitive.get(name);
				if (rParam != null) {
					
					this.validate(rParam);
					
				} else {
					
					transitive.put(name, new Parameter(name, mParam.getValue()));
				}
			}
		}
		
		// we are now sure that we have all the parameters we need
		// and that all the parameters have been validated
		//
		rParams.clear();
		rParams.addAll(transitive.values());
		
		// we copy back the list to the run instance to be sure that
		// if there was a cloning we actually add the additional
		// parameters to the instance and not only the list.
		//
		
		populated.setParameters(rParams);
		
		return populated;
	}
    
	/**
	 * Provides a {@link String} representation of an optimisation model. 
	 * 
	 * @return 	a {@link String} in the following form: 
	 * 			<pre>
	 * 			[id: ..., label: ...., modelVersion: ...., default: ....]
	 * 			</pre>
	 */
	public String toString() {
		
		return "[id: " +  this.getId() + ", label: " + this.getLabel() + ", modelVersion: " + this.getModelVersion() + ", default: " + this.isDefaultModel() + "]";
	}
	
	/**
	 * This method clones a {@link Model}. The {@link String} properties
	 * of a {@link Model} are not cloned because the underlying type is
	 * immutable. The method first, invokes the super-class version of the
	 * method and then clones or copies the properties directly defined by
	 * the {@link Model} class.
	 * 
	 * @return  a {@link Model} instance that represents the clone of the
	 * 			the current instance.
	 */
	@Override
	public Entity clone() {
		
		// this call to the super-class version of the method
		// does take care of the super-class defined properties.
		//
		Model zombie = (Model) super.clone();
		
		// here we need to clone the specific properties that
		// are defined in this class.
		
		// we do not set the label, because already set in
		// constructor of the Model.
		
		zombie.setDefaultModel(this.isDefaultModel());
		zombie.setModelVersion(this.getModelVersion());
		zombie.setDescription(this.getDescription());
		
		
		List<Objective> newObjs = null;
		List<Objective> objs = this.getObjectives(); 
		if (objs != null) {
			
			newObjs = new ArrayList<Objective>();
			for(Objective obj : objs) {
				newObjs.add(obj.clone());
			}
		}
		zombie.setObjectives(newObjs);
		
		// now the list of template parameters.
		//
		List<ModelParameter> newParams = null;
		List<ModelParameter> params = this.getParameters();
		if (params != null) {
			
			newParams = new ArrayList<ModelParameter>();
			for(ModelParameter p : params) {
				newParams.add((ModelParameter) p.clone());
			}
		}
		zombie.setParameters(newParams);
		
		List<OutputMapping> newMappings = this.getOutputMappings();
		List<OutputMapping> mappings = this.getOutputMappings();
		if (mappings != null) {
			
			newMappings = new ArrayList<OutputMapping>();
			for(OutputMapping m : mappings) {
				newMappings.add(m.clone());
			}
		}
		zombie.setOutputMappings(newMappings);
		
		return zombie;
	}
	
	/**
	 * This method creates an instance of {@link Model}. The method overrides the base
	 * class implementation to change the type of the instance returned by the method
	 * to {@link Model}.
	 * 
	 * @return 	an instance of {@link Model} configured with the current <i>label</i>.
	 * 			
	 */
	@Override
	protected Entity newInstance() {
		
		return new Model(this.getLabel());
	}

	
}
