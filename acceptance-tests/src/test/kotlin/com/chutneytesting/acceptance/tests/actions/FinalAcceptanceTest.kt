/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.*
import com.chutneytesting.kotlin.dsl.*


val `Register simple success action` = Scenario(title = "Register simple success action") {
  Given("A simple scenario is saved") {
    createScenario(
      "scenarioId",
      """
        {
            "when":{
                "sentence":"Final action is registered",
                "implementation":{
                    "task":"{\n type: final \n inputs: {\n type: success \n name: Testing final action... \n} \n}"
                }
            },
            "thens": []
        }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("scenarioId".spEL, "DEFAULT")
  }
  Then("the report status is SUCCESS") {
    checkScenarioReportSuccess()
  }
  And("The report contains a single node for final actions") {
    JsonAssertAction(
      document = "report".spEL,
      expected = mapOf(
        "$.report.steps[1].name" to "TearDown",
        "$.report.steps[2].name" to "${'$'}isNull"
      )
    )
  }
  And("The report contains a single final action execution") {
    JsonAssertAction(
      document = "report".spEL,
      expected = mapOf(
        "$.report.steps[1].steps[0].name" to "Testing final action...",
        "$.report.steps[1].steps[0].type" to "success",
        "$.report.steps[1].steps[1]" to "${'$'}isNull"
      )
    )
  }
}

val `Register multiple actions with one complex, ie with inputs and strategy` = Scenario(title = "Register multiple actions with one complex, i.e. with inputs and strategy") {
  Given("A simple scenario is saved") {
    createScenario(
      "scenarioId",
      """
        {
            "when":{
                "sentence":"Final actions are registered",
                "subSteps":[
                    {
                        "sentence":"Register an assertion",
                        "implementation":{
                            "task":"{\n type: final \n inputs: {\n type: compare \n name: An assertion \n inputs: {\n actual: aValue \n expected: aValue \n mode: equals \n} \n} \n}"
                        }
                    },
                    {
                        "sentence":"Register a fail with retry",
                        "implementation":{
                            "task":"{\n type: final \n inputs: {\n type: fail \n name: I'm no good \n strategy-type: retry-with-timeout \n strategy-properties: {\n timeOut: 1500 ms \n retryDelay: 1 s \n} \n} \n}"
                        }
                    },
                    {
                        "sentence":"Register variable in context",
                        "implementation":{
                            "target": "CHUTNEY_LOCAL",
                            "task":"{\n type: final \n inputs: {\n type: context-put \n name: Put myKey \n inputs: {\n entries: {\n myKey: myValue \n} \n} validations: {\n putOk: ${"myKey == 'myValue'".hjsonSpEL} \n} \n} \n}"
                        }
                    }
                ]
            },
            "thens": []
        }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("scenarioId".spEL, "DEFAULT")
  }
  Then("the report status is FAILURE") {
    checkScenarioReportFailure()
  }
  And("The report contains a single node for final actions") {
    JsonAssertAction(
      document = "report".spEL,
      expected = mapOf(
        "$.report.steps[1].steps[0].type" to "compare",
        "$.report.steps[1].steps[0].status" to "SUCCESS",
        "$.report.steps[1].steps[1].type" to "fail",
        "$.report.steps[1].steps[1].status" to "FAILURE",
        "$.report.steps[1].steps[1].strategy" to "retry-with-timeout",
        "$.report.steps[1].steps[2].type" to "context-put",
        "$.report.steps[1].steps[2].status" to "SUCCESS"
      )
    )
  }
}

val `Register final action with validations on outputs` = Scenario(title = "Register final action with validations on outputs") {
  Given("A configured target for an endpoint") {
    createEnvironment(
      "ENV_FINAL",
      """
        [
            {
                "name": "test_http",
                "url": "https://localhost:8443",
                "properties": [
                    { "key" : "keyStore", "value": "/config/keystores/client.jks" },
                    { "key" : "keyStorePassword", "value": "client" },
                    { "key" : "keyPassword", "value": "client" },
                    { "key" : "username", "value": "admin" },
                    { "key" : "password", "value": "admin" }
                ]
            }
        ]
      """.trimIndent()
    )
  }
  And("A simple scenario is saved") {
    createScenario(
      "scenarioId",
      """
        {
            "when":{
                "sentence":"Final action are registered",
                "subSteps":[
                    {
                        "sentence":"Register action providing outputs",
                        "implementation":{
                            "target": "CHUTNEY_LOCAL",
                            "task":"{\n type: final \n target: test_http \n inputs: {\n type: http-get \n name: Get user \n inputs: {\n uri: /api/v1/user \n timeout: 5 sec \n} \n validations: { \n http_OK: ${"status == 200".hjsonSpEL} \n} \n} \n}"
                        }
                    }
                ]
            },
            "thens": []
        }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("scenarioId".spEL, "ENV_FINAL")
  }
  Then("the report status is SUCCESS") {
    checkScenarioReportSuccess()
  }
  And("The report contains the executions (in declaration reverse order) of all the final actions action") {
    JsonAssertAction(
      document = "report".spEL,
      expected = mapOf(
        "$.report.steps[0].steps[0].evaluatedInputs.validations.http_OK" to "\\\${#status == 200}",
        "$.report.steps[1].steps[0].type" to "http-get",
        "$.report.steps[1].steps[0].status" to "SUCCESS",
      )
    )
  }
}