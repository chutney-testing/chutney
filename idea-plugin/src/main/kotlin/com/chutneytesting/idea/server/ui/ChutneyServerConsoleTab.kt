package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerOutputListener
import com.google.gson.JsonObject
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.ui.tabs.TabInfo
import java.awt.BorderLayout
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JPanel

class ChutneyServerConsoleTab(project: Project, parentDisposable: Disposable) {
    private val myConsoleView: ConsoleView
    private val myStatusView: ChutneyServerStatusView
    val tabInfo: TabInfo

    fun attachToServer(server: ChutneyServer) {
        myStatusView.attachToServer(server)
        myConsoleView.clear()
        server.addOutputListener(object : ChutneyServerOutputListener {
            override fun onOutputAvailable(text: String, outputType: Key<*>) {
                val contentType = ConsoleViewContentType.getConsoleViewType(outputType)
                myConsoleView.print(text, contentType)
            }

            override fun onEvent(obj: JsonObject) {}
        })
    }

    fun showServerStartupError(error: Throwable) {
        myConsoleView.clear()
        val buffer = StringWriter()
        val printer = PrintWriter(buffer)
        try {
            error.printStackTrace(printer)
        } finally {
            printer.close()
        }
        myConsoleView.print(buffer.buffer.toString(), ConsoleViewContentType.ERROR_OUTPUT)
    }

    companion object {
        private fun createContent(consoleView: ConsoleView, capturingView: ChutneyServerStatusView): JPanel {
            val panel = JPanel(BorderLayout(0, 0))
            val consoleComponent = consoleView.component
            panel.add(consoleComponent, BorderLayout.CENTER)
            val consoleActionToolbar = createActionToolbar(consoleView)
            consoleActionToolbar.setTargetComponent(consoleComponent)
            panel.add(consoleActionToolbar.component, BorderLayout.WEST)
            panel.add(capturingView.component, BorderLayout.NORTH)
            return panel
        }

        private fun createActionToolbar(consoleView: ConsoleView): ActionToolbar {
            val group = DefaultActionGroup()
            val actions = consoleView.createConsoleActions()
            for (action in actions) {
                group.add(action)
            }
            return ActionManager.getInstance().createActionToolbar("ChutneyServerConsoleTab", group, false)
        }
    }

    init {
        val consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project)
        consoleBuilder.setViewer(true)
        myConsoleView = consoleBuilder.console
        Disposer.register(parentDisposable, myConsoleView)
        myStatusView = ChutneyServerStatusView(parentDisposable)
        val panel = createContent(myConsoleView, myStatusView)
        tabInfo = TabInfo(panel)
        tabInfo.text = "Console"
    }
}
