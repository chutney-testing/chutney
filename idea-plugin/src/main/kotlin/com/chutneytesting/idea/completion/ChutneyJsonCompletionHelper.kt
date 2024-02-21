package com.chutneytesting.idea.completion

import com.intellij.psi.PsiElement

class ChutneyJsonCompletionHelper(
    psiElement: PsiElement?,
    traversal: JsonTraversal?,
    private val pathResolver: PathResolver
) : CompletionHelper(psiElement!!, traversal!!) {
    fun completeTargetsValue(): Boolean {
        return pathResolver.isTargetsValue(psiElement)
    }

    fun completeStepsValue(): Boolean {
        return pathResolver.isStepsValue(psiElement)
    }

    fun completeHeadersKey(): Boolean {
        return pathResolver.childOfHeaders(psiElement)
    }

    fun completeVariableValue(): Boolean { //return SpringElTemplateParser.parse()
        return !pathResolver.isRefsValue(psiElement)
    }

}
