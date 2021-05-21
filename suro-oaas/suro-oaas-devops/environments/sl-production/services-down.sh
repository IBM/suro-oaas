#!/bin/bash

# This script stops all the services and ensures that all of them are not running
# The script is invoked before performing any deployment to the environment in 
# order to ensure that there is no potential of leaving the system in an inconsistent
# state.
#
# The sequence for stopping the services is the following:
#
# - first the Meteor application will be stopped
# - then WebSphere Liberty server
# - finally the CouchDb server
#
# A failure in stopping one of the services will prevent the next operation
# to continue, and the script will exit with an error code.
#
# The script expects the following arguments to be passed
#
# 1: WLP_ROOT              root of the installation of WebSphere Liberty server.            
# 2: WLP_SERVER_INSTANCE   name of the server instance to stop.
# 3: METEOR_SERVICE		   name of the upstart service controlling the Meteor app.

# The returns the following exit status codes:
#
# 0: everything went well.
# 1: could not stop the Meteor application
# 2: could not stop WebSphere Liberty
# 3: could not stop Couch Db

EXIT_CODE=0

if [ "$#" -ne 3 ]
then
    EXIT_CODE=4
    exit $EXIT_CODE
fi


$WLP_ROOT=$1
$WLP_SERVER_INSTANCE=2
$METEOR_SERVICE=$3

# Here are the scripts that we will use in this phase.
#
WLP_COMMAND=./wlp-down.sh
COUCH_COMMAND=./couch-down.sh
METEOR_COMMAND=./meteor-down.sh

echo "Stopping Meteor application...."

$METEOR_COMMAND $METEOR_SERVICE
SERVICE_UP=$?

if [ $SEVICE_UP -ne 0 ]
then
    echo "Could not stop Meteor application, terminating operation.."
    EXIT_CODE=1;
else
    $WLP_COMMAND  $WLP_ROOT $WLP_SERVER_INSTANCE
    SERVICE_UP=$?

    echo "Stopping WebSphere Liberty server instance..."
    if [ $SERVICE_UP -ne 0 ]
    then
        echo "Could not stop WebSphere Liberty server instance, terminating operation.."
        EXIT_CODE=2
    else

        echo "Stopping Couch Db Service...."
        $COUCH_COMMAND
        SERVICE_UP=$?

        if [ $SERVICE_UP -ne 0 ]
        then
            echo "Could not stop Couch Db service, exiting.."
	        EXIT_CODE=3
        fi
    fi
fi


exit EXIT_CODE
