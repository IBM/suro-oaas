/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.dashboard.events({
    'click .section-title': (e) => {
        $(e.target).closest('.card').find('.section-body')
            .toggle('slide');
        $(e.target).closest('.section-title').toggleClass('active');
    },


    'click .edit-button': (e) => {
        $(e.target).closest('.card').find('.toggle')
            .toggle();
        $(e.target).closest('.card').find('input:not(.disabled),select:not(.disabled)')
            .prop('disabled', false);
        $(e.target).closest('.card').find('.section-title')
            .addClass('edit');
    },


    // save meta data
    'click #save-meta': (e) => {
        const currentHospital = Template.dashboard.currentHospital.get();
        const newData = {
            name: $('#meta-name').val(),
            theatreCount: $('#meta-theatres').val(),
            icuBedCount: $('#meta-icu-beds').val(),
            theatreSessionsPerDay: $('#meta-daily-sessions').val(),
            sessionDuration: $('#meta-session-duration').val(),
            regionId: currentHospital.regionId,
        };

        Meteor.call('updateHospitalMeta', currentHospital.id, newData, (err, data) => {
            if (!err) {
                Template.dashboard.currentHospital.set(data);
                sAlert.success('Hospital meta data updated');
                $(e.target).closest('.card').find('.toggle')
                    .toggle();
                $(e.target).closest('.card').find('input:not(.disabled),select:not(.disabled)')
                    .prop('disabled', true);
            }
        });

    },

});


Template.dashboard.currentHospital = new ReactiveVar();

Template.dashboard.helpers({

    hospital: () => {
        if (Template.dashboard.currentHospital.get() === undefined) {
            Meteor.call('getHospital', (err, data) => {
                if (!err) {
                    Template.dashboard.currentHospital.set(data);
                }
            });
        }

        return Template.dashboard.currentHospital.get();
    },

});
