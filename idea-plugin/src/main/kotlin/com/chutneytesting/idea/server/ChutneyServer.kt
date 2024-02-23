package com.chutneytesting.idea.server

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.PathUtil
import com.intellij.util.ui.UIUtil
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.Path

class ChutneyServer(settings: ChutneyServerSettings) {
    val settings: ChutneyServerSettings
    private val myProcessHandler: OSProcessHandler
    private val myName: String
    private val myDisposable: MyDisposable
    private val myOutputProcessor: ChutneyServerOutputProcessor
    private val myLifeCycleManager: ChutneyServerLifeCycleManager
    val isProcessRunning: Boolean
        get() = !myProcessHandler.isProcessTerminated

    val serverUrl: String
        get() = "http://localhost:" + settings.port

    val isReadyForCapturing: Boolean
        get() = isProcessRunning && myLifeCycleManager.isServerStarted && !myLifeCycleManager.isServerStopped

    //&& !getCapturedBrowsers().isEmpty();
    val isReadyForRunningTests: Boolean
        get() = isReadyForCapturing //&& !getCapturedBrowsers().isEmpty();

    val capturedBrowsers: Collection<ChutneyBrowserInfo>
        get() = myLifeCycleManager.capturedBrowsers

    val isStopped: Boolean
        get() = myLifeCycleManager.isServerStopped

    fun addOutputListener(listener: ChutneyServerOutputListener) {
        UIUtil.invokeLaterIfNeeded { myOutputProcessor.addListener(listener) }
    }

    fun addLifeCycleListener(listener: ChutneyServerLifeCycleListener, disposable: Disposable) {
        myLifeCycleManager.addListener(listener, disposable)
    }

    fun removeLifeCycleListener(listener: ChutneyServerLifeCycleListener) {
        myLifeCycleManager.removeListener(listener)
    }

    fun shutdownAsync() {
        if (!myProcessHandler.isProcessTerminated) {
            LOG.info("$myName: shutting down asynchronously...")
            ApplicationManager.getApplication().executeOnPooledThread {
                if (!myProcessHandler.isProcessTerminated) {
                    ScriptRunnerUtil.terminateProcessHandler(myProcessHandler, 1000, myProcessHandler.commandLine)
                }
            }
        }
    }

    override fun toString(): String {
        return myName
    }

    private inner class MyDisposable : Disposable {
        private val myDisposed = false
        override fun dispose() {
            if (myDisposed) {
                return
            }
            LOG.info("Disposing $myName")
            shutdownAsync()
            myOutputProcessor.dispose()
        }
    }

    companion object {
        private val LOG = Logger.getInstance(ChutneyServer::class.java)
        private val NEXT_ID = AtomicInteger(1)
        @Throws(IOException::class)
        private fun start(settings: ChutneyServerSettings, id: Int): OSProcessHandler {
            val commandLine = createCommandLine(settings)
            val processHandler: OSProcessHandler
            processHandler = try {
                KillableColoredProcessHandler(commandLine)
            } catch (e: ExecutionException) {
                throw IOException(
                    "Cannot start " + formatName(
                        id,
                        null
                    ) + ".\nCommand: " + commandLine.commandLineString, e
                )
            }
            LOG.info(
                formatName(
                    id,
                    processHandler.getProcess()
                ) + " started successfully: " + commandLine.commandLineString
            )
            ProcessTerminatedListener.attach(processHandler)
            processHandler.setShouldDestroyProcessRecursively(true)
            return processHandler
        }

        private fun formatName(id: Int, process: Process?): String {
            var name = "ChutneyServer#$id"
            if (process != null && SystemInfo.isUnix) {
                try {
                    val pid = OSProcessUtil.getProcessID(process)
                    name += " (pid $pid)"
                } catch (ignored: Exception) {
                }
            }
            return name
        }

        private fun createCommandLine(settings: ChutneyServerSettings): GeneralCommandLine {
            val libFolder = File(PathUtil.toSystemIndependentName(PathManager.getPluginsPath() + "/chutney-idea-plugin/lib"))
          val ideaServerJarFile = libFolder.walk().find { it.name.startsWith("local-api-unsecure-") && it.name.endsWith(".jar") }
                  ?: throw RuntimeException("local-api-unsecure jar file not found in " + libFolder.absolutePath)

            val commandLine = GeneralCommandLine()
            val javaHomePath = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
            commandLine.exePath = Path(javaHomePath).resolve("bin").resolve("java").toString()
            val charset = StandardCharsets.UTF_8
            commandLine.charset = charset
            commandLine.addParameter("-Dfile.encoding=" + charset.name())
            commandLine.addParameter("-Dloader.path=" + PathUtil.toSystemIndependentName(PathManager.getConfigPath() + "/chutney-idea-plugin/dependencies/"))
            commandLine.workDirectory = ideaServerJarFile.parentFile
            commandLine.addParameter("-jar")
            commandLine.addParameter(ideaServerJarFile.name)
            commandLine.addParameter("--server.port=" + settings.port)
            commandLine.addParameter("--chutney.configuration-folder=" + PathUtil.toSystemIndependentName(PathManager.getConfigPath() + "/chutney-idea-plugin/conf"))
            return commandLine
        }

        /* private val classpath: String
             private get() {
                 val classes = arrayOf<Class<*>>(JarLauncher::class.java)
                 val result: MutableList<String> = ContainerUtil.newArrayList()
                 for (clazz in classes) {
                     val path = PathUtil.getJarPathForClass(clazz)
                     val file = File(path)
                     result.add(file.absolutePath)
                 }
                 return StringUtil.join(result, File.pathSeparator)
             }*/
    }

    init {
        val id = NEXT_ID.getAndIncrement()
        this.settings = settings
        myProcessHandler = start(settings, id)
        myName = formatName(id, myProcessHandler.process)
        myOutputProcessor = ChutneyServerOutputProcessor(myProcessHandler)
        myProcessHandler.startNotify()
        myLifeCycleManager = ChutneyServerLifeCycleManager()
        myOutputProcessor.addListener(myLifeCycleManager)
        myDisposable = MyDisposable()
        myProcessHandler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                LOG.info(myName + " terminated with exit code " + event.exitCode)
                myLifeCycleManager.onTerminated(event.exitCode)
                Disposer.dispose(myDisposable)
            }
        })
        Disposer.register(ApplicationManager.getApplication(), myDisposable)
    }
}
