#!/bin/bash

# This script reboots all the services and ensures that all of them are running
# The script is invoked after the all the new binaries have been deployed and
# and a backup of the previous state has been done.
#
# The sequence for starting the services is the following:
#
# - first Couch Db will be started
# - then WebSphere Liberty server
# - finally the Meteor application
#
# A failure in starting one of the services will prevent the next operation
# to continue, and the script will exit with an error code.
#
# The script expects the following arguments to be set:
#
# 1: WLP_ROOT              root of the installation of WebSphere Liberty server.            
# 2: WLP_SERVER_INSTANCE   name of the server instance to start up.
# 3: METEOR_ROOT		   location of the meteor application to start.

# The returns the following exit status codes:
#
# 0: everything went well.
# 1: could not start up Couch Db
# 2: could not start WebSphere Liberty
# 3: could not start Meteor application

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
WLP_COMMAND=./wlp-up.sh
COUCH_COMMAND=./couch-up.sh
METEOR_COMMAND=./meteor-up.sh

echo "Starting Couch Db Service...."

$COUCH_COMMAND
SERVICE_UP=$?

if [ $SEVICE_UP -ne 0 ]
then
    echo "Could not start Couch Db service, terminating operation.."
    EXIT_CODE=1;
else

    echo "Starting WebSphere Liberty server instance...."
    $WLP_COMMAND  $WLP_ROOT $WLP_SERVER_INSTANCE
    SERVICE_UP=$?

    if [ $SERVICE_UP -ne 0 ]
    then
        echo "Could not start WebSphere Liberty server instance, terminating operation.."
        EXIT_CODE=2
    else
        
        echo "Starting Meteor application...."
        
        $METEOR_COMMAND $METEOR_SERVICE
        SERVICE_UP=$?

        if [ $SERVICE_UP -ne 0 ]
        then
            echo "Could not start Meteor application, exiting.."
	        EXIT_CODE=3
        fi
    fi
fi


exit EXIT_CODE
