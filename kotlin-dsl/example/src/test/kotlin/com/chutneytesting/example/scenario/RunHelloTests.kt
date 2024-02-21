package com.chutneytesting.example.scenario

import com.chutneytesting.example.environment_en
import com.chutneytesting.example.environment_fr
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.junit.api.ChutneyTest
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.Test

class RunHelloTests {

    companion object {
        @JvmField
        val en_www = environment_en
    }

    @ChutneyTest(environment = "en_www")
    fun testMethod(): ChutneyScenario {
        return search_title_scenario
    }

    @Test
    fun `search title is displayed`() {
        Launcher().run(search_title_scenario, environment_fr)
    }

}
