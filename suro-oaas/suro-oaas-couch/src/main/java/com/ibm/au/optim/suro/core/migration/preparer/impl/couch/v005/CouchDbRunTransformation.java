/**
 * 
 */
package com.ibm.au.optim.suro.core.migration.preparer.impl.couch.v005;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.entities.Template;

/**
 * <p>
 * Class <b>CouchDbRunTransformation</b>. This class extends {@link CouchDbDocumentTransformation}
 * and specialises the transformation operation to convert previous instances of the optimisation 
 * runs in <i>CouchDb</i> with documents that can be deserialised instances of the udpated type 
 * <i>Run</i>.
 * </p>
 * <p>
 * This object performs a complete transformation of the original instance into the new version of
 * the {@link Run} document. The only attribute that is not mapped are the parameters which are
 * not essential to the consistency of the system.
 * </p>
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class CouchDbRunTransformation extends CouchDbDocumentTransformation {
	
	/**
	 * A {@link String} constant that contains the name of the resource file describing
	 * the mappings from the {@link Run} attributes to the corresponding {@link RundDetails}
	 * document.
	 */
	public static final String SOURCE_ATTRIBUTES_MAPPING 	= "schemas/v0.0.5/Run.properties";
	
	/**
	 * A {@link String} constant that contains the name of the original type
	 * used in the previous version of the data model for documents representing
	 * optimisation run entities.
	 */
	public static final String SOURCE_DOCUMENT 				= "CouchDbRun";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the previous version of the data model.
	 */
	public static final String OLD_TYPE_DISCRIMINATOR 		= "cdbRun";
	/**
	 * A {@link String} constant that contains the name of the type discriminator
	 * property used in the new version of the data model.
	 */
	public static final String NEW_TYPE_DISCRIMINATOR  		= "cdbRun";
	
	/**
	 * A {@link String} constant that contains the name of the attribute <i>failureType</i>.
	 * This attribute will be removed and stashed for later, since it will be moved to the
	 * {@link RunDetails} document.
	 */
	public static final String FAILURE_TYPE_ATTRIBUTE 		= "failureType";
	
	/**
	 * A {@link String} constant that contains the name of the attribute <i>failureMessage</i>.
	 * This attribute will be removed and stashed for later, since it will be moved to the
	 * {@link RunDetails} document.
	 */
	public static final String FAILURE_MESSAGE_ATTRIBUTE 	= "failureMessage";

	/**
	 * A {@link String} constant that contains the name of the attribute <i>exceptionType</i>.
	 * This attribute will be removed and stashed for later, since it will be moved to the
	 * {@link RunDetails} document.
	 */
	public static final String EXCEPTION_TYPE_ATTRIBUTE 	= "exceptionType";

	/**
	 * A {@link String} constant that contains the name of the attribute <i>exceptionMessage</i>.
	 * This attribute will be removed and stashed for later, since it will be moved to the
	 * {@link RunDetails} document.
	 */
	public static final String EXCEPTION_MESSAGE_ATTRIBUTE 	= "exceptionMessage";

	/**
	 * A {@link String} constant that contains the name of the attribute <i>interruptionType</i>.
	 * This attribute will be removed and stashed for later, since it will be moved to the
	 * {@link RunDetails} document.
	 */
	public static final String INTERRUPTION_TYPE_ATTRIBUTE 	= "interruptionType";
	
	/**
	 * A {@link String} constant that contains the name of the attribute <i>parameters</i>.
	 * This attributes will be removed because it takes much more work to remap them and
	 * they are not essential to maintain the consistency of the system.
	 */
	public static final String PARAMETERS_ATTRIBUTE 		= "parameters";
	
	/**
	 * A {@link CouchDbRunDetailsTransformation} instance that is used to collect the
	 * specific attributes for each run document that are no more stored in the run
	 * itself but into a secondary document.
	 */
	protected CouchDbRunDetailsTransformation runDetails;
	
	/**
	 * A {@link Properties} instance that can be used to store the mappings of the old names
	 * of the document attributes to the new names.
	 */
	protected Properties mappings = null;
	
	

	/**
	 * Initialises an instance of {@link CouchDbRunTransformation}. This constructor calls
	 * the base constructor with the specific name of the class type that was previously
	 * used to persist run entities.
	 */
	public CouchDbRunTransformation() {
		super(CouchDbRunTransformation.SOURCE_DOCUMENT);
	}

	/**
	 * This method performs the transformation of {@link Run} documents from the
	 * previous version into the new version as defined by the updated data model.
	 * All the properties in the original run documents are mapped to the new ones
	 * and those in excess are stored into a configured instance of the transformation
	 * class that will convert the <i>OptimizationResult</i> document into {@link 
	 * RunDetails} instances that correspond to them.
	 * 
	 * @throws CouchDbTransformationException 	if any not recoverable error occurs
	 * 											during the transformation process.
	 */
	@Override
	public void execute() throws CouchDbTransformationException {
		
		
		// we load the attributes mapping so that we can
		// transform the instances. These define how we
		// should remap the key names.
		//
		try {
			
			this.loadMappings();
		
		} catch(IOException ioex) {
			
			throw new CouchDbTransformationException("Could not initialise the attributes map.", ioex);
		}
		
		// we clear up the details so that we can be sure
		// that in case of previous execution we are not
		// mixing up ids.
		//
		if (this.runDetails != null) {
			
			this.runDetails.clearDetails();
		}
		
		super.execute();
		
	}
	
	/**
	 * Sets the {@link CouchDbRunDetailsTransformation} instance that will be used
	 * to transform <i>OptimizationResult</i> documents into their corresponding
	 * {@link RunDetails} instances. This component will collect all those attributes
	 * that are now stored in the {@link RunDetails} rather than in the run.
	 * 
	 * @param runDetails	a {@link CouchDbRunDetailsTransformation} instance used to
	 * 						collect the excess attributes identified for each document
	 * 						during the transformation. It can be {@literal null}.
	 */
	public void setRunDetailsTransformation(CouchDbRunDetailsTransformation runDetails) {
		
		this.runDetails = runDetails;
	}

	/**
	 * This method iterates over the fields of the document and changes the values
	 * that are required. The following transformation are applied:
	 * <ul>
	 * <li></li>
	 * <li></li>
	 * </ul>
	 * 
	 * @param source	a {@link Map} implementation representing an instance of the
	 * 					previous version of the {@link Run} class.
	 * 
	 * @return 	a {@link Map} implementation representing the corresponding instance
	 * 			of {@link Run} updated to the new version.
	 * 
	 * 
	 * @throws CouchdbDocumentTransformationException	if there is any error in the
	 * 													conversion process.
	 * 
	 */
	@Override
	protected Map<String, Object> transformContent(String id, Map<String, Object> content) throws CouchDbDocumentTransformationException {

		
		// we collect the surplus attributes into the following map
		// so that we can pass along it later to the transformation
		// that will need these attributes to complete the object.
		//
		Map<String, Object> details = new HashMap<String, Object>();
		
		Object value = content.remove(CouchDbRunTransformation.FAILURE_TYPE_ATTRIBUTE);
		details.put(CouchDbRunTransformation.FAILURE_TYPE_ATTRIBUTE, value);
		
		value = content.remove(CouchDbRunTransformation.FAILURE_MESSAGE_ATTRIBUTE);
		details.put(CouchDbRunTransformation.FAILURE_MESSAGE_ATTRIBUTE, value);
		
		value = content.remove(CouchDbRunTransformation.EXCEPTION_TYPE_ATTRIBUTE);
		details.put(CouchDbRunTransformation.EXCEPTION_TYPE_ATTRIBUTE, value);
		
		value = content.remove(CouchDbRunTransformation.EXCEPTION_MESSAGE_ATTRIBUTE);
		details.put(CouchDbRunTransformation.EXCEPTION_MESSAGE_ATTRIBUTE, value);
		
		value = content.remove(CouchDbRunTransformation.INTERRUPTION_TYPE_ATTRIBUTE);
		details.put(CouchDbRunTransformation.INTERRUPTION_TYPE_ATTRIBUTE, value);
		
		
		
		if (this.runDetails != null) {
			
			this.runDetails.addDetails(id, details);
		}
		
		this.remap(this.mappings, content, true);
		
		// we ch
		//
		content.remove(CouchDbRunTransformation.PARAMETERS_ATTRIBUTE);
		
		
		return content;
	
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
				   .getResourceAsStream(CouchDbRunTransformation.SOURCE_ATTRIBUTES_MAPPING);
		if (mappings != null) {
		
			this.mappings = new Properties();
			this.mappings.load(mappings);
		
		} else {
		
			// we need to throw an exception because we're missing an
			// essential part
			
			throw new IOException("Resource file for attributes is not found [Path: " + CouchDbRunTransformation.SOURCE_ATTRIBUTES_MAPPING +"].");
		}
	}
	
	/**
	 * Gets the name of the type discriminator property for <i>Run</i> documents. This property name if
	 * different from the new one will be replaced, otherwise only the property value will be updated.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbRunTransformation#OLD_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getOldTypeDiscriminator() {
		
		return CouchDbRunTransformation.OLD_TYPE_DISCRIMINATOR;
	}
	/**
	 * Gets the name of the type discriminator property for {@link Run} documents aimed at replacing the
	 * previous <i>Run</i> documents.
	 * 
	 * @return a {@link String} pointing to {@link CouchDbRunTransformation#NEW_TYPE_DISCRIMINATOR}.
	 */
	@Override
	protected String getNewTypeDiscriminator() {
		
		return CouchDbRunTransformation.NEW_TYPE_DISCRIMINATOR;
	}
	
	/**
	 * Gets the updated value that is mapped by the type discriminator property for template documents.
	 * The value corresponds the {@link Template} simple class name.
	 * 
	 * @return 	a {@link String} containing the simple class name of the {@link Run} class as returned
	 * 			by {@link Class#getSimpleName()}.
	 */
	@Override
	protected String getTypeDiscriminatorValue() {

		return Run.class.getSimpleName();
	}
}