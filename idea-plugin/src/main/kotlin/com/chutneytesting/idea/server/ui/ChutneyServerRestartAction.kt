package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.chutneytesting.idea.server.ChutneyServerSettingsManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ChutneyServerRestartAction(private val mySession: ChutneyToolWindowSession) : AnAction() {
    override fun update(e: AnActionEvent) {
        val runningServer = ChutneyServerRegistry.instance.myServer
        val presentation = e.presentation
        if (runningServer != null && runningServer.isProcessRunning) {
            presentation.icon = AllIcons.Actions.Restart
            presentation.text = "Rerun local server"
        } else {
            presentation.icon = AllIcons.Actions.Execute
            presentation.text = "Start a local server"
        }
        e.presentation.isEnabled = true
    }

    override fun actionPerformed(e: AnActionEvent) {
        mySession.saveSettings()
        mySession.restart(ChutneyServerSettingsManager.loadSettings())
    }

}
