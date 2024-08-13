/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.util

import java.util.concurrent.TimeoutException

interface WaitUntilUtils {
    companion object {
        @Throws(TimeoutException::class)
        fun waitUntil(condition: () -> Boolean, timeoutms: Long) {
            val start = System.currentTimeMillis()
            while (!condition.invoke()) {
                if (System.currentTimeMillis() - start > timeoutms) {
                    throw TimeoutException(String.format("Condition not meet within %s ms", timeoutms))
                }
            }
        }
    }
}
