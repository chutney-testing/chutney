/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.common

import com.chutneytesting.kotlin.dsl.ChutneyStepBuilder
import com.chutneytesting.kotlin.dsl.HttpPostAction
import com.chutneytesting.kotlin.dsl.statusValidation

const val UNKNOWN_TARGET = "unknownhost:12345"

fun ChutneyStepBuilder.createEnvironment(environmentName: String, targets: String) {
  HttpPostAction(
    target = "CHUTNEY_LOCAL",
    uri = "/api/v2/environments",
    headers = jsonHeader(),
    body = """
            {
                "name": "$environmentName",
                "description": "",
                "targets": $targets
            }
           """,
    validations = mapOf(
      statusValidation(200)
    )
  )
}