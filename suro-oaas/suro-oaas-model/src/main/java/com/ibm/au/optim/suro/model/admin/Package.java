/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Template;

/**
 * Class <b>Package</b>. This class is used to represent the metadata about a package.
 * A package comprises information about the following entities that are required for
 * the application:
 * <ul>
 * <li>details about the model file and its attachment.</li>
 * <li>details about the data set file and its attachment.</li>
 * <li>details about the templates prepared for the model.</li>
 * </ul>
 * A package can be used for the following purposes:
 * <ul>
 * <li>installation of new entities (models, templates, and datasets)</li>
 * <li>update of existing entities, in this case the entities already have the <i>id</i>
 * property set.</li>
 * <li>partial updates</li>	
 * </ul>
 * At least one of the entities contained in the package need to be valid. 
 * 
 * @author Christian Vecchiola
 *
 */
public class Package {

	/**
	 * This {@link String} contains the version of the package.
	 */
	@JsonProperty("version")
	protected String version;
	

	/**
	 * A {@link String} containing informative notes about the package.
	 * It can be {@literal null} or an empty string.
	 */
	@JsonProperty("description") 
	protected String description;
	
	/**
	 * This {@link Model} represents the model that contains the
	 * metadata about the optimisation model to be installed in
	 * the system. 
	 */
	@JsonProperty("model")
	protected Model model;
	
	/**
	 * This {@link DataSet} instance contains the dataset file that
	 * is paired with the model in the package. An optimisation model
	 * goes along with a corresponding data set file since the two
	 * must have corresponding parameters. When this instance is 
	 * {@literal null} the package will not update the existing model
	 * but simply rely on an existing one.
	 */
	@JsonProperty("dataSet")
	protected DataSet dataSet;
	
	/**
	 * This is a {@link List} implementation that is used to store all
	 * the templates that have been prepared by the model.
	 */
	@JsonProperty("templates")
	protected List<Template> templates;
	
	
	/**
	 * Initialises an instance of {@link Package} with the given version
	 * and data set. This constructor can be used to create a package
	 * that updates the dataset installed into the system.
	 * 
	 * @param version	a {@link String} representing the version of the
	 * 					package. It cannot be {@literal null} or an empty
	 * 					string.
	 * 
	 * @param dataSet	a {@link DataSet} instance representing the dataset
	 * 					to be added.  It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null}
	 * 									or an empty string.
	 * 
	 * @throws IllegalStateException	if <i>dataSet</i> is {@literal null}.
	 */
	public Package(String version, DataSet dataSet) {
		this(version, null, null, dataSet, null);

	}
	
	/**
	 * Initialises an instance of {@link Package} witht the given version
	 * and model. This constructor can be used to create a package that 
	 * simply updates the model of the current solution.
	 * 
	 * @param version	a {@link String} representing the version of the
	 * 					package. It cannot be {@literal null} or an empty
	 * 					string.
	 * 
	 * @param model		a {@link Model} instance representing the new version
	 * 					of the optimisation model.
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null}
	 * 									or an empty string.
	 * 
	 * @throws IllegalStateException	if <i>model</i> is {@literal null}.
	 */
	public Package(String version, Model model) {
		
		this(version, model, null, null, null);
	}
	
	
	/**
	 * Initialises an instance of {@link Package} with the given version
	 * and model. This constructor can be used when we want to add a new 
	 * set of templates to the one currently existing and bind them to
	 * either an existing model (in that case, the template instances would
	 * have the {@link Template#getModelId()} set to a specific template) or
	 * to the default model.
	 * 
	 * @param version	a {@link String} representing the version of the
	 * 					package. It cannot be {@literal null} or an empty
	 * 					string.
	 * 
	 * @param templates	a {@link List} implementation that is used to store
	 * 					the set of templates to be added to the solution.
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null}
	 * 									or an empty string.
	 * 
	 * @throws IllegalStateException	if <i>templates</i> is either {@literal 
	 * 									null} or an empty list.
	 */
	public Package(String version, List<Template> templates) {
		this(version, null, templates, null, null);

	}
	
	/**
	 * Initialises an instance of {@link Package} with the given version and
	 * list of templates. This constructor can be used when we want to update
	 * both a model and a collection of templates. 
	 * 
	 * @param version	a {@link String} representing the version of the 
	 * 					package. It cannot be {@literal null} or an empty
	 * 					string.
	 * @param model		a {@link Model} instance containing the definition of
	 * 					the model to install / update.
	 * 
	 * @param templates	a {@link List} implementation containing the list of
	 * 					templates to install / update.
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null}
	 * 									or an empty string.
	 * 
	 * @throws IllegalStateException	if both <i>model</i> and <i>templates</i> are 
	 * 									{@literal null} or <i>model</i> is {@literal null}
	 * 									and the <i>templates</i> are an empty list.
	 */
	public Package(String version, Model model, List<Template> templates) {
		this(version, model, templates, null, null);
	}
	

	/**
	 * Initialsies an instance of {@link Package} with the given parameters.
	 * 
	 * 
	 * @param version		a {@link String} representing the version of the package. It cannot be 
	 * 						{@literal null} or an empty string.
	 * 
	 * @param model			a {@link Model} instance containing the definition of the model to install
	 * 						/ update. If not {@literal null} this can either indicate a replacement
	 * 						of an existing entity or a new entity to add.
	 * 
	 * @param templates		a {@link List} implementation containing the list of templates to install
	 * 						/ update. If not {@literal null} or empty, it contains the list of {@link
	 * 				   		Template} instances to be either updated or installed. If these templates
	 * 						have a defined {@link Template#getModelId()} they'll be bound to the existing
	 * 						model, otherwise they'll be bound to the default model.
	 * 
	 * @param dataset		a {@link DataSet} instance representing the dataset to be added. If not 
	 * 						{@literal null} this can either indicate a replacement of an existing entity 
	 * 						or a new entity to add.
	 * 
	 * @param description	a {@link String} containing some descriptive notes about the package. It
	 * 						can be {@literal null} or an empty string.
	 * 
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null} or an empty string.
	 * 
	 * @throws IllegalStateException	if <i>model</i>, <i>dataSet</i> and <i>templates</i> are
	 * 									{@literal null} or the <i>model</i> and <i>dataSet</i> are 
	 * 									{@literal null} and <i>templates</i> is an empty list.
	 */
	@JsonCreator
	public Package(@JsonProperty("version") String version, 
				   @JsonProperty("model") Model model, @JsonProperty("templates") List<Template> templates, @JsonProperty("dataset") DataSet dataset,
				   @JsonProperty("description") String description) {
		
		this.setVersion(version);
		this.setDescription(description);
		
		// we cannot call the setters for these, because
		// they will get into the way of each other because
		// they call validate...
		//
		this.model = model;
		this.templates = templates;
		this.dataSet = dataset;
		
		this.validate();
	}
	

	/**
	 * Gets the description of the package.
	 * 
	 * @return	a {@link String} containing the description. It can be {@literal null} or empty.
	 */
	public String getDescription() {
		
		return this.description;
	}
	
	/**
	 * Sets the description for the package.
	 * 
	 * 
	 * @param description	a {@link String} containing the description. It can be {@literal null} or
	 * 						an empty string.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}
	
	/**
	 * Sets the model for the package. If not {@literal null} this can either indicate a replacement
	 * of an existing entity or a new entity to add.
	 * 
	 * @param model	a {@link Model} instances that represents the model to be updated or installed.
	 * 				If the {@link Model#getId()} is not {@literal null}, this refers to an existing
	 * 				model, which will be updated. 
	 * 
	 * @throws IllegalStateException	if this operation causes the {@link Package} to be invalid.
	 * 									An invalid package does not contain a single element that
	 * 									can be installed or updated.
	 */
	public void setModel(Model model) {
		
		this.model = model;
		this.validate();
	}
	
	/**
	 * Gets the model for the package. If not {@literal null} this can either indicate a replacement
	 * of an existing entity or a new entity to add.
	 * 
	 * @return	a {@link Model} instances that represents the model to be updated or installed. If the 
	 * 			{@link Model#getId()} is not {@literal null}, this refers to an existing model, which 
	 * 			will be updated. 
	 */
	public Model getModel() {
		
		return this.model;
	}
	
	/**
	 * Sets the list of templates that are defined by the package. It this is not {@literal null} or
	 * an empty list. The list contains a collection of {@link Template} instances that are either
	 * meant to replace existing templates or new ones to be added. If the {@link Template} instances
	 * have a defined {@link Template#getModelId()}, these will be bound to the existing model, if
	 * the model identifier is {@literal null} they'll be bound to the default model.
	 * 
	 * @param templates	a {@link List} implementation containing the list of templates.
	 * 
	 * @throws IllegalStateException	if this operation causes the {@link Package} to be invalid.
	 * 									An invalid package does not contain a single element that
	 * 									can be installed or updated.
	 * 
	 */
	public void setTemplates(List<Template> templates) {

		this.templates = templates;
		this.validate();
	}
	
	/**
	 * Gets the list of templates that are defined by the package. It this is not {@literal null} or
	 * an empty list. The list contains a collection of {@link Template} instances that are either
	 * meant to replace existing templates or new ones to be added. If the {@link Template} instances
	 * have a defined {@link Template#getModelId()}, these will be bound to the existing model, if
	 * the model identifier is {@literal null} they'll be bound to the default model.
	 * 
	 * @return a {@link List} implementation containing the list of templates.
	 */
	public List<Template> getTemplates() {
		
		return this.templates;
	}
	
	/**
	 * Sets the version for the package. A version is a unique identification number that can be used
	 * to identify a change in the entities that are managed by the solution.
	 * 
	 * @param version	a {@link String} representing the version number. It cannot be {@literal null}
	 * 					or an empty string.
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null} or an empty string.
	 */
	public void setVersion(String version) {
		
		if ((version == null) || (version.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'version' cannot be null or an empty string.");
		}

		this.version = version;
	}
	
	/**
	 * Gets the version for the package. A version is a unique identification number that can be used
	 * to identify a change in the entities that are managed by the solution. It cannot be {@literal 
	 * null} or an empty string.
	 * 
	 * @return	a {@link String} representing the version of the package.
	 */
	public String getVersion() {
		
		return this.version;
	}
	
	/**
	 * Sets the dataset for the package. If not {@literal null} this can either indicate a replacement
	 * of an existing entity or a new entity to add.
	 * 
	 * @param dataSet	a {@link DataSet} instances that represents the model to be updated or installed.
	 * 					If the {@link DataSet#getId()} is not {@literal null}, this refers to an existing
	 * 					model, which will be updated. 
	 * 
	 * @throws IllegalStateException	if this operation causes the {@link Package} to be invalid.
	 * 									An invalid package does not contain a single element that
	 * 									can be installed or updated.
	 */
	public void setDataSet(DataSet dataSet) {
		
		this.dataSet = dataSet;
		this.validate();
	}
	
	/**
	 * Gets the dataset for the package. If not {@literal null} this can either indicate a replacement
	 * of an existing entity or a new entity to add.
	 * 
	 * @return	a {@link DataSet} instances that represents the model to be updated or installed. If the 
	 * 			{@link DataSet#getId()} is not {@literal null}, this refers to an existing model, which
	 * 			will be updated. 
	 */
	public DataSet getDataSet() {
		
		return this.dataSet;
	}
	
	
	
	
	/**
	 * This is a utility method that is used to internally check that the package
	 * is consistent at any point in time, with what is meant to be. 
	 * 
	 * @throws IllegalStateException	if <i>model</i>, <i>dataset</i>, and <i>templates</i>
	 * 									are all {@literal null} (for <i>templates</i> it also
	 * 									counts that it is empty).
	 */
	private void validate() {
		
		if ((model == null) && (dataSet == null) && ((templates == null) || (templates.isEmpty() == true))) {
			
			throw new IllegalStateException("Package must have at least one item [model, dataSet, templates] not null or empty.");
		}
	}
	

	
	
	
	
}
