/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.completion.value.model

import com.chutneytesting.idea.completion.value.StepValueData

class StepValue(val data: StepValueData) : Value(data.name) {
    override val isQuotable: Boolean
        get() = true

}
