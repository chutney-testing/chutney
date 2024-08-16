/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ChutneyEnvironmentDslTest {

    @Test
    fun `Test unique target name in environment`() {
        val targets: List<ChutneyTarget> = listOf(
            ChutneyTarget("toto", "url1")
        )

        var env: ChutneyEnvironment? = null
        assertDoesNotThrow {
            env = ChutneyEnvironment("default value", "description", targets)
        }

        assertThat(env).isNotNull
        env?.targets?.let {
            assertThat(it.size).isEqualTo(1)
            assertThat(it.get(0).name).isEqualTo("toto")
        }

    }
    @Test
    fun `Test duplicate target name in environment`() {
        val targets: List<ChutneyTarget> = listOf(
            ChutneyTarget("toto", "url1"),
            ChutneyTarget("toto","url2"),
            ChutneyTarget("bou","url3") ,
            ChutneyTarget("bou","url4"),
            ChutneyTarget("unique","url")
        )

        assertThat(assertThrows<IllegalArgumentException> {
            ChutneyEnvironment("default value", "description", targets)
        }.message).isEqualTo("Targets are not unique : toto, bou")
    }
}
