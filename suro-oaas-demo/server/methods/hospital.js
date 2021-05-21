/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

const HOSPITAL_API = `${SERVICE_URL}/api/hospitals`;

Meteor.methods({

    getCurrentHospital: () => httpCall('get', HOSPITAL_API).data[0],

});
