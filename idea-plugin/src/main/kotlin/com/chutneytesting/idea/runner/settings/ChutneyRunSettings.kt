package com.chutneytesting.idea.runner.settings

import com.chutneytesting.idea.runner.TestType
import com.chutneytesting.idea.runner.settings.ui.ChutneyVariablesData

data class ChutneyRunSettings(
    var directory: String = "",
    var scenarioFilePath: String = "",
    var scenariosFilesPath: String = "",
    var methodName: String = "",
    var variables: ChutneyVariablesData = ChutneyVariablesData.create(mapOf()),
    var serverType: ServerType = ServerType.INTERNAL,
    var serverAddress: String = "",
    var testType: TestType = TestType.SCENARIO_FILE
) {
    fun isExternalServerType(): Boolean {
        return serverType === ServerType.EXTERNAL
    }
}
