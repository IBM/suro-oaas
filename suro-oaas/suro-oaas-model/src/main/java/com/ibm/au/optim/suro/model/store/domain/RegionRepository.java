package com.ibm.au.optim.suro.model.store.domain;

import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.store.Repository;

/**
 * The repository interface for all the regions in the system.
 *
 * @author Peter Ilfrich
 */
public interface RegionRepository extends Repository<Region> {

    /**
     * A {@link String} constant that contains the name of the attribute that will contain
     * the instance of the {@link RegionRepository} implementation that has been injected
     * into the environment.
     */
    String REGION_REPOSITORY_INSTANCE = "repo:region:instance";

    /**
     * A {@link String} constant that contains the name of the parameter that will contain
     * the name of the type of {@link RegionRepository} that will be used in the application.
     */
    String REGION_REPOSITORY_TYPE = RegionRepository.class.getName();


    /**
     * Find a region object in the repository by name.
     * @param name - the name to look for
     * @return - a region, who has a name that matches the parameter or null.
     */
    Region findByName(String name);
}
