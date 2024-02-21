package com.chutneytesting.idea.runner


import com.chutneytesting.idea.server.ChutneyBrowserInfo
import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerLifeCycleAdapter
import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.chutneytesting.idea.server.ui.ChutneyToolWindowManager.Companion.getInstance
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.AsyncProgramRunner
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.util.Alarm
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.rejectedPromise
import org.jetbrains.concurrency.resolvedPromise

/**
 *
 */
class ChutneyRunProgramRunner : AsyncProgramRunner<RunnerSettings>() {

    override fun getRunnerId(): String {
        return "ChutneyClientRunner"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile is ChutneyRunConfiguration
    }

    override fun execute(environment: ExecutionEnvironment, state: RunProfileState): Promise<RunContentDescriptor?> {
        val ChutneyState = state as ChutneyRunProfileState
        if (ChutneyState.getRunSettings().isExternalServerType()) {
            return resolvedPromise(start(null, false, state, environment))
        }
        val ChutneyToolWindowManager = getInstance(environment.project)
        ChutneyToolWindowManager.setAvailable(true)
        // ChutneyServer server = ChutneyServerRegistry.getInstance().getServer();
        val server = ChutneyServerRegistry.instance.myServer
        if (server != null && !server.isStopped) {
            return resolvedPromise(start(server, false, state, environment))
        }
        return ChutneyToolWindowManager.restartServer()
            .thenAsync {
                try {
                    return@thenAsync if (it == null) null else start(
                        it,
                        false,
                        state,
                        environment
                    )?.let { it1 -> resolvedPromise(it1) }
                } catch (e: ExecutionException) {
                    return@thenAsync rejectedPromise(e)
                }
            }

    }

    companion object {
        fun start(
            server: ChutneyServer?,
            fromDebug: Boolean,
            state: ChutneyRunProfileState,
            environment: ExecutionEnvironment
        ): RunContentDescriptor? {
            FileDocumentManager.getInstance().saveAllDocuments()
            val executionResult = state.executeWithServer(server)
            val contentBuilder = RunContentBuilder(executionResult, environment)
            val descriptor = contentBuilder.showRunContent(environment.contentToReuse)
            if (server != null && executionResult.processHandler is NopProcessHandler) {
                server.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {


                    override fun onBrowserCaptured(info: ChutneyBrowserInfo) {
                        if (fromDebug) {
                            val alarm = Alarm(Alarm.ThreadToUse.SWING_THREAD, descriptor)
                            alarm.addRequest({ ExecutionUtil.restartIfActive(descriptor) }, 1000)
                        } else {
                            ExecutionUtil.restartIfActive(descriptor)
                        }
                        server.removeLifeCycleListener(this)
                    }

                }, contentBuilder)


            }

            return descriptor
        }

    }


}
