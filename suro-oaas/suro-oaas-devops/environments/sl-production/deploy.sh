#!/bin/bash
#
# This script deploys the new copies of the application
# software into the production environment. The script
# does the following:
#
# 1. it deletes the content of the meteor application folder
#    and copies the new content.
# 2. it copies the conteont of the server folder into the
#    corresponding websphere liberty installation.
#
# The scripts expects the following parameters:
#
#  ./deploy.sh source frontend backend
#
# Where:
#
# source     represents the source folder for the build
#            artifacts to be deployed.
#
# frontend   represents the target directory path for the
#            application front-end code.
#
# backend    represents the target directory path for the
#            application back-end code.
#
# Exit codes:
#
# 0: everything went ok and the application binaries, sources
#    and configuration files have been deployed.
#
# 1: the application front end could not be deployed.
#
# 2: the application binaries and configuration settings for the
#    back-end could not be deployed successfully.
#
# 3: the script has been invoked with invalid parameters.
#

if [ "$#" -ne 3 ]
then
	EXIT_CODE=3
	exit $EXIT_CODE
fi

SOURCE_PATH=$1
FRONTEND_PATH=$2
BACKEND_PATH=$3

EXIT_CODE=0


