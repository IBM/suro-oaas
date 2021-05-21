package com.ibm.au.optim.suro.model.store.domain.impl;

import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.impl.AbstractTransientRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Transient implementation of the hospital repository. This should only be used during automated tests.
 *
 * @author Peter Ilfrich
 */
public class TransientHospitalRepository extends AbstractTransientRepository<Hospital, Hospital> implements HospitalRepository {

    @Override
    public List<Hospital> findByRegion(String regionId) {
        List<Hospital> result = new ArrayList<>();

        for (Hospital h : repositoryContent.values()) {
            if (h.getRegionId().equals(regionId)) {
                result.add(h);
            }
        }

        return result;
    }
}
