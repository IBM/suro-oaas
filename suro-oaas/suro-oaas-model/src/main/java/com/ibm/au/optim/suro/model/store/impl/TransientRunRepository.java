package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.entities.Run;


import java.util.ArrayList;
import java.util.List;

/**
 * Transient implementation of the run repository. It offers methods to get the last run, find runs by their job ID or
 * strategy ID and other CRUD operations.
 *
 * @author Peter Ilfrich
 */
public class TransientRunRepository extends AbstractTransientRepository<Run, Run> implements RunRepository {


    @Override
    public Run getLast() {
        List<Run> runs = this.getAll();
        Run result = null;
        long latest = 0;
        if (!runs.isEmpty()) {
            for (Run run : runs) {
                if (run.getStartTime() >= latest) {
                    latest = run.getStartTime();
                    result = run;
                }
            }
        }
        return result;
    }

    @Override
    public void addItem(Run item) {
        item.setStartTime(System.currentTimeMillis());
        super.addItem(item);
    }


    @Override
    public Run findByJobId(String jobId) {
        for (Run r : repositoryContent.values()) {
            if (jobId != null && jobId.equals(r.getJobId())) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Run> findByTemplateId(String templateId) {
        List<Run> result = new ArrayList<>();
        for (Run r : repositoryContent.values()) {
            if (templateId != null && templateId.equals(r.getTemplateId())) {
                result.add(r);
            }
        }
        return result;
    }
}
