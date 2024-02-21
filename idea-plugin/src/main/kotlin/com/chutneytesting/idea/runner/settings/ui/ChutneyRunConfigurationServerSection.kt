package com.chutneytesting.idea.runner.settings.ui

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.chutneytesting.idea.runner.settings.ServerType
import com.chutneytesting.idea.server.ChutneyServerUtils
import com.chutneytesting.idea.util.SwingUtils
import com.chutneytesting.idea.util.TextChangeListener
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.JBColor
import com.intellij.util.Consumer
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import com.intellij.util.ui.UIUtil
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class ChutneyRunConfigurationServerSection : AbstractRunSettingsSection() {
    private val myInternalServerRadioButton: JRadioButton
    private val myExternalServerRadioButton: JRadioButton
    private val myExternalServerUrl: JTextField
    private val myTestConnectionButton: JButton
    private val myTestConnectionResult: JLabel
    private val myExternalServerPanel: JPanel
    private val myRoot: JPanel
    private fun createSwitchServerTypeAction(serverType: ServerType): ActionListener {
        return ActionListener {
            myTestConnectionResult.text = ""
            selectServerType(serverType)
        }
    }

    private fun selectServerType(serverType: ServerType) {
        val external = serverType === ServerType.EXTERNAL
        myExternalServerRadioButton.isSelected = external
        myInternalServerRadioButton.isSelected = !external
        UIUtil.setEnabled(myExternalServerPanel, external, true)
    }

    private fun testConnectionToExternalServer() {
        if (!myExternalServerRadioButton.isSelected) {
            return
        }
        val serverUrl = myExternalServerUrl.text
        myTestConnectionButton.isEnabled = false
        myTestConnectionResult.foreground = UIUtil.getLabelForeground()
        myTestConnectionResult.text = "Connecting to $serverUrl ..."
        ChutneyServerUtils.asyncFetchServerInfo(serverUrl, Consumer { p0 ->
            UIUtil.invokeLaterIfNeeded {
                if (p0!!.isError) {
                    myTestConnectionResult.foreground = JBColor.RED
                    myTestConnectionResult.text = p0.errorMessage
                } else {
                    val serverInfo = p0.serverInfo
                    val capturedBrowsers = serverInfo?.capturedBrowsers?.size
                    val browserMessage: String
                    browserMessage = if (capturedBrowsers == 0) {
                        "no captured browsers found"
                    } else if (capturedBrowsers == 1) {
                        "1 captured browser found"
                    } else {
                        "$capturedBrowsers captured browsers found"
                    }
                    myTestConnectionResult.foreground = UIUtil.getLabelForeground()
                    //TODO cleanup
//myTestConnectionResult.setText("Connected successfully, " + browserMessage);
                    myTestConnectionResult.text = "Connected successfully."
                }
                myTestConnectionButton.isEnabled = true
            }
        })

    }

    override fun resetFrom(runSettings: ChutneyRunSettings) {
        selectServerType(runSettings.serverType)
        if (runSettings.isExternalServerType()) {
            myExternalServerUrl.text = runSettings.serverAddress
        }
    }

    override fun applyTo(runSettingsBuilder: ChutneyRunSettings) {
        if (myExternalServerRadioButton.isSelected) {
            runSettingsBuilder.serverType = ServerType.EXTERNAL
            runSettingsBuilder.serverAddress = StringUtil.notNullize(myExternalServerUrl.text)
        } else {
            runSettingsBuilder.serverType = ServerType.INTERNAL
        }
    }

    override fun createComponent(creationContext: CreationContext): JComponent {
        return myRoot
    }

    companion object {
        private fun createExternalServerPanel(
            externalServerUrl: JTextField,
            testConnectionButton: JButton,
            testConnectionResult: JLabel
        ): JPanel {
            val up = FormBuilder().addLabeledComponent("S&erver URL:", externalServerUrl).panel
            val down = FormBuilder().addLabeledComponent(testConnectionButton, testConnectionResult).panel
            val panel = SwingHelper.newLeftAlignedVerticalPanel(up, Box.createVerticalStrut(7), down)
            panel.border = BorderFactory.createEmptyBorder(0, 30, 0, 0)
            return panel
        }
    }

    init {
        myInternalServerRadioButton = JRadioButton("\u001BRunning in IDE")
        myInternalServerRadioButton.addActionListener(createSwitchServerTypeAction(ServerType.INTERNAL))
        myExternalServerRadioButton = JRadioButton("\u001BAt address:")
        myExternalServerRadioButton.addActionListener(createSwitchServerTypeAction(ServerType.EXTERNAL))
        val group = ButtonGroup()
        group.add(myExternalServerRadioButton)
        group.add(myInternalServerRadioButton)
        myExternalServerUrl = JTextField()
        myTestConnectionButton = JButton("\u001BTest Connection")
        myTestConnectionButton.addActionListener { e: ActionEvent? -> testConnectionToExternalServer() }
        myTestConnectionResult = JLabel()
        myExternalServerPanel =
            createExternalServerPanel(myExternalServerUrl, myTestConnectionButton, myTestConnectionResult)
        val panel = SwingHelper.newVerticalPanel(
            Component.LEFT_ALIGNMENT,
            myInternalServerRadioButton,
            myExternalServerRadioButton,
            Box.createVerticalStrut(3),
            myExternalServerPanel
        )
        myRoot = SwingHelper.wrapWithHorizontalStretch(panel)
        SwingUtils.addTextChangeListener(myExternalServerUrl, object : TextChangeListener {
            override fun textChanged(oldText: String?, newText: String) {
                myTestConnectionResult.text = ""
            }
        })
    }
}
