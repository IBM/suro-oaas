/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.admin.helpers({
    displayOptimality: (gap) => (100 - (gap * 100)),

    displayRuntime: (ms) => {
        let secs = Math.round(ms / 1000);
        const mins = Math.floor(secs / 60);
        secs -= (mins * 60);

        return `${mins}min ${secs}s`;
    },
});

Template.admin.events({

    'click .run .fa-trash': (e) => {
        const runId = $(e.target).closest('.run').attr('data-run-id');
        Meteor.call('deleteRun', runId, (err) => {
            if (!err) {
                sAlert.success('Run successfully deleted.');
            } else {
                sAlert.error('Failed to delete run');
                console.log(err);
            }
        });
    },
});
