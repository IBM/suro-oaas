// BrowserPolicy.content.allowImageOrigin("*");
/**
 * Main Router File. This file contains all your routes. If you have too many routes or very complex routes, you should
 * split up your routes and group them by functionality into separate files (e.g. mainRoutes.js, adminRoutes.js or
 * apiRoutes.js)
 */

/**
 * Specify the layout template for all pages. You can always override the layout template inside a route (e.g. for the
 * login screen, you might not want to use the standard layout.
 */
Router.configure({
    // refers to a <template name="layout">
    layoutTemplate: 'layout',
    // refers to a <template name="loading">
    loadingTemplate: 'loading',
});


/**
 * Before any route is executed, execute this function. this.next() needs to be executed to continue the execution of
 * the actual route.
 */
Router.onBeforeAction(function() {
    // This is a client only task which simply makes the main div invisible so that it can be
    // faded in smoothly in afterAction
    if (Meteor.isClient) {
        $('#main').css('display', 'none');
        $('#main').removeClass('animated fadeIn');
    }
    this.next();
});

/**
 * Once the route is executed, execute this function. This executes the task to animate the transition
 */
Router.onAfterAction(() => {
    if (Meteor.isClient) {
        $('#main').show();
        $('#main').addClass('animated fadeIn');
    }

});

// Router.onAfterAction(fadeContentIn());


/**
 * The default route. Displays the dashboard / home template and subscribes to a few collections/documents.
 *
 * By using waitOn you can add subscriptions to the waiting list. The action function will not be executed before all
 * the subscriptions are loaded.
 * subscriptions are ready yet.
 */
Router.route('/', {
    waitOn() {
        return [
            Meteor.subscribe('runs'),
            Meteor.subscribe('strategies'),
            Meteor.subscribe('models'),
        ]
    },
    action() {

        if (Meteor.userId()) {
            // logged in
            this.render('overview');
        } else {
            // not logged in
            Router.go('/loginwatson');
        }
    },
    data() {

    },
});

Router.route('/overview', {
    waitOn() {
        return [
            Meteor.subscribe('runs'),
            Meteor.subscribe('strategies'),
            Meteor.subscribe('models'),
        ]
    },
    action() {
        // reset chart updaters
        Template.summaryAllWait.chartUpdater = null;
        Template.summaryOverdue.chartUpdater = null;
        Template.summaryTheatreAllocation.chartUpdater = null;
        Template.bedsTab.chartUpdater = null;
        Template.patientsChart.chartUpdater = null;

        this.render('overview');
    },
    data() {

    },
});

Router.route('/dashboard', {
    waitOn() {
        return [
            Meteor.subscribe('runs'),
            Meteor.subscribe('strategies'),
            Meteor.subscribe('models'),
        ]
    },
    action() {
        this.render('dashboard');
    },
    data() {

    },
});


Router.route('/admin', {
    waitOn() {
        return [
            Meteor.subscribe('runs'),
        ];
    },
    action() {
        if (Meteor.userId()) {
            // logged in
            this.render('admin');
        } else {
            Router.go('/loginwatson');
        }
    },
    data() {
        return {
            runs: runs.find({}, { sort: {
                startTime: -1,
            } }),
        };
    },
})


/**
 * Login route, showing a login form.
 */
Router.route('/login', {
    action() {
        if (Meteor.userId()) {
            Router.go('/');
        } else {
            this.render('login');
        }
    },
});


/**
 * Login route, showing a login form.
 */
Router.route('/loginwatson', {
    action() {
        if (Meteor.userId()) {
            Router.go('/');
        } else {
            this.render('loginwatson');
        }
    },
});


