package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.junit.engine.ChutneyTestEngine.Companion.CHUTNEY_JUNIT_ENGINE_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.FilterResult
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors.*
import org.junit.platform.launcher.PostDiscoveryFilter
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.testkit.engine.EngineExecutionResults
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.EventStatistics
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import java.nio.file.Path
import java.util.*

private class ChutneyTestEngineTest {

    val sut = ChutneyTestEngine()

    companion object {
        @JvmStatic
        fun emptySelectors(): Array<Any> {
            return arrayOf(
                selectClass("UnknownClass"),
                selectClasspathRoots(setOf(Path.of("./src/test/kotlin/com/chutneytesting/kotlin/junit/engine")))[0],
                selectClasspathResource("unknownResource"),
                selectMethod("com.chutneytesting.kotlin.junit.engine.ChutneyTest#unknownMethod()")
            )
        }

        @JvmStatic
        fun containerChutneyTestSelectors(): Array<Any> {
            return arrayOf(
                arrayOf(selectClass("com.chutneytesting.kotlin.junit.engine.ChutneyTest"), 46, 46, 75),
                arrayOf(selectClasspathRoots(setOf(Path.of(".")))[0], 49, 49, 77),
                arrayOf(
                    selectClasspathResource("com/chutneytesting/kotlin/junit/engine/ChutneyTest.class"), 46, 46, 75
                ),
                arrayOf(selectMethod("com.chutneytesting.kotlin.junit.engine.ChutneyTest#withSubSteps()"), 12, 12, 10)
            )
        }
    }

    @Test
    fun should_get_engineId() {
        assertThat(sut.id).isEqualTo(CHUTNEY_JUNIT_ENGINE_ID)
    }

    @Test
    fun should_get_groupId() {
        assertThat(sut.groupId).hasValue("com.chutneytesting")
    }

    @Test
    fun should_get_artifactId() {
        assertThat(sut.artifactId).hasValue("chutney-kotlin-dsl")
    }

    @ParameterizedTest
    @MethodSource("emptySelectors")
    fun should_do_nothing_when_select_nothing(selector: DiscoverySelector) {
        val result: EngineExecutionResults = EngineTestKit.engine(CHUTNEY_JUNIT_ENGINE_ID)
            .selectors(selector)
            .execute()

        result
            .allEvents()
            //.debug(System.out)
            .assertStatistics { stats: EventStatistics ->
                stats.started(1).finished(1).succeeded(1)
            }
    }

    @ParameterizedTest
    @MethodSource("containerChutneyTestSelectors")
    fun should_execute_scenario_when_select_containerChutneyTest(selector: DiscoverySelector, startedEvent: Long, finishedEvent: Long, expectedReportingEntryPublished: Long) {
        val result: EngineExecutionResults = EngineTestKit.engine(CHUTNEY_JUNIT_ENGINE_ID)
            .selectors(selector)
            .execute()

        result
            .allEvents()
            //.debug(System.out)
            .assertStatistics { stats: EventStatistics ->
                stats
                    .started(startedEvent)
                    .finished(finishedEvent)
                    .succeeded(finishedEvent)
                    .reportingEntryPublished(expectedReportingEntryPublished)
            }
    }

    @Test
    fun should_filter_on_method_name() {
        val filter = MyPostDiscoveryFilter()
        val filterMock = Mockito.spy(filter)
        val discoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClasspathRoots(Collections.singleton(Path.of("/com/chutneytesting/kotlin/junit/engine"))),
            )
            .filters(
                filterMock
            )
            .build()

        val testEngine = ChutneyTestEngine()

        testEngine.discover(discoveryRequest, UniqueId.forEngine(testEngine.id))

        Mockito.verify(filterMock, times(10)).apply(any())
    }

    open class MyPostDiscoveryFilter : PostDiscoveryFilter {
        override fun apply(`object`: TestDescriptor?): FilterResult {
            return FilterResult.includedIf(true)
        }
    }
}
