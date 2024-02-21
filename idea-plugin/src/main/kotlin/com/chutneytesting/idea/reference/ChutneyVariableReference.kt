package com.chutneytesting.idea.reference

import com.chutneytesting.idea.completion.JsonTraversal
import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase


class ChutneyVariableReference(
    val tagName: String,
    val traversal: JsonTraversal,
    val psiElement: PsiElement,
    val textRange: TextRange
) : PsiReferenceBase<PsiElement>(psiElement) {

    override fun resolve(): PsiElement? {
        return traversal.getVariables(element.containingFile).stream()
            .filter { tag -> tagName == (tag as JsonProperty).name }
            .findFirst()
            .orElse(
                traversal.getVariablesFromReferences(element.containingFile)
                    .stream()
                    .filter { tag -> tagName == (tag as JsonProperty).name }
                    .findFirst()
                    .orElse(null)

            )
    }

    override fun getVariants(): Array<Any> {
        return emptyArray()
    }

    override fun getRangeInElement(): TextRange {
        return textRange
    }
}
