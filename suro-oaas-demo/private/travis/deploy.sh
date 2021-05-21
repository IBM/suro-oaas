#!/usr/bin/env bash

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

echo " 1. Preparing deployment $TRAVIS_BRANCH ==> $TARGET_HOST"

NOW=$(date +"%Y-%m-%d %T%z")
FINGERPRINT_DATA="{ \"date\": \"$NOW\", \"branch\": \"$TRAVIS_BRANCH\", \"commit\": \"$TRAVIS_COMMIT\" }"
FINGERPRINT_PATH=private/fingerprint.json

# This is to ensure that the private folder exists, this will
# be where we store the fingerprint of the the deployment.
#
mkdir -p private
touch $FINGERPRINT_PATH
echo $FINGERPRINT_DATA > $FINGERPRINT_PATH
echo " 2. Creating deployment fingerprint: $FINGERPRINT_DATA >> $FINGERPRINT_PATH"


echo " 3. Removing unneeded directories [.git, .meteor/local, node_modules]"
rm -rf .git
rm -rf .meteor/local
rm -rf node_modules


# These variables contains the information about the location 
# of the host where to deploy the application, the local path,
# and additional information about the local deployment.

TARGET_PATH=/root/suro/deployments/suro-demo-app
TARGET_BUILDS=/root/suro/builds
TARGET_PACKAGE=demo-ui.tar
TARGET_SERVICE=suro-demo

echo "4. Deploy to target environment: $TARGET_HOST"
# MeteorJS Demo Application

echo "5. Create Tarball $TARGET_PACKAGE and copy to remote location: $TARGET_BUILDS"
tar --exclude=*.tar* -cf $TARGET_PACKAGE .
ls -la
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "mkdir -p $TARGET_BUILDS"
scp -o StrictHostKeyChecking=no $TARGET_PACKAGE root@$TARGET_HOST:$TARGET_BUILDS/$TARGET_PACKAGE

echo "6. Stop application service: $TARGET_SERVICE"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "stop $TARGET_SERVICE"

echo "7. Removing existing application code from: $TARGET_PATH"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "mkdir -p $TARGET_PATH"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "cd $TARGET_PATH && rm -rf *"

echo "8. Extracting the $TARGET_PACKAGE to: $TARGET_PATH"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "cd $TARGET_BUILDS && tar -xvf $TARGET_PACKAGE -C $TARGET_PATH"

echo "9. Copying .meteor/[packages|release|versions] to: $TARGET_PATH/.meteor"
scp -o StrictHostKeyChecking=no .meteor/packages root@$TARGET_HOST:$TARGET_PATH/.meteor/
scp -o StrictHostKeyChecking=no .meteor/release root@$TARGET_HOST:$TARGET_PATH/.meteor/
scp -o StrictHostKeyChecking=no .meteor/versions root@$TARGET_HOST:$TARGET_PATH/.meteor/

echo "10. Executing npm install in: $TARGET_PATH"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "cd $TARGET_PATH && meteor npm install"

echo "11. Starting application service: $TARGET_SERVICE"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "start $TARGET_SERVICE"

echo "12. Removing temporary package $TARGET_PACKAGE from: $TARGET_BUILDS"
ssh -o StrictHostKeyChecking=no root@$TARGET_HOST "cd $TARGET_BUILDS && rm $TARGET_PACKAGE"

echo "13. Completed"

exit 0
