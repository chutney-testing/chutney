package com.chutneytesting.idea.server

import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise

class ChutneyServerRegistry {
    var myServer: ChutneyServer? = null

    fun restartServer(settings: ChutneyServerSettings): Promise<ChutneyServer> {
        val server = myServer
        val promise = AsyncPromise<ChutneyServer>()
        if (server != null && server.isProcessRunning) {
            server.addLifeCycleListener(object : ChutneyServerLifeCycleAdapter() {
                override fun onServerStopped() {
                    myServer = null
                    doStart(settings, promise)
                }
            }, ApplicationManager.getApplication())
            server.shutdownAsync()
            return promise
        } else {
            doStart(settings, promise)
        }
        return promise
    }

    private fun doStart(settings: ChutneyServerSettings, promise: AsyncPromise<ChutneyServer>) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val server = ChutneyServer(settings)
                ApplicationManager.getApplication().invokeLater {
                    myServer = server
                    promise.setResult(server)
                }
            } catch (e: Exception) {
                ApplicationManager.getApplication().invokeLater { promise.setError(e) }
            }
        }
    }

    companion object {
        val instance = ChutneyServerRegistry()
    }
}
