Template.sidebarMenu.helpers({
    showDefault() {
        if (runs.find({ desciption: '%DefaultPlan%' })) {
            return true
        }
        return false;
    },
    showProcessing () {
        if (runs.find({ status: { $ne: 'COMPLETED' } }, { sort: { startTime: -1 } }).count() > 0) {
            return true
        }
        return false
    },
    showCompleted () {
        if (runs.find({ status: 'COMPLETED' }, { sort: { startTime: -1 } }).count() > 0) {
            return true
        }
        return false
    },

    myRunsCompleted () {
        // return the current runs , sorting newer to older (newer being on top)
        return runs.find({ status: 'COMPLETED' }, { sort: { startTime: -1 } });
       // console.log('runs',tRuns);


    },

    myRunsProcessing () {
        // return the current runs , sorting newer to older (newer being on top)
        return runs.find({ status: { $ne: 'COMPLETED' } }, { sort: { startTime: -1 } });
    },
});

Template.sidebarMenu.events({
});

Template.sidebarMenu.rendered = function() {


};
