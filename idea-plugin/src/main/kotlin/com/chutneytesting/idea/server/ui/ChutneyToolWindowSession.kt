package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerLifeCycleAdapter
import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.chutneytesting.idea.server.ChutneyServerSettings
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.TabbedPaneWrapper
import com.intellij.ui.border.CustomLineBorder
import com.intellij.ui.tabs.TabInfo
import com.intellij.util.ui.JBUI
import org.jetbrains.concurrency.Promise
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class ChutneyToolWindowSession(val project: Project) {
    private val myToolbar: ActionToolbar
    private val myTabs: TabbedPaneWrapper
    private val myRootPanel: JPanel
    private val mySettingsTab: ChutneyServerSettingsTab
    private var myConsoleTab: ChutneyServerConsoleTab? = null
    private fun addTab(tabInfo: TabInfo) {
        myTabs.addTab(tabInfo.text, tabInfo.component)
    }

    val component: JComponent
        get() = myRootPanel

    private fun createActionToolbar(): ActionToolbar {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(ChutneyServerRestartAction(this))
        actionGroup.add(ChutneyServerStopAction())
        // TODO: 18/12/2018 cleanup
/*actionGroup.add(new AnAction("Configure paths to local web browsers", null, PlatformIcons.WEB_ICON) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                ShowSettingsUtil settingsUtil = ShowSettingsUtil.getInstance();
                settingsUtil.editConfigurable(e.getProject(), new BrowserSettings());
            }
        });*/return ActionManager.getInstance().createActionToolbar("ChutneyToolWindowSession", actionGroup, false)
    }

    fun saveSettings() {
        mySettingsTab.saveSettings()
    }

    private val orRegisterConsoleContent: ChutneyServerConsoleTab
        private get() {
            if (myConsoleTab == null) {
                myConsoleTab = ChutneyServerConsoleTab(project, project)
                addTab(myConsoleTab!!.tabInfo)
            }
            return myConsoleTab!!
        }

    private fun attachToServer(server: ChutneyServer) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        myToolbar.updateActionsImmediately()
        server.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {
            override fun onServerStopped() {
                myToolbar.updateActionsImmediately()
            }
        }, project)
        val consoleTab = orRegisterConsoleContent
        consoleTab.attachToServer(server)
        myTabs.selectedTitle = consoleTab.tabInfo.text
    }

    private fun showServerStartupError(error: Throwable) {
        val consoleView = orRegisterConsoleContent
        consoleView.showServerStartupError(error)
    }

    fun restart(settings: ChutneyServerSettings): Promise<ChutneyServer> {
        return ChutneyServerRegistry.instance.restartServer(settings)
            .onError { error: Throwable -> showServerStartupError(error) }
            .then { server: ChutneyServer ->
                attachToServer(server)
                server
            }
    }

    init {
        myToolbar = createActionToolbar()
        myTabs = TabbedPaneWrapper(project)
        myTabs.component.border = JBUI.Borders.empty(0, 2, 0, 0)
        mySettingsTab = ChutneyServerSettingsTab(project)
        addTab(mySettingsTab.tabInfo)
        val server = ChutneyServerRegistry.instance.myServer
        if (server != null && server.isProcessRunning) {
            attachToServer(server)
        }
        myRootPanel = JPanel(BorderLayout(0, 0))
        myToolbar.component.border = CustomLineBorder(JBColor.border(), 0, 0, 0, 1)
        myRootPanel.add(myToolbar.component, BorderLayout.WEST)
        myRootPanel.add(myTabs.component, BorderLayout.CENTER)
    }
}
