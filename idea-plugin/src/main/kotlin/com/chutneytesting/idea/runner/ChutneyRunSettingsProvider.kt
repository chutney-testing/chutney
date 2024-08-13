/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.runner

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.intellij.psi.PsiElement

interface ChutneyRunSettingsProvider {
    fun provideSettings(psiElement: PsiElement): ChutneyRunSettings?
}
