package com.chutneytesting.idea.runner

import com.chutneytesting.idea.runner.settings.ChutneySettingsUtil
import com.intellij.execution.Executor
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.testframework.TestConsoleProperties
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.execution.testframework.sm.runner.TestProxyFilterProvider
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile


class ChutneyConsoleProperties(
    project: Project,
    val configuration: ChutneyRunConfiguration,
    testFrameworkName: String,
    executor: Executor,
    val processHandler: ProcessHandler,
    val filterProvider: ChutneyTestProxyFilterProvider,
    val failedTests: MutableList<String>
) : SMTRunnerConsoleProperties(configuration, testFrameworkName, executor), SMCustomMessagesParsing {

    init {
        isUsePredefinedMessageFilter = false
        setIfUndefined(TestConsoleProperties.HIDE_PASSED_TESTS, false)
        setIfUndefined(TestConsoleProperties.HIDE_IGNORED_TEST, true)
        setIfUndefined(TestConsoleProperties.SCROLL_TO_SOURCE, true)
        isIdBasedTestTree = true
        isPrintTestingStartedTime = false
    }


    override fun createTestEventsConverter(
        testFrameworkName: String,
        consoleProperties: TestConsoleProperties
    ): OutputToGeneralTestEventsConverter {
        val list: List<VirtualFile?>
        list = when {
            failedTests.isNotEmpty() -> failedTests.map {
                LocalFileSystem.getInstance().findFileByPath(it)
            }.toMutableList()
            configuration.getRunSettings().testType === TestType.ALL_SCENARIO_IN_DIRECTORY -> ChutneySettingsUtil.collectChutneyScenarioFilesInDirectory(
                project,
                LocalFileSystem.getInstance().findFileByPath(configuration.getRunSettings().directory)!!
            )
            configuration.getRunSettings().testType === TestType.MUTLI_SCENARIO_FILES -> configuration.getRunSettings().scenariosFilesPath.split(
                ";"
            ).map { LocalFileSystem.getInstance().findFileByPath(it) }.toMutableList()
            else -> {
                val scenarioFilePath = configuration.getRunSettings().scenarioFilePath
                mutableListOf(LocalFileSystem.getInstance().findFileByPath(scenarioFilePath))
            }
        }
        return ChutneyJsonToTestEventConverter(
            testFrameworkName,
            consoleProperties,
            configuration,
            project,
            processHandler,
            list
        )
    }

    override fun getTestLocator(): SMTestLocator? {
        return ChutneyTestLocationProvider.INSTANCE
    }

    override fun getFilterProvider(): TestProxyFilterProvider? {
        return filterProvider
    }

    override fun createRerunFailedTestsAction(consoleView: ConsoleView): AbstractRerunFailedTestsAction {
        return ChutneyRerunFailedTestAction(consoleView as SMTRunnerConsoleView, this)
    }

}

