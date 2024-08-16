/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.actions

import com.chutneytesting.idea.actions.ui.ValueLabelComboBox
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.idea.util.sanitizeFilename
import com.chutneytesting.kotlin.util.HttpClient
import com.google.gson.Gson
import com.intellij.json.JsonFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.nio.file.Paths
import javax.swing.JPanel

data class ValueLabel(val value: String, val label: String)

class GetScenarioFromRemoteServer : AnAction() {

    private val LOG = Logger.getInstance(GetScenarioFromRemoteServer::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val nav = event.getData(CommonDataKeys.NAVIGATABLE) ?: return
        val directory: PsiDirectory? = if (nav is PsiDirectory) nav else (nav as PsiFile).parent

        val dialogBuilder = DialogBuilder().title("Select Scenario")
        val centerPanel = JPanel(BorderLayout())

        val scenarios = HttpClient.get<List<Map<String, Map<String, Any>>>>(ChutneySettings.getInstance().state.serverInfo()!!, "/api/scenario/v2")
        val items : List<ValueLabel> = scenarios.map {
            val metadata : Map<String, Any> = it["metadata"]!!
             ValueLabel(metadata["id"].toString(), metadata["title"].toString()) }
        val myComboBox = ValueLabelComboBox(CollectionComboBoxModel(items))

        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = "Select Directory"
        val textFieldWithBrowseButton = TextFieldWithBrowseButton()
        textFieldWithBrowseButton.text = directory?.virtualFile?.canonicalPath ?: ""
        textFieldWithBrowseButton.addBrowseFolderListener(descriptor.title, "Select directory", project, descriptor)

        val box = JBCheckBox("Replace variables in scenario", true)
        val panel = FormBuilder.createFormBuilder().addComponent(myComboBox)
            .addComponent(textFieldWithBrowseButton)
            .addComponent(box)
            .panel

        centerPanel.add(panel)
        dialogBuilder.centerPanel(centerPanel).resizable(false)


        dialogBuilder.setOkOperation {
            dialogBuilder.dialogWrapper.close(DialogWrapper.OK_EXIT_CODE)
            val selectedItem = myComboBox.selectedItem as ValueLabel
            val idScenario = selectedItem.value

            try {
                val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
                val result: Map<String, Any> = HttpClient.get(
                    serverInfo, "/api/scenario/v2/raw/$idScenario"
                )
                val rawScenario = result["content"].toString()
                val psiFileFactory = PsiFileFactory.getInstance(project)

                var convertHJson = HJsonUtils.convertHjson(rawScenario)
                if (box.isSelected) {
                    try {
                        val variables: Map<String, String> =
                            Gson().fromJson(result["dataset"].toString(), mutableMapOf<String, String>().javaClass)
                        variables.keys.forEach { key ->
                            convertHJson = convertHJson.replace("**$key**", variables.getValue(key), false)
                        }
                    } catch (e: Exception) {
                        // ignore exception
                    }
                }
                val psiFileCreated = psiFileFactory.createFileFromText(
                    sanitizeFilename(result["id"].toString()) + "-" + result["title"].toString().trim().lowercase() + ".chutney." + JsonFileType.INSTANCE.defaultExtension,
                    JsonFileType.INSTANCE,
                    convertHJson
                )
                val scenarioFile = CodeStyleManager.getInstance(project).reformat(psiFileCreated)
                val targetDirectory = VfsUtil.findFile(Paths.get(textFieldWithBrowseButton.text), true)
                    ?.let { PsiManager.getInstance(project).findDirectory(it) }

                ApplicationManager.getApplication().runWriteAction {
                    targetDirectory?.add(scenarioFile)
                }


            } catch (e: Exception) {
                EventDataLogger.logError(e.toString(), project)
            }
        }

        dialogBuilder.show()
    }

}
