#! /usr/bin/env bash


# This function checks whether a given service (passed as a first)
# parameter is running. The function assumes that the service is
# managed with upstart and it uses the status function to check Its
# value, which is then piped through grep to identify the 'start'
# string.
#
function service_running()
{
  status $1 | grep -q "$1 start"
  return $?
}

# This function checks whether a given service (passed as a first)
# parameter is stopped. The function assumes that the service is
# managed with upstart and it uses the status function to check Its
# value, which is then piped through grep to identify the 'stop'
# string.
#
function service_stopped()
{
  status $1 | grep -q "$1 stop"
  return $?
}

# This function checks whether a given service is running and if that
# is the case, it attempts to stop the service. The function returns
# a value that indicates whether the service needs to be restarted (1)
# or not (0). This depends on whether the service was running or not.
#
function check_and_stop_service()
{
  local RESTART=0
  service_running $1
  local RUNNING=$?


  if [ $RUNNING -eq 0 ]; then

    echo "    - $1 is running, attempting to stop it ..."

    stop $1
    echo "    - $1 stopped, will be restarted."

    RESTART=1

  else

    echo "    - $1 is not running, will not be restarted."

  fi

  return $RESTART
}

# This function is a modified version of check_and_stop_service to stop the
# WebSphere Liberty server. Even though the server is managed through the
# upstart service, the invocation of 'stop' and 'start' hangs rather than
# returning.
#
# The function directly controls the server instance via the 'server' command
# and does require two additional parameters to perform its function:
#
# $1 pointer to the server root
# $2 server instance name to control
# $3 service name
#
function  check_and_stop_api
{
  local SERVER_ROOT=$1
  local SERVER_NAME=$2
  local SERVICE=$3

  $SERVER_ROOT/bin/server status $SERVER_NAME | grep -q "is running"
  local RUNNING=$?
  local RESTART=0

  if [ $RUNNING -eq 0 ]; then

    echo "    - $3 is running, attempting to stop it ..."

    stop $3
    echo "    - $3 stopped, will be restarted."

    RESTART=1

  else

    echo "    - $3 is not running, will not be restarted."

  fi

  return $RESTART
}

# This function is a modified version of check_and_restart_service to start
# the WebSphere Liberty server. Even though the server is managed through the
# upstart service, the invocation of 'stop' and 'start' hangs rather than
# returning.
#
# The function directly controls the server instance via the 'server' command
# and does require two additional parameters to perform its function:
#
# $1 pointer to the server root
# $2 server instance name to control
# $3 condition (restart or not)
# $4 service name.
#
function check_and_restart_api
{
  local SERVER_ROOT=$1
  local SERVER_NAME=$2
  local CONDITION=$3
  local SERVICE=$4

  if [ $3 ]; then

    echo "    - $4 was running, will be restarted..."
    $SERVER_ROOT/bin/server start $SERVER_NAME
    echo "    - $4 restarted."

  else

    echo "    - $4 was not running, will not be restarted."

  fi

}

# This function checks whether a service needs to be restarted and if so
# it attempts to restart it. The function expects two parameters: the first
# is an integer that indicates whether the service needs to be started (1)
# or not (0), and the second is the identifier of the service.
#
# The function assumes that the service is managed with upstart, and uses
# the start function to statrt the service.
#
function check_and_restart_service()
{
  local CONDITION=$1
  local SERVICE=$2

  if [ $1 ]; then

    echo "    - $2 was running, will be restarted..."
    start $2
    echo "    - $2 restarted."

  else

    echo "    - $2 was not running, will not be restarted."

  fi

}


# We expect the script to receive the specific name of
# the archive to unpack.
#
SURO_ARCHIVE=$1

# Convenience variables for managing more cleanly the script. The
# first is the pointer to a temporary directory that stores the build
# artifacts: this is where we will unpack the artifact and copy the
# updated the files from. The fourth one is a pointer to the target folder
# where the application is installed. More precisely, the server instance
# that is used to run the application: this is where we will copy the
# files to.
#
SURO_BUILDS_PATH=/root/suro/builds/
SURO_SERVER_NAME=suro
SURO_SERVER_ROOT=/root/suro/deployments/suro-services
SURO_TARGET_PATH=$SURO_SERVER_ROOT/usr/servers/$SURO_SERVER_NAME


# Upstart services identifiers.
#

# API service, this is the one we need to update with the
# new deployment.
#
SURO_API_SERVICE=suro-services

# Dependent services, these are the ones that require the APIs
# to be up and running. Therefore, if they're running we need
# to stop them and restart them once everything has been updated.
#
SURO_DEMO_SERVICE=suro-demo
SURO_ADMIN_SERVICE=suro-admin


echo "0. Updating $SURO_API_SERVICE with $SURO_ARCHIVE ..."

echo "1. Checking the status of dependent services ..."
check_and_stop_service $SURO_DEMO_SERVICE
RESTART_SURO_DEMO=$?
check_and_stop_service $SURO_ADMIN_SERVICE
RESTART_SURO_ADMIN=$?

echo "2. Checking the status of $SURO_API_SERVICE ..."
check_and_stop_api $SURO_SERVER_ROOT $SURO_SERVER_NAME $SURO_API_SERVICE
RESTART_SURO_API=$?

echo "3. Unpacking the build artifact to: $SURO_BUILDS_PATH ..."

cd $SURO_BUILDS_PATH
tar -xpvzf $SURO_ARCHIVE

echo "4. Copying the files to the server directory: $SURO_TARGET_PATH ..."

cp -R suro-oaas-api/server/source.properties $SURO_TARGET_PATH/source.properties
cp -R suro-oaas-api/server/source.init $SURO_TARGET_PATH/source.init
cp -R suro-oaas-api/server/fingerprint.json $SURO_TARGET_PATH/fingerprint.json
cp -R suro-oaas-api/server/apps/suro-oaas-api.war $SURO_TARGET_PATH/apps/suro-oaas-api.war

echo "5. Deleting temporary files ...."

rm -rf suro-oaas-api
rm $SURO_ARCHIVE

echo "6. Restarting $SURO_API_SERVICE ..."
check_and_restart_api $SURO_SERVER_ROOT $SURO_SERVER_NAME $RESTART_SURO_API $SURO_API_SERVICE

echo "7. Restarting dependent services ..."
check_and_restart_service $RESTART_SURO_ADMIN $SURO_ADMIN_SERVICE
check_and_restart_service $RESTART_SURO_DEMO $SURO_DEMO_SERVICE

echo "8. Update completed ..."
