import overview from '/imports/overviewFunctions'


Template.overview.solutionHandler = new ReactiveVar();

Template.overview.helpers({
    resourcesWards() {
        if (currentHospital.get() !== undefined) {
            let wardCount = 0;
            const currWards = currentHospital.get().wards;
            currWards.forEach((ward) => {
                wardCount += ward.bedsCount
            })
            return wardCount;
        }
        return '';
    },
    resourcesTeathres() {
        if (currentHospital.get() !== undefined) {
            return currentHospital.get().theatreCount

        }
        return '';
    },
    resourcesICUbeds() {
        if (currentHospital.get() !== undefined) {
            return currentHospital.get().icuBedCount

        }
        return '';
    },

    totalWaitlist() {
        mySolution = currentSolution.get();
        if (mySolution !== undefined) {
            waitList = _patientsOnWatingFirstDay(mySolution);
            return waitList;
        }
        return 'Empty'
    },
    overdueWaitList() {
        mySolution = currentSolution.get();
        if (mySolution !== undefined) {
            waitList = _patientsOverdueFirstDay(mySolution);
            return waitList;
        }
        return 'Empty'

    },
    renderBasePlanSessionChart: overview.renderBasePlanSessionChart,


});

Template.overview.events({
});

Template.overview.rendered = function() {
    $('.modal-trigger').leanModal();

    currentRun.set(runs.findOne({ description: '%DefaultPlan%' }))
    const tStrategy = strategies.findOne({ label: 'Flexible (free form)' });
    currentStrategy.set(tStrategy);
    currentModel.set(models.findOne());
    const run = currentRun.get();

    Meteor.call('loadRunSolution', run.id, (err, data) => {
        if (err) {
            currentSolution.set();
        } else {
            currentSolution.set(data);
            const handler = new SessionSolutionHandler(data);
            handler.parse();
            Template.overview.solutionHandler.set(handler);
        }
    });
};
