package com.chutneytesting.idea.runner.settings.ui

import com.chutneytesting.idea.runner.settings.ChutneySettingsUtil
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ListUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.Consumer
import com.intellij.util.PathUtil
import com.intellij.util.SmartList
import com.intellij.util.containers.ContainerUtil
import java.io.File
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class ChutneyFilesTextFieldWithBrowseButton(private val myProject: Project?) : TextFieldWithBrowseButton() {

    private val myListeners = ContainerUtil.createLockFreeCopyOnWriteList<ChangeListener>()
    private var myConfigurationFiles = ContainerUtil.newSmartList<String>()
    private val myAllowToChoseFolders = true

    init {
        isEditable = false
        this.button.addActionListener { event ->
            val dialog = MyChutneyFilesDialog(myProject, myConfigurationFiles, myAllowToChoseFolders)
            if (dialog.showAndGet()) {
                this@ChutneyFilesTextFieldWithBrowseButton.setConfigurationFiles(dialog.configurationFiles)
            }
        }
    }

    fun setConfigurationFiles(configurationFiles: List<String>) {
        this.myConfigurationFiles = configurationFiles
        this.text = this.getPresentation(configurationFiles)
        this.fireStateChanged()
    }

    private fun getPresentation(configurationFiles: List<String>): String {
        val files: String?
        if (this.myProject != null && this.myProject.basePath != null) {
            val baseDir = File(this.myProject.basePath!!)
            files = configurationFiles.map { File(it) }.map { nextFile -> this.getFilePresentation(baseDir, nextFile) }
                .joinToString(separator = ";")
            return files

        } else {
            files = StringUtil.join(configurationFiles, SEPARATOR)

            return files
        }
    }

    private fun getFilePresentation(baseDir: File, file: File): String {
        return if (FileUtil.isAncestor(
                baseDir.path,
                file.path,
                true
            )
        ) "." + File.separatorChar + FileUtil.getRelativePath(baseDir, file) else file.path
    }

    private fun fireStateChanged() {

        for (listener in this.myListeners) {
            listener.stateChanged(ChangeEvent(this))
        }

    }

    private inner class MyChutneyFilesDialog internal constructor(
        private val myProject: Project?,
        configurationFiles: List<String>,
        private val myAllowToChoseFolders: Boolean
    ) : DialogWrapper(myProject) {

        private val myMainPanel: JPanel
        private val myConfigurationFilesList: JBList<String>
        private val myConfigurationFilesModel: DefaultListModel<String>

        private val chooserTitle: String
            get() = "Choose Scenario Files"

        internal val configurationFiles: List<String>
            get() {
                val configurationFilePaths = SmartList<String>()
                for (i in 0 until this.myConfigurationFilesList.model.size) {
                    configurationFilePaths.add(this.myConfigurationFilesList.model.getElementAt(i))
                }
                return configurationFilePaths
            }

        init {
            this.title = "Chutney Scenario Files"
            this.myConfigurationFilesModel = DefaultListModel()
            this.myConfigurationFilesList = JBList(this.myConfigurationFilesModel)
            configurationFiles.forEach { file ->
                this.myConfigurationFilesModel.addElement(
                    FileUtil.toSystemDependentName(
                        file
                    )
                )
            }
            this.myMainPanel = ToolbarDecorator.createDecorator(this.myConfigurationFilesList)
                .setAddAction { button -> this.addConfigurationFileItem() }
                .setRemoveAction { button -> this.deleteConfigurationFileItems() }.createPanel()
            this.init()
        }

        private fun addConfigurationFileItem() {
            val fileChooserDescriptor =
                FileChooserDescriptor(true, this.myAllowToChoseFolders, false, true, false, true)
            fileChooserDescriptor.withTitle(this.chooserTitle)
            val toSelect = if (this.myProject == null) null else this.myProject.baseDir
            FileChooser.chooseFiles(
                fileChooserDescriptor,
                this.myProject,
                this.myConfigurationFilesList,
                toSelect,
                Consumer<List<VirtualFile>> { this.onConfigurationFileChosen(it) })
        }

        private fun deleteConfigurationFileItems() {
            ListUtil.removeSelectedItems(this.myConfigurationFilesList)
        }

        private fun onConfigurationFileChosen(chosenFiles: List<VirtualFile>) {
            val systemDependentPaths = LinkedHashSet<String>()

            for (nextFile in chosenFiles) {
                if (nextFile.isDirectory) {
                    ChutneyFilesTextFieldWithBrowseButton.getChutneyScenarioFilesFromDir(myProject!!, nextFile)
                        .map { PathUtil.toSystemDependentName(it.path) }.forEach { systemDependentPaths.add(it) }
                } else {
                    systemDependentPaths.add(FileUtil.toSystemDependentName(nextFile.path))
                }
            }

            systemDependentPaths.removeAll(Collections.list(this.myConfigurationFilesModel.elements()))
            systemDependentPaths.forEach { this.myConfigurationFilesModel.addElement(it) }
        }

        override fun createCenterPanel(): JComponent? {
            return this.myMainPanel
        }
    }

    companion object {
        private val SEPARATOR = ";"

        private fun getChutneyScenarioFilesFromDir(project: Project, directory: VirtualFile): List<VirtualFile> {
            return ChutneySettingsUtil.collectChutneyScenarioFilesInDirectory(project, directory)
        }
    }
}
