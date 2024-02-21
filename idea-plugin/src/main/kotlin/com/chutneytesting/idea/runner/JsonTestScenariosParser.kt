package com.chutneytesting.idea.runner

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.actions.converter.ScenarioV2ToV1Converter
import com.google.gson.Gson
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


class JsonTestScenariosParser(
    val configuration: ChutneyRunConfiguration,
    val project: Project,
    val handler: ProcessHandler,
    val jsonFile: VirtualFile
) {

    fun parseScenarios(index: Int) {
        val findFile = ApplicationManager.getApplication()
            .runReadAction(Computable<PsiFile> { PsiManager.getInstance(project).findFile(jsonFile) })
        val text = findFile?.text
        val virtualFile = findFile.virtualFile
        val json =
            if (findFile is JsonFile || findFile is YAMLFile) {
                if (virtualFile is LightVirtualFile) text else ChutneyUtil.processJsonReference(virtualFile)
            } else {
                error("Unsupported")
            }
        val scenarioBase = if (ChutneyUtil.isChutneyV1Json(findFile)) {
            parseScenario(json)
        } else {
            parseScenarioV2ToV1(json)
        }
        handler.notifyTextAvailable(
            ServiceMessageBuilder.testSuiteStarted(scenarioBase.scenario.name ?: "<no-name>")
                .addAttribute("locationHint", jsonFile.url)
                .addAttribute("nodeId", index.toString())
                .addAttribute("parentNodeId", "0")
                .toString() + "\n", ProcessOutputTypes.STDOUT
        )
        try {
            val scenario = scenarioBase.scenario
            if (scenario.steps?.size != 0) {
                scenario.steps?.forEachIndexed { testCaseIndex, base ->
                    parseTestCase(
                        base,
                        index.toString() + "_" + testCaseIndex.plus(1).toString(),
                        index.toString(),
                        findFile
                    )
                }
            }

        } catch (e: Exception) {
            handler.notifyTextAvailable(
                ServiceMessageBuilder.testSuiteFinished(
                    scenarioBase.scenario.name
                        ?: "<no-name>"
                )
                    .addAttribute("locationHint", jsonFile.url)
                    .addAttribute("nodeId", index.toString())
                    .addAttribute("parentNodeId", "0")
                    .toString() + "\n", ProcessOutputTypes.STDOUT
            )
        }

    }

    private fun parseScenario(text: String?): ScenarioBase {
        return Gson().fromJson(text, ScenarioBase::class.java)
    }

    private fun parseScenarioV2ToV1(text: String?): ScenarioBase {
        return Gson().fromJson(ScenarioV2ToV1Converter().convert(text), ScenarioBase::class.java)
    }

    private fun parseTestCase(testCase: Steps, nodeId: String, parentNodeId: String, findFile: PsiFile) {
        val name = testCase.name ?: "<no-name>"
        val document = PsiDocumentManager.getInstance(project).getDocument(findFile)
        val offset = getOffsetByTestCaseName(findFile, name)
        val line = document?.getLineNumber(offset)
        val locationHint = jsonFile.url + ":" + line
        handler.notifyTextAvailable(
            ServiceMessageBuilder.testStarted(name)
                .addAttribute("locationHint", locationHint)
                .addAttribute("nodeId", nodeId)
                .addAttribute("parentNodeId", parentNodeId)
                .toString() + "\n", ProcessOutputTypes.STDOUT
        )
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
        if (psiFile !is JsonFile) return 1
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


}
