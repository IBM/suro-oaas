package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.store.ModelRepository;

/**
 * A transient, in-memory, implementation of the repository holding the optimization models. It should not be used in
 * the actual application logic, only as a last resort to avoid double effort and knowing about all the limitations and
 * restrictions of this implementation.
 *
 * @author Peter Ilfrich
 */
public class TransientModelRepository extends AbstractTransientRepository<Model, Model> implements ModelRepository {



    @Override
    public Model findByDefaultFlag() {
        for (Model model : repositoryContent.values()) {
            if (model.isDefaultModel()) {
                return model;
            }
        }
        return null;
    }

    @Override
    public void addItem(Model item) {
        // item.setOpsFile(null);
        // item.setModFile(null);
        super.addItem(item);
    }

    @Override
    public void updateItem(Model item) {

        super.updateItem(item);
    }
}
