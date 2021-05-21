#!/bin/bash

# This script check whether the liberty server is not running and tries to start it.
# If the server is already started it will simply skip the start operation.
#
# The script takes two parameters as input:
# 
# $1: location of the WebSphere Liberty installation (without a trailing slash)
# $2: name of the server to start.
#
# Exit codes:
#
# 0: operation successful
# 1: server does not exist
# 2: error either while querying the status of the server
# 3: error while attempting to start the server
# 4: missing argument during script invocation
#
# Example usage:
#
# wlp-down.sh /opt/ibm/wlp demo
#
# where:
#
# /opt/ibm/wlp   is the locaiton of the WebSphere Liberty installation
#
# demo           is the name of the server instance to start
#


if [ "$#" -ne 2 ]
then
   EXIT_CODE=4
   exit $EXIT_CODE
fi

# Ok we're now sure that we have all that we need.
#
WLP_SERVER=$1/bin/server
WLP_SERVER_INSTANCE=$2


# executing status check for the server instance.
#
$WLP_SERVER status $WLP_SERVER_INSTANCE

WLP_STATUS=$?

EXIT_CODE=0
RUNNING=0

case $WLP_STATUS in
0)
  echo "Server $WLP_SERVER_INSTANCE is already running, no need to start it..."  
  RUNNING=1
  ;;  
1)
  echo "Server $WLP_SERVER_INSTANCE is stopped, attempting to start it..."
  ;;
2)
  echo "Server $WLP_SERVER_INSTANCE does not exist!"
  EXIT_CODE=1
  ;;
*)
  echo "Attempt to query server $WLP_SERVER_INSTANCE generated an unknown error (code: $WLP_STATUS)."
  EXIT_CODE=2
  ;;
esac

if [ $EXIT_CODE -eq 0 ]
then
   if [ $RUNNING -eq 0 ] 
   then
  
       # We fall in here if and only if there are no errors while checking the
       # the server status and the server is not running.
       #
       $WLP_SERVER start $WLP_SERVER_INSTANCE
       
       WLP_STATUS=$?
       case $WLP_STATUS in
       0)
         echo "Server $WLP_SERVER_INSTANCE succesfully started."
         ;;
       1)
         echo "Server $WLP_SERVER_INSTANCE already started?"
         ;;
       2)
         echo "Server $WLP_SERVER_INSTANCE does not exist!"
         EXIT_CODE=1
         ;;
       *)
         echo "Could not start $WLP_SERVER_INSTANCE (error code: $WLP_STATUS)."
         EXIT_CODE=3
         ;;
       esac 
   fi
fi

exit $EXIT_CODE


