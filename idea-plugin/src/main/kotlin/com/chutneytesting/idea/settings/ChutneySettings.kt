/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.settings

import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

@State(name = "ChutneySettings", storages = [Storage("chutney.xml")])
class ChutneySettings : PersistentStateComponent<ChutneySettings.ChutneySettingsState> {

    private var settingsState: ChutneySettingsState = ChutneySettingsState()

    class ChutneySettingsState(
        var url: String? = "",
        var user: String? = "",
        var password: String? = "",
        var proxyUrl: String? = "",
        var proxyUser: String? = "",
        var proxyPassword: String? = ""
    ) {
        fun serverInfo(): ChutneyServerInfo? {
            if (!url.isNullOrBlank()) {
                return try {
                    ChutneyServerInfo(
                        url = url!!,
                        user = user ?: "",
                        password = password ?: "",
                        proxyUrl = proxyUrl.takeIf { ! it.isNullOrBlank() },
                        proxyUser = proxyUser.takeIf { ! it.isNullOrBlank() },
                        proxyPassword = proxyPassword.takeIf { ! it.isNullOrBlank() }
                    )
                } catch (e: Exception) {
                    null;
                }
            }
            return null
        }
    }

    override fun getState(): ChutneySettingsState {
        return settingsState
    }

    override fun loadState(state: ChutneySettingsState) {
        settingsState = state
    }

    companion object {
        fun getInstance(): ChutneySettings {
            return ApplicationManager.getApplication().getService(ChutneySettings::class.java)
        }

        fun checkRemoteServerUrlConfig(project: Project): Boolean {
            val serverInfo = getInstance().state.serverInfo()
            if (serverInfo == null || serverInfo.url.isBlank() || serverInfo.user.isBlank() || serverInfo.password.isBlank()) {
                EventDataLogger.logError(
                    " <a href=\"configure\">Configure</a> Missing remote configuration server, please check url, user, password and proxy if needed",
                    project
                ) { _, _ ->
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, "Chutney")
                }
                return false
            }
            return true
        }
    }
}
