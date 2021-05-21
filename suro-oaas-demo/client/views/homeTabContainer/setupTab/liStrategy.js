findInModel = function(name) {
    const model = currentModel.get();
    if (model != null) {
        const parameters = currentModel.get().parameters;
        return $.grep(parameters, (e) => e.name === name);
    }

    return null;
};


Template.liStrategy.helpers({

    expandedLi () {
        let tItem = findInModel(this.name);
        tItem = tItem[0];
        // console.log(tItem);
        return tItem;

    },
});
