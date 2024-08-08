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
