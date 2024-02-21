package com.chutneytesting.idea.runner

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.intellij.psi.PsiElement

interface ChutneyRunSettingsProvider {
    fun provideSettings(psiElement: PsiElement): ChutneyRunSettings?
}
