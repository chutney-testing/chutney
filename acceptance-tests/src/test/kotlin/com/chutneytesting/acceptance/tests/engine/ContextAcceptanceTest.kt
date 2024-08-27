/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.engine

import com.chutneytesting.acceptance.common.checkScenarioFailure
import com.chutneytesting.acceptance.common.checkScenarioSuccess
import com.chutneytesting.acceptance.common.createScenario
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.Scenario

val `Action instantiation and execution of a success scenario` = Scenario(title = "Action instantiation and execution of a success scenario") {
  Given("this scenario is saved") {
    createScenario("scenarioId",
        """
        {
            "when":{
                "sentence":"Success scenario",
                "implementation":{
                    "task":"{\n type: success \n }"
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
    checkScenarioSuccess()
  }
}

val `Task instantiation and execution of a failed scenario` = Scenario(title = "Task instantiation and execution of a failed scenario") {
  Given("this scenario is saved") {
    createScenario("scenarioId",
        """
        {
            "when":{
                "sentence":"Step fail",
                "implementation":{
                    "task":"{\n type: fail \n }"
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
  Then("the report status is FAILURE") {
    checkScenarioFailure()
  }
}

val `Task instantiation and execution of a sleep scenario` = Scenario(title = "Task instantiation and execution of a sleep scenario") {
  Given("this scenario is saved") {
    createScenario("scenarioId",
        """
        {
            "when":{
                "sentence":"Step sleep",
                "implementation":{
                    "task":"{\n type: sleep \n inputs: {\n duration: 20 ms \n} \n}"
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
    checkScenarioSuccess()
  }
}

val `Task instantiation and execution of a debug scenario` = Scenario(title = "Task instantiation and execution of a debug scenario") {
  Given("this scenario is saved") {
    createScenario("scenarioId",
        """
        {
            "when":{
                "sentence":"Put value in context",
                "implementation":{
                    "task":"{\n type: context-put \n inputs: {\n entries: {\n \"test key\": valeur \n} \n} \n}"
                }
            },
            "thens":[
                {
                    "implementation":{
                        "task":"{\n type: debug \n}"
                    }
                }
            ]
        }
        """.trimIndent()
    )
  }
  When("The scenario is executed") {
    executeScenario("${'$'}{#scenarioId}","DEFAULT")
  }
  Then("the report status is SUCCESS") {
    checkScenarioSuccess()
  }
}