// local development mode
SURO_DEFAULTS = {
    url: 'http://localhost:9080',
    endpoint: '/api/auth/login',
    createNewUser: true,
};

const keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

const Base64 = {

    /**
     * Encodes the given input string into its corresponding Base64 format.
     *
     * @param {String} e	the input string.
     *
     * @returns {String} the corresponding decoded string.
     */
    encode: (e) => {

        var t = '';
        var n, r, i, s, o, u, a;
        var f = 0;

        e = Base64._utf8_encode(e);

        while (f < e.length) {

            n = e.charCodeAt(f++);
            r = e.charCodeAt(f++);
            i = e.charCodeAt(f++);
            s = n >> 2;
            o = (n & 3) << 4 | r >> 4;
            u = (r & 15) << 2 | i >> 6;
            a = i & 63;

            if (isNaN(r)) {

                u = a = 64

            } else if (isNaN(i)) {

                a = 64
            }

            t = t + keyStr.charAt(s) +
            keyStr.charAt(o) +
            keyStr.charAt(u) +
            keyStr.charAt(a)
        }

        return t
    },

    /**
     * Decodes the Base64 string.
     *
     * @param {String} e	the input string.
     *
     * @return {String} the corresponding decoded string.
     */
    decode: (e) => {

        let t = '';
        let n;
        let r;
        let i;
        let s;
        let o;
        let u
        let a;
        let f = 0;

        const e2 = e.replace(/[^A-Za-z0-9\+\/\=]/g, '');

        while (f < e2.length) {

            s = keyStr.indexOf(e2.charAt(f++));
            o = keyStr.indexOf(e2.charAt(f++));
            u = keyStr.indexOf(e2.charAt(f++));
            a = keyStr.indexOf(e2.charAt(f++));

            n = s << 2 | o >> 4;
            r = (o & 15) << 4 | u >> 2;
            i = (u & 3) << 6 | a;
            t = t + String.fromCharCode(n);

            if (u !== 64) {
                t = t + String.fromCharCode(r)
            }

            if (a !== 64) {
                t = t + String.fromCharCode(i)
            }
        }

        t = Base64._utf8_decode(t);

        return t
    },

    /**
     * Encodes the input string into the corresponding Base64 form.
     *
     * @param {String} e	the input string.
     *
     * @return {String} the corresponding encoded string.
     */
    _utf8_encode(e) {

        e = e.replace(/\r\n/g, '\n');
        var t = '';

        for (var n = 0; n < e.length; n++) {

            var r = e.charCodeAt(n);
            if (r < 128) {

                t += String.fromCharCode(r)

            } else if (r > 127 && r < 2048) {

                t += String.fromCharCode(r >> 6 | 192);
                t += String.fromCharCode(r & 63 | 128)

            } else {

                t += String.fromCharCode(r >> 12 | 224);
                t += String.fromCharCode(r >> 6 & 63 | 128);
                t += String.fromCharCode(r & 63 | 128)
            }
        }
        return t
    },

    /**
     * Decodes the UTF-8 Base64 string.
     *
     * @param {String} e	the input string.
     *
     * @return {String} the corresponding decoded string.
     */
    _utf8_decode(e) {

        var t = '';
        var n = 0;
        var r = c1 = c2 = 0;

        while (n < e.length) {

            r = e.charCodeAt(n);

            if (r < 128) {

                t += String.fromCharCode(r);
                n++

            } else if (r > 191 && r < 224) {

                c2 = e.charCodeAt(n + 1);
                t += String.fromCharCode((r & 31) << 6 | c2 & 63);
                n += 2

            } else {

                c2 = e.charCodeAt(n + 1);
                c3 = e.charCodeAt(n + 2);
                t += String.fromCharCode((r & 15) << 12 | (c2 & 63) << 6 | c3 & 63);
                n += 3
            }
        }
        return t;
    },
};


/**
 @class SUROAUTH
 @constructor
 */
SUROAUTH = function(options) {
    // Set options
    this.options = _.defaults(options, SURO_DEFAULTS);

    // console.log(this.options);

    // Make sure options have been set
    try {
        check(this.options.url, String);
        check(this.options.endpoint, String);
    } catch (e) {
        throw new Meteor.Error('Bad Defaults', 'Options not set. Make sure to set SURO_DEFAULTS.url and SURO_DEFAULTS.endpoint!');
    }
};

/**
 * Attempt to authenticate against SURO API
 *
 * @method suroAuthCheck
 *
 * @param {Object} options  Object with username, password and overrides for SURO_DEFAULTS object.
 */
SUROAUTH.prototype.suroAuthCheck = function(options) {
    var self = this;
    options = options || {};
    var returnObject = {};

    if (options.hasOwnProperty('username') && options.hasOwnProperty('suroPass')) {

        // Compose the request URL
        var fullUrl = self.options.url + self.options.endpoint;
        var auth = 'Basic ' + Base64.encode(options.username + ':' + options.suroPass);
        // this authenticates vs. SURO
        var response = HTTP.get(fullUrl, {
            headers: {
                Authorization: auth,
            },
        });
        // response header contains auth token:
        if (response.statusCode == 200) {
            // console.log(response);
            returnObject = {
                username: options.username,
                authToken: response.headers.authorization,
            };
        } else {
            return {
                error: new Meteor.Error(response.statusCode, 'Auth failed.'),
            }
        }

        return returnObject;

    }
    else {
        throw new Meteor.Error(403, 'Missing SURO Auth Parameter');
    }
};


// Register login handler with Meteor
// Here we create a new SUROAUTH instance with options passed from
// Meteor.loginWithSUROAUTH on client side
// @param {Object} loginRequest will consist of username, password and suroOptions
Accounts.registerLoginHandler('suro', function(loginRequest) {
    // If "suro" isn't set in loginRequest object,
    // then this isn't the proper handler (return undefined)
    if (!loginRequest.suro) {
        return undefined;
    }

    // Instantiate SUROAUTH with options
    var userOptions = loginRequest.suroOptions || {};
    var authObj = new SUROAUTH(userOptions);

    // Call suroAuthCheck and get response
    var authResponse = authObj.suroAuthCheck(loginRequest);

    if (authResponse.error) {
        // error, return no user
        return {
            userId: null,
            error: authResponse.error,
        }

    } else {
        // Set initial userId and token vals
        var userId = null;
        var stampedToken = {
            token: null,
        };

        // Look to see if user already exists
        var user = Meteor.users.findOne({
            username: authResponse.username,
        });

        // user doesn't exist, create basic user account
        if (user === undefined && authObj.options.createNewUser === true) {
            var userObject = {
                username: authResponse.username,
            };
            Accounts.createUser(userObject);
        }

        // make sure to have the user fetched
        user = Meteor.users.findOne({
            username: authResponse.username,
        });

        // login user (set auth token)
        if (user) {
            userId = user._id;

            // Create hashed token so user stays logged in
            stampedToken = { token: authResponse.authToken };
            var hashStampedToken = Accounts._hashStampedToken({ token: authResponse.authToken });
            // Update the user's token in mongo
            Meteor.users.update(userId, {
                $push: {
                    'services.resume.loginTokens': hashStampedToken,
                },
            });
            Meteor.users.update(userId, {
                $set: {
                    'services.suroapi.authtoken': authResponse.authToken,
                },
            });
        }
        // Otherwise create user if option is set
        else if (SUROAUTH.options.createNewUser !== true) {
            // login success, but no user created
            return {
                userId: null,
                error: 'Authentication succeeded, but no user exists in MongoDB. Either create a user for this username or set SURO_DEFAULTS.createNewUser to true',
            };
        }

        return {
            userId,
            token: stampedToken.token,
        };
    }

    return undefined;
});
