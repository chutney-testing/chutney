package com.chutneytesting.idea.runner

import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilBase
import com.intellij.refactoring.listeners.RefactoringElementListener
import com.intellij.refactoring.listeners.UndoRefactoringElementAdapter

object ChutneyRunConfigurationRefactoringHandler {
    fun getRefactoringElementListener(
        configuration: ChutneyRunConfiguration,
        element: PsiElement?
    ): RefactoringElementListener? {
        val fileAtElement = PsiUtilBase.asVirtualFile(element) ?: return null
        val (directory, scenarioFilePath, _, _, _, _, _, testType) = configuration.getRunSettings()
        val path = fileAtElement.path
        if (testType === TestType.ALL_SCENARIO_IN_DIRECTORY) {
            val allInDirectory = FileUtil.toSystemIndependentName(directory)
            if (allInDirectory == path) {
                return FilePathRefactoringElementListener(configuration, false, false, true)
            }
        } else {
            val jsFilePath = FileUtil.toSystemIndependentName(scenarioFilePath)
            if (jsFilePath == path) {
                return FilePathRefactoringElementListener(configuration, false, true, false)
            }
        }
        return null
    }

    private class FilePathRefactoringElementListener internal constructor(
        private val myConfiguration: ChutneyRunConfiguration,
        isConfigFile: Boolean,
        private val myIsJsTestFile: Boolean,
        private val myIsAllInDirectory: Boolean
    ) : UndoRefactoringElementAdapter() {
        override fun refactored(element: PsiElement, oldQualifiedName: String?) {
            val newFile = PsiUtilBase.asVirtualFile(element)
            if (newFile != null) {
                val newPath = FileUtil.toSystemDependentName(newFile.path)
                val settingsBuilder = myConfiguration.getRunSettings()
                if (myIsJsTestFile) {
                    settingsBuilder.scenarioFilePath = newPath
                }
                if (myIsAllInDirectory) {
                    settingsBuilder.directory = newPath
                }
                myConfiguration.setRunSettings(settingsBuilder)
            }
        }

    }
}
