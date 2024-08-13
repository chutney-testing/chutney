/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.logger

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.awt.RelativePoint
import javax.swing.SwingUtilities

object BalloonLogger {

    fun logBalloonError(htmlMessage: String, project: Project) {
        SwingUtilities.invokeLater {
            val statusBar = WindowManager.getInstance().getStatusBar(project)
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(htmlMessage, MessageType.ERROR, null)
                .setFadeoutTime(7500).createBalloon().show(
                RelativePoint.getCenterOf(statusBar.component!!),
                Balloon.Position.atRight
            )
        }
    }

    fun logBalloonInfo(htmlMessage: String, project: Project) {
        SwingUtilities.invokeLater {
            val statusBar = WindowManager.getInstance().getStatusBar(project)
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(htmlMessage, MessageType.INFO, null)
                .setFadeoutTime(7500).createBalloon().show(
                RelativePoint.getCenterOf(statusBar.component!!),
                Balloon.Position.atRight
            )
        }
    }
}//
