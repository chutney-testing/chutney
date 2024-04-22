/*
 *  Copyright 2017-2023 Enedis
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

package com.chutneytesting.example

import com.chutneytesting.kotlin.dsl.Campaign
import com.chutneytesting.kotlin.synchronize.synchronise
import com.chutneytesting.kotlin.util.ChutneyServerInfo

fun main() {
    val campaign = Campaign(
        id = 666,
        title = "MGN FROM KOTLIN",
        description = "Une putain de campagne !!",
        environment = "DEFAULT",
        scenarios = listOf(
            Campaign.CampaignScenario(5, "MGN_1"),
            Campaign.CampaignScenario(3),
            Campaign.CampaignScenario(2, "MGN_2")
        ),
        tags = listOf("666")
    )

    val remote = ChutneyServerInfo(url = "https://localhost:8443", user = "admin", password = "admin")
    campaign.synchronise(serverInfo = remote)
}
