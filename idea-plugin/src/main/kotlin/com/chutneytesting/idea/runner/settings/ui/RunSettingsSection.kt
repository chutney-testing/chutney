/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.runner.settings.ui


import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.intellij.ui.PanelWithAnchor
import javax.swing.JComponent

interface RunSettingsSection : PanelWithAnchor {

    fun resetFrom(runSettings: ChutneyRunSettings)

    fun applyTo(runSettings: ChutneyRunSettings)

    fun getComponent(creationContext: CreationContext): JComponent
}
