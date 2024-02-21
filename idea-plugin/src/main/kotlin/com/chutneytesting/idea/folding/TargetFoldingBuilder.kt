package com.chutneytesting.idea.folding

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.completion.TargetsValueCompletionHelper
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil


class TargetFoldingBuilder : FoldingBuilderEx() {

    override fun getPlaceholderText(node: ASTNode): String? = "..."

    override fun isCollapsedByDefault(node: ASTNode): Boolean = true

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        if (!ChutneyUtil.isChutneyJson(root.containingFile)) {
            return emptyArray()
        }
        val group = FoldingGroup.newGroup("targets")
        val descriptors = mutableListOf<FoldingDescriptor>()
        val properties = PsiTreeUtil.findChildrenOfType(root, JsonProperty::class.java)
        for (property in properties) {
            val jsonValue = if (property.value is JsonValue) property.value else null
            val value = jsonValue?.text
            if (value != null && property.name == "target") {
                val key = StringUtil.unquoteString(value)
                val targetsMap = TargetsValueCompletionHelper.targets.map { it.name to it.url }.toMap()
                if (targetsMap[key] != null) {
                    descriptors.add(
                        FoldingDescriptor(
                            jsonValue.node,
                            TextRange(jsonValue.textRange.startOffset + 1, jsonValue.textRange.endOffset - 1),
                            group, targetsMap[key] ?: ""
                        )
                    )
                }
            }
        }
        return descriptors.toTypedArray()
    }

}
