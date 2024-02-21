package com.chutneytesting.idea.runner

import com.chutneytesting.idea.ChutneyUtil
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.execution.Executor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.icons.AllIcons
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.annotations.NotNull
import javax.swing.Icon


class ChutneyJsonLineMarkerProvider : ChutneyLineMarkerProvider() {

    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {
        val jsonPsi = psiElement.containingFile
        if (!ChutneyUtil.isChutneyJson(jsonPsi)) {
            return null
        }
        if (ChutneyUtil.isChutneyV2Json(jsonPsi)) {
            if (psiElement is JsonProperty && psiElement.name == "title") {
                val anchor = PsiTreeUtil.getDeepestFirst(psiElement)
                val value = psiElement.value
                val displayName = value?.text ?: "<no-name> scenario"
                return lineMarkerInfo(anchor, displayName)
            }

        } else {
            if (psiElement is JsonProperty && psiElement.name == "scenario") {
                val anchor = PsiTreeUtil.getDeepestFirst(psiElement)
                val value = psiElement.value
                var displayName = ""
                if (value is JsonObject) {
                    displayName = value.findProperty("name")?.value?.text ?: "<no-name> scenario"
                }
                return lineMarkerInfo(anchor, displayName)
            }
        }
        return null
    }

    override fun collectSlowLineMarkers(
        list:  MutableList<out PsiElement>,
        result:  MutableCollection<in LineMarkerInfo<*>>
    ) {

    }


}

sealed class Type {

    internal abstract val isAvailable: Boolean

    internal abstract val icon: Icon

    internal abstract val executor: Executor

    internal abstract fun getTitle(displayName: String): String

    object RUN : Type() {
        override val isAvailable: Boolean
            get() = true

        override val icon: Icon
            get() = AllIcons.Toolwindows.ToolWindowRun

        override val executor: Executor
            get() = DefaultRunExecutor.getRunExecutorInstance()

        override fun getTitle(displayName: String): String {
            return "Run $displayName"
        }
    }
}
