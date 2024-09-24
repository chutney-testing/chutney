/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.synchronize.server

import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpsClientTest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import org.junit.jupiter.api.extension.RegisterExtension
import util.ChutneyServerInfoClearProperties

@ChutneyServerInfoClearProperties
abstract class ChutneyServerServiceImplTest {

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

    fun serverEventRequestBodyAsJson(event: ServeEvent): JsonNode {
        return ObjectMapper().readTree(event.request.bodyAsString)
    }

    fun buildServerInfo() = ChutneyServerInfo(
        wireMockServer.baseUrl(),
        "user",
        "password"
    )
}
