package com.chutneytesting.idea.runner.settings.ui


import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.chutneytesting.idea.runner.settings.ChutneySettingsUtil
import com.chutneytesting.idea.util.ProjectRootUtils
import com.chutneytesting.idea.util.SwingUtils
import com.chutneytesting.idea.util.TextChangeListener
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.util.ArrayUtil
import com.intellij.util.ObjectUtils
import com.intellij.util.ui.UIUtil
import java.awt.*
import java.io.File
import javax.swing.*

class AllInDirectoryRunSettingsSection internal constructor() : AbstractRunSettingsSection() {

    private val myDirectoryTextFieldWithBrowseButton: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val myLabel: JBLabel = JBLabel("Directory:")

    init {
        anchor = myLabel
    }

    override fun resetFrom(runSettings: ChutneyRunSettings) {
        myDirectoryTextFieldWithBrowseButton.text = runSettings.directory
    }

    override fun applyTo(runSettings: ChutneyRunSettings) {
        runSettings.directory = ObjectUtils.notNull(myDirectoryTextFieldWithBrowseButton.text, "")
    }

    public override fun createComponent(creationContext: CreationContext): JComponent {
        val panel = JPanel(GridBagLayout())

        myLabel.setDisplayedMnemonic('D')
        myLabel.labelFor = myDirectoryTextFieldWithBrowseButton.textField
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

        myDirectoryTextFieldWithBrowseButton.addBrowseFolderListener(
            "Select directory",
            "All Chutney scenario files in this folder will be executed",
            creationContext.project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )
        panel.add(
            myDirectoryTextFieldWithBrowseButton, GridBagConstraints(
                1, 0,
                1, 1,
                1.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                Insets(UIUtil.DEFAULT_VGAP, 0, 0, 0),
                0, 0
            )
        )

        val infoComponent = createInfoComponent(creationContext.project, myDirectoryTextFieldWithBrowseButton.textField)
        panel.add(
            infoComponent, GridBagConstraints(
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
                val configs = getScenariosInDir(project, newText)
                fileList.setListData(configs.toTypedArray())
            }
        })
        panel.add(fileList, BorderLayout.CENTER)
        return panel
    }

    private fun getScenariosInDir(project: Project, dirPath: String): List<String> {
        var result = emptyList<String>()
        val dir = File(dirPath)
        if (!StringUtil.isEmpty(dirPath) && dir.isDirectory && dir.isAbsolute) {
            val directoryVFile = LocalFileSystem.getInstance().findFileByIoFile(dir)
            if (directoryVFile != null) {
                val scenarios = ChutneySettingsUtil.collectChutneyScenarioFilesInDirectory(project, directoryVFile)
                result = scenarios.mapNotNull { ProjectRootUtils.getRootRelativePath(project, it.path) }.toList()
            }
        }
        return result
    }

    override fun setAnchor(anchor: JComponent?) {
        super.setAnchor(anchor)
        myLabel.anchor = anchor
    }
}
