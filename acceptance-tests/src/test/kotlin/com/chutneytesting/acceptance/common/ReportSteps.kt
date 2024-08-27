/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.common

import com.chutneytesting.kotlin.dsl.ChutneyStepBuilder
import com.chutneytesting.kotlin.dsl.CompareAction
import com.chutneytesting.kotlin.dsl.spEL

fun ChutneyStepBuilder.checkScenarioSuccess() {
  CompareAction(
      mode = "equals",
      actual = "json(#report, \"$.report.status\")".spEL,
      expected = "SUCCESS"
  )
}

fun ChutneyStepBuilder.checkScenarioFailure() {
  CompareAction(
      mode = "equals",
      actual = "json(#report, \"$.report.status\")".spEL,
      expected = "FAILURE"
  )
}