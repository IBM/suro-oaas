authToken = null;


/**
 * This function is used to retrieve all the runs contained in the server (SURO) and create a shadow database locally,
 * it verifies that the run hasn't changed, if it has, it will update it accordingly
 *
 *
 */
syncRuns = () => {
    Meteor.call('getRuns', (err, data) => {
        if (!err) {
            try {
                // clean up local runs
                runs.find().forEach((localRun) => {
                    const localID = localRun.id;
                    let keepRun = false;
                    data.forEach((run) => {
                        if (run.id === localID) {
                            keepRun = true;
                            console.log(`[Run Id : ${localID} found]`);
                        }
                    });
                    if (keepRun === false) {
                        runs.remove({ _id: localRun._id });
                    }
                });


                // HERE WE START INSERTING NEW RUNS
                data.forEach((run) => {

                    // find if this run already exists in the shadow db
                    const localRun = runs.findOne({ id: run.id });
                    if (localRun != null) {

                        const localRunId = localRun._id;

                        // compare local run with run from API
                        delete localRun._id;
                        if (_.isEqual(localRun, run)) {
                            console.log(`Run [ ${localRunId} ] - present, no change`);
                        } else {

                            runs.update({ _id: localRunId }, { $set: run }, (err2, data2) => {
                                if (!err2) {
                                    console.log(`Run [ ${localRunId} ] - Run Updated`);
                                } else {
                                    console.log(err2);
                                }
                            });

                            // console.log('Run [ '+test.id+' ] - present, updated value')
                        }
                    } else {
                        runs.insert(run, (err2, data2) => {
                            console.log(`Run [ ${data2} ] - Successfully inserted`);
                        });
                    }
                });

            } catch (err2) {
                console.error('Error in getting runs', err2);
            }

        } else {
            console.error('Failed to retrieve runs', err)
        }
    });
};

syncStrategies = () => {
    console.log('Synchronising strategies');
    // get strategies from API
    Meteor.call('getStrategies', (err, data) => {

        if (!err) {
            console.log('Strategies found');
            try {
                console.log(`GET Strategies ${data.length}`);
                console.log(data);

                // stores the strategy IDs from the API
                const existingApiStrategyIds = [];

                // sync strategies from the API with the local Mongo store
                data.forEach((strategy) => {
                    // store id of api strategy
                    existingApiStrategyIds.push(strategy.id);

                    // check if the strategy exists locally
                    const localStrategy = strategies.findOne({ id: strategy.id });
                    if (localStrategy != null) {
                        // strategy exists locally
                        const localStrategyId = localStrategy._id;

                        delete test._id;
                        if (_.isEqual(test, strategy)) {
                            // no change necessary, strategy in sync
                            console.log('Strategy - present, no change')
                        } else {
                            // update local strategy with latest data
                            console.log('Strategy - present but different, updated value')
                            strategies.update({ _id: localStrategyId }, { $set: strategy });
                        }
                    } else {
                        // strategy doesn't exist yet, create it
                        strategies.insert(strategy);
                        console.log('Strategy successfully inserted');
                    }
                });

                // clean up local straegies that are no longer in the API
                strategies.find().forEach((strategy) => {
                    if (existingApiStrategyIds.indexOf(strategy.id) === -1) {
                        // local strategy cannot be found in API, delete it
                        strategies.remove({ _id: strategy._id });
                    }
                });

            } catch (err2) {
                console.error('Error in getting Strategies', err2);
            }

        } else {
            console.error(err)
        }
    });


};

syncModels = () => {

    console.log('Synchronising Models');
    // get models from the API
    Meteor.call('getModels', (err, data) => {
        if (!err) {
            try {
                console.log(`GET Models :${data.length}`);
                // array to store the model IDs in couchDB
                const existingModelIds = [];

                // iterate over models from API
                data.forEach((model) => {

                    // store existing models from API
                    existingModelIds.push(model.id);

                    // check the local model
                    const existing = models.findOne({ id: model.id })
                    if (existing != null) {
                        // remember the MongoDB document ID
                        const mongoId = existing._id;

                        // compare existing document with document delivered from the API
                        delete existing._id;
                        if (_.isEqual(existing, model)) {
                            console.log('Model - present, no change')
                        } else {
                            console.log('Model - present, updated value')
                            models.update({ _id: mongoId }, { $set: model });
                        }
                    } else {
                        // new model, just insert it
                        models.insert(model);
                        console.log('Model not present, successfully created it');
                    }
                });

                // clean-up outdated models
                models.find().forEach((model) => {
                    // check if local models also exist in API
                    if (existingModelIds.indexOf(model.id) === -1) {
                        console.log('Model no longer exists in API, deleting it locally');
                        models.remove({ _id: model._id });
                    }
                });

            } catch (err2) {
                console.error('Error in getting Models', err2);

            }
        } else {
            console.error(err)
        }
    });
};


/**
 * Startup point to manage the runs
 */
Meteor.startup(() => {

    // create and start run sync job
    SyncedCron.config({
        log: false,
    });

    // sync runs
    SyncedCron.add({
        name: 'Automatic poll for the runs',
        schedule (parser) {
            // parser is a later.parse object
            return parser.text('every 10 seconds');
        },
        job: syncRuns,
    });


    // sync strategies
    SyncedCron.add({
        name: 'Automatic poll for the strategies',
        schedule (parser) {
            // parser is a later.parse object
            return parser.text('every 30 minutes');
        },
        job: syncStrategies,
    });

    // sync model
    SyncedCron.add({
        name: 'Automatic poll for the models',
        schedule (parser) {
            // parser is a later.parse object
            return parser.text('every 30 minutes');
        },
        job: syncModels,
    });

    // execute once to sync on startup
    syncStrategies();
    syncModels();
    syncRuns();

    // start cron jobs
    SyncedCron.start();
});
