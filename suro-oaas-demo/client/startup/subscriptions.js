Meteor.startup(() => {
    Meteor.subscribe('runs');
    Meteor.subscribe('strategies');
    Meteor.subscribe('models');
});
