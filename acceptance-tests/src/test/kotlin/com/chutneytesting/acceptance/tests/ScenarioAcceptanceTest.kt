/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests

import com.chutneytesting.acceptance.common.jsonHeader
import com.chutneytesting.kotlin.dsl.*

fun createUpdateScenarioWithSpecificId(scenarioId: Int) = Scenario(title = "Create/Update scenario with specific id") {
  When("Scenario is created") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/scenario/v2/raw",
      body = """
            {
                "id": "$scenarioId",
                "title": "My scenario",
                "content": "{\"when\": {}}",
                "description": "My scenario description",
                "tags": [],
                "defaultDataset": null
            }
            """.trimIndent(),
      headers = jsonHeader(),
      validations = mapOf(
        statusValidation(200),
        "id" to "jsonPath(#body, '$') == $scenarioId".spEL()
      )
    )
  }
  Then("Scenario is updated") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/scenario/v2/raw",
      body = """
            {
                "id": "$scenarioId",
                "title": "My new title",
                "content": "{\"when\": {}}",
                "description": "My new scenario description",
                "tags": ["A_TAG"],
                "defaultDataset": null,
                "version": 1
            }
            """.trimIndent(),
      headers = jsonHeader(),
      validations = mapOf(
        statusValidation(200),
        "id" to "jsonPath(#body, '$') == $scenarioId".spEL()
      )
    )
  }
  And("Verify scenario") {
    Step("Get the scenario") {
      HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/scenario/v2/raw/$scenarioId",
        headers = jsonHeader(),
        validations = mapOf(
          statusValidation(200)
        )
      )
    }
    Step("Assert scenario attributes") {
      JsonAssertAction(
        document = "body".spEL(),
        expected = mapOf(
          "$.id" to "$scenarioId",
          "$.title" to "My new title",
          "$.description" to "My new scenario description",
          "$.tags[0]" to "A_TAG"
        )
      )
    }
  }
}