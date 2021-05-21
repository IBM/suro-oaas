/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 * @param {String} method - the request method (e.g. get, post, put in lower case)
 * @param {String} url - the full request url
 * @param {Object} data - the optional data payload for the request
 * @returns {Object} the response object of the http request
 */
httpCall = function (method, url, data) {
    let result = null;
    console.log(Meteor.user().services.suroapi.authtoken);
    const headers = { Authorization: Meteor.user().services.suroapi.authtoken };

    if (process.env.DEBUG !== undefined) {
        console.log(`${method.toUpperCase()}: ${url}`);
    }

    if (data !== undefined && process.env.DEBUG !== undefined) {
        console.log('DATA:');
        console.log(data);
    }


    // processing
    if (method === 'get') {
        result = HTTP.get(url, {
            headers,
        });
    } else if (method === 'post') {
        if (data === undefined) {
            result = HTTP.post(url, { headers });
        } else {
            result = HTTP.post(url, {
                data,
                headers,
            });
        }
    } else if (method === 'put') {
        if (data === undefined) {
            result = HTTP.put(url, { headers });
        } else {
            result = HTTP.put(url, {
                data,
                headers,
            });
        }
    } else if (method === 'del') {
        if (data === undefined) {
            result = HTTP.del(url, { headers });
        } else {
            result = HTTP.del(url, {
                headers,
                data,
            });
        }

    }

    if (process.env.DEBUG !== undefined) {
        console.log('Result');
        console.log(Object.keys(result.data));
        console.log('END--------->');
    }

    return result;
};
