/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.engine

import com.chutneytesting.acceptance.common.checkScenarioSuccess
import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.*

val `Execute gwt scenario with global vars` = Scenario(title = "Execute gwt scenario with global vars") {
  Given("Global variables defined in global_var") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/globalvar/v1/global_var",
      timeout = "5 s",
      body =
      """
      {
          "message": "{ \"simple\": { \"word\": \"a_word\", \"line\": \"one line\", \"multiline\": \"My half empty glass,\\nI will fill your empty half.\\nNow you are half full.\" }, \"escape\": { \"quote\": \"line with quote \\\"\", \"backslash\": \"line with backslash \\\\\", \"slash\": \"line with slash as url http://host:port/path\", \"apostrophe\": \"line with apostrophe '\" } }"
      }
      """,
      headers = mapOf("Content-Type" to "application/json;charset=UTF-8"),
      validations = mapOf(
        statusValidation(200)
      )
    )
  }
  Given("this scenario is saved") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/scenario/v2",
      body = """
            {
                "title": "GWT testcase with parameters for global vars",
                "tags": [],
                "computedParameters": {
                    "testcase parameter quote": "**unused**",
                    "testcase parameter apostrophe": "**unused**"
                },
                "scenario": {
                    "when": {
                        "sentence": "Putting variables with sensitive characters in context",
                        "subSteps": [
                            {
                                "implementation": {
                                    "type": "context-put",
                                    "inputs": {
                                        "entries": {
                                            "slash": "**escape.slash**"
                                        }
                                    }
                                }
                            },
                            {
                                "implementation": {
                                    "type": "context-put",
                                    "inputs": {
                                        "entries": {
                                            "apostrophe": "**escape.apostrophe**"
                                        }
                                    }
                                }
                            },
                            {
                                "implementation": {
                                    "type": "context-put",
                                    "inputs": {
                                        "entries": {
                                            "quote": "**escape.quote**"
                                        }
                                    }
                                }
                            },
                            {
                                "implementation": {
                                    "type": "context-put",
                                    "inputs": {
                                        "entries": {
                                            "backslash": "**escape.backslash**"
                                        }
                                    }
                                }
                            }
                        ]
                    },
                    "thens": [
                        {
                            "sentence": "Context contains correct value of those variables",
                            "subSteps": [
                                {
                                    "implementation": {
                                        "type": "compare",
                                        "inputs": {
                                            "mode": "equals",
                                            "actual": "\${'$'}{#slash}",
                                            "expected": "line with slash as url http:\/\/host:port\/path"
                                        }
                                    }
                                },
                                {
                                    "implementation": {
                                        "type": "compare",
                                        "inputs": {
                                            "mode": "equals",
                                            "actual": "\${'$'}{#apostrophe}",
                                            "expected": "line with apostrophe '"
                                        }
                                    }
                                },
                                {
                                    "implementation": {
                                        "type": "compare",
                                        "inputs": {
                                            "mode": "equals",
                                            "actual": "\${'$'}{#quote}",
                                            "expected": "line with quote \""
                                        }
                                    }
                                },
                                {
                                    "implementation": {
                                        "type": "compare",
                                        "inputs": {
                                            "mode": "equals",
                                            "actual": "\${'$'}{#backslash}",
                                            "expected": "line with backslash \\"
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            }
            """,
      headers = mapOf(
        "Content-Type" to "application/json;charset=UTF-8"
      ),
      validations = mapOf(
          statusValidation(200)
      ),
      outputs = mapOf(
        "scenarioId" to "body".spEL()
      )
    )
  }
  When("The scenario is executed") {
    executeScenario("${'$'}{#scenarioId}", "DEFAULT")
  }
  Then("the report status is SUCCESS") {
    checkScenarioSuccess()
  }
}