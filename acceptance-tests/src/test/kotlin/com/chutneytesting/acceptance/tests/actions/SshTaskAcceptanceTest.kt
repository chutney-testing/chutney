/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.assertSshCommand
import com.chutneytesting.acceptance.common.createExecuteAndCheckReportStatusOf
import com.chutneytesting.acceptance.common.validateSshCommandExitCode
import com.chutneytesting.kotlin.dsl.*

fun `SSH - Server is unreachable`(): ChutneyScenario {
  return createExecuteAndCheckReportStatusOf(
    environment = "SSH_ENV",
    scenario =
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
      environment = "SSH_ENV",
      scenario =
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
                "shellOutLines" to "results.get(0).stdout.lines().toList()".spEL()
              ),
              validations = mapOf(
                "user-check" to "shellOutLines.contains('${if (idx == 1) "internuser" else "jumpuser"}')".spEL(),
                "version-check" to "shellOutLines.contains('12.7')".spEL()
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
      environment = "SSH_ENV",
      scenario = Scenario(title = "SSH - Execute commands on server${if (idx == 1) " via proxy" else ""}") {
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
              "firstCmdResults" to "results.get(0)".spEL(),
              "secondCmdResults" to "results.get(1)".spEL(),
              "thirdCmdResults" to "results.get(2)".spEL(),
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
