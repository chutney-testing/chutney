package com.chutneytesting.idea.completion.field

import com.chutneytesting.idea.completion.CompletionHelper
import com.chutneytesting.idea.completion.field.model.ChutneyJsonFields.headers
import com.chutneytesting.idea.completion.field.model.Field
import com.intellij.codeInsight.completion.CompletionResultSet
import java.util.function.Consumer

internal class HeadersCompletion(completionHelper: CompletionHelper?, completionResultSet: CompletionResultSet?) :
    FieldCompletion(completionHelper!!, completionResultSet!!) {
    override fun fill() {
        headers().forEach(Consumer { field: Field? -> addUnique(field!!) })
    }
}
