/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.reference

import com.chutneytesting.idea.completion.JsonTraversal
import com.chutneytesting.idea.removeAllQuotes
import com.intellij.codeInsight.completion.CompletionUtil
import com.intellij.json.JsonLanguage
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonLiteral
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.*
import com.intellij.psi.filters.ElementFilter
import com.intellij.psi.filters.position.FilterPattern
import com.intellij.util.ObjectUtils
import com.intellij.util.ProcessingContext
import com.jetbrains.jsonSchema.ide.JsonSchemaService
import com.jetbrains.jsonSchema.impl.JsonPointerReferenceProvider


class ChutneyJsonReferenceContributor : PsiReferenceContributor() {

    private val REF_PATTERN = createPropertyValuePattern("\$ref", false, false)


    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(localVariablesPattern(), createLocalVariablesProvider())
        registrar.registerReferenceProvider(REF_PATTERN, JsonPointerReferenceProvider(false))
    }

    private fun localVariablesPattern(): PsiElementPattern.Capture<JsonLiteral> {
        return psiElement(JsonLiteral::class.java)
            .withParent(psiElement(JsonProperty::class.java))
            .withText(StandardPatterns.string().contains("\${"))
            .withLanguage(JsonLanguage.INSTANCE)
    }

    private val traversal = JsonTraversal()

    private fun createPropertyValuePattern(
        propertyName: String,
        schemaOnly: Boolean,
        rootOnly: Boolean
    ): PsiElementPattern.Capture<JsonValue> {

        return psiElement(JsonValue::class.java).and(FilterPattern(object : ElementFilter {
            override fun isAcceptable(element: Any, context: PsiElement?): Boolean {
                if (element is JsonValue) {
                    if (schemaOnly && !JsonSchemaService.isSchemaFile(CompletionUtil.getOriginalOrSelf(element.containingFile))) {
                        return false
                    }

                    val property = ObjectUtils.tryCast(element.parent, JsonProperty::class.java)
                    if (property != null && property.value === element) {
                        val file = property.containingFile
                        return if (!rootOnly || file is JsonFile && file.topLevelValue === property.parent) {
                            propertyName == property.name
                        } else false
                    }
                }
                return false
            }

            override fun isClassAcceptable(hintClass: Class<*>): Boolean {
                return true
            }
        }))
    }

    private fun createLocalVariablesProvider(): PsiReferenceProvider {
        return object : PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                // val list = SpringElTemplateParser.parse(element.text)
                // val str = "\${T(java.time.format.DateTimeFormatter).ofPattern(#dateFormat).format(#date(#dateDeGlissement, #dateTimeFormat))}"
                val variableRegex = Regex("(#\\w+)\\W")
                return generateSequence(variableRegex.find(element.text)) {
                    variableRegex.find(
                        element.text,
                        it.range.last
                    )
                }
                    .map {
                        ChutneyVariableReference(
                            removeAllQuotes(it.groups[1]?.value!!).replace("#", "").trim(),
                            traversal,
                            element,
                            TextRange(it.range.first + 1, it.range.last)
                        )
                    }
                    .toList()
                    .toTypedArray()
            }
        }
    }
}
