# <img src="docs/docs/img/logo.svg" width="400"/> 
## Spice up your spec , Better `taste` your app !

[![Build](https://github.com/chutney-testing/chutney/actions/workflows/build-all.yml/badge.svg?branch=main)](https://github.com/chutney-testing/chutney/actions/workflows/build-all.yml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/559893368d134d729b204891e3ce0239)](https://www.codacy.com/gh/chutney-testing/chutney?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=chutney-testing/chutney&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://codecov.io/gh/chutney-testing/chutney/branch/master/graph/badge.svg)](https://codecov.io/gh/chutney-testing/chutney/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/server/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/server)

-------------

## Summary

* [Introduction](#introduction)
* [Installation](#installation)
* [Write your first Scenario](#write_your_first_scenario)
* [Documentation](#documentation)
* [Contributing](#contrib)
* [Support](#support)
* [Team](#team)
    * [Contributors](#contributors)

-------------

## <a name="introduction"></a> Introduction
Chutney aims to test deployed software in order to validate functional requirements.

Chutney is released as a standalone application including a test execution engine, 
a web front end, and an edition server to create and edit your scenarios, consult test reports, and define your environments and test data.

Chutney scenarios are declarative. They provide functional requirements and technical details (needed for automation) in a single view.

Those technical details are provided by generic [Actions](https://github.com/chutney-testing/chutney/blob/main/chutney/action-spi/src/main/java/com/chutneytesting/action/spi/Action.java) (such as HTTP, AMQP, MongoDB, Kafka, Selenium, etc.)  
Those Tasks are extensions, and you can easily develop yours, even proprietary or non-generic one, and include them in your own release.

In addition, Chutney provide SpEL evaluation and extensible [Function](https://github.com/chutney-testing/chutney/blob/main/chutney/action-spi/src/main/java/com/chutneytesting/action/spi/SpelFunction.java) in order to ease the use of managing scenario data like JSON path or Date comparison.

 
[Find out more in the documentation !](https://www.chutney-testing.com/)

Still asking yourself ["Why another test tool ?"](https://www.chutney-testing.com/concepts/)

-------------

## <a name="installation"></a> Installation

#### <a name="prerequisites"></a>Prerequisites

In order to install Chutney on your machine you need to have installed :
* JDK 17 or newer
* Docker (only to run integration tests)



#### Run the server

* Clone the repository
* From project root, go to chutney folder
* Build the project with the command : `mvn clean compile`
* Now you can start the server with the predefined configuration 'start_local_server'

You can also run the server with Docker with the command : `docker build --tag ghcr.io/chutney-testing/chutney/server:latest . -f ./.docker/server/Dockerfile`
[More information about Chutney and Docker](https://github.com/chutney-testing/chutney/tree/main/chutney/.docker)

#### Run the web app


* Once you built the server, you need to go to the ui module (from chutney folder)
* During compilation, Chutney downloaded a version of Node. If you don't have Node installed, you can make your environment variable points to it, for Windows you can run `$env:Path += ";./node"`
* Now are ready to start the front app, just use the downloaded npm with the command : `node .\node\node_modules\npm start`

You can also run the front app with Docker with the command : `docker build --tag ghcr.io/chutney-testing/chutney/ui:latest . -f ./.docker/ui/Dockerfile`
[More information about Chutney and Docker](https://github.com/chutney-testing/chutney/tree/main/chutney/.docker)


#### Use kotlin-dsl in your project

To wrte your own scenarios, you can create a Kotlin project with the following dependencies :

* [com.chutneytesting:chutney-kotlin-dsl](https://central.sonatype.com/artifact/com.chutneytesting/chutney-kotlin-dsl?smo=true)
* [org.jetbrains.kotlin:kotlin-stdlib](https://central.sonatype.com/artifact/org.jetbrains.kotlin/kotlin-stdlib?smo=true)
* [org.junit.jupiter:junit-jupiter-api](https://search.maven.org/artifact/org.junit.jupiter/junit-jupiter-api)


    <dependencies>
    <dependency>
        <groupId>com.chutneytesting</groupId>
        <artifactId>chutney-kotlin-dsl</artifactId> <!---->
        <version>1.7.0</version>
    </dependency>
       <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId> <!---->
            <version>1.6.10</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.8.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId> <!-- Optional  -->
            <version>5.8.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>


-------------

## <a name="write_your_first_scenario"></a> Write your first Scenario

In order to write a scenario, you also need to declare environments and services you want to test.

### Define your test environment
#### Declare a target
Under src/main/kotlin create a package (ex. com.chutneytesting.getstart) and create a Kotlin file (ex. Environments.kt) with the following content :

    package com.chutneytesting.getstart
    
    import com.chutneytesting.kotlin.dsl.ChutneyTarget
    
    val google = ChutneyTarget(
        name = "search_engine",
        url = "https://www.google.fr"
    )
    The target name search_engine is used as a reference in your scenarios
    The google variable is a reference to set a target in an environment

* The target name search_engine is used as a reference in your scenarios
* The google variable is a reference to set a target in an environment

#### Declare an environment
Now you can declare an environment within the same file, add the following content :

    val environment = ChutneyEnvironment(
        name = "The World Wide Web",
        description = "The World Wide Web",
        targets = listOf(
            google
        )
    )
* We reference the target google using the variable name.
* The environment name and description can be anything meaningful to you. The name will be shown in the execution report.
* The variable name environment is a reference to set the environment on running tests

#### Write a scenario
Under src/main/kotlin, in the same package or another, create a Kotlin file (ex. Scenarios.kt) with the following content :

    package com.chutneytesting.getstart
    
    import com.chutneytesting.kotlin.dsl.HttpGetAction
    import com.chutneytesting.kotlin.dsl.Scenario
    import com.chutneytesting.kotlin.dsl.SuccessAction
    import com.chutneytesting.kotlin.dsl.spEL
    
    val search_scenario = Scenario(title = "Search documents") {
        When("I visit a search engine") {
            HttpGetAction(
                target = "search_engine",
                uri = "/",
                validations = mapOf("request accepted" to "status == 200".spEL())
            )
        }
        Then("I am on the front page") {
            SuccessAction()
        }
    }
* The scenario title Search documents will be shown in the execution report.
* There are 2 steps When I visit a search engine and Then I am on the front page
* The first step will execute an HTTP GET call on the target name search_engine on the uri /
  * It also has one validation request accepted to check the response code status is 200.
* The second step does nothing meaningful in this example


-------------

## <a name="documentation"></a> Documentation

You can find the technical documentation of the 4 projects here :
* [Chutney server](https://github.com/chutney-testing/chutney)
* [Chutney UI](https://github.com/chutney-testing/chutney/tree/main/chutney/ui#readme)
* [Kotlin DSL](https://github.com/chutney-testing/chutney/tree/main/kotlin-dsl#readme)
* [Idea-plugin](https://github.com/chutney-testing/chutney/tree/main/idea-plugin#readme)

Get the [official documentation](https://www.chutney-testing.com/) for more information about how Chutney works.

-------------

## <a name="contrib"></a> Contributing ?

![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)

You don't need to be a developer to contribute, nor do much, you can simply:
* Enhance documentation,
* Correct a spelling,
* [Report a bug](https://github.com/chutney-testing/chutney/issues/new/choose)
* [Ask a feature](https://github.com/chutney-testing/chutney/issues/new/choose)
* [Give us advices or ideas](https://github.com/chutney-testing/chutney/discussions/categories/ideas),
* etc.

To help you start, we invite you to read:
* [Contributing](chutney/CONTRIBUTING.md), which gives you rules and code conventions to respect
* [Getting started](chutney/GETTING_STARTED.md), which document :
    * How to install and use Chutney as a User
    * How to install and setup the required environment for developing
* [Help Wanted](chutney/HELP_WANTED.md), if you wish to help us, but you don't know where to start, you might find some ideas in here !

To contribute to this documentation (README, CONTRIBUTING, etc.), we conforms to the [CommonMark Spec](https://spec.commonmark.org/)

## <a name="support"></a> Support

We’re using [Discussions](https://github.com/chutney-testing/chutney/discussions) as a place to connect with members of our - slow pace growing - community. We hope that you:
  * Ask questions you’re wondering about,
  * Share ideas,
  * Engage with other community members,
  * Welcome others, be friendly and open-minded !

For a more informal place to chat, if you worry about feeling dumb in the open on Github or feel uncomfortable with English, we can meet on [Zulip](https://chutney-testing.zulipchat.com/) through public or private messages. We will be happy to chat either in English, French, Spanish or Italian as much as we can ! :) https://chutney-testing.zulipchat.com/

## <a name="team"></a> Team

Core contributors :
  * [Nicolas Brouand](https://github.com/nbrouand)
  * [Alexandre Delaunay](https://github.com/DelaunayAlex)
  * [Matthieu Gensollen](https://github.com/boddissattva)
  * [Karim Goubbaa](https://github.com/KarimGl)

### <a name="contributors"></a> Contributors

We strive to provide a benevolent environment and support any [contribution](#contrib).

Before going open source, Chutney was inner-sourced and received contribution from over 30 persons