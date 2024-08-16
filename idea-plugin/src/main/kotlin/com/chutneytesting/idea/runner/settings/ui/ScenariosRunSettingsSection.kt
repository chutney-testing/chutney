/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.runner.settings.ui


import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.chutneytesting.idea.util.SwingUtils
import com.chutneytesting.idea.util.TextChangeListener
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.util.ArrayUtil
import com.intellij.util.ObjectUtils
import com.intellij.util.ui.UIUtil
import java.awt.*
import javax.swing.*

class ScenariosRunSettingsSection(val project: Project) : AbstractRunSettingsSection() {

    private val myChutneyVariablesTextFieldWithBrowseButton: ChutneyFilesTextFieldWithBrowseButton =
        ChutneyFilesTextFieldWithBrowseButton(project)
    private val myLabel: JBLabel = JBLabel("Scenarios:")

    init {
        anchor = myLabel
    }

    override fun resetFrom(runSettings: ChutneyRunSettings) {
        myChutneyVariablesTextFieldWithBrowseButton.text = runSettings.scenariosFilesPath
        myChutneyVariablesTextFieldWithBrowseButton.setConfigurationFiles(runSettings.scenariosFilesPath.split(";"))
    }

    override fun applyTo(runSettings: ChutneyRunSettings) {
        runSettings.scenariosFilesPath = ObjectUtils.notNull(myChutneyVariablesTextFieldWithBrowseButton.text)
    }

    public override fun createComponent(creationContext: CreationContext): JComponent {
        val panel = JPanel(GridBagLayout())
        myLabel.setDisplayedMnemonic('S')
        myLabel.labelFor = myChutneyVariablesTextFieldWithBrowseButton.textField
        myLabel.horizontalAlignment = SwingConstants.RIGHT
        panel.add(
            myLabel, GridBagConstraints(
                0, 0,
                1, 1,
                0.0, 0.0,
                GridBagConstraints.EAST,
                GridBagConstraints.NONE,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, UIUtil.DEFAULT_HGAP),
                0, 0
            )
        )

        panel.add(
            myChutneyVariablesTextFieldWithBrowseButton, GridBagConstraints(
                1, 0,
                1, 1,
                1.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, 0),
                0, 0
            )
        )
        /*val infoComponent = createInfoComponent(creationContext.project, myChutneyVariablesTextFieldWithBrowseButton.textField) */
        // hack for positioning
        panel.add(
            JLabel(""), GridBagConstraints(
                0, 1,
                2, 1,
                1.0, 1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.BOTH,
                Insets(UIUtil.DEFAULT_VGAP + 5, 0, 0, 0),
                0, 0
            )
        )

        return panel
    }

    private fun createInfoComponent(
        project: Project,
        directoryTextField: JTextField
    ): JComponent {
        val panel = JPanel(BorderLayout())
        panel.add(JLabel("Matched scenarios files (*.json):"), BorderLayout.NORTH)

        val fileList = JBList(*ArrayUtil.EMPTY_STRING_ARRAY)
        fileList.border = BorderFactory.createLineBorder(JBColor.GRAY)
        fileList.cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
                if (value is String) {
                    label.text = value
                }
                return label
            }
        }
        SwingUtils.addTextChangeListener(directoryTextField, object : TextChangeListener {
            override fun textChanged(oldText: String?, newText: String) {
                val configs = newText.split(";")
                fileList.setListData(configs.toTypedArray())
            }
        })

        panel.add(fileList, BorderLayout.CENTER)
        return panel
    }

    override fun setAnchor(anchor: JComponent?) {
        super.setAnchor(anchor)
        myLabel.anchor = anchor
    }
}
