package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.actions.converter.ScenarioV1ToV2Converter
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.notification.NotificationListener
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.ui.EditorNotifications
import org.apache.commons.text.StringEscapeUtils
import org.hjson.JsonValue
import org.hjson.Stringify

class UpdateRemoteScenarioFromLocal : RemoteScenarioBaseAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return

        val id = ChutneyUtil.getChutneyScenarioIdFromFileName(file.name)

        val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
        val result: Map<String, Any> = HttpClient.get(
            serverInfo, "/api/scenario/v2/raw/$id"
        )

        val version = result["version"].toString()

        val title = ChutneyUtil.getChutneyScenarioDescriptionFromFileName(file.name)
        val processJsonReference = ChutneyUtil.processJsonReference(psiFile.virtualFile)
        var hJsonString: String? = JsonValue.readHjson(processJsonReference).toString(Stringify.PLAIN)
        if (ChutneyUtil.isChutneyV1Json(psiFile)) {
            hJsonString =
                JsonValue.readHjson(ScenarioV1ToV2Converter().convert(processJsonReference)).toString(Stringify.PLAIN)
        }

        val query = "/api/scenario/v2/raw"
        val body = "{\"id\": \"$id\", \"title\": \"$title\", \"version\": $version, \"content\":\"${StringEscapeUtils.escapeJson(hJsonString)}\"}"


        try {
            HttpClient.post<Any>(serverInfo, query, body)
            EventDataLogger.logInfo(
                "Remote scenario file updated with success.<br>" +
                        "<a href=\"${serverInfo.url}/#/scenario/$id/executions?open=last&active=last\">Open in remote Chutney Server</a>",
                project,
                NotificationListener.URL_OPENING_LISTENER
            )
            EditorNotifications.getInstance(project).updateNotifications(file)

        } catch (e: Exception) {
            EventDataLogger.logError("Remote scenario file could not be updated.<br> cause: [$e]", project)
        }
    }
}
