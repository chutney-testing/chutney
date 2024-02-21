package com.chutneytesting.idea.runner

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.execution.*
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.ui.awt.RelativePoint
import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList

abstract class ChutneyLineMarkerProvider : LineMarkerProvider {

    protected fun lineMarkerInfo(anchor: PsiElement, displayName: String): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            anchor,
            anchor.textRange,
            IconLoader.getIcon("/runConfigurations/testState/run_run.svg", ChutneyLineMarkerProvider::class.java),
            { e -> e.containingFile.name },
            { e, elt ->
                run {
                    if (elt.isValid) {
                        showPopup(e, elt, displayName)
                    }
                }

            }
            , GutterIconRenderer.Alignment.RIGHT,
            { displayName }
        )
    }

    private fun showPopup(e: MouseEvent, psiElement: PsiElement, displayName: String) {
        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(getAvailableTypes().toList())
            .setRenderer(object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: JList<*>?,
                    value: Any?,
                    index: Int,
                    isSelected: Boolean,
                    cellHasFocus: Boolean
                ): Component {
                    val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel

                    if (value is Type) {
                        label.icon = value.icon
                        label.text = value.getTitle(displayName)
                    }

                    return label
                }
            })
            .setMovable(true)
            .setItemChosenCallback { type ->
                if (psiElement.isValid) {
                    execute(type.executor, psiElement)
                }
            }.createPopup().show(RelativePoint(e))
    }

    private fun execute(executor: Executor, element: PsiElement) {
        val project = element.project
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val dataContext = createDataContext(editor, element)
        val ChutneyOriginalProducer = getChutneyRunConfigurationProducer()
        val context = ConfigurationContext.getFromContext(dataContext)
        var created = false
        var configuration = ChutneyOriginalProducer.findExistingConfiguration(context)
        if (configuration == null) {
            created = true
            val fromContext = ChutneyOriginalProducer.createConfigurationFromContext(context)
            if (fromContext != null) {
                configuration = fromContext.configurationSettings
            } else {
                return
            }
        }

        execute(project, executor, configuration, created)
    }

    private fun getChutneyRunConfigurationProducer(): RunConfigurationProducer<ChutneyRunConfiguration> {
        return RunConfigurationProducer.getInstance(ChutneyRunConfigurationProducer::class.java)
    }

    private fun createDataContext(editor: Editor, element: PsiElement): DataContext? {
        val dataContext = DataManager.getInstance().getDataContext(editor.component)
        return DataContext { dataId ->
            if (Location.DATA_KEY.`is`(dataId)) {
                return@DataContext PsiLocation(element.project, element)
            }
            dataContext.getData(dataId)
        }
    }

    private fun execute(
        project: Project,
        executor: Executor,
        configuration: RunnerAndConfigurationSettings,
        created: Boolean
    ) {
        val runManager: RunManager = RunManager.getInstance(project)
        if (created) {
            runManager.setTemporaryConfiguration(configuration)
        }
        runManager.selectedConfiguration = configuration
        val builder: ExecutionEnvironmentBuilder? = ExecutionEnvironmentBuilder.createOrNull(executor, configuration)
        if (builder != null) {
            ExecutionManager.getInstance(project).restartRunProfile(builder.build())
        }
    }

    private fun getAvailableTypes(): Array<Type> {
        return arrayOf(Type.RUN)
    }
}
