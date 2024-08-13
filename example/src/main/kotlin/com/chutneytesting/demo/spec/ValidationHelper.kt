/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.demo.spec

import com.chutneytesting.kotlin.dsl.spEL

object ValidationHelper {
    fun httpStatusOK(): Pair<String, String> = httpStatusEqual(
        validationKey = "http OK",
        expected = 200
    )

    fun httpStatusNotFound(): Pair<String, String> = httpStatusEqual(
        validationKey = "http Not Found",
        expected = 404
    )

    fun httpStatusSuccess(): Pair<String, String> = httpStatusRange(
        validationKey = "http success",
        expectedMin = 200,
        expectedMax = 299
    )

    fun httpStatusClientError(): Pair<String, String> = httpStatusRange(
        validationKey = "http client error",
        expectedMin = 400,
        expectedMax = 499
    )

    fun httpStatusServerError(): Pair<String, String> = httpStatusRange(
        validationKey = "http server error",
        expectedMin = 500,
        expectedMax = 599
    )

    private fun httpStatusRange(
        validationKey: String = "httpStatusRange",
        expectedMin: Int,
        expectedMax: Int
    ): Pair<String, String> {
        return validationKey to "status >= $expectedMin && #status <= $expectedMax".spEL()
    }

    private fun httpStatusEqual(validationKey: String = "httpStatusEqual", expected: Int): Pair<String, String> {
        return validationKey to "status == $expected".spEL()
    }
}
