/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.*
import com.chutneytesting.kotlin.dsl.*

fun `SSH - Server is unreachable`(): ChutneyScenario {
  return createExecuteAndCheckReportStatusOf(
    Scenario(title = "SSH - Server is unreachable") {
      When("Execute commands") {
        SshClientAction(
          target = "SSH_INTERN_SERVER_DIRECT",
          channel = SSH_CLIENT_CHANNEL.COMMAND,
          commands = listOf("echo test")
        )
      }
    },
    reportStatusSuccess = false
  )
}

fun `SSH - Execute shell on server`(): List<ChutneyScenario> {
  return listOf("SSH_JUMP_SERVER", "SSH_INTERN_SERVER").mapIndexed { idx, target ->
    createExecuteAndCheckReportStatusOf(
      Scenario(title = "SSH - Execute shell on server${if (idx == 1) " via proxy" else ""}") {
        When("open a shell and execute some commands") {
          SshClientAction(
            target = target,
            commands = listOf(
              """
                export PS1=
                whoami
                cat /etc/debian_version
                exit
            """.trimIndent()
            ),
            channel = SSH_CLIENT_CHANNEL.SHELL,
            outputs = mapOf(
              "shellOutLines" to "results.get(0).stdout.lines().toList()".escapedSpEL()
            ),
            validations = mapOf(
              "user-check" to "shellOutLines.contains('${if (idx == 1) "internuser" else "jumpuser"}')".escapedSpEL(),
              "version-check" to "shellOutLines.contains('12.7')".escapedSpEL()
            )
          )
        }
      }
    )
  }
}

fun `SSH - Execute commands on server`(): List<ChutneyScenario> {
  return listOf("SSH_JUMP_SERVER", "SSH_INTERN_SERVER").mapIndexed { idx, target ->
    createExecuteAndCheckReportStatusOf(
      Scenario(title = "SSH - Execute commands on server${if (idx == 1) " via proxy" else ""}") {
        When("Execute commands") {
          SshClientAction(
            target = target,
            channel = SSH_CLIENT_CHANNEL.COMMAND,
            commands = listOf(
              mapOf("command" to "whoami", "timeout" to "500 ms"),
              "unknownCommand",
              "cat /etc/debian_version"
            ),
            outputs = mapOf(
              "firstCmdResults" to "results.get(0)".escapedSpEL(),
              "secondCmdResults" to "results.get(1)".escapedSpEL(),
              "thirdCmdResults" to "results.get(2)".escapedSpEL(),
            ),
            validations = mapOf(
              validateSshCommandExitCode(),
              validateSshCommandExitCode(1, ">", 0),
              validateSshCommandExitCode(2)
            )
          )
        }
        Then("Assert commands results") {
          assertSshCommand(
            description = "First command",
            sshCommandELVarName = "firstCmdResults",
            expectedCommand = "whoami",
            expectedTimeout = "500 ms",
            expectedStdout = "${if (idx == 1) "internuser" else "jumpuser"}\n",
            strategy = SoftAssertStrategy()
          )
          assertSshCommand(
            description = "Second command",
            sshCommandELVarName = "secondCmdResults",
            expectedCommand = "unknownCommand",
            expectedTimeout = "5000 ms",
            expectedExitCode = 127,
            expectedStdout = "",
            expectedStderr = "command not found",
            expectedStderrCompare = "contains",
            strategy = SoftAssertStrategy()
          )
          assertSshCommand(
            description = "Third command",
            sshCommandELVarName = "thirdCmdResults",
            expectedCommand = "cat /etc/debian_version",
            expectedStdout = "12.7\n",
            strategy = SoftAssertStrategy()
          )
        }
      }
    )
  }
}

fun `SSH - Start server and Execute some commands`(sshServerPort: Int): ChutneyScenario {
  return Scenario(title = "SSH - Execute multiple actions") {
    Given("An accessible SSHD server") {
      Step("Start a SSHD server") {
        SshServerStartAction(
          port = "$sshServerPort",
          host = "0.0.0.0",
          usernames = listOf("test"),
          passwords = listOf("test")
        )
      }
      Step("Save environment with SSHD connection information") {
        createEnvironment(
          "SSH_ENV_OK",
          """
          [
              {
                  "name": "test_ssh",
                  "url": "ssh://host.docker.internal:$sshServerPort",
                  "properties": [
                      { "key" : "username", "value": "test" },
                      { "key" : "password", "value": "test" }
                  ]
              }
          ]
          """.trimIndent()
        )
      }
    }
    And("This scenario is saved") {
      createScenario("scenarioId", Scenario(title = "") {
        When("Execute commands") {
          SshClientAction(
            target = "test_ssh",
            channel = SSH_CLIENT_CHANNEL.COMMAND,
            commands = listOf(
              mapOf("command" to "echo test", "timeout" to "500 ms"),
              mapOf("command" to "echo testbis")
            )
          )
        }
        Then("Assert results") {
          Step("Assert first command parameters") {
            Step("Assert command") {
              CompareAction(
                mode = "equals",
                actual = "results.get(0).command.command".escapedSpEL(),
                expected = "echo test"
              )
            }
            Step("Assert command timeout") {
              CompareAction(
                mode = "equals",
                actual = "results.get(0).command.timeout.toString()".escapedSpEL(),
                expected = "500 ms"
              )
            }
            Step("Assert command exit code") {
              CompareAction(
                mode = "equals",
                actual = """T(Integer).toString(${"results.get(0).exitCode".spELVar})""".escapedElEval(),
                expected = "0"
              )
            }
            Step("Assert command stdout") {
              CompareAction(mode = "equals", actual = "results.get(0).stdout".escapedSpEL(), expected = "")
            }
            Step("Assert command sterr") {
              CompareAction(mode = "equals", actual = "results.get(0).stderr".escapedSpEL(), expected = "")
            }
          }
          Step("Assert second command parameters") {
            Step("Assert command") {
              CompareAction(
                mode = "equals",
                actual = "results.get(1).command.command".escapedSpEL(),
                expected = "echo testbis"
              )
            }
            Step("Assert command timeout") {
              CompareAction(
                mode = "equals",
                actual = "results.get(1).command.timeout.toString()".escapedSpEL(),
                expected = "5000 ms"
              )
            }
            Step("Assert command exit code") {
              CompareAction(
                mode = "equals",
                actual = """T(Integer).toString(${"results.get(1).exitCode".spELVar})""".escapedElEval(),
                expected = "0"
              )
            }
            Step("Assert command stdout") {
              CompareAction(mode = "equals", actual = "results.get(1).stdout".escapedSpEL(), expected = "")
            }
            Step("Assert command sterr") {
              CompareAction(mode = "equals", actual = "results.get(1).stderr".escapedSpEL(), expected = "")
            }
          }
        }
      })
    }
    When("The scenario is executed") {
      executeScenario("scenarioId".spEL, "SSH_ENV_OK")
    }
    Then("the report status is SUCCESS") {
      checkScenarioReportSuccess()
    }
    And("The SSHD server has received the commands") {
      Step("Check first command") {
        CompareAction(
          mode = "equals",
          actual = "sshServer.command(0)".spEL,
          expected = "echo test"
        )
      }
      Step("Check second command") {
        CompareAction(
          mode = "equals",
          actual = "sshServer.command(1)".spEL,
          expected = "echo testbis"
        )
      }
    }
  }
}
