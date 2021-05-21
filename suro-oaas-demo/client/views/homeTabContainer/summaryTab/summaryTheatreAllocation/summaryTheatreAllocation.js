
Template.summaryTheatreAllocation.currentChartData = new ReactiveVar([]);

Template.summaryTheatreAllocation.helpers({

    solutionAvailable() {

        if (currentSolution.get() !== undefined) {
            Template.summaryTheatreAllocation.solutionHandler = new SessionSolutionHandler(currentSolution.get());
            Template.summaryTheatreAllocation.solutionHandler.parse();
            return true;
        }

        return false;
    },


    renderTheatreAllocation() {

        const solution = currentSolution.get();
        const solutionHandler = Template.summaryTheatreAllocation.solutionHandler;


        if (solution !== undefined) {

            const columnData = {}

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

            });

            const categories = [];
            const sessions = ['Optimal Plan'];
            const baseSessions = ['Base Plan'];
            Object.keys(columnData).forEach((depName) => {
                categories.push(depName);
                sessions.push(columnData[depName].sessions);
                baseSessions.push(columnData[depName].baseSessions);
            });

            const result = {
                bindto: '#summarySessions-department-chart',
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


            if (Template.summaryTheatreAllocation.chartUpdater == null) {
                Template.summaryTheatreAllocation.chartUpdater = new ChartUpdater();
            }


            Template.summaryTheatreAllocation.chartUpdater.handleChartUpdate(result, () => {

                const chartData = Template.summaryTheatreAllocation.chartUpdater.chart.data();
                Template.summaryTheatreAllocation.currentChartData.set(chartData);
            });

        }

    },


});

Template.summaryTheatreAllocation.events({
});

Template.summaryTheatreAllocation.rendered = function() {


};
