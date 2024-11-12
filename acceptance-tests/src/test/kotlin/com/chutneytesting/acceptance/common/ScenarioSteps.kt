/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.common

import com.chutneytesting.kotlin.dsl.*
import org.apache.commons.text.StringEscapeUtils.escapeJson

fun createExecuteAndCheckReportStatusOf(
  scenario: ChutneyScenario,
  environment: String = "ACCEPTANCE",
  reportStatusSuccess: Boolean = true
): ChutneyScenario {
  return Scenario(title = "Execute - ${scenario.title}") {
    Given("This scenario is saved") {
      createScenario("scenarioId", scenario)
    }
    When("The scenario is executed") {
      executeScenario("scenarioId".spEL, environment)
    }
    Then("The report status is ${if (reportStatusSuccess) "SUCCESS" else "FAILURE"}") {
      if (reportStatusSuccess) checkScenarioReportSuccess()
      else checkScenarioReportFailure()
    }
  }
}

// Take into account escaped spEL in scenario
private fun ChutneyScenario.cleanSpELForJsonRawCreateHttpPostAction() =
  escapeJson(this.toString()).replace("\\\\\\\\\${", "\\\${")

fun ChutneyStepBuilder.createScenario(outputScenarioId: String, scenario: ChutneyScenario) {
  HttpPostAction(
    target = "CHUTNEY_LOCAL",
    uri = "/api/scenario/v2/raw",
    body = """{ "title": "${scenario.title}", "content": "${scenario.cleanSpELForJsonRawCreateHttpPostAction()}" }""",
    headers = jsonHeader(),
    validations = mapOf(statusValidation(200)),
    outputs = mapOf(
      outputScenarioId to "body".spEL()
    )
  )
}

fun ChutneyStepBuilder.createScenario(outputScenarioId: String, scenario: String? = null) {
  val tmpScenario = scenario ?: """
    {
        "when":{
            "sentence":"Just a success step",
            "implementation":{
                "task":"{\n type: success \n }"
            }
        },
        "thens":[]
    }  
    """.trimIndent()

  HttpPostAction(
    target = "CHUTNEY_LOCAL",
    uri = "/api/scenario/v2",
    body = """
                {
                    "title":"Scenario with id: $outputScenarioId",
                    "scenario": $tmpScenario
                }
                """,
    headers = jsonHeader(),
    validations = mapOf(statusValidation(200)),
    outputs = mapOf(
      outputScenarioId to "body".spEL()
    )
  )
}

fun ChutneyStepBuilder.executeScenario(scenarioId: String, environment: String) {
  HttpPostAction(
    target = "CHUTNEY_LOCAL",
    uri = "/api/ui/scenario/execution/v1/$scenarioId/$environment",
    timeout = "25 s",
    body = null,
    validations = mapOf(statusValidation(200)),
    outputs = mapOf(
      "report" to "body".spEL()
    )
  )
}
