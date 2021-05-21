Surgical Unit Resource Optimization (SURO) - Parent POM Module
==============================================================

This project contains the shared settings for all the projects such as the configuration of common plugins and repositories and also acts as an __aggregator__ (according to Maven terminology). This means that this project can be used to trigger the build (and other maven specific operations) for the entire solution from this project.

As a result, the project does not contain any source code and it is packaged as a POM artifact. 

Setup
=====

This project does not require any specific setup beyond what is already mentioned for the [solution itself](../README.md).

Of those dependencies, specific to this project are:

1. [Java SE 1.7 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html): the development 
language for the solution is primarily Java. Therefore it is necessary to have installed the runtime.
2. [Apache Maven](https://maven.apache.org): the entire solution is built by using Maven, which is also use to package the 
web application for further deployment.

Moreover, some of the project dependencies and the artifacts produced are hosted in the [IBM Research - Australia Artifactory](https://mrlartifacts.sl.cloud9.ibm.com). 
Please ensure that you configure your java and Maven environments to access Artifactory as explained in the following sections:

1. [Configuration of the Java Environment](https://mrlartifacts.sl.cloud9.ibm.com/ssl.shtml)
2. [Configuration of the Maven Environment](https://mrlartifacts.sl.cloud9.ibm.com/repository.shtml)

Project Layout
==============

The project is packaged as a POM artifact and does not contain code. Therefore the only relevant information and asset is 
contained in the [POM file](pom.xml). This file contains, besides the definition of common settings for the plug-ins and
the dependencies, a list of the modules that will be managed through this aggregator. These are:

1. [suro-oaas-model](../../tree/master/suro-oaas-model/README.md): definition of data entities and components interfaces.
2. [suro-oaas-couch](../../tree/master/suro-oaas-couch/README.md): implementation of the CouchDB/Cloudant bindings.
3. [suro-oaas-mongo](../../tree/master/suro-oaas-mongo/README.md): implementation of the MongoDB/DB2 JSON bindings.
4. [suro-oaas-docloud](../../tree/master/suro-oaas-docloud/README.md): implementation of the DOCloud bindings.
6. [suro-oaas-api](../../tree/master/suro-oaas-api/README.md): implementation of the REST services.

The listing order also identifies the build order that should be followed (and that is followed by reactor when maven is 
executed under specific conditions.

Build
=====

To build this project simply issue the following maven command, which needs to be executed in the project root directory:

    $ mvn clean install
    
This process will enable the build of the all the modules in the appropriate order, so that each project can receive the
last updated dependency.

## When Things Do Not Work Out

Sometimes in specific configurations and installations of the Maven environment, it is possible that the previos command
does not completes successfully. Here are a list of errors with fixes that can be applied:

### Parent POM Issues 

It may happen that while trying to build the entire solution from the parent project the following error occurs:

```
[INFO] Scanning for projects...
....
[ERROR] [ERROR] Some problems were encountered while processing the POMs:
[WARNING] 'version' contains an expression but should be a constant. @ line 7, column 11
[FATAL] Non-resolvable parent POM for com.ibm.au.optim:suro-oaas-api:${suro-oaas.version}: Could not transfer artifact com.ibm.au.optim.suro:suro-oaas-parent:pom:0.0.x-SNAPSHOT from/to muse-repo (https://mrlartifacts.sl.cloud9.ibm.com/repo/libs-release-local): Failed to transfer file: https://mrlartifacts.sl.cloud9.ibm.com/repo/libs-release-local/com/ibm/au/optim/suro/suro-oaas-parent/0.0.2-SNAPSHOT/suro-oaas-parent-0.0.2-SNAPSHOT.pom. Return code is: 409 , ReasonPhrase:Conflict. and 'parent.relativePath' points at wrong local POM @ line 10, column 10
[WARNING] 'version' contains an expression but should be a constant. @ line 7, column 11
[FATAL] Non-resolvable parent POM for com.ibm.au.optim.suro:suro-oaas-couch:${suro-oaas.version}: Could not find artifact com.ibm.au.optim.suro:suro-oaas-parent:pom:0.0.x-SNAPSHOT and 'parent.relativePath' points at wrong local POM @ line 9, column 10
[WARNING] 'version' contains an expression but should be a constant. @ line 7, column 11
[FATAL] Non-resolvable parent POM for com.ibm.au.optim.suro:suro-oaas-docloud:${suro-oaas.version}: Could not find artifact com.ibm.au.optim.suro:suro-oaas-parent:pom:0.0.x-SNAPSHOT and 'parent.relativePath' points at wrong local POM @ line 9, column 10
[WARNING] 'version' contains an expression but should be a constant. @ line 7, column 11
[FATAL] Non-resolvable parent POM for com.ibm.au.optim.suro:suro-oaas-model:${suro-oaas-.version}: Could not find artifact com.ibm.au.optim.suro:suro-oaas-parent:pom:0.0.x-SNAPSHOT and 'parent.relativePath' points at wrong local POM @ line 9, column 10
[WARNING] 'version' contains an expression but should be a constant. @ line 7, column 11
[FATAL] Non-resolvable parent POM for com.ibm.au.optim.suro:suro-oaas-mongo:${suro-oaas.version}: Could not find artifact com.ibm.au.optim.suro:suro-oaas-parent:pom:0.0.x-SNAPSHOT and 'parent.relativePath' points at wrong local POM @ line 10, column 10
....
[ERROR] The build could not read 6 projects -> [Help 1]
....
```

In this case, the best solution is to __move into each of the child projects and issue the command__ `mvn install`. The order would be the following:

1. `suro-oaas-model`
2. `suro-oaas-core`
3. `suro-oaas-couch`
4. `suro-oaas-docloud`
5. `suro-oaas-test`
6. `suro-oaas-redmine`
7. `suro-oaas-api`

Once this process is executed once all the subsequent builds can be issues from the `suro-oaas-parent` project.


Deploy
======

This module only enables the deployment of the all artifacts produced by the sub-modules into the [IBM Research - Australia 
Artifactory Repository](https://mrlartifacts.sl.cloud9.ibm.com/). To enable the deployment, given that appropriate credentials
are configured with Maven to deploy into the repository, simply issue the command:

    $ mvn deploy
    
This command will deploy the latest built version of the module, which was previously produced by `mvn install`. If there are
no build artifact, the command will trigger a build.