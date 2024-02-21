package com.chutneytesting.idea.server.ui

import com.chutneytesting.idea.ChutneyIcons.ChutneyToolWindow
import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerSettingsManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentManager
import org.jetbrains.concurrency.Promise

class ChutneyToolWindowManager(private val myProject: Project) {

    private val myToolWindow: ToolWindow
    private val myContentManager: ContentManager
    private var myCurrentSession: ChutneyToolWindowSession? = null
    fun setAvailable(available: Boolean) {
        if (available) {
            if (myContentManager.contentCount == 0) {
                val session = ChutneyToolWindowSession(myProject)
                myCurrentSession = session
                val content = myContentManager.factory.createContent(session.component, null, true)
                content.isCloseable = true
                myContentManager.addContent(content)
            }
        } else {
            myContentManager.removeAllContents(true)
            myCurrentSession = null
        }
        myToolWindow.setAvailable(available, null)
    }

    fun show() {
        if (myToolWindow.isAvailable) {
            myToolWindow.show(null)
        }
    }

    fun restartServer(): Promise<ChutneyServer> {
        val session = myCurrentSession ?: throw RuntimeException("Chutney Server toolwindow isn't available")
        return session.restart(ChutneyServerSettingsManager.loadSettings())
    }

    companion object {
        private const val TOOL_WINDOW_ID = "Chutney Server"
        @JvmStatic
        fun getInstance(project: Project): ChutneyToolWindowManager {
            return ServiceManager.getService(project, ChutneyToolWindowManager::class.java)
        }
    }

    init {
        myToolWindow = ToolWindowManager.getInstance(myProject).registerToolWindow(
            TOOL_WINDOW_ID,
            true,
            ToolWindowAnchor.BOTTOM,
            myProject,
            true
        )
        //myToolWindow.isToHideOnEmptyContent = true
        //myToolWindow.icon = ChutneyToolWindow
        myToolWindow.isAutoHide = true
        myToolWindow.setSplitMode(true, null)
        myContentManager = myToolWindow.contentManager
    }
}
