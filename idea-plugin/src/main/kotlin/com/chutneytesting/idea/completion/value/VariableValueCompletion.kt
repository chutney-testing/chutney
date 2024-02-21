package com.chutneytesting.idea.completion.value

import com.chutneytesting.idea.completion.CompletionHelper
import com.chutneytesting.idea.completion.JsonTraversal
import com.chutneytesting.idea.completion.value.model.StringValue
import com.chutneytesting.idea.completion.value.model.Value
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonProperty
import com.intellij.psi.PsiElement
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream

internal class VariableValueCompletion(completionHelper: CompletionHelper?, completionResultSet: CompletionResultSet?) :
    ValueCompletion(completionHelper!!, completionResultSet!!) {
    override fun fill() {
        variables.forEach(Consumer { value: Value -> addValue(value) })
    }

    private val variables: List<Value>
        private get() {
            val jsonTraversal = JsonTraversal()
            val containingFile = completionHelper.psiElement.containingFile
            val variablesInCurrentFile = jsonTraversal.getVariables(containingFile)
            val variablesInFrags = jsonTraversal.getVariablesFromReferences(containingFile)
            return Stream.concat(variablesInCurrentFile.stream(), variablesInFrags.stream())
                .map { tag: PsiElement -> (tag as JsonProperty).name }
                .map { value: String? -> StringValue(value!!) }
                .collect(Collectors.toList())
        }

    override fun addValue(value: Value) {
        completionResultSet.addElement(LookupElementBuilder.create(value.value))
    }
}
