package com.ibm.au.optim.suro.model.store.domain.impl;

import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;

/**
 * A transient implementation of the region repository.
 *
 * @author Peter Ilfrich
 */
public class TransientRegionRepository extends AbstractTransientRepository<Region, Region> implements RegionRepository {

    @Override
    public Region findByName(String name) {
        for (Region r : repositoryContent.values()) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }
}
