/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.settings

import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class ChutneySettingsConfigurable :
    SearchableConfigurable, Configurable.NoScroll {

    val url: JBTextField = JBTextField()
    val user: JBTextField = JBTextField()
    val password: JBPasswordField = JBPasswordField()
    val proxyUrl: JBTextField = JBTextField()
    val proxyUser: JBTextField = JBTextField()
    val proxyPassword: JBPasswordField = JBPasswordField()
    val chutneySettings: ChutneySettings = ChutneySettings.getInstance()

    override fun isModified(): Boolean {
        return stateFromFields() != chutneySettings.state
    }

    override fun getDisplayName(): String {
        return "Chutney"
    }

    override fun getId(): String {
        return "chutney.tools.settings"
    }

    override fun reset() {
        initFields()
    }

    override fun apply() {
        this.chutneySettings.loadState(stateFromFields())
    }

    private fun stateFromFields() = ChutneySettings.ChutneySettingsState(
        url = url.text,
        user = user.text,
        password = String(password.password),
        proxyUrl = proxyUrl.text,
        proxyUser = proxyUser.text,
        proxyPassword = String(proxyPassword.password)
    )

    private fun initFields() {
        val serverInfo = chutneySettings.state.serverInfo()
        url.text = serverInfo?.url
        user.text = serverInfo?.user
        password.text = serverInfo?.password
        proxyUrl.text = serverInfo?.proxyUrl
        proxyUser.text = serverInfo?.proxyUser
        proxyPassword.text = serverInfo?.proxyPassword
    }

    override fun createComponent(): JComponent {
        initFields()

        val checkConnectionButton = JButton("Check connection")
        val checkLabel = JBLabel("").apply { isVisible = false }

        checkConnectionButton.addActionListener {
            try {
                val serverInfo = ChutneyServerInfo(
                    url.text,
                    user.text,
                    String(password.password),
                    proxyUrl.text.ifBlank { null },
                    proxyUser.text.ifBlank { null },
                    String(proxyPassword.password).ifBlank { null }
                )
                HttpClient.get<Any>(serverInfo,"/api/v1/user")
                checkLabel.text = "Connection successfull"
                checkLabel.foreground = Color.decode("#297642")
            } catch (exception: Exception) {
                checkLabel.text = "Connection failed: $exception"
                checkLabel.foreground = JBColor.RED
            }
            checkLabel.isVisible = true
        }

        val myWrapper = JPanel(BorderLayout())
        val centerPanel =
                FormBuilder.createFormBuilder()
                        .addLabeledComponent("Server url : ", url)
                        .addLabeledComponent("User: ", user)
                        .addLabeledComponent("Password: ", password)
                        .addLabeledComponent("Proxy url: ", proxyUrl)
                        .addLabeledComponent("Proxy user: ", proxyUser)
                        .addLabeledComponent("Proxy password: ", proxyPassword)
                        .addComponentToRightColumn(checkConnectionButton)
                        .addComponent(checkLabel)
                        .panel
        myWrapper.add(centerPanel, BorderLayout.NORTH)
        return myWrapper
    }

    companion object {
        fun getInstance(): ChutneySettingsConfigurable {
            return ApplicationManager.getApplication().getService(ChutneySettingsConfigurable::class.java)
        }
    }
}
