/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests

import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.kotlin.dsl.*

val executeCampaignById = Scenario(title = "Execute campaign by id") {
  createCampaignWith2Scenarios()
  When("this campaign is executed by id") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/execution/v1/byID/${'$'}{#campaignId}",
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "report" to "#body".elEval()
        )
    )
  }
  checkCampaignExecution()
}

val executeCampaignByName = Scenario(title = "Execute campaign name") {
  Given("A campaign name") {
    ContextPutAction(
        entries = mapOf(
            "campaignName" to "generate().id(\"campaign-\", 10)".spEL()
        )
    )
  }
  createCampaignWith2Scenarios("campaignName".spEL())
  When("this campaign is executed by name") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/execution/v1/" + "campaignName".spEL(),
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
        outputs = mapOf(
            "report" to "json(#body, \"${'$'}[0]\")".spEL()
        )
    )
  }
  checkCampaignExecution()
}

val executeForSurefireReport = Scenario(title = "Execution for surefire of a campaign") {
  Given("An unknown campaign name") {
    ContextPutAction(
        entries = mapOf(
            "campaignName" to "generate().id(\"campaign-\", 10)".spEL()
        )
    )
  }
  When("this campaign is executed by name") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/execution/v1/\" + \"campaignName\".spEL() + /surefire",
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
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
        entries = mapOf(
            "campaignId" to "generate().randomLong()".spEL()
        )
    )
  }
  When("an unknown campaign is executed by id and not found") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/execution/v1/byID/${'$'}{#campaignId}",
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8"
        ),
        validations = mapOf(
            statusValidation(404)
        ),
    )
  }
}

val unknownCampaignByName = Scenario(title = "Execution by name of an unknown campaign") {
  Given("An unknown id") {
    ContextPutAction(
        entries = mapOf(
            "campaignName" to "generate().id(\"campaign-\", 10)".spEL()
        )
    )
  }
  When("an unknown campaign is executed by name") {
    HttpGetAction(
        target = "CHUTNEY_LOCAL",
        uri = "/api/ui/campaign/execution/v1/" + "campaignName".spEL(),
        headers = mapOf(
            "Content-Type" to "application/json;charset=UTF-8"
        ),
        validations = mapOf(
            statusValidation(200)
        ),
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
private fun ChutneyScenarioBuilder.checkCampaignExecution() {
  Then("We verify the report") {
    Step("Check execution id not empty") {
      CompareAction(
          mode = "greater-than",
          actual = "json(#report, \"${'$'}.executionId\").toString()".spEL(),
          expected = "0"
      )
    }
    Step("Check status is SUCCESS") {
      CompareAction(
          mode = "equals",
          actual = "json(#report, \"${'$'}.status\")".spEL(),
          expected = "SUCCESS"
      )
    }
    Step("Check scenario ids") {
      CompareAction(
          mode = "equals",
          actual = "#json(#report, \"${'$'}.scenarioExecutionReports[*].scenarioId\").toString()".elEval(),
          expected = "[\"${'$'}{#scenario1Id}\",\"${'$'}{#scenario2Id}\"]"
      )
    }
  }
  And("this execution report is stored in the campaign execution history") {
    Step("Request campaign from Chutney instance") {
      HttpGetAction(
          target = "CHUTNEY_LOCAL",
          uri = "/api/ui/campaign/v1/${'$'}{#campaignId}",
          headers = mapOf(
              "Content-Type" to "application/json;charset=UTF-8"
          ),
          validations = mapOf(
              statusValidation(200)
          ),
          outputs = mapOf(
              "campaign" to "body".spEL()
          )
      )
    }
    Step("Assert execution is present") {
      CompareAction(
          mode = "equals",
          actual = "json(#campaign, \"${'$'}.campaignExecutionReports[0].executionId\").toString()".spEL(),
          expected = "json(#report, \"${'$'}.executionId\").toString()".spEL()
      )
    }
  }
}

private fun ChutneyScenarioBuilder.createCampaignWith2Scenarios(campaignName: String = "generate().id(\"campaign-\", 10)".spEL()) {
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
                    "scenarios":[ {"scenarioId": "${'$'}{#scenario1Id}"}, {"scenarioId": "${'$'}{#scenario2Id}", "datasetId": null} ],
                    "environment":"DEFAULT",
                    "parallelRun": false,
                    "retryAuto": false,
                    "tags":[]
                }
                """.trimIndent(),
      headers = mapOf(
          "Content-Type" to "application/json;charset=UTF-8"
      ),
      validations = mapOf(
          statusValidation(200)
      ),
      outputs = mapOf(
          "campaignId" to "jsonPath(#body, \"${'$'}.id\")".spEL()
      )
  )
}

