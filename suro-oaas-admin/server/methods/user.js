/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Meteor.methods({
    logoutSuro: () => {
        Meteor.users.update({ _id: Meteor.user()._id }, { $unset: { 'services.suroapi': 1 } });
    },
});
