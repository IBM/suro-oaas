const categoryMapping = {
    'Category 1': 1,
    'Category 2': 2,
    'Category 3': 3,
}


Template.patientsChart.currentChartData = new ReactiveVar([]);

Template.patientsChart.helpers({


    /**
     * Handles the chart rendering and update.
     * For every update, only ONE operation should be defined. If you perform multiple load() and unload()
     * at the same time, you will run into timing issues and get wrong results.
     */
    renderPatientChart () {


        const solution = currentSolution.get();
        const filter = currentPatientFilter.get();

        const solutionHandler = Template.patientsChart.solutionHandler;

        if (solution !== undefined) {

            const result = {
                data: {
                    columns: [],
                    type: 'line', // 'bar' if explode filter is set (+ grouping to get stacked bar chart)
                },
                bindto: '#patient-chart',
                axis: {
                    y: {
                        min: 0,
                        padding: {
                            bottom: 5,
                        },
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
            };

            const data = solutionHandler.getPatientData(filter);
            const columnData = {};

            if (filter.explode === 'status') {
                const statusIds = ['On Time', 'Overdue'];

                const notOverdue = [statusIds[0]];
                const overDue = [statusIds[1]];

                data.forEach((periodEntry) => {

                    notOverdue.push(periodEntry.onTime);
                    overDue.push(periodEntry.overdue);

                });

                // push the different lines
                if (filter.status.indexOf('overdue') !== -1 || filter.status.length === 0) {
                    result.data.columns.push(overDue);
                }
                if (filter.status.indexOf('onTime') !== -1 || filter.status.length === 0) {
                    result.data.columns.push(notOverdue);
                }
            } else if (filter.explode === 'department') {

                data.forEach((periodEntry) => {
                    Object.keys(periodEntry).forEach((departmentName) => {
                        if (columnData[departmentName] === undefined) {
                            columnData[departmentName] = [departmentName];
                        }

                        columnData[departmentName].push(periodEntry[departmentName]);
                    });
                });

                Object.keys(columnData).forEach((department) => {
                    result.data.columns.push(columnData[department]);
                });


            } else if (filter.explode === 'category') {
                data.forEach((periodEntry) => {
                    Object.keys(periodEntry).forEach((cat) => {
                        let categoryName = '';
                        Object.keys(categoryMapping).forEach((catName) => {
                            if (parseInt(cat, 10) === categoryMapping[catName]) {
                                categoryName = catName;
                            }
                        });
                        if (columnData[categoryName] === undefined) {
                            columnData[categoryName] = [categoryName];
                        }

                        columnData[categoryName].push(periodEntry[cat]);
                    });
                });

                Object.keys(columnData).forEach((cat) => {
                    result.data.columns.push(columnData[cat]);
                });

            } else if (filter.explode === '') {
                const collapsedColumn = ['Aggregated'];
                data.forEach((periodEntry) => {
                    collapsedColumn.push(periodEntry.total);
                });
                result.data.columns = [collapsedColumn];
            }


            if (Template.patientsChart.chartUpdater == null) {
                Template.patientsChart.chartUpdater = new ChartUpdater();
            }

            Template.patientsChart.chartUpdater.handleChartUpdate(result, () => {
                Template.patientsChart.currentChartData.set(Template.patientsChart.chartUpdater.chart.data());
            });

        }

    },


    solutionAvailable() {

        if (currentSolution.get() !== undefined) {
            // the moment the solution gets set, create the handler and parse the solution
            Template.patientsChart.solutionHandler = new PatientSolutionHandler(currentSolution.get());
            Template.patientsChart.solutionHandler.parse();
            return true;
        }
        return false;

    },

    weekdaysOnly() {
        if (Template.patientsChart.currentChartData.get().length > 0 && Template.patientsChart.currentChartData.get()[0].values.length === 20) {
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

        return Template.patientsChart.currentChartData.get();
    },
});

Template.patientsChart.events({

    'click #expand-patient-data': () => {
        $('#expand-patient-data span').toggle();
        $('#patient-data').toggle();
    },

});

Template.patientsChart.rendered = function () {

};
