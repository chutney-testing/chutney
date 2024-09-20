/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.checkScenarioSuccess
import com.chutneytesting.acceptance.common.createEnvironment
import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.ChutneyScenarioBuilder
import com.chutneytesting.kotlin.dsl.Scenario
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils

val `Micrometer counter meter` = Scenario(title = "Micrometer counter meter") {
  micrometerScenario(
    """
        {
                "givens":[
                    {
                        "sentence": "An existing counter",
                        "subSteps":[
                            {
                                "sentence": "Create counter meter",
                                "implementation":{
                                    "task":"{\n type: micrometer-counter \n inputs: {\n name: my.counter \n tags: [ 'myTagKey', 'myTagValue' ] \n} \n outputs: {\n myCounter: \${'$'}{#micrometerCounter} \n} \n}"
                                }
                            },
                            {
                                "sentence": "Check counter creation",
                                "subSteps":[
                                    {
                                        "sentence": "Request for counter meter",
                                        "implementation":{
                                            "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.counter \n} \n}"
                                        }
                                    },
                                    {
                                        "sentence": "Check counter value",
                                        "implementation":{
                                            "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[0.0]' \n mode: equals \n} \n}"
                                        }
                                    },
                                    {
                                        "sentence": "Check counter tags",
                                        "implementation":{
                                            "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.availableTags[?(@.tag=='myTagKey')].values[0]\").toString()} \n expected: '[\"myTagValue\"]' \n mode: equals \n} \n}"
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                "when":{
                    "sentence": "Increment my counter",
                    "implementation":{
                        "task":"{\n type: micrometer-counter \n inputs: {\n counter: \${'$'}{#myCounter} \n increment: '2' \n} \n}"
                    }
                },
                "thens":[
                    {
                        "sentence": "Request for counter meter",
                        "implementation":{
                            "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.counter \n} \n}"
                        }
                    },
                    {
                        "sentence": "Check counter value",
                        "implementation":{
                            "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[2.0]' \n mode: equals \n} \n}"
                        }
                    }
                ]
            }
        }
        """
  )
}

val `Micrometer timer meter` = Scenario(title = "Micrometer timer meter") {
  micrometerScenario(
    """
        {
            "givens":[
                {
                    "sentence": "An existing timer",
                    "subSteps":[
                        {
                            "sentence": "Create timer meter",
                            "implementation":{
                                "task":"{\n type: micrometer-timer \n inputs: {\n name: my.timer \n tags: [ 'myTagKey', 'myTagValue' ] \n} \n outputs: {\n myTimer: \${'$'}{#micrometerTimer} \n} \n}"
                            }
                        },
                        {
                            "sentence": "Check timer creation",
                            "subSteps":[
                                {
                                    "sentence": "Request for timer meter",
                                    "implementation":{
                                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.timer \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check timer count value",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[0.0]' \n mode: equals \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check timer tags",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.availableTags[?(@.tag=='myTagKey')].values[0]\").toString()} \n expected: '[\"myTagValue\"]' \n mode: equals \n} \n}"
                                    }
                                }
                            ]
                        }
                    ]
                }
            ],
            "when":{
                "sentence": "Update my timer",
                "implementation":{
                    "task":"{\n type: micrometer-timer \n inputs: {\n timer: \${'$'}{#myTimer} \n record: '5 s' \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence": "Request for timer meter",
                    "implementation":{
                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.timer \n} \n}"
                    }
                },
                {
                    "sentence": "Check timer count value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[1.0]' \n mode: equals \n} \n}"
                    }
                },
                {
                    "sentence": "Check timer total time value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='TOTAL_TIME')].value\").toString()} \n expected: '[5.0]' \n mode: equals \n} \n}"
                    }
                },
                {
                    "sentence": "Check timer max value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='MAX')].value\").toString()} \n expected: '[5.0]' \n mode: equals \n} \n}"
                    }
                }
            ]
        }
        """
  )
}

val `Micrometer timer meter with start and stop` = Scenario(title = "Micrometer timer meter with start and stop") {
  micrometerScenario(
    """
        {
            "givens":[
                {
                    "sentence": "An existing timer",
                    "implementation":{
                        "task":"{\n type: micrometer-timer \n inputs: {\n name: my.timer.start.stop \n tags: [ 'myTagKey', 'myTagValue' ] \n} \n outputs: {\n myTimer: \${'$'}{#micrometerTimer} \n} \n}"
                    }
                }
            ],
            "when":{
                "sentence": "Start and stop a timing sample",
                "subSteps":[
                    {
                        "sentence": "Start a timing sample",
                        "implementation":{
                            "task":"{\n type: micrometer-timer-start \n outputs: {\n myTimingSample: \${'$'}{#micrometerTimerSample} \n} \n}"
                        }
                    },
                    {
                        "sentence": "Sleep for a second",
                        "implementation":{
                            "task":"{\n type: sleep \n inputs: {\n duration: 1100 ms \n} \n}"
                        }
                    },
                    {
                        "sentence": "Stop the timing sample",
                        "implementation":{
                            "task":"{\n type: micrometer-timer-stop \n inputs: {\n sample: \${'$'}{#micrometerTimerSample} \n timer: \${'$'}{#myTimer} \n} \n}"
                        }
                    }
                ]
            },
            "thens":[
                {
                    "sentence": "Request for timer meter",
                    "implementation":{
                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.timer.start.stop \n} \n}"
                    }
                },
                {
                    "sentence": "Check timer count value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[1.0]' \n mode: equals \n} \n}"
                    }
                },
                {
                    "sentence": "Check timer total time value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \"\${'$'}{#json(#body, \\\"${'$'}.measurements[?(@.statistic=='TOTAL_TIME')].value\\\").get(0).toString()}\" \n expected: '1' \n mode: greater than \n} \n}"
                    }
                },
                {
                    "sentence": "Check timer max value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \"\${'$'}{#json(#body, \\\"${'$'}.measurements[?(@.statistic=='MAX')].value\\\").get(0).toString()}\" \n expected: '1' \n mode: greater than \n} \n}"
                    }
                }
            ]
        }
        """
  )
}

val `Micrometer gauge meter` = Scenario(title = "Micrometer gauge meter") {
  micrometerScenario(
    """
        {
            "givens":[
                {
                    "sentence": "An existing gauge",
                    "subSteps":[
                        {
                            "sentence": "Create gauge meter",
                            "implementation":{
                                "task":"{\n type: micrometer-gauge \n inputs: {\n name: my.gauge \n tags: [ 'myTagKey', 'myTagValue' ] \n gaugeObject: \${'$'}{new java.util.ArrayList()} \n} \n outputs: {\n myGaugeObject: \${'$'}{#micrometerGaugeObject} \n} \n}"
                            }
                        },
                        {
                            "sentence": "Check gauge creation",
                            "subSteps":[
                                {
                                    "sentence": "Request for counter meter",
                                    "implementation":{
                                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.gauge \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check gauge value",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='VALUE')].value\").toString()} \n expected: '[0.0]' \n mode: equals \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check gauge tags",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.availableTags[?(@.tag=='myTagKey')].values[0]\").toString()} \n expected: '[\"myTagValue\"]' \n mode: equals \n} \n}"
                                    }
                                }
                            ]
                        }
                    ]
                }
            ],
            "when":{
                "sentence": "Update gauge object",
                "implementation":{
                    "task":"{\n type: success \n outputs: {\n noop: \${'$'}{#myGaugeObject.add(new Object())} \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence": "Request for counter meter",
                    "implementation":{
                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.gauge \n} \n}"
                    }
                },
                {
                    "sentence": "Check gauge value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='VALUE')].value\").toString()} \n expected: '[1.0]' \n mode: equals \n} \n}"
                    }
                }
            ]
        }
        """
  )
}

val `Micrometer distribution summary meter` = Scenario(title = "Micrometer distribution summary meter") {
  micrometerScenario(
    """
        {
            "givens":[
                {
                    "sentence": "An existing distribution",
                    "subSteps":[
                        {
                            "sentence": "Create distribution meter",
                            "implementation":{
                                "task":"{\n type: micrometer-summary \n inputs: {\n name: my.summary \n tags: [ 'myTagKey', 'myTagValue' ] \n} \n outputs: {\n mySummary: \${'$'}{#micrometerSummary} \n} \n}"
                            }
                        },
                        {
                            "sentence": "Check distribution creation",
                            "subSteps":[
                                {
                                    "sentence": "Request for distribution meter",
                                    "implementation":{
                                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.summary \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check distribution total value",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='TOTAL')].value\").toString()} \n expected: '[0.0]' \n mode: equals \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check distribution count value",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[0.0]' \n mode: equals \n} \n}"
                                    }
                                },
                                {
                                    "sentence": "Check distribution tags",
                                    "implementation":{
                                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.availableTags[?(@.tag=='myTagKey')].values[0]\").toString()} \n expected: '[\"myTagValue\"]' \n mode: equals \n} \n}"
                                    }
                                }
                            ]
                        }
                    ]
                }
            ],
            "when":{
                "sentence": "Update my distribution",
                "implementation":{
                    "task":"{\n type: micrometer-summary \n inputs: {\n distributionSummary: \${'$'}{#mySummary} \n record: '2' \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence": "Request for distribution meter",
                    "implementation":{
                        "task":"{\n type: http-get \n target: chutney_local \n inputs: {\n uri: /actuator/metrics/my.summary \n} \n}"
                    }
                },
                {
                    "sentence": "Check distribution total value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='TOTAL')].value\").toString()} \n expected: '[2.0]' \n mode: equals \n} \n}"
                    }
                },
                {
                    "sentence": "Check distribution count value",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#json(#body, \"${'$'}.measurements[?(@.statistic=='COUNT')].value\").toString()} \n expected: '[1.0]' \n mode: equals \n} \n}"
                    }
                }
            ]
        }
        """
  )
}
private fun ChutneyScenarioBuilder.micrometerScenario(scenario: String) {
  val micrometerEnv = "ENV_MICRO_" + RandomStringUtils.randomAlphabetic(8)
  Given("a target for the http server mock") {
    createEnvironment(
      micrometerEnv,
      """
        [
            {
                "name": "chutney_local",
                "url": "https://localhost:8443",
                "properties": [
                    { "key" : "user", "value": "admin" },
                    { "key" : "password", "value": "admin" }
                ]
            }
        ]
      """.trimIndent()
    )
  }
  And("this scenario is saved") {
    createScenario(
      "scenarioId",
      scenario.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("${'$'}{#scenarioId}", micrometerEnv)
  }
  Then("the report status is SUCCESS") {
    checkScenarioSuccess()
  }
}