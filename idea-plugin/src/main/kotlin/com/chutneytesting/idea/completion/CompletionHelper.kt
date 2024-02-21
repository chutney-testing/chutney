package com.chutneytesting.idea.completion

import com.chutneytesting.idea.completion.field.model.Field
import com.chutneytesting.idea.completion.value.model.Value
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement

open class CompletionHelper(val psiElement: PsiElement, val traversal: JsonTraversal) {


    fun isUniqueArrayStringValue(keyName: String): Boolean {
        return traversal.isUniqueArrayStringValue(keyName, psiElement)
    }

    fun createInsertValueHandler(value: Value): InsertHandler<LookupElement> {
        return traversal.createInsertValueHandler(value)
    }

    fun createInsertFieldHandler(field: Field): InsertHandler<LookupElement> {
        return traversal.createInsertFieldHandler(field)
    }

    fun isUniqueKey(keyName: String): Boolean {
        val children = PathFinder().findDirectNamedChildren("parent", psiElement)

        return children.stream().noneMatch({ c -> keyName == c.name })
    }

    fun createInsertStepValueHandler(value: Value): InsertHandler<LookupElement> {
        return traversal.createInsertStepValueHandler(value)
    }

}
