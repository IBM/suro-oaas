#!/usr/bin/bash

# This script is used to prepare the SoftLayer Production environment for deployment.
# The script first shuts down the services that are running, it backs up the content
# of the directory and then copies the artifacts and the other components in the
# appropriate locations.
# 


# Here is where we define all the parameters for the scripts
#
#

$SOURCE=$(basename $0)
$BACKUPS=~/backups/

$WLP_SERVER=/opt/ibm/wlp-beta-2015.1.0.1
$WLP_SERVER_INSTANCE=suro

$METEOR_SERVICE=meteor-suro
$METEOR_APP_PATH=/opt/ibm/meteor


$COUCH_DB_PATH=/var/lib/couchdb/suro.couch


echo "Stopping all the running application services...."

./services-down.sh $WLP_SERVER $WLP_SERVER_INSTANCE $METEOR_SERVICE
STATUS=$?

if [ $STATUS -ne 0 ]
then
	
	echo "Could not stop all the running services, deployment failed." 
	EXIT_CODE=1
else
	
	echo "Services has been shut down, executing backup of the application."

	./backup.sh $BACKUPS $COUCH_DB_PATH $METEOR_APP_PATH $WLP_SERVER $WLP_SERVER_INSTANCE
	STATUS=$?

	if [ $STATUS -ne 0 ]
	then

		echo "Could not execute full backup of the existing application."
		EXIT_CODE=2
	else

		echo "Backup completed, deploying the new version of the application."

		./deploy.sh $SOURCE $METEOR_APP_PATH WLP_SERVER/usr/servers/$WLP_SERVER_INSTANCE
		STATUS=$?

		if [ $STATUS -ne 0 ]
		then

			echo "Could not deploy successfully the new application."
			EXIT_CODE=3

		else

			echo "Application successfully deployed, bringing services up.."
	
			./services-up.sh $WLP_SERVER $WLP_SERVER_INSTANCE $METEOR_SERVICE
			STATUS=$?

			if [ $STATUS -ne 0 ]
			then

				echo "Could not restart all the application services."
				EXIT_CODE=4
			else

				echo "All services succeffully brought up.."
			fi
		fi
	fi
fi

echo "Deployment terminated (exit code: $EXIT_CODE)."

exit $EXIT_CODE


