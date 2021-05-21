package com.ibm.au.optim.suro.core.migration.preparer.impl.couch;

import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.impl.couch.AbstractCouchDbRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Class <b>CouchDbAttachmentPreparer</b>. This special implementation of  {@link DatabasePreparer} takes care of 
 * making sure that all the documents in the database have the proper attachments.
 * </p>
 * <p>
 * With the rework of the attachment handling (#1514 -> #1564) we introduced a new field 'attachments' in every
 * {@link Entity}. Later we changed the repository to load the attachments when the document is retrieved from 
 * the repository. This required to remove the attachments field of the store object again to clean up the data. 
 * This preparer addresses this issue by scanning every document in the database, checking whether the field 
 * <i>content.attachments</i> exists and removes the field from the document. It uses the {@link ModelRepository} 
 * to access the CouchDB connector.
 * </p>
 * <p>
 * <b>NOTE<b>: this preparer might get expensive the larger the database is because it is scanning every document on
 * startup ...
 * </p>
 *
 * @author Peter Ilfrich
 */
public class CouchDbAttachmentPreparer implements DatabasePreparer {

	/**
	 * A {@link Logger} implementation that is used to log all the messages of the instances
	 * of this class.
	 */
    private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbAttachmentPreparer.class);

    /**
     * The list of repositories to check. Some repositories that don't have attachments can be ignored.
     */
    private final String repositoryKey = ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE;
    
    /**
     * A {@link String} constant that can be used to reference the property that contains the
     * bean stored within the {@link CouchDbDocument}. This is used to access the data object.
     */
    private static final String CONTENT_PROPERTY = "content";
    /**
     * A {@link String} constant that can be used to reference the property that contains the
     * list of attachments in the bean. These are the ones that need to be removed because they're
     * stored at {@link CouchDbDocument} level.
     */
    private static final String ATTACHMENTS_PROPERTY = "attachments";


    /**
     * The map that is used for the check and will contain the repository name as key and the list of document ids as
     * value.
     */
    private List<String> inconsistentDocuments;

    /**
     * This method scans all the documents in the database and exports them as {@link Map} 
     * implementations. It then checks for the existence of a <i>content.attachments</i>
     * field, if the field is found, the document is stored in an internal list.
     * 
     * @param env	a {@link Environment} implementation that is used to access the configuration
     * 				and the collection of shared objects.
     * 
     * @return 	{@literal true} if any document is found with attachment. This means that
     * 			the list is not empty.
     */
    @SuppressWarnings("unchecked")
	@Override
    public boolean check(Environment env) throws Exception {
    	
        inconsistentDocuments = new ArrayList<>();
        @SuppressWarnings("rawtypes")
		AbstractCouchDbRepository repo = (AbstractCouchDbRepository) env.getAttribute(repositoryKey);

        Map<String, Object> map = new HashMap<>();
        // check each document in the repository (this will retrieve ALL documents in the database)
        for (String docId : repo.getConnector().getAllDocIds()) {
        	
            map = repo.getConnector().get(map.getClass(), docId);
            if (map.get(CouchDbAttachmentPreparer.CONTENT_PROPERTY) != null && ((Map<String, Object>) map.get(CouchDbAttachmentPreparer.CONTENT_PROPERTY)).keySet().contains(CouchDbAttachmentPreparer.ATTACHMENTS_PROPERTY)) {
                this.inconsistentDocuments.add(docId);
            }
        }

        return this.inconsistentDocuments.size() > 0;
    }


    /**
     * This method iterates over all the documents that have been found during the check phase
     * and removes all the {@link CouchDbAttachmentPreparer#ATTACHMENTS_PROPERTY} fields from the
     * identified documents. It then updates the database with the new version of the document.
     * 
     * @param env	a {@link Environment} implementation that is used to access the configuration
     * 				and the collection of shared objects.
     */
    @SuppressWarnings("unchecked")
	@Override
    public void execute(Environment env) throws Exception {
    	
        @SuppressWarnings("rawtypes")
		AbstractCouchDbRepository repo = (AbstractCouchDbRepository) env.getAttribute(repositoryKey);

        Map<String, Object> map = new HashMap<>();
        LOGGER.debug("- Removing content.attachments field from " + inconsistentDocuments.size() + " documents.");
        for (String docId : inconsistentDocuments) {
           
        	map = repo.getConnector().get(map.getClass(), docId);
            
        	((Map<String, Object>) map.get(CouchDbAttachmentPreparer.CONTENT_PROPERTY)).remove(CouchDbAttachmentPreparer.ATTACHMENTS_PROPERTY);
            
        	repo.getConnector().update(map);
        }

    }

    /**
     * This method simply verifies that none of the documents in the database has an attachment
     * field. It does so by running {@link CouchDbAttachmentPreparer#check(Environment)} again.
     * 
     * @param env	a {@link Environment} implementation that is used to access the configuration
     * 				and the collection of shared objects.
     */
    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }

}
