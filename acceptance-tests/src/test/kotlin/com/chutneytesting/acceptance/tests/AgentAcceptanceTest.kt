/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests

import com.chutneytesting.acceptance.common.jsonHeader
import com.chutneytesting.kotlin.dsl.*

// Agent A (main) -> Agent B
// We are the agent B. The agent A send us its network configuration in order we save it.
// TODO - This test is flaky on some setup do the exploration process which is very time consuming, because of connection refused and connection timeout.
// In order to run test features in any order and then rename folder "1-agent" to "agent", when have to implement a way to delete all existing environment.
// Current solution seems to be a for loop.

fun `We receive a network configuration to persist`(): ChutneyScenario {
  return Scenario(title = "We receive a network configuration to persist") {
    Given("network configuration initialized") {
      Step("Request current network configuration") {
        HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/description",
          outputs = mapOf(
            "networkConfiguration" to "json(#body, '\$.networkConfiguration')".spEL
          )
        )
      }
      Step("Init configuration") {
        HttpPostAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v1/agentnetwork/configuration",
          headers = jsonHeader(),
          body = "jsonSerialize(#networkConfiguration)".spEL,
          timeout = "5 s",
          outputs = mapOf(
            "networkConfiguration" to "body".spEL
          )
        )
      }
    }
    When("network configuration with target fake_target with url http://fake_url:1234/fake is received") {
      HttpPostAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/v1/agentnetwork/wrapup",
        headers = jsonHeader(),
        body = """
       {
            "networkConfiguration": {
                "creationDate": "${"jsonPath(#networkConfiguration, \"$.networkConfiguration.creationDate\")".spEL}",
                "environmentsConfiguration": [
                    {
                        "name": "AGENT_ENV",
                        "targets": [
                            {
                                "name": "fake_target",
                                "url": "http://fake_url:1234/fake"
                            }
                        ]
                    }
                ],
                "agentNetworkConfiguration": ${"jsonSerialize(#jsonPath(#networkConfiguration, \"$.networkConfiguration.agentNetworkConfiguration\"))".spEL}
            },
            "agentsGraph": ${"jsonSerialize(#jsonPath(#networkConfiguration, \"$.agentsGraph\"))".spEL}
        }  
       """.trimIndent(),
        validations = mapOf(statusValidation(200))
      )
    }
    Then("target FAKE_TARGET is saved locally") {
      Step("") {
        HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/v2/environments",
          outputs = mapOf(
            "environments" to "body".spEL
          ),
          validations = mapOf(statusValidation(200))
        )
      }
      Step("Check target is present") {
        CompareAction(
          mode = "equals",
          actual = "json(#environments, '\$[?(@.name==''AGENT_ENV'')].targets[*].name')[0]".spEL,
          expected = "fake_target"
        )
      }
    }
  }
}
