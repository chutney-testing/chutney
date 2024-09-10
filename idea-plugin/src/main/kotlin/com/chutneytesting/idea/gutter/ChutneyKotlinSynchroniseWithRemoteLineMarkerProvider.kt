/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.gutter

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.logger.EventDataLogger
import com.chutneytesting.idea.runner.ChutneyKotlinJsr223JvmLocalScriptEngineFactory
import com.chutneytesting.idea.runner.getFullyQualifiedMethodName
import com.chutneytesting.idea.runner.moduleIsUpToDate
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.notification.NotificationListener
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.task.ProjectTaskManager
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.UIUtil
import org.apache.commons.text.StringEscapeUtils
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.awt.Component
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList

val icon = IconLoader.getIcon("icons/ksync.svg", ChutneyKotlinSynchroniseWithRemoteLineMarkerProvider::class.java)

class ChutneyKotlinSynchroniseWithRemoteLineMarkerProvider : LineMarkerProvider{
    override fun getLineMarkerInfo(psiElement: PsiElement): LineMarkerInfo<*>? {
        if (psiElement is KtNamedFunction && ChutneyUtil.isChutneyDslMethod(psiElement)) {
            val displayName = "${psiElement.name}"
            return lineMarkerInfo(psiElement.funKeyword!!, displayName)
        }
        return null
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result:  MutableCollection<in LineMarkerInfo<*>>
    ) {}

    protected fun lineMarkerInfo(anchor: PsiElement, displayName: String): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            anchor,
            anchor.textRange,
            icon,
            { e -> e.containingFile.name },
            { e, elt ->
                run {
                    if (elt.isValid) {
                        showPopup(e, elt, displayName)
                    }
                }
            },
            GutterIconRenderer.Alignment.RIGHT,
            { displayName }
        )
    }

    private fun showPopup(e: MouseEvent, psiElement: PsiElement, displayName: String) {
        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(listOf("synchronise"))
            .setRenderer(object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: JList<*>?,
                    value: Any?,
                    index: Int,
                    isSelected: Boolean,
                    cellHasFocus: Boolean
                ): Component {
                    val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel

                    if (value is String) {
                        label.icon = icon
                        label.text = "$value $displayName"
                    }

                    return label
                }
            })
            .setMovable(true)
            .setItemChosenCallback { type ->
                if (psiElement.isValid) {
                    executeSyncRemote(psiElement)
                }
            }.createPopup().show(RelativePoint(e))
    }

    private fun executeSyncRemote(psiElement: PsiElement) {
        val virtualFile = psiElement.containingFile.virtualFile ?: error("cannot find virtualFile")
        val project = psiElement.project
        if (!ChutneySettings.checkRemoteServerUrlConfig(project)) return
        val module = ModuleUtil.findModuleForFile(virtualFile, project) ?: error("cannot find module")

        if (moduleIsUpToDate(module).not()) {
            ProjectTaskManager.getInstance(project).build(module).blockingGet(60, TimeUnit.SECONDS)
        }

        val script = """
                    import com.chutneytesting.kotlin.dsl.*    
                     
                    ${getFullyQualifiedMethodName(psiElement)}()
                    """.trimIndent()

        val eval =
            ChutneyKotlinJsr223JvmLocalScriptEngineFactory(virtualFile, project).scriptEngine.eval(script)

        val scenariosToUpdate = if (eval is List<*>) eval else listOf(eval)

        scenariosToUpdate.filterNotNull().forEach { scenario ->
            val id = scenario::class.members.firstOrNull { it.name == "id" }?.call(scenario) ?: return@forEach
            val title = scenario::class.members.firstOrNull { it.name == "title" }?.call(scenario) ?: return@forEach
            val escapeJson = StringEscapeUtils.escapeJson(scenario.toString())
            val query = "/api/scenario/v2/raw"

            try {
                FilenameIndex.getVirtualFilesByName(
                    "$id-${title}.chutney.json",
                    GlobalSearchScope.moduleScope(module)
                ).forEachIndexed { _, virtualFile  ->
                    val psiFile = PsiManager.getInstance(project).findFile(virtualFile)!!
                    val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return@forEachIndexed
                    document.setText("$scenario".replace("\r\n", "\n"))
                    FileDocumentManager.getInstance().saveDocument(document)
                    UIUtil.invokeAndWaitIfNeeded(Runnable {
                        CodeStyleManager.getInstance(project).reformat(psiFile)
                    })
                }
                val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
                val result: Map<String, Any> = HttpClient.get(
                    serverInfo, "$query/$id"
                )
                val version = result["version"].toString()
                val body =
                    "{\"id\": $id ,\"title\": \"$title\" ,\"version\": $version , \"content\":\"${escapeJson}\"}"

                HttpClient.post<Any>(serverInfo,query, body)
                EventDataLogger.logInfo(
                    "Remote scenario file updated with success.<br>" +
                            "<a href=\"${serverInfo.url}/#/scenario/$id/executions?open=last&active=last\">Open in remote Chutney Server</a>",
                    project,
                    NotificationListener.URL_OPENING_LISTENER
                )
            } catch (e: Exception) {
                EventDataLogger.logError("Remote scenario file could not be updated.<br> cause: [$e]", project)
            }
        }
    }
}
