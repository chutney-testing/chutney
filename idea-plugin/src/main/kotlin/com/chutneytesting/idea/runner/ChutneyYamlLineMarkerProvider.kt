package com.chutneytesting.idea.runner

import com.chutneytesting.idea.ChutneyUtil
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.annotations.NotNull

class ChutneyYamlLineMarkerProvider : ChutneyLineMarkerProvider() {
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {
        val psiFile = psiElement.containingFile
        if (!ChutneyUtil.isChutneyYaml(psiFile)) {
            return null
        }
        if ((psiElement is LeafPsiElement) && (psiElement.text == "title")) {
            val displayName = (psiElement.parent).text.split(":")[1].trim()
            return lineMarkerInfo(psiElement, displayName)
        }
        return null
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result:  MutableCollection<in LineMarkerInfo<*>>
    ) {}
}
