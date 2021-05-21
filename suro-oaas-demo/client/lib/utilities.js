/**
 * This function retrieves a specific run, first it will look into the cache, which is a local collection,
 * if it finds it, it will return it, otherwise it will make a request for it to the rest api
 * @param {String} runId run unique identifier
 */
getRun = function(runId) {
    console.log('Getting run :', runId);
    const tRun = CachedRuns.findOne({ id: runId });
    if (tRun === undefined) {
        console.log('No entry found');
        Meteor.call('getRun', runId, (err, resp) => {
            if (!err) {
                console.log(resp);
                currentRun.set(resp);
            }

        });
    } else {
        console.log('Entry Found');
        currentRun.set(tRun);
    }
};

showToast = function(message) {
    Materialize.toast(message, 4000); // 4000 is the duration of the toast

};


patientsChartFilter = function() {

};

arrayAverage = function(array) {
    let sum = 0;
    for (let i = 0; i < array.length; i++) {
        sum += parseInt(array[i], 10); // don't forget to add the base
    }

    const avg = sum / array.length;
    return parseInt(avg, 10);

}
