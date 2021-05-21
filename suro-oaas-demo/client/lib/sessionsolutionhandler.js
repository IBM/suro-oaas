/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */


SessionSolutionHandler = function(solution) {
    this.solution = solution;
}


/**
 * Lookup tables
 */

const weekDays = {
    monday: 1,
    tuesday: 2,
    wednesday: 3,
    thursday: 4,
    friday: 5,
    saturday: 6,
    sunday: 7,
}

// TODO: departments should contain the unit codes as attributes
const departments = {
    11: 'Cardiac Surgery',
    12: 'Cardiology',
    13: 'Dental Unit',
    15: 'Gastroenterology',
    16: 'General Surgery',
    18: 'Neurosurgery',
    19: 'Ophthalmology',
    20: 'Orthopaedic - General',
    21: 'Otolaryngology',
    22: 'Plastic Surgery',
    23: 'Urology',
}

function parsePeriod(week, day) {

    const weekInt = parseInt(week.substr(5), 10);
    const dayInt = weekDays[day];

    return ((weekInt - 1) * 7) + dayInt;
}


SessionSolutionHandler.prototype.parse = function() {

    this.parseSurgeryTypes();
    this.parseSchedule();
    this.parseScheduleSummary();

}

SessionSolutionHandler.prototype.parseSchedule = function() {
    const sessions = {};

    // 1: { depName: { sessions: 2, baseSessions: 4, patients: 4 } },
    // 2: { depName: { sessions: 0, baseSessions: 2, patients: 0 },
    //      depName2: { sessions: 4, baseSessions: 4, patients: 8 } }
    // ...

    const self = this;
    this.solution.scheduleWithPatient.forEach((patient) => {

        const period = parsePeriod(patient.week, patient.day);
        if (sessions[period] === undefined) {
            sessions[period] = {};
        }

        const departmentName = departments[patient.medicalUnitId];
        if (sessions[period][departmentName] === undefined) {
            sessions[period][departmentName] = {
                patients: 0,
                surgeryTypes: [],
            };
        }

        sessions[period][departmentName].sessions = patient.allocatedSessions;
        sessions[period][departmentName].baseSessions = patient.allocatedSessionsBase;
        sessions[period][departmentName].patients += 1;
        sessions[period][departmentName].unitCode = patient.medicalUnitId;
        sessions[period][departmentName].surgeryTypes.push({
            cluster: patient['treatedPatient.surgeryType'],
            remaining: patient['treatedPatient.daysUntilOverdue'],
            category: self.surgeryTypes[patient['treatedPatient.surgeryType']],
            duration: self.surgeryDurations[patient['treatedPatient.surgeryType']],
            lengthOfStay: self.lengthOfStay[patient['treatedPatient.surgeryType']],
            icuProbability: self.icuProbability[patient['treatedPatient.surgeryType']],
        });
    });

    this.sessions = sessions;

}


SessionSolutionHandler.prototype.parseSurgeryTypes = function() {

    const types = {};
    const durations = {};
    const lengthOfStay = {};
    const icuProbability = {};

    this.solution.schedule_surgery_types.forEach((type) => {

        types[type.clusterName] = type.urgencyCategory;
        durations[type.clusterName] = Math.round(type.duration);
        icuProbability[type.clusterName] = type.icuRequirementProbability;
        lengthOfStay[type.clusterName] = type.lengthOfStay;

    });

    this.surgeryTypes = types;
    this.surgeryDurations = durations;
    this.lengthOfStay = lengthOfStay;
    this.icuProbability = icuProbability;

}

/**
 *  Returns the total number of sessions for all the departments
 *  @returns {Array} list of sessions
 */
SessionSolutionHandler.prototype.parseScheduleSummary = function() {
    const sessions = [];
    const self = this;
    $.each(departments, (i, val) => {
        const department = {
            departments: val,
            sessions: 0,
        }
        self.solution.sessions.forEach((session) => {
            if (session.department === val) {
                department.sessions += session.value;
            }
        })
        sessions.push(department);

    })

    return sessions;


}
