/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.runner

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.actions.converter.JsonSerializer
import com.chutneytesting.idea.actions.converter.ScenarioV1ToV2Converter
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.testframework.sm.ServiceMessageBuilder
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.yaml.psi.YAMLFile


data class ScenarioBase(@SerializedName("scenario") val scenario: Scenario)

data class Scenario(
    @SerializedName("gwtType") val gwtType: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("steps") val steps: List<Steps>?
)

data class Steps(
    @SerializedName("gwtType") val gwtType: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("x-\$ref") val xRef: String?,
    @SerializedName("steps") val steps: List<Steps>?
)

data class Request(val content: String, val params: MutableMap<String, String>)
data class Report(
    val executionId: Long,
    val scenarioName: String,
    val environment: String,
    val contextVariables: Map<String, Any>,
    val report: Base
)

data class Base(
    val executionId: Long,
    val name: String?,
    val duration: Number?,
    val startDate: String?,
    val status: String?,
    val information: List<Any>?,
    val errors: List<Any>?,
    val steps: List<Base>?,
    //val context: Context?,
    val type: String?,
    val targetName: String?,
    val targetUrl: String?,
    val strategy: String?,
    val evaluatedInputs: Map<String, Any>?,
    val stepOutputs: Map<String, Any>?,
)

class JsonTestReportsParser(
    val configuration: ChutneyRunConfiguration,
    val project: Project,
    val serverUrl: String,
    val handler: ProcessHandler,
    val jsonFile: VirtualFile
) {

    private val mapper = JsonSerializer()

    fun parseReports(index: Int) {
        val query = "/api/idea/scenario/execution/GLOBAL"
        val findFile = ApplicationManager.getApplication()
            .runReadAction(Computable<PsiFile> { PsiManager.getInstance(project).findFile(jsonFile) })
        val virtualFile = findFile.virtualFile
        val json =
            if (findFile is JsonFile || findFile is YAMLFile)
                if (virtualFile is LightVirtualFile) findFile.text else ChutneyUtil.processJsonReference(virtualFile)
            else {
                error("Unsupported")
            }

        val jsonString = if (ChutneyUtil.isChutneyV1Json(findFile)) {
            mapper.toString(ScenarioV1ToV2Converter().getScenarioV2(json).asMap())
        } else {
            json
        }
        val request = Request(content = jsonString, params = configuration.getRunSettings().variables.envs)
        val body = Gson().toJson(request, Request::class.java)
        try {
            val result = HttpClient.post<Report>(ChutneyServerInfo(serverUrl, "", ""), query, body)
            val report = result.report
            if (report.steps?.size != 0) {
                report.steps?.forEachIndexed { testCaseIndex, base ->
                    parseTestCase(
                        base,
                        index.toString() + "_" + testCaseIndex.plus(1).toString(),
                        index.toString(),
                        findFile
                    )
                }
            } else {
                parseTestCase(report, index.toString() + "_" + "1", index.toString(), findFile)
            }
            handler.notifyTextAvailable(
                ServiceMessageBuilder.testSuiteFinished(report.name!!)
                    .addAttribute("locationHint", jsonFile.url)
                    .addAttribute("nodeId", index.toString())
                    .addAttribute("parentNodeId", "0")
                    .toString() + "\n", ProcessOutputTypes.STDOUT
            )
        } catch (e: Exception) {
            handler.notifyTextAvailable("\n" + "Test running failed: " + e.message + "\n", ProcessOutputTypes.STDERR)
        }

    }

    private fun parseTestCase(testCase: Base, nodeId: String, parentNodeId: String, findFile: PsiFile) {
        val name = testCase.name ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(findFile)
        val offset = getOffsetByTestCaseName(findFile, name)
        val line = document?.getLineNumber(offset)
        val locationHint = jsonFile.url + ":" + line
        val duration = getDuration(testCase)

        when (testCase.status) {
            "SUCCESS" -> {
                val sysout = testCase.information?.joinToString(separator = "\n") + "\n" +
                        testCase.steps?.map { it.information?.joinToString(separator = "\n") }
                            ?.joinToString(separator = "\n")
                handler.notifyTextAvailable(
                    ServiceMessageBuilder.testStdOut(name)
                        .addAttribute("nodeId", nodeId)
                        .addAttribute("parentNodeId", parentNodeId)
                        .addAttribute("out", sysout)
                        .toString() + "\n", ProcessOutputTypes.STDOUT
                )
                reportTestFinished(nodeId, parentNodeId, name, duration)
            }

            "FAILURE" -> {
                var failureMessage = testCase.errors?.joinToString(separator = "\n") + "\n" +
                        testCase.steps?.map { it.errors?.joinToString(separator = "\n") }
                            ?.joinToString(separator = "\n")
                val failureData = ChutneyJsonComparisonFailureParser().tryParse(failureMessage)
                if (failureData != null) {
                    failureMessage += "\n <Click to see assertion> $locationHint"
                }
                handler.notifyTextAvailable(
                    ServiceMessageBuilder.testStdOut(name)
                        .addAttribute("nodeId", nodeId)
                        .addAttribute("parentNodeId", parentNodeId)
                        .addAttribute("out", failureMessage)
                        .toString() + "\n", ProcessOutputTypes.STDERR
                )
                reportTestFailure(nodeId, parentNodeId, name, true, failureMessage, failureData)
            }

            "NOT_EXECUTED" -> {
                reportTestNotExecuted(nodeId, parentNodeId, name)
            }

        }

        if (testCase.steps?.size != 0) {
            testCase.steps?.forEachIndexed { testCaseIndex, base ->
                parseTestCase(
                    base,
                    nodeId + "_" + testCaseIndex.plus(1).toString(),
                    nodeId,
                    findFile
                )
            }
        }

    }

    private fun getOffsetByTestCaseName(psiFile: PsiFile?, name: String): Int {
        if (psiFile !is JsonFile) {
            return 1
        }
        return ApplicationManager.getApplication().runReadAction(Computable<Int> {
            val steps = PsiTreeUtil.findChildOfType(psiFile, JsonArray::class.java)
            val children = steps?.children
            for (i in children!!) {
                if (i is JsonObject) {
                    val filter = i.propertyList.find { it.name == "name" }
                    val value = filter?.value
                    if (value?.text == "\"$name\"") {
                        return@Computable value.textOffset
                    }
                }
            }
            return@Computable 1
        })

    }


    private fun reportTestFinished(nodeId: String, parentNodeId: String, methodName: String, duration: Long?) {
        handler.notifyTextAvailable(
            ServiceMessageBuilder.testFinished(methodName)
                .addAttribute("nodeId", nodeId)
                .addAttribute("parentNodeId", parentNodeId)
                .addAttribute("duration", duration.toString())
                .toString() + "\n", ProcessOutputTypes.STDOUT
        )
    }

    private fun reportTestNotExecuted(nodeId: String, parentNodeId: String, methodName: String) {
        handler.notifyTextAvailable(
            ServiceMessageBuilder.testIgnored(methodName)
                .addAttribute("nodeId", nodeId)
                .addAttribute("parentNodeId", parentNodeId)
                .toString() + "\n", ProcessOutputTypes.STDOUT
        )
    }

    private fun reportTestFailure(
        nodeId: String,
        parentNodeId: String,
        methodName: String,
        testError: Boolean,
        failureMessage: String,
        failureData: ChutneyComparisonFailureData?
    ) {
        val testErrorMessage = ServiceMessageBuilder.testFailed(methodName)
            .addAttribute("nodeId", nodeId)
            .addAttribute("parentNodeId", parentNodeId)
            .addAttribute("message", failureMessage)
        if (failureData != null) {
            testErrorMessage.addAttribute("expected", failureData.expected)
            testErrorMessage.addAttribute("actual", failureData.actual)
        }
        handler.notifyTextAvailable(testErrorMessage.toString() + "\n", ProcessOutputTypes.STDOUT)
    }

    private fun getDuration(testcase: Base): Long? {
        val timeValue = testcase.duration
        return (timeValue)?.toLong()
    }

}
