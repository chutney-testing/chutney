package com.chutneytesting.kotlin.execution.report

import com.chutneytesting.kotlin.ChutneyConfigurationParameters
import com.chutneytesting.kotlin.execution.CHUTNEY_ROOT_PATH_DEFAULT
import com.chutneytesting.kotlin.util.SystemEnvConfigurationParameters
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.File
import java.util.*

object SiteGeneratorMain {
    @JvmStatic
    fun main(args: Array<String>) {
        SiteGenerator(args.getOrElse(0) { null }).generateSite()
    }
}

const val CHUTNEY_REPORT_ROOT_PATH_DEFAULT = "$CHUTNEY_ROOT_PATH_DEFAULT/reports"

class SiteGenerator(reportRootPathInput: String? = null) {

    private val reportListFileName = "reports-list.json"
    private val pathResolver = PathMatchingResourcePatternResolver()

    private val systemParameters = SystemEnvConfigurationParameters()

    private val reportRootPath: String = Optional.ofNullable(reportRootPathInput).orElse(
        systemParameters.get(ChutneyConfigurationParameters.CONFIG_REPORT_ROOT_PATH.parameter).orElse(
            ChutneyConfigurationParameters.CONFIG_REPORT_ROOT_PATH.defaultString()
        )
    )

    fun generateSite() {
        if (createReportsListFile()) {
            copySiteFiles()
        }
    }

    private fun createReportsListFile(): Boolean {
        if (!File(reportRootPath).exists()) {
            println("Reports' root path does not exist: $reportRootPath")
            return false
        } else {
            val reportsListFile = File(reportRootPath, reportListFileName)
            reportsListFile
                .bufferedWriter()
                .use { out ->
                    out.write("[" + readReportsNames()
                        .joinToString(
                            separator = "\"},{\"",
                            prefix = "{\"", "\"}"
                        ) { p -> "env\":\"${p.first}\",\"scenario\":\"${p.second}" } + "]")
                }
            println("Reports' list file generated at ${reportsListFile.absolutePath}")
            return true
        }
    }

    private fun copySiteFiles() {
        pathResolver.getResources("classpath*:chutney-report-website/*").forEach {
            it.inputStream.copyTo(
                out = File(reportRootPath, it.filename!!).outputStream()
            )
        }
        println("Reports web site copied into ${File(reportRootPath).absolutePath}")
    }

    private fun readReportsNames(): List<Pair<String, String>> {
        return File(reportRootPath)
            .listFiles { file -> file.isDirectory }
            ?.flatMap { envDir ->
                envDir?.listFiles { _, name ->
                    name.endsWith(".json")
                }?.toList() ?: emptyList()
            }
            ?.map { reportFile ->
                Pair(reportFile.parentFile.name, reportFile.name)
            } ?: emptyList()
    }
}
