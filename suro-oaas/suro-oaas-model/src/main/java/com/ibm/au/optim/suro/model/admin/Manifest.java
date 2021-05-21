/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class <b>Manifest</b>. This class defines the metadata about a {@link Package}
 * it contains the description of the resources that are used to compose a package.
 * 
 * @author Christian Vecchiola
 *
 */
public class Manifest {
	
	/**
	 * A {@link String} constant that represents the default name of the manifest
	 * file. This is a JSON file that contains the JSON representation of instances
	 * of this class.
	 */
	public static final String DEFAULT_MANIFEST_FILE = "manifest.json";
	
	/**
	 * A {@link String} representing the version of the package. It cannot be {@literal 
	 * null} or empty.
	 */
	@JsonProperty("version")
	protected String version;
	
	/**
	 * A {@link String} containing informative notes about the package described by the
	 * manifest.
	 */
	@JsonProperty("description") 
	protected String description;
	
	/**
	 * A {@link ManifestEntity} instance containing the metadata about the resource that
	 * defines an optimisation model. It can be {@literal null}.
	 */
	@JsonProperty("model")
	protected ManifestEntity model;
	
	/**
	 * A {@link ManifestEntity} instance containing the metadata about the resource that
	 * defines a dataset. It can be {@literal null}.
	 */
	@JsonProperty("dataSet")
	protected ManifestEntity dataSet;
	
	/**
	 * A {@link ManifestEntity} instance containing the metadata about the resource that
	 * defines the collection of templates. It can be {@literal null}.
	 * 
	 */
	@JsonProperty("templates")
	protected ManifestEntity templates;
	
	/**
	 * Initialises an instance of {@link Manifest} with the given resources and <i>version</i>.
	 * 
	 * @param version		a {@link String} representing the version of the package. It cannot be 
	 * 						{@literal  null} or empty.
	 * 
	 * @param model			a {@link ManifestEntity} instance containing the metadata about the 
	 * 						resource that defines an optimisation model. It can be {@literal null}.
	 * 
	 * @param templates		a {@link ManifestEntity} instance containing the metadata about the 
	 * 						resource that defines the collection of templates. It can be {@literal 
	 * 						null}.
	 * 
	 * @param dataSet		a {@link ManifestEntity} instance containing the metadata about the 
	 * 						resource that defines a dataset. It can be {@literal null}.
	 * 
	 * @param description	a {@link String} containing an optional description about the package
	 * 						defined by this manifest. It can be {@literal null}.
	 * 
	 * @throws IllegalArgumentException	if <i>version</i> is {@literal null} or an empty string.
	 */
	@JsonCreator
	public Manifest(@JsonProperty("version") String version, 
					@JsonProperty("model") ManifestEntity model, @JsonProperty("templates") ManifestEntity templates, @JsonProperty("dataSet") ManifestEntity dataSet, 
					@JsonProperty("description") String description) {
		
		this.setVersion(version);
		this.setModel(model);
		this.setTemplates(templates);
		this.setDataSet(dataSet);
		this.setDescription(description);
	}

	/**
	 * Gets the version of the manifest. It cannot be {@literal null} or an empty string.
	 * 
	 * 
	 * @return	a {@link String} representing the version of the manifest.
	 */
	public String getVersion() {
	
		return this.version;
	}
	
	/**
	 * Sets the version of the manifest. 
	 * 
	 * @param version	a {@link String} representing the version. It cannot be {@literal null}
	 * 					or an empty string.
	 */
	public void setVersion(String version) {
	
		if ((version == null) || (version.isEmpty() == true)) {
			
			throw new IllegalArgumentException("Parameter 'version' cannot be null or an empty string.");
		}
		
		this.version = version;
	}

	/**
	 * Gets the description of the package defined by this manifest.
	 * 
	 * @return	a {@link String} containing the description. It can be {@literal null} or empty.
	 */
	public String getDescription() {
		
		return this.description;
	}
	
	/**
	 * Sets the description for the package defined by this manifest.
	 * 
	 * 
	 * @param description	a {@link String} containing the description. It can be {@literal null} or
	 * 						an empty string.
	 */
	public void setDescription(String description) {
		
		this.description = description;
	}
	
	/**
	 * Gets the resource metadata for the {@link Model} instance described by
	 * this manifest. It can be {@literal null}.
	 * 
	 * @return	a {@link ManifestEntity} instance wrapping the information about
	 * 			the location of the model resource referenced by this instance
	 * 			of {@link Manifest}.
	 */
	public ManifestEntity getModel() {
		
		return this.model;
	} 
	
	/**
	 * Sets the resource metadata for the {@link Model} instance described by
	 * this manifest. 
	 * 
	 * @param model	a {@link ManifestEntity} instance wrapping the information 
	 * 				about the location of the model resource referenced by this 
	 * 				instance of {@link Manifest}. It can be {@literal null}.
	 */
	public void setModel(ManifestEntity model) {
		
		this.model = model;
	}
	
	/**
	 * Gets the resource metadata for the {@link Template} instances described by
	 * this manifest. It can be {@literal null}.
	 * 
	 * @return	a {@link ManifestEntity} instance wrapping the information about
	 * 			the location of the template resources referenced by this instance
	 * 			of {@link Manifest}.
	 */
	public ManifestEntity getTemplates() {
		
		return this.templates;
	}

	/**
	 * Sets the resource metadata for the {@link Template} instances described by
	 * this manifest. It can be {@literal null}.
	 * 
	 * @param templates	a {@link ManifestEntity} instance wrapping the information 
	 * 					about the location of the template resources referenced by 
	 * 					this instance of {@link Manifest}. It can be {@literal null}.
	 */
	public void setTemplates(ManifestEntity templates) {
		
		this.templates = templates;
		
	}
	
	/**
	 * Gets the resource metadata for the {@link DataSet} instance described by
	 * this manifest. It can be {@literal null}.
	 * 
	 * @return	a {@link ManifestEntity} instance wrapping the information about
	 * 			the location of the dataset resource referenced by this instance
	 * 			of {@link Manifest}.
	 */
	public ManifestEntity getDataSet() {
		
		return this.dataSet;
	}

	/**
	 * Sets the resource metadata for the {@link DataSet} instance described by
	 * this manifest. It can be {@literal null}.
	 * 
	 * @param dataSet	a {@link ManifestEntity} instance wrapping the information about
	 * 					the location of the dataset resource referenced by this instance
	 * 					of {@link Manifest}. It can be {@literal null}.
	 */
	public void setDataSet(ManifestEntity dataSet) {
		
		this.dataSet = dataSet;
		
	}
	
	/**
	 * This method checks whether the {@link Manifest} instance is valid. In order 
	 * to be valid a manifest should have a least a resource not {@literal null}.
	 * 
	 * @return	{@literal true} if at least one of {@link Manifest#getModel()},
	 * 			{@link Manifest#getDataSet()} or {@link Manifest#getTemplates()}
	 * 			is not {@literal null}, {@literal false} otherwise.
	 */
	public boolean isValid() {
		
		return  (this.getModel() != null) || 
				(this.getDataSet() != null) || 
				(this.getTemplates() != null);
	}
	
	

}
