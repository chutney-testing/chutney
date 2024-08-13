/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.annotations.Scenario
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.util.ClassGraphUtil
import kotlin.reflect.KFunction

/**
 * Cosmetic to create a list of scenarios
 */
class SynchronizeScenariosBuilder {
    var scenarios: List<ChutneyScenario> = mutableListOf()

    companion object {
        fun searchScenarios(packageName: String): SynchronizeScenariosBuilder.() -> Unit = {
            ClassGraphUtil.findAllAnnotatedFunctions(packageName, Scenario::class).forEach { scenario: KFunction<*> ->
                +scenario
            }
        }
    }

    operator fun ChutneyScenario.unaryPlus() {
        scenarios = scenarios + this
    }

    operator fun List<ChutneyScenario>.unaryPlus() {
        scenarios = scenarios + this
    }

    operator fun <R> KFunction<R>.unaryPlus() {
        scenarios = scenarios +
            (this.call()?.let {
                when (it) {
                    is ChutneyScenario -> listOf(it)
                    is List<*> -> it.filterIsInstance<ChutneyScenario>()
                    else -> throw UnsupportedOperationException()
                }
            })!!
    }

    operator fun ChutneyScenario.unaryMinus() {
        // scenarios = scenarios - this
        // cosmetic to ignore scenario
    }
}
