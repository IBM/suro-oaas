package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.RunDetails;
import com.ibm.au.optim.suro.model.store.RunDetailsRepository;

/**
 * @author Peter Ilfrich
 */
public class TransientOptimizationResultRepository  extends AbstractTransientRepository<RunDetails, RunDetails> implements RunDetailsRepository {

    @Override
    public RunDetails findByRunId(String runId) {
        for (RunDetails r : repositoryContent.values()) {
            if (runId != null && runId.equals(r.getRunId()))
                return r;
        }
        return null;
    }
}
