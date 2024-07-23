## Getting Started

Everything you need to run the app and start coding.

## Summary

* [How to run Chutney server](#use)
* [How to code](#code)

## <a name="use"></a> How to run Chutney server

### Run with docker
To launch Chutney, two docker images could be used :
* The [local-dev server](chutney/.docker) directly
* The [demo server](example/.docker) with some example scenarios

### Run in local-dev mode manually
To launch Chutney in _local-dev_ mode, use
* the classpath of [packaging/local-dev](chutney/packaging/local-dev) module
* `com.chutneytesting.ServerBootstrap` as main class

## <a name="code"></a> How to code

### Prerequisites

* [Java](https://adoptium.net/fr/temurin/releases/?package=jdk&version=17) - version 17
* [Maven](https://maven.apache.org/) - version 3.9.2 or higher - Java dependency management
* [Node](https://nodejs.org/en/) - version 20.10.0 or higher - JavaScript runtime
* [Npm] (https://www.npmjs.com/) - version 6.14.4 or higher - JavaScript package manager

If you use **direnv** and **nix** packages manager, we provide 2 files for the ui module : [.env.nix](../.env.nix) and [.envrc](../.envrc).

Upon running **direnv allow** inside ui module folder, it will install node, npm, and some usefull symlinks you can use for configuring IDE or other tools.

You can use a Javascript launcher such as [Volta](https://volta.sh/) to take care of **Node** and **Npm** by using the additional command line property: `-DuseExternalNpm=true`

## How to build

* Build and install Chutney: `mvn clean install -DskipTests -f chutney/pom.xml`
* Build and install kotlin-dsl: `cd kotlin-dsl && gradlew clean build -x test publishToMavenLocal`
* Install local-api-insecure-jar: use install-local-api-unsecure-jar [run configuration](https://github.com/chutney-testing/chutney/blob/main/.idea/runConfigurations/install_local_api_unsecure_jar.xml) or manually run maven command with options in this file as arguments.
* Build plugin: `idea-plugin/gradlew clean buildPlugin`

## Modules explanation

* chutney: server with ui to show and execute scenario with local-dev docker image
  * engine: Execution engine which sole responsibility is to execute scenarios and provide a report for each execution
  * packaging: default packaging used to start Chutney
  * server/server-core: Main module that
    * Back-end for front-end
    * Store scenarios (json), execution report and campaigns in jdbc database
    * Store in files target and environment information
    * Send scenarios to the execution engine and retrieve reports
  * action-impl: Default implementation of task (Sql, Http, Jms,...)
  * action-spi: Contains interfaces to extend the engine
  * environment: To manage environments for Chutney
  * jira: To interact with jira XRay plugin to update test, test exec or test plan
  * tools: Utility class with no dependency
  * ui : Front-end of Chutney
* kotlin-dsl: dsl to test as code and synchronise scenario with a server
* idea-plugin: plugin intellij to have interaction with a Chutney server
* docs: documentation as code for [chutney-testing](https://www.chutney-testing.com)
* example : Example project demonstrating the use of kotlin DSL with demo docker image
