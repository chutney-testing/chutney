/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.completion.field

import com.chutneytesting.idea.completion.ChutneyJsonCompletionHelper
import com.intellij.codeInsight.completion.CompletionResultSet
import java.util.*

object ChutneyJsonFieldCompletionFactory {
    fun from(
        completionHelper: ChutneyJsonCompletionHelper,
        completionResultSet: CompletionResultSet?
    ): Optional<FieldCompletion> {
        return if (completionHelper.completeHeadersKey()) {
            Optional.of(HeadersCompletion(completionHelper, completionResultSet))
        } else {
            Optional.empty()
        }
    }
}
