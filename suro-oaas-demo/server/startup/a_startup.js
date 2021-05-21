Meteor.startup(() => {

    /** Mock up for testing the runs */

    /* if (runs.find().count() < 1) {
     runsMock.forEach(function (run) {
     runs.insert(run, function () {
     Log('Mock run inserted');
     });
     })
     }*/

    /** END MOCK UP */

    const options = {
        username: 'demo',
        suroPass: 'd3m0',
    };

    const suroAuth = new SUROAUTH({});
    const result = suroAuth.suroAuthCheck(options);
    authToken = result.authToken;
    console.log('Server logged in , token : ', authToken);

});
