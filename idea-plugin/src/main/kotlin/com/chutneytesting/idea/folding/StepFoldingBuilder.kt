/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.folding

import com.chutneytesting.idea.ChutneyUtil
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.nio.file.Paths

class StepFoldingBuilder : FoldingBuilderEx() {

    override fun getPlaceholderText(node: ASTNode): String? = "..."

    override fun isCollapsedByDefault(node: ASTNode): Boolean = true

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        if (!ChutneyUtil.isChutneyJson(root.containingFile)) {
            return emptyArray()
        }
        val descriptors = mutableListOf<FoldingDescriptor>()
        val group = FoldingGroup.newGroup("steps")
        // step folding for v2
        val jsonFile = root.containingFile as JsonFile
        val allTopLevelValues = jsonFile.allTopLevelValues
        val first = allTopLevelValues.first { it is JsonObject }
        (first as JsonObject).propertyList
          .asSequence()
          .filter { it?.name in listOf("givens", "thens") }
          .map { it.value }
          .filterIsInstance<JsonArray>()
          .flatMap { it.children.toList() }
          .filterIsInstance<JsonObject>()
          .toList()
          .forEach {
            val descriptionProperty = it.propertyList.find { it.name == "description" }
            val name = descriptionProperty?.value
            val nameString = name?.text ?: "<no-description>"
            val placeHolder = StringUtil.unquoteString(nameString)
            descriptors.add(
              FoldingDescriptor(
                it.node,
                TextRange(it.textRange.startOffset, it.textRange.endOffset),
                group,
                placeHolder
              )
            )

          }
        val `when` = first.propertyList.first { it?.name in listOf("when") }
        val psiElement = `when`.value as JsonObject
        val descriptionProperty = psiElement.propertyList.find { it.name == "description" }
        val name = descriptionProperty?.value
        val nameString = name?.text ?: "<no-description>"
        val placeHolder = StringUtil.unquoteString(nameString)
        descriptors.add(
          FoldingDescriptor(
            psiElement.node,
            TextRange(psiElement.textRange.startOffset, psiElement.textRange.endOffset),
            group,
            placeHolder
          )
        )
        return descriptors.toTypedArray()
    }

}
