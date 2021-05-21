package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * This preparer will create a new region provided in the region.json, which will serve as the
 * initial region when you set up a system.
 *
 * @author Peter Ilfrich
 */
public class RegionPreparer implements DatabasePreparer {
	
	public static final String REGION_PATH = "/migration/0.0.4/domain/region.json";

    @Override
    public boolean check(Environment env) throws Exception {
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        return (repo.getAll().size() == 0);
    }

    @Override
    public void execute(Environment env) throws Exception {
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);

        // parse the JSON file into a transient region and create CouchDB object
        Region parse = new ObjectMapper().readValue(this.getClass().getResourceAsStream(RegionPreparer.REGION_PATH), new TypeReference<Region>() {});
        Region demoRegion = new Region(parse.getName());

        // copy fields from the transient object to the CouchDB object
        demoRegion.setFirstIntervalStart(parse.getFirstIntervalStart());
        demoRegion.setIntervalType(parse.getIntervalType());
        demoRegion.setUrgencyCategories(parse.getUrgencyCategories());

        // finally store the region
        repo.addItem(demoRegion);
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }
}
