package com.chutneytesting.idea.actions

import com.chutneytesting.idea.actions.ui.ValueLabelComboBox
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.idea.util.HJsonUtils
import com.chutneytesting.idea.util.sanitizeFilename
import com.chutneytesting.kotlin.util.HttpClient
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

data class Campaign(
    var id: Int?,
    val title: String,
    val description: String,
    val scenarioIds: List<String> = emptyList(),
    val computedParameters: Map<String, String>?,
    val campaignExecutionReports: List<Any>?,
    val environment: String?,
    val parallelRun: Boolean,
    val retryAuto: Boolean,
    val datasetId: String?,
    val tags: List<String>?
)

class GetCampaignFromRemoteServer : AnAction() {

    private val LOG = Logger.getInstance(GetCampaignFromRemoteServer::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val nav = event.getData(CommonDataKeys.NAVIGATABLE) ?: return
        val directory: PsiDirectory? = if (nav is PsiDirectory) nav else (nav as PsiFile).parent
        val dialogBuilder = DialogBuilder().title("Select Campaign")
        val centerPanel = JPanel(BorderLayout())

        val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
        val campaigns = HttpClient.get<List<Campaign>>(serverInfo, "/api/ui/campaign/v1")
        val items = campaigns.map {
            ValueLabel(it.id.toString(), it.title)
        }
        val myComboBox = ValueLabelComboBox(CollectionComboBoxModel(items))

        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = "Select Directory"
        val textFieldWithBrowseButton = TextFieldWithBrowseButton()
        textFieldWithBrowseButton.text = directory?.virtualFile?.canonicalPath ?: ""
        textFieldWithBrowseButton.addBrowseFolderListener("Select directory", "Select directory", project, descriptor)

        val box = JBCheckBox("Replace variables in scenarios", true)
        val panel = FormBuilder.createFormBuilder().addComponent(myComboBox)
            .addComponent(textFieldWithBrowseButton)
            .addComponent(box)
            .panel

        centerPanel.add(panel)
        dialogBuilder.centerPanel(centerPanel).resizable(false)

        HttpClient.get<Any>(serverInfo, "/api/ui/campaign/v1")

        dialogBuilder.setOkOperation {
            dialogBuilder.dialogWrapper.close(DialogWrapper.OK_EXIT_CODE)
            val selectedItem = myComboBox.selectedItem as ValueLabel
            val id = selectedItem.value
            try {
                val psiFileFactory = PsiFileFactory.getInstance(project)
                campaigns.find { it.id.toString() == id }?.scenarioIds?.forEach { scenarioId ->
                    val scenario = HttpClient.get<Map<String, Any>>(
                        serverInfo,
                        "/api/scenario/v2/raw/$scenarioId"
                    )
                    val convertedScenario: String = scenario["content"].toString()
                    val convertHJson = HJsonUtils.convertHjson(convertedScenario)
                    val psiFileCreated = psiFileFactory.createFileFromText(
                        sanitizeFilename(scenario["id"].toString()) + "-" + scenario["title"].toString()
                            .lowercase()
                            .trim() + ".chutney." + JsonFileType.INSTANCE.defaultExtension,
                        JsonFileType.INSTANCE,
                        convertHJson
                    )
                    val scenarioFile = CodeStyleManager.getInstance(project).reformat(psiFileCreated)
                    val targetDirectory = VfsUtil.findFile(Paths.get(textFieldWithBrowseButton.text), true)
                        ?.let { PsiManager.getInstance(project).findDirectory(it) }
                    ApplicationManager.getApplication().runWriteAction {
                        targetDirectory?.add(scenarioFile)
                    }
                }
            } catch (e: Exception) {
                EventDataLogger.logError(e.toString(), project)
            }
        }
        dialogBuilder.show()
    }
}
