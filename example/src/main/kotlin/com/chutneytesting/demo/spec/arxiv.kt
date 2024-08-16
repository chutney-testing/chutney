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
        ArxivSpecs.synchronize(this.CHUTNEY_DEMO, this.ENVIRONMENT_DEMO)
    }
}

object ArxivSpecs {
    private const val TAG = "ARXIV"
    private const val TARGET = "ARXIV"

    fun synchronize(serverInfo: ChutneyServerInfo, env: String) {
        serverInfo.apply {
            allScenarios().forEach { it.synchronise(this) }
            campaign(env).synchronise(this)
        }
    }

    fun campaign(env: String) =
        Campaign(
            id = 667500,
            title = "ARXIV - All scenarios",
            environment = env,
            retryAuto = true,
            tags = listOf(TAG),
            scenarios = allScenarios().map { Campaign.CampaignScenario(scenarioId = it.id!!) }
        )

    fun allScenarios() = listOf(
        search_response_is_valid,
        software_testing_articles
    )

    val search_response_is_valid =
        Scenario(
            id = 667000,
            title = "ARXIV - Search response is valid",
            tags = listOf(TAG)
        ) {
            Given("Given a random search word") {
                ContextPutAction(
                    entries = mapOf(
                        "searchWordList" to "{'electron', 'proton', 'llm', 'database', 'entropy', 'computer', 'algorithm', 'ecology', 'neural'}".elEval()
                    )
                )
            }
            When("When search for articles") {
                HttpPostAction(
                    target = TARGET,
                    uri = "/query",
                    headers = mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded"
                    ),
                    body = """
                        search_query=all:${"$"}{#searchWordList.get(#generate().randomInt(#searchWordList.size()))}
                    """.trimIndent(),
                    validations = mapOf(
                        httpStatusOK()
                    )
                )
            }
            Then("Then response feed is valid") {
                XsdValidationAction(
                    xml = "body".spEL(),
                    xsdPath = "file:///schema/atom/atom.xsd"
                )
            }
        }

    val software_testing_articles =
        Scenario(
            id = 667001,
            title = "ARXIV - Software testing is a subject",
            tags = listOf(TAG)
        ) {
            When("Searching for articles about testing in cs.SE category") {
                HttpPostAction(
                    target = TARGET,
                    uri = "/query",
                    headers = mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded"
                    ),
                    body = """
                        search_query=cat:cs.SE&abs:testing
                    """.trimIndent(),
                    validations = mapOf(
                        httpStatusOK()
                    )
                )
            }
            Then("Some articles are found") {
                XmlAssertAction(
                    document = "body".spEL(),
                    expected = mapOf(
                        "//totalResults" to "\$isGreaterThan:10"
                    )
                )
            }
        }
}
