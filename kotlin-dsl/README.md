# Chutney Testing Kotlin DSL

[![Build](https://github.com/chutney-testing/chutney-kotlin-dsl/workflows/Build/badge.svg?branch=master)](https://github.com/chutney-testing/chutney-kotlin-dsl/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/chutney-kotlin-dsl/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.chutneytesting/chutney-kotlin-dsl)

## DO IT IN CODE {"NOT": "JSON"}

This repository aims to add a kotlin flavor for writing and executing chutney scenarios.

## Why?

- Avoid text copy pasting
- Provide better code assistance using IDE 
- Allow customization for teams

A chutney json scenario:
```json
{
  "title": "SWAPI GET people record",
  "description": "SWAPI GET people record",
  "givens": [
    {
      "description": "I set get people service api endpoint",
      "implementation": {
        "type": "context-put",
        "inputs": {
          "entries": {
            "uri": "api/people/1"
          }
        }
      }
    }
  ],
  "when": {
    "description": "I send GET HTTP request",
    "implementation": {
      "type": "http-get",
      "target": "swapi.dev",
      "inputs": {
        "uri": "${#uri}"
      }
    }
  },
  "thens": [
    {
      "description": "I receive valid HTTP response",
      "implementation": {
        "type": "json-assert",
        "inputs": {
          "document": "${#body}",
          "expected": {
            "$.name": "Luke Skywalker"
          }
        }
      }
    }
  ]
}
```

Writing the same scenario with a kotlin DSL:
```kotlin
Scenario(title = "SWAPI GET people record") {
    Given("I set get people service api endpoint") {
        ContextPutAction(entries = mapOf("uri" to "api/people/1"))
    }
    When("I send GET HTTP request") {
        HttpGetAction(target = "swapi.dev", uri = "uri".spEL())
    }
    Then("I receive valid HTTP response") {
        JsonAssertAction(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
    }
}
```

How (k)ool is Kotlin? super (k)ool! 

# How to run Kotlin scenarios

> Some examples are available in [example module](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/master/src)

You want to run Chutney scenarios from your local environment or on your CI/CD ?

You want to use the DSL in your tests without the hassle of installing a Chutney server ?

Here we go !

## 1. Define your Environments

#### Define targets

Here, you can see that ```systemA``` and ```systemB``` share the same name ```mySystem```.
This is usefull for writing scenarios without coupling them to specific URLs or configuration.

By using the same name and overriding only specific properties, you can run the same scenario on different environment (see next snippet).

```kotlin
val systemA = ChutneyTarget(
    name = "mySystem",
    url = "tcp://my.system.com:4242",
    configuration = ChutneyConfiguration(
        properties = mapOf("some" to "properties"),
        security = ChutneySecurityProperties(
            credential = ChutneySecurityProperties.Credential(
                username = "kakarot",
                password = "uruchim41"
            )
        )
    )
)
val systemB = systemA.copy(url = "tcp://another.url.com:1313")
val systemBprime = systemB.copy(name = "prime", url = "http://yet.another.url")
```

#### Define environments

Take care while adding your targets to an environment. In the previous snippet, ```systemA``` and ```systemB``` share the same name ```mySystem```.
Since the target name is used as an identifier, you should not put targets with the same name in the same environment !

```kotlin
val envA = ChutneyEnvironment(
    name = "envA",
    description = "fake environment for test",
    targets = listOf(
        systemA
    )
)

val envB = ChutneyEnvironment(
    name = "envB",
    description = "fake environment for test",
    targets = listOf(
        systemB,
        systemBprime
    )
)
```

#### Define environments as JSON

Alternatively, you can describe your environments with JSON files.

They follow the model from Chutney [Environment module](https://github.com/chutney-testing/chutney/blob/master/environment/src/main/java/com/chutneytesting/environment/api/dto/EnvironmentDto.java).

In order to use environments from JSON files, you should store them in a folder named **environment** under some directory whose default path is ```./chutney```, but you can override it with a constructor parameter : ```Launcher(environmentJsonRootPath = "./chutney_env")```


## 3. Define your scenarios

As seen in the two previous snippets, note how the scenario refers only to the target name ```mySystem```.
So this scenario can run on environment ```envA``` and ```envB``` without modifying it.

```kotlin
val say_hi = Scenario(title = "Say hi!") {
    When("Hello world") {
        HttpGetAction(
            target = "mySystem"
        )
    }
    Then("Succeed") {
        SuccessAction()
    }
}
```

## 4. Run your scenarios ! 

### Using the Launcher in JUnit test

For example, you can wrap Chutney execution with JUnit.

```kotlin
class CrispyIntegrationTest {
    @Test
    fun `say hi`() {
        Launcher().run(say_hi, envA)
    }
}
```

#### Change default reports folder

By default, reports are in ".chutney/reports". But you can override it using ```Launcher("target/chutney-reports")```

#### Expecting a failure

You can change the expecting status of your scenario. For example, the Chutney scenario will fail, 
but not the running JUnit test.

```kotlin
@Test
fun `is able to fail`() {
    launcher.run(failing_scenario, environment, StatusDto.FAILURE)
}
 ```

#### Running many scenarios

You can simply pass a list of scenarios.

```kotlin
val my_campaign = listOf(
    a_scenario,
    another_scenario
)

@Test
fun `is able to run many scenarios`() {
    launcher.run(my_campaign, environment)
}
```

#### Running many scenarios, again

You can create campaigns by using ```@ParameterizedTest```
This is nice because JUnit will wrap each scenario execution into its own.

```kotlin
private companion object {
    @JvmStatic
    fun campaign_scenarios() = Stream.of(
        Arguments.of(a_scenario),
        Arguments.of(another_scenario)
    )
}

@ParameterizedTest
@MethodSource("campaign_scenarios")
fun `is able to emulate a campaign on one environment`(scenario: ChutneyScenario) {
    launcher.run(scenario, environment)
}
```

#### Running a campaign, on different environment

To keep it simple, we will combine the two previous snippets, 
but this time we will parameterize the environment. 

```kotlin
private companion object {
    @JvmStatic
    fun environments() = Stream.of(
        Arguments.of(envA),
        Arguments.of(envB)
    )
}

val my_campaign = listOf(
    a_scenario,
    another_scenario
)

@ParameterizedTest
@MethodSource("environments")
fun `is able to run a campaign on different environments`(environment: ChutneyEnvironment) {
    launcher.run(my_campaign, environment)
}
```

### Using the JUnit5 engine

A JUnit5 engine has been developed to execute Chutney scenarios.

#### Annotation
The annotation ```@ChutneyTest``` signals that a method is a JUnit5 testable element.
This annotated method must have a ```ChutneyScenario``` as return type to be taken into account.
The ```environment``` property could be used to specify a chutney environment to use.

```kotlin
@ChutneyTest(environment = "CHUTNEY")
    fun testMethod(): ChutneyScenario {
        return call_a_website
    }
```

#### JUnit listeners
Four test execution listeners are automatically included :
* **ConsoleLogScenarioReportExecutionListener** : Log a readable report after a scenario execution
* **ConsoleLogStepReportExecutionListener** : Log a readable report after a scenario step execution
* **FileWriterScenarioReportExecutionListener** : Write JSON report on disk after a scenario execution
* **SiteGeneratorExecutionListener** : Use JSON reports of an execution to install a static website

#### JUnit reports entries
Two report entries could be listened to with following keys :
* **chutney.report** : Report entry key for JSON scenario report
* **chutney.report.step** : Report entry key for JSON scenario's step report

#### Configuration
As the Kotlin Chutney JUnit engine is packaged in ```chutney-koltin-dsl``` module, just adding it as a dependency will make 
the engine active when executing tests with the JUnit platform.

Chutney JUnit configuration parameters are :
* **chutney.environment.default** : Default environment name to use (string with no default value)
* **chutney.environment.rootPath** : Path of environments JSON definitions (string
  with ```.chutney/environments``` default value)
* **chutney.report.rootPath** : Path where reports and website will be accessible after execution (string
  with ```.chutney/reports``` default value)
* **chutney.log.scenario.enabled** : Log scenario report to console (boolean with ```true``` default value)
* **chutney.log.step.enabled** : Log scenario step report to console (boolean with ```true``` default value)
* **chutney.log.color.enabled** : Log scenario report and scenario step report to console with ANSI colors (
  boolean with ```true``` default value)
* **chutney.report.file.enabled** : Write scenario JSON report to disk (boolean with ```true``` default value)
* **chutney.report.site.enabled** : Generate website from scenario JSON report (boolean with ```false``` default
  value)
* **chutney.engine.stepAsTest** : Do not consider scenario's steps as tests (boolean with ```true``` default
  value)

Those configuration parameters could be defined as environment, JVM system or JUnit properties (junit-platform.properties).
