/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

const week = {
    1: 'Monday',
    2: 'Tuesday',
    3: 'Wednesday',
    4: 'Thursday',
    5: 'Friday',
    6: 'Saturday',
    7: 'Sunday',
}
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


Template.sessionTab.currentChartData = new ReactiveVar([]);
Template.sessionTab.currentPatients = new ReactiveVar([]);

Template.sessionTab.solutionHandler = new ReactiveVar();


Template.sessionTab.currentSessionCounter = 0;

Template.sessionTab.compareWeeks = new ReactiveVar([])


Template.sessionTab.helpers({

    currentPatients() {
        return Template.sessionTab.currentPatients.get();
    },

    resolveDepartment(unitCode) {
        let result = false;
        Object.keys(departments).forEach((depName) => {
            if (unitCode === departments[depName]) {
                result = depName;
            }
        });

        return result;
    },

    departments(period) {

        const result = [];
        Object.keys(period).forEach((department) => {
            if (department !== 'period') {
                result.push(period[department]);
            }
        });

        return result;

    },


    departmentNames() {
        return Object.keys(departments);
    },

    /**
     * Will retrieve the session data necessary to render the 4 weeks schedule (how many sessions
     * per day, per department). Additionally it will create/update the chart displaying the overall
     * number of sessions per department for the entire planning period.
     * @returns {Array} the session data
     */
    sessionData() {

        const solutionHandler = Template.sessionTab.solutionHandler.get();

        const columnData = {}

        const result = [];
        Object.keys(solutionHandler.sessions).forEach((period) => {

            // retrieve entry
            const entry = solutionHandler.sessions[period];

            // deeper analysis to update the column data
            Object.keys(entry).forEach((depName) => {

                // make sure the entry exists
                if (columnData[depName] === undefined) {
                    columnData[depName] = {
                        department: depName,
                        sessions: 0,
                        baseSessions: 0,
                    }
                }

                // store number of sessions
                const currentDep = columnData[depName];
                currentDep.sessions += entry[depName].sessions;
                currentDep.baseSessions += entry[depName].baseSessions;
                columnData[depName] = currentDep;

            });


            // set period and push it to the result
            entry.period = period;
            result.push(entry);

        });


        // compile columns data for chart
        const categories = [];
        const sessions = ['Optimal Plan'];
        const baseSessions = ['Base Plan'];
        Object.keys(columnData).forEach((depName) => {
            categories.push(depName);
            sessions.push(columnData[depName].sessions);
            baseSessions.push(columnData[depName].baseSessions);
        });

        // make sure to render chart
        setTimeout(() => {
            const chartData = {
                bindto: '#sessions-department-chart',
                data: {
                    columns: [sessions, baseSessions],
                    type: 'bar',
                },
                color: {
                    pattern: ['#0c83b2', '#79D2F2'],
                },
                axis: {
                    x: {
                        type: 'category',
                        categories,
                    },
                },
                grid: {
                    y: {
                        show: true,
                    },
                    x: {
                        show: true,
                    },
                },
                bar: {
                    width: {
                        ratio: 0.5,
                    },
                },
            };
            const chart = c3.generate(chartData);
            Template.sessionTab.currentChartData.set(chart.data());
        }, 500);


        return result;

    },

    renderWeekData(selectedPeriod) {
        const startPeriod = parseInt(selectedPeriod, 10);
        const endPeriod = startPeriod + 7;

        const solutionHandler = Template.sessionTab.solutionHandler.get();
        const columnData = {}

        Object.keys(solutionHandler.sessions).forEach((period) => {
            if (startPeriod <= parseInt(period, 10) && parseInt(period, 10) < endPeriod) {
                // retrieve entry
                const entry = solutionHandler.sessions[period];

                // deeper analysis to update the column data
                Object.keys(entry).forEach((depName) => {

                    if (depName === 'period') {
                        return;
                    }

                    // make sure the entry exists
                    if (columnData[depName] === undefined) {
                        columnData[depName] = {
                            department: depName,
                            sessions: 0,
                            baseSessions: 0,
                        }
                    }

                    // store number of sessions
                    const currentDep = columnData[depName];
                    currentDep.sessions += entry[depName].sessions;
                    currentDep.baseSessions += entry[depName].baseSessions;
                    columnData[depName] = currentDep;

                });
            }
        });


        // compile columns data for chart
        const categories = [];
        const sessions = ['Optimal Plan'];
        const baseSessions = ['Base Plan'];
        Object.keys(columnData).forEach((depName) => {
            categories.push(depName);
            sessions.push(columnData[depName].sessions);
            baseSessions.push(columnData[depName].baseSessions);
        });

        setTimeout(() => {
            const chartData = {
                bindto: `#compare-sessions-week-${Math.floor(startPeriod / 7) + 1}`,
                data: {
                    columns: [sessions, baseSessions],
                    type: 'bar',
                },
                color: {
                    pattern: ['#0c83b2', '#79D2F2'],
                },
                axis: {
                    x: {
                        type: 'category',
                        categories,
                    },
                },
                grid: {
                    y: {
                        show: true,
                    },
                    x: {
                        show: true,
                    },
                },
                bar: {
                    width: {
                        ratio: 0.5,
                    },
                },
            };
            c3.generate(chartData);
        }, 500)
    },

    tableViewChartData() {
        return Template.sessionTab.currentChartData.get();
    },


    resolveWeekday(period) {

        return week[period % 7];
    },

    getWeek(period) {
        return Math.floor(period / 7) + 1;
    },

    solutionAvailable() {

        if (currentSolution.get() !== undefined) {
            // the moment the solution gets set, create the handler and parse the solution
            Template.sessionTab.solutionHandler.set(new SessionSolutionHandler(currentSolution.get()));
            Template.sessionTab.solutionHandler.get().parse();
            return true;
        }

        return false;

    },

    isBeginningOfWeek(period) {
        return period % 7 === 1;
    },

    isCompareWeek(period) {
        const compWeek = Math.floor(period / 7) + 1;
        return Template.sessionTab.compareWeeks.get().indexOf(compWeek) !== -1;
    },

});

Template.sessionTab.currentSelection = {};
Template.sessionTab.lastHoverToken = 0;


Template.sessionTab.events({

    'click div.toggle-buttons span.fa': (e) => {
        // already active
        if ($(e.target).closest('.span.fa').hasClass('active')) {
            return false;
        }

        // toggle active class
        $(e.target).closest('div.toggle-buttons').find('span.fa')
            .removeClass('active');
        $(e.target).closest('span.fa').addClass('active');

        // determine if we selected to compare
        const compare = $(e.target).closest('span.fa').hasClass('fa-bar-chart');

        // update data
        const selectedWeek = parseInt($(e.target).closest('[data-week]').attr('data-week'), 10);
        const active = Template.sessionTab.compareWeeks.get();

        if (compare === true && active.indexOf(selectedWeek) === -1) {
            active.push(selectedWeek);
        } else if (compare === false && active.indexOf(selectedWeek) !== -1) {
            active.splice(active.indexOf(selectedWeek), 1);
        }

        Template.sessionTab.compareWeeks.set(active)
        return true;
    },

    'mouseover .slot': (e) => {
        const unitCode = $(e.target).closest('[data-department-unitcode]').attr('data-department-unitcode');
        const period = $(e.target).find('span[data-period]').attr('data-period');
        const departmentName = $(e.target).closest('.slot').find('.department-name')
            .text();

        if (Template.sessionTab.currentSelection.period !== period || Template.sessionTab.currentSelection.unitCode !== unitCode) {

            // update data
            const patients = [];

            $(e.target).closest('.session-slot').find('span.hide')
                .each((index, item) => {
                    const patient = {
                        category: $(item).attr('data-category'),
                        cluster: $(item).attr('data-cluster'),
                        isOverdue: $(item).attr('data-overdue') === 0,
                        remaining: $(item).attr('data-overdue'),
                        duration: $(item).attr('data-duration'),
                        lengthOfStay: $(item).attr('data-length-of-stay'),
                        icuProbability: $(item).attr('data-icu-probability') * 100,
                        patientNumber: index + 1,
                    };
                    patients.push(patient);
                });

            $('#overlay-department-name').text(departmentName)

            $('#session-overlay').show();
            Template.sessionTab.lastHoverToken = new Date().getTime();
            Template.sessionTab.currentPatients.set(patients);

            // update current selection
            Template.sessionTab.currentSelection = {
                unitCode,
                period,
            }

        }
    },

    mouseover: (e) => {

        if (Template.sessionTab.lastHoverToken !== 0) {

            const lastToken = Template.sessionTab.lastHoverToken;
            setTimeout(() => {
                if (lastToken === Template.sessionTab.lastHoverToken) {
                    if ($(e.target).closest('.slot').length === 0 && $(e.target).closest('#session-overlay').length === 0) {
                        Template.sessionTab.lastHoverToken = 0;
                        $('#session-overlay').hide();
                        Template.sessionTab.currentPatients.set();
                        Template.sessionTab.currentSelection = {};
                    }
                }
            }, 300);

        }

    },

    'click #expand-session-data': () => {
        $('#expand-session-data span').toggle();
        $('#table-data-session-total').toggle();
    },

    'mouseover .base-row': (e) => {
        $(e.target).closest('.base-row').find('.col.base:first-child div')
            .html('Base Plan');
    },
    'mouseout .base-row': (e) => {
        $(e.target).closest('.base-row').find('.col.base:first-child div')
            .html('');
    },
});
