package com.chutneytesting.kotlin.execution.report

import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object JsonReportWriter {

    private val om = ObjectMapper()
        .findAndRegisterModules()
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

    private val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault())

    fun reportAsJson(
        report: StepExecutionReportDto,
        pretty: Boolean = false
    ): String {
        return if (pretty) {
            om.writerWithDefaultPrettyPrinter().writeValueAsString(report)
        } else {
            om.writeValueAsString(report)
        }
    }

    fun jsonAsReport(
        report: String
    ): StepExecutionReportDto {
        return om.readValue(report, StepExecutionReportDto::class.java)
    }

    fun writeReport(
        report: StepExecutionReportDto,
        reportRootPath: String,
        pretty: Boolean = true
    ) {
        val reportPath = File(reportRootPath, report.environment)
        reportPath.mkdirs()
        File(
            reportPath,
            report.name.split(" ")
                .joinToString("", postfix = "." + formatter.format(Instant.now()) + ".json") { it.replaceFirstChar(Char::titlecase) }
        )
            .bufferedWriter()
            .use { it.write(reportAsJson(report, pretty)) }
    }
}
