package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import org.apache.http.entity.ContentType;

import java.io.InputStream;

/**
 * Baisc data set provider, which will provide the content of a dat file in the source code combined with the provided
 * runtime parameters.
 *
 * @author Peter Ilfrich
 */
public class BasicDataSetController extends AbstractSuroService implements DataSetController {


    private DataSetRepository repo = null;

    @Override
    protected void doRelease() {
        this.repo = null;
    }

    @Override
    protected void doBind(Environment environment) {
        this.repo = (DataSetRepository) environment.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
    }


    @Override
    public DataSet getDataSet(String datasetId, boolean loadData) {
        DataSet set = repo.getItem(datasetId);

        // load attachment (dat file)
        if (loadData) {
            set.setDatFile(repo.getAttachment(datasetId, DataSet.FILENAME_DAT_FILE));
        }

        return set;
    }

    @Override
    public DataSet createDataSet(Model model, String label, InputStream datFile) {
        // create document
        DataSet set = new DataSet(label, model.getId(), null);
        repo.addItem(set);

        // add attachments
        if (datFile != null) {
        	repo.attach(set, DataSet.FILENAME_DAT_FILE, ContentType.TEXT_PLAIN.getMimeType(), datFile);
        }

        return set;
    }



    @Override
    public void setRepository(DataSetRepository repo) {
        this.repo = repo;
    }

    @Override
    public DataSetRepository getRepository() {
        return repo;
    }
}
