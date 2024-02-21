package com.chutneytesting.idea.insert

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.actions.PasteHJsonFromClipBoardAction
import com.chutneytesting.idea.util.HJsonUtils
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actionSystem.EditorTextInsertHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.psi.PsiManager
import com.intellij.util.Producer
import java.awt.datatransfer.Transferable


class PasteHJsonHandler(originalAction: EditorActionHandler) : EditorActionHandler(), EditorTextInsertHandler {

    private val myOriginalHandler: EditorActionHandler? = originalAction

    private fun createAnEvent(action: AnAction, context: DataContext): AnActionEvent {
        val presentation = action.templatePresentation.clone()
        return AnActionEvent(null, context, ActionPlaces.UNKNOWN, presentation, ActionManager.getInstance(), 0)
    }

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        if (editor is EditorEx) {
            val virtualFile = editor.virtualFile
            if (virtualFile != null) {
                val psiFile = PsiManager.getInstance(editor.project!!).findFile(virtualFile)
                if (psiFile != null && ChutneyUtil.isChutneyJson(psiFile)) {
                    val hJsonFromClipboard = HJsonUtils.hjsonFromClipboard()
                    if (hJsonFromClipboard != null) {
                        assert(caret == null) { "Invocation of 'paste' operation for specific caret is not supported" }
                        val action = PasteHJsonFromClipBoardAction()
                        val event = createAnEvent(action, dataContext)
                        action.actionPerformed(event)
                        return
                    }

                }
            }
        }
        myOriginalHandler?.execute(editor, caret, dataContext)

    }

    override fun execute(editor: Editor?, dataContext: DataContext?, p2: Producer<out Transferable>?) {
        val caret = editor?.caretModel?.primaryCaret
        doExecute(editor!!, caret, dataContext!!)

    }
}
