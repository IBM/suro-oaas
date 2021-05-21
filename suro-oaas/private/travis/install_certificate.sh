#!/bin/bash

ARTIFACTORY_CERTIFICATE_ALIAS=mrlartifacts
ARTIFACTORY_CERTIFICATE=mrlartifacts.crt
ARTIFACTORY_CERTIFICATE_URL=https://mrlartifacts.sl.cloud9.ibm.com/downloads/$ARTIFACTORY_CERTIFICATE

JAVA_KEYSTORE=$JAVA_HOME/jre/lib/security/cacerts

curl -kLO $ARTIFACTORY_CERTIFICATE_URL
sudo keytool -import -trustcacerts -noprompt -alias $ARTIFACTORY_CERTIFICATE_ALIAS -file $ARTIFACTORY_CERTIFICATE -keystore $JAVA_KEYSTORE -storepass changeit



