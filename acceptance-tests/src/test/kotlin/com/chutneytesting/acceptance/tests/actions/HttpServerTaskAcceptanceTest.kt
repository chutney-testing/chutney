/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.checkScenarioReportSuccess
import com.chutneytesting.acceptance.common.createEnvironment
import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.hjsonSpEL
import com.chutneytesting.kotlin.dsl.spEL


val `Http post request local server endpoint` = Scenario(title = "Http post request local server endpoint") {
  Given("a target for the http server mock") {
    createEnvironment(
      "HTTP_SERVER_ENV",
      """
        [
            {
                "name": "test_http",
                "url": "https://localhost:11789",
                "properties": [
                    { "key" : "keyStore", "value": "/config/keystores/client.jks" },
                    { "key" : "keyStorePassword", "value": "client" },
                    { "key" : "keyPassword", "value": "client" }
                ]
            }
        ]
      """.trimIndent()
    )
  }
  And("This scenario is saved") {
    createScenario(
      "scenarioId",
      """
        {
            "givens":[
                {
                    "sentence":"Start HTTPS server",
                    "implementation":{
                        "task":"{\n type: https-server-start \n inputs: {\n port: \"11789\" \n truststore-path: \"/config/keystores/truststore.jks\" \n truststore-password: truststore \n}\n}"
                    }
                }
            ],
            "when":{
                "sentence":"Make POST request",
                "implementation":{
                    "task":"{\n type: http-post \n target: test_http \n inputs: {\n uri: /test \n body: cool buddy \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Listens to POST requests",
                    "implementation":{
                        "task":"{\n type: https-listener \n inputs: {\n https-server: ${"httpsServer".hjsonSpEL} \n uri: /test \n verb: POST \n expected-message-count: \"1\" \n} \n}"
                    }
                },
                {
                    "implementation":{
                        "task":"{\n type: debug \n}"
                    }
                }
            ]
        }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("scenarioId".spEL, "HTTP_SERVER_ENV")
  }
  Then("the report status is SUCCESS") {
    checkScenarioReportSuccess()
  }
}