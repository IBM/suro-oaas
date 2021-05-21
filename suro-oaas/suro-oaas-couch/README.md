Surgical Unit Resource Optimization (SURO) - CouchDB Bindings Module
====================================================================

This project contains the implementation of the components defined in the [SURO - Data Entities](../../tree/master/suro-oaas-model/README.md) module that represents repositories and the corresponding entities managed by those repositories. Therefore, this component enables the [SURO - REST Services](../../tree/master/suro-oaas-api/README.md) module to use a [CouchDB](http://couchdb.apache.org) or [Cloudant](https://cloudant.com) database as a storage for the entities managed by the web APIs.


Setup and Requirements
======================

This project is based on Java and Maven and therefore requires the following:

1. [Java SE 1.7 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
2. [Apache Maven](https://maven.apache.org).

Additionally, some configuration for Maven is required to interact with the [IBM Research - Australia Artifact Repository](https://mrlartifacts.sl.cloud9.ibm.com). Please ensure that your Java and Maven environment is properly set up according to the related documentation:

1. [Configuration of the Java Environment](https://mrlartifacts.sl.cloud9.ibm.com/ssl.shtml)
2. [Configuration of the Maven Environment](https://mrlartifacts.sl.cloud9.ibm.com/repository.shtml)

Project Layout
==============

The project layout follows the standard layout of a [Apache Maven Quickstart Java Application](http://maven.apache.org/archetypes/maven-archetype-quickstart/):

1. [src/main](../../tree/master/suro-oaas-couch/src/main): contains the source folder for the application logic, including resources, java classes, and web application files.
2. [src/test](../../tree/master/suro-oaas-couch/src/test): contains the testing logic.
3. [pom.xml](../../tree/master/suro-oaas-couch/pom.xml): contains the bill of material for this project and specifies all the dependencies and plugins required by this project.
 
The majority of the components are defined under the packages `com.ibm.au.optim.suro.model.strategy.impl.couch` and `com.ibm.au.suro.model.store.impl.couchdb`.

Build
=====

To build the package you can simply trigger the following maven command in the root directory of the project:

    $ mvn clean package
    
Alternatively, you can package and install the artifact into the local repository cache by issuing:

    $ mvn clean install

Deploy
======

To deploy the package you can simply trigger the following maven command, and given that Maven has the appropriate credentials, you will be able to push the artifact to the repository:

    $ mvn deploy
    
This command deploys the latest version of the built artifact to the repository and if it does not find it it triggers the build process.


Configuration
=============

The repositories implementation defined in `com.ibm.au.optim.suro.model.store.impl.couch` do require configuration parameters for connecting to the CouchDB or Cloudant instance. These
parameters are retrieved from the `com.ibm.au.vizanalytics.web.core.runtime.Environment` that is passed to the `bind(Environment)` method exposed by the following components:

1. `com.ibm.au.optim.suro.model.store.impl.couch.CouchDbTemplateRepository`
2. `com.ibm.au.optim.suro.mdoel.store.impl.couch.CouchDbRunRepository`
3. `com.ibm.au.optim.suro.model.store.impl.couch.CouchDbOptimizationResultRepository`

These parameters are the following:

1. `couchdb.url` - CouchDB base URL (default: http://localhost:5984)
2. `couchdb.username` - CouchDB username (default:  null)
3. `couchdb.password` - CouchDB password (default:  null)
4. `couchdb.database` - CouchDB default database (default: null)

In order to feed these parameters when using this component through REST Services module, please refer to the configuration section of that project.

