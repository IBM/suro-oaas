/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Template;


/**
 * <p>
 * Class <b>CouchDbModelTransformation</b>. This class extends {@link CouchDbDocumentTransformation}
 * and specialises the transformation operation to convert previous instances of the optimisation 
 * model in <i>CouchDb</i> with documents that can be deserialised instances of type <i>Model</i>.
 * </p>
 * <p>
 * <b>NOTE:</b> this is not a full-fledged transformation, the current transformation has access to the
 * updated versions of the only model that we use and it simply replaces the old one with the former.
 * There is no need to implement a full-fledged transformer because there is only one model being used
 * now. What the transformer does, is imply ensure that the original identifiers and revisions numbers
 * are copied to prevent conflicts and inconsistencies.
 * </p>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbModelTransformation extends CouchDbDocumentTransformation {

	/**
	 * A {@link String} constant that contains the name of the resource containing
	 * the definition of the model that will be used to replace the existing one.
	 */
	public static final String SOURCE_TEMPLATES_MAPPING = "schemas/v0.0.5/model.json";
	
	/**
	 * A {@link String} constant that contains the name of the original type
	 * used in the previous version of the data model for documents representing
	 * optimisation model instances.
	 */
	public static final String SOURCE_DOCUMENT = "CouchDbOptimizationModel";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the previous version of the data model.
	 */
	public static final String OLD_TYPE_DISCRIMINATOR = "cdbModel";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the new version of the data model.
	 */
	public static final String NEW_TYPE_DISCRIMINATOR  = "cdbModel";
	
	/**
	 * A {@link Map} implementation that contains the definition of the model
	 * currently used in the new version of the data model. It is much easier
	 * to replace it than trying to fix everything. We only have one model so
	 * it should be fine.
	 */
	protected Map<String,Object> model;

	/**
	 * Initialises an instance of {@link CouchDbModelTransformation}. This constructor
	 * calls the base constructor with the specific name of the class type that was
	 * previously used to persist optimisation model entities.
	 */
	public CouchDbModelTransformation() {
		super(CouchDbModelTransformation.SOURCE_DOCUMENT);
		
		// The name has changed, than we can delete the view name.
		//
		this.setDeleteView(true);
	}
	
	/**
	 * This method overrides the base class implementation and pre-loads the information
	 * about the model document that will be used to replace the existing model. If there
	 * is any error during the loading process a {@link CouchDbTransformationException}
	 * is thrown.
	 * 
	 * @throws CouchDbTransformationException	for any error occurring during the operation.
	 */
	@Override
	public void execute() throws CouchDbTransformationException {
		
		try {
			
			this.loadModel();
			
			
		} catch(IOException ioex) {
			
			throw new CouchDbTransformationException("Could not find model details for replacement.", ioex);
		}
		
		super.execute();
	}

	
	/**
	 * This method loads the data about the model that will be used to
	 * replace the existing one from the resources of the package. 
	 * 
	 * @throws IOException	id there is any error while reading the model
	 * 						file from the resources or deserialising its
	 * 						JSON representation into a {@link Map} instance.
	 */
	@SuppressWarnings("unchecked")
	private void loadModel() throws IOException {
		
		InputStream stream = this.getClass()
								 .getClassLoader()
								 .getResourceAsStream(CouchDbModelTransformation.SOURCE_TEMPLATES_MAPPING);
		
		if (stream != null) {
			
			ObjectMapper mapper = new ObjectMapper();
			this.model = (Map<String,Object>) mapper.readValue(stream, Map.class);
			
		} else {
			
			throw new IOException("Could not find resource file [Path: " + CouchDbModelTransformation.SOURCE_TEMPLATES_MAPPING + "].");
		}
		
	}

	/**
	 * Gets the name of the type discriminator property for <i>Strategy</i> documents. This property
	 * name if different from the new one will be replaced, otherwise only the property value will be
	 * updated.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbTemplateTransformation#OLD_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getOldTypeDiscriminator() {
		
		return CouchDbModelTransformation.OLD_TYPE_DISCRIMINATOR;
	}

	/**
	 * Gets the name of the type discriminator property for {@link Template} documents aimed at replacing
	 * the previous <i>Strategy</i> documents.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbTemplateTransformation#NEW_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getNewTypeDiscriminator() {
		
		return CouchDbModelTransformation.NEW_TYPE_DISCRIMINATOR;
	}

	/**
	 * Gets the updated value that is mapped by the type discriminator property for template documents.
	 * The value corresponds the {@link Template} simple class name.
	 * 
	 * @return 	a {@link String} containing the simple class name of the {@link Template} class as returned
	 * 			by {@link Class#getSimpleName()}.
	 */
	@Override
	protected String getTypeDiscriminatorValue() {
		
		return Model.class.getSimpleName();
	}

	/**
	 * This method simply replaces the existing {@link Map} implementation into with the model
	 * that has been pre-loaded in the preparer. The assumption is that there is only one model
	 * configured in the system and therefore there is not need to do any complex and specific
	 * transformation.
	 * 
	 * @param id		a {@link String} representing the unique identifier of the document that
	 * 					is being transformed.
	 * 
	 * @param content	a {@link Map} implementation representing an instance of the previous 
	 * 					version of the {@link Model} class, which was originally called 
	 * 					<i>OptimizationModel</i>.
	 * 
	 * @throws CouchDbDocumentTransformationException	this implementation will never throw this
	 * 													exception.
	 */
	@Override
	protected Map<String, Object> transformContent(String id, Map<String, Object> content) throws CouchDbDocumentTransformationException {
		
		return this.model;
		
	}

}
