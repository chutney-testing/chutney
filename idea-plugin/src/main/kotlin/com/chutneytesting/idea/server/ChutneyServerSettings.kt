package com.chutneytesting.idea.server

class ChutneyServerSettings(
    val port: Int,
    val browserTimeoutMillis: Int,
    val runnerMode: RunnerMode,
    private val myTarget: String
) {

    enum class RunnerMode {
        DEBUG, DEBUG_NO_TRACE, DEBUG_OBSERVE, PROFILE, QUIET, INFO
    }

    override fun toString(): String {
        return "port=" + port +
                ", browserTimeout=" + browserTimeoutMillis +
                ", runnerMode=" + runnerMode +
                ", target=" + myTarget
    }

    class Builder {
        private var myPort = 9876
        private var myBrowserTimeoutMillis = 30000
        private var myRunnerMode = RunnerMode.QUIET
        private var myTarget = "GLOBAL"
        fun setPort(port: Int): Builder {
            myPort = port
            return this
        }

        fun setBrowserTimeoutMillis(browserTimeoutMillis: Int): Builder {
            myBrowserTimeoutMillis = browserTimeoutMillis
            return this
        }

        fun setRunnerMode(runnerMode: RunnerMode): Builder {
            myRunnerMode = runnerMode
            return this
        }

        fun setTarget(target: String): Builder {
            myTarget = target
            return this
        }

        fun build(): ChutneyServerSettings {
            return ChutneyServerSettings(myPort, myBrowserTimeoutMillis, myRunnerMode, myTarget)
        }
    }

}
