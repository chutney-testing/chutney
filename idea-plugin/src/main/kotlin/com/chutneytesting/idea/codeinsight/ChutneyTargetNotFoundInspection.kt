package com.chutneytesting.idea.codeinsight

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.completion.TargetsValueCompletionHelper
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.psi.JsonElementVisitor
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.PsiElementVisitor


class ChutneyTargetNotFoundInspection : LocalInspectionTool() {

    override fun getDisplayName(): String {
        return "Chutney Target Not Found"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val targets = TargetsValueCompletionHelper.targets.map { it.name }
        return object : JsonElementVisitor() {
            override fun visitObject(jsonObject: JsonObject) {
                if (!ChutneyUtil.isChutneyJson(jsonObject.containingFile)) {
                    return
                }
                jsonObject.propertyList.filter { it.name == "target" }.forEach { property ->
                    val propertyValue = property.value as JsonStringLiteral
                    val value = propertyValue.value
                    if (!targets.contains(value)) {
                        holder.registerProblem(propertyValue, "Chutney Target Not Found")
                    }
                }
            }
        }
    }
}
