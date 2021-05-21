/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

Template.header.events({});

Template.header.helpers({});

Template.header.events({
    /**
     * Click on the logout icon
     */
    'click #logout': () => {
        Meteor.call('logoutSuro', () => {
            Meteor.logout();
        });
    },


    /**
     * Click on the home icon
     */
    'click #home-icon': () => {
        Router.go('/');
    },

});
