/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

BedSolutionHandler = function(solution) {
    this.solution = solution;
}


/**
 * Lookup tables
 */

// TODO: departments should contain the unit codes as attributes
const departments = {
    'Cardiac Surgery': 11,
    Cardiology: 12,
    'Dental Unit': 13,
    Gastroenterology: 15,
    'General Surgery': 16,
    Neurosurgery: 18,
    Ophthalmology: 19,
    'Orthopaedic - General': 20,
    Otolaryngology: 21,
    'Plastic Surgery': 22,
    Urology: 23,
}

/**
 * Helper Functions
 */

/**
 *
 */


function getDepartmentName(unitCode) {
    let result = '';
    Object.keys(departments).forEach((departmentName) => {
        if (departments[departmentName] === unitCode) {
            result = departmentName;
        }
    });

    return result;
}

/**
 * High level functions
 */

BedSolutionHandler.prototype.parse = function() {

    this.parseBeds();
    this.parseIcuBeds();
    this.parseAvailableBeds();
    this.parseBedsByDepartment();

}

BedSolutionHandler.prototype.getDepartments = function() {
    return departments;
}


BedSolutionHandler.prototype.parseBeds = function() {

    const beds = {};

    this.solution.bedRes.forEach((bedDep) => {
        const depName = bedDep.name;
        const period = bedDep.period;
        const value = bedDep.value;

        if (beds[period] === undefined) {
            beds[period] = {};
        }

        beds[period][depName] = value;
    });

    this.beds = beds;

}

BedSolutionHandler.prototype.parseIcuBeds = function() {

    const beds = {};

    this.solution.icuRes.forEach((bedDep) => {
        const depName = bedDep.name;
        const period = bedDep.period;
        const value = bedDep.value;

        if (beds[period] === undefined) {
            beds[period] = {};
        }

        beds[period][depName] = value;
    });

    this.icus = beds;
}

BedSolutionHandler.prototype.parseAvailableBeds = function() {
    const beds = {};

    this.solution.bedAvailRes.forEach((period) => {
        beds[period.period] = period.value;
    });

    this.availableBeds = beds;

    const icus = {}
    this.solution.icuAvailRes.forEach((period) => {
        icus[period.period] = period.value;
    });

    this.availableIcus = icus;
}


BedSolutionHandler.prototype.parseBedsByDepartment = function() {
    const deps = {
        'Cardiac Surgery': 0,
        Cardiology: 0,
        'Dental Unit': 0,
        Gastroenterology: 0,
        'General Surgery': 0,
        Neurosurgery: 0,
        Ophthalmology: 0,
        'Orthopaedic - General': 0,
        Otolaryngology: 0,
        'Plastic Surgery': 0,
        Urology: 0,
    };
    for (let index = 1; index < 29; index++) {
        const tBed = this.beds[index.toString()];
        Object.keys(deps).forEach((key) => {
            deps[key] += tBed[key];
        });
    }

    this.bedsByDepartment = deps;
}
