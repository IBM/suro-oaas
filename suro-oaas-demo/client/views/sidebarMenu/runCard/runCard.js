import moment from 'moment'

Template.runCard.helpers({
    cardDescription() {
        try {
            if (this.description === '%DefaultPlan%') {
                return 'DefaultPlan'
            }
        } catch (e) {
            return ''
        }
        return '';

    },

    target () {
        return 100 - (Math.round(this.finalGap * 100 * 100) / 100);
    },

    calculateRuntime: (runTime) => {
        if (runTime === 0) {
            return 'tba';
        }
        return `${Math.round(parseFloat((runTime / 1000) / 60))}m`;
    },

    labelShort() {
        if (this.label === undefined) {
            return '';
        }
        const maxLength = 30;
        const label = (this.label.length > maxLength) ? `${this.label.substr(0, maxLength)}...` : this.label;
        return label;
    },


});

Template.runCard.events({

    'click li': (e) => {
        const run = runs.findOne({ id: $(e.target).closest('li[data-run-id]').attr('data-run-id') });
        if (run != null && run.status === 'COMPLETED') {

            runTitle.set(`Scenario: ${run.label}`);
            runDate.set(moment(run.startTime).format('YYYY-MM-DD'));

            // remove all selections
            $('.view-runCard > li').removeClass('activeCard');
            const tDropDownColum = $('.dropDownColumn');
            const tArrow = $('.arrowDownButton');

            if (tArrow.hasClass('rotate')) {
                tArrow.removeClass('rotate');
                tDropDownColum.removeClass('dropDownColumnActive');
            }

            // load current run
            currentRun.set(runs.findOne({ id: run.id }));

            // load current solution
            Meteor.call('loadRunSolution', run.id, (err, data) => {
                if (err) {
                    currentSolution.set();
                } else {
                    // $('#sidenav-overlay').hide();
                    currentSolution.set(data);
                    Router.go('dashboard');

                }
            });

        } else {
            sAlert.warning('Run Processing, please wait for completion');
        }
    },

});

Template.runCard.rendered = function() {

};
