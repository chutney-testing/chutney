/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
