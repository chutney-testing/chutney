/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.util

import util.ChutneyServerInfoClearProperties
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@ChutneyServerInfoClearProperties
class HttpClientTest {

    @RegisterExtension
    val wireMockServer: WireMockExtension = WireMockExtension.newInstance()
        .options(wireMockConfig()
            .dynamicPort()
        )
        .configureStaticDsl(false)
        .build()

    @Nested
    @DisplayName("Use preemptive basic authentication")
    inner class UsePreemptiveBasicAuth {
        @Test
        fun chutney() {
            // Given
            val serverInfo = ChutneyServerInfo(
                wireMockServer.baseUrl(),
                "user",
                "password"
            )

            val expectedAuthorization = Base64.getEncoder()
                .encodeToString((serverInfo.user + ":" + serverInfo.password).toByteArray())

            wireMockServer.stubFor(
                get(urlPathMatching("/pre"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("999")
                    )
            )

            // When
            HttpClient.get<Any>(serverInfo, "/pre")

            // Then
            wireMockServer.verify(
                1, getRequestedFor(urlPathMatching("/pre"))
                    .withHeader("Authorization", equalTo("Basic $expectedAuthorization"))
            )
        }

        @Nested
        @DisplayName("Proxy")
        inner class Proxy {
            @Test
            fun explicit() {
                // Given
                val serverInfo = ChutneyServerInfo(
                    "http://chutney.server:999",
                    "user",
                    "password",
                    wireMockServer.baseUrl(),
                    "proxyUer",
                    "proxyPassword"
                )

                val expectedProxyAuthorization = Base64.getEncoder()
                    .encodeToString((serverInfo.proxyUser + ":" + serverInfo.proxyPassword).toByteArray())

                wireMockServer.stubFor(
                    get(urlPathMatching("/pre"))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("999")
                        )
                )

                // When
                HttpClient.get<Any>(serverInfo, "/pre")

                // Then
                wireMockServer.verify(
                    1, getRequestedFor(urlPathMatching("/pre"))
                        .withHeader("Proxy-Authorization", equalTo("Basic $expectedProxyAuthorization"))
                )
            }

            @Test
            fun implicit() {
                // Given
                System.setProperty("http.proxyHost", "localhost")
                System.setProperty("http.proxyPort", wireMockServer.port.toString())
                System.setProperty("http.proxyUser", "proxyUer")
                System.setProperty("http.proxyPassword", "proxyPassword")

                val serverInfo = ChutneyServerInfo("http://chutney.server:999", "user", "password")

                val expectedProxyAuthorization = Base64.getEncoder()
                    .encodeToString((serverInfo.proxyUser + ":" + serverInfo.proxyPassword).toByteArray())

                wireMockServer.stubFor(
                    get(urlPathMatching("/pre"))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("999")
                        )
                )

                // When
                HttpClient.get<Any>(serverInfo, "/pre")

                // Then
                wireMockServer.verify(
                    1, getRequestedFor(urlPathMatching("/pre"))
                        .withHeader("Proxy-Authorization", equalTo("Basic $expectedProxyAuthorization"))
                )
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [300, 310, 401, 403, 500])
    fun throw_when_http_status_ko(statusCode: Int) {
        // Given
        val serverInfo = ChutneyServerInfo(wireMockServer.baseUrl(), "user", "password")

        wireMockServer.stubFor(
            get(urlEqualTo("/status"))
                .willReturn(
                    aResponse()
                        .withStatus(statusCode)
                )
        )

        // When / Then
        assertThrows<HttpClientException> {
            HttpClient.get<Any>(serverInfo, "/status")
        }
    }

    @Nested
    @DisplayName("Parse response as JSON")
    inner class ParseJsonResponse {
        @Test
        fun number() {
            // Given
            val serverInfo = ChutneyServerInfo(
                wireMockServer.baseUrl(),
                "user",
                "password"
            )

            wireMockServer.stubFor(
                get(urlPathMatching("/pre"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("999")
                    )
            )

            // When / Then
            val result = HttpClient.get<Long>(serverInfo, "/pre")

            // Then
            assertThat(result).isEqualTo(999)
            wireMockServer.verify(
                1, getRequestedFor(urlPathMatching("/pre"))
            )
        }

        @Test
        fun list() {
            // Given
            val serverInfo = ChutneyServerInfo(
                wireMockServer.baseUrl(),
                "user",
                "password"
            )

            wireMockServer.stubFor(
                get(urlPathMatching("/pre"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""["a", "b", "c"]""")
                    )
            )

            // When / Then
            val result = HttpClient.get<List<String>>(serverInfo, "/pre")

            // Then
            assertThat(result).containsExactly("a", "b", "c")
            wireMockServer.verify(
                1, getRequestedFor(urlPathMatching("/pre"))
            )
        }

        @Test
        fun object_() {
            // Given
            val serverInfo = ChutneyServerInfo(
                wireMockServer.baseUrl(),
                "user",
                "password"
            )

            wireMockServer.stubFor(
                get(urlPathMatching("/pre"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""{"a": 1, "b": "text"}""")
                    )
            )

            // When / Then
            val result = HttpClient.get<HashMap<String, Any>>(serverInfo, "/pre")

            // Then
            assertThat(result).contains(
                entry("a", 1),
                entry("b", "text")
            )
            wireMockServer.verify(
                1, getRequestedFor(urlPathMatching("/pre"))
            )
        }

        @Test
        fun allow_empty_response() {
            // Given
            val serverInfo = ChutneyServerInfo(
                wireMockServer.baseUrl(),
                "user",
                "password"
            )

            wireMockServer.stubFor(
                get(urlPathMatching("/pre"))
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                    )
            )

            // When / Then
            assertDoesNotThrow { HttpClient.get<Any>(serverInfo, "/pre") }

            // Then
            wireMockServer.verify(
                1, getRequestedFor(urlPathMatching("/pre"))
            )
        }
    }
}
