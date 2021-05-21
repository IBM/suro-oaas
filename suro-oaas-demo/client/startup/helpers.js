
/* Handlebars.registerHelper('state', function() {
    return 'alessio';
});*/

/**
 *  This is a global helper, meaning that it can be called from anywhere from any template,
 *  we use it in the layout to command the navbar toggle.
 *  When at any stage the Session variable 'showHeader' changes from true to false, this becomes
 *  triggers and the navbar is shown/hidden
 */
Handlebars.registerHelper('navBar', () => Meteor.user());

Handlebars.registerHelper('footBar', () => Session.get('showFooter'));

Handlebars.registerHelper('lostConnection', () => Session.get('lostConnection'));
