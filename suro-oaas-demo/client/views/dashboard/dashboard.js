
Template.dashboard.helpers({
    /*
    staticArrayHelper: function () {
        return ['a', 'b', 'c'];
    },

    staticObjectHelper: function () {
        return {
            a: 'b',
            c: 'd',
            e: [
                {
                    f: 'g',
                    h: 'i'
                },
                {
                    f: 'j',
                    h: 'k'
                }
            ]
        }
    },

    dynamicListHelper: function (parentCategory) {
        return dbCategories.find({parent: parentCategory});
    }
    */
});
Template.dashboard.events({});
Template.dashboard.rendered = function() {
    $('.button-collapse').sideNav({
        menuWidth: 400,

        edge: 'left', // Choose the horizontal origin
    });
};
