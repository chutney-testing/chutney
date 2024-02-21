package com.chutneytesting.idea.runner.settings.ui

import com.chutneytesting.idea.runner.settings.ui.ChutneyVariablesData.Companion.create
import com.intellij.execution.util.EnvVariablesTable
import com.intellij.execution.util.EnvironmentVariable
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.UserActivityProviderComponent
import com.intellij.util.EnvironmentUtil
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.io.IdeUtilIoBundle
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class ChutneyVariablesTextFieldWithBrowseButton : TextFieldWithBrowseButton(), UserActivityProviderComponent {
    private var myData = ChutneyVariablesData.DEFAULT
    private val myListeners = ContainerUtil.createLockFreeCopyOnWriteList<ChangeListener>()
    /**
     * @return unmodifiable Map instance
     */
    /**
     * @param envs Map instance containing user-defined environment variables
     * (iteration order should be reliable user-specified, like [LinkedHashMap] or [ImmutableMap])
     */
    var envs: Map<String, String>
        get() = myData.envs
        set(envs) {
            data = create(envs)
        }

    var data: ChutneyVariablesData
        get() = myData
        set(data) {
            val oldData = myData
            myData = data
            text = stringifyEnvs(data.envs)
            if (!oldData.equals(data)) {
                fireStateChanged()
            }
        }

    override fun addChangeListener(changeListener: ChangeListener) {
        myListeners.add(changeListener)
    }

    override fun removeChangeListener(changeListener: ChangeListener) {
        myListeners.remove(changeListener)
    }

    private fun fireStateChanged() {
        for (listener in myListeners) {
            listener.stateChanged(ChangeEvent(this))
        }
    }

    private inner class MyChutneyVariablesDialog : DialogWrapper(this@ChutneyVariablesTextFieldWithBrowseButton, true) {
        private val myEnvVariablesTable: EnvVariablesTable = EnvVariablesTable()
      private val myWholePanel = JPanel(BorderLayout())
        override fun createCenterPanel(): JComponent? {
            return myWholePanel
        }

        override fun doValidate(): ValidationInfo? {
            for (variable in myEnvVariablesTable.environmentVariables) {
                val name = variable.name
                val value = variable.value
                if (!EnvironmentUtil.isValidName(name)) return ValidationInfo(
                    IdeUtilIoBundle.message(
                        "run.configuration.invalid.env.name",
                        name
                    )
                )
                if (!EnvironmentUtil.isValidValue(value)) return ValidationInfo(
                    IdeUtilIoBundle.message(
                        "run.configuration.invalid.env.value",
                        name,
                        value
                    )
                )
            }
            return super.doValidate()
        }

        override fun doOKAction() {
            myEnvVariablesTable.stopEditing()
            val envs: MutableMap<String, String> = LinkedHashMap()
            for (variable in myEnvVariablesTable.environmentVariables) {
                envs[variable.name] = variable.value
            }
            this@ChutneyVariablesTextFieldWithBrowseButton.envs = envs
            super.doOKAction()
        }

        init {
          myEnvVariablesTable.setValues(convertToVariables(myData.envs))
            myWholePanel.add(myEnvVariablesTable.component, BorderLayout.CENTER)
            title = "Chutney Scenario Variables"
            init()
        }
    }

    companion object {
        private fun stringifyEnvs(envs: Map<String, String>): String {
            if (envs.isEmpty()) {
                return ""
            }
            val buf = StringBuilder()
            for ((key, value) in envs) {
                if (buf.isNotEmpty()) {
                    buf.append(";")
                }
                buf.append(key).append("=").append(value)
            }
            return buf.toString()
        }

        private fun convertToVariables(map: Map<String, String>): List<EnvironmentVariable> {
            return map.entries.map { EnvironmentVariable(it.key, it.value, false) }.toList()
        }
    }

    init {
        isEditable = false
        addActionListener { e: ActionEvent? -> MyChutneyVariablesDialog().show() }
    }
}
