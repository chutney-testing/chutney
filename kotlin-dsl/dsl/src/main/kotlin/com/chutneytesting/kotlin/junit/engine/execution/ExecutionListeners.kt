package com.chutneytesting.kotlin.junit.engine.execution

import com.chutneytesting.kotlin.ChutneyConfigurationParameters
import com.chutneytesting.kotlin.execution.report.AnsiReportWriter
import com.chutneytesting.kotlin.execution.report.JsonReportWriter
import com.chutneytesting.kotlin.execution.report.SiteGenerator
import com.chutneytesting.kotlin.util.SystemEnvConfigurationParameters
import com.chutneytesting.kotlin.ChutneyConfigurationParameters.*
import com.chutneytesting.kotlin.junit.engine.execution.ChutneyJUnitReportingKeys.REPORT_JSON_STRING
import com.chutneytesting.kotlin.junit.engine.execution.ChutneyJUnitReportingKeys.REPORT_STEP_JSON_STRING
import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan
import java.util.Optional.ofNullable

class ConsoleLogScenarioReportExecutionListener : EnabledTestExecutionListener(enabledProperty = CONFIG_SCENARIO_LOG) {

    private var ansiReportWriter: AnsiReportWriter? = null

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        super.testPlanExecutionStarted(testPlan)

        if (enabled()) {
            ansiReportWriter = AnsiReportWriter(
                configurationParameters().getBoolean(CONFIG_CONSOLE_LOG_COLOR.parameter).orElse(CONFIG_CONSOLE_LOG_COLOR.defaultBoolean())
            )
        }
    }

    override fun reportingEntryPublished(testIdentifier: TestIdentifier?, entry: ReportEntry?) {
        entry?.let {
            printScenarioReport(it)
        }
    }

    private fun printScenarioReport(entry: ReportEntry) {
        entry.keyValuePairs[REPORT_JSON_STRING.value]?.let {
            if (enabled()) {
                ansiReportWriter!!.printReport(JsonReportWriter.jsonAsReport(it))
            }
        }
    }
}

class ConsoleLogStepReportExecutionListener : EnabledTestExecutionListener(enabledProperty = CONFIG_STEP_LOG) {

    private var ansiReportWriter: AnsiReportWriter? = null

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        super.testPlanExecutionStarted(testPlan)

        if (enabled()) {
            ansiReportWriter = AnsiReportWriter(
                configurationParameters().getBoolean(CONFIG_CONSOLE_LOG_COLOR.parameter).orElse(CONFIG_CONSOLE_LOG_COLOR.defaultBoolean())
            )
        }
    }

    override fun reportingEntryPublished(testIdentifier: TestIdentifier?, entry: ReportEntry?) {
        entry?.let {
            printStepReport(it)
        }
    }

    private fun printStepReport(entry: ReportEntry) {
        entry.keyValuePairs[REPORT_STEP_JSON_STRING.value]?.let {
            if (enabled()) {
                val stepReport = JsonReportWriter.jsonAsReport(it)
                if (stepReport.steps.isEmpty()) {
                    ansiReportWriter!!.printStep(stepReport)
                }
            }
        }
    }
}

class FileWriterScenarioReportExecutionListener : EnabledTestExecutionListener(enabledProperty = CONFIG_REPORT_FILE) {

    private var reportRootPathConfig: String? = null

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        super.testPlanExecutionStarted(testPlan)

        if (enabled()) {
            reportRootPathConfig =
                configurationParameters().get(CONFIG_REPORT_ROOT_PATH.parameter).orElse(CONFIG_REPORT_ROOT_PATH.defaultString())
        }
    }

    override fun reportingEntryPublished(testIdentifier: TestIdentifier?, entry: ReportEntry?) {
        entry?.let {
            writeScenarioReport(it)
        }
    }

    private fun writeScenarioReport(entry: ReportEntry) {
        entry.keyValuePairs[REPORT_JSON_STRING.value]?.let {
            if (enabled()) {
                val report = JsonReportWriter.jsonAsReport(it)
                JsonReportWriter.writeReport(
                    report,
                    reportRootPathConfig!!,
                    true
                )
            }
        }
    }
}

class SiteGeneratorExecutionListener : EnabledTestExecutionListener(enabledProperty = CONFIG_REPORT_SITE) {

    override fun testPlanExecutionFinished(testPlan: TestPlan?) {
        if (enabled()) {
           SiteGenerator().generateSite()
        }
    }
}

abstract class EnabledTestExecutionListener(
    private val enabledProperty: ChutneyConfigurationParameters) : TestExecutionListener {
    private var configurationParameters: SystemEnvConfigurationParameters? = null
    private var enabled: Boolean = false

    override fun testPlanExecutionStarted(testPlan: TestPlan) {
        configurationParameters = SystemEnvConfigurationParameters()
        enabled = configurationParameters!!.getBoolean(enabledProperty.parameter).orElse(enabledProperty.defaultBoolean())
    }

    protected fun configurationParameters(): SystemEnvConfigurationParameters {
        return configurationParameters!!
    }

    protected fun enabled(): Boolean {
        return enabled
    }
}
