/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction
import com.chutneytesting.kotlin.junit.api.ChutneyTest

class SameMethodNameClassTest {

    @ChutneyTest
    fun sameMethodNameInOtherClassTest(): ChutneyScenario {
        return Scenario(title = "A scenario") {
            When("Something happens") {
                SuccessAction()
            }
        }
    }
}
