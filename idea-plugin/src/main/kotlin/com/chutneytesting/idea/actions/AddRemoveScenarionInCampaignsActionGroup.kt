package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*

class DynamicActionGroup : ActionGroup() {

    private fun getCampaigns() =
        HttpClient.get<List<Campaign>>(ChutneySettings.getInstance().state.serverInfo()!!, "/api/ui/campaign/v1")

    override fun getChildren(event: AnActionEvent?): Array<out AnAction> {
        val project = event?.project ?: return emptyArray()
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return emptyArray()

        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return emptyArray()
        val id = ChutneyUtil.getChutneyScenarioIdFromFileName(file.name)
        val campaigns = getCampaigns()
        return campaigns.map {
            val selected = it.scenarioIds.contains(id.toString())
            AddRemoveScenarioInCampaignAction(
                it,
                id!!,
                selected,
                it.id.toString() + "-" + it.title,
                it.title,
                if (selected) AllIcons.Actions.Checked_selected else null
            )
        }.toTypedArray()
    }

    override fun update(event: AnActionEvent) {
        // Enable/disable depending
        val project = event.project
        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        val psiFile = event.getData(LangDataKeys.PSI_FILE)
        event.presentation.isEnabledAndVisible =
            project != null && psiFile != null && virtualFile != null && !virtualFile.isDirectory &&
                    ChutneyUtil.isRemoteChutneyJson(psiFile)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT;
    }
}
