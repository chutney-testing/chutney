package com.chutneytesting.idea.completion.value

import com.chutneytesting.idea.completion.ChutneyJsonCompletionHelper
import com.intellij.codeInsight.completion.CompletionResultSet
import java.util.*

object ChutneyJsonValueCompletionFactory {
    fun from(
        completionHelper: ChutneyJsonCompletionHelper,
        completionResultSet: CompletionResultSet
    ): Optional<ValueCompletion> {
        if (completionHelper.completeTargetsValue()) {
            return Optional.of(TargetsValueCompletion(completionHelper, completionResultSet))
        } else if (completionHelper.completeStepsValue()) {
            return Optional.of(StepsValueCompletion(completionHelper, completionResultSet))
        } else if (completionHelper.completeVariableValue()) {
            return Optional.of(VariableValueCompletion(completionHelper, completionResultSet))
        }
        return Optional.empty()
    }
}
