/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.actions

import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.kotlin.util.HttpClient
import com.google.gson.Gson
import com.intellij.notification.NotificationListener
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

class AddRemoveScenarioInCampaignAction(
    val campaign: Campaign,
    val scenarioId: Int,
    val selected: Boolean,
    text: String?,
    description: String?,
    icon: Icon?
) : AnAction(text, description, icon) {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        try {
            var campaignScenarios: MutableList<String> = campaign.scenarioIds.toMutableList()
            if (selected) {
                //remove from campaign
                campaignScenarios.remove(scenarioId.toString())

            } else {
                //add to campaign
                campaignScenarios = campaignScenarios.plus(scenarioId.toString()).toMutableList()
            }
            val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
            HttpClient.put<Any>(serverInfo,"/api/ui/campaign/v1", Gson().toJson(campaign.copy(scenarioIds = campaignScenarios)))

            EventDataLogger.logInfo(
                "scenario" + (if (selected) " removed from" else " added to") + " campaign with success.<br>" +
                        "<a href=\"${serverInfo.url}/#/campaign/${campaign.id}/executions\">Open Campaign in remote Chutney Server</a>",
                project,
                NotificationListener.URL_OPENING_LISTENER
            )
        } catch (e: Exception) {
            EventDataLogger.logError(e.toString(), project)
        }
    }
}
