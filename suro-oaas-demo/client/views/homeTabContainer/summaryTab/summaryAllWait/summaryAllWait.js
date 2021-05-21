const filter = {
    view: 'waitlist',
    status: [{ 0: 'overdue' }, { 1: 'onTime' }],
    department: [
        'Cardiac Surgery',
        'Cardiology',
        'Dental Unit',
        'Gastroenterology',
        'General Surgery',
        'Neurosurgery',
        'Ophthalmology',
        'Orthopaedic - General',
        'Otolaryngology',
        'Plastic Surgery',
        'Urology',
    ],
    category: [
        1, 2, 3,
    ],
    explode: 'status',
    collapse: false,
}


Template.summaryAllWait.currentChartData = new ReactiveVar([]);
Template.summaryAllWait.chartWidth = null;
Template.summaryAllWait.helpers({

    renderPatientChart() {

        const solution = currentSolution.get();
        const solutionHandler = Template.patientsChart.solutionHandler;

        if (solution !== undefined) {

            const result = {
                data: {
                    columns: [],
                    type: 'line',
                },
                bindto: '#summary-waitlist',
                grid: {
                    y: {
                        show: true,
                    },
                    x: {
                        show: true,
                    },
                },
                size: {
                    width: Template.summaryAllWait.chartWidth,
                },
            };

            const data = solutionHandler.getPatientData(filter);

            if (filter.explode === 'status') {
                const statusIds = ['On Time', 'Overdue', 'All Lists'];
                const totalWaitingList = [statusIds[2]]

                data.forEach((periodEntry) => {
                    totalWaitingList.push(periodEntry.onTime + periodEntry.overdue)
                });
                result.data.columns.push(totalWaitingList);

            }


            if (Template.summaryAllWait.chartUpdater == null) {
                Template.summaryAllWait.chartUpdater = new ChartUpdater();
            }

            result.data.columns[0] = Template.summaryTab.weekify(result.data.columns[0]);

            Template.summaryAllWait.chartUpdater.handleChartUpdate(result, () => {
                const chartData = Template.summaryAllWait.chartUpdater.chart.data();
                Template.summaryAllWait.currentChartData.set(chartData);
            });

        }

    },
});

Template.summaryAllWait.events({});

Template.summaryAllWait.rendered = function() {
    Template.summaryAllWait.chartWidth = document.getElementById('summary-waitlist').clientWidth;
};
