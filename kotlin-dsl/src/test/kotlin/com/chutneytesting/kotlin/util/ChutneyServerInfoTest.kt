/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.util

import com.chutneytesting.kolin.util.ChutneyServerInfoClearProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.MalformedURLException

class ChutneyServerInfoTest {

    @ParameterizedTest
    @ValueSource(strings = ["", "no.protocol.url"])
    fun malformed_url(chutneyServerUrl: String) {
        assertThrows<MalformedURLException> {
            ChutneyServerInfo(chutneyServerUrl, "user", "password")
        }
    }

    @Nested
    @DisplayName("Use system properties for proxy setup")
    @ChutneyServerInfoClearProperties
    inner class UseSystemPropsForProxy {

        @Test
        fun http_blank() {
            System.setProperty("http.proxyHost", "")

            var serverInfo: ChutneyServerInfo? = null

            assertDoesNotThrow {
                serverInfo = ChutneyServerInfo("http://host.name:1234", "", "")
            }

            assertThat(serverInfo?.proxyUrl).isNull()
            assertThat(serverInfo?.proxyUser).isNull()
            assertThat(serverInfo?.proxyPassword).isNull()
            assertThat(serverInfo?.proxyUri).isNull()
        }

        @Test
        fun http() {
            System.setProperty("http.proxyHost", "proxyHost")
            System.setProperty("http.proxyPort", "5678")
            System.setProperty("http.proxyUser", "proxyUer")
            System.setProperty("http.proxyPassword", "proxyPassword")

            val serverInfo = ChutneyServerInfo("http://host.name:1234", "user", "password")

            assertThat(serverInfo.proxyUrl).isEqualTo("http://proxyHost:5678")
            assertThat(serverInfo.proxyUser).isEqualTo(System.getProperty("http.proxyUser"))
            assertThat(serverInfo.proxyPassword).isEqualTo(System.getProperty("http.proxyPassword"))

            assertThat(serverInfo.proxyUri.toString()).isEqualTo(serverInfo.proxyUrl)
        }

        @Test
        fun http_default_port() {
            System.setProperty("http.proxyHost", "proxyHost")
            System.setProperty("http.proxyUser", "proxyUer")
            System.setProperty("http.proxyPassword", "proxyPassword")

            val serverInfo = ChutneyServerInfo("http://host.name:1234", "user", "password")

            assertThat(serverInfo.proxyUrl).isEqualTo("http://proxyHost:80")
            assertThat(serverInfo.proxyUser).isEqualTo(System.getProperty("http.proxyUser"))
            assertThat(serverInfo.proxyPassword).isEqualTo(System.getProperty("http.proxyPassword"))

            assertThat(serverInfo.proxyUri.toString()).isEqualTo(serverInfo.proxyUrl)
        }

        @Test
        fun https_blank() {
            System.setProperty("https.proxyHost", "")

            var serverInfo: ChutneyServerInfo? = null

            assertDoesNotThrow {
                serverInfo = ChutneyServerInfo("http://host.name:1234", "", "")
            }

            assertThat(serverInfo?.proxyUrl).isNull()
            assertThat(serverInfo?.proxyUser).isNull()
            assertThat(serverInfo?.proxyPassword).isNull()
            assertThat(serverInfo?.proxyUri).isNull()
        }

        @Test
        fun https() {
            System.setProperty("https.proxyHost", "proxyHost")
            System.setProperty("https.proxyPort", "5678")
            System.setProperty("https.proxyUser", "proxyUer")
            System.setProperty("https.proxyPassword", "proxyPassword")

            val serverInfo = ChutneyServerInfo("https://host.name:1234", "user", "password")

            assertThat(serverInfo.proxyUrl).isEqualTo("https://proxyHost:5678")
            assertThat(serverInfo.proxyUser).isEqualTo(System.getProperty("https.proxyUser"))
            assertThat(serverInfo.proxyPassword).isEqualTo(System.getProperty("https.proxyPassword"))

            assertThat(serverInfo.proxyUri.toString()).isEqualTo(serverInfo.proxyUrl)
        }

        @Test
        fun https_default_port() {
            System.setProperty("https.proxyHost", "proxyHost")
            System.setProperty("https.proxyUser", "proxyUer")
            System.setProperty("https.proxyPassword", "proxyPassword")

            val serverInfo = ChutneyServerInfo("https://host.name:1234", "user", "password")

            assertThat(serverInfo.proxyUrl).isEqualTo("https://proxyHost:443")
            assertThat(serverInfo.proxyUser).isEqualTo(System.getProperty("https.proxyUser"))
            assertThat(serverInfo.proxyPassword).isEqualTo(System.getProperty("https.proxyPassword"))

            assertThat(serverInfo.proxyUri.toString()).isEqualTo(serverInfo.proxyUrl)
        }
    }
}
