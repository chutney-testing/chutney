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
