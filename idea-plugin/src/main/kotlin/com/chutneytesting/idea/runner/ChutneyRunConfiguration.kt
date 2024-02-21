package com.chutneytesting.idea.runner

import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.chutneytesting.idea.runner.settings.ChutneyRunSettingsSerializationUtils
import com.chutneytesting.idea.runner.settings.ui.ChutneyRunConfigurationEditor
import com.chutneytesting.idea.util.ProjectRootUtils
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.intellij.psi.PsiElement
import com.intellij.refactoring.listeners.RefactoringElementListener
import org.jdom.Element
import java.io.File


class ChutneyRunConfiguration(project: Project, factory: ConfigurationFactory, name: String?) :
    LocatableConfigurationBase<RunConfiguration>(project, factory, name), RefactoringListenerProvider {
    private var myRunSettings = ChutneyRunSettings()


    @Volatile
    private var myGeneratedName: String? = null

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return ChutneyRunConfigurationEditor(project)
    }


    override fun getState(executor: Executor, executionEnvironment: ExecutionEnvironment): RunProfileState? {
        return ChutneyRunProfileState(project, executionEnvironment, this)
    }

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        super.readExternal(element)
        val runSettings = ChutneyRunSettingsSerializationUtils.readFromXml(element)
        setRunSettings(runSettings)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        ChutneyRunSettingsSerializationUtils.writeToXml(element, myRunSettings)
    }

    fun getRunSettings(): ChutneyRunSettings {
        return myRunSettings
    }

    override fun suggestedName(): String? {
        var generatedName = myGeneratedName
        if (myGeneratedName == null) {
            generatedName = generateName()
            myGeneratedName = generatedName
        }
        return generatedName
    }

    fun resetGeneratedName(): String {
        val name = generateName()
        myGeneratedName = name
        return name
    }

    private fun generateName(): String {
        val testType = myRunSettings.testType
        if (testType === TestType.ALL_SCENARIO_IN_DIRECTORY) {
            val directoryPath = myRunSettings.directory
            var rootRelativePath = ProjectRootUtils.getRootRelativePath(project, directoryPath)
            if (rootRelativePath == null) {
                rootRelativePath = directoryPath
            }
            return "All in $rootRelativePath"
        } else if (testType === TestType.MUTLI_SCENARIO_FILES) {
            val list = myRunSettings.scenariosFilesPath.split(";")
            val paths = list.joinToString(separator = ", ") { File(it).name }
            return "Composed scenarios(${list.size}): $paths"
        } else if (testType === TestType.SCENARIO_FILE) {
            val file = File(myRunSettings.scenarioFilePath)
            return file.name
        }
        return "Unnamed"
    }

    fun setRunSettings(settings: ChutneyRunSettings) {
        myRunSettings = settings
    }

    override fun getRefactoringElementListener(element: PsiElement?): RefactoringElementListener? {
        return ChutneyRunConfigurationRefactoringHandler.getRefactoringElementListener(this, element)
    }

}

