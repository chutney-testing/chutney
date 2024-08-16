/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.completion.value.model

class StringValue(value: String) : Value(value) {

    override val isQuotable: Boolean
        get() = true
}
