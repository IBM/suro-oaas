import moment from 'moment'

Template.homeTabContainer.helpers({

    runTitle () {
        return runTitle.get();
    },
    runDate () {

        return runDate.get();
    },

});

Template.homeTabContainer.events({
    'click .chartsLi ,.summaryLi': () => {
        $('ul.chartsTabs').tabs();
        $('ul.tabs').tabs();
        $('ul.tabsListPatients').tabs();

        const currRun = currentRun.get();

        if (currRun != null) {
            runTitle.set(`Scenario: ${currRun.label}`);
            runDate.set(moment(currRun.startTime).format('YYYY-MM-DD'));
        }

    },

    'click .summaryLi': () => {
        currentSolution.set(currentSolution.get());
    },

    'click .setupLi': () => {

        runTitle.set('Create a New Scenario');
        runDate.set(moment(Date.now()).format('YYYY-MM-DD'));

    },
    'click .buttonHome': () => {
        Router.go('/overview')
    },

});

Template.homeTabContainer.rendered = function () {

    $('ul.tabs').tabs();
};
