package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioIdFromFileName
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffManager
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.json.psi.JsonElementGenerator
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.codeStyle.CodeStyleManager

class ShowDiffBetweenLocalScenarioFileAndRemote : RemoteScenarioBaseAction() {

    private val LOG = Logger.getInstance(ShowDiffBetweenLocalScenarioFileAndRemote::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        try {
            val id = getChutneyScenarioIdFromFileName(file.name) ?: return
            val result: Map<String, Any> = HttpClient.get(
                ChutneySettings.getInstance().state.serverInfo()!!, "/api/scenario/v2/raw/$id"
            )

            val rawScenario: String = result["content"].toString()

            val remoteScenario = HJsonUtils.convertHjson(rawScenario)
            val createRemotePsiFileFromText = JsonElementGenerator(project).createDummyFile(remoteScenario)
            val remoteScenarioFileContent =
                CodeStyleManager.getInstance(project).reformat(createRemotePsiFileFromText).text.replace(
                    Regex("\\v+"),
                    file.detectedLineSeparator!!
                )
            val content1 = DiffContentFactory.getInstance().create(project, file)
            val content2 = DiffContentFactory.getInstance().create(remoteScenarioFileContent)
            val request = SimpleDiffRequest(
                "Show Diff Between Local Scenario File and Remote",
                content1,
                content2,
                "Local Scenario File",
                "Remote Scenario File"
            )
            DiffManager.getInstance().showDiff(project, request)

        } catch (e: Exception) {
            LOG.debug(e.toString())
            EventDataLogger.logError(e.toString(), project)
        }
    }
}
