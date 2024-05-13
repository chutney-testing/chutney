# Chutney
## Spice up your spec , Better `taste` your app !

[![Build](https://github.com/chutney-testing/chutney/actions/workflows/build-all.yml/badge.svg?branch=main)](https://github.com/chutney-testing/chutney/actions/workflows/build-all.yml)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/559893368d134d729b204891e3ce0239)](https://www.codacy.com/gh/chutney-testing/chutney?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=chutney-testing/chutney&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://codecov.io/gh/chutney-testing/chutney/branch/master/graph/badge.svg)](https://codecov.io/gh/chutney-testing/chutney/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/server/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/server)

-------------

## Summary

* [Introduction](#introduction)
* [Installation](#installation)
* [Scenario Example](#scenario_example)
* [Documentation](#documentation)
* [Contributing](#contrib)
* [Support](#support)
* [Contributors](#contributors)

-------------

## <a name="introduction"></a> Introduction
Chutney aims to **test deployed software** in order to validate functional requirements.

Chutney scenarios are **declarative** written with a **kotlin dsl**. They provide functional requirements and technical details (needed for automation) in a single view.

Chutney is also released as a standalone application including a test execution engine and a a web front end to consult test reports.  

Technical details are provided by generic [Actions](https://github.com/chutney-testing/chutney/blob/main/chutney/action-spi/src/main/java/com/chutneytesting/action/spi/Action.java) (such as HTTP, AMQP, MongoDB, Kafka, Selenium, etc.)  
Those Actions are extensions, and you can easily develop yours, even proprietary or non-generic one, and include them in your own release.

In addition, Chutney provide SpEL evaluation and extensible [Function](https://github.com/chutney-testing/chutney/blob/main/chutney/action-spi/src/main/java/com/chutneytesting/action/spi/SpelFunction.java) in order to ease the use of managing scenario data like JSON path or Date comparison.

[Find out more in the documentation !](https://www.chutney-testing.com/)

Still asking yourself ["Why another test tool ?"](https://www.chutney-testing.com/concepts/)

-------------

## <a name="installation"></a> Installation

#### Locally

In order to install Chutney on your machine, you can use Java or Docker. 
See [Start a server](https://www.chutney-testing.com/installation/local_dev/#start-a-server).

#### On premise

See [installation on premise](https://www.chutney-testing.com/installation/on_premise/), for details if you want to customize your own version of chutney server.

-------------

## <a name="scenario_example"></a> Scenario Example

You can find all the documentation of [how to write a scenario here](https://www.chutney-testing.com/getting_started/write/)

### Example of a scenario

Here is an example of a scenario written in Kotlin.
* [Scenario source](https://github.com/chutney-testing/chutney/blob/main/kotlin-dsl/example/src/main/kotlin/com/chutneytesting/example/scenario/http_scenario.kt)
* [How to run it locally with test containers](https://github.com/chutney-testing/chutney/blob/main/kotlin-dsl/example/src/test/kotlin/com/chutneytesting/example/http/HttpScenarioTest.kt)

```kotlin
    const val HTTP_TARGET_NAME = "HTTP_TARGET"

    const val FILMS_ENDPOINT = "/films"
    private val JSON_CONTENT_TYPE = "Content-Type" to "application/json";

    var FILM = """
    {
        "title": "Castle in the Sky",
        "director": "Hayao Miyazaki",
        "rating": "%rating%",
        "category": "fiction"
    }
    """

    val http_scenario = Scenario(title = "Films library") {
        Given("I save a new film") {
            HttpPostAction(
                target = HTTP_TARGET_NAME,
                uri = FILMS_ENDPOINT,
                body = FILM.trimIndent(),
                headers = mapOf(
                    JSON_CONTENT_TYPE
                ),
                validations = mapOf(
                    statusValidation(201)
                ),
                outputs = mapOf(
                    "filmId" to "#body".elEval()
                )
            )
        }
    
        When ("I update rating") {
            HttpPatchAction(
                target = HTTP_TARGET_NAME,
                uri = "$FILMS_ENDPOINT/\${#filmId}",
                body = """
                    {
                    "rating": "79",
                    }
                """.trimIndent(),
                headers = mapOf(
                    JSON_CONTENT_TYPE
                ),
                validations = mapOf(
                    statusValidation(200)
                )
            )
        }
    
        Then ("I check that rating was updated") {
            Step("I get film by id") {
                HttpGetAction(
                    target = HTTP_TARGET_NAME,
                    uri = "$FILMS_ENDPOINT/\${#filmId}",
                    headers = mapOf(
                        JSON_CONTENT_TYPE
                    ),
                    validations = mapOf(
                        statusValidation(200)
                    ),
                    outputs = mapOf(
                        "title" to "jsonPath(#body, '\$.title')".spEL(),
                        "rating" to "jsonPath(#body, '\$.rating')".spEL()
                    )
                )
            }
        Step ("I check rating"){
            AssertAction(
                asserts = listOf(
                    "title.equals('Castle in the Sky')".spEL(),
                    "rating.equals('79')".spEL()
                )
            )
        }
    }
```

* In this example the scenario will save the content of FILM to an external server.
* Then it will update it, fetch it and finally verify that the FILM has indeed been updated.
* In this scenario we perform Http Actions, you can find [all available Chutney Actions here](https://www.chutney-testing.com/documentation/actions/)
* You can find some other example with jms, kafka, rabbit or sql [here](https://github.com/chutney-testing/chutney/tree/main/kotlin-dsl/example/src/main/kotlin/com/chutneytesting/example/scenario)
-------------

## <a name="documentation"></a> Documentation

Get the [official documentation](https://www.chutney-testing.com/) for more information about how Chutney works.

-------------

## <a name="contrib"></a> Contributing ?

![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)

See the [Getting started](chutney/GETTING_STARTED.md), which document how to install and setup the required environment for developing

You don't need to be a developer to contribute, nor do much, you can simply:
* Enhance documentation,
* Correct a spelling,
* [Report a bug](https://github.com/chutney-testing/chutney/issues/new/choose)
* [Ask a feature](https://github.com/chutney-testing/chutney/issues/new/choose)
* [Give us advices or ideas](https://github.com/chutney-testing/chutney/discussions/categories/ideas),
* etc.

To help you start, we invite you to read [Contributing](chutney/CONTRIBUTING.md), which gives you rules and code conventions to respect

To contribute to this documentation (README, CONTRIBUTING, etc.), we conforms to the [CommonMark Spec](https://spec.commonmark.org/)

## <a name="support"></a> Support

We’re using [Discussions](https://github.com/chutney-testing/chutney/discussions) as a place to connect with members of our - slow pace growing - community. We hope that you:
  * Ask questions you’re wondering about,
  * Share ideas,
  * Engage with other community members,
  * Welcome others, be friendly and open-minded !

## <a name="contributors"></a> Contributors

Core contributors :
* [Mael Besson](https://github.com/bessonm)
* [Nicolas Brouand](https://github.com/nbrouand)
* [Alexandre Delaunay](https://github.com/DelaunayAlex)
* [Matthieu Gensollen](https://github.com/boddissattva)
* [Karim Goubbaa](https://github.com/KarimGl)
* [Loic Ledoyen](https://github.com/ledoyen)

We strive to provide a benevolent environment and support any [contribution](#contrib).