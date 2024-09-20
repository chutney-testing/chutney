/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.*
import com.chutneytesting.kotlin.dsl.*

fun `Scenario execution unable to login, status SUCCESS and command stderr`(): ChutneyScenario {
  return Scenario(title = "Scenario execution unable to login, status SUCCESS and command stderr") {
    Given("Target containing SSHD connection information with wrong password") {
      createEnvironment(
        "SSH_ENV_KO",
        """
        [
            {
                "name": "test_ssh",
                "url": "ssh://host.testcontainers.internal:12345",
                "properties": [
                    { "key" : "username", "value": "user" },
                    { "key" : "password", "value": "wrongpass" }
                ]
            }
        ]
      """.trimIndent()
      )
    }
    And("this scenario is saved") {
      createScenario(
        "scenarioId",
        """
        {
            "when":{
                "sentence":"Execute commands",
                "implementation":{
                    "task":"{\n type: ssh-client \n target: test_ssh \n inputs: {\n commands: [\n echo test \n] \n} \n}"
                }
            },
            "thens":[]
        }
        """.trimIndent()
      )
    }
    When("The scenario is executed") {
      executeScenario("${'$'}{#scenarioId}", "SSH_ENV_KO")
    }
    Then("the report status is FAILURE") {
      checkScenarioFailure()
    }
  }
}

fun `Scenario execution with multiple ssh action`(): ChutneyScenario {

  return Scenario(title = "Scenario execution with multiple ssh action") {
    Given("an SSHD server is started") {
      SshServerStartAction(
        usernames = listOf("test"),
        passwords = listOf("test")
      )
    }
    And("Target containing SSHD connection information") {
      createEnvironment(
        "SSH_ENV_OK",
        """
        [
            {
                "name": "test_ssh",
                "url": "ssh://${'$'}{#sshServer.host()}:${'$'}{#sshServer.port()}",
                "properties": [
                    { "key" : "username", "value": "test" },
                    { "key" : "password", "value": "test" }
                ]
            }
        ]
      """.trimIndent()
      )
    }
    And("This scenario is saved") {
      createScenario(
        "scenarioId",
        """
        {
            "when":{
                "sentence":"Execute commands",
                "implementation":{
                    "task":"{\n type: ssh-client \n target: test_ssh \n inputs: {\n commands: [\n {\n command: echo test \n timeout: 500 ms \n},{\n command: echo testbis \n} \n] \n} \n}"
                }
            },
            "thens":[
                {
                    "sentence":"Assert results",
                    "subSteps":[
                        {
                            "sentence": "Assert first command",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(0).command.command} \n expected: echo test \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert first command timeout",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(0).command.timeout.toString()} \n expected: 500 ms \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert first command exit code",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{T(Integer).toString(#results.get(0).exitCode)} \n expected: \"0\" \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert first command stdout",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(0).stdout} \n expected: \"\" \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert first command sterr",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(0).stderr} \n expected: \"\" \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert second command",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(1).command.command} \n expected: echo testbis \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert second command timeout",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(1).command.timeout.toString()} \n expected: 5000 ms \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert second command exit code",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{T(Integer).toString(#results.get(1).exitCode)} \n expected: \"0\" \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert second command stdout",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(1).stdout} \n expected: \"\" \n mode: equals \n} \n}"
                            }
                        },
                        {
                            "sentence": "Assert second command sterr",
                            "implementation":{
                                "task":"{\n type: compare \n inputs: {\n actual: \${'$'}{#results.get(1).stderr} \n expected: \"\" \n mode: equals \n} \n}"
                            }
                        }
                    ]
                }
            ]
        }
        """.trimIndent()
      )
    }
    When("The scenario is executed") {
      executeScenario("${'$'}{#scenarioId}", "SSH_ENV_OK")
    }
    Then("the report status is SUCCESS") {
      checkScenarioSuccess()
    }
    And("the SSHD server has received the commands") {
      Step("Check first command"){
        CompareAction(
          mode = "equals",
          actual = "sshServer.command(0)".spEL,
          expected = "echo test"
        )
      }
      Step("Check second command"){
        CompareAction(
          mode = "equals",
          actual = "sshServer.command(1)".spEL,
          expected = "echo testbis"
        )
      }
    }
  }
}