Template.setupTab.helpers({
    myStrategies () {
        currentModel.set(models.findOne());
        return strategies.find({}, { sort: { description: 1 } });
    },

    showSetup () {
        if (currentStrategy.get() === null || currentStrategy.get() === undefined || currentStrategy.get() === '') {
            Template.instance().currentStrategyText.set('Strategies');
            return false;
        }

        return true;
    },


    tabTitle () {
        return Template.instance().currentStrategyText.get();
    },

});

Template.setupTab.events({

    'click #strategyDropdown li': (event, template) => {
        const currentTab = $(event.target).closest('li');
        const title = $(event.target).closest('li');

        template.currentStrategyText.set(title.data('title'));
        const tStrategy = strategies.findOne({ id: currentTab.data('template') });
        currentStrategy.set(tStrategy);
        showSetup.set(true);
        $('#newrRunLabel').val('');
        $('#labelRun').removeClass('active');
        $('#strategyDropdown').css('display', 'none');
    },
});

Template.setupTab.rendered = function () {


    $('.strategyDropdown').dropdown({
        inDuration: 300,
        outDuration: 225,
        constrain_width: true, // Does not change width of dropdown to that of the activator
        hover: true, // Activate on hover
        gutter: 0, // Spacing from edge
        belowOrigin: true, // Displays dropdown below the button
        alignment: 'left', // Displays dropdown with edge aligned to the left of button
    }
    );


    /*
     * Setting up the model as the default one, this is acceptable for now as we only have one model,
     * it will need to be modified if at some stage we decide to have a different set of models.
     */
    currentModel.set(models.findOne());

};

Template.setupTab.onCreated(function () {
    currentModel.set(models.findOne());
    // this.strategyTab = new ReactiveVar("strategyTab");
    this.currentStrategyText = new ReactiveVar('Strategies')
});
