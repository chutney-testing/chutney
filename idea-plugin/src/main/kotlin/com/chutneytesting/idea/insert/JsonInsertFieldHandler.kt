package com.chutneytesting.idea.insert

import com.chutneytesting.idea.completion.JsonTraversal
import com.chutneytesting.idea.completion.field.model.Field
import com.chutneytesting.idea.completion.field.model.StringField
import com.chutneytesting.idea.util.StringUtils
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.psi.PsiElement
import com.intellij.util.text.CharArrayUtil
import java.util.*

class JsonInsertFieldHandler(private val jsonTraversal: JsonTraversal, private val field: Field) :
    InsertHandler<LookupElement> {
    override fun handleInsert(insertionContext: InsertionContext, lookupElement: LookupElement) {
        handleStartingQuote(insertionContext, lookupElement)
        handleEndingQuote(insertionContext)
        if (!StringUtils.nextCharAfterSpacesAndQuotesIsColon(getStringAfterAutoCompletedValue(insertionContext))) {
            val suffixWithCaret = field.getJsonPlaceholderSuffix(getIndentation(insertionContext, lookupElement))
            val suffixWithoutCaret = suffixWithCaret!!.replace(StringField.CARET, "")
            EditorModificationUtil.insertStringAtCaret(
                insertionContext.editor,
                withOptionalComma(suffixWithoutCaret, insertionContext), false, true,
                getCaretIndex(suffixWithCaret)
            )
        }
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

    private fun getCaretIndex(suffix: String?): Int {
        return suffix!!.indexOf(StringField.CARET)
    }

    private fun getIndentation(context: InsertionContext, item: LookupElement): Int {
        val stringBeforeAutoCompletedValue = getStringBeforeAutoCompletedValue(context, item)
        return StringUtils.getNumberOfSpacesInRowStartingFromEnd(stringBeforeAutoCompletedValue)
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
        val psiFile = context.file
        return Optional.ofNullable(psiFile.findElementAt(context.startOffset))
            .map { el: PsiElement? -> !jsonTraversal.isLastChild(el!!) }
            .orElse(false)
    }

}
