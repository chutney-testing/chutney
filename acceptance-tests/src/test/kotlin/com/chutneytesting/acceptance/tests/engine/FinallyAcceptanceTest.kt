/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.engine

import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.DebugAction
import com.chutneytesting.kotlin.dsl.Scenario

val `Step of a type self registering as Finally Action does not create an infinite loop` = Scenario(title = "Step of a type self registering as Finally Action does not create an infinite loop") {
  Given("this scenario is saved") {
    createScenario("scenarioId",
        """
        {
            "when":{
                "sentence":"Do something to register finally action",
                "implementation":{
                    "task":"{\n type: self-registering-finally \n }"
                }
            },
            "thens":[]
        }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("${'$'}{#scenarioId}","DEFAULT")
  }
  Then("the report status is SUCCESS") {
    DebugAction()
  }
}

