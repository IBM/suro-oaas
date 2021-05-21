package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.store.TemplateRepository;
import com.ibm.au.optim.suro.model.entities.Template;

import java.util.ArrayList;
import java.util.List;

/**
 * Transient implementation of the strategy repository.
 * @author Peter Ilfrich
 */
public class TransientTemplateRepository extends AbstractTransientRepository<Template, Template> implements TemplateRepository {


    @Override
    public List<Template> findByModelId(String modelId) {
        List<Template> result = new ArrayList<>();
        for (Template s : repositoryContent.values()) {
            if (modelId != null && modelId.equals(s.getModelId())) {
                result.add(s);
            }
        }
        return result;
    }
    



}
