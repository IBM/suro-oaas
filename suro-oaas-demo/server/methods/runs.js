/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 * Methods providing Websphere access to runs.
 */

const RUN_API = `${SERVICE_URL}/api/runs`;

Meteor.methods({

    getLastRun() {
        return httpCall('get', `${RUN_API}/last`).data;
    },

    /**
     * Loads all available runs of the currently selected strategy.
     * @returns {Array} a list of runs
     */
    getRuns () {

        console.log('getRuns called');
        return httpCall('get', RUN_API).data;
    },

    /**
     * Loads the data of the requested run.
     * @param {String} runId - the id of the run
     * @returns {Object} the requested run
     */
    getRun (runId) {

        return httpCall('get', `${RUN_API}/${runId}`).data;
    },

    /**
     * submits a new run with the specified data for the selected strategy.
     * @param {Object} runData - the parameters for the run
     * @returns {Object} the new run object
     */
    submitRun (runData) {
        console.log(runData);
        return httpCall('post', RUN_API, runData).data;
    },

    /**
     * Attempts to abort the run specified by the provided ID in DOCloud
     * @param {String} runId - the run id
     */
    abortRun (runId) {

    },

    /**
     * Deletes the run with the provided ID from DOCloud
     * @param {String} runId - the run id
     * @returns {Boolean} true if the operation was successful.
     */
    deleteRun (runId) {
        const serviceRun = runs.findOne(runId);
        if (serviceRun !== undefined) {
            const response = httpCall('del', `${RUN_API}/${serviceRun.id}`);
            if (response.statusCode === 200) {
                runs.remove(runId);
                return true;
            }

            throw new Error(`Unexpected response code ${response.statusCode}`);
        } else {
            throw new Error('Could not find the specified run.');
        }
    },

    /**
     * Deletes the run with the provided ID from DOCloud
     * @param {String} runId - the run id
     */
    deleteLocalRun (runId) {
        console.log('[Deleting run] id : ', runId);
        runs.remove({ id: runId }, (err, result) => {
            if (!err) {
                console.log('[Run Deleted from Local Storage] : ', result);
            } else {
                console.log('[Error] :', err);
            }
        })

    },


    loadRunSolution(runId) {
        try {
            return httpCall('get', `${RUN_API}/${runId}/solution`).data;

        } catch (err) {
            console.log('Error retrieving solution', err);
            return 0;
        }
    },
});


