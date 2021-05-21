package com.ibm.au.optim.suro.model.store.domain;

import java.util.List;

import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.store.Repository;

/**
 * Repository interface for access to the hospital storage.
 *
 * @author Peter Ilfrich
 */
public interface HospitalRepository extends Repository<Hospital> {

    /**
     * A {@link String} constant that contains the name of the attribute that will contain
     * the instance of the {@link HospitalRepository} implementation that has been injected
     * into the environment.
     */
    String HOSPITAL_REPOSITORY_INSTANCE = "repo:hospital:instance";


    /**
     * A {@link String} constant that contains the name of the parameter that will contain
     * the name of the type of {@link HospitalRepository} that will be used in the application.
     */
    String HOSPITAL_REPOSITORY_TYPE = HospitalRepository.class.getName();


    /**
     * Retrieves a list of all hospitals within a specific region
     * @param regionId - the region Id which all hospitals have to belong to.
     * @return - a list of hospital instances from the repository
     */
    List<Hospital> findByRegion(String regionId);

}
