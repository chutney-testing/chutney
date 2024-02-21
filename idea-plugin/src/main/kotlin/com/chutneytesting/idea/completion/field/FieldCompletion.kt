package com.chutneytesting.idea.completion.field

import com.chutneytesting.idea.completion.CompletionHelper
import com.chutneytesting.idea.completion.field.model.Field
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

abstract class FieldCompletion(
    protected val completionHelper: CompletionHelper,
    val completionResultSet: CompletionResultSet
) {
    abstract fun fill()
    fun addUnique(field: Field) {
        if (completionHelper.isUniqueKey(field.name)) {
            completionResultSet.addElement(create(field, completionHelper.createInsertFieldHandler(field)))
        }
    }

    private fun create(field: Field): LookupElementBuilder {
        var lookupElementBuilder = LookupElementBuilder.create(field, field.name)
        if (field.isRequired) {
            lookupElementBuilder = lookupElementBuilder.bold()
        }
        return lookupElementBuilder
    }

    private fun create(
        field: Field,
        insertHandler: InsertHandler<LookupElement>
    ): LookupElementBuilder {
        return create(field).withInsertHandler(insertHandler)
    }

}
