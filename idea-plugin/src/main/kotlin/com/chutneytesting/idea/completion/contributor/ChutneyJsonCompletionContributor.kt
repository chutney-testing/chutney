package com.chutneytesting.idea.completion.contributor

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.completion.ChutneyJsonCompletionHelper
import com.chutneytesting.idea.completion.ChutneyJsonPathResolver
import com.chutneytesting.idea.completion.JsonTraversal
import com.chutneytesting.idea.completion.field.ChutneyJsonFieldCompletionFactory
import com.chutneytesting.idea.completion.value.ChutneyJsonValueCompletionFactory
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet


class ChutneyJsonCompletionContributor : CompletionContributor() {

    private val jsonTraversal: JsonTraversal = JsonTraversal()

    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        if (ChutneyUtil.isChutneyJson(parameters.originalFile)) {
            val psiElement = parameters.position
            val pathResolver = ChutneyJsonPathResolver()
            val completionHelper = ChutneyJsonCompletionHelper(psiElement, jsonTraversal, pathResolver)
            when {
                jsonTraversal.isKey(psiElement) -> ChutneyJsonFieldCompletionFactory.from(completionHelper, result)
                    .ifPresent { it.fill() }
                else -> ChutneyJsonValueCompletionFactory.from(completionHelper, result)
                    .ifPresent { it.fill() }
            }
        }

    }
}
