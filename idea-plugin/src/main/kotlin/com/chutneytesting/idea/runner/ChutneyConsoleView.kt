package com.chutneytesting.idea.runner

import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerLifeCycleAdapter
import com.chutneytesting.idea.server.ui.ChutneyToolWindowManager
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.PoolOfTestIcons
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.TestTreeView
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.execution.testframework.sm.runner.ui.SMRootTestProxyFormatter
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.testframework.sm.runner.ui.TestTreeRenderer
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.util.ObjectUtils


class ChutneyConsoleView(
    testConsoleProperties: TestConsoleProperties,
    env: ExecutionEnvironment,
    splitterProperty: String,
    val myServer: ChutneyServer?
) : SMTRunnerConsoleView(testConsoleProperties, splitterProperty) {

    private var myFormatter: ChutneyRootTestProxyFormatter? = null

    override fun initUI() {
        super.initUI()
        val treeView = this.resultsViewer.treeView
        if (myServer == null) {
            return
        }
        val originalRenderer = ObjectUtils.tryCast(treeView?.cellRenderer, TestTreeRenderer::class.java)
        if (originalRenderer != null) {
            myFormatter = ChutneyRootTestProxyFormatter(myServer, treeView!!)
            originalRenderer.setAdditionalRootFormatter(myFormatter as ChutneyRootTestProxyFormatter)
        }
        if (!myServer.isStopped && myServer.capturedBrowsers.isEmpty()) {
            myServer.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {
                override fun onServerStarted() {
                    //TODO cleanup
                    //print("To capture a browser open ", ConsoleViewContentType.SYSTEM_OUTPUT)
                    //val url = myServer.getServerUrl() + "/capture"
                    //printHyperlink(url, OpenUrlHyperlinkInfo(url))
                    //print("\n", ConsoleViewContentType.SYSTEM_OUTPUT)
                }
            }, this)
        }
        myServer.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {
            override fun onServerTerminated(exitCode: Int) {
                print("Chutney server finished with exit code $exitCode\n", ConsoleViewContentType.SYSTEM_OUTPUT)
                ChutneyToolWindowManager.getInstance(properties.project).show()
            }
        }, this)

    }

    override fun attachToProcess(processHandler: ProcessHandler) {
        super.attachToProcess(processHandler)
        if (processHandler is NopProcessHandler) {
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun processTerminated(event: ProcessEvent) {
                    if (myFormatter != null) {
                        (myFormatter as ChutneyRootTestProxyFormatter).onTestRunProcessTerminated()
                    }
                }
            })
        }
    }

    internal class ChutneyRootTestProxyFormatter(
        private val myServer: ChutneyServer,
        private val myTestTreeView: TestTreeView
    ) : SMRootTestProxyFormatter {
        private var myTestRunProcessTerminated = false

        override fun format(testProxy: SMTestProxy.SMRootTestProxy, renderer: TestTreeRenderer) {
            if (!testProxy.isLeaf) {
                return
            }
            if (myTestRunProcessTerminated) {
                render(renderer, "Aborted", true)
            } else if (myServer.isProcessRunning) {
                if (!myServer.isReadyForCapturing) {
                    if (!myServer.isStopped) {
                        render(renderer, "Starting up server...", false)
                    }
                } else {
                    //render(renderer, "Waiting for captured browser...", false)
                }
            }
        }

        private fun render(renderer: TestTreeRenderer, msg: String, error: Boolean) {
            renderer.clear()
            if (error) {
                renderer.icon = PoolOfTestIcons.TERMINATED_ICON
            }
            renderer.append(msg)
        }

        internal fun onTestRunProcessTerminated() {
            myTestRunProcessTerminated = true
            myTestTreeView.repaint()
        }
    }
}
