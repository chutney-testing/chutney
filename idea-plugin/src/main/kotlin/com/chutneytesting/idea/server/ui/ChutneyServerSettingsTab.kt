package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.server.ChutneyServerSettings
import com.chutneytesting.idea.server.ChutneyServerSettings.RunnerMode
import com.chutneytesting.idea.server.ChutneyServerSettingsManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Disposer
import com.intellij.ui.PortField
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.tabs.TabInfo
import com.intellij.util.ObjectUtils
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.text.ParseException
import javax.swing.*

class ChutneyServerSettingsTab(parentDisposable: Disposable) {
    private val myPortField: PortField
    private val myBrowserTimeoutSpinner: JSpinner
    private val myRunnerModeComboBox: ComboBox<*>
    val tabInfo: TabInfo
    private var myTrackChanges = true

    private fun listenForChanges() {
        myPortField.addPropertyChangeListener { update() }
        myBrowserTimeoutSpinner.addChangeListener { update() }
        myRunnerModeComboBox.addActionListener { update() }
    }

    private fun update() {
        if (myTrackChanges) {
            val settings = settings
            ChutneyServerSettingsManager.saveSettings(settings)
        }
    }

    private var settings: ChutneyServerSettings
        private get() {
            try {
                myPortField.commitEdit()
                myBrowserTimeoutSpinner.commitEdit()
            } catch (ignored: ParseException) {
            }
            val browserTimeout: Int = (myBrowserTimeoutSpinner.model as SpinnerNumberModel).number.toInt()
            var runnerMode = ObjectUtils.tryCast(myRunnerModeComboBox.selectedItem, RunnerMode::class.java)
            runnerMode = ObjectUtils.notNull(runnerMode, RunnerMode.QUIET)
            return ChutneyServerSettings.Builder()
                .setPort(myPortField.number)
                .setBrowserTimeoutMillis(browserTimeout)
                .setRunnerMode(runnerMode)
                .build()
        }
        private set(settings) {
            myTrackChanges = false
            try {
                myPortField.number = settings.port
                myBrowserTimeoutSpinner.value = settings.browserTimeoutMillis
                myRunnerModeComboBox.selectedItem = settings.runnerMode
            } finally {
                myTrackChanges = true
            }
        }

    fun saveSettings() {
        val settings = settings
        ChutneyServerSettingsManager.saveSettings(settings)
    }

    companion object {
        private const val GAP = 8
        private fun createResultPanel(form: JPanel): JPanel {
            val p = JPanel(BorderLayout(0, 0))
            p.add(form, BorderLayout.NORTH)
            p.add(JPanel(), BorderLayout.CENTER)
            p.add(createHyperlink(), BorderLayout.SOUTH)
            return p
        }

        private fun addMillisDescription(spinner: JSpinner): JComponent {
            val panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
            panel.add(spinner)
            panel.add(Box.createHorizontalStrut(10))
            panel.add(JLabel("ms"))
            return panel
        }

        private fun createBrowserTimeoutSpinner(): JSpinner {
            val spinner = JSpinner()
            spinner.model = SpinnerNumberModel(0, 0, null, 1)
            val numberEditor = JSpinner.NumberEditor(spinner, "#")
            spinner.editor = numberEditor
            numberEditor.textField.columns = 5
            return spinner
        }

        private fun createHyperlink(): JComponent {
            return SwingHelper.createWebHyperlink(
                "Chutney Server Help",
                "https://localhost:8443/#/home-page"
            )
        }
    }

    init {
        myPortField = PortField()
        myBrowserTimeoutSpinner = createBrowserTimeoutSpinner()
        myRunnerModeComboBox = ComboBox<Any?>(RunnerMode.values())
        val form = FormBuilder.createFormBuilder()
            .setAlignLabelOnRight(true)
            .addLabeledComponent(
                "&Port:",
                myPortField
            ) /*.addLabeledComponent("&Browser timeout:", addMillisDescription(myBrowserTimeoutSpinner))*/
            .addLabeledComponent("&Runner mode:", myRunnerModeComboBox)
            .panel
        val result = createResultPanel(form)
        result.border = BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP)
        tabInfo = TabInfo(JBScrollPane(result))
        tabInfo.text = "Settings"
        settings = ChutneyServerSettingsManager.loadSettings()
        ChutneyServerSettingsManager.addListener(object : ChutneyServerSettingsManager.Listener {
            override fun onChanged(settings: ChutneyServerSettings) {
                this@ChutneyServerSettingsTab.settings = settings
            }
        }, parentDisposable)
        Disposer.register(parentDisposable, Disposable { saveSettings() })
        listenForChanges()
    }
}
