package com.chutneytesting.idea.vcs

import com.chutneytesting.idea.ChutneyIcons
import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.kotlin.util.HttpClient
import com.chutneytesting.kotlin.util.HttpClientException
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications

class ChutneyOutdatedVersionNotifier(val project: Project) : EditorNotifications.Provider<EditorNotificationPanel>(),
    DumbAware {

    private val KEY =
        Key.create<EditorNotificationPanel>("outdated.chutney.scenario.source.file.editing.notification.panel")

    override fun getKey(): Key<EditorNotificationPanel> {
        return KEY
    }

    override fun createNotificationPanel(virtualFile: VirtualFile, fileEditor: FileEditor): EditorNotificationPanel? {
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return null
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null
        if (!ChutneyUtil.isChutneyJson(psiFile)) return null
        val id = ChutneyUtil.getChutneyScenarioIdFromFileName(virtualFile.name) ?: return null

        val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
        try {
            val result: Map<String, Any> = HttpClient.get(serverInfo, "/api/scenario/v2/raw/$id")
            val rawScenario = result["content"].toString()
            val replace = HJsonUtils.convertHjson(rawScenario).replace("\r\n", "\n")
            if (replace != HJsonUtils.convertHjson(psiFile.text)) {
                val panel = EditorNotificationPanel()
                panel.icon(ChutneyIcons.ChutneyToolWindow)
                panel.createActionLabel("Show Diff", "Chutney.ShowDiffBetweenLocalScenarioFileAndRemote")
                panel.createActionLabel("Update Local Scenario", "Chutney.UpdateLocalScenarioFromRemoteServer")
                panel.createActionLabel("Update Remote Scenario", "Chutney.UpdateRemoteScenarioFromLocal")
                panel.text = "Outdated version."
                return panel
            }
        } catch (tce: HttpClientException) {
            val panel = EditorNotificationPanel()
            panel.icon(ChutneyIcons.ChutneyToolWindow)
            panel.text = tce.message?: "Cannot check scenario version : ${tce.cause?.message}"
            return panel
        }
        return null
    }
}
