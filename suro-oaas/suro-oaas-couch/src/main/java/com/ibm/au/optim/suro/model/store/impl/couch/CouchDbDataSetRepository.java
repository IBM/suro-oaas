package com.ibm.au.optim.suro.model.store.impl.couch;

import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbDataSet;
import com.ibm.au.optim.suro.model.store.DataSetRepository;

import org.ektorp.AttachmentInputStream;
import org.ektorp.support.GenerateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Class <b>CouchDbDataSetRepository</b>. CouchDB specific implementation for the
 * {@link DataSetRepository} interface. This repository specialises the base class
 * {@link AbstractCouchDbRepository} for the management of {@link DataSet} entities, 
 * via the corresponding {@link CouchDbDataSet} documents.
 * 
 * @author Brendan Haesler
 */
public class CouchDbDataSetRepository extends AbstractCouchDbRepository<CouchDbDataSet, DataSet> implements DataSetRepository {

    /**
     * A {@link Logger} instance that records all the log messages created by instances
     * of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbDataSetRepository.class);
    
	/**
	 * A {@link String} containing the name of the view that is used to
	 * query the database by sorting the documents by the <i>modelId</i>
	 * attribute.
	 */
    public static final String VIEW_BY_MODEL = "by_modelId";

    /**
     * Creates an instance of this repository type.
     */
    public CouchDbDataSetRepository() {
        super(CouchDbDataSet.class);
    }


    /**
     * Attaches the given file to the selected dataset.
     * 
     * @param dataset 		a {@link DataSet} instance representing the dataset owning the document.
     * @param fileName		a {@link String} representing the name of the attachment.	
     * @param contentType	a {@link String} defining the MIME type for the attachment.
     * @param data			a {@link InputStream} implementation that provides access to the content
     * 						of the file.
     */
    @Override
    public void attach(DataSet dataset, String fileName, String contentType, InputStream data) {
    	
        CouchDbDataSet cDataset = this.proxy.getItem(dataset.getId());

        AttachmentInputStream ais = new AttachmentInputStream(fileName, data, contentType);

        LOGGER.debug("[{}] >>> Attaching {}", cDataset.getId(), fileName);

        long t1 = System.currentTimeMillis();
        cDataset.setRevision(this.proxy.getDb().createAttachment(cDataset.getId(), cDataset.getRevision(), ais));
        long t2 = System.currentTimeMillis();

        // update revision on the data set object
        dataset.setRevision(cDataset.getRevision());

        LOGGER.debug("[{}] <<< Attaching [{} ms]", cDataset.getId(), t2 - t1);
    }

    /**
     * Retrieves the specific attachment associated to the given document.
     * 
     * @param id		a {@link String} representing the unique identifier of the document
     * 					owning the attachment.
     * 
     * @param fileName	a {@link String} representing the name of the attachment to access.
     * 
     * @return 	a {@link InputStream} implementation that can be used to read the content
     * 			of the attachment.
     */
    @Override
    public InputStream getAttachment(String id, String fileName) {

        return this.proxy.getDb().getAttachment(id, fileName);
    }

    /**
     * Retrieves the list of data sets that are prepared for the selected model.
     * 
     * @param modelId	a {@link String} representing the unique identifier of the model.
     * 
     * @return 	a {@link List} implementation representing the collection of {@link DataSet}
     * 			instances that have been associated to the model identified by <i>modelId</i>.
     * 			If there are no datasets associated, the returned list is empty.
     */
    @Override
    @GenerateView
    public List<DataSet> findByModelId(String modelId) {
    	
        List<DataSet> output = new ArrayList<>();

        for (CouchDbDataSet set : this.proxy.getView(CouchDbDataSetRepository.VIEW_BY_MODEL, modelId)) {
            output.add(getContent(set));
        }

        return output;
    }
}
