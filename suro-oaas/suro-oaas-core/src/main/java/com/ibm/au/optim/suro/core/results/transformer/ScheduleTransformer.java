package com.ibm.au.optim.suro.core.results.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.au.optim.suro.core.results.mapper.AbstractJsonMapper;
import com.ibm.au.optim.suro.model.control.ResultMapper;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;

import java.io.IOException;
import java.util.List;

/**
 * This is a custom transformer, which extracts schedule data from the solution.json and returns it in the desired
 * output format. This transformer hard-codes field names and structure, which means it highly depends on the solution
 * output
 *
 * @author Peter Ilfrich
 */
public class ScheduleTransformer extends AbstractJsonMapper implements ResultMapper {

    /**
     * The object mapper used to create nodes.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();


    @Override
    public List<String[]> transformToCsv(JsonNode node, OutputMapping mapping) throws IOException {
        return null;
    }

    @Override
    public JsonNode transformToJson(JsonNode node, OutputMapping mapping) throws IOException {
        ObjectNode schedule = MAPPER.createObjectNode();

        ArrayNode scheduleData = (ArrayNode) node.get("schedule");
        ArrayNode schedulePatientsData = (ArrayNode) node
                .get("scheduleWithPatient");

        for (JsonNode entry : scheduleData) {
            schedule = processScheduleEntry(schedule, ((ObjectNode) entry));
        }

        for (JsonNode entry : schedulePatientsData) {
            schedule = processScheduleEntry(schedule, ((ObjectNode) entry));
        }

        return schedule;
    }


    @Override
    public boolean validate(OutputMapping mapping) {
        return validate(mapping, new String[] { "json" });
    }

    /**
     * Processes the content of a JSON node and integrates the data into the existing schedule that is returned.
     * @param schedule - the current schedule
     * @param item - the item to add to the schedule
     * @return - the schedule after adding the item.
     */
    private ObjectNode processScheduleEntry(ObjectNode schedule, ObjectNode item) {

        int medicalUnitId = item.get("medicalUnitId").intValue();

        ArrayNode dateEntry = initDate(schedule, item);
        ObjectNode medicalUnitItem = lookupByMedicalUnit(medicalUnitId,
                dateEntry);

        if (medicalUnitItem == null) {
            medicalUnitItem = MAPPER.createObjectNode();

            medicalUnitItem.put("medicalUnitId", medicalUnitId);
            medicalUnitItem.put("allocatedSessions",
                    item.get("allocatedSessions").intValue());
            medicalUnitItem.put("allocatedSessionsBase",
                    item.get("allocatedSessionsBase").intValue());
            medicalUnitItem.set("treatedPatients", MAPPER.createArrayNode());

            dateEntry.add(medicalUnitItem);
        }

        ObjectNode patientObject = extractPatientObject(item);
        if (patientObject != null) {
            ((ArrayNode) medicalUnitItem.get("treatedPatients"))
                    .add(patientObject);
        }

        return schedule;
    }

    /**
     * Creates a new array node for the given item in the provided schedule. It extracts day and week from the item and
     * creates a new node in the schedule, which will store the data for this day of this week.
     * @param schedule - the current schedule object with all the schedule entries that have already been processed.
     * @param item - the item to use to retrieve day and week from
     * @return - the schedule after the new day/week has been added.
     */
    private ArrayNode initDate(ObjectNode schedule, ObjectNode item) {
        // extract data
        String week = item.get("week").textValue();
        String day = item.get("day").textValue();

        // make sure the week node exists
        if (!schedule.has(week)) {
            schedule.set(week, MAPPER.createObjectNode());
        }

        // fetch the week node
        ObjectNode weekNode = (ObjectNode) schedule.get(week);

        // make sure the day node exists
        if (!weekNode.has(day)) {
            weekNode.set(day, MAPPER.createArrayNode());
        }

        // return the day node
        return (ArrayNode) schedule.get(week).get(day);
    }

    /**
     * Extracts patient information from the `treatedPatient` sub element of an item and returns it as JSON object
     * @param data - the data for a specific day containing the schedule and patient information
     * @return - the extracted patient information from the provided data node.
     */
    private ObjectNode extractPatientObject(ObjectNode data) {
        // only if treatedPatients are defined.
        if (data.has("treatedPatient.surgeryType")) {
            // create new node
            ObjectNode treatedPatient = MAPPER.createObjectNode();
            // add fields
            treatedPatient.put("surgeryType", data.get("treatedPatient.surgeryType").textValue());
            treatedPatient.put("isOverdue", data.get("treatedPatient.isOverdue").booleanValue());
            treatedPatient.put("daysUntilOverdue", data.get("treatedPatient.daysUntilOverdue").intValue());
            // return patient node
            return treatedPatient;
        }
        return null;
    }

    /**
     * Retrieves the node containing the medical unit information from the provided data.
     * @param medicalUnitId - the medical unit to retrieve
     * @param data - the data containing the medical unit
     * @return - the json node containing the medical unit data or null, if the medical unit could not be found.
     */
    private ObjectNode lookupByMedicalUnit(int medicalUnitId, ArrayNode data) {
        for (int idx = 0; idx < data.size(); idx++) {
            ObjectNode node = (ObjectNode) data.get(idx);
            if (node.get("medicalUnitId").intValue() == medicalUnitId) {
                return node;
            }
        }

        return null;
    }
}
