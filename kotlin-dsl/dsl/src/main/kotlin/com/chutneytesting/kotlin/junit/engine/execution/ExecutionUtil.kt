package com.chutneytesting.kotlin.junit.engine.execution

import com.chutneytesting.engine.domain.execution.engine.step.Step
import com.chutneytesting.engine.domain.execution.report.Status
import org.junit.platform.engine.TestExecutionResult

open class NoStackTraceAssertionError(message: String) : AssertionError(message) {
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

class StepExecutionFailedException(step: Step) :
    NoStackTraceAssertionError(
        "Step [${step.definition().name}] execution failed : \n${
            step.errors().joinToString("\n")
        }"
    )

class UnresolvedScenarioEnvironmentException(message: String?) :
    NoStackTraceAssertionError(message ?: "Cannot resolve environment")

fun Step.findSubStepPath(toBeFound: Step): List<Step> {
    if (this == toBeFound) {
        return listOf(this)
    }

    this.subSteps().forEach {
        if (it == toBeFound) {
            return listOf(this, it)
        }

        val subStepPath = it.findSubStepPath(toBeFound)
        if (subStepPath.isNotEmpty()) {
            return listOf(listOf(this), subStepPath).flatten()
        }
    }

    return emptyList()
}

fun Step.isExecutionFailed(): Boolean {
    return Status.FAILURE == this.status()
}

fun testExecutionResultFromStatus(throwable: Throwable? = null, vararg status: Status): TestExecutionResult {
    if (status.isEmpty()) {
        return TestExecutionResult.successful()
    }

    val worst = Status.worst(status.asList())
    return if (Status.SUCCESS == worst) {
        TestExecutionResult.successful()
    } else {
        TestExecutionResult.failed(throwable)
    }
}
