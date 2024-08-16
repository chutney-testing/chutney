/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.server

interface ChutneyServerLifeCycleListener {
    fun onServerStarted()
    fun onBrowserCaptured(info: ChutneyBrowserInfo)
    fun onBrowserPanicked(info: ChutneyBrowserInfo)
    fun onServerStopped()
    fun onServerTerminated(exitCode: Int)
}
