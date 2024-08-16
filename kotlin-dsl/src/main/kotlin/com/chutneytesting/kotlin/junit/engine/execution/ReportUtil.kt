/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.junit.engine.execution

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.engine.api.execution.StepExecutionReportDto.StepContextDto
import com.chutneytesting.engine.domain.execution.engine.step.Step
import com.chutneytesting.engine.domain.execution.report.Status
import com.chutneytesting.engine.domain.execution.report.StepExecutionReport
import com.chutneytesting.engine.domain.execution.report.StepExecutionReportBuilder

object ReportUtil {

    fun generateReportDto(step: Step): StepExecutionReportDto {
        return toDto(generateReport(step, getEnvironment(step)))
    }

    private fun generateReport(step: Step, env: String): StepExecutionReport {
        return StepExecutionReportBuilder().setName(step.definition().name)
            .setDuration(step.duration().toMillis())
            .setStartDate(step.startDate())
            .setStatus(step.status())
            .setInformation(step.informations())
            .setErrors(step.errors())
            .setSteps(step.subSteps().map { generateReport(it, env) }.toList())
            .setEvaluatedInputs(step.evaluatedInputs)
            .setStepResults(step.stepOutputs)
            .setScenarioContext(step.scenarioContext)
            .setType(step.type())
            .setTarget(step.target())
            .setStrategy(step.strategy().map { it.type }.orElse(null))
            .setEnvironment(env)
            .createStepExecutionReport()
    }

    private fun getEnvironment(step: Step): String {
        return if (step.isParentStep) {
            getEnvironment(step.subSteps()[0])
        } else step.scenarioContext["environment"] as String
    }

    private fun toDto(report: StepExecutionReport): StepExecutionReportDto {
        return StepExecutionReportDto(
            report.name,
            report.environment,
            report.startDate,
            report.duration,
            statusToDto(report.status),
            report.information,
            report.errors,
            report.steps.map { toDto(it) }.toList(),
            contextToDto(report.scenarioContext, report.evaluatedInputs, report.stepResults),
            report.type,
            report.targetName,
            report.targetUrl,
            report.strategy
        )
    }

    private fun contextToDto(
        scenarioContext: Map<String, Any>?,
        evaluatedInput: Map<String, Any>?,
        stepResults: Map<String, Any>?
    ): StepContextDto {
        return StepContextDto(
            scenarioContext ?: emptyMap(),
            evaluatedInput ?: emptyMap(),
            stepResults ?: emptyMap()
        )
    }

    private fun statusToDto(status: Status): StatusDto {
        return StatusDto.valueOf(status.name)
    }
}
