package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kolin.util.ChutneyServerInfoClearProperties
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
                assertThat(createRequestReceived.get("tags")[0].textValue()).isEqualTo("KOTLIN")
                assertThat(createRequestReceived.get("defaultDataset").isNull).isTrue()
            }

            @Test
            fun id_from_code() {
                // Given
                val scenario = Scenario(id = 666, title = "A scenario") {
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
                assertThat(createRequestReceived.get("tags")[0].textValue()).isEqualTo("KOTLIN")
                assertThat(createRequestReceived.get("defaultDataset").isNull).isTrue()
            }
        }

        @Nested
        inner class ExistingRemoteScenario {
            @Test
            fun update_from_code() {
                // Given
                val scenario = Scenario(id = 666, title = "A new scenario", defaultDataset = "NEW_DATASET") {
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
                    .containsExactlyInAnyOrder(tuple("TAG1"), tuple("TAG2"), tuple("KOTLIN"))
                assertThat(createRequestReceived.get("defaultDataset").textValue()).isEqualTo(scenario.defaultDataset)
            }
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
