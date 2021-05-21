#!/usr/bin/bash

# This script is used to prepare the SoftLayer Production environment for deployment.
# The script first shuts down the services that are running, it backs up the content
# of the directory and then copies the artifacts and the other components in the
# appropriate locations.
# 

./services-down.sh
./backup.sh
./deploy.sh
./services-up.sh


