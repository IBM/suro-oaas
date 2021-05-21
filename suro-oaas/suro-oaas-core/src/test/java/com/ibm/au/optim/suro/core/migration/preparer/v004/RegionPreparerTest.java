package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.domain.Region;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientRegionRepository;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * @author Peter Ilfrich
 */
public class RegionPreparerTest {

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidationEmptyRepo() throws Exception {
        Environment env = createEnvironment();
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);

        RegionPreparer p = new RegionPreparer();
        Assert.assertFalse(p.validate(env));
        Assert.assertEquals(0, repo.getAll().size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testValidationWithData() throws Exception {
        Environment env = createEnvironment();
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        repo.addItem(new Region());

        RegionPreparer p = new RegionPreparer();
        Assert.assertTrue(p.validate(env));
        Assert.assertEquals(1, repo.getAll().size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCheckEmptyRepo() throws Exception {
        Environment env = createEnvironment();
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);

        RegionPreparer p = new RegionPreparer();
        Assert.assertTrue(p.check(env));
        Assert.assertEquals(0, repo.getAll().size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCheckWithData() throws Exception {
        Environment env = createEnvironment();
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);
        repo.addItem(new Region());

        RegionPreparer p = new RegionPreparer();
        Assert.assertFalse(p.check(env));
        Assert.assertEquals(1, repo.getAll().size());
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception {
        Environment env = createEnvironment();
        RegionRepository repo = (RegionRepository) env.getAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE);

        RegionPreparer p = new RegionPreparer();
        p.execute(env);
        Assert.assertEquals(1, repo.getAll().size());
    }



    /**
     * 
     * @return
     */
    private Environment createEnvironment() {
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE, new TransientRegionRepository());

        return env;
    }
}
