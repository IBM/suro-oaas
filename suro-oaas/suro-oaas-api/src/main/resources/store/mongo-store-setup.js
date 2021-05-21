
// this command creates a security collection that contains
// the rules that are required to control the filtering of
// requests if needed.
db.createCollection("security");


// 1. Properties
//
// 1.1. default settings: Allow, and Forbidden on Deny.
db.security.insert({ "type": "property", "name": "default-behaviour", "value": "Deny" });
db.security.insert({ "type": "property", "name": "default-answer",    "value": 401 });

// 1.2. virtual endpoint for authentication
db.security.insert({ "type": "property", "name": "auth-login",        "value": "/auth/login" });
db.security.insert({ "type": "property", "name": "auth-logout",       "value": "/auth/logout" });

// 1.3. session expiration time
db.security.insert({ "type": "property", "name": "expire-time",       "value": 5 });
db.security.insert({ "type": "property", "name": "password-hash",     "value": "none"});

// 1.4. security for password
//
// Uncomment this if you want to have encryption of the passwords on the database 
//
// db.security.insert({"type": "property", "name" : "password-hash", "value": "SHA-256"}); 
// db.security.insert({"type": "property", "name" : "password-hash", "value": "MD5"}); 

// 1.5a. rules for endpoints: we prevent everyone that is not in the users group to access the apis.
//       These rules work when the security filter is mapped against /api/*, in this way it will not
//       intercept the calls to the web-socket endpoint.
//
db.security.insert({ "type" : "rule", "endpoint": "r:^/strategies(.*)", "verbs" : { "GET" : [ "users" ] , "POST" : [ "users" ], "PUT" : [ "users" ],  "DELETE": [ "users" ] } });

db.security.insert({ "type" : "rule", "endpoint": "r:^/datasets(.*)", "verbs" : { "GET" : [ "users" ] , "POST" : [ "users" ], "PUT" : [ "users" ],  "DELETE": [ "users" ] } });

// 1.5b. rules for endpoints and web-socket. Use this configuration if you want to map the security filter to *.
//       In this case both the connection to the endpoint and the web-socket connection upgrade will be intercepted.
//
// db.security.insert({ "type" : "rule", "endpoint": "r:^/api/strategies(.*)",  "verbs" : { "GET" : [ "users" ] , "POST" : [ "users" ], "PUT" : [ "users" ],  "DELETE" : [ "users" ] } });
// db.security.insert({ "type" : "rule", "endpoint": "r:^/ws/job-notifier", "verbs" : { "GET" : [ "users" ] , "POST" : [ "users" ], "PUT" : [ "users" ],  "DELETE": [ "users" ] } });


// 1.6 add a user that we can use to authenticate against the database.
// 
db.security.insert({ "type" : "user", "name": "demo", "password": "d3m0", "roles" : [ "users" ], "email" : "demo@ibm.com", "fullName" : "John Doe" });
