/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.synchronize.server

import com.chutneytesting.kotlin.synchronize.ChutneyServerServiceImpl
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnvironmentsTest : ChutneyServerServiceImplTest() {
    @Test
    fun get_environments() {
        wireMockServer.stubFor(
            get(urlPathMatching("/api/v2/environment"))
                .willReturn(
                    okJson("[]")
                )
        )

        val environments = ChutneyServerServiceImpl.getEnvironments(buildServerInfo())

        wireMockServer.verify(
            1,
            getRequestedFor(urlPathMatching("/api/v2/environment"))
        )
        assertThat(environments).isEmpty()
    }
}
