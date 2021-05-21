findInModel = function(name) {
    const parameters = currentModel.get().parameters;
    return $.grep(parameters, (e) => e.name === name);
};


Template.parameterInput.helpers({

    labelVisible() {
        if (!this.fixed) {
            return 'labelVisible';
        }
        return '';
    },

    selectable() {
        if (this.fixed) {
            return 'notSelected';
        }
        return '';
    },

    disabled() {
        if (this.fixed) {
            return 'disabled';
        }
        return '';
    },

    minmax() {
        let tItem = findInModel(this.name);
        tItem = tItem[0];
        minmax = {
            min: tItem.range[0],
            max: tItem.range[1],
        };
        // console.log(tItem);
        return minmax;
    },
    expandedTemplate() {
        // console.log(this.name);
        let tItem = findInModel(this.name);
        tItem = tItem[0];
        // console.log(tItem);
        return tItem;

    },
});

Template.parameterInput.events({
});

Template.parameterInput.rendered = function() {


};
