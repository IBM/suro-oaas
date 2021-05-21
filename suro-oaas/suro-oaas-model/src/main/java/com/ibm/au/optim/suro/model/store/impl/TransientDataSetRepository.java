package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.store.DataSetRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Transient implementation of the data set repository.
 *
 * @author Peter Ilfrich
 */
public class TransientDataSetRepository extends AbstractTransientRepository<DataSet, DataSet> implements DataSetRepository {


    @Override
    public List<DataSet> findByModelId(String modelId) {

        List<DataSet> result = new ArrayList<>();

        for (DataSet ds : this.repositoryContent.values()) {
            if (modelId != null && modelId.equals(ds.getModelId())) {
                result.add(ds);
            }
        }

        return result;
    }

    @Override
    public void addItem(DataSet item) {
        // make sure the dat file is never stored in document storage
        item.setDatFile(null);
        super.addItem(item);
    }

    @Override
    public void updateItem(DataSet item) {
        // make sure the dat file is never stored in document storage
        item.setDatFile(null);
        super.updateItem(item);
    }
}
