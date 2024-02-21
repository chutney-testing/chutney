package com.chutneytesting.idea.folding

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.completion.JsonTraversal
import com.chutneytesting.idea.reference.SpringElTemplateParser
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ThreeState
import com.jetbrains.jsonSchema.impl.JsonOriginalPsiWalker


class VariableFoldingBuilder : FoldingBuilderEx() {

    override fun getPlaceholderText(node: ASTNode): String? = "..."

    override fun isCollapsedByDefault(node: ASTNode): Boolean = true

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val jsonPsi = root.containingFile
        if (!ChutneyUtil.isChutneyJson(jsonPsi)) {
            return emptyArray()
        }
        val group = FoldingGroup.newGroup("variables")
        val descriptors = mutableListOf<FoldingDescriptor>()
        val literals = PsiTreeUtil.findChildrenOfType(root, JsonStringLiteral::class.java)
        val jsonTraversal = JsonTraversal()
        val variablesInCurrentFile = jsonTraversal.getVariables(jsonPsi)
        val variablesInFrags = jsonTraversal.getVariablesFromReferences(jsonPsi)
        val map = (variablesInCurrentFile + variablesInFrags)
            .map { it as JsonProperty }
            .map { StringUtil.unquoteString(it.name) to StringUtil.unquoteString(it.value?.text!!) }
            .toMap()
        val walker = JsonOriginalPsiWalker.INSTANCE
        for (literal in literals) {
            val isName = walker.isName(literal)
            if (isName == ThreeState.NO) {
                val value = literal?.text?.let { StringUtil.unquoteString(it) } ?: ""
                if (value.contains("\${#")) {
                    val parser = SpringElTemplateParser.parse(value)
                    var info = value
                    parser
                        .map { it to value.substring(it.startOffset, it.endOffset) }
                        .map { it.first to map[it.second.substring(3, it.second.length - 1)] }
                        .forEach {
                            if (it.second != null) {
                                info = info.replace(
                                    value.substring(it.first.startOffset, it.first.endOffset),
                                    it.second as String
                                )
                            }
                        }
                    descriptors.add(
                        FoldingDescriptor(
                            literal.node,
                            TextRange(literal.textRange.startOffset + 1, literal.textRange.endOffset - 1),
                            group,
                            info
                        )
                    )
                }
            }
        }
        return descriptors.toTypedArray()
    }
}
