package com.chutneytesting.idea.runner

import com.intellij.execution.filters.Filter
import com.intellij.execution.testframework.sm.runner.TestProxyFilterProvider
import com.intellij.openapi.project.Project

class ChutneyTestProxyFilterProvider(private val myProject: Project) : TestProxyFilterProvider {
    override fun getFilter(nodeType: String, nodeName: String, nodeArguments: String?): Filter? {
        return null
    }
}
