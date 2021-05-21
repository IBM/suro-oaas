package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.optim.suro.model.entities.domain.Hospital;
import com.ibm.au.optim.suro.model.store.DatabasePreparer;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

/**
 * This preparer will make sure that there's at least one hospital in the system.
 * If the HospitalRepository is empty a new demo hospital provided in the hospital.json
 * will be created.
 *
 * @author Peter Ilfrich
 */
public class HospitalPreparer implements DatabasePreparer {
	
	public static final String HOSPITAL_PATH = "/migration/0.0.4/domain/hospital.json";

    @Override
    public boolean check(Environment env) throws Exception {
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);
        return (repo.getAll().size() == 0);
    }

    @Override
    public void execute(Environment env) throws Exception {
        HospitalRepository repo = (HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE);

        // read the json file containing the hospital data into a transient object
        ObjectMapper mapper = new ObjectMapper();
        Hospital parsedHospital = mapper.readValue(this.getClass().getResourceAsStream(HospitalPreparer.HOSPITAL_PATH), new TypeReference<Hospital>() {});

        // create a new CouchDB hospital from the repository
        Hospital demoHospital = new Hospital(parsedHospital.getRegionId(), parsedHospital.getName());

        // copy fields from the parsed hospital to the CouchDB hospital
        demoHospital.setSessionDuration(parsedHospital.getSessionDuration());
        demoHospital.setTheatreCount(parsedHospital.getTheatreCount());
        demoHospital.setTheatreSessionsPerDay(parsedHospital.getTheatreSessionsPerDay());
        demoHospital.setIcuBedCount(parsedHospital.getIcuBedCount());
        demoHospital.setWards(parsedHospital.getWards());
        demoHospital.setDepartments(parsedHospital.getDepartments());
        demoHospital.setUrgencyCategories(parsedHospital.getUrgencyCategories());
        demoHospital.setSpecialistTypes(parsedHospital.getSpecialistTypes());

        // finally add the CouchDB hospital to the repository/database
        repo.addItem(demoHospital);
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }
}
