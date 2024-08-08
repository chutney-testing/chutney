/*
 *  Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
