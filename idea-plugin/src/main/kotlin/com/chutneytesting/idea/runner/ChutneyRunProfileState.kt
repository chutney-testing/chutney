package com.chutneytesting.idea.runner

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerLifeCycleAdapter
import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.autotest.ToggleAutoTestAction
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer


class ChutneyRunProfileState(
    private val project: Project,
    private val env: ExecutionEnvironment,
    private val configuration: ChutneyRunConfiguration
) : RunProfileState {

    var failedTests: MutableList<String> = mutableListOf()

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        if (configuration.getRunSettings().isExternalServerType()) {
            return executeWithServer(null)
        }
        val ideServer = ChutneyServerRegistry.instance.myServer
        if (ideServer == null || !ideServer.isProcessRunning) {
            throw ExecutionException("Chutney server is not running unexpectedly")
        }
        return executeWithServer(ideServer)
    }

    fun executeWithServer(ideServer: ChutneyServer?): ExecutionResult {
        if (!configuration.getRunSettings().isExternalServerType() && ideServer == null) {
            throw ExecutionException("[Internal error] Local Chutney ideServer running in IDE not found")
        }
        val processHandler = createProcessHandler(ideServer)
        val consoleView = createSMTRunnerConsoleView(processHandler, ideServer)
        consoleView.attachToProcess(processHandler)
        consoleView.addMessageFilter(ConsoleFileLinkFilter(project))
        val executionResult = DefaultExecutionResult(consoleView, processHandler)
        executionResult.setRestartActions(ToggleAutoTestAction())
        // processHandler.startNotify()
        executionResult.setRestartActions(
            (consoleView.properties as ChutneyConsoleProperties).createRerunFailedTestsAction(consoleView),
            ToggleAutoTestAction()
        )
        return executionResult
    }

    private fun createProcessHandler(ideServer: ChutneyServer?): ProcessHandler {
        val nopProcessHandler = NopProcessHandler()
        if (ideServer != null) {
            ideServer.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {
                override fun onServerTerminated(exitCode: Int) {
                    nopProcessHandler.destroyProcess()
                }
            }, env.project)
        }
        return nopProcessHandler
    }


    private fun createSMTRunnerConsoleView(
        processHandler: ProcessHandler,
        server: ChutneyServer?
    ): SMTRunnerConsoleView {
        //TODO review usage of env.runProfile can be ChutneyRunConfiguration or AbstractRerunFailedTestsAction
        //val configuration = env.runProfile is ChutneyRunConfiguration
        val filterProvider = ChutneyTestProxyFilterProvider(env.project)
        val testConsoleProperties = ChutneyConsoleProperties(
            project,
            configuration,
            "Chutney",
            env.executor,
            processHandler,
            filterProvider,
            failedTests
        )
        val propertyName = SMTestRunnerConnectionUtil.getSplitterPropertyName("Chutney")
        val consoleView = ChutneyConsoleView(testConsoleProperties, env, propertyName, server)
        Disposer.register(env.project, consoleView)
        SMTestRunnerConnectionUtil.initConsoleView(consoleView, "Chutney")
        return consoleView
    }

    fun getRunSettings(): ChutneyRunSettings {
        return configuration.getRunSettings()
    }
}
