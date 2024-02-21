package com.chutneytesting.idea.server

interface ChutneyServerLifeCycleListener {
    fun onServerStarted()
    fun onBrowserCaptured(info: ChutneyBrowserInfo)
    fun onBrowserPanicked(info: ChutneyBrowserInfo)
    fun onServerStopped()
    fun onServerTerminated(exitCode: Int)
}
