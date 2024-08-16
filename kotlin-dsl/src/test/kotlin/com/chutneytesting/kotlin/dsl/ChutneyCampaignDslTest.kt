/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChutneyCampaignDslTest {
    @Test
    fun instantiate_campaign_with_title() {
        val sut = Campaign(title = "Campaign title", environment = "DEV")
        assertThat(sut)
            .hasFieldOrPropertyWithValue("id", null)
            .hasFieldOrPropertyWithValue("title", "Campaign title")
            .hasFieldOrPropertyWithValue("description", null)
            .hasFieldOrPropertyWithValue("environment", "DEV")
            .hasFieldOrPropertyWithValue("parallelRun", false)
            .hasFieldOrPropertyWithValue("retryAuto", false)
            .hasFieldOrPropertyWithValue("datasetId", null)
            .hasFieldOrPropertyWithValue("scenarios", emptyList<Any>())
            .hasFieldOrPropertyWithValue("tags", emptyList<Any>())
    }
}
