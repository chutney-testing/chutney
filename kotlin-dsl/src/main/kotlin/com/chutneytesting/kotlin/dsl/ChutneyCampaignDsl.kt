/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.dsl

data class Campaign(
    val id: Int? = null,
    val title: String,
    val description: String? = null,
    val environment: String,
    val parallelRun: Boolean = false,
    val retryAuto: Boolean = false,
    val datasetId: String? = null,
    val scenarios: List<CampaignScenario> = emptyList(),
    val tags: List<String> = emptyList()
) {
    data class CampaignScenario(val scenarioId: Int, val datasetId: String? = null)
}
