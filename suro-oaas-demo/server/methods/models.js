
const MODELS_API = `${SERVICE_URL}/api/models`;
Meteor.methods({
    /**
     * Loads all the available models
     * @returns {Array} the list of models returned from the API
     */
    getModels() {
        return httpCall('get', MODELS_API).data;

    },
});
