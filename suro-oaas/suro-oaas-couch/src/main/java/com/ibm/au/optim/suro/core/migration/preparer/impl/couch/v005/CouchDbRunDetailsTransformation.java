/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ibm.au.optim.suro.model.entities.RunDetails;

/**
 * <p>
 * Class <b>CouchDbRunDetailsTransformation</b>. This class extends {@link CouchDbDocumentTransformation}
 * and specialises the transformation operation to convert previous instances of the optimisation results
 * in <i>CouchDb</i> with documents that can be deserialised instances of type <i>RunDetails</i>.
 * </p>
 * <p>
 * This object performs a complete transformation of the original instance into the new version of
 * the <i>OptimizationResult</i> class, which is {@link RunDetails}.
 * </p>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbRunDetailsTransformation extends CouchDbDocumentTransformation {

	/**
	 * A {@link String} constant that contains the name of the original type
	 * used in the previous version of the data model for documents representing
	 * optimisation model instances.
	 */
	public static final String SOURCE_DOCUMENT = "CouchDbOptimizationResult";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the previous version of the data model.
	 */
	public static final String OLD_TYPE_DISCRIMINATOR = "cdbOptimResult";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the new version of the data model.
	 */
	public static final String NEW_TYPE_DISCRIMINATOR  = "cdbRunDetails";
	
	/**
	 * A {@link String} constant that contains the name of the resource file describing
	 * the mappings from the {@link Run} attributes to the corresponding {@link RunDetails}
	 * document.
	 */
	public static final String SOURCE_ATTRIBUTES_MAPPING = "schemas/v0.0.5/RunDetails.properties";
	
	/**
	 * A {@link String} constant that contains the name of the properties that
	 * stores back-end specific attributes.
	 */
	public static final String ATTRIBUTES_PROPERTY = "attributes";
	/**
	 * A {@link String} constant that contains the name of the attribute that is
	 * used to identify the {@link Run} instance the details belong to.
	 */
	public static final String RUN_ID_PROPERTY     = "runId";
	
	/**
	 * A {@link Map} implementation that is used to store the set of additional attributes
	 * that for each <i>run</i> document (identified by its id) need to be transferred to
	 * the corresponding run details instance.
	 */
	protected Map<String,Map<String,Object>> details = new HashMap<String,Map<String,Object>>();
	
	/**
	 * A {@link Properties} instance that can be used to store the mappings of the old names
	 * of the document attributes to the new names.
	 */
	protected Properties mappings = null;
	

	/**
	 * Initialises an instance of {@link CouchDbRunDetailsTransformation}. This constructor
	 * calls the base constructor with the specific name of the class type that was previously
	 * used to persist optimisation model entities.
	 */
	public CouchDbRunDetailsTransformation() {
		super(CouchDbRunDetailsTransformation.SOURCE_DOCUMENT);
		

		// The name has changed, than we can delete the view name.
		//
		this.setDeleteView(true);
	}
	
	/**
	 * Initialises the details map and removes any existing mapping.
	 */
	public void clearDetails() {
		
		this.details.clear();
	}
	
	/**
	 * This method extends the behaviour defined in the base class implementation by pre-loading
	 * the mappings of the attributes that need to be transferred to {@link RunDetails} document.
	 * If there is any error in loading the attributes mapping a {@link CouchDbTransformationException}
	 * is thrown.
	 * 
	 * @throws CouchDbTransformationException	if there is any error loading the attributes mapping
	 *											from the properties file, embedded as a resource.
	 */
	@Override
	public void execute() throws CouchDbTransformationException {
		
		try {
			
			this.loadMappings();
		
		} catch(IOException ioex) {
			
			throw new CouchDbTransformationException("Could not initialise the attributes map.", ioex);
		}
		
		super.execute();
		
	}
	
	/**
	 * Adds metadata about a run that are required to compose the {@link RunDetails}
	 * instance. This step is necessary because some of the properties that originally
	 * were stored in the {@link Run} instance are now moved to the {@link RunDetails}
	 * instance.
	 * 
	 * @param runId		a {@link String} representing the unique identifier of the {@link
	 * 					Run} instance that originally owned the information stored in
	 * 					<i>details</i>.
	 * 
	 * @param details	a {@link Map} implementation that is used to store the key-value
	 * 					pairs of attributes originally stored in the {@link Run} instance
	 * 					and that now are stored in the {@link RunDetails} instance.
	 */
	public void addDetails(String runId, Map<String, Object> details) {
		
		this.details.put(runId, details);
	}

	/**
	 * This method iterates over the fields of the document and changes the values
	 * that are required. The method extends the base class implementation by copying
	 * the optimisation specific back-end attributes originally stored in the <i>run</i>
	 * document into the <i>attributes</i> property.
	 * 
	 * @param content	a {@link Map} implementation representing an instance of the
	 * 					previous version of the {@link RunDetails} class, which was
	 * 					originally called <i>OptimizationResult</i>.
	 * 
	 * @return 	a {@link Map} implementation representing the corresponding instance
	 * 			of {@link RunDetails}.
	 * 
	 * 
	 * @throws CouchdbDocumentTransformationException	if there is any error in the
	 * 													conversion process.
	 * 
	 */
	@Override
	protected Map<String,Object> transformContent(String id, Map<String, Object> content) throws CouchDbDocumentTransformationException {

		// we want a true clone.
		//
		
		String runId = (String) content.get(CouchDbRunDetailsTransformation.RUN_ID_PROPERTY);
		
		Map<String,Object> runDetails = this.details.get(runId);
		if (runDetails != null) {
			
			
			this.remap(this.mappings, runDetails, true);
			
			content.put(CouchDbRunDetailsTransformation.ATTRIBUTES_PROPERTY, runDetails);
		}
		
		return content;
		
	}
	
	/**
	 * Gets the name of the type discriminator property for <i>OptimzationResult</i> documents. This property
	 * name if different from the new one will be replaced, otherwise only the property value will be
	 * updated.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbTemplateTransformation#OLD_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getOldTypeDiscriminator() {
		
		return CouchDbRunDetailsTransformation.OLD_TYPE_DISCRIMINATOR;
	}

	/**
	 * Gets the name of the type discriminator property for {@link RunDetails} documents aimed at replacing
	 * the previous <i>OptimizationResult</i> documents.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbTemplateTransformation#NEW_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getNewTypeDiscriminator() {
		
		return CouchDbRunDetailsTransformation.NEW_TYPE_DISCRIMINATOR;
	}

	/**
	 * Gets the updated value that is mapped by the type discriminator property for template documents.
	 * The value corresponds the {@link RunDetails} simple class name.
	 * 
	 * @return 	a {@link String} containing the simple class name of the {@link RunDetails} class as returned
	 * 			by {@link Class#getSimpleName()}.
	 */
	@Override
	protected String getTypeDiscriminatorValue() {
		
		return RunDetails.class.getSimpleName();
	}
	
	/**
	 * This method loads the mappings that are used to replace the property names and delete
	 * some of the previous existing properties that are not used anymore.
	 * 
	 * @throws IOException	a {@link IOException} if there is any error loading the mappings
	 * 						specification from the resources.
	 */
	protected void loadMappings() throws IOException {
		
		InputStream mappings = this.getClass()
				   .getClassLoader()
				   .getResourceAsStream(CouchDbRunDetailsTransformation.SOURCE_ATTRIBUTES_MAPPING);
		if (mappings != null) {
		
			this.mappings = new Properties();
			this.mappings.load(mappings);
		
		} else {
		
			// we need to throw an exception because we're missing an
			// essential part
			
			throw new IOException("Resource file for attributes is not found [Path: " + CouchDbRunDetailsTransformation.SOURCE_ATTRIBUTES_MAPPING +"].");
		}
	}

}