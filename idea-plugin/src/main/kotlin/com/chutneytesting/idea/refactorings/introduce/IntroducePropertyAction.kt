package com.chutneytesting.idea.refactorings.introduce

import com.chutneytesting.idea.ChutneyUtil
import com.intellij.json.JsonLanguage
import com.intellij.json.psi.*
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.actions.BaseRefactoringAction
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextField

class IntroducePropertyAction : BaseRefactoringAction() {

    override fun isEnabledOnElements(elements: Array<out PsiElement>): Boolean {
        return false
    }

    override fun isAvailableForFile(file: PsiFile?): Boolean {
        return ChutneyUtil.isChutneyJson(file!!)
    }

    override fun getHandler(context: DataContext): RefactoringActionHandler? {
        return MyRefactoringActionHandler()
    }

    override fun isAvailableInEditorOnly(): Boolean = true

    override fun isAvailableForLanguage(language: Language): Boolean {
        return language == JsonLanguage.INSTANCE
    }

}


class MyRefactoringActionHandler : RefactoringActionHandler {
    override fun invoke(p0: Project, p1: Array<out PsiElement>, p2: DataContext?) {

    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile, context: DataContext) {
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val elementAndRange = getSelectedElementAndTextRange(editor, file)
        if (elementAndRange != null) {
            val selectedElement = elementAndRange.first as JsonLiteral
            val range = elementAndRange.second as TextRange
            val stringValue = selectedElement.text
            if (stringValue != null) {
                val selectedString = editor.document.getText(range)
                val ranges = getPropertiesTextRanges(stringValue)
                val offsetInElement = range.startOffset - selectedElement.textOffset
                if (!StringUtil.isEmptyOrSpaces(selectedString) && !isIntersectWithRanges(
                        ranges,
                        offsetInElement,
                        offsetInElement + selectedString.length
                    )
                ) {
                    editor.selectionModel.setSelection(range.startOffset, range.endOffset)
                    val findFirstPropertyParent =
                        PsiTreeUtil.findFirstParent(selectedElement) { e -> e is JsonProperty } as JsonProperty
                    val propertyName = findFirstPropertyParent.name
                    val replaceWith = "\"\${#$propertyName}\""
                    val dialogBuilder = DialogBuilder().title("IntroduceProperty")
                    val centerPanel = JPanel(BorderLayout())
                    val propertyNameField = JTextField(propertyName)
                    val panel = FormBuilder.createFormBuilder().addLabeledComponent("Name", propertyNameField)
                        .panel
                    centerPanel.add(panel)
                    dialogBuilder.centerPanel(centerPanel)
                    dialogBuilder.resizable(false)
                    dialogBuilder.setOkOperation {
                        dialogBuilder.dialogWrapper.close(DialogWrapper.OK_EXIT_CODE)
                        WriteCommandAction.runWriteCommandAction(project) {
                            editor.document.replaceString(range.startOffset, range.endOffset, replaceWith)
                            PsiDocumentManager.getInstance(project).commitAllDocuments()
                            createChutneyProperty(project, selectedElement, propertyNameField.text, selectedString)
                            PsiDocumentManager.getInstance(project).commitAllDocuments()
                        }
                    }
                    dialogBuilder.show()
                }
            }
        }
    }

    private fun createChutneyProperty(
        project: Project,
        selectedElement: PsiElement,
        propertyName: String,
        selectedString: String
    ) {
        val property = PsiTreeUtil.findChildrenOfType(selectedElement.containingFile, JsonProperty::class.java)
            .firstOrNull { it.name == "entries" }
        if (property != null) {
            val jsonObject = property.value as JsonObject
            val jsonProperty = JsonElementGenerator(project).createProperty(propertyName, selectedString)
            JsonPsiUtil.addProperty(jsonObject, jsonProperty, false)
        }

    }


    private fun getPropertiesTextRanges(s: String): List<TextRange> {
        val ranges = mutableListOf<TextRange>()
        var startOffset = s.indexOf("\${")

        while (startOffset >= 0) {
            val endOffset = s.indexOf("}", startOffset)
            if (endOffset <= startOffset) {
                break
            }

            if (s.substring(startOffset + "\${".length, endOffset).contains("\${")) {
                startOffset = s.indexOf("\${", startOffset + 1)
            } else {
                ranges.add(TextRange(startOffset, endOffset))
                startOffset = s.indexOf("\${", endOffset)
            }
        }

        return ranges
    }


    private fun isIntersectWithRanges(ranges: Collection<TextRange>, start: Int, end: Int): Boolean {

        val var3 = ranges.iterator()
        var range: TextRange
        do {
            do {
                if (!var3.hasNext()) {
                    return false
                }

                range = var3.next()
            } while (start <= range.startOffset && end >= range.endOffset)
        } while (end <= range.startOffset || start >= range.endOffset)

        return true
    }

    private fun getSelectedElementAndTextRange(editor: Editor, file: PsiFile): Pair<JsonLiteral, TextRange>? {
        val startOffset = editor.selectionModel.selectionStart
        val endOffset = editor.selectionModel.selectionEnd
        val elementAtStart = file.findElementAt(startOffset)
        if (elementAtStart == null) {
            return null
        } else {
            val elementAtEnd = file.findElementAt(if (endOffset == startOffset) endOffset else endOffset - 1)
            if (elementAtEnd == null) {
                return null
            } else {
                var elementAt = PsiTreeUtil.findCommonParent(elementAtStart, elementAtEnd)
                if (elementAt is LeafPsiElement) {
                    elementAt = elementAt.parent
                }

                if (elementAt !is JsonStringLiteral && elementAt !is JsonNumberLiteral) {
                    return null
                } else {
                    val range = if (editor.selectionModel.hasSelection()) TextRange(
                        startOffset,
                        endOffset
                    ) else elementAt.textRange
                    return Pair.create<JsonLiteral, TextRange>(elementAt as JsonLiteral?, range)
                }
            }
        }
    }


}
