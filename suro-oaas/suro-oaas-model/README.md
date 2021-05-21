Surgical Unit Resource Optimization (SURO) - Data Entities Module
=================================================================

This project contains the definition of the standard components that make up the logic of the Surgical Unit Resource Optimization application and that are primarily used by the module [SURO - REST Services](../../tree/master/suro-oaas-api/README.md) to implement the behaviour of the application. Such Components:

1. `Strategy` and `StrategyRepository`: the first is the basic interface representing a strategy, while the second one is the interface of the repository used to manage strategies.
2. `Run` and `RunRepository`: the first is the basic interface representing a strategy execution, while the second one is the interface of the repository used to manage executions.
3. `OptimizationResult` and `OptimizationResultRepository`. the first is the basic interface representing an optimization result, while the second one is the associated repository.
4. `JobController` and `Job`: the first is the basic interface representing an optimization engine, and the second is the abstraction of an optimization job.
5. `NotificationBus` and `Notifier`: the first is the implementation of a simple bus for events, while the second is the interface that needs to be implemented by component that want to be notified about events and that can be registered with the bus.

These components are primarily used by the REST Services and the CouchDB and MongoDB binding modules.

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

1. [src/main](../../tree/master/suro-oaas-model/src/main): contains the source folder for the application logic, including resources, java classes, and web application files.
2. [src/test](../../tree/master/suro-oaas-model/src/test): contains the testing logic.
3. [pom.xml](../../tree/master/suro-oaas-model/pom.xml): contians the bill of material for this project and specifies all the dependencies and plugins required by this project.
 
The majority of the components are defined under the package `com.ibm.au.optim.suro.model` and subpackages.

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