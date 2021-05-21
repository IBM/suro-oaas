
const STRATEGIES_API = `${SERVICE_URL}/api/templates`;
Meteor.methods({

    /**
     * Loads all available strategies.
     * @returns {Array} a list of templates
     */
    getStrategies() {
        return httpCall('get', STRATEGIES_API).data;
    },

});
