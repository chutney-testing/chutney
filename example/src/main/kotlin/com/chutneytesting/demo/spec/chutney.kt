/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.demo.spec

import com.chutneytesting.demo.sync.DemoServer
import com.chutneytesting.kotlin.dsl.*
import com.chutneytesting.kotlin.synchronize.synchronise
import com.chutneytesting.kotlin.util.ChutneyServerInfo

fun main() {
    DemoServer.apply {
        ChutneyDBSpecs.synchronize(this.CHUTNEY_DEMO, this.ENVIRONMENT_DEMO)
    }
}

object ChutneyDBSpecs {
    private const val TAG = "CHUTNEY"
    private const val TARGET = "CHUTNEY_DB"

    fun synchronize(serverInfo: ChutneyServerInfo, env: String) {
        serverInfo.apply {
            allScenarios().forEach { it.synchronise(this) }
            campaign(env).synchronise(this)
        }
    }

    fun campaign(env: String) =
        Campaign(
            id = 665500,
            title = "CHUTNEY - All scenarios",
            environment = env,
            retryAuto = true,
            tags = listOf(TAG),
            scenarios = allScenarios().map { Campaign.CampaignScenario(scenarioId = it.id!!) }
        )

    fun allScenarios() = listOf(
        scenarios_exist,
        last_execution_of_each_scenario_is_success
    )

    val scenarios_exist =
        Scenario(
            id = 665000,
            title = "CHUTNEY - It exists some scenarios to execute",
            tags = listOf(TAG)
        ) {
            When("When count all the scenarios in db") {
                SqlAction(
                    target = TARGET,
                    statements = listOf(
                        "SELECT count(id) as count FROM scenario"
                    )
                )
            }
            Then("Then at least one exists") {
                AssertAction(
                    asserts = listOf(
                        "firstRow.get('count') > 0".spEL()
                    )
                )
            }
        }

    val last_execution_of_each_scenario_is_success =
        Scenario(
            id = 665001,
            title = "CHUTNEY - Each scenario's last execution is in success",
            tags = listOf(TAG)
        ) {
            When("When requesting for scenarios' last executions") {
                SqlAction(
                    target = TARGET,
                    statements = listOf(
                        """
                            SELECT scenario_id, max(id) as last_execution_id
                            FROM scenario_executions
                            WHERE status != 'RUNNING'
                              AND scenario_id != ${this@Scenario.id}
                            GROUP BY scenario_id
                        """.trimIndent()
                    )
                )
            }
            Then("Then the executions' status are SUCCESS - ${"last_execution_id".spEL()}") {
                SqlAction(
                    target = TARGET,
                    statements = listOf(
                        "SELECT status FROM scenario_executions WHERE id = ${"last_execution_id".spEL()}"
                    ),
                    validations = mapOf(
                        "status SUCCESS" to "'SUCCESS'.equals(#firstRow.get('status'))".elEval()
                    ),
                    strategy = ForStrategy(dataset = "recordResult.get(0).rows.asMap()".spEL())
                )
            }
        }
}
