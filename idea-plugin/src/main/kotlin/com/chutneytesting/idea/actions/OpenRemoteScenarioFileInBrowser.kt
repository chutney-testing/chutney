package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioIdFromFileName
import com.chutneytesting.idea.settings.ChutneySettings
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class OpenRemoteScenarioFileInBrowser : RemoteScenarioBaseAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val id = getChutneyScenarioIdFromFileName(file.name) ?: return
        BrowserUtil.browse("${ChutneySettings.getInstance().state.serverInfo()!!.url}/#/scenario/$id/executions?open=last&active=last")
    }
}
