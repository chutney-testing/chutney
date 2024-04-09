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

import com.chutneytesting.kotlin.dsl.Dataset
import com.chutneytesting.kotlin.dsl.Dataset.KeyValue
import com.chutneytesting.kotlin.synchronize.ChutneyServerServiceImpl
import com.fasterxml.jackson.databind.JsonNode
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery.forStubMapping
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class Datasets : ChutneyServerServiceImplTest() {
    @ParameterizedTest()
    @ValueSource(ints = [500, 400])
    fun create_dataset(readStubStatus: Int) {
        // Given
        val dataset = Dataset(
            name = "MY DATASET",
            description = "A description",
            uniqueValues = setOf(KeyValue("u1", "vu1"), KeyValue("u2", "vu2")),
            multipleValues = listOf(
                listOf(KeyValue("m1", "vm11"), KeyValue("m2", "vm12")),
                listOf(KeyValue("m1", "vm21"), KeyValue("m2", "vm22"))
            ),
            tags = setOf("TAG_1", "TAG_2")
        )

        wireMockServer.stubFor(
            get(urlPathMatching("/api/v1/datasets/${dataset.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(readStubStatus)
                )
        )

        val createDatasetStub = wireMockServer.stubFor(
            post(urlPathMatching("/api/v1/datasets"))
                .willReturn(
                    okJson(
                        """
                            {
                              "id": "${dataset.id}",
                              "name": "${dataset.name}",
                              "version": 0,
                              "description": "${dataset.description}",
                              "lastUpdated": "2023-08-16T13:14:53.965712Z",
                              "tags": ["TAG_1", "TAG_2"],
                              "uniqueValues": [
                                { "key": "u1", "value": "v1" },
                                { "key": "u2", "value": "v2" }
                              ],
                              "multipleValues": [
                                [
                                    { "key": "m1", "value": "vm11" },
                                    { "key": "m2", "value": "vm12" }
                                ],
                                [
                                    { "key": "m1", "value": "vm21" },
                                    { "key": "m2", "value": "vm22" }
                                ]
                              ]
                            }
                        """.trimIndent()
                    )
                )
        )

        // When
        val createOrUpdateDataset = ChutneyServerServiceImpl.createOrUpdateDataset(buildServerInfo(), dataset)

        // Then
        assertThat(createOrUpdateDataset).isEqualTo(dataset.id)
        assertThat(wireMockServer.allServeEvents).hasSize(2)

        val createRequestReceived = serverEventRequestBodyAsJson(
            wireMockServer.getServeEvents(forStubMapping(createDatasetStub.id)).requests[0]
        )
        assertThat(createRequestReceived.get("id").textValue()).isEqualTo(dataset.id)
        assertThat(createRequestReceived.get("version")).isNull()
        assertThat(createRequestReceived.get("lastUpdated")).isNull()
        assertThat(createRequestReceived.get("name").textValue()).isEqualTo(dataset.name)
        assertThat(createRequestReceived.get("description").textValue()).isEqualTo(dataset.description)
        assertThat(createRequestReceived.get("uniqueValues")).hasSize(2)
            .flatMap<String> { jsonNode: JsonNode ->
                listOf(
                    jsonNode.get("key").textValue(),
                    jsonNode.get("value").textValue()
                )
            }
            .containsExactly("u1", "vu1", "u2", "vu2")
        assertThat(createRequestReceived.get("multipleValues")).hasSize(2)
            .flatMap<String> { jsonNode: JsonNode ->
                jsonNode.flatMap { n: JsonNode ->
                    listOf(
                        n.get("key").textValue(),
                        n.get("value").textValue()
                    )
                }
            }
            .containsExactly("m1", "vm11", "m2", "vm12", "m1", "vm21", "m2", "vm22")
        assertThat(createRequestReceived.get("tags")).hasSize(2)
            .map<String> { jsonNode -> jsonNode.textValue() }
            .containsExactly("TAG_1", "TAG_2")
    }

    @Test
    fun update_dataset() {
        // Given
        val dataset = Dataset(
            name = "MY_DATASET",
            description = "A description",
            uniqueValues = setOf(KeyValue("u1", "vu1"), KeyValue("u2", "vu2")),
            multipleValues = listOf(
                listOf(KeyValue("m1", "vm11"), KeyValue("m2", "vm12")),
                listOf(KeyValue("m1", "vm21"), KeyValue("m2", "vm22"))
            ),
            tags = setOf("TAG_1", "TAG_2")
        )

        wireMockServer.stubFor(
            get(urlPathMatching("/api/v1/datasets/${dataset.id}"))
                .willReturn(
                    okJson(
                        """
                            {
                              "id": "${dataset.id}",
                              "name": "${dataset.name}",
                              "version": 0,
                              "description": "${dataset.description}",
                              "lastUpdated": "2023-08-16T13:14:53.965712Z",
                              "tags": [],
                              "uniqueValues": [
                                { "key": "u2", "value": "v2" }
                              ],
                              "multipleValues": [
                                [
                                    { "key": "m1", "value": "vm11" },
                                    { "key": "m2", "value": "vm12" }
                                ]
                              ]
                            }
                        """.trimIndent()
                    )
                )
        )

        val createDatasetStub = wireMockServer.stubFor(
            put(urlPathMatching("/api/v1/datasets"))
                .willReturn(
                    okJson(
                        """
                            {
                              "id": "${dataset.id}",
                              "name": "${dataset.name}",
                              "version": 0,
                              "description": "${dataset.description}",
                              "lastUpdated": "2023-08-16T13:14:53.965712Z",
                              "tags": ["TAG_1", "TAG_2"],
                              "uniqueValues": [
                                { "key": "u1", "value": "v1" },
                                { "key": "u2", "value": "v2" }
                              ],
                              "multipleValues": [
                                [
                                    { "key": "m1", "value": "vm11" },
                                    { "key": "m2", "value": "vm12" }
                                ],
                                [
                                    { "key": "m1", "value": "vm21" },
                                    { "key": "m2", "value": "vm22" }
                                ]
                              ]
                            }
                        """.trimIndent()
                    )
                )
        )

        // When
        ChutneyServerServiceImpl.createOrUpdateDataset(buildServerInfo(), dataset)

        // Then
        assertThat(wireMockServer.allServeEvents).hasSize(2)
        val createRequestReceived = serverEventRequestBodyAsJson(
            wireMockServer.getServeEvents(forStubMapping(createDatasetStub.id)).requests[0]
        )
        assertThat(createRequestReceived.get("id").textValue()).isEqualTo(dataset.id)
        assertThat(createRequestReceived.get("version")).isNull()
        assertThat(createRequestReceived.get("lastUpdated")).isNull()
        assertThat(createRequestReceived.get("name").textValue()).isEqualTo(dataset.name)
        assertThat(createRequestReceived.get("description").textValue()).isEqualTo(dataset.description)
        assertThat(createRequestReceived.get("uniqueValues")).hasSize(2)
            .flatMap<String> { jsonNode: JsonNode ->
                listOf(
                    jsonNode.get("key").textValue(),
                    jsonNode.get("value").textValue()
                )
            }
            .containsExactly("u1", "vu1", "u2", "vu2")
        assertThat(createRequestReceived.get("multipleValues")).hasSize(2)
            .flatMap<String> { jsonNode: JsonNode ->
                jsonNode.flatMap { n: JsonNode ->
                    listOf(
                        n.get("key").textValue(),
                        n.get("value").textValue()
                    )
                }
            }
            .containsExactly("m1", "vm11", "m2", "vm12", "m1", "vm21", "m2", "vm22")
        assertThat(createRequestReceived.get("tags")).hasSize(2)
            .map<String> { jsonNode -> jsonNode.textValue() }
            .containsExactly("TAG_1", "TAG_2")
    }
}
