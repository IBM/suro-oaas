package com.ibm.au.optim.suro.model.store.impl.couch;

import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.couch.CouchDbModel;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;

import org.ektorp.AttachmentInputStream;
import org.ektorp.support.GenerateView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;


/**
 * Class <b>CouchDbModelRepository</b>. This is a {@link RunRepository} specific implementation
 * that provides means to store {@link Model} implementation on a <i>CouchDb</i> instance. The
 * repository uses {@link CouchDbModel} instances to leverage the support of the Ektorp library.
 */
public class CouchDbModelRepository extends AbstractCouchDbRepository<CouchDbModel, Model> implements ModelRepository {

    /**
     * A {@link Logger} implementation that records all the log messages created by instances
     * of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CouchDbModelRepository.class);
    
    
    public static final String VIEW_BY_DEFAULT = "by_defaultFlag";

    /**
     * Creates a new repository instance.
     */
    public CouchDbModelRepository() {
        super(CouchDbModel.class);
    }

    /**
     * Attaches a document in the form of a file to the <i>model</i>. The method reads the
     * content accessible via the input stream and saves it as attachment named <i>fileName</i>
     * with the MIME type defined by <i>contentType</i>, to the given model.
     * 
     * @param model			a {@link Model} instance representing the recipient of the attachment.
     * @param fileName		a {@link String} representing the name of the attachment file as it 
     * 						will be saved in the model.
     * @param contentType	a {@link String} representing the specific MIMe to be associated to
     * 						the attachment.
     * @param data			a {@link InputStream} implementation providing access to the binary
     * 						content of the document to be saved as attachment.
     */
    @Override
    public void attach(Model model, String fileName, String contentType, InputStream data) {
    	
        CouchDbModel cModel = this.proxy.getItem(model.getId());

        AttachmentInputStream ais = new AttachmentInputStream(fileName, data, contentType);

        LOGGER.debug("[{}] >>> Attaching {}", cModel.getId(), fileName);

        long t1 = System.currentTimeMillis();
        cModel.setRevision(this.proxy.getDb().createAttachment(cModel.getId(), cModel.getRevision(), ais));
        long t2 = System.currentTimeMillis();

        model.setRevision(cModel.getRevision());

        LOGGER.debug("[{}] <<< Attaching [{} ms]", cModel.getId(), t2-t1);
    }


    /**
     * Retrieves the attachment identified by <i>id</i> and <i>fileName</i>.
     * 
     * @param id		a {@link String} representing the unique identifier of the
     * 					{@link Model} that owns the attachment named <i>fileName</i>.	
     * @param fileName	a {@link String} representing the file name of the attachment.
     */
    @Override
    public InputStream getAttachment(String id, String fileName) {

        return this.proxy.getDb().getAttachment(id, fileName);
    }

    /**
     * Retrieves the default model from the repository. The method queries queries
     * the repository to identify the {@link Model} instance whose value of the
     * flag {@link Model#isDefaultModel()} is set to {@literal true}.
     * 
     * @return 	a {@link Model} instance, representing the default model. If this 
     * 			instance is not {@literal null}, this means that the value of 
     * 			{@link Model#isDefaultModel()} returned by this instance is {@literal 
     * 			true}. When the value returned is {@literal null} this may indicate
     * 			either that there is no model set as default, or there are more than
     * 			one model, and in this case an error is logged.
     */
    @Override
    @GenerateView
    public Model findByDefaultFlag() {
    	
    	Model model = null;
    	
        List<CouchDbModel> models = this.proxy.getView(CouchDbModelRepository.VIEW_BY_DEFAULT);
        if (models.size() == 1) {
        	
        	model = this.getContent(models.get(0));
        	
        } else if (models.size() > 0) {
            
        	LOGGER.error("There is more than one default model in the system", new Exception("There is more than one default model declared"));
        
        }
        
        return model;
    }
}
