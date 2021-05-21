#!/bin/bash 
#
# This script is designated to control the Couch DB service installed
# in the machine and managing the database supporting the application.
#
#
#
# Exit codes:
#
# 0: the Couch DB service has been successfully started
# 1: the Couch DB service does not exist
# 2: the Couch DB service returned an error when started
# 3: the script has been invoked with invalid parameters
#

EXIT_CODE=0

exit $EXIT_CODE