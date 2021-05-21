/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

PatientSolutionHandler = function(solution) {
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

/**
 * Function to ingest a new entry into an existing list by matching category and department
 * @param {Array} list - the input list
 * @param {Object} entry - the entry to add
 * @returns {Array} - the input list after the entry has been added
 */
function updateListWithEntry(list, entry) {
    const newList = [];
    let match = false;
    list.forEach((existing) => {
        if (existing.department === entry.department && existing.category === entry.category) {
            // update the current value
            existing.value += entry.value;
            match = true;
        }
        newList.push(existing);
    });
    if (!match) {
        // add a new entry to the list
        newList.push({ category: entry.category, department: entry.department, value: entry.value });
    }

    return newList;
}


function getValueFromEntryList(list, entry) {
    let result = 0;
    list.forEach((existing) => {
        if (existing.category === entry.category && existing.department === entry.department) {
            result = existing.value;
        }
    });

    return result;
}

function getDepartmentName(unitCode) {
    let result = '';
    Object.keys(departments).forEach((departmentName) => {
        if (departments[departmentName] === unitCode) {
            result = departmentName;
        }
    });

    return result;
}

function parsePeriod(week, day) {

    const weekInt = parseInt(week.substr(5), 10);
    const dayInt = weekDays[day];

    return ((weekInt - 1) * 7) + dayInt;
}

function checkFilters(catDepValue, filter) {

    if (filter.department.length > 0 && filter.department.indexOf(catDepValue.department) === -1) {
        return false;
    }

    if (filter.category.length > 0 && filter.category.indexOf(catDepValue.category) === -1) {
        return false;
    }

    return true;
}

/**
 * High level functions
 */

PatientSolutionHandler.prototype.parse = function() {

    this.parseSurgeryTypes();
    this.parseTreated();
    this.parseWaiting();

}

PatientSolutionHandler.prototype.getPatientData = function(uiFilter) {

    const result = [];

    let baseData;
    if (uiFilter.view === 'treated') {
        baseData = this.treated;
    } else if (uiFilter.view === 'waitlist') {
        baseData = this.waiting;
    }

    const departmentFilter = [];
    uiFilter.department.forEach((depName) => {
        departmentFilter.push(departments[depName]);
    });

    const filter = {
        category: uiFilter.category,
        department: departmentFilter,
        status: uiFilter.status.length > 0 ? uiFilter.status : ['onTime', 'overdue'],
        explode: uiFilter.explode,
    };

    for (let period = 1; period <= Object.keys(baseData).length; period++) {

        const currentLists = baseData[period];

        if (filter.explode === 'status') {

            const entry = {
                onTime: 0,
                overdue: 0,
            }

            // only if stats available for day (not the case for saturday/sunday)
            if (currentLists !== undefined) {

                currentLists.overdue.forEach((catDepValue) => {

                    if (checkFilters(catDepValue, filter)) {
                        entry.overdue += catDepValue.value;
                    }

                });
                currentLists.onTime.forEach((catDepValue) => {

                    if (checkFilters(catDepValue, filter)) {
                        entry.onTime += catDepValue.value;
                    }

                });
            }

            result.push(entry);

        } else if (filter.explode === 'department') {

            const entry = {}

            Object.keys(departments).forEach((departmentName) => {
                if (filter.department.length === 0 || filter.department.indexOf(departments[departmentName]) !== -1) {
                    entry[departmentName] = 0;
                }
            });

            if (currentLists !== undefined) {

                filter.status.forEach((key) => {

                    const currentList = currentLists[key];
                    currentList.forEach((depCatValue) => {
                        if (checkFilters(depCatValue, filter)) {
                            entry[getDepartmentName(depCatValue.department)] += depCatValue.value;
                        }
                    });

                });

            }

            result.push(entry);

        } else if (filter.explode === 'category') {

            const entry = {};
            for (let cat = 1; cat <= 3; cat++) {
                if (filter.category.length === 0 || filter.category.indexOf(cat) !== -1) {
                    entry[cat] = 0;
                }
            }

            if (currentLists !== undefined) {

                filter.status.forEach((key) => {
                    const currentList = currentLists[key];
                    currentList.forEach((depCatValue) => {
                        if (checkFilters(depCatValue, filter)) {
                            entry[depCatValue.category] += depCatValue.value;
                        }
                    });
                });

            }

            result.push(entry);

        } else if (filter.explode === '') {
            const entry = {
                total: 0,
            };

            if (currentLists !== undefined) {

                filter.status.forEach((key) => {
                    const currentList = currentLists[key];
                    currentList.forEach((depCatValue) => {
                        if (checkFilters(depCatValue, filter)) {
                            entry.total += depCatValue.value;
                        }
                    });
                });

            }

            result.push(entry);
        }

    }

    return result;
}


PatientSolutionHandler.prototype.handleChartUpdate = function(oldChart, result) {
    let chart = oldChart;
    if (oldChart === undefined) {
        chart = c3.generate(result);
    } else {

        const chartSize = chart.data().length;
        const resultSize = result.data.columns.length;

        if (chartSize === resultSize) {
            // same size, just reload data
            chart.load(result.data);

        } else if (chartSize > resultSize) {

            // remove data
            const unloads = [];
            chart.data().forEach((dataSeries) => {

                let stay = false;
                result.data.columns.forEach((col) => {
                    if (dataSeries.id === col[0]) {
                        stay = true;
                    }
                });

                if (!stay) {
                    unloads.push(dataSeries.id);
                }

            });
            chart.unload({ ids: unloads });

        } else {

            // add new data
            chart.load(result.data);
        }
    }
    return chart;
}


/**
 * Mostly internal functions
 */
PatientSolutionHandler.prototype.parseSurgeryTypes = function() {

    const types = {};

    this.solution.schedule_surgery_types.forEach((type) => {
        types[type.clusterName] = type.urgencyCategory;
    });

    this.surgeryTypes = types;
}

PatientSolutionHandler.prototype.parseTreated = function() {

    // will produce the following structure:
    /*

        treated: {
            1: {
                onTime: [ dep/cat/value, dep/cat/value, dep/cat/value ],
                overdue: [ dep/cat/value, dep/cat/value, dep/cat/value ]
            },
            2: { .. },
            ...
            28: { .. }
        }

     */

    const surgeryTypes = this.surgeryTypes;
    this.treated = {};
    const self = this;

    this.solution.scheduleWithPatient.forEach((scheduledPatient) => {

        // parse the current entry
        const entry = {
            period: parsePeriod(scheduledPatient.week, scheduledPatient.day),
            category: surgeryTypes[scheduledPatient['treatedPatient.surgeryType']],
            overdue: scheduledPatient['treatedPatient.isOverdue'] === 'true',
            department: scheduledPatient.medicalUnitId,
            value: 1,
        }

        // prepare the current period if not already done
        if (self.treated[entry.period] === undefined) {
            self.treated[entry.period] = {
                overdue: [],
                onTime: [],
            };
        }

        // add the entry to the corresponding list (overdue / on-time)
        if (entry.overdue) {
            self.treated[entry.period].overdue = updateListWithEntry(self.treated[entry.period].overdue, entry);

        } else {
            self.treated[entry.period].onTime = updateListWithEntry(self.treated[entry.period].onTime, entry)
        }
    });


    // handle weekends, where we have no schedule data (empty arrays)
    let maxKey = 0;
    Object.keys(this.treated).forEach((period) => {
        maxKey = Math.max(maxKey, period);
    });

    maxKey += (7 - (maxKey % 7));

    for (let i = 1; i <= maxKey; i++) {
        if (this.treated[i] === undefined) {
            this.treated[i] = {
                overdue: [],
                onTime: [],
            }
        }
    }


}

PatientSolutionHandler.prototype.parseWaiting = function() {

    // will produce the following structure
    /*
     waiting: {
         1: {
            onTime: [ dep/cat/value, dep/cat/value, dep/cat/value ],
            overdue: [ dep/cat/value, dep/cat/value, dep/cat/value ]
         },
         2: { .. },
         ...
         28: { .. }
     }
     */

    this.waiting = {};
    const self = this;

    // calculate overdue patients
    this.solution.waitListOverdue.forEach((overdue) => {
        // parse the current entry
        const entry = {
            category: overdue.cat,
            period: overdue.period,
            department: overdue.unitCode,
            value: overdue.value,
        }

        // prepare the current period if not already done
        if (self.waiting[entry.period] === undefined) {
            self.waiting[entry.period] = {
                overdue: [],
                onTime: [],
            }
        }

        // add the overdue patients
        self.waiting[entry.period].overdue = updateListWithEntry(self.waiting[entry.period].overdue, entry);

    });


    // fetch the total number of waiting patients
    this.solution.waitListTotal.forEach((total) => {
        const entry = {
            period: total.period,
            category: total.cat,
            department: total.unitCode,
            value: total.value,
        }

        self.waiting[entry.period].onTime = updateListWithEntry(self.waiting[entry.period].onTime, entry);
    });


    // subtract overdues from totals to get on-time patients
    Object.keys(self.waiting).forEach((period) => {

        const periodEntries = self.waiting[period].overdue;

        periodEntries.forEach((overdueEntry) => {

            // set value by using the current value minus the overdue value to calculate on-time
            const entryCopy = {
                category: overdueEntry.category,
                department: overdueEntry.department,
                value: (-1) * overdueEntry.value,
            };


            self.waiting[period].onTime = updateListWithEntry(self.waiting[period].onTime, entryCopy);

        });

    });
}
