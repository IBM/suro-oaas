package com.ibm.au.optim.suro.core.migration.preparer.v004;

import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.optim.suro.core.composer.NTuple;
import com.ibm.au.optim.suro.core.composer.components.TemporalInputComponent;
import com.ibm.au.optim.suro.model.control.domain.HospitalController;
import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.control.domain.learning.MachineLearningController;
import com.ibm.au.optim.suro.model.entities.domain.*;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.ibm.au.optim.suro.model.entities.domain.learning.ArrivingPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.InitialPatient;
import com.ibm.au.optim.suro.model.entities.domain.learning.SurgeryCluster;
import com.ibm.au.optim.suro.model.store.*;
import com.ibm.au.optim.suro.model.store.domain.HospitalRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.BasePlanListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.SpecialistAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WardAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.ArrivingPatientRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.InitialPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.learning.SurgeryClusterListRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * @author brendanhaesler
 */

// TODO: Document this class
public class IngestionDatabasePreparer implements DatabasePreparer {

    protected static final String SAMPLE_DAT_FILE = "migration/0.0.4/model/optimization-model-data.dat";
    protected static final String SAMPLE_REGION_ID = "SAMPLE REGION";
    protected static final String SAMPLE_HOSPITAL_NAME = "SAMPLE HOSPITAL";
    protected static final long SAMPLE_TIME_FROM = 1451606400000l; // 01/01/2016 12:00am

    @Override
    public boolean check(Environment env) throws Exception {
        return ((HospitalRepository) env.getAttribute(HospitalRepository.HOSPITAL_REPOSITORY_INSTANCE)).getAll().isEmpty()
                && ((SpecialistAvailabilitiesRepository) env.getAttribute(SpecialistAvailabilitiesRepository.SPECIALIST_AVAILABILITY_REPOSITORY_INSTANCE)).getAll().isEmpty()
                && ((WardAvailabilitiesRepository) env.getAttribute(WardAvailabilitiesRepository.WARD_AVAILABILITY_REPOSITORY_INSTANCE)).getAll().isEmpty()
                && ((ArrivingPatientRepository) env.getAttribute(ArrivingPatientRepository.ARRIVING_PATIENT_REPOSITORY_INSTANCE)).getAll().isEmpty()
                && ((InitialPatientListRepository) env.getAttribute(InitialPatientListRepository.INITIAL_PATIENT_LIST_REPOSITORY_INSTANCE)).getAll().isEmpty()
                && ((BasePlanListRepository) env.getAttribute(BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE)).getAll().isEmpty()
                && ((SurgeryClusterListRepository) env.getAttribute(SurgeryClusterListRepository.SURGERY_CLUSTER_LIST_REPOSITORY_INSTANCE)).getAll().isEmpty();
    }

    @Override
    public void execute(Environment env) throws Exception {
    	
        SortedMap<IngestionDatabasePreparer.DatSection, String> sections = new TreeMap<>();
        sections.put(IngestionDatabasePreparer.DatSection.DEPARTMENTS, "DEPARTMENTS");
        sections.put(IngestionDatabasePreparer.DatSection.SPECIALIST_INFO, "SPECIALIST_INFO");
        sections.put(IngestionDatabasePreparer.DatSection.WARD_INFO, "WARD_INFO");
        sections.put(IngestionDatabasePreparer.DatSection.ARRIVING_PATIENT_INFO, "arrivingPatientInfo");
        sections.put(IngestionDatabasePreparer.DatSection.INITIAL_PATIENT_INFO, "initialPatientInfo");
        sections.put(IngestionDatabasePreparer.DatSection.BASE_PLAN_INFO, "BASE_PLAN_INFO");
        sections.put(IngestionDatabasePreparer.DatSection.SURGERY_CLUSTERS, "SURGERIES");
        sections.put(IngestionDatabasePreparer.DatSection.SURGERY_CATEGORIES, "maxWaitListStay");

        this.populateDatabasesFromDatFile(SAMPLE_DAT_FILE, env, SAMPLE_REGION_ID, SAMPLE_HOSPITAL_NAME, SAMPLE_TIME_FROM, sections);
    }

    @Override
    public boolean validate(Environment env) throws Exception {
        return !check(env);
    }

    public void populateDatabasesFromDatFile(String datFilename,
                                             Environment environment,
                                             String regionId,
                                             String hospitalName,
                                             long timeFrom,
                                             SortedMap<DatSection, String> sectionNames) throws IOException {
        // Create a sample hospital
        HospitalController hospitalController =
                (HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        String hospitalId = hospitalController.createHospital(new Hospital(regionId, hospitalName)).getId();

        for (Map.Entry<DatSection, String> section : sectionNames.entrySet()) {

        	InputStream datStream = IngestionDatabasePreparer.class.getClassLoader().getResourceAsStream(datFilename);
        	
            Reader reader = new BufferedReader(new InputStreamReader(datStream));

            switch (section.getKey()) {
                case DEPARTMENTS:
                    populateDepartments(section.getValue(), reader, environment, hospitalId);
                    break;
                case SPECIALIST_INFO:
                    populateSpecialistInfo(section.getValue(), reader, environment, hospitalId, timeFrom);
                    break;
                case WARD_INFO:
                    populateWardInfo(section.getValue(), reader, environment, hospitalId, timeFrom);
                    break;
                case ARRIVING_PATIENT_INFO:
                    populateArrivingPatientInfo(section.getValue(), reader, environment, timeFrom);
                    break;
                case INITIAL_PATIENT_INFO:
                    populateInitialPatientInfo(section.getValue(), reader, environment, timeFrom);
                    break;
                case BASE_PLAN_INFO:
                    populateBasePlanInfo(section.getValue(), reader, environment, timeFrom);
                    break;
                case SURGERY_CLUSTERS:
                    populateSurgeryClusters(section.getValue(), reader, environment, timeFrom);
                    break;
                case SURGERY_CATEGORIES:
                    populateSurgeryCategories(section.getValue(), reader, environment, hospitalId);
                    break;
            }
        }
    }

    private void populateDepartments(String sectionName,
                                     Reader reader,
                                     Environment environment,
                                     String hospitalId) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the hospital and controller
        HospitalController hospitalController =
                (HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        Hospital hospital = hospitalController.getHospital(hospitalId);

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            // create and add the department
            Department department = new Department((String) tuple.getElement(1),
                    ((Number) tuple.getElement(2)).intValue());
            department.setId((String) tuple.getElement(0));
            hospitalController.createDepartment(hospital, department);
        }
    }

    private void populateSpecialistInfo(String sectionName,
                                        Reader reader,
                                        Environment environment,
                                        String hospitalId,
                                        long timeFrom) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the ingestion and hospital controllers
        IngestionController ingestionController =
                (IngestionController) environment.getAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE);
        HospitalController hospitalController =
                (HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        Hospital hospital = hospitalController.getHospital(hospitalId);

        List<SpecialistAvailability> saList = new ArrayList<>();
        List<String> specialistTypes = new ArrayList<>();

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            // store the specialist type if it doesn't exist
            String specialistTypeId = (String) tuple.getElement(0);

            if (!specialistTypes.contains(specialistTypeId)) {
                specialistTypes.add(specialistTypeId);
            }

            // create the availability
            saList.add(new SpecialistAvailability((String) tuple.getElement(0),
                    (((Number) tuple.getElement(1)).intValue() - 1) * TemporalInputComponent.MS_IN_DAY + timeFrom,
                    ((Number) tuple.getElement(2)).intValue()));
        }

        // create specialist types
        for (String type : specialistTypes) {
            SpecialistType specialistType = new SpecialistType("Specialist Type " + type, "Unknown");
            specialistType.setId(type);
            hospitalController.createSpecialistType(hospital, specialistType);
        }

        ingestionController.createRecordFromList(timeFrom, saList);
    }

    private void populateWardInfo(String sectionName,
                                  Reader reader,
                                  Environment environment,
                                  String hospitalId,
                                  long timeFrom) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the hospital and ingestion controller
        IngestionController ingestionController =
                (IngestionController) environment.getAttribute(IngestionController.INGESTION_CONTROLLER_INSTANCE);
        HospitalController hospitalController =
                (HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        Hospital hospital = hospitalController.getHospital(hospitalId);

        // create a map of wards to availabilities
        Map<String, List<Integer>> availabilities = new HashMap<>();

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            // get the list od bed availabilities from the ward name
            List<Integer> bedsByDay = availabilities.get(tuple.getElement(0));

            // if the list doesn't yet exist, create it
            if (bedsByDay == null) {
                bedsByDay = new ArrayList<>();
                availabilities.put((String) tuple.getElement(0), bedsByDay);
            }

            // get the day this number of beds applies to
            int day = ((Number) tuple.getElement(1)).intValue();

            // if the day index doesn't exist in the list, create entries until it does
            if (bedsByDay.size() < day) {
                for (int i = bedsByDay.size(); i < day; ++i) {
                    bedsByDay.add(0);
                }
            }

            // add the number of beds to the day's index
            bedsByDay.set(day - 1, ((Number) tuple.getElement(2)).intValue());
        }

        // create wards and availabilities from map
        List<WardAvailability> wardAvailabilityList = new ArrayList<>();
        int patientId = 0;

        // for each ward in the map
        for (Map.Entry<String, List<Integer>> entry : availabilities.entrySet()) {
            // create the ward
            Ward ward = new Ward("Ward " + entry.getKey(), entry.getValue().get(entry.getValue().size() - 1));
            ward.setId(entry.getKey());
            hospitalController.createWard(hospital, ward);

            // find out how many beds are freed each day. Start by setting the previous number of beds seen to the number of
            // beds in the ward
            int previousNumBeds = ward.getBedsCount();

            // for each day in the list
            for (int day = entry.getValue().size() - 1; day >= 0; --day) {
                // calculate how many beds are free this day that weren't the previous day
                int bedDifference = previousNumBeds - entry.getValue().get(day);

                // create availabilities for each bed that became free
                for (int beds = 0; beds < bedDifference; ++beds) {
                    wardAvailabilityList.add(new WardAvailability(ward.getId(),
                            "" + patientId++,
                            timeFrom + day * TemporalInputComponent.MS_IN_DAY));
                }

                previousNumBeds = entry.getValue().get(day);
            }
        }

        // store the availabilities
        ingestionController.createRecordFromList(timeFrom, wardAvailabilityList);
    }

    private void populateArrivingPatientInfo(String sectionName,
                                             Reader reader,
                                             Environment environment,
                                             long timeFrom) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the machine learning controller
        MachineLearningController machineLearningController = (MachineLearningController) environment.getAttribute(
                MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE);

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            // create and add the arriving patient
            machineLearningController.addArrivingPatient(new ArrivingPatient(tuple.getElement(0).toString(),
                    (((Number) tuple.getElement(1)).intValue() - 1) * TemporalInputComponent.MS_IN_DAY + timeFrom,
                    ((Number) tuple.getElement(2)).intValue()));
        }
    }

    private void populateInitialPatientInfo(String sectionName,
                                            Reader reader,
                                            Environment environment,
                                            long timeFrom) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the machine learning controller
        MachineLearningController machineLearningController = (MachineLearningController) environment.getAttribute(
                MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE);

        // create a list of initial patients
        List<InitialPatient> initialPatients = new ArrayList<>();

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            initialPatients.add(new InitialPatient(tuple.getElement(0).toString(),
                    ((Number) tuple.getElement(1)).intValue(),
                    ((Number) tuple.getElement(2)).intValue()));
        }

        // store the initial patients
        machineLearningController.addInitialPatients(timeFrom, initialPatients);
    }

    private void populateBasePlanInfo(String sectionName,
                                      Reader reader,
                                      Environment environment,
                                      long timeFrom) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the ingestion controller
        IngestionController ingestionController = (IngestionController) environment.getAttribute(
                IngestionController.INGESTION_CONTROLLER_INSTANCE);

        // create a list of base plan entries
        List<BasePlanEntry> basePlanEntries = new ArrayList<>();

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            basePlanEntries.add(new BasePlanEntry(((NTuple) tuple.getElement(0)).getElement(0).toString(),
                    ((Number) tuple.getElement(1)).intValue(),
                    ((Number) tuple.getElement(2)).intValue()));
        }

        // store the base plan
        ingestionController.createRecordFromList(timeFrom, basePlanEntries);
    }

    private void populateSurgeryClusters(String sectionName,
                                         Reader reader,
                                         Environment environment,
                                         long timeFrom) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '{'
        readUntilStringFound("{", reader);

        // get the machine learning controller
        MachineLearningController machineLearningController = (MachineLearningController) environment.getAttribute(
                MachineLearningController.MACHINE_LEARNING_CONTROLLER_INSTANCE);

        // create a list of surgery clusters
        List<SurgeryCluster> surgeryClusterList = new ArrayList<>();

        // read all tuples
        while (NTuple.hasNextNTuple(reader)) {
            NTuple tuple = NTuple.readTuple(reader);

            surgeryClusterList.add(new SurgeryCluster(tuple.getElement(0).toString(),
                    tuple.getElement(1).toString(),
                    ((NTuple) tuple.getElement(2)).getElement(0).toString(),
                    ((Number) tuple.getElement(3)).floatValue(),
                    ((Number) tuple.getElement(4)).floatValue(),
                    ((Number) tuple.getElement(5)).floatValue(),
                    ((Number) tuple.getElement(6)).floatValue(),
                    tuple.getElement(7).toString(),
                    tuple.getElement(8).toString(),
                    tuple.getElement(9).toString(),
                    ((Number) tuple.getElement(10)).floatValue()));
        }

        // store the surgery clusters
        machineLearningController.addSurgeryClusters(timeFrom, surgeryClusterList);
    }

    private void populateSurgeryCategories(String sectionName,
                                           Reader reader,
                                           Environment environment,
                                           String hospitalId) throws IOException {
        // read up to the section we want
        readUntilStringFound(sectionName, reader);
        // read up to the '['
        readUntilStringFound("[", reader);

        // get the hospital controller
        HospitalController hospitalController =
                (HospitalController) environment.getAttribute(HospitalController.HOSPITAL_CONTROLLER_INSTANCE);
        Hospital hospital = hospitalController.getHospital(hospitalId);

        // create a list of surgery clusters
        List<UrgencyCategory> urgencyCategories = new ArrayList<>();

        // read surgery categories
        StringBuilder token = new StringBuilder();
        int category = 1;

        char c = '\0';

        while (c != ']') {
            c = (char) reader.read();

            switch (c) {
                case ' ':
                case ']':
                    if (token.length() > 0) {
                        urgencyCategories.add(
                                new UrgencyCategory(null, "" + category++, Integer.parseInt(token.toString()), 0, 0));
                        token = new StringBuilder();
                    }
                    break;
                default:
                    token.append(c);
            }
        }

        // store the urgency categories
        hospitalController.updateUrgencyCategories(hospital, urgencyCategories);
    }

    private void readUntilStringFound(String string, Reader reader) throws IOException {
        // record how many characters of the given section name have been matched
        int matched = 0;

        // while we haven't found the section, read using the reader
        while (matched < string.length()) {
            // read the next character
            char c = (char) reader.read();

            // if it matches the current character of the section name we're looking for
            if (c == string.charAt(matched)) {
                // increment the match count
                ++matched;
            } else {
                // otherwise reset the match count
                matched = 0;
            }
        }
    }

    /**
     * This is used to specify which sections of the sample dat file need to be read into the database.
     */
    public enum DatSection {
        DEPARTMENTS,
        SPECIALIST_INFO,
        WARD_INFO,
        ARRIVING_PATIENT_INFO,
        INITIAL_PATIENT_INFO,
        BASE_PLAN_INFO,
        SURGERY_CLUSTERS,
        SURGERY_CATEGORIES,
    }
}
