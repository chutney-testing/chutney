/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.engine

import com.chutneytesting.acceptance.common.checkScenarioReportSuccess
import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.*

val `Retry should stop after success assertion` = Scenario(title = "Retry should stop after success assertion") {
  Given("this scenario is saved") {
    createScenario(
      "scenarioId",
      """
        {
            "when":{
                "sentence":"Set stop date",
                "implementation":{
                    "task":"{\n type: context-put \n inputs: {\n entries: {\n dateTimeFormat: ss \n secondsPlus5: ${"dateFormatter(#dateTimeFormat).format(#now().plusSeconds(5))".hjsonSpEL} \n} \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Assertion",
                    "strategy": {
                        "type": "retry-with-timeout",
                        "parameters": {
                            "timeOut": "15 sec",
                            "retryDelay": "1 sec"
                        }
                    },
                    "subSteps":[
                        {
                            "sentence":"Set current date",
                            "implementation":{
                                "task":"{\n type: context-put \n inputs: {\n entries: {\n currentSeconds: ${"dateFormatter(#dateTimeFormat).format(#now())".hjsonSpEL} \n} \n} \n}"
                            }
                        },
                        {
                            "sentence":"Check current date get to stop date",
                            "implementation":{
                                "task":"{\n type: string-assert \n inputs: {\n document: \${'$'}{T(java.lang.String).format('%02d', new Integer(#secondsPlus5) + 1)} \n expected: \${'$'}{T(java.lang.String).format('%02d', new Integer(#currentSeconds) + 2)} \n} \n}"
                            }
                        }
                    ]
                }
            ]
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
}
