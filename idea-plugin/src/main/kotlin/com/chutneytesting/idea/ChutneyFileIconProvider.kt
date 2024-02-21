package com.chutneytesting.idea

import com.intellij.json.psi.JsonFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile
import javax.swing.Icon

class ChutneyFileIconProvider : com.intellij.ide.IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {

        if (element is PsiFile) {
            //val jsonPsi: PsiFile = element.viewProvider.getPsi(JsonLanguage.INSTANCE as Language) ?: return null
            if (element !is JsonFile && element !is YAMLFile) {
                return null
            }
            if (ChutneyUtil.isChutneyJson(element) || ChutneyUtil.isIcefragJson(element) || ChutneyUtil.isChutneyYaml(
                    element
                )
            ) {
                return ChutneyIcons.ChutneyFile
            }
        }
        return null
    }
}

