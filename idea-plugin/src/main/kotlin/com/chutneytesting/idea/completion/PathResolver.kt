/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

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
