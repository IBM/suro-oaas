/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.bedsTab.currentChartData = new ReactiveVar([]);
Template.bedsTab.solutionHandler = new ReactiveVar();
Template.bedsTab.helpers({


    solutionAvailable() {

        if (currentSolution.get() !== undefined) {
            // the moment the solution gets set, create the handler and parse the solution
            Template.bedsTab.solutionHandler.set(new BedSolutionHandler(currentSolution.get()));
            Template.bedsTab.solutionHandler.get().parse();
            return true;
        }

        return false;
    },


    /**
     * Retrieves the list of departments from the currently loaded hospital.
     * @returns {*} - a list of departments
     */
    hospitalDepartments () {
        const hospital = currentHospital.get();
        if (hospital !== undefined) {
            return hospital.departments;
        }

        return [];
    },


    groupByIs(compareValue) {
        const currentFilter = currentBedsFilter.get();
        return currentFilter.view === compareValue;
    },


    renderBedsChart() {

        const solutionHandler = Template.bedsTab.solutionHandler.get();
        const currentFilter = currentBedsFilter.get();

        // init chart definition
        let result;

        /*
         Compile Base Data for bed type view and department view
         */
        if (currentFilter.view === 'beds') {
            // load data from solution handler
            const beds = solutionHandler.beds;
            const icus = solutionHandler.icus;


            const bedColumn = ['Utilised Ward Beds'];
            const icuColumn = ['Utilised ICU Beds'];
            for (let period = 1; period <= 28; period++) {
                let currentBed = 0;
                let currentIcu = 0;
                if (beds[period] !== undefined) {
                    Object.keys(beds[period]).forEach((departmentName) => {
                        if (currentFilter.departments.length === 0 || currentFilter.departments.indexOf(departmentName) !== -1) {
                            currentBed += beds[period][departmentName];
                            if (icus[period] !== undefined && icus[period][departmentName] !== undefined) {
                                currentIcu += icus[period][departmentName];
                            }
                        }
                    });
                }
                bedColumn.push(currentBed);
                icuColumn.push(currentIcu);
            }

            const colData = [];
            if (currentFilter.bedTypes.indexOf('beds') !== -1) {
                colData.push(bedColumn);
            }
            if (currentFilter.bedTypes.indexOf('icus') !== -1) {
                colData.push(icuColumn);
            }

            if (Template.bedsTab.chartUpdater == null) {
                Template.bedsTab.chartUpdater = new ChartUpdater();
            }

            result = {
                data: {
                    columns: colData,
                    type: 'bar',
                },
                bindto: '#beds-chart',
                grid: {
                    y: {
                        show: true,
                    },
                    x: {
                        show: true,
                    },
                },
            }
        } else {
            // / view = 'departments'

            const departments = Template.bedsTab.solutionHandler.get().getDepartments();

            let columnData = [];
            Object.keys(departments).forEach((departmentName) => {
                if (currentFilter.departments.length === 0 || currentFilter.departments.indexOf(departmentName) !== -1) {
                    columnData.push([departmentName]);
                }
            });

            const bedResources = (currentFilter.bedTypes.indexOf('beds') === -1) ? solutionHandler.icus : solutionHandler.beds;

            Object.keys(bedResources).forEach((period) => {

                const currentDepartmentList = bedResources[period];
                const newColumnData = [];

                columnData.forEach((depData) => {
                    if (currentDepartmentList[depData[0]] !== undefined) {
                        depData.push(currentDepartmentList[depData[0]]);
                    }
                    newColumnData.push(depData);
                });

                columnData = newColumnData;
            });


            result = {
                data: {
                    columns: columnData,
                    type: 'line',
                },
                bindto: '#beds-chart',
                grid: {
                    y: {
                        show: true,
                    },
                    x: {
                        show: true,
                    },
                },
            }
        }


        // handle collapsing
        if (currentFilter.collapse) {
            const collapsedColumn = ['Aggregated'];
            result.data.columns.forEach((oldColumn) => {
                for (let i = 1; i < oldColumn.length; i++) {
                    if (collapsedColumn[i] === undefined) {
                        collapsedColumn[i] = oldColumn[i];
                    } else {
                        collapsedColumn[i] += oldColumn[i];
                    }
                }
            });

            result.data.columns = [collapsedColumn];
        }


        // handle poorly rounded values
        const newSeries = [];
        result.data.columns.forEach((series) => {

            for (let i = 1; i < series.length; i++) {
                if (Math.abs(Math.round(series[i]) - series[i]) < 0.01) {
                    // only round if the difference between the rounded value and the actual value is negligible enough
                    series[i] = Math.round(series[i]);
                } else {
                    series[i] = Math.round(series[i] * 100) / 100;
                }
            }

            newSeries.push(series);
        });
        result.data.columns = newSeries;


        // handle total available ward beds
        let availability = {};
        const avail = ['Available'];
        if (currentFilter.bedTypes.indexOf('beds') !== -1) {
            availability = solutionHandler.availableBeds;
        } else {
            availability = solutionHandler.availableIcus;
        }

        if (Object.keys(availability).length > 0) {

            Object.keys(availability).forEach((period) => {
                avail.push(availability[period]);
            });

            result.data.columns.push(avail);
            result.data.types = {
                Available: 'line',
            }
        }


        // handle weekly aggregate
        if (currentFilter.aggregate === 'week') {

            const columnData = result.data.columns;
            const newColumnData = [];
            columnData.forEach((col) => {

                const currentColumn = [col[0]];
                let weekSum = 0;
                for (let index = 1; index < col.length; index++) {

                    if ((index - 1) % 7 === 0 && index !== 1) {
                        currentColumn.push(weekSum);
                        weekSum = 0
                    }

                    weekSum += col[index];
                }

                // push final week
                currentColumn.push(weekSum);
                // add to new columns
                newColumnData.push(currentColumn);
            });

            result.data.columns = newColumnData;

        }

        if (Template.bedsTab.chartUpdater != null) {
            Template.bedsTab.chartUpdater.handleChartUpdate(result, () => {
                Template.bedsTab.currentChartData.set(Template.bedsTab.chartUpdater.chart.data());
            });
        }

    },


    weekView() {
        if (Template.bedsTab.currentChartData.get().length > 0 && Template.bedsTab.currentChartData.get()[0].values.length === 4) {
            return true;
        }
        return false;
    },

    weekdaysOnly() {
        if (Template.bedsTab.currentChartData.get().length > 0 && Template.bedsTab.currentChartData.get()[0].values.length === 20) {
            return true;
        }
        return false;
    },

    weekday() {
        return [
            { short: 'Mon' },
            { short: 'Tue' },
            { short: 'Wed' },
            { short: 'Thu' },
            { short: 'Fri' },
        ]
    },

    day() {
        return [
            { short: 'Mon' },
            { short: 'Tue' },
            { short: 'Wed' },
            { short: 'Thu' },
            { short: 'Fri' },
            { short: 'Sat' },
            { short: 'Sun' },
        ]
    },


    tableViewChartData() {

        return Template.bedsTab.currentChartData.get();
    },

});

Template.bedsTab.events({

    'change input[data-beds]': () => {
        const newList = [];
        $('input[data-beds]:checked').each((index, item) => {
            newList.push($(item).attr('data-beds'));
        });

        const filter = currentBedsFilter.get();
        filter.bedTypes = newList;
        currentBedsFilter.set(filter);
    },

    'change #collapse-beds': () => {

        const filter = currentBedsFilter.get();
        filter.collapse = $('#collapse-beds').is(':checked');
        Template.bedsTab.chartUpdater.resetChart();
        currentBedsFilter.set(filter);

    },


    'change input[type="radio"][name="beds-time"]': () => {
        const newValue = $('input[type="radio"][name="beds-time"]:checked').attr('data-time');
        const filter = currentBedsFilter.get();
        filter.aggregate = newValue;
        Template.bedsTab.chartUpdater.resetChart();
        currentBedsFilter.set(filter);
    },

    'change input[type="radio"][name="bedGroupExplode"]': () => {
        // get data
        const explode = $('input[type="radio"][name="bedGroupExplode"]:checked').attr('data-beds-explode');
        const filter = currentBedsFilter.get();

        // handle chart reset in case we change the explode
        if (filter.view !== explode && Template.bedsTab.chartUpdater !== undefined) {
            Template.bedsTab.chartUpdater.resetChart();
        }

        // set the new view
        filter.view = explode;

        // update the filter
        currentBedsFilter.set(filter);
    },


    'change input[type="checkbox"][data-filter="beds-department"]': (e) => {
        const depList = [];
        $('input[type="checkbox"][data-filter="beds-department"]:checked').each((index, item) => {
            depList.push($(item).attr('data-department-name'));
        });

        const filter = currentBedsFilter.get();
        filter.departments = depList;
        currentBedsFilter.set(filter);
    },

    'click #expand-beds-data': () => {
        $('#expand-beds-data span').toggle();
        $('#beds-data').toggle();
    },
});

Template.bedsTab.rendered = function () {

    // init all dropdowns
    $('.beds-dropdown-trigger').dropdown({
        inDuration: 300,
        outDuration: 225,
        constrain_width: true, // Does not change width of dropdown to that of the activator
        hover: true, // Activate on hover
        gutter: 0, // Spacing from edge
        belowOrigin: true, // Displays dropdown below the button
        alignment: 'left', // Displays dropdown with edge aligned to the left of button
    });

}
