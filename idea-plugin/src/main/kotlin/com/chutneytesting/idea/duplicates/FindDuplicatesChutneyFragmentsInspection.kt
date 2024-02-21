package com.chutneytesting.idea.duplicates

import com.intellij.codeInsight.daemon.GroupNames
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder

/**
 * Example of an inspection, which finds a class with the same fully qualified name. The files can be diffed or deleted.
 * @author markiewb
 */
class FindDuplicatesChutneyFragmentsInspection : LocalInspectionTool() {
    override fun getDisplayName(): String {
        return "Duplicate Chutney fragments in project"
    }

    override fun getGroupDisplayName(): String {
        return GroupNames.BUGS_GROUP_NAME
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): FindDuplicatesChutneyFragmentsVisitor {
        return FindDuplicatesChutneyFragmentsVisitor(holder)
    }
}
