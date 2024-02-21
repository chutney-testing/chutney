package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import kotlin.reflect.KFunction

/**
 * Cosmetic to create a list of scenarios
 */
class SynchronizeScenariosBuilder {
    var scenarios: List<ChutneyScenario> = mutableListOf()

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
                    else -> it as List<ChutneyScenario>
                }
            })!!
    }

    operator fun ChutneyScenario.unaryMinus() {
        // scenarios = scenarios - this
        // cosmetic to ignore scenario
    }
}
