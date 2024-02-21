package com.chutneytesting.idea.actions

import com.chutneytesting.idea.util.HJsonUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.ui.UIUtil
import javax.swing.JLabel


class PasteHJsonFromClipBoardAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(LangDataKeys.PSI_FILE) ?: return
        val hJsonFromClipboard = HJsonUtils.hjsonFromClipboard() ?: return
        val builder = DialogBuilder()
        builder.setCenterPanel(JLabel("Clipboard content copied from hjson file, Do you want to convert it to json?"))
        builder.setDimensionServiceKey("PasteHjsonFromClipboard.HasHjson")
        builder.setTitle("Convert code from Hjson")
        builder.addOkAction()
        builder.addCancelAction()
        val show = builder.show()
        var toPaste: String? = null
        if (show == DialogWrapper.OK_EXIT_CODE) {
            toPaste = hJsonFromClipboard
        } else {
            val fromClipboard = HJsonUtils.fromClipboard()
            if (fromClipboard != null) {
                toPaste = fromClipboard
            }
        }
        if (toPaste != null) {
            val ed = e.getData(PlatformDataKeys.EDITOR) ?: return
            val project = AnAction.getEventProject(e)
            val editor = e.getData(PlatformDataKeys.EDITOR)
            val file = e.getData(PlatformDataKeys.PSI_FILE)
            val caret = e.getData(PlatformDataKeys.CARET)
            if (project == null || editor == null || file == null || caret == null) {
                return
            }
            val document = editor.document
            val r = {
                EditorModificationUtil.insertStringAtCaret(ed, toPaste)
                PsiDocumentManager.getInstance(project).commitDocument(document)
                UIUtil.invokeAndWaitIfNeeded(Runnable {
                    //val psiFile = PsiManager.getInstance(project).findFile(currentFile!!)
                    CodeStyleManager.getInstance(project).reformat(file)
                })
            }
            WriteCommandAction.runWriteCommandAction(ed.project, r)
        }
    }
}
