/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.junit.engine.execution

import com.chutneytesting.engine.domain.execution.engine.step.Step
import com.chutneytesting.engine.domain.execution.report.Status
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException
import com.chutneytesting.environment.domain.exception.NoEnvironmentFoundException
import com.chutneytesting.environment.domain.exception.UnresolvedEnvironmentException
import com.chutneytesting.kotlin.ChutneyConfigurationParameters
import com.chutneytesting.kotlin.dsl.ChutneyStep
import com.chutneytesting.kotlin.dsl.ChutneyStepImpl
import com.chutneytesting.kotlin.dsl.Strategy
import com.chutneytesting.kotlin.execution.report.JsonReportWriter
import com.chutneytesting.kotlin.junit.engine.ChutneyScenarioDescriptor
import com.chutneytesting.kotlin.junit.engine.ChutneyStepDescriptor
import com.chutneytesting.kotlin.junit.engine.addStep
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.engine.support.descriptor.MethodSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ChutneyScenarioExecutionContext(
    private val chutneyClassExecutionContext: ChutneyClassExecutionContext,
    private val chutneyScenarioDescriptor: ChutneyScenarioDescriptor
) {
    private var executionId: Long? = null
    private var rootStep: Step? = null
    private var executionStatus: Status = Status.NOT_EXECUTED
    private val uniqueIds = mutableMapOf<Step, TestDescriptor>()
    private val retryParents = mutableSetOf<Step>()
    private val retry = mutableMapOf<Step, Pair<TestDescriptor, TestExecutionResult?>>()
    private val engineExecutionContext = chutneyClassExecutionContext.engineExecutionContext

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun execute() {
        logger.info("Running scenario {} from {}", chutneyScenarioDescriptor.chutneyScenario.title, (chutneyScenarioDescriptor.source.get() as MethodSource).methodName)

        try {
            executionId = delegateExecution()
            engineExecutionContext.addScenarioExecution(executionId!!, this)
        } catch (t: Throwable) {
            try {
                notifyFailedLaunchExecution(t)
            } finally {
                chutneyClassExecutionContext.endExecutionLatch()
            }
        }
    }

    private fun delegateExecution() =
        if (chutneyScenarioDescriptor.environment != null) {
            engineExecutionContext.executionService.execute(
                chutneyScenarioDescriptor.chutneyScenario,
                chutneyScenarioDescriptor.environment
            )
        } else {
            engineExecutionContext.executionService.execute(
                chutneyScenarioDescriptor.chutneyScenario,
                resolveEnvironmentName(chutneyScenarioDescriptor.environmentName)
            )
        }

    fun startExecution(rootStep: Step) {
        this.rootStep = rootStep

        engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.STARTED, chutneyScenarioDescriptor)
    }

    fun beginStepExecution(step: Step) {
        val rootStep = rootStep!!

        if (rootStep != step) {
            val uniqueId = buildStepUniqueId(chutneyScenarioDescriptor.uniqueId, rootStep, step)
            chutneyScenarioDescriptor.findByUniqueId(uniqueId).ifPresentOrElse({
                val chutneyStepDescriptor = it as ChutneyStepDescriptor
                if (chutneyStepDescriptor.hasRetryStrategy()) {
                    retryParents.remove(step)
                    retryParents.add(step)
                }
                if (hasParentRetryStep(step)) {
                    retry[step] = Pair(chutneyStepDescriptor, null)
                }
                if (!uniqueIds.containsKey(step)) {
                    uniqueIds[step] = chutneyStepDescriptor
                    engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.STARTED, chutneyStepDescriptor)
                }
            }, {
                val stepDescriptor = mapStepToChutneyDescriptor(uniqueId, step)
                uniqueIds[step] = stepDescriptor

                chutneyScenarioDescriptor.addChild(stepDescriptor)
                stepDescriptor.setParent(chutneyScenarioDescriptor)
                engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.DYNAMIC, stepDescriptor)
                stepDescriptor.children.forEach { engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.DYNAMIC, it) }
                engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.STARTED, stepDescriptor)
            })
        }
    }

    fun endStepExecution(step: Step) {
        val rootStep = rootStep!!

        if (rootStep != step) {
            val testDescriptor = uniqueIds[step]!! as ChutneyStepDescriptor

            engineExecutionContext.notifyJUnitListener(
                ChutneyEngineExecutionContext.ListenerEvent.REPORT,
                testDescriptor,
                reportEntry = ReportEntry.from(
                    mapOf(
                        ChutneyJUnitReportingKeys.REPORT_STEP_JSON_STRING.value to JsonReportWriter.reportAsJson(ReportUtil.generateReportDto(step))
                    )
                )
            )

            val testExecutionResult = testExecutionResultFromStatus(
                throwable = stepFailureException(step),
                status = arrayOf(step.status())
            )

            if (hasParentRetryStep(step)) {
                retry[step] = Pair(testDescriptor, testExecutionResult)
            } else if (testDescriptor.hasRetryStrategy()) {
                if (testExecutionResult.status.equals(TestExecutionResult.Status.SUCCESSFUL)) {
                    endRetryStep(step, testDescriptor, testExecutionResult)
                } else {
                    retry[step] = Pair(testDescriptor, testExecutionResult)
                }
            } else {
                engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.FINISHED, testDescriptor, testExecutionResult)
            }
        }
    }

    fun endExecution(rootStep: Step) {
        executionStatus = rootStep.status()

        try {
            endPendingFailedRetryStep()

            engineExecutionContext.notifyJUnitListener(
                ChutneyEngineExecutionContext.ListenerEvent.REPORT,
                chutneyScenarioDescriptor,
                reportEntry = ReportEntry.from(
                    mapOf(
                        ChutneyJUnitReportingKeys.REPORT_JSON_STRING.value to JsonReportWriter.reportAsJson(ReportUtil.generateReportDto(rootStep))
                    ))
            )

            engineExecutionContext.notifyJUnitListener(
                ChutneyEngineExecutionContext.ListenerEvent.FINISHED,
                chutneyScenarioDescriptor,
                testExecutionResultFromStatus(
                    throwable = stepFailureException(rootStep),
                    status = arrayOf(executionStatus)
                )
            )
        } finally {
            chutneyClassExecutionContext.endExecutionLatch()
        }
    }

    private fun resolveEnvironmentName(environmentName: String): String? {
        if (environmentName.isBlank()) {
            return engineExecutionContext.configurationParameters.get(ChutneyConfigurationParameters.CONFIG_ENVIRONMENT.parameter).orElse(null)
        }
        return environmentName
    }

    private fun convertExecuteException(t: Throwable, scenarioDescriptor: ChutneyScenarioDescriptor): Throwable {
        return when (t) {
            is UnresolvedEnvironmentException -> UnresolvedScenarioEnvironmentException(t.message + " Please, specify a name or declare only one environment.")
            is NoEnvironmentFoundException -> UnresolvedScenarioEnvironmentException(t.message + " Please, declare one.")
            is EnvironmentNotFoundException -> UnresolvedScenarioEnvironmentException("Environment [${scenarioDescriptor.environmentName}] not found. ${t.message}")
            else -> AssertionError(t)
        }
    }

    private fun buildStepUniqueId(
        fromUniqueId: UniqueId,
        rootStep: Step,
        subStep: Step
    ): UniqueId {
        val list = rootStep.findSubStepPath(subStep).filterIndexed { index, _ -> index > 0 }
        var uniqueId = fromUniqueId
        list.forEachIndexed { i, s ->
            val stepIndex = if (i == 0) {
                rootStep.subSteps().indexOf(s)
            } else {
                list[i - 1].subSteps().indexOf(s)
            }
            uniqueId = uniqueId.addStep(stepIndex)
        }
        return uniqueId
    }

    private fun stepFailureException(step: Step): Throwable? {
        if (step.isExecutionFailed()) {
            return StepExecutionFailedException(step)
        }
        return null
    }

    private fun notifyFailedLaunchExecution(t: Throwable) {
        engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.STARTED, chutneyScenarioDescriptor)
        chutneyScenarioDescriptor.children.forEach {
            engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.SKIPPED, it, reason = "Could not launch parent scenario execution")
        }
        engineExecutionContext.notifyJUnitListener(
            ChutneyEngineExecutionContext.ListenerEvent.FINISHED,
            chutneyScenarioDescriptor,
            testExecutionResultFromStatus(
                throwable = convertExecuteException(t, chutneyScenarioDescriptor),
                status = arrayOf(Status.FAILURE)
            )
        )
    }

    private fun mapStepToChutneyDescriptor(uniqueId: UniqueId, step: Step): TestDescriptor {
        val chutneyStepDescriptor = ChutneyStepDescriptor(uniqueId, step.definition().name, chutneyScenarioDescriptor.source.get(), mapStepToChutneyStep(step))
        step.subSteps().forEach {
            val childId = buildStepUniqueId(uniqueId, step, it)
            val childDescriptor = mapStepToChutneyDescriptor(childId, it)
            chutneyStepDescriptor.addChild(childDescriptor)
        }
        return chutneyStepDescriptor
    }

    private fun mapStepToChutneyStep(step: Step): ChutneyStep {
        return ChutneyStep(
            step.definition().name,
            if (step.definition().type.isBlank()) {
                null
            } else {
                ChutneyStepImpl(step.definition().type)
            },
            step.definition().strategy.map { Strategy(it.type, it.strategyProperties.mapValues { v -> v.toString() }) }.orElse(null)
        )
    }

    private fun hasParentRetryStep(step: Step): Boolean {
        return retryParents.flatMap { flatSubSteps(it) }.distinct().contains(step)
    }

    private fun flatSubSteps(step: Step, withSelf: Boolean = false): List<Step> {
        val stepList = step.subSteps().takeIf { it.isNotEmpty() }?.flatMap { flatSubSteps(it, true) } ?: emptyList()
        return if (withSelf) {
            stepList.plus(step)
        } else {
            stepList
        }
    }

    private fun endRetryStep(step: Step, testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult?) {
        step.subSteps().forEach {
            retry.remove(it)?.let { pair ->
                endRetryStep(it, pair.first, pair.second)
            }
        }

        retryParents.remove(step)
        engineExecutionContext.notifyJUnitListener(ChutneyEngineExecutionContext.ListenerEvent.FINISHED, testDescriptor, testExecutionResult)
    }

    private fun endPendingFailedRetryStep() {
        retryParents.toList().forEach {
            retry.remove(it)?.let { pair ->
                endRetryStep(it, pair.first, pair.second)
            }
        }
    }
}
