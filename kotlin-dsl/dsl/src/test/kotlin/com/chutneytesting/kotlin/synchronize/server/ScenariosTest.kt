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

package com.chutneytesting.kotlin.synchronize.server

import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction
import com.chutneytesting.kotlin.synchronize.ChutneyServerServiceImpl
import com.fasterxml.jackson.databind.JsonNode
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery.forStubMapping
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ScenariosTest : ChutneyServerServiceImplTest() {
    @Test
    fun get_all_scenarios() {
        wireMockServer.stubFor(
            get(urlPathMatching("/api/scenario/v2"))
                .willReturn(
                    okJson("[]")
                )
        )

        val scenarios = ChutneyServerServiceImpl.getAllScenarios(buildServerInfo())

        wireMockServer.verify(
            1,
            getRequestedFor(urlPathMatching("/api/scenario/v2"))
        )
        assertThat(scenarios).isEmpty()
    }

    @Nested
    inner class NewRemoteScenario {
        @Test
        fun new_id_from_remote() {
            // Given
            val scenario = Scenario(title = "A scenario") {
                When("Something happens") {
                    SuccessAction()
                }
            }

            val createStub = wireMockServer.stubFor(
                post(urlPathMatching("/api/scenario/v2/raw"))
                    .willReturn(
                        okJson("123")
                    )
            )

            // When
            val newId = ChutneyServerServiceImpl.createOrUpdateJsonScenario(
                buildServerInfo(),
                scenario
            )

            // Then
            assertThat(wireMockServer.allServeEvents).hasSize(1)
            assertThat(newId).isEqualTo(123)

            val createRequestReceived = serverEventRequestBodyAsJson(
                wireMockServer.getServeEvents(forStubMapping(createStub.id)).requests[0]
            )
            assertThat(createRequestReceived.get("id")).isNull()
            assertThat(createRequestReceived.get("title").textValue()).isEqualTo(scenario.title)
            assertThat(createRequestReceived.get("description").textValue()).isEqualTo(scenario.description)
            assertThat(createRequestReceived.get("content").textValue()).isNotBlank()
            assertThat(createRequestReceived.get("tags")).isEmpty()
            assertThat(createRequestReceived.get("defaultDataset").isNull).isTrue()
        }

        @Test
        fun id_from_code() {
            // Given
            val scenario = Scenario(id = 666, title = "A scenario", tags = listOf("TAG")) {
                When("Something happens") {
                    SuccessAction()
                }
            }

            val checkStub = wireMockServer.stubFor(
                get(urlPathMatching("/api/scenario/v2/raw/${scenario.id}"))
                    .willReturn(
                        aResponse()
                            .withStatus(404)
                    )
            )

            val createStub = wireMockServer.stubFor(
                post(urlPathMatching("/api/scenario/v2/raw"))
                    .willReturn(
                        okJson("${scenario.id}")
                    )
            )

            // When
            val id = ChutneyServerServiceImpl.createOrUpdateJsonScenario(
                buildServerInfo(),
                scenario
            )

            // Then
            assertThat(wireMockServer.allServeEvents).hasSize(2)
            wireMockServer.verify(1, getRequestedFor(checkStub.request.urlMatcher))
            assertThat(id).isEqualTo(scenario.id)

            val createRequestReceived = serverEventRequestBodyAsJson(
                wireMockServer.getServeEvents(forStubMapping(createStub.id)).requests[0]
            )
            assertThat(createRequestReceived.get("id").textValue()).isEqualTo("${scenario.id}")
            assertThat(createRequestReceived.get("title").textValue()).isEqualTo(scenario.title)
            assertThat(createRequestReceived.get("description").textValue()).isEqualTo(scenario.description)
            assertThat(createRequestReceived.get("content").textValue()).isNotBlank()
            assertThat(createRequestReceived.get("tags")[0].textValue()).isEqualTo("TAG")
            assertThat(createRequestReceived.get("defaultDataset").isNull).isTrue()
        }
    }

    @Nested
    inner class ExistingRemoteScenario {
        @Test
        fun update_from_code() {
            // Given
            val scenario = Scenario(
                id = 666,
                title = "A new scenario",
                defaultDataset = "NEW_DATASET",
                tags = listOf("STAG_1", "STAG_2")
            ) {
                description = "MyDescription"
                When("Something happens update") {
                    SuccessAction()
                }
            }

            val checkStub = wireMockServer.stubFor(
                get(urlPathMatching("/api/scenario/v2/raw/${scenario.id}"))
                    .willReturn(
                        okJson(
                            """
                                {
                                    "id": "${scenario.id}",
                                    "version": 1,
                                    "title": "title",
                                    "tags": ["TAG1", "TAG2"],
                                    "defaultDataset": "MY_DATASET",
                                    "content": "{...}"
                                }
                            """.trimIndent()
                        )
                    )
            )

            val updateStub = wireMockServer.stubFor(
                post(urlPathMatching("/api/scenario/v2/raw"))
                    .willReturn(
                        okJson("${scenario.id}")
                    )
            )

            // When
            val id = ChutneyServerServiceImpl.createOrUpdateJsonScenario(
                buildServerInfo(),
                scenario
            )

            // Then
            assertThat(wireMockServer.allServeEvents).hasSize(2)
            wireMockServer.verify(1, getRequestedFor(checkStub.request.urlMatcher))
            assertThat(id).isEqualTo(scenario.id)

            val createRequestReceived = serverEventRequestBodyAsJson(
                wireMockServer.getServeEvents(forStubMapping(updateStub.id)).requests[0]
            )
            assertThat(createRequestReceived.get("id").textValue()).isEqualTo("${scenario.id}")
            assertThat(createRequestReceived.get("version").intValue()).isEqualTo(1)
            assertThat(createRequestReceived.get("title").textValue()).isEqualTo(scenario.title)
            assertThat(createRequestReceived.get("description").textValue()).isEqualTo(scenario.description)
            assertThat(createRequestReceived.get("content").textValue()).isNotBlank()
            assertThat(createRequestReceived.get("tags"))
                .extracting(JsonNode::textValue)
                .containsExactlyInAnyOrder(tuple("STAG_1"), tuple("STAG_2"), tuple("TAG1"), tuple("TAG2"))
            assertThat(createRequestReceived.get("defaultDataset").textValue()).isEqualTo(scenario.defaultDataset)
        }
    }
}
