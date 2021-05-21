// Pass in username, password as normal
// customSuroOptions should be passed in if you want to override SURO_DEFAULTS
// on any particular call
Meteor.loginWithSURO = function(user, password, customSuroOptions, callback) {
    // Retrieve arguments as array
    const args = [];
    for (let i = 0; i < arguments.length; i++) {
        args.push(arguments[i]);
    }
    // Pull username and password
    user = args.shift();
    password = args.shift();

    // Check if last argument is a function
    // if it is, pop it off and set callback to it
    if (typeof args[args.length - 1] === 'function') callback = args.pop(); else callback = null;

    // if args still holds options item, grab it
    if (args.length > 0) customSuroOptions = args.shift(); else customSuroOptions = {};

    // Set up loginRequest object
    const loginRequest = {
        username: user,
        suroPass: password,
        suro: true,
        suroOptions: customSuroOptions,
    };


    Accounts.callLoginMethod({
        // Call login method with suro = true
        // This will hook into our login handler for suro
        methodArguments: [loginRequest],
        userCallback: (error, result) => {
            if (error) {
                callback && callback(error);
            } else {
                callback && callback();
            }
        },
    });
};
