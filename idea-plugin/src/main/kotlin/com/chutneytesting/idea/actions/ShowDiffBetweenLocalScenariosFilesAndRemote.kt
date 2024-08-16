/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioIdFromFileName
import com.chutneytesting.idea.ChutneyUtil.isRemoteChutneyJson
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.runner.settings.ChutneySettingsUtil
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffDialogHints
import com.intellij.diff.chains.SimpleDiffRequestChain
import com.intellij.diff.impl.CacheDiffRequestChainProcessor
import com.intellij.diff.impl.DiffWindow
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.json.psi.JsonElementGenerator
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.psi.codeStyle.CodeStyleManager


class ShowDiffBetweenLocalScenariosFilesAndRemote : AnAction() {

    private val LOG = Logger.getInstance(ShowDiffBetweenLocalScenariosFilesAndRemote::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        if (!file.isDirectory) {
            return
        }

        val scenariosInDirectory = ChutneySettingsUtil
            .collectChutneyScenarioFilesInDirectory(project, file)
            .filter { isRemoteChutneyJson(it) }
            .map { getChutneyScenarioIdFromFileName(it.name)!! to it }
            .toMap()

        try {
            val list = scenariosInDirectory.map {

                val result: Map<String, Any> = HttpClient.get(
                    ChutneySettings.getInstance().state.serverInfo()!!, "/api/scenario/v2/raw/${it.key}"
                )

                val rawScenario = result["content"].toString()
                val remoteScenario = HJsonUtils.convertHjson(rawScenario)

                val createRemotePsiFileFromText = JsonElementGenerator(project).createDummyFile(remoteScenario)
                val detectedLineSeparator = LoadTextUtil.detectLineSeparator(it.value, true) ?: ""
                val remoteScenarioFileContent =
                    CodeStyleManager.getInstance(project).reformat(createRemotePsiFileFromText)
                        .text.replace(Regex("\\v+"), detectedLineSeparator)
                val content1 = DiffContentFactory.getInstance().create(project, it.value)
                val content2 = DiffContentFactory.getInstance().create(remoteScenarioFileContent)
                val request = SimpleDiffRequest(
                    "Scenario ${it.value.name}",
                    content1,
                    content2,
                    "Local Scenario File",
                    "Remote Scenario File"
                )
                request
            }.toList()
            val cacheDiffRequestChainProcessor = CacheDiffRequestChainProcessor(project, SimpleDiffRequestChain(list))
            val diffWindow = DiffWindow(project, cacheDiffRequestChainProcessor.requestChain, DiffDialogHints.FRAME)
            diffWindow.show()

        } catch (e: Exception) {
            LOG.debug(e.toString())
            EventDataLogger.logError(e.toString(), project)
        }
    }

    override fun update(event: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = event.project
        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = project != null && virtualFile != null && virtualFile.isDirectory
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
