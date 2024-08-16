/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.demo.spec

import com.chutneytesting.demo.spec.ValidationHelper.httpStatusOK
import com.chutneytesting.demo.sync.DemoServer
import com.chutneytesting.kotlin.dsl.*
import com.chutneytesting.kotlin.synchronize.synchronise
import com.chutneytesting.kotlin.util.ChutneyServerInfo

fun main() {
    DemoServer.apply {
        SWAPISpecs.synchronize(this.CHUTNEY_DEMO, this.ENVIRONMENT_DEMO)
    }
}

object SWAPISpecs {
    private const val TAG = "SWAPI"
    private const val TARGET = "SWAPI"

    fun synchronize(serverInfo: ChutneyServerInfo, env: String) {
        serverInfo.apply {
            allScenarios().forEach { it.synchronise(this) }
            campaign(env).synchronise(this)
        }
    }

    fun campaign(env: String) =
        Campaign(
            id = 666500,
            title = "SWAPI - All scenarios",
            environment = env,
            retryAuto = true,
            tags = listOf(TAG),
            scenarios = allScenarios().map { Campaign.CampaignScenario(scenarioId = it.id!!) }
        )

    fun allScenarios() = listOf(
        root_list_all_resources,
        people_json_validation
    )

    val root_list_all_resources =
        Scenario(
            id = 666000,
            title = "SWAPI - The Root resource provides information on all available resources",
            tags = listOf(TAG)
        ) {
            When("When request SWAPI root") {
                HttpGetAction(
                    target = TARGET,
                    uri = "/",
                    validations = mapOf(
                        httpStatusOK()
                    )
                )
            }
            Then("Then all resources are listed") {
                JsonAssertAction(
                    document = "body".spEL(),
                    expected = mapOf(
                        "$.films" to "\$isNotNull",
                        "$.people" to "\$isNotNull",
                        "$.planets" to "\$isNotNull",
                        "$.species" to "\$isNotNull",
                        "$.starships" to "\$isNotNull",
                        "$.vehicles" to "\$isNotNull"
                    )
                )
            }
        }

    val people_json_validation =
        Scenario(
            id = 666100,
            title = "SWAPI - All people resources are valid",
            tags = listOf(TAG)
        ) {
            Given("Given the total number of people in SWAPI") {
                Step("Request all people") {
                    HttpGetAction(
                        target = TARGET,
                        uri = "/people",
                        validations = mapOf(
                            httpStatusOK()
                        ),
                        outputs = mapOf(
                            "peopleCount" to "jsonPath(#body, '$.count')".spEL(),
                            "peopleByPage" to "jsonPath(#body, '$.results.length()')".spEL(),
                            "peopleIterationNumber" to "T(java.lang.Math).floor(#peopleCount/#peopleByPage)".elEval()
                        )
                    )
                }
                Step("Build iteration dataset") {
                    ContextPutAction(
                        entries = mapOf(
                            "peopleIterationDataset" to "jsonPath('[' + '{},'.repeat(#peopleIterationNumber) + '{}]', '$')".spEL(),
                            "peopleList" to "jsonPath('[]', '$')".spEL()
                        )
                    )
                }
            }
            When("When request all SWAPI people (page <i>)") {
                HttpGetAction(
                    target = TARGET,
                    uri = "/people?page=\${<i>+1}",
                    validations = mapOf(
                        httpStatusOK()
                    ),
                    outputs = mapOf(
                        "n/a" to "peopleList.addAll(#jsonPath(#body, '$.results'))".spEL()
                    ),
                    strategy = ForStrategy(dataset = "peopleIterationDataset".spEL())
                )
            }
            Then("Then all people are valid") {
                Step("Get swapi people json schema") {
                    ContextPutAction(
                        entries = mapOf(
                            "peopleSchema" to "resourceContent('swapi/swapi-people.json', null)".spEL()
                        )
                    )
                }
                Step("Validate \${#name}") {
                    JsonValidationAction(
                        schema = "peopleSchema".spEL(),
                        json = "peopleList.get(<i>)".spEL()
                    )
                    strategy = ForStrategy(dataset = "peopleList".spEL())
                }
            }
        }
}
