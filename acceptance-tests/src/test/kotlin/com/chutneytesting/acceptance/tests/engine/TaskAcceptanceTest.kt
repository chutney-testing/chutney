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

fun `Retrieve action action-id from all actions and by identifier`(
  actionId: String,
  inputs: String
): ChutneyScenario {

  return Scenario(title = "Retry should stop after success assertion") {
    When("The scenario is executed") {
      HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/action/v1",
        validations = mapOf(statusValidation(200)),
        outputs = mapOf(
          "allTasks" to "body".spEL
        )
      )
    }
    And("Request engine for action " + actionId) {
      HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/action/v1/" + actionId,
        validations = mapOf(statusValidation(200)),
        outputs = mapOf(
          "action" to "body".spEL
        )
      )
    }
    Then("Its inputs are present in both responses") {
      Step("json-compare Assert inputs from all actions") {
        JsonCompareAction(
          document1 = "allTasks".spEL,
          document2 = inputs,
          comparingPaths = mapOf(
            "$[*][?(@.identifier == '$actionId')].inputs" to "$"
          )
        )
      }
      Step("Assert inputs from action alone") {
        JsonCompareAction(
          document1 = "action".spEL,
          document2 = inputs,
          comparingPaths = mapOf(
            "$.inputs" to "$[0]"
          )
        )
      }
    }
  }
}