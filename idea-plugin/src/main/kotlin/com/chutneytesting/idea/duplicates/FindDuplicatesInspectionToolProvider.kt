/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.duplicates

import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.codeInspection.LocalInspectionTool

/**
 */
class FindDuplicatesInspectionToolProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<out Class<out LocalInspectionTool>> {
        return arrayOf(FindDuplicatesChutneyFragmentsInspection::class.java)
    }
}
