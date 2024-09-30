/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import util.ChutneyServerInfoClearProperties
import util.SocketUtil

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ChutneyServerInfoClearProperties
abstract class HttpTestBase(
    wireMockServerPort: Int = SocketUtil.freePort()
) {
    val wireMockServer: WireMockServer
    val url: String

    init {
        wireMockServer = WireMockServer(SocketUtil.freePort(), wireMockServerPort)
        url = "https://localhost:${wireMockServerPort}"
    }

    @BeforeEach
    fun reset() {
        wireMockServer.resetAll()
        wireMockServer.start()
        WireMock.configureFor("localhost", wireMockServer.port())

    }

    @AfterAll
    fun tearDown() {
        wireMockServer.stop()
        wireMockServer.resetAll()
    }
}
