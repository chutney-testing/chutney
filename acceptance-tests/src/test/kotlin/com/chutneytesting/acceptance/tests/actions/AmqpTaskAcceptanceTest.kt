/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.*
import com.chutneytesting.kotlin.dsl.*


fun `amqp test all steps`(
  actionAmqpPort: Int
): ChutneyScenario {

  return Scenario(title = "amqp test all steps") {
    Given("An embedded amqp server") {
      QpidServerStartAction()
    }
    And("A target for this amqp server") {
      createEnvironment(
        "AMQP_SCENARIO_ENV",
        """
        [
            {
                "name": "test_amqp",
                "url": "amqp://host.docker.internal:$actionAmqpPort",
                "properties": [
                    { "key" : "user", "value": "guest" },
                    { "key" : "password", "value": "guest" }
                ]
            }
        ]  
        """.trimIndent()
      )
    }
    And("This scenario with amqp tasks is saved") {
      createScenario(
        "scenarioId",
        """
        {
            "when":{
                "sentence":"Create queue",
                "implementation":{
                    "task":"{\n type: amqp-create-bound-temporary-queue \n target: test_amqp \n inputs: {\n exchange-name: amq.direct \n routing-key: routemeplease \n queue-name: test \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Publish message 1",
                    "implementation":{
                        "task":"{\n type: amqp-basic-publish \n target: test_amqp \n inputs: {\n exchange-name: amq.direct \n routing-key: routemeplease \n payload: bodybuilder \n} \n}"
                    }
                },
                {
                    "sentence":"Publish message 2",
                    "implementation":{
                        "task":"{\n type: amqp-basic-publish \n target: test_amqp \n inputs: {\n exchange-name: amq.direct \n routing-key: routemeplease \n payload: bodybuilder2 \n} \n}"
                    }
                },
                {
                    "sentence":"Get messages",
                    "implementation":{
                        "task":"{\n type: amqp-basic-get \n target: test_amqp \n inputs: {\n queue-name: test \n} \n}"
                    }
                },
                {
                    "sentence":"Check message 1",
                    "implementation":{
                        "task":"{\n type: string-assert \n inputs: {\n document: ${"body".hjsonSpEL} \n expected: bodybuilder \n} \n}"
                    }
                },
                {
                    "sentence":"Clean queue",
                    "implementation":{
                        "task":"{\n type: amqp-clean-queues \n target: test_amqp \n inputs: {\n queue-names: [\n test \n] \n} \n}"
                    }
                }
            ]
        }  
        """.trimIndent()
      )
    }
    When("The scenario is executed") {
      executeScenario("scenarioId".spEL, "AMQP_SCENARIO_ENV")
    }
    Then("the report status is SUCCESS") {
      checkScenarioReportSuccess()
    }
  }
}