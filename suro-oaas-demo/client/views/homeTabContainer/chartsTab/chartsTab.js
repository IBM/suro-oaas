Template.chartsTab.helpers({});

Template.chartsTab.events({
    'click ul.chartsTab .indicator': () => {
        $('ul.chartsTab .indicator:last-child').addClass('clicked');
    },
});

Template.chartsTab.rendered = function () {
    $('ul.chartsTabs').tabs();
};
