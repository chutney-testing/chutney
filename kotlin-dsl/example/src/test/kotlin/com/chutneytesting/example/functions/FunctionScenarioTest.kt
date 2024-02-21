package com.chutneytesting.example.functions

import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.Test

class FunctionScenarioTest {

    private var environment: ChutneyEnvironment = ChutneyEnvironment("default value")

    @Test
    fun `jsonMerge` () {
        Launcher().run(json_merge_scenario, environment)
    }

    @Test
    fun `jsonSet` () {
        Launcher().run(json_set_scenario, environment)
    }

    @Test
    fun `jsonSetMany` () {
        Launcher().run(json_set_many_scenario, environment)
    }
}
