/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.demo.sync

import com.chutneytesting.demo.spec.ArxivSpecs
import com.chutneytesting.demo.spec.ChutneyDBSpecs
import com.chutneytesting.demo.spec.SWAPISpecs
import com.chutneytesting.kotlin.util.ChutneyServerInfo

fun main() {
    DemoServer.synchronize()
}

object DemoServer {
    val CHUTNEY_DEMO = ChutneyServerInfo(
        url = "http://localhost",
        user = "admin",
        password = "Admin"
    )

    const val ENVIRONMENT_DEMO = "DEMO"

    fun synchronize() {
        SWAPISpecs.synchronize(CHUTNEY_DEMO, ENVIRONMENT_DEMO)
        ArxivSpecs.synchronize(CHUTNEY_DEMO, ENVIRONMENT_DEMO)
        ChutneyDBSpecs.synchronize(CHUTNEY_DEMO, ENVIRONMENT_DEMO)
    }
}
