package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.RegionRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientHospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.impl.TransientRegionRepository;
import com.ibm.au.jaws.web.core.runtime.Environment;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Ilfrich
 */
public class BasicHospitalControllerTest {


	/**
	 * 
	 */
    private final String departmentName = "Cardiac Surgery";
    /**
     * 
     */
    private final String label = "Custom Label";
    /**
     * 
     */
    private final String regionName = "Australia/Victoria";

    /**
     * 
     */
    private final static double DELTA = 0.0000000000000000001;

    /**
     * 
     */
    private BasicHospitalController controller;

    /**
     * 
     */
    @Test
    public void testSpecialistTypeCheck() {
    	
    	this.setupController();

        SpecialistType t = new SpecialistType(this.label, this.departmentName);
        Hospital h = new Hospital();

        List<SpecialistType> types = new ArrayList<>();
        h.setSpecialistTypes(types);

        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_CREATE, h, t));
        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_UPDATE, h, t));

        types.add(new SpecialistType(this.label, this.departmentName));

        Assert.assertFalse(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_CREATE, h, t));
        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_UPDATE, h, t));

        types.add(new SpecialistType(this.label, this.departmentName));

        Assert.assertFalse(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_CREATE, h, t));
        Assert.assertFalse(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_UPDATE, h, t));

        types = new ArrayList<>();
        h.setSpecialistTypes(types);
        types.add(new SpecialistType(this.label + "foo", this.departmentName));

        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_CREATE, h, t));
        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_UPDATE, h, t));

        types.add(new SpecialistType(this.label, this.departmentName + "bar"));

        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_CREATE, h, t));
        Assert.assertTrue(this.controller.checkSpecialistTypeName(BasicHospitalController.OPERATION_UPDATE, h, t));
    }

    /**
     * 
     */
    @Test
    public void testWardCheck() {
    	
    	this.setupController();
       
    	Ward w = new Ward(this.label, 1337);
        Hospital h = new Hospital();

        List<Ward> wards = new ArrayList<>();
        h.setWards(wards);

        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_CREATE, h, w));
        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_UPDATE, h, w));

        wards.add(new Ward(this.label, 0));

        Assert.assertFalse(this.controller.checkWardName(BasicHospitalController.OPERATION_CREATE, h, w));
        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_UPDATE, h, w));

        wards.add(new Ward(this.label, 5));

        Assert.assertFalse(this.controller.checkWardName(BasicHospitalController.OPERATION_CREATE, h, w));
        Assert.assertFalse(this.controller.checkWardName(BasicHospitalController.OPERATION_UPDATE, h, w));

        wards = new ArrayList<>();
        h.setWards(wards);
        wards.add(new Ward(this.label + "foo", 0));

        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_CREATE, h, w));
        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_UPDATE, h, w));

        wards.add(new Ward(this.label + "bar", 5));

        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_CREATE, h, w));
        Assert.assertTrue(this.controller.checkWardName(BasicHospitalController.OPERATION_UPDATE, h, w));
    }

    /**
     * 
     */
    @Test
    public void testDepartmentCheck() {
    	
    	this.setupController();
        
    	Department dep = new Department(this.label, 1337);
        Hospital h = new Hospital();

        List<Department> deps = new ArrayList<>();
        h.setDepartments(deps);

        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_CREATE, h, dep));
        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_UPDATE, h, dep));

        deps.add(new Department(label, 0));

        Assert.assertFalse(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_CREATE, h, dep));
        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_UPDATE, h, dep));

        deps.add(new Department(label, 5));

        Assert.assertFalse(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_CREATE, h, dep));
        Assert.assertFalse(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_UPDATE, h, dep));

        deps = new ArrayList<>();
        h.setDepartments(deps);
        deps.add(new Department(label + "foo", 0));

        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_CREATE, h, dep));
        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_UPDATE, h, dep));

        deps.add(new Department(label + "bar", 5));

        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_CREATE, h, dep));
        Assert.assertTrue(this.controller.checkDepartmentName(BasicHospitalController.OPERATION_UPDATE, h, dep));
    }

    /**
     * 
     */
    @Test
    public void testGetHospital() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.getHospital(null));
        Assert.assertNull(this.controller.getHospital("not-existing"));

        Hospital h = new Hospital();
        this.controller.getHospitalRepository().addItem(h);

        Assert.assertNull(this.controller.getHospital(null));
        Assert.assertNull(this.controller.getHospital("not-existing"));
        Assert.assertNotNull(this.controller.getHospital(h.getId()));
    }

    /**
     * 
     */
    @Test
    public void testHospitalRepo() {
    	
    	this.setupController();
    	
    	this.controller.getHospitalRepository().addItem(new Hospital());
    	this.controller.getHospitalRepository().addItem(new Hospital());

        Assert.assertNotNull(this.controller.getHospitalRepository());
        Assert.assertEquals(2, this.controller.getHospitalRepository().getAll().size());
    }

    /**
     * 
     */
    @Test
    public void testGetRegion() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.getRegion(null));
        Assert.assertNull(this.controller.getRegion("not-existing"));

        Region r = new Region();
        r.setName(this.label);
        this.controller.getRegionRepository().addItem(r);

        Assert.assertNull(this.controller.getRegion(null));
        Assert.assertNull(this.controller.getRegion("not-existing"));
        Assert.assertNotNull(this.controller.getRegion(r.getId()));
        Assert.assertEquals(this.label, this.controller.getRegion(r.getId()).getName());
    }

    /**
     * 
     */
    @Test
    public void testKpiConsolidation() {
    	
    	this.setupController();
        // prepare KPI targets
        List<KpiTarget> kpi1 = new ArrayList<>();
        kpi1.add(new KpiTarget(1, 1, 50.0));
        kpi1.add(new KpiTarget(1, 2, 70.0));
        kpi1.add(new KpiTarget(1, 3, 90.0));

        List<KpiTarget> kpi2 = new ArrayList<>();
        kpi2.add(new KpiTarget(1, 1, 44.0));
        kpi2.add(new KpiTarget(1, 2, 70.0));
        kpi2.add(new KpiTarget(1, 3, 84.0));

        // prepare categories
        UrgencyCategory cat1 = new UrgencyCategory(kpi1, this.label, 1, 1, 1);
        UrgencyCategory cat2 = new UrgencyCategory(kpi2, this.label, 1, 1, 1);

        // test
        List<KpiTarget> targets = this.controller.getKpiTargets(cat2, cat1);

        Assert.assertEquals(50.0, targets.get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, targets.get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(90.0, targets.get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
    }

    /**
     * 
     */
    @Test
    public void testKpiConsolidationHospitalOnly() {
    	
    	this.setupController();
        // only hospital specifies KPI targets
        List<KpiTarget> kpi3 = new ArrayList<>();
        kpi3.add(new KpiTarget(1, 1, 44.0));
        kpi3.add(new KpiTarget(1, 2, 70.0));
        kpi3.add(new KpiTarget(1, 3, 84.0));

        // prepare categories
        UrgencyCategory cat3 = new UrgencyCategory(new ArrayList<KpiTarget>(), this.label, 1, 1, 1);
        UrgencyCategory cat4 = new UrgencyCategory(kpi3, this.label, 1, 1, 1);

        // test
        List<KpiTarget> targets = this.controller.getKpiTargets(cat3, cat4);
        Assert.assertEquals(44.0, targets.get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, targets.get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(84.0, targets.get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
    }

    /**
     * 
     */
    @Test
    public void testKpiConsolidationRegionOnly() {
    	
    	this.setupController();
        
    	List<KpiTarget> kpi3 = new ArrayList<>();
        kpi3.add(new KpiTarget(1, 1, 50.0));
        kpi3.add(new KpiTarget(1, 2, 70.0));
        kpi3.add(new KpiTarget(1, 3, 90.0));

        UrgencyCategory cat3 = new UrgencyCategory(null, this.label, 1, 1, 1);
        UrgencyCategory cat4 = new UrgencyCategory(kpi3, this.label, 1, 1, 1);

        List<KpiTarget> targets = this.controller.getKpiTargets(cat4, cat3);
        Assert.assertEquals(50.0, targets.get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, targets.get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(90.0, targets.get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
    }

    /**
     * 
     */
    @Test
    public void testConsolidateUrgencyCategories() {
        
    	this.setupController();

        Assert.assertEquals(0, this.controller.compileUrgencyCategories(null).size());

        Region region = new Region(this.label);
        Hospital hospital = new Hospital();
        hospital.setRegionId(this.label);

        this.controller.getRegionRepository().addItem(region);
        this.controller.getHospitalRepository().addItem(hospital);

        // prepare KPI targets
        List<KpiTarget> kpi1 = new ArrayList<>();
        kpi1.add(new KpiTarget(1, 1, 50.0));
        kpi1.add(new KpiTarget(1, 2, 70.0));
        kpi1.add(new KpiTarget(1, 3, 90.0));

        List<KpiTarget> kpi2 = new ArrayList<>();
        kpi2.add(new KpiTarget(1, 1, 44.0));
        kpi2.add(new KpiTarget(1, 2, 70.0));
        kpi2.add(new KpiTarget(1, 3, 84.0));

        // prepare categories
        UrgencyCategory cat1 = new UrgencyCategory(kpi2, this.label, 14, 0, 2);
        UrgencyCategory cat2 = new UrgencyCategory(kpi1, this.label, 30, 2, 3);

        hospital.setUrgencyCategories(Arrays.asList(cat2));
        region.setUrgencyCategories(Arrays.asList(cat1));

        List<UrgencyCategory> cats = this.controller.compileUrgencyCategories(hospital);

        UrgencyCategory cat = cats.get(0);
        Assert.assertEquals(30, cat.getMaxWaitListStay());
        Assert.assertEquals(2, cat.getMinPointsRequired());
        Assert.assertEquals(3, cat.getPossiblePoints());

        Assert.assertEquals(50.0, cat.getKpiTargets().get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, cat.getKpiTargets().get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(90.0, cat.getKpiTargets().get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);

        hospital.setRegionId("foobar");
        cats = this.controller.compileUrgencyCategories(hospital);

        cat = cats.get(0);

        Assert.assertEquals(30, cat.getMaxWaitListStay());
        Assert.assertEquals(2, cat.getMinPointsRequired());
        Assert.assertEquals(3, cat.getPossiblePoints());

        Assert.assertEquals(50.0, cat.getKpiTargets().get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, cat.getKpiTargets().get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(90.0, cat.getKpiTargets().get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);

        // reset items
        kpi2 = new ArrayList<>();
        kpi2.add(new KpiTarget(1, 1, 44.0));
        kpi2.add(new KpiTarget(1, 2, 70.0));
        kpi2.add(new KpiTarget(1, 3, 84.0));
        cat1 = new UrgencyCategory(kpi2, this.label, 14, 0, 2);
        // setup hospital
        hospital.setRegionId(region.getName());
        hospital.setUrgencyCategories(Arrays.asList(cat1));
        cats = this.controller.compileUrgencyCategories(hospital);

        cat = cats.get(0);

        Assert.assertEquals(14, cat.getMaxWaitListStay());
        Assert.assertEquals(0, cat.getMinPointsRequired());
        Assert.assertEquals(2, cat.getPossiblePoints());

        Assert.assertEquals(44.0, cat.getKpiTargets().get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, cat.getKpiTargets().get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(84.0, cat.getKpiTargets().get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
    }

    /**
     * 
     */
    @Test
    public void testConsolidateMatchingUrgencyCategories() {
    	
    	this.setupController();

        Assert.assertEquals(0, this.controller.compileUrgencyCategories(null).size());

        Region region = new Region(this.label);
        Hospital hospital = new Hospital();
        hospital.setRegionId(this.label);

        this.controller.getRegionRepository().addItem(region);
        this.controller.getHospitalRepository().addItem(hospital);

        // prepare KPI targets
        List<KpiTarget> kpi1 = new ArrayList<>();
        kpi1.add(new KpiTarget(1, 1, 50.0));
        kpi1.add(new KpiTarget(1, 2, 70.0));
        kpi1.add(new KpiTarget(1, 3, 90.0));

        List<KpiTarget> kpi2 = new ArrayList<>();
        kpi2.add(new KpiTarget(1, 1, 44.0));
        kpi2.add(new KpiTarget(1, 2, 70.0));
        kpi2.add(new KpiTarget(1, 3, 84.0));

        // prepare categories
        UrgencyCategory cat1 = new UrgencyCategory(kpi1, this.label, 30, 2, 3);
        UrgencyCategory cat2 = new UrgencyCategory(kpi2, this.label, 30, 2, 3);

        hospital.setUrgencyCategories(Arrays.asList(cat1));
        region.setUrgencyCategories(Arrays.asList(cat2));

        List<UrgencyCategory> cats = this.controller.compileUrgencyCategories(hospital);

        UrgencyCategory cat = cats.get(0);

        Assert.assertEquals(30, cat.getMaxWaitListStay());
        Assert.assertEquals(2, cat.getMinPointsRequired());
        Assert.assertEquals(3, cat.getPossiblePoints());

        Assert.assertEquals(50.0, cat.getKpiTargets().get(0).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(70.0, cat.getKpiTargets().get(1).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
        Assert.assertEquals(90.0, cat.getKpiTargets().get(2).getRequiredOnTimePerformance(), BasicHospitalControllerTest.DELTA);
    }

    /**
     * 
     */
    @Test
    public void testConsolidateUrgencyCategoriesSpecialCases() {
    	
    	this.setupController();
        
    	Region region = new Region(this.label);
        Hospital hospital = new Hospital();
        hospital.setRegionId(this.label);

        this.controller.getRegionRepository().addItem(region);
        this.controller.getHospitalRepository().addItem(hospital);

        UrgencyCategory cat1 = new UrgencyCategory(new ArrayList<KpiTarget>(),this.label, 30, 2, 3);
        UrgencyCategory cat2 = new UrgencyCategory(null, this.label, 14, 1, 2);
        UrgencyCategory cat3 = new UrgencyCategory(null, this.label + "bar", 14, 1, 2);

        // set a region urgency category
        region.setUrgencyCategories(Arrays.asList(cat1));

        List<UrgencyCategory> cats = this.controller.compileUrgencyCategories(hospital);

        Assert.assertEquals(30, cats.get(0).getMaxWaitListStay());
        Assert.assertEquals(2, cats.get(0).getMinPointsRequired());
        Assert.assertEquals(3, cats.get(0).getPossiblePoints());
        Assert.assertEquals(0, cats.get(0).getKpiTargets().size());

        // set a hospital urgency category
        hospital.setUrgencyCategories(Arrays.asList(cat2, cat3));

        cats = this.controller.compileUrgencyCategories(hospital);
        Assert.assertEquals(2, cats.size());

        for (UrgencyCategory cat : cats) {
            Assert.assertEquals(14, cat.getMaxWaitListStay());
            Assert.assertEquals(1, cat.getMinPointsRequired());
            Assert.assertEquals(2, cat.getPossiblePoints());
            if (this.label.equals(cat.getLabel())) {
                Assert.assertEquals(0, cat.getKpiTargets().size());
            }
        }
    }

    /**
     * 
     */
    @Test
    public void testRemoveDepartment() {
        
    	this.setupController();
        
        List<Department> departments = new ArrayList<>();
        departments.add(new Department("A", 5));
        departments.add(new Department("B", 5));

        Hospital hospital = new Hospital();
        this.controller.getHospitalRepository().addItem(hospital);

        Assert.assertNull(this.controller.removeDepartment(hospital, null).getDepartments());
        Assert.assertNull(this.controller.removeDepartment(hospital, new Department()).getDepartments());

        hospital.setDepartments(new ArrayList<Department>());
        this.controller.getHospitalRepository().updateItem(hospital);

        Assert.assertEquals(0, this.controller.removeDepartment(hospital, null).getDepartments().size());
        Assert.assertEquals(0, this.controller.removeDepartment(hospital, new Department()).getDepartments().size());

        hospital.setDepartments(departments);
        this.controller.getHospitalRepository().updateItem(hospital);

        Assert.assertEquals(2, this.controller.removeDepartment(hospital, null).getDepartments().size());
        Assert.assertEquals(2, this.controller.removeDepartment(hospital, new Department("C", 5)).getDepartments().size());
        Assert.assertEquals(1, this.controller.removeDepartment(hospital, new Department("A", 3)).getDepartments().size());
        Assert.assertEquals(0, this.controller.removeDepartment(hospital, new Department("B", 5)).getDepartments().size());

        hospital = new Hospital();
        hospital.setDepartments(new ArrayList<Department>());
        Assert.assertEquals(0, this.controller.removeDepartment(hospital, new Department("A", 5)).getDepartments().size());
        Assert.assertNull(this.controller.removeDepartment(null, new Department("A", 5)));
    }

    /**
     * 
     */
    @Test
    public void testRemoveWard() {
    	
    	this.setupController();
        
    	List<Ward> wards = new ArrayList<>();
        wards.add(new Ward("A", 5));
        wards.add(new Ward("B", 5));

        Hospital hospital = new Hospital();
        this.controller.getHospitalRepository().addItem(hospital);

        Assert.assertNull(this.controller.removeWard(hospital, null).getWards());
        Assert.assertNull(this.controller.removeWard(hospital, new Ward()).getWards());

        hospital.setWards(new ArrayList<Ward>());
        this.controller.getHospitalRepository().updateItem(hospital);

        Assert.assertEquals(0, this.controller.removeWard(hospital, null).getWards().size());
        Assert.assertEquals(0, this.controller.removeWard(hospital, new Ward()).getWards().size());

        hospital.setWards(wards);
        this.controller.getHospitalRepository().updateItem(hospital);

        Assert.assertEquals(2, this.controller.removeWard(hospital, null).getWards().size());
        Assert.assertEquals(2, this.controller.removeWard(hospital, new Ward("C", 5)).getWards().size());
        Assert.assertEquals(1, this.controller.removeWard(hospital, new Ward("A", 3)).getWards().size());
        Assert.assertEquals(0, this.controller.removeWard(hospital, new Ward("B", 5)).getWards().size());

        hospital.setWards(new ArrayList<Ward>());
        Assert.assertEquals(0, this.controller.removeWard(hospital, new Ward("A", 5)).getWards().size());
        Assert.assertNull(this.controller.removeWard(null, new Ward("A", 5)));
    }

    /**
     * 
     */
    @Test
    public void testRemoveSpecialist() {
    	
    	this.setupController();
        
    	List<SpecialistType> types = new ArrayList<>();
        types.add(new SpecialistType("A", this.label));
        types.add(new SpecialistType("B", this.label));

        Hospital hospital = new Hospital();
        this.controller.getHospitalRepository().addItem(hospital);

        Assert.assertNull(this.controller.removeSpecialistType(hospital, null).getSpecialistTypes());
        Assert.assertNull(this.controller.removeSpecialistType(hospital, new SpecialistType()).getSpecialistTypes());

        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        this.controller.getHospitalRepository().updateItem(hospital);

        Assert.assertEquals(0, this.controller.removeSpecialistType(hospital, null).getSpecialistTypes().size());
        Assert.assertEquals(0, this.controller.removeSpecialistType(hospital, new SpecialistType()).getSpecialistTypes().size());

        hospital.setSpecialistTypes(types);
        this.controller.getHospitalRepository().updateItem(hospital);

        Assert.assertEquals(2, this.controller.removeSpecialistType(hospital, null).getSpecialistTypes().size());
        Assert.assertEquals(2, this.controller.removeSpecialistType(hospital, new SpecialistType("C", this.label)).getSpecialistTypes().size());
        Assert.assertEquals(2, this.controller.removeSpecialistType(hospital, new SpecialistType("A", this.label + "bar")).getSpecialistTypes().size());
        Assert.assertEquals(1, this.controller.removeSpecialistType(hospital, new SpecialistType("A", this.label)).getSpecialistTypes().size());
        Assert.assertEquals(0, this.controller.removeSpecialistType(hospital, new SpecialistType("B", this.label)).getSpecialistTypes().size());

        hospital.setSpecialistTypes(new ArrayList<SpecialistType>());
        Assert.assertEquals(0, this.controller.removeSpecialistType(hospital, new SpecialistType("A", this.label)).getSpecialistTypes().size());
        Assert.assertNull(this.controller.removeSpecialistType(null, new SpecialistType("A", this.label)));
    }

    /**
     * 
     */
    @Test
    public void testRemoveUrgencyCategory() {
    	
    	this.setupController();
        
    	List<UrgencyCategory> cats = new ArrayList<>();
        cats.add(new UrgencyCategory(null, "A", 1, 1, 1));
        cats.add(new UrgencyCategory(null, "B", 1, 1, 1));

        Assert.assertEquals(2, this.controller.removeUrgencyCategory(cats, null).size());
        Assert.assertEquals(2, this.controller.removeUrgencyCategory(cats, new UrgencyCategory(null, "C", 1, 1, 1)).size());
        Assert.assertEquals(1, this.controller.removeUrgencyCategory(cats, new UrgencyCategory(null, "A", 3, 3, 3)).size());
        Assert.assertEquals(1, this.controller.removeUrgencyCategory(cats, new UrgencyCategory(null, "B", 5, 5, 5)).size());

        Assert.assertEquals(0, this.controller.removeUrgencyCategory(new ArrayList<UrgencyCategory>(), new UrgencyCategory(null, "A", 5, 5, 5)).size());
        List<UrgencyCategory> list = null;
        Assert.assertNull(this.controller.removeUrgencyCategory(list, new UrgencyCategory(null, "A", 5, 5, 5)));
    }


    /**
     * 
     */
    @Test
    public void testCreateHospital() {
    	
    	this.setupController();
        
    	Hospital h = new Hospital();
        h.setName(this.label);
        h.setTheatreCount(5);

        Assert.assertNull(this.controller.createHospital(null));
        Assert.assertEquals(0, this.controller.getHospitalRepository().getAll().size());

        Hospital dbH = this.controller.createHospital(h);
        Assert.assertNotNull(dbH.getId());
        Assert.assertEquals(this.label, dbH.getName());
        Assert.assertEquals(5, dbH.getTheatreCount());

        Assert.assertEquals(1, this.controller.getHospitalRepository().getAll().size());

        Assert.assertNull(this.controller.createHospital(dbH));
        Assert.assertEquals(1, this.controller.getHospitalRepository().getAll().size());
    }

    /**
     * 
     */
    @Test
    public void testUpdateHospital() {
    	
    	this.setupController();
        
    	Hospital dbHospital = new Hospital(this.regionName, this.label);
        this.controller.getHospitalRepository().addItem(dbHospital);

        Assert.assertNull(this.controller.updateHospital(null));

        Hospital hospital = new Hospital(this.regionName, this.label);
        Assert.assertNull(this.controller.updateHospital(hospital));

        hospital.setId(dbHospital.getId());
        hospital.setName(this.label + "foobar");

        Hospital result = controller.updateHospital(hospital);
        Assert.assertEquals(this.label + "foobar", controller.getHospitalRepository().getItem(hospital.getId()).getName());
        Assert.assertEquals(this.label + "foobar", result.getName());
    }

    /**
     * 
     */
    @Test
    public void testUpdateHospitalMetaData() {
    	
        setupController();
        Hospital dbHospital = new Hospital(this.regionName, this.label);
        this.controller.getHospitalRepository().addItem(dbHospital);

        Assert.assertNull(this.controller.updateHospitalMetaData(null, new Hospital()));
        Assert.assertNull(this.controller.updateHospitalMetaData(dbHospital, null));

        Hospital newData = new Hospital(this.regionName + "foo", this.label + "foo");
        newData.setIcuBedCount(1);
        newData.setSessionDuration(2);
        newData.setTheatreSessionsPerDay(3);
        newData.setTheatreCount(4);


        Hospital result = this.controller.updateHospitalMetaData(dbHospital, newData);
        Assert.assertEquals(result.getId(), dbHospital.getId());
        Assert.assertEquals(1, result.getIcuBedCount());
        Assert.assertEquals(2, result.getSessionDuration());
        Assert.assertEquals(3, result.getTheatreSessionsPerDay());
        Assert.assertEquals(4, result.getTheatreCount());
        Assert.assertEquals(this.label + "foo", result.getName());
        Assert.assertEquals(this.regionName + "foo", result.getRegionId());
    }
    
    /**
     * 
     */
    @Test
    public void testDeleteHospital() {
    	
    	this.setupController();
        
    	Hospital dbHospital = new Hospital();
        this.controller.getHospitalRepository().addItem(dbHospital);

        Hospital noHospital = null;
        String noString = null;
        Assert.assertFalse(this.controller.deleteHospital(noHospital));
        Assert.assertFalse(this.controller.deleteHospital(new Hospital()));
        Assert.assertFalse(this.controller.deleteHospital(noString));

        Assert.assertTrue(this.controller.deleteHospital("not-existing"));
        Assert.assertEquals(1, this.controller.getHospitalRepository().getAll().size());

        Hospital delete = new Hospital();
        delete.setId(dbHospital.getId());
        Assert.assertTrue(this.controller.deleteHospital(delete));

        Assert.assertEquals(0, this.controller.getHospitalRepository().getAll().size());

        dbHospital.setId(null);
        this.controller.getHospitalRepository().addItem(dbHospital);
        Assert.assertTrue(this.controller.deleteHospital(dbHospital.getId()));

        Assert.assertEquals(0, this.controller.getHospitalRepository().getAll().size());
    }

    /**
     * 
     */
    @Test
    public void testGetHospitals() {
    	
    	this.setupController();

        Assert.assertEquals(0, this.controller.getHospitals().size());

        Hospital dbHospital1 = new Hospital(this.regionName, this.label);
        Hospital dbHospital2 = new Hospital(this.regionName, this.label + "foo");

        this.controller.getHospitalRepository().addItem(dbHospital1);
        this.controller.getHospitalRepository().addItem(dbHospital2);

        Assert.assertEquals(2, this.controller.getHospitals().size());
        String firstId = this.controller.getHospitals().get(0).getId();
        Assert.assertTrue(firstId.equals(dbHospital1.getId()) || firstId.equals(dbHospital2.getId()));
    }

    /**
     * 
     */
    @Test
    public void testGetHospitalsByRegion() {
    	
    	this.setupController();

        // make sure the repo is empty
        Assert.assertEquals(0, this.controller.getHospitals().size());

        // init data
        Hospital dbHospital1 = new Hospital(this.regionName, this.label);
        Hospital dbHospital2 = new Hospital(this.regionName, this.label + "foo");
        Hospital dbHospital3 = new Hospital(this.regionName + "foo", this.label + "bar");

        // prepare repository
        this.controller.getHospitalRepository().addItem(dbHospital1);
        this.controller.getHospitalRepository().addItem(dbHospital2);
        this.controller.getHospitalRepository().addItem(dbHospital3);
        Assert.assertEquals(3, this.controller.getHospitals().size());


        // error cases
        Region noRegion = null;
        Assert.assertEquals(0, this.controller.getHospitals(noRegion).size());
        Assert.assertEquals(0, this.controller.getHospitals(new Region()).size());
        Assert.assertEquals(0, this.controller.getHospitals(new Region("foobar")).size());
        String noString = null;
        Assert.assertEquals(0, this.controller.getHospitals(noString).size());
        Assert.assertEquals(0, this.controller.getHospitals("foobar").size());


        // test object method
        Assert.assertEquals(2, this.controller.getHospitals(new Region(this.regionName)).size());
        String firstId = this.controller.getHospitals(new Region(this.regionName)).get(0).getId();
        Assert.assertTrue(firstId.equals(dbHospital1.getId()) || firstId.equals(dbHospital2.getId()));

        String secondId = this.controller.getHospitals(new Region(this.regionName)).get(1).getId();
        Assert.assertTrue(secondId.equals(dbHospital1.getId()) || secondId.equals(dbHospital2.getId()));

        // test string method
        Assert.assertEquals(1, this.controller.getHospitals(this.regionName + "foo").size());
        firstId = this.controller.getHospitals(this.regionName + "foo").get(0).getId();
        Assert.assertEquals(firstId, dbHospital3.getId());
    }


    /**
     * 
     */
    @Test
    public void testServiceBinding() throws Exception {
    	
        HospitalRepository repo1 = new TransientHospitalRepository();
        repo1.addItem(new Hospital());
        repo1.addItem(new Hospital());

        RegionRepository repo2 = new TransientRegionRepository();
        repo2.addItem(new Region());

        Environment env = EnvironmentHelper.mockEnvironment((Properties) null);
        env.setAttribute(RegionRepository.REGION_REPOSITORY_INSTANCE, repo2);
        env.setAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE, repo1);

        BasicHospitalController service = new BasicHospitalController();

        Assert.assertNull(service.getHospitalRepository());
        Assert.assertNull(service.getRegionRepository());

        service.doBind(env);

        Assert.assertEquals(2, service.getHospitalRepository().getAll().size());
        Assert.assertEquals(1, service.getRegionRepository().getAll().size());

        service.doRelease();
        Assert.assertNull(service.getHospitalRepository());
        Assert.assertNull(service.getRegionRepository());
    }

    /**
     * 
     */
    @Test
    public void testRemoveUrgencyCategoryFromHospital() {
    	
    	this.setupController();

        Hospital noHospital = null;
        Hospital dbHospital = new Hospital(this.regionName, this.label);
        this.controller.getHospitalRepository().addItem(dbHospital);

        Assert.assertNull(this.controller.removeUrgencyCategory(dbHospital, null).getUrgencyCategories());
        Assert.assertNull(this.controller.removeUrgencyCategory(dbHospital, new UrgencyCategory()).getUrgencyCategories());
        Assert.assertNull(this.controller.removeUrgencyCategory(noHospital, null));
        Assert.assertNull(this.controller.removeUrgencyCategory(noHospital, new UrgencyCategory()));

        dbHospital.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        this.controller.getHospitalRepository().updateItem(dbHospital);

        Assert.assertEquals(0, this.controller.removeUrgencyCategory(dbHospital, null).getUrgencyCategories().size());
        Assert.assertEquals(0, this.controller.removeUrgencyCategory(dbHospital, new UrgencyCategory()).getUrgencyCategories().size());
        Assert.assertNull(this.controller.removeUrgencyCategory(noHospital, null));
        Assert.assertNull(this.controller.removeUrgencyCategory(noHospital, new UrgencyCategory()));

        // set up categories
        List<UrgencyCategory> categories = new ArrayList<>();
        categories.add(new UrgencyCategory(null, this.label + "foo", 1, 2, 3));
        categories.add(new UrgencyCategory(null, this.label + "bar", 4, 5, 6));
        dbHospital.setUrgencyCategories(categories);
        this.controller.getHospitalRepository().updateItem(dbHospital);

        Hospital result = this.controller.removeUrgencyCategory(dbHospital, new UrgencyCategory(null, this.label + "bar", 0, 0, 0));
        Assert.assertEquals(result.getId(), dbHospital.getId());

        Assert.assertEquals(1, result.getUrgencyCategories().size());
        Assert.assertEquals(1, result.getUrgencyCategories().get(0).getMaxWaitListStay());
        Assert.assertEquals(2, result.getUrgencyCategories().get(0).getMinPointsRequired());
        Assert.assertEquals(3, result.getUrgencyCategories().get(0).getPossiblePoints());
    }


    /**
     * 
     */
    @Test
    public void testGetRegions() {
    	
    	this.setupController();

        Assert.assertEquals(0, this.controller.getRegions().size());

        Region region1 = new Region(this.regionName);
        Region region2 = new Region(this.label);

        this.controller.getRegionRepository().addItem(region1);
        this.controller.getRegionRepository().addItem(region2);

        Assert.assertEquals(2, this.controller.getRegionRepository().getAll().size());
        Assert.assertEquals(2, this.controller.getRegions().size());
    }


    /**
     * 
     */
    @Test
    public void testCreateRegion() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.createRegion(null));

        Region region = new Region(this.regionName);
        Region result = this.controller.createRegion(region);

        Assert.assertEquals(result.getId(), this.controller.getRegionRepository().getAll().get(0).getId());
        Assert.assertEquals(this.regionName, result.getName());

        Assert.assertNull(this.controller.createRegion(result));
        Assert.assertEquals(1, this.controller.getRegionRepository().getAll().size());
    }


    /**
     * 
     */
    @Test
    public void testUpdateRegion() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.updateRegion(null));
        Assert.assertNull(this.controller.updateRegion(new Region(this.regionName)));

        Region region = new Region(this.regionName);
        this.controller.getRegionRepository().addItem(region);

        Region item = this.controller.getRegionRepository().getItem(region.getId());
        item.setName("foobar");
        this.controller.updateRegion(item);

        Assert.assertEquals("foobar", this.controller.getRegionRepository().getItem(region.getId()).getName());
    }


    /**
     * 
     */
    @Test
    public void testDeleteRegion() {
    	
    	this.setupController();

        Region region = new Region(this.regionName);
        this.controller.getRegionRepository().addItem(region);

        Region noRegion = null;
        String noString = null;

        Assert.assertFalse(this.controller.deleteRegion(noRegion));
        Assert.assertFalse(this.controller.deleteRegion(noString));
        Assert.assertFalse(this.controller.deleteRegion(new Region(this.regionName)));

        Assert.assertEquals(1, this.controller.getRegionRepository().getAll().size());

        Region delete = new Region(this.regionName);
        delete.setId(region.getId());
        Assert.assertTrue(this.controller.deleteRegion(delete));

        Assert.assertEquals(0, this.controller.getRegionRepository().getAll().size());

        region.setId(null);
        this.controller.getRegionRepository().addItem(region);

        Assert.assertEquals(1, this.controller.getRegionRepository().getAll().size());

        Assert.assertTrue(this.controller.deleteRegion(region.getId()));
        Assert.assertEquals(0, this.controller.getRegionRepository().getAll().size());
    }


    /**
     * 
     */
    @Test
    public void testRemoveUrgencyCategoryFromRegion() {
    	
    	this.setupController();

        Region noRegion = null;
        Assert.assertNull(this.controller.removeUrgencyCategory(noRegion, null));
        Assert.assertNull(this.controller.removeUrgencyCategory(noRegion, new UrgencyCategory()));

        Region region = new Region(this.regionName);
        Assert.assertNull(this.controller.removeUrgencyCategory(region, null).getUrgencyCategories());
        Assert.assertNull(this.controller.removeUrgencyCategory(region, new UrgencyCategory()).getUrgencyCategories());

        region.setUrgencyCategories(new ArrayList<UrgencyCategory>());
        Assert.assertEquals(0, this.controller.removeUrgencyCategory(region, null).getUrgencyCategories().size());
        Assert.assertEquals(0, this.controller.removeUrgencyCategory(region, new UrgencyCategory()).getUrgencyCategories().size());

        region.getUrgencyCategories().add(new UrgencyCategory(null, this.label, 1, 2, 3));
        Assert.assertEquals(1, this.controller.removeUrgencyCategory(region, null).getUrgencyCategories().size());
        Assert.assertEquals(1, this.controller.removeUrgencyCategory(region, new UrgencyCategory(null, "foobar", 1, 2, 3)).getUrgencyCategories().size());

        Assert.assertEquals(0, this.controller.removeUrgencyCategory(region, new UrgencyCategory(null, this.label, 4, 5, 6)).getUrgencyCategories().size());
    }


    /**
     * 
     */
    @Test
    public void testCreateDepartment() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.createDepartment(null, null));
        Assert.assertNull(this.controller.createDepartment(new Hospital(), null));
        Assert.assertNull(this.controller.createDepartment(null, new Department()));

        Hospital hospital = new Hospital(this.regionName, this.label);
        this.controller.createDepartment(hospital, new Department(this.label, 5));

        Assert.assertEquals(1, hospital.getDepartments().size());

        // duplicate department
        Assert.assertNull(this.controller.createDepartment(hospital, new Department(this.label, 5)));

        Hospital result = this.controller.createDepartment(hospital, new Department("foobar", 2));
        Assert.assertEquals(2, result.getDepartments().size());
    }

    /**
     * 
     */
    @Test
    public void testCreateWard() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.createWard(null, null));
        Assert.assertNull(this.controller.createWard(new Hospital(), null));
        Assert.assertNull(this.controller.createWard(null, new Ward()));

        Hospital hospital = new Hospital(this.regionName, this.label);
        this.controller.createWard(hospital, new Ward(this.label, 5));

        Assert.assertEquals(1, hospital.getWards().size());

        // duplicate ward
        Assert.assertNull(this.controller.createWard(hospital, new Ward(this.label, 5)));

        Hospital result = this.controller.createWard(hospital, new Ward("foobar", 2));
        Assert.assertEquals(2, result.getWards().size());
    }


    /**
     * 
     */
    @Test
    public void testCreateSpecialistType() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.createSpecialistType(null, null));
        Assert.assertNull(this.controller.createSpecialistType(new Hospital(), null));
        Assert.assertNull(this.controller.createSpecialistType(null, new SpecialistType()));

        Hospital hospital = new Hospital(this.regionName, this.label);
        this.controller.createSpecialistType(hospital, new SpecialistType(this.label, "dep"));

        Assert.assertEquals(1, hospital.getSpecialistTypes().size());

        // duplicate
        Assert.assertNull(this.controller.createSpecialistType(hospital, new SpecialistType(this.label, "dep")));

        Hospital result = this.controller.createSpecialistType(hospital, new SpecialistType("foobar", "dep"));
        Assert.assertEquals(2, result.getSpecialistTypes().size());
    }

    /**
     * 
     */
    @Test
    public void testUpdateDepartments() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.updateDepartments(null, null));
        Assert.assertNull(this.controller.updateDepartments(new Hospital(), null).getDepartments());
        Assert.assertNull(this.controller.updateDepartments(null, new ArrayList<Department>()));
        Assert.assertEquals(0, this.controller.updateDepartments(new Hospital(), new ArrayList<Department>()).getDepartments().size());

        List<Department> deps = new ArrayList<>();
        deps.add(new Department(this.label, 5));
        deps.add(new Department(this.label + "bar", 3));

        Hospital h = new Hospital(this.regionName, this.label);
        this.controller.updateDepartments(h, deps);
        Assert.assertEquals(2, h.getDepartments().size());

        List<Department> deps2 = new ArrayList<>();
        deps2.add(new Department(this.label, 5));
        deps2.add(new Department(this.label, 3));
        deps2.add(new Department(this.label + "bar", 1));
        Assert.assertNull(this.controller.updateDepartments(h, deps2));
        Assert.assertEquals(2, h.getDepartments().size());
    }

    /**
     * 
     */
    @Test
    public void testUpdateWards() {
    	
        this.setupController();

        Assert.assertNull(this.controller.updateWards(null, null));
        Assert.assertNull(this.controller.updateWards(new Hospital(), null).getWards());
        Assert.assertNull(this.controller.updateWards(null, new ArrayList<Ward>()));
        Assert.assertEquals(0, this.controller.updateWards(new Hospital(), new ArrayList<Ward>()).getWards().size());

        List<Ward> wards = new ArrayList<>();
        wards.add(new Ward(this.label, 5));
        wards.add(new Ward(this.label + "bar", 3));

        Hospital h = new Hospital(regionName, label);
        this.controller.updateWards(h, wards);
        Assert.assertEquals(2, h.getWards().size());

        List<Ward> wards2 = new ArrayList<>();
        wards2.add(new Ward(this.label, 5));
        wards2.add(new Ward(this.label, 3));
        wards2.add(new Ward(this.label + "bar", 1));
        Assert.assertNull(this.controller.updateWards(h, wards2));
        Assert.assertEquals(2, h.getWards().size());
    }

    /**
     * 
     */
    @Test
    public void testUpdateSpecialistTypes() {
    	
    	this.setupController();

        Assert.assertNull(this.controller.updateSpecialistTypes(null, null));
        Assert.assertNull(this.controller.updateSpecialistTypes(new Hospital(), null).getSpecialistTypes());
        Assert.assertNull(this.controller.updateSpecialistTypes(null, new ArrayList<SpecialistType>()));
        Assert.assertEquals(0, this.controller.updateSpecialistTypes(new Hospital(), new ArrayList<SpecialistType>()).getSpecialistTypes().size());

        List<SpecialistType> types = new ArrayList<>();
        types.add(new SpecialistType(this.label, "dep"));
        types.add(new SpecialistType(this.label + "bar", "dep"));

        Hospital h = new Hospital(this.regionName, this.label);
        this.controller.updateSpecialistTypes(h, types);
        Assert.assertEquals(2, h.getSpecialistTypes().size());

        List<SpecialistType> types2 = new ArrayList<>();
        types2.add(new SpecialistType(this.label, "dep"));
        types2.add(new SpecialistType(this.label, "dep"));
        types2.add(new SpecialistType(this.label + "bar", "dep"));
        Assert.assertNull(this.controller.updateSpecialistTypes(h, types2));
        Assert.assertEquals(2, h.getSpecialistTypes().size());
    }

    /**
     * 
     */
    @Test
    public void testUpdateUrgencyCategories() {
    	
    	this.setupController();

        Hospital noHospital = null;
        Region noRegion = null;

        Assert.assertNull(this.controller.updateUrgencyCategories(noHospital, null));
        Assert.assertNull(this.controller.updateUrgencyCategories(noHospital, new ArrayList<UrgencyCategory>()));

        Assert.assertNull(this.controller.updateUrgencyCategories(noRegion, null));
        Assert.assertNull(this.controller.updateUrgencyCategories(noRegion, new ArrayList<UrgencyCategory>()));


        Hospital hospital = new Hospital(this.regionName, this.label);
        Region region = new Region(this.regionName);

        Hospital resultHospital = this.controller.updateUrgencyCategories(hospital, new ArrayList<UrgencyCategory>());
        Region resultRegion = this.controller.updateUrgencyCategories(region, new ArrayList<UrgencyCategory>());

        Assert.assertEquals(0, resultHospital.getUrgencyCategories().size());
        Assert.assertEquals(0, resultRegion.getUrgencyCategories().size());

        List<UrgencyCategory> categories = new ArrayList<>();
        categories.add(new UrgencyCategory(null, "foo", 1, 2, 3));
        categories.add(new UrgencyCategory(null, "bar", 4, 5, 6));

        Hospital resultHospital2 = this.controller.updateUrgencyCategories(hospital, categories);
        Region resultRegion2 = this.controller.updateUrgencyCategories(region, categories);

        Assert.assertEquals(2, resultHospital2.getUrgencyCategories().size());
        Assert.assertEquals(2, resultRegion2.getUrgencyCategories().size());
    }

    /* HELPER METHODS */

    /**
     * 
     */
    private void setupController() {
    	
    	this.controller = new BasicHospitalController();
    	this.controller.setHospitalRepository(new TransientHospitalRepository());
    	this.controller.setRegionRepository(new TransientRegionRepository());
    }

}
