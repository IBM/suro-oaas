#!/bin/bash

# This script executes the backup of the current status of the application
# into a backup folder and tar-gizip all the content of the curent backup
# to reduce the space.

# The scripts takes the following parameters:
#
# 1. backup-folder		a path to the folder containing the backups.
# 2. couch-db-path		a path to the current location of the couch db database.
# 3. front-end-folder	a path to the current installation of the front-end.
# 4. back-end-folder    a path to the current WLP installation for the back-end.
# 5. server-instane 	the name of the server instance hosting the backend.

# Exit Codes:
#
# 0: Everything went well
# 1: Invalid parameters.


if [ "$#" -ne 5 ]
then
	 EXIT_CODE=1
	 exit $EXIT_CODE
fi


TODAY=$(date +"%Y-%m-%d_%H-%M-%S(%z)")
BACKUPS=$1
TARGET=$BACKUPS/$TODAY
TARGET_TAR_GZ=$TARGET.tar.gz




echo "Creating backup on $TODAY, target: $TARGET ..."

COUCH_DB_PATH=$2
WLP_SERVER_INSTANCE=$5
WLP_SERVER_DIR=$4/usr/servers/$WLP_SERVER_INSTANCE
METEOR_APP_DIR=$3

TARGET_COUCH=$TARGET/couchdb
TARGET_METEOR=$TARGET/meteor
TARGET_SERVER=$TARGET/server

echo ""
echo "1. Backing up the Couch DB database ...."

mkdir -p $TARGET_COUCH
cp $COUCH_DAB_PATH $TARGET_COUCH/

echo ""
echo "2. Backing up the server folder ...."
mkdir -p $TARGET_SERVER
cp -R $WLP_SERVER_DIR/** $TARGET_SERVER/


echo ""
echo "3. Backing up the meteor application ...."
mkdir -p $TARGET_METEOR
cp -R $METEOR_APP_DIR/** $TARGET_METEOR/

echo ""
echo "4. Compressing content to save space ...."
tar cvzf $BACKUPS/$TODAY.tar.gz $BACKUPS/$TODAY

echo ""
echo "5. Removing exploded backup folder ...."
rm -rf $BACKUPS/$TODAY


echo ""
echo "6. Backup completed."

exit 0

