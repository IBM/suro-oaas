#! /usr/bin/env bash

# This script will change directories, build the application and deploy the war file to the provided location

cd suro-oaas-parent

#-DtrimStackTrace=false
mvn -Dhttps.protocols=TLSv1.2 clean install
DEPLOY_STATUS=$?

if [ $DEPLOY_STATUS -eq 0 ]; then
    cd ../suro-oaas-api
    mvn -Dhttps.protocols=TLSv1.2 -P bundle install
    BUNDLE_STATUS=$?

    if [ $BUNDLE_STATUS -eq 0 ]; then
        if [ -z "$1" ]; then
            echo "Target directory not specified. Not copying war file."
        else
            echo "Copy war file into target server"
            cp server/apps/suro-oaas-api.war $1
            cp server/source.properties $1/../
            cp server/source.init $1/../
        fi
    else
        echo "mvn bundle failed."
    fi
else
    echo "mvn install failed."
fi

