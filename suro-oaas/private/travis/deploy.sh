#! /usr/bin/env bash

# This script will deploy the war file and additional required file to the test server and restart the server

SOURCE_PATH="suro-oaas-api"
TARGET_PATH=/root/suro/deployments/suro-services
TARGET_BUILDS_PATH=/root/suro/builds


echo "0. Checking current branch: $TRAVIS_BRANCH"

# we care about checking whether the travis branch is
# set to something (and not differentiating between
# unset and set to empty). Then, -z $TRAVIS_BRANCH would
# do the job

if [ ! -z $TRAVIS_BRANCH ]; then
   case $TRAVIS_BRANCH in
       master)
           TARGET_HOST=suro-test.sl.cloud9.ibm.com
       ;;
       production)
	         TARGET_HOST=suro-oaas.sl.cloud9.ibm.com
       ;;
   esac
fi

if [ -z "$TARGET_HOST" ]; then
    echo '--> Branch missing or not selected for deployment.'
    exit 0
fi


echo "1. Preparing deployment $TRAVIS_BRANCH ==> $TARGET_HOST"

NOW=$(date +"%Y-%m-%d %T%z")
FINGERPRINT_DATA="{ \"date\": \"$NOW\", \"branch\": \"$TRAVIS_BRANCH\", \"commit\": \"$TRAVIS_COMMIT\" }"
FINGERPRINT_PATH=$SOURCE_PATH/server/fingerprint.json

# We adjust the $NOW variable so that we can use it in file
# names...

NOW=$(echo $NOW | tr ' :' '_-')

# This is to ensure that the private folder exists, this will
# be where we store the fingerprint of the the deployment.
#
mkdir -p private
touch $FINGERPRINT_PATH
echo $FINGERPRINT_DATA > $FINGERPRINT_PATH
echo "2. Creating deployment fingerprint: $FINGERPRINT_DATA >> $FINGERPRINT_PATH"


echo "3. Archiving the build artifacts ...."

SURO_BUILD_ARCHIVE=suro-oaas-api-${NOW}.tar.gz


tar -cvzf $SURO_BUILD_ARCHIVE $SOURCE_PATH/server/source.properties \
                              $SOURCE_PATH/server/source.init \
                              $SOURCE_PATH/server/fingerprint.json \
                              $SOURCE_PATH/server/apps/suro-oaas-api.war

echo "4. Uploading build artifacts to: $TARGET_PATH:$TARGET_BUILDS_PATH"

scp -o StrictHostKeyChecking=no $SURO_BUILD_ARCHIVE root@$TARGET_HOST:$TARGET_BUILDS_PATH

echo "5. Copying update scripts to: $TARGET_HOST:$TARGET_BUILDS_PATH"

scp -o StrictHostKeyChecking=no private/travis/remote_update.sh root@$TARGET_HOST:$TARGET_BUILDS_PATH

echo "6. Executing remote update script...."

ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "$TARGET_BUILDS_PATH/remote_update.sh $SURO_BUILD_ARCHIVE > $TARGET_BUILDS_PATH/$NOW.log"

echo "7. Retrieving remote script log file..."
scp -o StrictHostKeyChecking=no root@$TARGET_HOST:$TARGET_BUILDS_PATH/$NOW.log $NOW.log
echo "[--- $NOW.log ---]"
cat $NOW.log
echo "[----------------]"

echo "8. Deleting temporary files from: $TARGET_PATH:$TARGET_BUILDS_PATH"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST rm $TARGET_BUILDS_PATH/remote_update.sh
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST rm $TARGET_BUILDS_PATH/$SURO_BUILD_ARCHIVE
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST rm $TARGET_BUILDS_PATH/$NOW.log

echo "9. Done"


exit 0;
