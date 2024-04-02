package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kolin.util.ChutneyServerInfoClearProperties
import com.chutneytesting.kotlin.dsl.Dataset
import com.chutneytesting.kotlin.dsl.Dataset.KeyValue
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpsClientTest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery.forStubMapping
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@ChutneyServerInfoClearProperties
class ChutneyServerServiceImplTest {

    private val keystorePath: String = HttpsClientTest::class.java.getResource("/security/server.jks")!!.path

    @RegisterExtension
    val wireMockServer: WireMockExtension = WireMockExtension.newInstance()
        .options(
            WireMockConfiguration.wireMockConfig()
            .dynamicHttpsPort()
            .httpDisabled(true)
            .keystorePath(keystorePath)
            .keystorePassword("server")
            .keyManagerPassword("server")
        )
        .configureStaticDsl(false)
        .build()

    @Nested
    inner class Environments {
        @Test
        fun get_environments() {
            wireMockServer.stubFor(
                get(urlPathMatching("/api/v2/environment"))
                .willReturn(
                    okJson("[]")
                )
            )

            val environments = ChutneyServerServiceImpl.getEnvironments(buildServerInfo())

            wireMockServer.verify(1,
                getRequestedFor(urlPathMatching("/api/v2/environment"))
            )
            assertThat(environments).isEmpty()
        }
    }

    @Nested
    inner class Scenarios {
        @Test
        fun get_all_scenarios() {
            wireMockServer.stubFor(
                get(urlPathMatching("/api/scenario/v2"))
                    .willReturn(
                        okJson("[]")
                    )
            )

            val environments = ChutneyServerServiceImpl.getAllScenarios(buildServerInfo())

            wireMockServer.verify(1,
                getRequestedFor(urlPathMatching("/api/scenario/v2"))
            )
            assertThat(environments).isEmpty()
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
                val scenario = Scenario(id = 666, title = "A new scenario", defaultDataset = "NEW_DATASET", tags = listOf("STAG_1", "STAG_2")) {
                    description = "MyDescription"
                    When("Something happens update") {
                        SuccessAction()
                    }
                }

                val checkStub = wireMockServer.stubFor(
                    get(urlPathMatching("/api/scenario/v2/raw/${scenario.id}"))
                        .willReturn(
                            okJson("""
                                {
                                    "id": "${scenario.id}",
                                    "version": 1,
                                    "title": "title",
                                    "tags": ["TAG1", "TAG2"],
                                    "defaultDataset": "MY_DATASET",
                                    "content": "{...}"
                                }
                            """.trimIndent())
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

    @Nested
    inner class Datasets {
        @ParameterizedTest()
        @ValueSource(ints = [500, 400])
        fun create_dataset(readStubStatus: Int) {
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
                        aResponse()
                            .withStatus(readStubStatus)
                    )
            )

            val createDatasetStub = wireMockServer.stubFor(
                post(urlPathMatching("/api/v1/datasets"))
                    .willReturn(
                        okJson("""
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
                .flatMap<String> { jsonNode: JsonNode -> listOf(jsonNode.get("key").textValue(), jsonNode.get("value").textValue()) }
                .containsExactly("u1", "vu1", "u2", "vu2")
            assertThat(createRequestReceived.get("multipleValues")).hasSize(2)
                .flatMap<String> { jsonNode: JsonNode -> jsonNode.flatMap { n: JsonNode -> listOf(n.get("key").textValue(), n.get("value").textValue()) } }
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
                        okJson("""
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
                        okJson("""
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
                .flatMap<String> { jsonNode: JsonNode -> listOf(jsonNode.get("key").textValue(), jsonNode.get("value").textValue()) }
                .containsExactly("u1", "vu1", "u2", "vu2")
            assertThat(createRequestReceived.get("multipleValues")).hasSize(2)
                .flatMap<String> { jsonNode: JsonNode -> jsonNode.flatMap { n: JsonNode -> listOf(n.get("key").textValue(), n.get("value").textValue()) } }
                .containsExactly("m1", "vm11", "m2", "vm12", "m1", "vm21", "m2", "vm22")
            assertThat(createRequestReceived.get("tags")).hasSize(2)
                .map<String> { jsonNode -> jsonNode.textValue() }
                .containsExactly("TAG_1", "TAG_2")
        }
    }

    private fun serverEventRequestBodyAsJson(event: ServeEvent): JsonNode {
        return ObjectMapper().readTree(event.request.bodyAsString)
    }

    private fun buildServerInfo() = ChutneyServerInfo(
        wireMockServer.baseUrl(),
        "user",
        "password"
    )
}
