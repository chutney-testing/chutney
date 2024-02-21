package com.chutneytesting.idea.injection

import com.chutneytesting.idea.ChutneyUtil.isChutneyJson
import com.chutneytesting.idea.reference.SpringElTemplateParser
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.lang.Language
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.util.ThreeState
import com.jetbrains.jsonSchema.impl.JsonOriginalPsiWalker

class ChutneySpELJsonValueInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (!isChutneyJson(context.containingFile)) {
            return
        }
        val walker = JsonOriginalPsiWalker.INSTANCE
        if (context !is JsonStringLiteral) return
        val isName = walker.isName(context)
        if (isName == ThreeState.NO) {
            val parse = SpringElTemplateParser.parse(context.getText())
            if (!parse.isEmpty()) {
                injectForHost(registrar, context)
            }
        }
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement?>?> {
        return listOf(JsonStringLiteral::class.java)
    }

    companion object {
        private fun injectForHost(registrar: MultiHostRegistrar, element: JsonStringLiteral) {
            val spEL = Language.findLanguageByID("SpEL") ?: return
            val text = element.text
            val parse = SpringElTemplateParser.parse(text)
            registrar.startInjecting(spEL)
            for (e in parse) {
                registrar.addPlace(
                    "(", ")", (element as PsiLanguageInjectionHost),
                    TextRange(e.startOffset + 2, e.endOffset - 1)
                )
            }
            registrar.doneInjecting()
        }
    }
}
