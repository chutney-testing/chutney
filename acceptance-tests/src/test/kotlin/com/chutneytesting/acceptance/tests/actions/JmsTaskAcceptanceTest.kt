/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.*
import com.chutneytesting.kotlin.dsl.*


fun `Jms jmsAction wrong url`(
  jmsAction: String,
  actionInputs: String
): ChutneyScenario {
  return Scenario(title = "Jms jmsAction wrong url") {
    val environmentName = "JMS_$jmsAction"
    Given("A target pointing to an unknown service") {
      createEnvironment(
        environmentName,
        """
        [
          {
              "name": "test_jms",
              "url": "tcp://localhost:11111",
              "properties": [
                  {
                      "key": "java.naming.factory.initial",
                      "value": "org.apache.activemq.jndi.ActiveMQInitialContextFactory"
                  }
              ]
          }
        ]
      """.trimIndent()
      )
    }
    And("This scenario with sql task is saved") {
      createScenario(
        "scenarioId",
        """
        {
            "when":{
                "sentence":"Make failed jms request",
                "implementation":{
                    "task":"{\n type: jms-$jmsAction \n target: test_jms \n inputs: {\n $actionInputs \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Assert http status",
                    "implementation":{
                        "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#status} \n expected: 200 \n mode: not equals \n} \n}"
                    }
                }
            ]
        }
        """.trimIndent()
      )
    }
    When("The scenario is executed") {
      executeScenario("${'$'}{#scenarioId}", environmentName)
    }
    Then("the report status is FAILURE") {
      checkScenarioFailure()
    }
  }
}

fun `Jms sender then clean then send and listen it on embedded broker`(): ChutneyScenario {
  return Scenario(title = "Jms sender then clean then send and listen it on embedded broker") {
    Given("An associated target") {
      createEnvironment(
        "JMS_ENV_OK",
        """
        [
          {
              "name": "test_jms",
              "url": "tcp://localhost:61616",
              "properties": [
                  {
                      "key": "java.naming.factory.initial",
                      "value": "org.apache.activemq.jndi.ActiveMQInitialContextFactory"
                  }
              ]
          }
        ]
      """.trimIndent()
      )
    }
    And("This scenario with sql task is saved") {
      createScenario(
        "scenarioId",
        """
        {
            "givens":[
                {
                    "sentence":"Start JMS server",
                    "implementation":{
                        "task":"{\n type: jms-broker-start \n}"
                    }
                }
            ],
            "when":{
                "sentence":"Send JMS Message",
                "implementation":{
                    "task":"{\n type: jms-sender \n target: test_jms \n inputs: {\n destination: dynamicQueues/test \n body: something \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Clean queue",
                    "implementation":{
                        "task":"{\n type: jms-clean-queue \n target: test_jms \n inputs: {\n destination: dynamicQueues/test \n} \n}"
                    }
                },
                {
                    "sentence":"Send JMS Message",
                    "implementation":{
                        "task":"{\n type: jms-sender \n target: test_jms \n inputs: {\n destination: dynamicQueues/test \n body: message to catch \n} \n}"
                    }
                },
                {
                    "sentence":"Listen to queue",
                    "implementation":{
                        "task":"{\n type: jms-listener \n target: test_jms \n inputs: {\n destination: dynamicQueues/test \n} \n}"
                    }
                },
                {
                    "sentence":"Check JMS message",
                    "implementation":{
                        "task":"{\n type: string-assert \n inputs: {\n document: \${'$'}{#textMessage} \n expected: message to catch \n} \n}"
                    }
                }
            ]
        }
        """.trimIndent()
      )
    }
    When("The scenario is executed") {
      executeScenario("${'$'}{#scenarioId}", "JMS_ENV_OK")
    }
    Then("the report status is SUCCESS") {
      checkScenarioSuccess()
    }
  }
}