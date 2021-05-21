[Redmine (Project planning / issue tracker / wiki)](https://mrltrack.au.ibm.com:10445)

* [suro-oaas-admin](https://github.ibm.com/aur/suro-oaas-admin) - MeteorJS application to manage the hospital data
* [suro-oaas-demo](https://github.ibm.com/aur/suro-oaas-demo) - MeteorJS application to demo the capabilities of the service.

Surgical Unit Resource Optimization (SURO)
==========================================

Surgical Unit Resource Optimization is a solution for helping hospitals in better utilize the resources available to them 
to plan elective surgeries. It comprises of a collection of backend services that perform the scheduling and optimization 
of surgical schedule plans and a web application that enables users to issue request for scheduling optimization and
explore the result of these optimizations.

This project contains all the source code of the components that bring this application to life.


Setup
=====

In order to have a full a deployment of the application the current tools and runtimes are required:

1. [Java SE 1.7 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html): the development 
language for the solution is primarily Java. Therefore it is necessary to have installed the runtime.
2. [Apache Maven](https://maven.apache.org): the entire solution is built by using Maven, which is also use to package the 
web application for further deployment.
3. [Node.js](https://nodejs.org) and [Node Packaging Tool (npm)](https://www.npmjs.com/). Node, npm, and 
[Grunt.js](http://gruntjs.com) are used to build and package the web front-end.
4. [CouchDB](http://couchdb.apache.org) or [Cloudant](https://cloudant.com): Cloudant or CouchDB are used for as a backing 
store for the [SURO REST Services](../../tree/master/suro-oaas-api/README.md) component.
5. [MongoDB](https://www.mongodb.org) or alternatively [DB2 with JSON extensions](http://www-01.ibm.com/software/data/db2/linux-unix-windows/downloads.html): 
Mongo or DB2 with JSON extensions is used to maintain the security settings that are used to protect the [SURO REST Services](../../tree/master/suro-oaas-api/README.md).
6. [WebSphere Liberty Profile](https://developer.ibm.com/wasdev/websphere-liberty/) or any other J2EE Servlet Container. 
The application is designed to be natively hosted in Liberty but it does not use any specific feature of the application 
server (except for the packaging and deployment). With minor efforts changes can be made to host the application onto other 
Servlet Containers.
7. [DOCloud](https://developer.ibm.com/docloud/): the optimization is executed by leveraging the cloud services of Decision 
Opimization Cloud. To use such services you need to register and obtain a key that will enable you submit jobs.

Moreover, some of the project dependencies and the artifacts produced are hosted in the [IBM Research - Australia Artifactory](https://mrlartifacts.sl.cloud9.ibm.com). 
Please ensure that you configure your java and Maven environments to access Artifactory as explained in the following sections:

1. [Configuration of the Java Environment](https://mrlartifacts.sl.cloud9.ibm.com/ssl.shtml)
2. [Configuration of the Maven Environment](https://mrlartifacts.sl.cloud9.ibm.com/repository.shtml)

These two steps are __essential__ to build the project.

__NOTE__: it is very important to have have the **JAVA_HOME** variable set in your shell. There are different ways to setup the JAVA_HOME please refer to the documentation 
that matches the operating system that is used to develop or build the project on. The absence of the **JAVA_HOME** environment variable may cause different type of errors, 
such as the inability of Maven to connect to the Artifact Repository. Here are some quick solutions that can be applied:
1. __Mac OS X__: `export JAVA_HOME=$(/usr/libexec/java_home -v <version>)` where `<version>` can be: `1.7`, `1.8`, .... The current build is verified against Java 1.7 and 1.8.
2. __Linux__: [TODO]
3. __Windows__: [TODO]


Project Layout
==============

This solution is organized as a Maven multi-module project and these are the modules:

1. [SURO - Parent POM](../../tree/master/suro-oaas-parent/README.md): this module contains the parent POM, which is also the module 
aggregator for the solution. Therefore it enables
to build consistently all the packages of the solution from the corresponding project.
2. [SURO - Data Entities](../../tree/master/suro-oaas-model/README.md): this module contains the definition of the entities and 
components that are used by the REST Services module.
3. [SURO - Core APIs](../../tree/master/suro-oaas-core/README.md): this moduels contains the implementation of the `Core` interface and
supporting components that acts as a coordination layer between the different components of the REST APIs.
3. [SURO - CouchDB Bindings](../../tree/master/suro-oaas-couch/README.md): this module contains the bindings for using implementations 
of some of the components defined in the Data Entities module by using either CouchDB and Cloudant as a backend.
4. [SURO - DOcloud Bindings](../../tree/master/suro-oaas-docloud/README.md): this module contains the bindings for using DOCloud services
for scheduling the optimization jobs and monitoring them from within the REST Services modules.
5. [SURO - REST Services](../../tree/master/suro-oaas-api/README.md): this module contains the implementation of the REST API that 
embody all the application logic, and constitutes the core of the project from a build perspective since it packages the
application that will be deployed.
6. [SURO - Redmine Bindings](../../tree/master/suro-ooas-redmine/README.md): this module contains the implementation of the `IssueManager`
interface which enables the submission of bugs and features requests in Redmine installations.
7. [SURO - Test Utilities](../../tree/master/suro-ooas-test/README.md): this module contains the implementation supporting testing across different modules.


Build
=====

To build the project please refer to the instructions that are contained in the [SURO - Parent POM](../../tree/master/suro-oaas-parent/README.md)
module. 


Deploy
======

To deploy the project plase refer to the instructions that are contained in the following modules:

1. [SURO - Parent POM](../../tree/master/suro-oaas-parent/) for general considerations about deployment.
2. [SURO - REST Services](../../tree/master/suro-oaas-api/) for deploying the REST Services or the entire application.


++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

Start of new documentation

++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

# Server (Runtime) Setup

*Also required for local development.*

## Configure DB

**CouchDB**

* Setup local CouchDB and execute the following commands (in respect to the settings for `couchdb.database` (suro), `couchdb.username` (suro) and `couchdb.password` (couchsuro):

```
curl -X PUT http://127.0.0.1:5984/suro
curl -X PUT http://127.0.0.1:5984/_config/admins/suro -d '"couchsuro"'
```

## Deploy and Configure into Websphere

* Run Java8 JRE / OpenJDK8 (Java9+ seems to have issues)
* Download the `kernel` version of Websphere: https://libertyfs.hursley.ibm.com/liberty/wasdev/downloads/wlp/8.5.5.9/ (we recommend an older version, due to our JAWS library not supporting newer Websphere)
* Extract the Webpshere kernel - choose a path like /opt/ibm/websphere
* Create a server using `<websphere>/bin/server.sh create suro` - this will create a new server folder: `<websphere>/usr/servers/suro`
* After building (`mvn install` */suro-oaas-parent*) and bundling (`mvn -P bundle install` */suro-oaas-api*) the project ( ) copy the content of the `/suro-oaas-api/server` folder into your `<websphere>/usr/servers/suro` folder
* Create a server.env file providing the following environment specific configuration parameters:

```
PARAMETER_SOURCE_STRATEGY=com.ibm.au.jaws.web.core.runtime.impl.CompositeParameterSourceResolutionStrategy
ENVIRONMENT_PARAMETER_SOURCE_MAPPINGS=.=__

couchdb__url=http://localhost:5984
couchdb__username=
couchdb__password=
couchdb__database=suro

oaas__authentication__token=<your DOCLoud API token>
```

The first 2 lines should remain as they are as the rest of the setup depends on it.
The `couchdb__` parameters should be adapted to match the local security settings and database name.
The `oaas__authentication__token` needs to be specified if you're planning to execute optimisation jobs and needs to be a valid API key for DOCloud.

* Create or overwrite the `<websphere>/usr/servers/suro/source.init` with the following content:

```
com.ibm.au.vizanalytics.web.core.runtime.impl.cloudfoundry.CloudFoundryServiceParameterSource boolean:false
com.ibm.au.vizanalytics.web.core.runtime.impl.EnvironmentParameterSource
com.ibm.au.vizanalytics.web.core.runtime.impl.PropertiesParameterSource
com.ibm.au.vizanalytics.web.core.runtime.impl.ServletContextParameterSource $ctx
```

* Install the following features:

```
bin/installUtility install localConnector-1.0
bin/installUtility install websocket-1.1
bin/installUtility install cdi-1.0
bin/installUtility install jsp-2.2
bin/installUtility install jaxrs-1.1
bin/installUtility install jndi-1.0
```

* Make sure the file `<websphere>/usr/servers/suro/server.xml` contains the following features and add the following application:

```
<featureManager>
    <feature>localConnector-1.0</feature>
    <feature>websocket-1.1</feature>
    <feature>cdi-1.0</feature>
    <feature>jsp-2.2</feature>
    <feature>jaxrs-1.1</feature>
    <feature>jndi-1.0</feature>
</featureManager>

<application id="suro" location="${server.config.dir}/apps/suro-oaas-api.war" context-root="/" name="suro" type="war" />
```

In the same file, you can also adjust the port used by the installation:

```
<httpEndpoint httpPort="9080" httpsPort="9443" id="defaultHttpEndpoint" />
````

**Start the Server**

To start the server execute

```
<websphere>/bin/server.sh start suro
``` 

This will make the API accessible via `http://<hostname>:9080/api` (unless configured otherwise). The default credentials are:

* Username: **demo**
* Password: **d3m0**

The logs for the installation can be found in: `<websphere>/usr/servers/suro/logs`. Older logs (older than 1 day) can be found in the `./archive` sub-folder.

## Deploy and Configure using BlueMix

* Setup Cloudant Service
* Create new app using Java Liberty Profile as runtime.
* Bind the Cloudant service to your new app
* Setup the following environment parameters:

| Name | Value |
| ------- | ------ |
| `CF_MAPPINGS` | `{"couchdb":"<cloudant-service-name>"}` |
| `ENVIRONMENT_PARAMETER_SOURCE_MAPPINGS` | `.=__` |
| `couchdb__database` | `<cloudant-database-name>` |
| `oaas__authentication__token` |  `<your API token>` |
| `LOG_PROFILE` | `BLUEMIX` |

Deploy the app using CloudFoundry CLI after successful build and bundle:

```
<source-root>/suro-oaas-api/target/cf push @TODO: complete command
```

# Local Dev Environment Setup

## Setup Environment

**Make sure you have JAVA_HOME configured**

MacOS: add to `~/.bash_profile`:

```
export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)
```

**Configure Global MVN Servers**

- SURO requires dependencies from [IBM NA Artifactory](https://na.artifactory.swg-devops.com)
- Create if not exists: `~/.m2/settings.xml` with the following server/credential list:

```xml
<settings>
  <servers>
    <server>
      <id>arl-repo-release</id>
      <username>your@email.com</username>
      <password>yourArtifactoryKey</password>
      <configuration></configuration>
    </server>
    <server>
      <id>arl-repo-snapshot</id>
      <username>your@email.com</username>
      <password>yourArtifactoryKey</password>
      <configuration></configuration>
    </server>
    <server>
      <id>arl-thirdparty-release</id>
      <username>your@email.com</username>
      <password>yourArtifactoryKey</password>
      <configuration></configuration>
    </server>
    <server>
      <id>arl-thirdparty-snapshot</id>
      <username>your@email.com</username>
      <password>yourArtifactoryKey</password>
      <configuration></configuration>
    </server>
  </servers>
</settings>
```

## Build the Project

To manually invoke the build process execute:

1. `<source-root>/suro-oaas-parent/mvn clean install`
2. `<source-root>/suro-oaas-api/mvn -P bundle install`

The output is a folder `<source-root/suro-oaas-api/server` with the updated `source.properties` and updated `apps/suro-oaas-api.war` (the app bundle)

There is an automated build script available, which performs both tasks sequentially:

```
<source-root>/build.sh
```

The script allows for one parameter to specify where to copy the output (the new `apps/suro-oaas-api.war` file and the `source.properties` are copied). 
If your server is installed at `/opt/ibm/websphere/wlp/usr/servers/suro/`, then the build script can automatically re-deploy to the local server by executing:

```
<source-root>/build.sh /opt/ibm/websphere/wlp/usr/servers/suro
```

This will overwrite the existing `source.properties` and `app/suro-oaas-api.war` in the target server.
If the path is too long and inconvenient to handle, create a symbolic link (http://www.computerhope.com/unix/uln.htm).
