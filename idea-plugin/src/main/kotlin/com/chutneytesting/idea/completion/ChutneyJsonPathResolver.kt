package com.chutneytesting.idea.completion

import com.intellij.psi.PsiElement

class ChutneyJsonPathResolver : PathResolver {
    private fun hasPath(psiElement: PsiElement, pathExpression: String): Boolean {
        return PathFinder().isInsidePath(psiElement, pathExpression)
    }

    override fun isTargetsValue(psiElement: PsiElement): Boolean {
        return hasPath(psiElement, "$.scenario.**.target") or hasPath(psiElement, "target")
    }

    override fun isStepsValue(psiElement: PsiElement): Boolean {
        return hasPath(psiElement, "$.scenario.**.name")
    }

    override fun isRefsValue(psiElement: PsiElement): Boolean {
        return hasPath(psiElement, "$.scenario.**.\$ref")
    }

    override fun childOfHeaders(psiElement: PsiElement): Boolean {
        return hasPath(psiElement, "$.scenario.**.headers.**")
    }
}
