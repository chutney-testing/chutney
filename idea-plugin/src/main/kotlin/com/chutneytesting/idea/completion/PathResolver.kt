package com.chutneytesting.idea.completion

import com.intellij.psi.PsiElement

interface PathResolver {
    fun isTargetsValue(psiElement: PsiElement): Boolean {
        return false
    }

    fun isStepsValue(psiElement: PsiElement): Boolean
    fun isRefsValue(psiElement: PsiElement): Boolean
    fun childOfHeaders(psiElement: PsiElement): Boolean
}
