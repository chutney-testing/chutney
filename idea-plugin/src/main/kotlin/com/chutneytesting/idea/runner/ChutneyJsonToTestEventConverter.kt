package com.chutneytesting.idea.runner


import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.server.ChutneyServer
import com.chutneytesting.idea.server.ChutneyServerRegistry
import com.chutneytesting.idea.util.WaitUntilUtils
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.task.ProjectTaskManager
import com.intellij.testFramework.LightVirtualFile
import java.util.concurrent.TimeUnit

fun moduleIsUpToDate(module: Module): Boolean {
    val compilerManager = CompilerManager.getInstance(module.project)
    val compilerScope = compilerManager.createModuleCompileScope(module, true)
    return compilerManager.isUpToDate(compilerScope)
}

class ChutneyJsonToTestEventConverter(
    testFrameworkName: String,
    consoleProperties: TestConsoleProperties,
    val configuration: ChutneyRunConfiguration,
    val project: Project,
    val processHandler: ProcessHandler,
    val jsonFiles: List<VirtualFile?>
) : OutputToGeneralTestEventsConverter(testFrameworkName, consoleProperties) {

    companion object{
        private val LOG = Logger.getInstance(OutputToGeneralTestEventsConverter::class.java)
    }

    override fun onStartTesting() {
        ApplicationManager.getApplication().executeOnPooledThread {
            val server = ChutneyServerRegistry.instance.myServer
            WaitUntilUtils.waitUntil({ getServerUrl(server) != null }, 60000)
            processHandler.startNotify()
            val cacheEvaluation = mutableMapOf<Int, Any>()
            jsonFiles.filterNotNull().forEachIndexed { index, virtualFile ->
                if (ChutneyUtil.isChutneyDsl(virtualFile)) {
                    //ProjectTaskManager.getInstance(project).build(ModuleUtil.findModuleForFile(virtualFile, project)).blockingGet(60)
                    val module = ModuleUtil.findModuleForFile(virtualFile, project) ?: error("cannot find module")
                    if (!moduleIsUpToDate(module = module)) {
                        ProjectTaskManager.getInstance(project).build(module).blockingGet(60, TimeUnit.SECONDS)
                    }
                    val script = """
                    import com.chutneytesting.kotlin.dsl.*    
                     
                    ${configuration.getRunSettings().methodName}()
                    """.trimIndent()
                    LOG.info("evaluating script = $script")
                    val eval =
                        ChutneyKotlinJsr223JvmLocalScriptEngineFactory(virtualFile, project).scriptEngine.eval(
                            script
                        )
                    cacheEvaluation[index] = eval
                    if (eval is List<*>) {
                        eval.forEachIndexed { s_index, any ->
                            val lightVirtualFile = LightVirtualFile("test.chutney.json", JsonFileType.INSTANCE, "$any")
                            val parser =
                                JsonTestScenariosParser(configuration, project, processHandler, lightVirtualFile)
                            parser.parseScenarios(s_index + 1)
                        }
                    } else {
                        val lightVirtualFile = LightVirtualFile("test.chutney.json", JsonFileType.INSTANCE, "$eval")
                        val parser = JsonTestScenariosParser(configuration, project, processHandler, lightVirtualFile)
                        parser.parseScenarios(index + 1)
                    }
                } else {
                    val parser = JsonTestScenariosParser(configuration, project, processHandler, virtualFile)
                    parser.parseScenarios(index + 1)
                }
            }
            jsonFiles.filterNotNull().forEachIndexed { index, virtualFile ->
                if (ChutneyUtil.isChutneyDsl(virtualFile)) {
                    val eval = cacheEvaluation.get(index)
                    if (eval is List<*>) {
                        eval.forEachIndexed { s_index, any ->
                            val lightVirtualFile = LightVirtualFile("test.chutney.json", JsonFileType.INSTANCE, "$any")
                            val parser = JsonTestReportsParser(
                                configuration,
                                project,
                                getServerUrl(ChutneyServerRegistry.instance.myServer)!!,
                                processHandler,
                                lightVirtualFile
                            )
                            parser.parseReports(s_index + 1)
                        }
                    } else {
                        val lightVirtualFile = LightVirtualFile("test.chutney.json", JsonFileType.INSTANCE, "$eval")
                        val parser = JsonTestReportsParser(
                            configuration,
                            project,
                            getServerUrl(ChutneyServerRegistry.instance.myServer)!!,
                            processHandler,
                            lightVirtualFile
                        )
                        parser.parseReports(index + 1)
                    }
                } else {
                    val parser = JsonTestReportsParser(
                        configuration,
                        project,
                        getServerUrl(ChutneyServerRegistry.instance.myServer)!!,
                        processHandler,
                        virtualFile
                    )
                    parser.parseReports(index + 1)
                }
            }
            processHandler.detachProcess()
        }
    }

    private fun getServerUrl(ideServer: ChutneyServer?): String? {
        if (configuration.getRunSettings().isExternalServerType()) {
            return configuration.getRunSettings().serverAddress
        }
        return if (ideServer != null && ideServer.isReadyForRunningTests) {
            ideServer.serverUrl
        } else null
    }
}
