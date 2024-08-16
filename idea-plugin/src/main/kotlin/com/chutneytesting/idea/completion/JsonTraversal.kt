/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.completion

import com.chutneytesting.idea.completion.field.model.Field
import com.chutneytesting.idea.completion.value.model.Value
import com.chutneytesting.idea.insert.JsonInsertFieldHandler
import com.chutneytesting.idea.insert.JsonInsertValueHandler
import com.chutneytesting.idea.insert.JsonStepInsertValueHandler
import com.chutneytesting.idea.removeAllQuotes
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.json.psi.*
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


class JsonTraversal {
    private fun getRootObject(psiFile: PsiFile): Optional<JsonObject> {
        return Arrays.stream(psiFile.children)
            .filter { el -> el is JsonObject }
            .map { JsonObject::class.java.cast(it) }
            .findFirst()
    }

    private fun <T : PsiElement> getRootChildrenOfType(psiFile: PsiFile, type: Class<T>): List<T> {
        val children = getRootObject(psiFile).map { it.children }
            .orElse(arrayOf())

        return Arrays.stream(children)
            .filter { child -> type.isAssignableFrom(child::class.java) }
            .map { type.cast(it) }
            .collect(Collectors.toList<T>())
    }

    fun getTags(psiFile: PsiFile): List<PsiElement> {
        return getRootChildrenOfType(psiFile, JsonProperty::class.java).stream()
            .filter { jsonProperty -> "tags" == jsonProperty.name }
            .map(({ it.value }))
            .map { el -> Arrays.asList(el?.children) }
            .flatMap(({ it.stream() }))
            .filter { el -> el is JsonObject }
            .map(({ JsonObject::class.java.cast(it) }))
            .map { jsonObject -> jsonObject.findProperty("name") }
            .map(({ it?.value }))
            .filter(({ Objects.nonNull(it) }))
            .collect(Collectors.toList<PsiElement>())
    }

    fun getVariables(psiFile: PsiFile): List<PsiElement> {
        val list = mutableListOf<PsiElement>()

        val flatMap = PsiTreeUtil
            .findChildrenOfType(psiFile, JsonProperty::class.java)
            .filter { it.name == "entries" || it.name == "outputs" }
            .flatMap { listOf(it?.children) }

        for (arrayOfPsiElements in flatMap) {
            val elements = arrayOfPsiElements?.filter { el -> el is JsonObject }
                ?.flatMap { listOf(it.children) }
            for (i in elements!!) {
                list.addAll(i)
            }
        }
        return list

    }

    fun getVariablesFromReferences(psiFile: PsiFile): List<PsiElement> {
        return PsiTreeUtil
            .findChildrenOfType(psiFile, JsonProperty::class.java)
            .asSequence()
            .filter { it.name == "\$ref" }
            .map { jsonProperty -> StringUtil.unquoteString(jsonProperty?.value?.text!!) }
            .map { ref -> Paths.get(psiFile.virtualFile.parent.path, ref) }
            .map { path -> VirtualFileManager.getInstance().findFileByUrl(VfsUtilCore.pathToUrl(path.toUri().path)) }
            .filterNotNull()
            .map { virtualFile -> PsiManager.getInstance(psiFile.project).findFile(virtualFile) }
            .map { psi -> getVariables(psi!!) }
            .flatten()
            .toList()
    }


    fun isUniqueArrayStringValue(value: String, psiElement: PsiElement): Boolean {
        return Optional.ofNullable(psiElement.parent)
            .map { it.parent }
            .filter { el -> el is JsonArray }
            .map { el -> Arrays.asList(*el.children) }
            .map<Stream<PsiElement>> { children -> children.stream().filter { c -> c is JsonLiteral } }
            .map<Stream<JsonLiteral>> { childrenStream -> childrenStream.map { JsonLiteral::class.java.cast(it) } }
            .map<Boolean> { childrenStream ->
                childrenStream.noneMatch({ jsonLiteral ->
                    value == removeAllQuotes(
                        jsonLiteral.text
                    )
                })
            }
            .orElse(true)
    }

    fun createInsertValueHandler(value: Value): InsertHandler<LookupElement> {
        return JsonInsertValueHandler(value)
    }

    fun createInsertFieldHandler(field: Field): InsertHandler<LookupElement> {
        return JsonInsertFieldHandler(this, field)
    }

    fun isLastChild(psiElement: PsiElement): Boolean {
        val lastChildOfParent = Optional.ofNullable(psiElement.parent)
            .map<PsiElement> { it.parent }
            .map<PsiElement> { it.parent }
            .map { el -> el.children[el.children.size - 1] }

        val child = Optional.of(psiElement)
            .map<PsiElement>({ it.parent })
            .map<PsiElement>({ it.parent })

        return lastChildOfParent == child
    }

    fun isKey(psiElement: PsiElement): Boolean {
        return Optional.ofNullable(psiElement.parent)
            .filter { JsonPsiUtil.isPropertyKey(it) }
            .isPresent
    }

    fun createInsertStepValueHandler(value: Value): InsertHandler<LookupElement> {
        return JsonStepInsertValueHandler(value)
    }
}
