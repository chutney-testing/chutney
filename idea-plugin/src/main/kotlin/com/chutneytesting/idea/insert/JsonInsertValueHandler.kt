package com.chutneytesting.idea.insert

import com.chutneytesting.idea.completion.value.model.Value
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.util.text.CharArrayUtil

class JsonInsertValueHandler(private val value: Value) : InsertHandler<LookupElement> {
    override fun handleInsert(insertionContext: InsertionContext, lookupElement: LookupElement) {
        if (value.isQuotable) {
            handleStartingQuote(insertionContext, lookupElement)
            handleEndingQuote(insertionContext)
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
            EditorModificationUtil.moveCaretRelatively(insertionContext.editor, 1)
        }
    }

}
