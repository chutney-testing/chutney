package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.notification.NotificationListener
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.ui.EditorNotifications
import com.intellij.util.ui.UIUtil

class UpdateLocalScenarioFromRemoteServer : RemoteScenarioBaseAction() {

    private val LOG = Logger.getInstance(UpdateLocalScenarioFromRemoteServer::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return
        val document = editor.document
        val id = ChutneyUtil.getChutneyScenarioIdFromFileName(file.name)
        try {

            val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
            val result: Map<String, Any> = HttpClient.get(
                serverInfo, "/api/scenario/v2/raw/$id"
            )

            val rawScenario = result["content"].toString()
            val runnable = Runnable {
                document.setText(HJsonUtils.convertHjson(rawScenario).replace("\r\n", "\n"))
                FileDocumentManager.getInstance().saveDocument(document)
                UIUtil.invokeAndWaitIfNeeded(Runnable {
                    CodeStyleManager.getInstance(project).reformat(psiFile)
                })
            }
            WriteCommandAction.runWriteCommandAction(project, runnable)
            EventDataLogger.logInfo(
                "Local scenario file updated with success.<br>" +
                        "<a href=\"${serverInfo.url}/#/scenario/$id/executions?open=last&active=last\">Open in remote Chutney Server</a>",
                project,
                NotificationListener.URL_OPENING_LISTENER
            )

            EditorNotifications.getInstance(project).updateNotifications(file)

        } catch (e: Exception) {
            LOG.debug(e.toString())
            EventDataLogger.logError(e.toString(), project)
        }
    }
}
