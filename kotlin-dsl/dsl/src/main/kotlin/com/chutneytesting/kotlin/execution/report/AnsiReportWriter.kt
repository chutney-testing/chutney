package com.chutneytesting.kotlin.execution.report

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.engine.api.execution.StatusDto.FAILURE
import com.chutneytesting.engine.api.execution.StatusDto.NOT_EXECUTED
import com.chutneytesting.engine.api.execution.StatusDto.STOPPED
import com.chutneytesting.engine.api.execution.StatusDto.SUCCESS
import com.chutneytesting.engine.api.execution.StatusDto.WARN
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.kotlin.execution.report.AnsiColor.BLUE
import com.chutneytesting.kotlin.execution.report.AnsiColor.GREEN
import com.chutneytesting.kotlin.execution.report.AnsiColor.MAGENTA
import com.chutneytesting.kotlin.execution.report.AnsiColor.RED
import com.chutneytesting.kotlin.execution.report.AnsiColor.RESET
import com.chutneytesting.kotlin.execution.report.AnsiColor.YELLOW
import java.io.OutputStream

enum class AnsiColor(val color: String) {

    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m"),

    RESET("\u001b[0m");

    fun bright(): String {
        return this.color.removeSuffix("m").plus(";1m")
    }
}

class AnsiReportWriter(private val withColor: Boolean = true) {

    fun printReport(
        report: StepExecutionReportDto,
        out: OutputStream = System.out
    ) {
        val bufferedWriter = out.bufferedWriter()
        val builder = StringBuilder(reportHeader(report))
        report.steps?.forEach {
            builder.append(step(it)).appendLine()
        }
        bufferedWriter.appendLine()
        bufferedWriter.write(builder.toString())
        bufferedWriter.appendLine()
        bufferedWriter.flush()
    }

    fun printStep(
        step: StepExecutionReportDto,
        indent: String = "  ",
        out: OutputStream = System.out
    ) {
        val bufferedWriter = out.bufferedWriter()
        bufferedWriter.appendLine()
        bufferedWriter.write(step(step, indent))
        bufferedWriter.appendLine()
        bufferedWriter.appendLine()
        bufferedWriter.flush()
    }

    private fun reportHeader(report: StepExecutionReportDto): String {
        return color(
            "[" + report.status + "] " + "scenario: \"" + report.name + "\"" + " on environment " + report.environment + "\n",
            report.status
        )
    }

    private fun step(step: StepExecutionReportDto, indent: String = "  "): String {
        val builder = StringBuilder(stepHeader(step, indent))

        errors(step, indent).takeIf { it.isNotBlank() }?.let {
            builder.appendLine().append(it)
        }

        information(step, indent).takeIf { it.isNotBlank() }?.let {
            builder.appendLine().append(it)
        }

        if (step.steps.isNotEmpty()) {
            step.steps.forEach {
                builder.appendLine()
                    .append(step(it, "$indent  "))
            }
        } else {
            builder.appendLine()
                .append("$indent  " + step.type + " " + mapAsString(step.context.evaluatedInputs))
            if (step.targetName.isNotBlank()) {
                builder.appendLine()
                    .append("$indent  on { " + step.targetName + ": " + step.targetUrl + " }")
            }
        }

        return builder.toString()
    }

    private fun stepHeader(step: StepExecutionReportDto, indent: String = "  "): String {
        return indent + color("[" + step.status + "] " + step.name + " [" + step.strategy.ifBlank { "default" } + "]",
            step.status)
    }

    private fun mapAsString(map: Map<String, Any>): String {
        return map.entries.joinToString(separator = ",", prefix = "{ ", postfix = "}") {
            it.key + ": " + "\"" + it.value + "\""
        }
    }

    private fun color(s: String, status: StatusDto): String {
        return if (withColor) {
            when (status) {
                SUCCESS -> GREEN.bright()
                WARN -> YELLOW.bright()
                FAILURE -> RED.bright()
                STOPPED -> YELLOW.bright()
                NOT_EXECUTED -> MAGENTA.bright()
                else -> ""
            }.plus(s + RESET.color)
        } else s
    }

    private fun errors(step: StepExecutionReportDto, indent: String): String {
        return step.errors
            .filter { !it.isNullOrBlank() }
            .joinToString("\n") {
                color("$indent  >> $it", step.status)
            }
    }

    private fun information(step: StepExecutionReportDto, indent: String): String {
        return step.information
            .filter { !it.isNullOrBlank() }
            .joinToString("\n") {
                if (withColor) {
                    BLUE.bright() + "$indent  >> $it" + RESET.color
                } else {
                    "$indent  >> $it"
                }
            }
    }
}
