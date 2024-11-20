/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests

import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.emptyScenario
import com.chutneytesting.acceptance.common.jsonHeader
import com.chutneytesting.kotlin.dsl.*

val executeCampaignById = Scenario(title = "Execute campaign by id") {
  createCampaignWith2Scenarios()
  When("this campaign is executed by id") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/execution/v1/byID/${"campaignId".spEL}",
      headers = jsonHeader(),
      body = null,
      validations = mapOf(statusValidation(200)),
      outputs = mapOf("report" to "#body".elEval())
    )
  }
  checkCampaignExecution()
}

val executeCampaignByName = Scenario(title = "Execute campaign name") {
  Given("A campaign name") {
    ContextPutAction(
      entries = mapOf("campaignName" to "generate().id('campaign-', 10)".spEL())
    )
  }
  createCampaignWith2Scenarios("campaignName".spEL())
  When("this campaign is executed by name") {
    HttpGetAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/execution/v1/" + "campaignName".spEL(),
      headers = jsonHeader(),
      validations = mapOf(statusValidation(200)),
      outputs = mapOf(
        "report" to "json(#body, '$[0]')".spEL()
      )
    )
  }
  checkCampaignExecution()
}

val executeForSurefireReport = Scenario(title = "Execution for surefire of a campaign") {
  Given("An unknown campaign name") {
    ContextPutAction(
      entries = mapOf("campaignName" to "generate().id('campaign-', 10)".spEL())
    )
  }
  When("this campaign is executed by name") {
    HttpGetAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/execution/v1/\" + \"campaignName\".spEL() + /surefire",
      headers = jsonHeader(),
      validations = mapOf(statusValidation(200)),
      outputs = mapOf(
        "surfireResponseHeader" to "headers".spEL()
      )
    )
  }
  Then("the response is a non empty zip file") {
    Step("Check content type") {
      CompareAction(
        mode = "equals",
        actual = "surfireResponseHeader.getContentType().toString()".spEL(),
        expected = "application/zip"
      )
    }
    Step("Check content disposition") {
      CompareAction(
        mode = "contains",
        actual = "surfireResponseHeader.getContentDisposition().toString()".spEL(),
        expected = "attachment; filename=\"surefire-report.zip\""
      )
    }
    Step("Check content length") {
      CompareAction(
        mode = "greater than",
        actual = "T(Long).toString(#surfireResponseHeader.getContentLength())".elEval(),
        expected = "20"
      )
    }
  }
}

val unknownCampaignById = Scenario(title = "Execution by id of an unknown campaign") {
  Given("An unknown id") {
    ContextPutAction(
      entries = mapOf("campaignId" to "generate().randomLong()".spEL())
    )
  }
  When("an unknown campaign is executed by id and not found") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/execution/v1/byID/${"campaignId".spEL}",
      headers = jsonHeader(),
      body = null,
      validations = mapOf(
        statusValidation(404)
      ),
    )
  }
}

val unknownCampaignByName = Scenario(title = "Execution by name of an unknown campaign") {
  Given("An unknown id") {
    ContextPutAction(
      entries = mapOf("campaignName" to "generate().id('campaign-', 10)".spEL())
    )
  }
  When("an unknown campaign is executed by name") {
    HttpGetAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/execution/v1/" + "campaignName".spEL(),
      headers = jsonHeader(),
      validations = mapOf(statusValidation(200)),
      outputs = mapOf(
        "report" to "body".spEL()
      )
    )
  }
  Then("the campaign report is empty") {
    CompareAction(
      mode = "equals",
      actual = "report".spEL(),
      expected = "[]"
    )
  }
}

fun createUpdateCampaignWithSpecificId(campaignId: Int) = Scenario(title = "Create/Update campaign with specific id") {
  Given("Create two scenarios") {
    Step("Create first scenario") {
      createScenario("scenarioIdA", emptyScenario)
    }
    Step("Create second scenario") {
      createScenario("scenarioIdB", emptyScenario)
    }
  }
  When("Campaign is created") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/v1",
      body = """
            {
                "id": $campaignId,
                "title": "My campaign",
                "description": "",
                "scenarios": [{"scenarioId": "${"scenarioIdA".spEL()}"}],
                "environment": "DEFAULT",
                "parallelRun": false,
                "retryAuto": false,
                "datasetId": null,
                "tags": []
            }
            """.trimIndent(),
      headers = jsonHeader(),
      validations = mapOf(
        statusValidation(200),
        "id" to "jsonPath(#body, '$.id') == $campaignId".spEL(),
        "scenario" to "jsonPath(#body, '$.scenarios[0].scenarioId') == #scenarioIdA".spEL()
      )
    )
  }
  Then("Campaign is updated") {
    HttpPostAction(
      target = "CHUTNEY_LOCAL",
      uri = "/api/ui/campaign/v1",
      body = """
            {
                "id": $campaignId,
                "title": "My new campaign",
                "description": "My new campaign description",
                "scenarios": [{"scenarioId": "${"scenarioIdB".spEL()}"}],
                "environment": "DEFAULT",
                "parallelRun": false,
                "retryAuto": false,
                "datasetId": null,
                "tags": ["A_TAG"]
            }
            """.trimIndent(),
      headers = jsonHeader(),
      validations = mapOf(
        statusValidation(200)
      )
    )
  }
  And("Verify campaign") {
    Step("Get the campaign") {
      HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/v1/$campaignId",
        headers = jsonHeader(),
        validations = mapOf(
          statusValidation(200)
        )
      )
    }
    Step("Assert campaign attributes") {
      JsonAssertAction(
        document = "body".spEL(),
        expected = mapOf(
          "$.id" to "$campaignId",
          "$.title" to "My new campaign",
          "$.description" to "My new campaign description",
          "$.scenarios[0].scenarioId" to "scenarioIdB".spEL(),
          "$.tags[0]" to "A_TAG"
        )
      )
    }
  }
}

private fun ChutneyScenarioBuilder.checkCampaignExecution() {
  Then("We verify the report") {
    Step("Check execution id not empty") {
      CompareAction(
        mode = "greater-than",
        actual = "json(#report, '$.executionId').toString()".spEL(),
        expected = "0"
      )
    }
    Step("Check status is SUCCESS") {
      CompareAction(
        mode = "equals",
        actual = "json(#report, '$.status')".spEL(),
        expected = "SUCCESS"
      )
    }
    Step("Check scenario ids") {
      CompareAction(
        mode = "equals",
        actual = "json(#report, '$.scenarioExecutionReports[*].scenarioId').toString()".spEL(),
        expected = "[\"${"scenario1Id".spEL}\",\"${"scenario2Id".spEL}\"]"
      )
    }
  }
  And("this execution report is stored in the campaign execution history") {
    Step("Request campaign from Chutney instance") {
      HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/v1/${"campaignId".spEL}",
        headers = jsonHeader(),
        validations = mapOf(
          statusValidation(200)
        ),
        outputs = mapOf("campaign" to "body".spEL())
      )
    }
    Step("Assert execution is present") {
      CompareAction(
        mode = "equals",
        actual = "json(#campaign, '$.campaignExecutionReports[0].executionId').toString()".spEL(),
        expected = "json(#report, '$.executionId').toString()".spEL()
      )
    }
  }
}

private fun ChutneyScenarioBuilder.createCampaignWith2Scenarios(campaignName: String = "generate().id('campaign-', 10)".spEL()) {
  Given("Create a campaign with 2 scenario") {
    Step("Create first scenario") {
      createScenario("scenario1Id")
    }
    Step("Create second scenario") {
      createScenario("scenario2Id")
    }
    Step("Create campaign with 2 scenario") {
      createCampaign(campaignName)
    }
  }
}

private fun ChutneyStepBuilder.createCampaign(campaignName: String = "campaign") {
  HttpPostAction(
    target = "CHUTNEY_LOCAL",
    uri = "/api/ui/campaign/v1",
    body = """
                {
                    "title":"$campaignName",
                    "description":"",
                    "scenarios":[ {"scenarioId": "${"scenario1Id".spEL}"}, {"scenarioId": "${"scenario2Id".spEL}", "datasetId": null} ],
                    "environment":"DEFAULT",
                    "parallelRun": false,
                    "retryAuto": false,
                    "tags":[]
                }
                """.trimIndent(),
    headers = jsonHeader(),
    validations = mapOf(statusValidation(200)),
    outputs = mapOf(
      "campaignId" to "jsonPath(#body, '$.id')".spEL()
    )
  )
}
