package com.chutneytesting.idea.runner.settings

import com.chutneytesting.idea.runner.TestType
import com.chutneytesting.idea.runner.settings.ui.ChutneyVariablesData
import com.chutneytesting.idea.util.EnumUtils
import com.intellij.openapi.util.JDOMExternalizer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.ObjectUtils
import org.jdom.Element

object ChutneyRunSettingsSerializationUtils {

    private enum class Key(val key: String) {
        TEST_TYPE("configLocationType"),
        ALL_IN_DIRECTORY("allInDirectory"),
        METHOD_NAME("methodName"),
        CONFIG_TYPE("configType"),
        SCENARIO_FILE("scenarioFile"),
        SERVER_ADDRESS("serverAddress"),
        SERVER_TYPE("serverType"),
        SCENARIOS_FILES("scenariosFiles")
    }

    fun readFromXml(element: Element): ChutneyRunSettings {
        val builder = ChutneyRunSettings()

        val testType = readEnumByName(element, Key.TEST_TYPE, TestType.SCENARIO_FILE)
        builder.testType = testType
        if (testType === TestType.ALL_SCENARIO_IN_DIRECTORY) {
            val directory = readString(element, Key.ALL_IN_DIRECTORY, "")
            builder.directory = FileUtil.toSystemDependentName(directory)
        } else if (testType === TestType.SCENARIO_FILE) {
            readScenarioFile(element, builder)
        } else if (testType === TestType.MUTLI_SCENARIO_FILES) {
            readScenariosFiles(element, builder)
        }
        val serverType = readEnumByName(element, Key.SERVER_TYPE, ServerType.INTERNAL)
        builder.serverType = serverType
        if (serverType === ServerType.EXTERNAL) {
            val serverAddress = readString(element, Key.SERVER_ADDRESS, "")
            builder.serverAddress = serverAddress
        }
        builder.variables = ChutneyVariablesData.readExternal(element)
        builder.methodName = readString(element, Key.METHOD_NAME, "")
        return builder
    }

    private fun readScenariosFiles(element: Element, builder: ChutneyRunSettings) {
        builder.scenariosFilesPath = readString(element, Key.SCENARIOS_FILES, "")
    }


    private fun readScenarioFile(element: Element, builder: ChutneyRunSettings) {
        val jsFile = readString(element, Key.SCENARIO_FILE, "")
        builder.scenarioFilePath = FileUtil.toSystemDependentName(jsFile)
    }

    private fun <E : Enum<E>> readEnumByName(element: Element, key: Key, defaultValue: E): E {
        val str = readString(element, key, "")
        val enumConstant = EnumUtils.findEnum<E>(defaultValue.declaringClass, str)
        return ObjectUtils.notNull(enumConstant, defaultValue)
    }

    private fun readString(element: Element, key: Key, defaultValue: String): String {
        val value = JDOMExternalizer.readString(element, key.key)
        return value ?: defaultValue
    }

    fun writeToXml(element: Element, runSettings: ChutneyRunSettings) {
        val testType = runSettings.testType
        writeString(element, Key.TEST_TYPE, testType.name)
        if (testType === TestType.ALL_SCENARIO_IN_DIRECTORY) {
            writeString(element, Key.ALL_IN_DIRECTORY, FileUtil.toSystemIndependentName(runSettings.directory))
        } else if (testType === TestType.SCENARIO_FILE) {
            writeScenarioFile(element, runSettings)
        } else if (testType === TestType.MUTLI_SCENARIO_FILES) {
            writeScenariosFile(element, runSettings)
        }
        writeString(element, Key.SERVER_TYPE, runSettings.serverType.name)
        if (runSettings.serverType === ServerType.EXTERNAL) {
            writeString(element, Key.SERVER_ADDRESS, runSettings.serverAddress)
        }
        runSettings.variables.writeExternal(element)
        writeString(element, Key.METHOD_NAME, runSettings.methodName)
    }

    private fun writeScenariosFile(element: Element, runSettings: ChutneyRunSettings) {
        writeString(element, Key.SCENARIOS_FILES, runSettings.scenariosFilesPath)
    }


    private fun writeScenarioFile(element: Element, runSettings: ChutneyRunSettings) {
        writeString(element, Key.CONFIG_TYPE, "FILE_PATH")
        writeString(element, Key.SCENARIO_FILE, FileUtil.toSystemIndependentName(runSettings.scenarioFilePath))
    }

    private fun writeString(element: Element, key: Key, value: String) {
        JDOMExternalizer.write(element, key.key, value)
    }

}
