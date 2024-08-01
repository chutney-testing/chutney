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

package com.chutneytesting.kotlin.synchronize.server

import com.chutneytesting.kotlin.dsl.Campaign
import com.chutneytesting.kotlin.dsl.Campaign.CampaignScenario
import com.chutneytesting.kotlin.synchronize.ChutneyServerServiceImpl
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery.forStubMapping
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignsTest : ChutneyServerServiceImplTest() {
    @Test
    fun new_id_from_remote() {
        // Given
        val campaign = Campaign(title = "Campaign title", environment = "DEV", tags = listOf("TAG_1", "TAG_2"))
        val newIdFromRemote = 666

        val createStub = wireMockServer.stubFor(
            post(urlPathMatching("/api/ui/campaign/v1"))
                .willReturn(
                    okJson(
                        """
                                {
                                  "id": $newIdFromRemote,
                                  "title": "${campaign.title}",
                                  "description": "",
                                  "scenarios": [],
                                  "campaignExecutionReports": [],
                                  "environment": "DEV",
                                  "parallelRun": false,
                                  "retryAuto": false,
                                  "tags": ["TAG_1", "TAG_2"]
                                }
                            """.trimIndent()
                    )
                )
        )

        // When
        val id = ChutneyServerServiceImpl.createOrUpdateCampaign(
            buildServerInfo(),
            campaign
        )

        // Then
        assertThat(wireMockServer.allServeEvents).hasSize(1)
        wireMockServer.verify(1, postRequestedFor(createStub.request.urlMatcher))
        assertThat(id).isEqualTo(newIdFromRemote)

        val createRequestReceived = serverEventRequestBodyAsJson(
            wireMockServer.getServeEvents(forStubMapping(createStub.id)).requests[0]
        )
        assertThat(createRequestReceived.get("id")).isNull()
        assertThat(createRequestReceived.get("title").textValue()).isEqualTo(campaign.title)
        assertThat(createRequestReceived.get("environment").textValue()).isEqualTo(campaign.environment)
        assertThat(createRequestReceived.get("parallelRun").booleanValue()).isEqualTo(campaign.parallelRun)
        assertThat(createRequestReceived.get("retryAuto").booleanValue()).isEqualTo(campaign.retryAuto)
        assertThat(createRequestReceived.get("scenarios")).isEmpty()
        assertThat(createRequestReceived.get("tags")).hasSize(2)
            .map<String> { jsonNode -> jsonNode.textValue() }
            .containsExactly("TAG_1", "TAG_2")
    }

    @Test
    fun id_from_code() {
        // Given
        val campaign = Campaign(
            id = 666,
            title = "Campaign title",
            description = "Campaign description",
            scenarios = listOf(
                CampaignScenario(111, "dataset_1"),
                CampaignScenario(666),
                CampaignScenario(8888, "dataset_2")
            ),
            environment = "STAGING",
            parallelRun = true,
            retryAuto = true,
            datasetId = "DATASET",
            tags = listOf("TAG_1", "TAG_2")
        )

        val createStub = wireMockServer.stubFor(
            post(urlPathMatching("/api/ui/campaign/v1"))
                .willReturn(
                    okJson(
                        """
                                {
                                  "id": ${campaign.id},
                                  "title": "${campaign.title}",
                                  "description": "",
                                  "scenarios": [{"scenarioId": "111", "datasetId": "dataset_1"}, {"scenarioId": "666"}, {"scenarioId": "8888", "datasetId": "dataset_2"}],
                                  "campaignExecutionReports": [],
                                  "datasetId": "${campaign.datasetId}",
                                  "environment": "${campaign.environment}",
                                  "parallelRun": ${campaign.parallelRun},
                                  "retryAuto": ${campaign.retryAuto},
                                  "tags": ["TAG_1", "TAG_2"]
                                }
                            """.trimIndent()
                    )
                )
        )

        // When
        val id = ChutneyServerServiceImpl.createOrUpdateCampaign(
            buildServerInfo(),
            campaign
        )

        // Then
        assertThat(wireMockServer.allServeEvents).hasSize(1)
        wireMockServer.verify(1, postRequestedFor(createStub.request.urlMatcher))
        assertThat(id).isEqualTo(campaign.id)

        val createRequestReceived = serverEventRequestBodyAsJson(
            wireMockServer.getServeEvents(forStubMapping(createStub.id)).requests[0]
        )
        assertThat(createRequestReceived.get("id").intValue()).isEqualTo(campaign.id)
        assertThat(createRequestReceived.get("title").textValue()).isEqualTo(campaign.title)
        assertThat(createRequestReceived.get("environment").textValue()).isEqualTo(campaign.environment)
        assertThat(createRequestReceived.get("parallelRun").booleanValue()).isEqualTo(campaign.parallelRun)
        assertThat(createRequestReceived.get("retryAuto").booleanValue()).isEqualTo(campaign.retryAuto)
        assertThat(createRequestReceived.get("scenarios")).hasSize(3)
            .map<CampaignScenario> { jsonNode ->
                CampaignScenario(jsonNode.get("scenarioId").textValue().toInt(), jsonNode.get("datasetId")?.textValue())
            }
            .containsExactlyElementsOf(campaign.scenarios)
        assertThat(createRequestReceived.get("tags")).hasSize(2)
            .map<String> { jsonNode -> jsonNode.textValue() }
            .containsExactlyInAnyOrderElementsOf(campaign.tags)
    }
}
