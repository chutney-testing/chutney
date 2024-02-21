package com.chutneytesting.idea.server

import com.chutneytesting.idea.server.ChutneyServerSettings.RunnerMode
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import com.intellij.util.containers.ContainerUtil

object ChutneyServerSettingsManager {
    private const val PREFIX = "Chutney.server.settings."
    private const val PORT_TIMEOUT = PREFIX + "port"
    private const val BROWSER_TIMEOUT = PREFIX + "browserTimeout"
    private const val RUNNER_MODE = PREFIX + "runnerMode"
    private val myListeners: MutableList<Listener> = ContainerUtil.createEmptyCOWList()
    @Volatile
    private var mySettings: ChutneyServerSettings? = null

    fun loadSettings(): ChutneyServerSettings {
        var settings = mySettings
        if (settings != null) {
            return settings
        }
        val builder = ChutneyServerSettings.Builder()
        val port = toInteger(loadApplicationSetting(PORT_TIMEOUT))
        if (port != null) {
            builder.setPort(port)
        }
        val browserTimeout = toInteger(loadApplicationSetting(BROWSER_TIMEOUT))
        if (browserTimeout != null) {
            builder.setBrowserTimeoutMillis(browserTimeout)
        }
        val runnerMode = loadRunnerMode()
        if (runnerMode != null) {
            builder.setRunnerMode(runnerMode)
        }
        settings = builder.build()
        mySettings = settings
        return settings
    }

    fun saveSettings(settings: ChutneyServerSettings) {
        if (settings != mySettings) {
            storeApplicationSetting(PORT_TIMEOUT, settings.port.toString())
            storeApplicationSetting(BROWSER_TIMEOUT, settings.browserTimeoutMillis.toString())
            storeApplicationSetting(RUNNER_MODE, settings.runnerMode.name)
            mySettings = settings
            fireOnChanged(settings)
        }
    }

    private fun fireOnChanged(settings: ChutneyServerSettings) {
        for (listener in myListeners) {
            listener.onChanged(settings)
        }
    }

    fun addListener(listener: Listener, disposable: Disposable) {
        myListeners.add(listener)
        Disposer.register(disposable, Disposable { myListeners.remove(listener) })
    }

    private fun loadRunnerMode(): RunnerMode? {
        val str = loadApplicationSetting(RUNNER_MODE)
        if (str != null) {
            try {
                return RunnerMode.valueOf(str)
            } catch (ignored: IllegalArgumentException) {
            }
        }
        return null
    }

    private fun toInteger(str: String?): Int? {
        if (str != null) {
            try {
                return str.toInt()
            } catch (ignored: NumberFormatException) {
            }
        }
        return null
    }

    private fun loadApplicationSetting(key: String): String? {
        val propertiesComponent = PropertiesComponent.getInstance()
        return propertiesComponent.getValue(key)
    }

    private fun storeApplicationSetting(key: String, value: String) {
        val propertiesComponent = PropertiesComponent.getInstance()
        propertiesComponent.setValue(key, value)
    }

    interface Listener {
        fun onChanged(settings: ChutneyServerSettings)
    }
}
