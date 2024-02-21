package com.chutneytesting.idea.server

open class ChutneyServerLifeCycleAdapter : ChutneyServerLifeCycleListener {
    override fun onServerStarted() {}
    override fun onServerStopped() {}
    override fun onServerTerminated(exitCode: Int) {}
    override fun onBrowserCaptured(info: ChutneyBrowserInfo) {}
    override fun onBrowserPanicked(info: ChutneyBrowserInfo) {}
}
