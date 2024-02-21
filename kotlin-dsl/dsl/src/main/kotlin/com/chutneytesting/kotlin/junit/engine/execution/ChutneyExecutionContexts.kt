package com.chutneytesting.kotlin.junit.engine.execution

import com.chutneytesting.engine.domain.execution.RxBus
import com.chutneytesting.engine.domain.execution.event.BeginStepExecutionEvent
import com.chutneytesting.engine.domain.execution.event.EndScenarioExecutionEvent
import com.chutneytesting.engine.domain.execution.event.EndStepExecutionEvent
import com.chutneytesting.engine.domain.execution.event.StartScenarioExecutionEvent
import com.chutneytesting.kotlin.ChutneyConfigurationParameters.CONFIG_ENGINE_STEP_AS_TEST
import com.chutneytesting.kotlin.ChutneyConfigurationParameters.CONFIG_ENVIRONMENT_ROOT_PATH
import com.chutneytesting.kotlin.execution.ExecutionService
import com.chutneytesting.kotlin.junit.engine.ChutneyClassDescriptor
import com.chutneytesting.kotlin.junit.engine.execution.ChutneyEngineExecutionContext.ListenerEvent.*
import com.chutneytesting.kotlin.util.SystemEnvConfigurationParameters
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.TestExecutionResult.successful
import org.junit.platform.engine.reporting.ReportEntry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class ChutneyEngineExecutionContext(val request: ExecutionRequest) {
    val configurationParameters: SystemEnvConfigurationParameters = SystemEnvConfigurationParameters()
    val executionService: ExecutionService

    private val scenarioExecutions = HashMap<Long, ChutneyScenarioExecutionContext>()
    private val syncExecutionSemaphore: Semaphore = Semaphore(1)

    private val endExecutionLatch: CountDownLatch = CountDownLatch(request.rootTestDescriptor.children.size)

    private val stepAsTest: Boolean = configurationParameters.getBoolean(CONFIG_ENGINE_STEP_AS_TEST.parameter).orElse(CONFIG_ENGINE_STEP_AS_TEST.defaultBoolean())
    private val environmentRootPath: String? = configurationParameters.get(CONFIG_ENVIRONMENT_ROOT_PATH.parameter).orElse(CONFIG_ENVIRONMENT_ROOT_PATH.defaultString())

    private val startScenarioDisposable: Disposable
    private val beginStepDisposable: Disposable
    private val endStepDisposable: Disposable
    private val endScenarioDisposable: Disposable

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        executionService = if (environmentRootPath == null) ExecutionService() else ExecutionService(environmentRootPath)

        val chutneyBus = RxBus.getInstance()
        startScenarioDisposable =
            chutneyBus.register(StartScenarioExecutionEvent::class.java, this::startScenarioExecution)

        if (stepAsTest) {
            beginStepDisposable = chutneyBus.register(BeginStepExecutionEvent::class.java, this::beginStepExecution)
            endStepDisposable = chutneyBus.register(EndStepExecutionEvent::class.java, this::endStepExecution)
        } else {
            beginStepDisposable = Observable.empty<BeginStepExecutionEvent>().subscribe()
            endStepDisposable = Observable.empty<BeginStepExecutionEvent>().subscribe()
        }

        endScenarioDisposable = chutneyBus.register(EndScenarioExecutionEvent::class.java, this::endScenarioExecution)
    }

    fun execute() {
        startExecution()
        try {
            request.rootTestDescriptor.children
                .filterIsInstance<ChutneyClassDescriptor>()
                .forEach {
                    executeClass(it)
                }
        } finally {
            endExecution()
        }
    }

    fun addScenarioExecution(executionId: Long, chutneyScenarioExecutionContext: ChutneyScenarioExecutionContext) {
        scenarioExecutions[executionId] = chutneyScenarioExecutionContext
    }

    fun endExecutionLatch() {
        syncExecutionSemaphore.release()
        endExecutionLatch.countDown()
    }

    enum class ListenerEvent { STARTED, SKIPPED, FINISHED, REPORT, DYNAMIC }

    fun notifyJUnitListener(
        event: ListenerEvent,
        testDescriptor: TestDescriptor,
        testResult: TestExecutionResult? = null,
        reportEntry: ReportEntry? = null,
        reason: String? = null
    ) {
        try {
            when (event) {
                STARTED -> request.engineExecutionListener.executionStarted(testDescriptor)
                SKIPPED -> request.engineExecutionListener.executionSkipped(testDescriptor, reason)
                FINISHED -> request.engineExecutionListener.executionFinished(testDescriptor, testResult)
                REPORT -> request.engineExecutionListener.reportingEntryPublished(testDescriptor, reportEntry)
                DYNAMIC -> request.engineExecutionListener.dynamicTestRegistered(testDescriptor)
            }
        } catch (e: Exception) {
            logger.warn("Notification failed", e)
        }
    }

    private fun executeClass(classDescriptor: ChutneyClassDescriptor) {
        syncExecutionSemaphore.acquire()

        val chutneyClassExecutionContext = ChutneyClassExecutionContext(this, classDescriptor)
        chutneyClassExecutionContext.execute()
    }

    private fun startExecution() {
        notifyJUnitListener(STARTED, request.rootTestDescriptor)
    }

    private fun endExecution() {
        endExecutionLatch.await()
        unregisterRxBus()

        notifyJUnitListener(FINISHED, request.rootTestDescriptor, successful())
    }

    private fun startScenarioExecution(event: StartScenarioExecutionEvent) {
        awaitForScenarioExecutionIdContextMapping(event)

        checkExecutionThen(event.executionId()) {
            it.startExecution(event.step)
        }
    }

    private fun beginStepExecution(event: BeginStepExecutionEvent) {
        checkExecutionThen(event.executionId()) {
            it.beginStepExecution(event.step)
        }
    }

    private fun endStepExecution(event: EndStepExecutionEvent) {
        checkExecutionThen(event.executionId()) {
            it.endStepExecution(event.step)
        }
    }

    private fun endScenarioExecution(event: EndScenarioExecutionEvent) {
        checkExecutionThen(event.executionId()) {
            it.endExecution(event.step)
        }
    }

    private fun checkExecutionThen(executionId: Long, block: (ChutneyScenarioExecutionContext) -> Unit) {
        scenarioExecutions[executionId]?.apply(block)
            ?: throw IllegalStateException("Cannot find execution [${executionId}]")
    }

    private fun awaitForScenarioExecutionIdContextMapping(event: StartScenarioExecutionEvent) {
        while (true) {
            if (scenarioExecutions[event.executionId()] != null) break
            TimeUnit.MILLISECONDS.sleep(50)
        }
    }

    private fun unregisterRxBus() {
        startScenarioDisposable.dispose()
        beginStepDisposable.dispose()
        endStepDisposable.dispose()
        endScenarioDisposable.dispose()
    }
}
