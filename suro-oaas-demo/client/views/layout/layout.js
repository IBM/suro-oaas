import overview from '/imports/overviewFunctions'

Template.layout.helpers({
    runTime () {
        return runTime.get();
    },
    gap () {
        return gap.get()
    },

    renderForms() {
    },
    currentRun() {
        return currentRun.get()
    },

    myStrategy () {
        return currentStrategy.get();
    },

})
Template.layout.events({
    'click #minWaitListCB': () => {
        const minWaitListCB = $('#minWaitListCB');
        const minOverdueCB = $('#minOverdueCB');
        if (minWaitListCB.is(':checked') || minOverdueCB.is(':checked')) {
            $('.submitMaxPerformance').removeClass('not-active')
        } else {
            $('.submitMaxPerformance').addClass('not-active')

        }
    },
    'click #minOverdueCB': () => {
        const minWaitListCB = $('#minWaitListCB');
        const minOverdueCB = $('#minOverdueCB');
        if (minWaitListCB.is(':checked') || minOverdueCB.is(':checked')) {
            $('.submitMaxPerformance').removeClass('not-active')
        } else {
            $('.submitMaxPerformance').addClass('not-active')

        }
    },


    'click #main': () => {
        // $('.button-collapse').sideNav('hide');
        const tDropDownColum = $('.dropDownColumn');
        const tArrow = $('.arrowDownButton');
        if (tArrow.hasClass('rotate')) {
            tArrow.removeClass('rotate');
            tDropDownColum.removeClass('dropDownColumnActive');
        }
    },
    'change .rangeTime': (event) => {
        runTime.set(parseInt($(event.target).val(), 10));
    },

    'change .rangeOptimality': (event) => {
        gap.set($(event.target).val());
    },


    'click .submitMaxPerformance': overview.submitMax,
    'click .defaultCard': overview.goToDefault,
    'click .modal-trigger': overview.resetModalWindow,
    'click .submitTune': overview.submitFineTune,
});
