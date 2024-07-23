/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
