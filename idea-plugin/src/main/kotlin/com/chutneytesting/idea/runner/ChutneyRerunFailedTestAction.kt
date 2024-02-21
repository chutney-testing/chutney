package com.chutneytesting.idea.runner

import com.intellij.execution.Executor
import com.intellij.execution.Location
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.testframework.AbstractTestProxy
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope


class ChutneyRerunFailedTestAction(consoleView: SMTRunnerConsoleView, consoleProperties: ChutneyConsoleProperties) :
    AbstractRerunFailedTestsAction(consoleView) {

    init {
        init(consoleProperties)
        model = consoleView.resultsViewer
    }

    private fun toLocation(project: Project, test: AbstractTestProxy): Location<*> {
        return test.getLocation(project, GlobalSearchScope.allScope(project))
    }

    override fun getRunProfile(environment: ExecutionEnvironment): MyRunProfile? {
        val configuration = myConsoleProperties.configuration as ChutneyRunConfiguration
        val project = configuration.project
        val failedTests = getFailedTests(project)
            .asSequence()
            .filter { it.isLeaf }
            .map { test -> toLocation(project, test).virtualFile }
            .filterNotNull()
            .map { it.path }.toMutableList()
        //configuration.setRunSettings(configuration.getRunSettings().copy(scenariosFilesPath = failedTests.joinToString(separator = ";"), testType = TestType.MUTLI_SCENARIO_FILES))
        val state = ChutneyRunProfileState(configuration.project, environment, configuration)
        state.failedTests = failedTests

        return object : AbstractRerunFailedTestsAction.MyRunProfile(configuration) {
            override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
                return state
            }
        }

    }
}
