/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ChutneyServerStopAction : AnAction("Stop the local server", null, AllIcons.Actions.Suspend) {
    override fun update(e: AnActionEvent) {
        val server = ChutneyServerRegistry.instance.myServer
        e.presentation.isEnabled = server != null && server.isProcessRunning
    }

    override fun actionPerformed(e: AnActionEvent) {
        val server = ChutneyServerRegistry.instance.myServer
        server?.shutdownAsync()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
      return ActionUpdateThread.BGT
    }
}
