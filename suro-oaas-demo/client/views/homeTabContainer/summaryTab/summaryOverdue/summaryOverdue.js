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


Template.summaryOverdue.currentChartData = new ReactiveVar([]);


Template.summaryOverdue.helpers({
    renderPatientChart() {


        const solution = currentSolution.get();
        const solutionHandler = Template.patientsChart.solutionHandler;


        if (solution !== undefined) {

            const result = {
                data: {
                    columns: [],
                    type: 'line', // 'bar' if explode filter is set (+ grouping to get stacked bar chart)
                },
                bindto: '#summary-overduelist',
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

            if (filter.explode === 'status') {
                const statusIds = ['On Time', 'Overdue'];

                const notOverdue = [statusIds[0]];
                const overDue = [statusIds[1]];


                data.forEach((periodEntry) => {
                    notOverdue.push(periodEntry.onTime);
                    overDue.push(periodEntry.overdue);
                });
                result.data.columns.push(overDue);

            }


            if (Template.summaryOverdue.chartUpdater == null) {
                Template.summaryOverdue.chartUpdater = new ChartUpdater();
            }

            result.data.columns[0] = Template.summaryTab.weekify(result.data.columns[0]);
            Template.summaryOverdue.chartUpdater.handleChartUpdate(result, () => {
                const data2 = Template.summaryOverdue.chartUpdater.chart.data();
                Template.summaryOverdue.currentChartData.set(data2);
            });

        }

    },
});

Template.summaryOverdue.events({
});

Template.summaryOverdue.rendered = function() {

};
