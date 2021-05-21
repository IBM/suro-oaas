package com.ibm.au.optim.suro.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.core.controller.BasicHospitalController;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientHospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientRegionRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



/**
 * Class <b>HospitalApiTest</b>. This class verifies that the implementation
 * of {@link HospitalApi} adheres to the expected behaviour.
 * 
 * @author Peter Ilfrich
 */
public class HospitalApiTest {


	/**
	 * {@link Environment} instance that will be injected into the {@link HospitalApi}
	 * and that constitutes the shared environment between the test class and the
	 * instance being tested.
	 */
	private Environment env = null;
	/**
	 * {@link HospitalApi} instance under test. This instance is continuously recreated
	 * before the execution of every test.
	 */
    private HospitalApi api = new HospitalApi();

    /**
     * {@link HospitalRepository} implementation that is used to configure the controller
     * instance used by the {@link HospitalApi} instance under test.
     */
    private HospitalRepository hospitalRepo = new TransientHospitalRepository();
    
    /**
     * {@link RegionRepository} implementation that is used to configure the controller
     * instance used by the {@link HospitalApi} instance under test.
     */
    private RegionRepository regionRepo = new TransientRegionRepository();

    /**
     * A {@link ObjectMapper} instance used to read and write JSON representation of
     * {@link Hospital}, {@link UrgencyCategory}, {@link Department}, {@link Ward}
     * and {@link SpecialistType} instances.
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * A {@link String} instance containing the label that is used to name the {@link Hospital}
     * instances and other instances under test.
     */
    private String label = "label";
    
    /**
     * A {@link String} instance containing the name of the region that the hospital belongs to.
     * The region affects the information about the performance target and points that the hospital
     * is expected to meet.
     */
    private String region = "Australia/Victoria";

    /**
     * This method tests the functionality exposed by the API for creating a {@link Hospital}. The method
     * first sets up a dummy instance of the {@link Hospital} class. Then, the following tests are executed:
     * <ul>
     * <li>creation of an hospital instance with valid data: expected response code 201, the hospital instance
     * should be added to the repository.</li>
     * <li>creation of an hospital instance with the invalid data: expected response code 400, the hospital 
     * should not be added to the repository.</li>
     * <li>creation of an hospital instance when an existing hospital is already present: expected response code
     * 409, the hospital should not be added to the repository.</li>
     * <li>creation of an hospital instance when a controller is not available: expected response code 503, no
     * hospital instance should be added to the repository.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void testCreateHospital() throws Exception {
    	
        this.setupApi();
        
        // 1. test creation with invalid data.
        //
        Response res = this.api.createHospital(new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Region(this.region, new ArrayList<UrgencyCategory>(), Region.INTERVAL_BIWEEKLY, 123))));
        Assert.assertEquals(400, res.getStatus());

        // 2. test the sunny day scenario.
        //
        Hospital hospital = new Hospital(this.region, this.label);

        res = this.api.createHospital(new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(201, res.getStatus());

        Assert.assertEquals(1, this.hospitalRepo.getAll().size());
        
        // we need to test that the hospital added is the same.
        Hospital created = (Hospital) res.getEntity();
        Hospital added = this.hospitalRepo.getItem(created.getId());
        Assert.assertEquals(created, added);
        
        // [CV] NOTE: we should perform a different test, but at the moment Hospital.equals(...) has
        // 			  not been defined as it should and therefore, it only returns true if the instance
        //			  is exactly the same.
        Assert.assertEquals(hospital.getName(), added.getName());
        Assert.assertEquals(hospital.getRegionId(), added.getRegionId());
        

        Assert.assertEquals(1, this.hospitalRepo.getAll().size());
        
        
        // 3. test creation when already existing.
        //
        hospital = new Hospital(this.region, this.label + "new");
        res = this.api.createHospital(new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(409, res.getStatus());
        
        Assert.assertEquals(1, this.hospitalRepo.getAll().size());
        

        // 4. test creation when controller is not available.
        //
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.createHospital(new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Region(this.region, new ArrayList<UrgencyCategory>(), Region.INTERVAL_BIWEEKLY, 123))));
        Assert.assertEquals(503, res.getStatus());
    }

    /**
     * This method tests the functionality exposed by the API for creating a {@link Department} for an hospital. 
     * The method first sets up a dummy instance of the {@link Hospital} class. Then, the following tests are 
     * executed:
     * <ul>
     * <li>creation of a department instance with valid data: expected response code 201, the department instance
     * should be added to the repository.</li>
     * <li>creation of a department instance with the invalid data: expected response code 400, the department 
     * should not be added to the repository.</li>
     * <li>creation of a department for a hospital that does not exist: expected response code 404, the department
     * instance should not be created.</li>
     * <li>creation of a department instance when an existing department is already present: expected response code
     * 409, the department should not be added to the repository.</li>
     * <li>creation of a department instance when a controller is not available: expected response code 503, no
     * department instance should be added to the repository.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void testCreateDepartment() throws Exception {
    	
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.createDepartment(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Department(this.label, 5))));
        Assert.assertEquals(201, res.getStatus());
        Assert.assertEquals(1, hospital.getDepartments().size());

        res = this.api.createDepartment(null, new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Department(this.label, 5))));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.createDepartment("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Department(this.label, 5))));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.createDepartment(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Department(label, 5))));
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(1, hospital.getDepartments().size());

        res = this.api.createDepartment(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(1, hospital.getDepartments().size());

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.createDepartment(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Department(this.label, 5))));
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the functionality exposed by the API for creating a {@link Ward} for an hospital. 
     * The method first sets up a dummy instance of the {@link Hospital} class. Then, the following tests 
     * are executed:
     * <ul>
     * <li>creation of a ward instance with valid data: expected response code 201, the ward instance should
     * be added to the repository.</li> 
     * <li>creation of a ward instance with the invalid data: expected response code 400, the ward should 
     * not be added to the repository.</li>
     * <li>creation of a ward for a hospital that does not exist: expected response code 404, the ward instance
     * should not be created.</li>
     * <li>creation of a ward instance when an existing ward is already present: expected response code 409.
     * the ward should not be added to the repository.</li>
     * <li>creation of a ward instance when a controller is not available: expected response code 503, no
     * ward instance should be added to the repository.</li>
     * </ul>
     * 
     * @throws Exception
     */    
    @Test
    public void testCreateWard() throws Exception {
    	
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.createWard(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Ward(this.label, 5))));
        Assert.assertEquals(201, res.getStatus());
        Assert.assertEquals(1, hospital.getWards().size());
        
        res = api.createWard(null, new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Ward(this.label, 5))));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.createWard("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Ward(this.label, 5))));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.createWard(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Ward(this.label, 5))));
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(1, hospital.getWards().size());

        res = this.api.createWard(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(1, hospital.getWards().size());
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.createWard(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new Ward(this.label, 5))));
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the functionality exposed by the API for creating a {@link SpecialistType} for an hospital. 
     * The method first sets up a dummy instance of the {@link Hospital} class. Then, the following tests 
     * are executed:
     * <ul>
     * <li>creation of a specialist type instance with valid data: expected response code 201, the instance should
     * be added to the repository.</li> 
     * <li>creation of a specialist instance with the invalid data: expected response code 400, the instance should 
     * not be added to the repository.</li>
     * <li>creation of a specialist type for a hospital that does not exist: expected response code 404, the instance
     * should not be created.</li>
     * <li>creation of a specialist type instance when an existing ward is already present: expected response code 409.
     * the instance should not be added to the repository.</li>
     * <li>creation of a specialist type instance when a controller is not available: expected response code 503, no
     * instance should be added to the repository.</li>
     * </ul>
     * 
     * @throws Exception
     */ 
    @Test
    public void testCreateSpecialistType() throws Exception {
    	
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.createSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new SpecialistType(this.label, this.label))));
        Assert.assertEquals(201, res.getStatus());
        Assert.assertEquals(1, hospital.getSpecialistTypes().size());

        res = this.api.createSpecialistType(null, new ByteArrayInputStream(this.mapper.writeValueAsBytes(new SpecialistType(this.label, this.label))));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.createSpecialistType("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(new SpecialistType(this.label, this.label))));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.createSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new SpecialistType(this.label, this.label))));
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(1, hospital.getSpecialistTypes().size());

        res = this.api.createSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(1, hospital.getSpecialistTypes().size());
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.createSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new SpecialistType(this.label, this.label))));
        Assert.assertEquals(503, res.getStatus());
    }

    /**
     * This method tests the functionality exposed by the APIs to retrieve the list of hospitals.
     * The following tests are executed:
     * <ul>
     * <li>retrieve the hospitals list when no hospitals are set: expected response code 200, empty list.</li>
     * <li>retrieve the hospitals list when an instance of hospitals has been added: expected response code 200, list containing a hospital.</li>
     * <li>retrieve the hospitals list when there is no controller: expected response code 503.</li>
     * </ul> 
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testGetHospitals() {
    	
    	this.setupApi();
    	
    	Response res = this.api.getHospitals();
    	Assert.assertEquals(200, res.getStatus());
    	List<Hospital> hospitals = (List<Hospital>) res.getEntity();
    	Assert.assertEquals(0, hospitals.size());
    	
    	
    	Hospital hospital = new Hospital(this.region, this.label);
    	this.hospitalRepo.addItem(hospital);
    	res = this.api.getHospitals();
    	Assert.assertEquals(200, res.getStatus());
    	
    	hospitals = (List<Hospital>) res.getEntity();
    	Assert.assertEquals(1, hospitals.size());
    	
    	Hospital actual = hospitals.get(0);
    	Assert.assertEquals(actual.getId(), hospital.getId());
    	
    	
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.getHospitals();
        Assert.assertEquals(503, res.getStatus());
    }
    
    /**
     * This method tests the functionality exposed by the APIs for retrieving the information about the hospital.
     * The method creates a dummy {@link Hospital} instance and configures with the information that is expected to
     * be retrieved. Then, the following tests are executed:
     * <ul>
     * <li>retrieve the hospital information for a non existing id: expected response code 404.</li>
     * <li>retrieve the hospital information for an existing id: expected response code 200, the information should
     * be retrieved as part of the response body.</li>
     * <li>retrieve the hospital information when no controller is available: expected response code 503.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void testGetHospital() throws Exception {
        
    	this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        hospital.setDepartments(new ArrayList<Department>());
        hospital.getDepartments().add(new Department(this.label, 5));
        hospital.setSessionDuration(1337);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.getHospitalComplete("foobar");
        Assert.assertEquals(404, res.getStatus());

        res = this.api.getHospitalComplete(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Hospital result = (Hospital) res.getEntity();
        Assert.assertEquals(1337, result.getSessionDuration());
        Assert.assertEquals(1, result.getDepartments().size());
        Assert.assertEquals(hospital.getId(), result.getId());
        Assert.assertEquals(this.label, hospital.getName());
        Assert.assertEquals(this.region, hospital.getRegionId());

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.getHospitalComplete(hospital.getId());
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the functionality exposed by the APIs for retrieving the wards information about 
     * a given hospital. The method creates a dummy {@link Hospital} instance and configures with the information that 
     * is expected to be retrieved. Then, the following tests are executed:
     * <ul>
     * <li>retrieve the wards information for a non existing hospital id: expected response code 404.</li>
     * <li>retrieve the wards information for an existing hospital id: expected response code 200, the 
     * information should be retrieved as part of the response body.</li>
     * <li>retrieve the wards information when no controller is available: expected response code 503.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	@Test
    public void testGetWards() throws Exception {
    	
        this.setupApi();
        
        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = api.getHospitalWards("foobar");
        Assert.assertEquals(404, res.getStatus());

        res = api.getHospitalWards(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertNull(res.getEntity());

        hospital.setWards(new ArrayList<Ward>());
        hospital.getWards().add(new Ward("foo", 5));
        hospital.getWards().add(new Ward("bar", 2));
        this.hospitalRepo.updateItem(hospital);

        res = this.api.getHospitalWards(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, ((List) res.getEntity()).size());

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.getHospitalWards(hospital.getId());
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the functionality exposed by the APIs for retrieving the departments information about 
     * a given hospital. The method creates a dummy {@link Hospital} instance and configures with the information that 
     * is expected to be retrieved. Then, the following tests are executed:
     * <ul>
     * <li>retrieve the departments information for a non existing hospital id: expected response code 404.</li>
     * <li>retrieve the departments information for an existing hospital id: expected response code 200, the 
     * information should be retrieved as part of the response body.</li>
     * <li>retrieve the departments information when no controller is available: expected response code 503.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	@Test
    public void testGetDepartments() {
    	
        this.setupApi();
        
        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.getHospitalDepartments("foobar");
        Assert.assertEquals(404, res.getStatus());

        res = this.api.getHospitalDepartments(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertNull(res.getEntity());

        hospital.setDepartments(new ArrayList<Department>());
        hospital.getDepartments().add(new Department("foo", 5));
        hospital.getDepartments().add(new Department("bar", 2));
        this.hospitalRepo.updateItem(hospital);

        res = this.api.getHospitalDepartments(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, ((List) res.getEntity()).size());

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.getHospitalDepartments(hospital.getId());
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the functionality exposed by the APIs for retrieving the specialist type information about 
     * a given hospital. The method creates a dummy {@link Hospital} instance and configures with the information that 
     * is expected to be retrieved. Then, the following tests are executed:
     * <ul>
     * <li>retrieve the specialist type information for a non existing hospital id: expected response code 404.</li>
     * <li>retrieve the specialist type information for an existing hospital id: expected response code 200, the 
     * information should be retrieved as part of the response body.</li>
     * <li>retrieve the specialist type information when no controller is available: expected response code 503.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	@Test
    public void testGetSpecialistTypes() {
    	
        this.setupApi();
        
        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.getHospitalSpecialistTypes("foobar");
        Assert.assertEquals(404, res.getStatus());

        res = this.api.getHospitalSpecialistTypes(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertNull(res.getEntity());

        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        hospital.getSpecialistTypes().add(new SpecialistType("foo", "dep"));
        hospital.getSpecialistTypes().add(new SpecialistType("bar", "dep"));
        this.hospitalRepo.updateItem(hospital);

        res = this.api.getHospitalSpecialistTypes(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, ((List) res.getEntity()).size());

        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.getHospitalSpecialistTypes(hospital.getId());
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the functionality exposed by the APIs for retrieving the urgency category information about 
     * a given hospital. The method creates a dummy {@link Hospital} instance and configures with the information that 
     * is expected to be retrieved. Then, the following tests are executed:
     * <ul>
     * <li>retrieve the urgency category information for a non existing hospital id: expected response code 404.</li>
     * <li>retrieve the urgency category information for an existing hospital id: expected response code 200, the 
     * information should be retrieved as part of the response body.</li>
     * <li>retrieve the urgency category information when no controller is available: expected response code 503.</li>
     * </ul>
     * 
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	@Test
    public void testGetUrgencyCategories() {
    	
        this.setupApi();
        
        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Response res = this.api.getHospitalUrgencyCategories("foobar");
        Assert.assertEquals(404, res.getStatus());

        // empty list is not allowed
        res = this.api.getHospitalUrgencyCategories(hospital.getId());
        Assert.assertEquals(404, res.getStatus());

        hospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        hospital.getUrgencyCategories().add(new UrgencyCategory(null, this.label, 1, 2, 3));
        this.hospitalRepo.updateItem(hospital);

        res = this.api.getHospitalUrgencyCategories(hospital.getId());
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(1, ((List) res.getEntity()).size());

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.getHospitalUrgencyCategories(hospital.getId());
        Assert.assertEquals(503, res.getStatus());
    }


    /**
     * This method tests the function implemented by the APIs for updating the hospital's departments information. The 
     * method first configures a dummy {@link Hospital} and a few instances of {@link Department}, it then executes 
     * the following tests:
     * <ul>
     * <li>update an department in a non existing hospital: expected status code 404, no changes applied.</li>
     * <li>update an existing department with invalid data: expected status code 400, no changes applied.</li>
     * <li>update an existing department with valid data: expected status code 200, the new data should be set.</li>
     * <li>update an existing department with duplicate names: expected status code 409, no changes applied.</li>
     * <li>update an department instance when the controller is not available: expected status code 503, no changes applied.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testUpdateDepartments() throws IOException {
        
    	this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        List<Department> departments = new ArrayList<>();
        departments.add(new Department("foo", 5));
        departments.add(new Department("bar", 5));

        Response res = this.api.updateDepartments("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(departments)));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.updateDepartments(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());

        res = this.api.updateDepartments(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(departments)));

        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, hospital.getDepartments().size());

        departments.add(new Department("foo", 3));
        res = this.api.updateDepartments(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(departments)));
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(2, hospital.getDepartments().size());
        
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.updateDepartments(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(departments)));
        Assert.assertEquals(503, res.getStatus());

    }



    /**
     * This method tests the function implemented by the APIs for updating the hospital's wards information. The 
     * method first configures a dummy {@link Hospital} and a few instances of {@link Ward}, it then executes 
     * the following tests:
     * <ul>
     * <li>update an ward in a non existing hospital: expected status code 404, no changes applied.</li>
     * <li>update an existing ward with invalid data: expected status code 400, no changes applied.</li>
     * <li>update an existing ward with duplicate names: expected status code 409, no changes applied.</li>
     * <li>update an existing ward with valid data: expected status code 200, the new data should be set.</li>
     * <li>update an ward instance when the controller is not available: expected status code 503, no changes applied.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testUpdateWards() throws IOException {
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        List<Ward> wards = new ArrayList<>();
        wards.add(new Ward("foo", 5));
        wards.add(new Ward("bar", 5));

        Response res = this.api.updateWards("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(wards)));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.updateWards(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());

        res = this.api.updateWards(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(wards)));

        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, hospital.getWards().size());

        wards.add(new Ward("foo", 3));
        res = this.api.updateWards(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(wards)));
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(2, hospital.getWards().size());
        
        

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.updateWards(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(wards)));
        Assert.assertEquals(503, res.getStatus());

    }



    /**
     * This method tests the function implemented by the APIs for updating the hospital's specialist type set. The 
     * method first configures a dummy {@link Hospital} and a few instances of {@link SpecialistType}, it then executes 
     * the following tests:
     * <ul>
     * <li>update an specialist type in a non existing hospital: expected status code 404, no changes applied.</li>
     * <li>update an existing specialist type with invalid data: expected status code 400, no changes applied.</li>
     * <li>update an existing specialist type with duplicate names: expected status code 409, no changes applied.</li>
     * <li>update an existing specialist type with valid data: expected status code 200, the new data should be set.</li>
     * <li>update an specialist type instance when the controller is not available: expected status code 503, no changes applied.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testUpdateSpecialistTypes() throws IOException {
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        List<SpecialistType> types = new ArrayList<>();
        types.add(new SpecialistType("foo", "dep"));
        types.add(new SpecialistType("bar", "dep"));

        Response res = this.api.updateSpecialistTypes("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(types)));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.updateSpecialistTypes(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());

        res = this.api.updateSpecialistTypes(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(types)));

        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, hospital.getSpecialistTypes().size());

        types.add(new SpecialistType("foo", "dep"));
        res = this.api.updateSpecialistTypes(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(types)));
        Assert.assertEquals(409, res.getStatus());
        Assert.assertEquals(2, hospital.getSpecialistTypes().size());
        
       
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.updateSpecialistTypes(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(types)));
        Assert.assertEquals(503, res.getStatus());

    }


    /**
     * This method tests the function implemented by the APIs for updating the hospital's urgency categories set. The 
     * method first configures a dummy {@link Hospital} and a few instances of {@link UrgencyCategory}, it then executes 
     * the following tests:
     * <ul>
     * <li>update an urgency category in a non existing hospital: expected status code 404, no changes applied.</li>
     * <li>update an existing urgency category with invalid data: expected status code 400, no changes applied.</li>
     * <li>update an existing urgency category with valid data: expected status code 200, the new data should be set.</li>
     * <li>update an urgency category instance when the controller is not available: expected status code 503, no changes applied.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testUpdateUrgencyCategories() throws IOException {
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        List<UrgencyCategory> cats = new ArrayList<>();
        cats.add(new UrgencyCategory(null, "foo", 1, 2, 3));
        cats.add(new UrgencyCategory(null, "bar", 1, 2, 3));

        Response res = this.api.updateUrgencyCategories("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(cats)));
        Assert.assertEquals(404, res.getStatus());

        res = this.api.updateUrgencyCategories(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());

        res = this.api.updateUrgencyCategories(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(cats)));

        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(2, hospital.getUrgencyCategories().size());

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.updateUrgencyCategories(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(cats)));
        Assert.assertEquals(503, res.getStatus());
    }

    /**
     * This method tests the function implemented by the APIs for updating the hospital's metadata information. The method
     * first configures a dummy {@link Hospital} instance with metadata information, it then executes the following tests:
     * <ul>
     * <li>update a non existing hospital: expected status code 404, no changes applied.</li>
     * <li>update an existing hospital with invalid data: expected status code 400, no changes applied.</li>
     * <li>update an existing hospital with valid data: expected status code 200, the new metadata should be set.</li>
     * <li>update an hospital instance when the controller is not available: expected status code 503, no changes applied.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testUpdateHospitalMetaData() throws IOException {
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        this.hospitalRepo.addItem(hospital);

        Hospital newData = new Hospital("foo", "bar");
        newData.setSessionDuration(5);
        newData.setTheatreCount(9);
        newData.setTheatreSessionsPerDay(12);
        newData.setIcuBedCount(18);

        Response res = this.api.updateHospitalMetaData("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(newData)));
        Assert.assertEquals(404, res.getStatus());
        Assert.assertEquals(this.label, hospital.getName());

        res = this.api.updateHospitalMetaData(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(new UrgencyCategory(new ArrayList<KpiTarget>(), this.label + "bar", 5, 2, 1))));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(this.label, hospital.getName());

        res = this.api.updateHospitalMetaData(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(newData)));
        Assert.assertEquals(200, res.getStatus());

        hospital = this.hospitalRepo.getItem(hospital.getId());
        Assert.assertEquals("foo", hospital.getRegionId());
        Assert.assertEquals("bar", hospital.getName());
        Assert.assertEquals(5, hospital.getSessionDuration());
        Assert.assertEquals(9, hospital.getTheatreCount());
        Assert.assertEquals(12, hospital.getTheatreSessionsPerDay());
        Assert.assertEquals(18, hospital.getIcuBedCount());
        

        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.updateHospitalMetaData(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(newData)));
        Assert.assertEquals(503, res.getStatus());
    }
    
    /**
     * This method tests the function implemented by the APIs for deleting a department. The method first
     * configures a dummy {@link Hospital} instance with test instances of {@link Department} it then executes
     * the following tests:
     * <ul>
     * <li>deletion of a non existing department: expected response code 404, no department should be deleted.</li>
     * <li>deletion of an invalid department: expected response code 400, no department should be deleted.</li>
     * <li>deletion of an existing department: expected response code 200, the department should be deleted.</li>
     * <li>deletion of a ward, when the controller is missing: expected response code 503, no department should be deleted.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testDeleteDepartment() throws IOException {
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        hospital.setDepartments(new ArrayList<Department>());
        hospital.getDepartments().add(new Department(this.label, 5));
        this.hospitalRepo.addItem(hospital);

        Department delete = new Department(this.label, 5);

        Response res = this.api.deleteDepartment("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(404, res.getStatus());
        Assert.assertEquals(1, hospital.getDepartments().size());


        res = this.api.deleteDepartment(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(1, hospital.getDepartments().size());

        res = this.api.deleteDepartment(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(0, hospital.getDepartments().size());
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.deleteSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(503, res.getStatus());
    }
    
    /**
     * This method tests the function implemented by the APIs for deleting a ward. The method first
     * configures a dummy {@link Hospital} instance with test instances of {@link Ward} it then executes
     * the following tests:
     * <ul>
     * <li>deletion of a non existing ward: expected response code 404, no ward should be deleted.</li>
     * <li>deletion of an invalid ward: expected response code 400, no ward should be deleted.</li>
     * <li>deletion of an existing ward: expected response code 200, the ward should be deleted.</li>
     * <li>deletion of a ward, when the controller is missing: expected response code 503, no ward should be deleted.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testDeleteWard() throws IOException {
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        hospital.setWards(new ArrayList<Ward>());
        hospital.getWards().add(new Ward(this.label, 5));
        this.hospitalRepo.addItem(hospital);

        Ward delete = new Ward(label, 5);

        Response res = this.api.deleteWard("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(404, res.getStatus());
        Assert.assertEquals(1, hospital.getWards().size());


        res = this.api.deleteWard(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(1, hospital.getWards().size());

        res = this.api.deleteWard(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(0, hospital.getWards().size());
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.deleteSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(503, res.getStatus());
    }

    /**
     * This method tests the function implemented by the APIs for deleting a specialist type. The method first
     * configures a dummy {@link Hospital} instance with test instances of {@link SpecialistType} it then executes
     * the following tests:
     * <ul>
     * <li>deletion of a non existing specialist type: expected response code 404, no specialist types should be deleted.</li>
     * <li>deletion of an invalid specialist type: expected response code 400, no specialist type should be deleted.</li>
     * <li>deletion of an existing specialist type: expected response code 200, the specialist type should be deleted.</li>
     * <li>deletion of a specialist type, when the controller is missing: expected response code 503, no specialist should be deleted.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testDeleteSpecialistType() throws IOException {
    	
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        hospital.getSpecialistTypes().add(new SpecialistType(this.label, this.label));
        this.hospitalRepo.addItem(hospital);

        SpecialistType delete = new SpecialistType(this.label, this.label);

        Response res = this.api.deleteSpecialistType("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(404, res.getStatus());
        Assert.assertEquals(1, hospital.getSpecialistTypes().size());


        res = this.api.deleteSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(1, hospital.getSpecialistTypes().size());

        res = this.api.deleteSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(0, hospital.getSpecialistTypes().size());
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.deleteSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(503, res.getStatus());
        
    }
    
    /**
     * This method tests the function implemented by the APIs for deleting an urgency category. The method first
     * adds a dummy hospital with some default instances of {@link UrgencyCategory} it then performs the following
     * tests:
     * <ul>
     * <li>deletion of a non existing urgency category: the expected return value is 404, no categories should be deleted.</li>
     * <li>deletion of an invalid urgency category: the expected return value is 500, no categories should be deleted.</li>
     * <li>deletion of an existing urgency category: the expected return value is 200, the category should be deleted.</li>
     * </ul>
     * 
     * @throws IOException
     */
    @Test
    public void testDeleteUrgencyCategory() throws IOException {
    	
        this.setupApi();

        Hospital hospital = new Hospital(this.region, this.label);
        hospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        hospital.getUrgencyCategories().add(new UrgencyCategory(null, this.label + "bar", 1, 2, 3));
        hospital.getUrgencyCategories().add(new UrgencyCategory(null, this.label, 4, 5, 6));
        this.hospitalRepo.addItem(hospital);

        UrgencyCategory delete = new UrgencyCategory(null, this.label, 4, 5, 6);

        
        
        Response res = this.api.deleteUrgencyCategory("foobar", new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(404, res.getStatus());
        Assert.assertEquals(2, hospital.getUrgencyCategories().size());


        res = this.api.deleteUrgencyCategory(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(hospital)));
        Assert.assertEquals(400, res.getStatus());
        Assert.assertEquals(2, hospital.getUrgencyCategories().size());

        res = this.api.deleteUrgencyCategory(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals(1, hospital.getUrgencyCategories().size());
        
        this.env.removeAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        res = this.api.deleteSpecialistType(hospital.getId(), new ByteArrayInputStream(this.mapper.writeValueAsBytes(delete)));
        Assert.assertEquals(503, res.getStatus());
        
        
    }





    /**
     * Initialises the {@link Environment} with the components that are needed by
     * the {@link HospitalApi} instances to function and configures a new instance
     * of the API with the created {@link Environment}.
     */
    private void setupApi() {
    	
        this.api = new HospitalApi();

        this.hospitalRepo = new TransientHospitalRepository();
        this.regionRepo = new TransientRegionRepository();

        BasicHospitalController controller = new BasicHospitalController();
        controller.setRegionRepository(this.regionRepo);
        controller.setHospitalRepository(this.hospitalRepo);

        this.env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE, this.hospitalRepo);
        env.setAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE, this.regionRepo);
        env.setAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE, controller);

        this.api.environment = this.env;
    }






}
