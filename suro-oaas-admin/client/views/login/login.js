/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.login.events({
    'click #submit-button': () => {

        const username = $('#username').val();
        const password = $('#password').val();

        Meteor.loginWithSURO(username, password, (err, data) => {
            console.log(err);
            console.log(data);
            if (err) {
                sAlert.error('Login failed');
            } else {

                Router.go('/');
            }
        });
    },
});
