/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

Template.loginwatson.events({

    /**
     * Click on the login button
     */
    'click .goToSuro': () => {
        if (validateForm('#login-form')) {
            // const user = $('#username').val();
            // const pass = $('#password').val();
            const user = 'demo';
            const pass = 'd3m0';

            Meteor.loginWithSURO(user, pass, (err, data) => {
                if (err) {
                    console.log('Login failed');
                    console.log(err);
                    // login failed
                } else {
                    console.log(data);
                    console.log(Meteor.user().services);
                    // login successful (router handles redirect to /home route)
                }
            });
        }
    },
    'click .goToWatson': () => {
        const win = window.open('https://watson.analytics.ibmcloud.com/storybook' +
            '/dd923663-390e-4953-b747-e1a7b48effe1?_=1484630230565&loginAccountId=' +
            '3ZPDZ2KL8DE0&loginTenantId=S6XYFZIRSQ4O');
        win.focus();
    },
});

Template.loginwatson.rendered = function () {
  // $('.modal').leanModal();
};
