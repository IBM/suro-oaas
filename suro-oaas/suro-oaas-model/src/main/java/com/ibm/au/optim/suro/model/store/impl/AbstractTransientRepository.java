package com.ibm.au.optim.suro.model.store.impl;


import com.ibm.au.optim.suro.model.entities.Entity;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.store.Repository;
import com.ibm.au.optim.suro.util.SessionIdentifierGenerator;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.AbstractRuntimeService;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Superclass for all transient repository implementations providing base functionality, accessors, storage and mapping
 * of store objects. Implementing sub-classes can completely replace the corresponding CouchDb repository.
 *
 * Views are usually implemented in Java by iterating over the list of documents / store objects in the repository.
 *
 * The intended use case for these repositories is automated testing, where we don't want to rely on CouchDb being
 * available.
 *
 * @author Peter Ilfrich
 */
public abstract class AbstractTransientRepository<Z extends Entity, T> extends AbstractRuntimeService implements Repository<T> {

    /**
     * The environment used to retrieve other repositories
     */
    protected Environment environment;

    /**
     * The generator for the unique document identifiers.
     */
    private SessionIdentifierGenerator generator = new SessionIdentifierGenerator();

    /**
     * The list of documents in this repository. The key represents the document ID and the value is a transient
     * implementation of the optimization model.
     */
    public Map<String, Z> repositoryContent = new HashMap<>();

    /**
     * A map holding a list of attachments. The first key is the document ID, the value is a map from the file name to
     * an InputStream of the actual attachment.
     */
    public Map<String, HashMap<String, String>> repositoryAttachments = new HashMap<>();


	@Override
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        for (Z object : repositoryContent.values()) {
            result.add((T) object);
        }
        return result;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void addItem(T item) {
		Z casted = (Z) item;
        casted.setId(nextId());
        casted.setRevision(nextId());
        repositoryContent.put(casted.getId(), casted);
    }

	@Override
    @SuppressWarnings("unchecked")
    public T getItem(String id) {
        Z item = repositoryContent.get(id);
        if (item != null) {
            return (T) item;
        }

        return null;
    }


    @Override
    public void removeItem(String id) {
        this.repositoryContent.remove(id);
        this.repositoryAttachments.remove(id);
    }

    @Override
    public void removeAll() {
        repositoryContent = new HashMap<>();
        repositoryAttachments = new HashMap<>();
    }

    @Override
    public void updateItem(T item) {
        @SuppressWarnings("unchecked")
		Z cast = (Z) item;
        cast.setRevision(nextId());
        if (repositoryContent.get(cast.getId()) != null) {
            repositoryContent.put(cast.getId(), cast);
        }
    }

    @Override
    public List<Attachment> getAttachments(String id) {
        if (id == null || repositoryContent.get(id) == null) {
            return null;
        }
        return repositoryContent.get(id).getAttachments();
    }

    /**
     * Retrieves the specified attachment from the repository
     * @param id - the id of the document / store object the attachment belongs to
     * @param fileName - the name of the attachment to retrieve
     * @return - the attachment content as input stream
     */
    public InputStream getAttachment(String id, String fileName) {
        HashMap<String, String> docAttachments = repositoryAttachments.get(id);
        if (docAttachments == null) {
            return null;
        }

        if (docAttachments.get(fileName) == null) {
            return null;
        }
        return IOUtils.toInputStream(docAttachments.get(fileName));
    }


    /**
     * Attaches new content as attachment to the repository.
     *
     * @param transientObject - the store object to attach the document to
     * @param fileName - the name of the attachment
     * @param contentType - the content type of the attachment
     * @param data - the content of the attachment represented as input stream
     */
    public void attach(T transientObject, String fileName, String contentType, InputStream data) {
        if (transientObject == null || data == null || fileName == null) {
            return;
        }

        @SuppressWarnings("unchecked")
		Z casted = (Z) transientObject;
        if (repositoryContent.get(casted.getId()) == null) {
            return;
        }


        HashMap<String, String> currentAttachment = repositoryAttachments.get(casted.getId());
        if (currentAttachment == null) {
            currentAttachment = new HashMap<>();
            repositoryAttachments.put(casted.getId(), currentAttachment);
        }

        try {
            // store attachment
            currentAttachment.put(fileName, IOUtils.toString(data));
            // update store object with attachment meta information
            Attachment sa = new Attachment(fileName, IOUtils.toString(data).length(), contentType);
            this.addAttachment(casted, sa);

        } catch (NullPointerException | IOException ex) {
            LoggerFactory.getLogger(this.getClass().getName()).error("Error creating attachment in transient repository", ex);
        }

    }

    /**
     * Returns the next id, determined by the SessionIdentifierGenerator.
     * @return - a new unique identifier
     */
    public String nextId() {
        return generator.next();
    }


    /*
     * Service Methods
     */

    /**
     * This is the default implementation of the {@link AbstractRuntimeService#doBind(Environment)}
     * method. The implementation simply sets the internal reference to <i>Environment</i>.
     * 
     * @param environment	an {@link Environment} implementation used to bind the service with all
     * 						initialisation parameters that are required for the service to function.
     */
    @Override
    protected void doBind(Environment environment) throws Exception {
        this.environment = environment;
    }

    /**
     * This is the default implementation of the {@link AbstractRuntimeService#doRelease()} method.
     * This implementation simply nullifies the reference to the {@link Environment} implementation
     * used to bind the service.
     */
    @Override
    protected void doRelease() throws Exception {
        this.environment = null;
    }

    /**
     * Adds the provided attachment information to the transient object passed into this method.
     * 
     * @param transientObject	the item representing the owning document of the attachment.
     * @param attachment		an {@link Attachment} instance that contains the metadata about
     * 							the attachment.
     */
    protected void addAttachment(Z transientObject, Attachment attachment) {
    	

    	List<Attachment> attachments = transientObject.getAttachments();
    	if (attachments == null) {
    		
    		attachments = new ArrayList<Attachment>();
    	}
    	
    	
    	attachments.add(attachment);
    	
    	// [CV] NOTE: re-assigning the list ensures that in case of implementations that
    	//			  are based on a "copy-on-get" pattern, we still set the attachment
    	//			  to the original container and not only the returned one.
    	//
		transientObject.setAttachments(attachments);
    }
}
