package com.chutneytesting.idea.insert

import com.chutneytesting.idea.completion.field.model.StringField
import com.chutneytesting.idea.completion.value.StepValueData
import com.chutneytesting.idea.completion.value.model.StepValue
import com.chutneytesting.idea.completion.value.model.Value
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.util.text.CharArrayUtil
import org.apache.commons.lang.StringUtils
import java.util.stream.Collectors

class JsonStepInsertValueHandler(private val value: Value) : InsertHandler<LookupElement> {
    override fun handleInsert(insertionContext: InsertionContext, lookupElement: LookupElement) {
        if (value.isQuotable) {
            handleStartingQuote(insertionContext, lookupElement)
            handleEndingQuote(insertionContext)
        }
        if (value is StepValue) {
            val data = value.data
            val test = ""
            /*if (data.getTask() != null) {
                test = data.getTask();
                final Editor editor = insertionContext.getEditor();
                final int offset = editor.getCaretModel().getOffset();
                final PsiElement elementAt = insertionContext.getFile().findElementAt(offset);
                final PsiElement parent = PsiTreeUtil.findFirstParent(elementAt, psiElement -> psiElement instanceof JsonObject);
                if (parent != null) {
                    final int start = parent.getTextOffset();
                    insertionContext.getDocument().replaceString(start, start + parent.getTextLength(), test);
                    CodeStyleManager.getInstance(editor.getProject()).reformat(parent);
                }
            } else if (data.getSteps() != null) {
                if (!StringUtils.nextCharAfterSpacesAndQuotesIsColon(getStringAfterAutoCompletedValue(insertionContext))) {
                    final String suffixWithCaret = getJsonPlaceholderSuffix(data.getSteps(), getIndentation(insertionContext, lookupElement));
                    final Editor editor = insertionContext.getEditor();
                    final int offset = editor.getCaretModel().getOffset();
                    final PsiElement elementAt = insertionContext.getFile().findElementAt(offset);
                    final PsiElement parent = PsiTreeUtil.findFirstParent(elementAt, psiElement -> psiElement instanceof JsonObject);
                    final String suffixWithoutCaret = suffixWithCaret.replace(CARET, "");
                    EditorModificationUtil.insertStringAtCaret(
                            insertionContext.getEditor(),
                            withOptionalComma(suffixWithoutCaret, insertionContext), false, true,
                            getCaretIndex(suffixWithCaret));
                    CodeStyleManager.getInstance(editor.getProject()).reformat(parent);

                }
            } */
        }
    }

    private fun getJsonPlaceholderSuffix(test: List<StepValueData>, indentation: Int): String {
        val sb = StringBuilder()
        val indentationPadding = StringUtils.repeat(" ", indentation)
        sb.append(", \n\"steps\":")
        sb.append("[\n")
        sb.append(
            test.stream()
                .map<String>(StepValueData::task)
                .collect(Collectors.joining(", "))
        )
        sb.append("]\n")
        return sb.toString()
    }

    private fun handleStartingQuote(insertionContext: InsertionContext, lookupElement: LookupElement) {
        val caretOffset = insertionContext.editor.caretModel.offset
        val startOfLookupStringOffset = caretOffset - lookupElement.lookupString.length
        val hasStartingQuote = insertionContext.document.text[startOfLookupStringOffset - 1] == '\"'
        if (!hasStartingQuote) {
            insertionContext.document.insertString(startOfLookupStringOffset, "\"")
        }
    }

    private fun handleEndingQuote(insertionContext: InsertionContext) {
        val caretOffset = insertionContext.editor.caretModel.offset
        val chars = insertionContext.document.charsSequence
        val hasEndingQuote = CharArrayUtil.regionMatches(chars, caretOffset, "\"")
        if (!hasEndingQuote) {
            insertionContext.document.insertString(caretOffset, "\"")
        }
        EditorModificationUtil.moveCaretRelatively(insertionContext.editor, 1)
    }

    private fun withOptionalComma(suffix: String, context: InsertionContext): String {
        return if (shouldAddComma(context)) "$suffix," else suffix
    }

    private fun getCaretIndex(suffix: String): Int {
        return suffix.indexOf(StringField.CARET)
    }

    private fun getIndentation(context: InsertionContext, item: LookupElement): Int {
        val stringBeforeAutoCompletedValue = getStringBeforeAutoCompletedValue(context, item)
        return com.chutneytesting.idea.util.StringUtils.getNumberOfSpacesInRowStartingFromEnd(
            stringBeforeAutoCompletedValue
        )
    }

    private fun getStringAfterAutoCompletedValue(context: InsertionContext): String {
        return context.document.text.substring(context.tailOffset)
    }

    private fun getStringBeforeAutoCompletedValue(context: InsertionContext, item: LookupElement): String {
        val tailOffset = if (context.document.text.substring(
                0,
                context.tailOffset
            ).endsWith("\"")
        ) context.tailOffset else context.tailOffset + 1
        return context.document.text.substring(0, tailOffset)
            .replace("\"" + item.lookupString + "\"", "")
    }

    private fun shouldAddComma(context: InsertionContext): Boolean {
        return false
    }

}
