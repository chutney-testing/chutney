/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.common

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.kotlin.dsl.ChutneyStepBuilder
import com.chutneytesting.kotlin.dsl.CompareAction
import com.chutneytesting.kotlin.dsl.spEL

fun ChutneyStepBuilder.checkScenarioReportSuccess() {
  checkScenarioReportStatus(StatusDto.SUCCESS)
}

fun ChutneyStepBuilder.checkScenarioReportFailure() {
  checkScenarioReportStatus(StatusDto.FAILURE)
}

private fun ChutneyStepBuilder.checkScenarioReportStatus(status: StatusDto) {
  CompareAction(
    mode = "equals",
    actual = "json(#report, '$.report.status')".spEL,
    expected = status.name
  )
}

fun jsonHeader() = mapOf("Content-Type" to "application/json;charset=UTF-8")