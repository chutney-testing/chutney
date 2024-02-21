package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.actions.converter.ScenarioV1ToV2Converter
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.util.HJsonUtils
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.ui.UIUtil
import org.hjson.JsonValue
import org.hjson.Stringify

class UpdateScenarioToV2Action : AnAction() {

    private val LOG = Logger.getInstance(UpdateLocalScenarioFromRemoteServer::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val file = event.getData(DataKeys.VIRTUAL_FILE) ?: return
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return
        val document = editor.document
        if (!ChutneyUtil.isChutneyV1Json(psiFile)) return
        val processJsonReference = ChutneyUtil.processJsonReference(psiFile.virtualFile)
        //val hJsonString: String? = JsonValue.readHjson(processJsonReference).toString(Stringify.PLAIN)
        val jsonV2 =
            JsonValue.readHjson(ScenarioV1ToV2Converter().convert(processJsonReference)).toString(Stringify.PLAIN)

        try {
            val runnable = Runnable {
                document.setText(HJsonUtils.convertHjson(jsonV2).replace("\r\n", "\n"))
                FileDocumentManager.getInstance().saveDocument(document)
                UIUtil.invokeAndWaitIfNeeded(Runnable {
                    CodeStyleManager.getInstance(project).reformat(psiFile)
                })
            }
            WriteCommandAction.runWriteCommandAction(project, runnable)
            EventDataLogger.logInfo(
                "Scenario file updated to V2 with success.<br>",
                project
            )

        } catch (e: Exception) {
            LOG.debug(e.toString())
            EventDataLogger.logError(e.toString(), project)
        }

    }
}
