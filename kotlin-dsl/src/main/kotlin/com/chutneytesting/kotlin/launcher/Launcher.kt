package com.chutneytesting.kotlin.launcher

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StatusDto.SUCCESS
import com.chutneytesting.kotlin.ChutneyConfigurationParameters.CONFIG_ENVIRONMENT_ROOT_PATH
import com.chutneytesting.kotlin.ChutneyConfigurationParameters.CONFIG_REPORT_ROOT_PATH
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.execution.ExecutionService
import com.chutneytesting.kotlin.execution.report.AnsiReportWriter
import com.chutneytesting.kotlin.execution.report.JsonReportWriter
import com.chutneytesting.kotlin.util.SystemEnvConfigurationParameters
import org.assertj.core.api.Assertions
import org.assertj.core.api.SoftAssertions
import java.util.Optional.ofNullable

/**
 * reportRootPathInput defaulted to ChutneyConfigurationParameters.CONFIG_REPORT_ROOT_PATH
 * -------
 * environmentJsonRootPathInput defaulted to ChutneyConfigurationParameters.CONFIG_ENVIRONMENT_ROOT_PATH
 */
class Launcher(
    reportRootPathInput: String? = null,
    environmentJsonRootPathInput: String? = null
) {
    private val systemParameters = SystemEnvConfigurationParameters();

    init {
        if(reportRootPathInput != null) {
            System.setProperty(CONFIG_REPORT_ROOT_PATH.parameter,reportRootPathInput);
        }
    }
    private val reportRootPath: String = systemParameters.get(CONFIG_REPORT_ROOT_PATH.parameter).orElse(CONFIG_REPORT_ROOT_PATH.defaultString())

    private val executionService = ExecutionService(ofNullable(environmentJsonRootPathInput).orElse(
            systemParameters.get(CONFIG_ENVIRONMENT_ROOT_PATH.parameter).orElse(CONFIG_ENVIRONMENT_ROOT_PATH.defaultString())
        )
    )

    private fun run(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment
    ): StatusDto? {
        val report = executionService.waitLastReport(executionService.execute(scenario, environment))
        AnsiReportWriter().printReport(report)
        JsonReportWriter.writeReport(report, reportRootPath)
        return report.status
    }

    fun run(
        scenario: ChutneyScenario,
        environmentName: String,
        expected: StatusDto = SUCCESS
    ) {
        run(scenario, executionService.getEnvironment(environmentName), expected)
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environmentName: String,
        expected: StatusDto = SUCCESS
    ) {
        run(scenarios, executionService.getEnvironment(environmentName), expected)
    }

    fun run(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        expected: StatusDto = SUCCESS
    ) {
        Assertions.assertThat(run(scenario, environment)).isEqualTo(expected)
    }

    fun run(
        scenarios: List<ChutneyScenario>,
        environment: ChutneyEnvironment,
        expected: StatusDto = SUCCESS
    ) {
        val softly = SoftAssertions()
        scenarios.map { runSoftly(it, environment, expected, softly) }
        softly.assertAll()
    }

    private fun runSoftly(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        expected: StatusDto,
        softly: SoftAssertions
    ) {
        val status = run(scenario, environment)
        softly.assertThat(status).isEqualTo(expected)
    }
}
