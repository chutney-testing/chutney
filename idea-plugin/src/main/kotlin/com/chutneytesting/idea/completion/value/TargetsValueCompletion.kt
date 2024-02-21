package com.chutneytesting.idea.completion.value

import com.chutneytesting.idea.completion.CompletionHelper
import com.chutneytesting.idea.completion.TargetsValueCompletionHelper
import com.chutneytesting.idea.completion.value.model.StringValue
import com.chutneytesting.idea.completion.value.model.Value
import com.intellij.codeInsight.completion.CompletionResultSet
import java.util.function.Consumer

internal class TargetsValueCompletion(completionHelper: CompletionHelper, completionResultSet: CompletionResultSet) :
    ValueCompletion(completionHelper, completionResultSet) {
    override fun fill() {
        targets.forEach(Consumer { value: Value -> addValue(value) })
    }

    private val targets: List<Value>
        get() = TargetsValueCompletionHelper.targets
            .map { it.name }
            .map { value: String? -> StringValue(value!!) }
            .toList()
}
