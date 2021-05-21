/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
import moment from 'moment'

Template.header.helpers({
    currentNavigation() {
        return 'Predict';
    },

});

Template.header.events({
    /**
     * Click on the logout icon
     */
    'click #logout': () => {
        const tDropDownColum = $('.dropDownColumn');
        const tArrow = $('.arrowDownButton');

        if (tArrow.hasClass('rotate')) {
            tArrow.removeClass('rotate');
            tDropDownColum.removeClass('dropDownColumnActive');
        }
        Meteor.call('authtest', () => {
            Meteor.logout()
        });
        Router.go('/loginwatson');
    },
    'click #defaultPlanCard': () => {
        // alert('calling');
        const tDropDownColum = $('.dropDownColumn');
        const tArrow = $('.arrowDownButton');

        if (tArrow.hasClass('rotate')) {
            tArrow.removeClass('rotate');
            tDropDownColum.removeClass('dropDownColumnActive');
        }
        const run = runs.findOne({ description: '%DefaultPlan%' });
        if (run != null && run.status === 'COMPLETED') {
            // console.log('calling default')

            runTitle.set(run.label);
            runDate.set(moment(run.startTime).format('YYYY-MM-DD'));

            currentRun.set(runs.findOne({ id: run.id }));

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

    /**
     * Click on the home icon
     */
    'click #home-icon': () => {
        Router.go('/');
    },

    'click .header-menu': () => {
        const win = window.open('https://watson.analytics.ibmcloud.com/storybook' +
            '/dd923663-390e-4953-b747-e1a7b48effe1?_=1484630230565&loginAccountId=' +
            '3ZPDZ2KL8DE0&loginTenantId=S6XYFZIRSQ4O');
        win.focus();
    },

    /*
     * Simulating the ease effect in the header
     */
    'mouseenter a': (e) => {
        $(e.target).closest('a').css('color', '#f00');
    },
    'mouseout a': (e) => {
        $(e.target).closest('a').css('color', '#337ab7');
    },

    'click .dropDownColumn': () => {
        const tDropDownColum = $('.dropDownColumn');
        const tArrow = $('.arrowDownButton');
        if (tArrow.hasClass('rotate')) {
            tArrow.removeClass('rotate');
            tDropDownColum.removeClass('dropDownColumnActive');
        } else {
            tArrow.addClass('rotate');
            tDropDownColum.addClass('dropDownColumnActive');

        }

    },
});

Template.header.rendered = function() {
    $('.dropdown-button').dropdown({
        inDuration: 300,
        outDuration: 225,
        constrain_width: false, // Does not change width of dropdown to that of the activator
        // hover: true, // Activate on hover
        gutter: 0, // Spacing from edge
        belowOrigin: true, // Displays dropdown below the button
        alignment: 'left', // Displays dropdown with edge aligned to the left of button
    }
    );

}
