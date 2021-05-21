
findInModel = function(name) {
    if (currentModel.get() == null) {
        return false;
    }

    const parameters = currentModel.get().parameters;
    return $.grep(parameters, (e) => e.name === name);

};
Template.runParameter.helpers({
    expandedTemplate() {
        let tItem = findInModel(this.name);
        if (tItem === false || tItem == null) {
            return '';
        }

        tItem = tItem[0];
        return tItem;
    },
});

Template.runParameter.events({
});

Template.runParameter.rendered = function() {

};
