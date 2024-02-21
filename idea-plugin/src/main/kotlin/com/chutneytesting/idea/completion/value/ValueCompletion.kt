package com.chutneytesting.idea.completion.value

import com.chutneytesting.idea.completion.CompletionHelper
import com.chutneytesting.idea.completion.value.model.Value
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

abstract class ValueCompletion(
    protected val completionHelper: CompletionHelper,
    protected val completionResultSet: CompletionResultSet
) {
    abstract fun fill()
    open fun addValue(value: Value) {
        if (completionHelper.isUniqueArrayStringValue(value.value)) {
            completionResultSet.addElement(create(value, completionHelper.createInsertValueHandler(value)))
        }
    }

    private fun create(
        value: Value,
        insertHandler: InsertHandler<LookupElement>
    ): LookupElementBuilder {
        return LookupElementBuilder.create(value.value).withInsertHandler(insertHandler)
    }

}
