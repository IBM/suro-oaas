# SURO Service-Level Management Application

> This MeteorJS application is the administration application connecting to the [SURO (Surgical Unit Resource Optimization) service](https://github.ibm.com/aur/suro-oaas).
 
Other SURO components:

* [suro-oaas](https://github.ibm.com/aur/suro-oaas) - the Java component providing the data model, core logic and APIs
* [suro-oaas-demo](https://github.ibm.com/aur/suro-oaas-demo) - the MeteorJS demo application

## Running this application

To run this application, you need to:
 
* [Install MeteorJS](https://meteor.com/install)
* Have a SURO API server available (WLP / [suro-oaas](https://github.ibm.com/aur/suro-oaas)).

By default the application will use http://localhost:9080 for the API endpoints (e.g. http://localhost:9080/api/hospitals)
To override this behaviour start the application with a `SERVICE_URL` environment variable pointing to the endpoint:

```
SERVICE_URL=http://localhost:9081/suro meteor --port 4000
```

Which would result in an endpoint call to http://localhost:9081/suro/api/hospitals.

In order to see the debug output from the API calls on the server console, you can start the application with the `DEBUG` 
environment variable:

```
DEBUG=1 meteor
```
