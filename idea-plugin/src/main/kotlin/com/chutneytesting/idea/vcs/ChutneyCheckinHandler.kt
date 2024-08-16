/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.vcs

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioDescriptionFromFileName
import com.chutneytesting.idea.ChutneyUtil.getChutneyScenarioIdFromFileName
import com.chutneytesting.idea.ChutneyUtil.isChutneyFragmentsJson
import com.chutneytesting.idea.ChutneyUtil.isChutneyJson
import com.chutneytesting.idea.ChutneyUtil.isRemoteChutneyJson
import com.chutneytesting.idea.actions.converter.ScenarioV1ToV2Converter
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.StringUtils.escapeSql
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationListener
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.ui.RefreshableOnComponent
import com.intellij.psi.PsiManager
import com.intellij.psi.search.searches.ReferencesSearch
import org.hjson.JsonValue
import org.hjson.Stringify
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class ChutneyCheckinHandler(private val checkinProjectPanel: CheckinProjectPanel) : CheckinHandler() {

    var updateChutneyScenarios: JCheckBox = JCheckBox("Update Remote Chutney Scenarios")

    override fun checkinSuccessful() {
        if (updateChutneyScenarios.isSelected) {
            val project = checkinProjectPanel.project
            if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
            val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!

            val psiScenariosToUpdateBecauseOfIceFragUsage = checkinProjectPanel.virtualFiles
                .filter { isChutneyFragmentsJson(it) }
                .mapNotNull { PsiManager.getInstance(project).findFile(it) }
                .map { ReferencesSearch.search(it) }
                .flatMap { it.findAll() }
                .map { it.element.containingFile }
                .filter { isChutneyJson(it) }
                .distinct()
                .toList()

            val psiScenariosModified = checkinProjectPanel.virtualFiles
                .filter { isChutneyJson(it) }
                .mapNotNull { PsiManager.getInstance(project).findFile(it) }
                .toList()

            (psiScenariosToUpdateBecauseOfIceFragUsage + psiScenariosModified)
                .filter { isRemoteChutneyJson(it) }
                .distinctBy { it.virtualFile.path }
                .forEach {
                    val processJsonReference = ChutneyUtil.processJsonReference(it.virtualFile)
                    val convert = ScenarioV1ToV2Converter().convert(processJsonReference)
                    val hJsonString = JsonValue.readHjson(convert).toString(Stringify.PLAIN)
                    val id = getChutneyScenarioIdFromFileName(it.name)
                    val title = getChutneyScenarioDescriptionFromFileName(it.name)
                    val escapeSql = escapeSql(hJsonString)
                    val query = "/api/scenario/v2/raw"


                    try {
                        val result: Map<String, Any> = HttpClient.get(
                            serverInfo, "$query/$id"
                        )
                        val version = result["version"].toString()
                        val body =
                            "{\"id\": $id ,\"title\": $title ,\"version\": $version , \"content\":\"${escapeSql}\"}"


                        HttpClient.post<Any>(serverInfo, query, body)
                        EventDataLogger.logInfo(
                            "Remote scenario files updated with success.<br>" +
                                    "<a href=\"${serverInfo.url}/#/scenario/\">Open Chutney Server</a>",
                            project,
                            NotificationListener.URL_OPENING_LISTENER
                        )

                    } catch (e: Exception) {
                        EventDataLogger.logError(e.toString(), project)
                    }
                }
        }
    }

    override fun getBeforeCheckinConfigurationPanel(): RefreshableOnComponent {
        return object : RefreshableOnComponent {

            override fun getComponent(): JComponent {
                val panel = JPanel(GridLayout(1, 0))
                panel.add(updateChutneyScenarios)
                return panel
            }

            override fun restoreState() {
                updateChutneyScenarios.isSelected = PropertiesComponent.getInstance(checkinProjectPanel.project)
                    .getBoolean("updateChutneyScenarios")
            }

            override fun saveState() {
                PropertiesComponent.getInstance(checkinProjectPanel.project)
                    .setValue("updateChutneyScenarios", updateChutneyScenarios.isSelected)
            }

            @Deprecated("Deprecated in Java")
            override fun refresh() {
            }
        }
    }
}
