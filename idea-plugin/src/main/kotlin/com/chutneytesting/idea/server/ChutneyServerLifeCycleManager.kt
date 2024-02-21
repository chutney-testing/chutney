package com.chutneytesting.idea.server

import com.chutneytesting.idea.common.ChutneyCommonConstants
import com.chutneytesting.idea.server.JsonUtil.getChildAsObject
import com.chutneytesting.idea.server.JsonUtil.getChildAsString
import com.google.gson.JsonObject
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.UIUtil
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class ChutneyServerLifeCycleManager : ChutneyServerOutputListener {
    private val myListeners: MutableList<ChutneyServerLifeCycleListener> = CopyOnWriteArrayList()
    private val myDisposables: MutableList<Disposable> = CopyOnWriteArrayList()
    private val myCapturedBrowsers: MutableMap<String, ChutneyBrowserInfo> = ContainerUtil.newHashMap()
    var isServerStarted = false
        private set
    var isServerStopped = false
        private set

    fun addListener(listener: ChutneyServerLifeCycleListener, disposable: Disposable) {
        myListeners.add(listener)
        if (isServerStarted) {
            listener.onServerStarted()
            for (browserInfo in myCapturedBrowsers.values) {
                listener.onBrowserCaptured(browserInfo)
            }
        }
        val d = Disposable { myListeners.remove(listener) }
        myDisposables.add(d)
        Disposer.register(disposable, d)
    }

    fun removeListener(listener: ChutneyServerLifeCycleListener) {
        myListeners.remove(listener)
    }

    val capturedBrowsers: List<ChutneyBrowserInfo>
        get() = Collections.unmodifiableCollection(myCapturedBrowsers.values).toList()

    override fun onOutputAvailable(text: String, outputType: Key<*>) {}
    override fun onEvent(obj: JsonObject) {
        UIUtil.invokeLaterIfNeeded {
            val type = getChildAsString(obj, ChutneyCommonConstants.EVENT_TYPE)
            if (ChutneyCommonConstants.SERVER_STARTED == type) {
                onServerStarted()
            } else if (ChutneyCommonConstants.SERVER_STOPPED == type) {
                onServerStopped()
            } else if (ChutneyCommonConstants.BROWSER_CAPTURED == type) {
                onBrowserCaptured(obj)
            } else if (ChutneyCommonConstants.BROWSER_PANICKED == type) {
                onBrowserPanicked(obj)
            }
        }
    }

    private fun onServerStarted() {
        if (isServerStarted) {
            LOG.warn("[on server started] Chutney server already started")
        }
        LOG.info("Chutney server started")
        isServerStarted = true
        isServerStopped = false
        for (listener in myListeners) {
            listener.onServerStarted()
        }
    }

    private fun onServerStopped() {
        if (isServerStopped) {
            LOG.warn("[on server stopped] Chutney server already stopped")
        }
        LOG.info("Chutney server stopped")
        isServerStarted = false
        isServerStopped = true
        for (listener in myListeners) {
            listener.onServerStopped()
        }
    }

    private fun onBrowserCaptured(obj: JsonObject) {
        val info = getBrowserInfo(obj)
        if (info == null) {
            LOG.warn("No browser info parsed, aborting...")
            return
        }
        if (myCapturedBrowsers.put(info.id, info) != null) {
            LOG.warn("Capturing already captured browser: $info")
        }
        for (listener in myListeners) {
            listener.onBrowserCaptured(info)
        }
    }

    private fun onBrowserPanicked(obj: JsonObject) {
        val info = getBrowserInfo(obj)
        if (info == null) {
            LOG.warn("No browser info parsed, aborting...")
            return
        }
        if (myCapturedBrowsers.remove(info.id) == null) {
            LOG.warn("Not-captured browser panicked: $info")
        }
        for (listener in myListeners) {
            listener.onBrowserPanicked(info)
        }
    }

    fun onTerminated(exitCode: Int) {
        UIUtil.invokeLaterIfNeeded {
            if (!isServerStopped) {
                onServerStopped()
            }
            for (listener in myListeners) {
                listener.onServerTerminated(exitCode)
            }
            dispose()
        }
    }

    private fun dispose() {
        myListeners.clear()
        myCapturedBrowsers.clear()
        for (disposable in myDisposables) {
            Disposer.dispose(disposable)
        }
        myDisposables.clear()
    }

    companion object {
        private val LOG = Logger.getInstance(ChutneyServerLifeCycleManager::class.java)
        private fun getBrowserInfo(obj: JsonObject): ChutneyBrowserInfo? {
            val browserInfoObj = getChildAsObject(obj, ChutneyCommonConstants.BROWSER_INFO)
            if (browserInfoObj != null) {
                val id = getChildAsString(browserInfoObj, ChutneyCommonConstants.BROWSER_INFO_ID)
                val name = getChildAsString(browserInfoObj, ChutneyCommonConstants.BROWSER_INFO_NAME)
                if (id != null && name != null) {
                    return ChutneyBrowserInfo(id, name)
                }
            }
            return null
        }
    }
}
