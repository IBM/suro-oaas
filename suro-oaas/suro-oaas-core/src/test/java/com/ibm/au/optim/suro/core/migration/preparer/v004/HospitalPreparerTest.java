package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientHospitalRepository;

import java.util.Properties;

import com.ibm.au.jaws.web.core.runtime.Environment;

import junit.framework.TestCase;

/**
 * @author Peter Ilfrich
 */
public class HospitalPreparerTest extends TestCase {

    public void testValidationEmptyRepo() throws Exception {
        Environment env = createEnvironment();
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);

        HospitalPreparer p = new HospitalPreparer();
        assertFalse(p.validate(env));
        assertEquals(0, repo.getAll().size());
    }

    public void testValidationWithData() throws Exception {
        Environment env = createEnvironment();
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        repo.addItem(new Hospital());

        HospitalPreparer p = new HospitalPreparer();
        assertTrue(p.validate(env));
        assertEquals(1, repo.getAll().size());
    }

    public void testCheckEmptyRepo() throws Exception {
        Environment env = createEnvironment();
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);

        HospitalPreparer p = new HospitalPreparer();
        assertTrue(p.check(env));
        assertEquals(0, repo.getAll().size());
    }

    public void testCheckWithData() throws Exception {
        Environment env = createEnvironment();
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        repo.addItem(new Hospital());

        HospitalPreparer p = new HospitalPreparer();
        assertFalse(p.check(env));
        assertEquals(1, repo.getAll().size());
    }

    public void testExecute() throws Exception {
        Environment env = createEnvironment();
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);

        HospitalPreparer p = new HospitalPreparer();
        p.execute(env);
        assertEquals(1, repo.getAll().size());
    }




    private Environment createEnvironment() {
        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE, new TransientHospitalRepository());

        return env;
    }
}
