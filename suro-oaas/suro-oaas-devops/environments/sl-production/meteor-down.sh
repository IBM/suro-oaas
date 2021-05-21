#!/bin/bash
#
# This script tries to start the meteor application in background. 
#
# The script requires a parameter as argument, which is the location of the
# application to start. It will then move into that directory (if it exists)
# and start the meteor application.
#
# Usage:
#
# ./meteor-up.sh meteor-service-name
#
# Exit Codes:
#
#  0: everything went well, the application has been found and stopped 
#  1: application could not be stopped
#  2: the script has been invoked with no parameters
#

pushd $(pwd)

EXIT_CODE=0

if [ "$#" -ne 1 ] 
then
   EXIT_CODE=2
   exit $EXIT_CODE
fi

METEOR_SERVICE=$1
METEOR_STATUS_FILE=
UPSTART_COMMAND=initctl

if [ -f $METEOR_STATUS_FILE ]
then 
    rm -f $METEOR_STATUS_FILE
fi

$UPSTART_COMMAND status $METEOR_SERVICE > $METEOR_STATUS_FILE

# we need to figure out what is in the file
# the content of upstart is the following:
#
# service-name goal/status[, process ID]
#
STATUS=$(<$METEOR_STATUS_FILE)
if [ $STATUS ~= "*running*" ] 
then
    echo "Application $METEOR_SERVICE is running, attempting to stop it..."

    rm $METEOR_STATUS_FILE
    
    $UPSTART_COMMAND stop $METEOR_SERVICE > $METEOR_STATUS_FILE
    STATUS=$(<$METEOR_STATUS_FILE)

    if [ $STATUS ~= "*waiting*" ]
    then

        echo "Application $METEOR_SERVICE stopped successfully."
    
    else

        EXIT_CODE=1
        echo "Application $METEOR_SERVICE has not been stopped (status: $STATUS)."
    fi

else
    echo "Application $METEOR_SERVICE is not running."
fi


exit EXIT_CODE

