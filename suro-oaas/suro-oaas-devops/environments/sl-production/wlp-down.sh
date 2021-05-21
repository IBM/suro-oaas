#!/bin/bash

# This script check whether the liberty server is running and tries to stop it.
# If the server is already stopped it will simply skip the stop operation.
#
# The script takes two parameters as input:
# 
# $1: location of the WebSphere Liberty installation (without a trailing slash)
# $2: name of the server to stop.
#
# Exit codes:
#
# 0: operation successful
# 1: server does not exist
# 2: error either while querying the status of the server
# 3: error while attempting to stop the server
# 4: missing argument during script invocation
#
# Example usage:
#
# wlp-down.sh /opt/ibm/wlp demo
#
# where:
#
# /opt/ibm/wlp   is the location of the WebSphere Liberty installation
#
# demo           is the name of the server instance to stop
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
RUNNING=1

case $WLP_STATUS in
0)
  echo "Server $WLP_SERVER_INSTANCE is running, attempting to shut it down..."  
  ;;  
1)
  echo "Server $WLP_SERVER_INSTANCE is stopped, will not attempt stopping it..."
  RUNNING=0
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
   if [ $RUNNING -eq 1 ] 
   then
  
       # We fall in here if and only if there are no errors while checking the
       # the server status and the server is running.
       #
       $WLP_SERVER stop $WLP_SERVER_INSTANCE
       
       WLP_STATUS=$?
       case $WLP_STATUS in
       0)
         echo "Server $WLP_SERVER_INSTANCE succesfully stopped."
         ;;
       1)
         echo "Server $WLP_SERVER_INSTANCE already stopped?"
         ;;
       2)
         echo "Server $WLP_SERVER_INSTANCE does not exist!"
         EXIT_CODE=1
         ;;
       *)
         echo "Could not stop $WLP_SERVER_INSTANCE (error code: $WLP_STATUS)."
         EXIT_CODE=3
         ;;
       esac 
   fi
fi

exit $EXIT_CODE

     	
