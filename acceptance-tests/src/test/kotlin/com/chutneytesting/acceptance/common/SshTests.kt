/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.common

import com.chutneytesting.kotlin.dsl.*

fun validateSshCommandExitCode(cmdIdx: Int = 0, comparison: String = "==", expectedCode: Int = 0) =
  "exitCode_ok-$cmdIdx" to "results.get($cmdIdx).exitCode $comparison $expectedCode".escapedSpEL()

fun ChutneyStepBuilder.assertSshCommand(
  description: String = "Assert SSH command",
  sshCommandELVarName: String = "sshCommand",
  expectedCommand: String? = null,
  expectedTimeout: String? = null,
  expectedExitCode: Int? = 0,
  expectedStdout: String,
  expectedStdoutCompare: String = "equals",
  expectedStderr: String? = "",
  expectedStderrCompare: String = "equals",
  strategy: Strategy? = null
) {
  Step(description) {
    strategy?.let { this.strategy = it }
    expectedCommand?.let {
      Step("Assert command") {
        CompareAction(
          mode = "equals",
          actual = "$sshCommandELVarName.command.command".escapedSpEL(),
          expected = it
        )
      }
    }
    expectedTimeout?.let {
      Step("Assert command timeout") {
        CompareAction(
          mode = "equals",
          actual = "$sshCommandELVarName.command.timeout.toString()".escapedSpEL(),
          expected = it
        )
      }
    }
    expectedExitCode?.let {
      Step("Assert command exit code") {
        CompareAction(
          mode = "equals",
          actual = """T(Integer).toString(${"$sshCommandELVarName.exitCode".spELVar})""".escapedElEval(),
          expected = "$it"
        )
      }
    }
    Step("Assert command stdout") {
      CompareAction(mode = expectedStdoutCompare, actual = "$sshCommandELVarName.stdout".escapedSpEL(), expected = expectedStdout)
    }
    expectedStderr?.let {
      Step("Assert command stderr") {
        CompareAction(mode = expectedStderrCompare, actual = "$sshCommandELVarName.stderr".escapedSpEL(), expected = it)
      }
    }
  }
}