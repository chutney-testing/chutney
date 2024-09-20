/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.common

import com.chutneytesting.kotlin.dsl.ChutneyStepBuilder
import com.chutneytesting.kotlin.dsl.HttpPostAction
import com.chutneytesting.kotlin.dsl.spEL
import com.chutneytesting.kotlin.dsl.statusValidation

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

