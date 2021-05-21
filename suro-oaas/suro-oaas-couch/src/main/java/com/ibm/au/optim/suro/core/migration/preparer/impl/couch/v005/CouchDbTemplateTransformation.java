/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.entities.Template;

/**
 * <p>
 * Class <b>CouchDbTemplateTransformation</b>. This class extends {@link CouchDbDocumentTransformation}
 * and specialises the transformation operation to convert previous instances of the optimisation 
 * strategies in <i>CouchDb</i> with documents that can be deserialised instances of type <i>Template</i>.
 * </p>
 * <p>
 * <b>NOTE:</b> this is not a full-fledged transformation, the current transformation has access to the
 * updated versions of the transformation and what it does is simply matching the old instances with the
 * new instances and replacing them by ensuring that identifiers and revision numbers are taken from the
 * original documents to prevent conflicts and incoherences in the data persisted in the database.
 * </p>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbTemplateTransformation extends CouchDbDocumentTransformation {
	
	/**
	 * A {@link String} constant that contains the name of the original type
	 * used in the previous version of the data model for documents representing
	 * optimisation strategy instances.
	 */
	public static final String SOURCE_DOCUMENT = "CouchDbStrategy";
	/**
	 * A {@link String} constant that contains the name of the resource file describing
	 * the mappings from the <i>Strategy</i> attributes to the corresponding {@link Template}
	 * document.
	 */
	public static final String SOURCE_TEMPLATES_MAPPING = "schemas/v0.0.5/templates.json";
	
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the previous version of the data model.
	 */
	public static final String OLD_TYPE_DISCRIMINATOR = "cdbStrategy";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the new version of the data model.
	 */
	public static final String NEW_TYPE_DISCRIMINATOR  = "cdbTemplate";
	
	/**
	 * A {@link String} constant that contains the name of the property in the 
	 * <i>Strategy</i> document that will be used as a key to find the corresponding
	 * instance of {@link Template} to use.
	 */
	public static final String TITLE_ATTRIBUTE = "title";
	/**
	 * A {@link String} constant that contains the name of the property in the {@link
	 * Template} document that maps the <i>title</i> property of the <i>Strategy</i>
	 * document.
	 */
	public static final String LABEL_ATTRIBUTE = "label";
	
	/**
	 * A {@link String} constant that contains the unique identifier of the model
	 * that is the <i>Strategy</i> document are pointing to. The transformation
	 * copies this attribute to the corresponding {@link Template} instance so
	 * that the links are maintained.
	 */
	public static final String MODEL_ID_ATTRIBUTE = "modelId";
	
	/**
	 * A {@link Map} implementation that contains the name of titles of the 
	 * <i>Strategy</i> documents to the corresponding new {@link Template}
	 * instances that are meant to substitute them.
	 */
	protected Map<String,Map<String,Object>> templates = null;
	
	/**
	 * A {@link String} containing the unique identifier of the model of the
	 * last <i>Strategy</i> document that has been processed. This is used to
	 * link the pending new {@link Template} documents that were not present
	 * in the previous data model, but that still refer to the same model.
	 */
	protected String modelId;

	/**
	 * Initialises an instance of {@link CouchDbTemplateTransformation}. This constructor
	 * calls the base constructor with the specific name of the class type previously used
	 * to persist optimisation model entities.
	 */
	public CouchDbTemplateTransformation() {
		super(CouchDbTemplateTransformation.SOURCE_DOCUMENT);

		// The name has changed, than we can delete the view name.
		//
		this.setDeleteView(true);
	}

	/**
	 * This method extends the behaviour of the base class implementation by pre-loading 
	 * the list of templates that are used to replace the <i>Strategy</i> documents in the 
	 * <i>CouchDb</i> database. It then executes the transformation process and for any of
	 * the pending templates that have not been matched, inserts a new <i>CouchDb</i>
	 * document.
	 */
	@Override
	public void execute() throws CouchDbTransformationException {
		
		try {
			
			this.loadTemplates();
			
			
		} catch(IOException ioex) {
			
			throw new CouchDbTransformationException("Cannot setup the template mapping.", ioex);
		}
		
		super.execute();
		
	}
	
	/**
	 * This method replaces the content of all the <i>strategy</i> document with 
	 * the corresponding template and then if the current set of templates are
	 * more than the previous strategies it simply adds the pending templates.
	 * 
	 * @throws CouchDbDocumentTransformationException
	 */
	@Override
	protected void process() throws CouchDbDocumentTransformationException {
		
		super.process();
		
		String modelId = this.getModelId();
		
		// ok now we check which one we have left and check whether
		// we can insert a new document 
		
		boolean emulate = this.isEmulationMode();
		
		for(Entry<String, Map<String, Object>> pairs : this.templates.entrySet()) {
			
			Map<String,Object> template = pairs.getValue();
			template.put(CouchDbTemplateTransformation.MODEL_ID_ATTRIBUTE, modelId);
			
			Map<String,Object> wrapper = new HashMap<String,Object>();
			wrapper.put(CouchDbDocumentTransformation.DOCUMENT_CONTENT_ATTRIBUTE, template);
			wrapper.put(this.getNewTypeDiscriminator(), this.getTypeDiscriminatorValue());
			
			
			if (emulate == false) {
			
			
				// we do not need to catch the exception because there is no revision
				// field set.
				//
				this.connector.create(wrapper);
			
			}
			
			this.trace("<new>", null, wrapper);
			
			
		} 
	}
	
	/**
	 * This method loads the content of the resource files that specifies the replacing
	 * {@link Template} instances and configures a {@link Map} whereby they are accessible
	 * through their <i>label</i> property. This is the property that will be used to match
	 * the value of the <i>title</i> property in the corresponding <i>Strategy</i> documents.
	 * 
	 * @throws IOException	if there is any error while loading the templates resource.
	 */
	protected void loadTemplates() throws IOException {
		
		InputStream content = this.getClass()
				  .getClassLoader()
				  .getResourceAsStream(CouchDbTemplateTransformation.SOURCE_TEMPLATES_MAPPING);

		ObjectMapper mapper = new ObjectMapper();
		
		List<Map<String,Object>> items = mapper.readValue(content, new TypeReference<List<Map<String,Object>>>() {});
		
		this.templates = new HashMap<String, Map<String,Object>>();
		
		for(Map<String,Object> item : items) {
		
			String label = (String) item.get(CouchDbTemplateTransformation.LABEL_ATTRIBUTE);
			this.templates.put(label, item);
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
		
		return CouchDbTemplateTransformation.OLD_TYPE_DISCRIMINATOR;
	}

	/**
	 * Gets the name of the type discriminator property for {@link Template} documents aimed at replacing
	 * the previous <i>Strategy</i> documents.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbTemplateTransformation#NEW_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getNewTypeDiscriminator() {
		
		return CouchDbTemplateTransformation.NEW_TYPE_DISCRIMINATOR;
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
		
		return Template.class.getSimpleName();
	}

	/**
	 * This method looks up the value of the <i>title</i> property of the <i>Strategy</i> document into
	 * the <i>templates</i> map and copies the reference to the <i>model id</i> property so that the
	 * entities are properly linked.
	 * 
	 * @param id	a {@link String} constant representing the unique identifier of the document.
	 * 
	 * @param content 	a {@link Map} implementation that contains the type specific attributes of the
	 * 					<i>Strategy</i> document.
	 * 
	 * @return 	a {@link Map} implementation that contains the specific attributes of the corresponding
	 * 			{@link Template} document with the model identified set.
	 */
	@Override
	protected Map<String,Object> transformContent(String id, Map<String, Object> content) throws CouchDbDocumentTransformationException {
		
		String tmp = (String) content.get(CouchDbTemplateTransformation.TITLE_ATTRIBUTE);
		
		Map<String,Object> template = this.templates.remove(tmp);
		
		tmp = (String) content.get(CouchDbTemplateTransformation.MODEL_ID_ATTRIBUTE);
		this.setModelId(tmp);
		template.put(CouchDbTemplateTransformation.MODEL_ID_ATTRIBUTE, tmp);
		
		return template;
	}

	/**
	 * This method stores the given model identifier. This information is used to fixup the
	 * unmatched template documents, that will be inserted as new items in the <i>CouchDb</i>
	 * database. The assumption is that at present time there is only one model in the database
	 * therefore all the templates will be prepared for (and refer to) that model.
	 * 
	 * @param modelId	a {@link String} representing the unique identifier of the model that
	 * 					owns all the <i>Strategy</i> documents in the database and will also
	 * 					own all the replacing <i>templates</i> documents.
	 */
	protected void setModelId(String modelId) {
		
		this.modelId = modelId;
	}
	
	/**
	 * This method retrieves the previously stored model identifier. This information is used 
	 * to fixup the unmatched template documents, that will be inserted as new items in the 
	 * <i>CouchDb</i> database. The assumption is that at present time there is only one model 
	 * in the database therefore all the templates will be prepared for (and refer to) that model.
	 * 
	 * @return	a {@link String} representing the unique identifier of the model that owns all 
	 * 			the <i>Strategy</i> documents in the database and will also own all the replacing 
	 * <i>templates</i> documents.
	 */
	protected String getModelId() {
		
		return this.modelId;
	}
}