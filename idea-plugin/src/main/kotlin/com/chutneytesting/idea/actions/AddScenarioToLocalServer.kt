package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioDescriptionFromFileName
import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioIdFromFileName
import com.chutneytesting.idea.actions.converter.ScenarioV1ToV2Converter
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.diagnostic.Logger
import org.apache.commons.lang.StringEscapeUtils.escapeSql
import org.apache.commons.text.StringEscapeUtils
import org.hjson.JsonValue
import org.hjson.Stringify

class AddScenarioToLocalServer : RemoteScenarioBaseAction() {

    private val LOG = Logger.getInstance(AddScenarioToLocalServer::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val project = event.project ?: return
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return
        val id = getChutneyScenarioIdFromFileName(file.name) ?: return
        val titleAndDescription = getChutneyScenarioDescriptionFromFileName(file.name)
        try {
            val localServerURL = ChutneyServerRegistry.instance.myServer?.serverUrl ?: return
            var content = escapeSql(HJsonUtils.convertHjson(psiFile.text))
            if (ChutneyUtil.isChutneyV1Json(psiFile)) {
                content = JsonValue.readHjson(ScenarioV1ToV2Converter().convert(content)).toString(Stringify.PLAIN)
            }

            val query = "/api/scenario/v2/raw"
            val body =
                "{\"id\": $id ,\"content\":\"${StringEscapeUtils.escapeJson(content)}\", \"title\": \"$titleAndDescription\", \"description\":\"$titleAndDescription\"}"

            val result = HttpClient.post<Any>(ChutneyServerInfo(localServerURL, "", ""), query, body)
            EventDataLogger.logInfo("Scenario Added to Local Server.<br>", project)
        } catch (e: Exception) {
            LOG.debug(e.toString())
            EventDataLogger.logError(e.toString(), project)
        }
    }
}
